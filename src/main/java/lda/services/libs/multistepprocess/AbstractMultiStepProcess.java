package lda.services.libs.multistepprocess;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lda.services.libs.multistepprocess.annotation.Step;
import lda.services.libs.multistepprocess.exception.MultiStepProcessRetryException;
import lda.services.libs.multistepprocess.exception.MultiStepProcessStopException;
import lda.services.libs.multistepprocess.model.MultiStepProcessData;
import lda.services.libs.utils.ClassResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@RequiredArgsConstructor
public abstract class AbstractMultiStepProcess<T> {
    private final Map<Integer, Method> steps = new HashMap<>();
    private final Map<String, AbstractMultiStepProcess<?>> processMap = new HashMap<>();
    @Inject
    private ObjectMapper mapper;
    private int maxProcess;

    @PostConstruct
    public void onInit() {
        final var classAbs = ClassResolver.fromName(this.getClass().getName());
        Arrays.stream(classAbs.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Step.class))
                .forEach(method -> steps.put(method.getAnnotation(Step.class).stepValue(), method));
        maxProcess = steps.keySet().stream()
                .max(Integer::compare)
                .orElse(0);
        log.info("MSP found {} [{}]", steps.size(), classAbs.getName());
    }

    public MultiStepProcessData<? extends T> apply(MultiStepProcessData<?> wrapper) {
        // Get the method corresponding to the specified step number
        final var stepNumber = wrapper.getCurrentStep();
        if (!steps.containsKey(stepNumber)) {
            log.error("total process -> {} :: {}", steps.size(), steps);
            throw new IllegalArgumentException("Step " + stepNumber + " does not exist for this process");
        }
        final var method = steps.get(stepNumber);

        try {
            // Invoke the method using reflection and pass in the input data
            final var type = method.getGenericParameterTypes()[0];
            final var data = mapper.readValue(mapper.writeValueAsString(wrapper.getData()) , Class.forName(type.getTypeName()));
//            final var data = new ObjectMapper().convertValue(wrapper.getData(), Class.forName(type.getTypeName()));

            final var outputData = method.invoke(this, data);

            // Update the process state to reflect that the step has been executed
            return updateState((MultiStepProcessData<T>) wrapper, (T) outputData);

        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | IOException e) {
            if (e.getCause() instanceof final MultiStepProcessRetryException retry) {
                log.debug("process[{}] retry cause[{}] - delayed[{}]", wrapper.getProcess(), retry.getDesc(), retry.getDelay());
                return updateRetry((MultiStepProcessData<T>) wrapper, retry);
            } else if (e.getCause() instanceof final MultiStepProcessStopException stop) {
                log.debug("process[{}] stop cause[{}] - stoped", wrapper.getProcess(), stop.getDesc());
                return stop((MultiStepProcessData<T>) wrapper);
            }
            throw new RuntimeException("Failed to execute step " + stepNumber, e);
        }
    }

    // Helper method to update the process state after executing a step
    private MultiStepProcessData<? extends T> updateState(final MultiStepProcessData<T> wrapper, final T data) {
        final var outWrapper = wrapper.toBuilder()
                .data(data)
                .retry(false)
                .build();

        if (wrapper.isFinish()) {
            return outWrapper;
        }
        if (wrapper.getCurrentStep() == maxProcess) {
            return outWrapper.toBuilder().finish(true).build();
        }

        return outWrapper.toBuilder()
                .currentStep(wrapper.getCurrentStep() + 1)
                .build();
    }

    private MultiStepProcessData<? extends T> updateRetry(
            final MultiStepProcessData<T> wrapper,
            final MultiStepProcessRetryException retryException) {
        return wrapper.toBuilder()
                .data((T) retryException.getData())
                .retry(true)
                .delay(retryException.getDelay())
                .build();
    }

    private MultiStepProcessData<? extends T> stop(final MultiStepProcessData<T> wrapper) {
        return wrapper.toBuilder()
                .finish(true)
                .build();
    }

}
