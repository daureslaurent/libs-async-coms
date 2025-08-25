package lda.services.libs.multistepprocess.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lda.services.libs.multistepprocess.AbstractMultiStepProcess;
import lda.services.libs.multistepprocess.annotation.MultiStepProcess;
import lda.services.libs.multistepprocess.client.MultiStepProcessClientRabbitMQ;
import lda.services.libs.multistepprocess.model.MultiStepProcessData;
import lda.services.libs.utils.ClassResolver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RabbitListener
public class MultiStepProcessListener {
    @Inject
    private MultiStepProcessClientRabbitMQ multiStepProcessClient;
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private Set<AbstractMultiStepProcess<?>> multiStepProcesses;
    private final Map<String, AbstractMultiStepProcess<?>> processMap = new HashMap<>();

    @PostConstruct
    public void init() {
        multiStepProcesses.forEach(this::registerProcess);
        log.info("Found {} process : {}", processMap.size(), processMap.keySet());
    }

    private void registerProcess(AbstractMultiStepProcess<?> abstractMultiStepProcess) {
        final var classAbs = ClassResolver.fromName(abstractMultiStepProcess.getClass().getName());
        if (classAbs.isAnnotationPresent(MultiStepProcess.class)) {
            final var processName = classAbs.getAnnotation(MultiStepProcess.class).name().toUpperCase();
            processMap.put(processName, abstractMultiStepProcess);
        }
    }

//    @Queue("${lda.steps.queue:unamed_msp}")
//    public void receive(MultiStepProcessData<?> data) {
//            onMessage(data);
//    }


    @Queue("${lda.steps.queue:unamed_msp}")
    public void receive(byte[] dataBytes) {
        try {
            onMessage(mapJsonToData(dataBytes));
        } catch (IOException e) {
            log.error("Error listener {lda.steps.queue:unamed_msp} ", e);
            throw new RuntimeException(e);
        }
    }

    private MultiStepProcessData<LinkedHashMap<String, String>> mapJsonToData(byte[] bytes) throws IOException {
        final var json = new String(bytes);
        log.debug("decoding: {}", json);
        final var hashMap = objectMapper.readValue(json, LinkedHashMap.class);
        return MultiStepProcessData.<LinkedHashMap<String, String>>builder()
                .currentStep((Integer) hashMap.get("currentStep"))
                .process((String) hashMap.get("process"))
                .finish((Boolean) hashMap.get("finish"))
                .retry((Boolean) hashMap.get("retry"))
                .data((LinkedHashMap<String, String>) hashMap.get("data"))
                .build();
    }

    private void onMessage(MultiStepProcessData<?> data) {
        final var processName = data.getProcess().toUpperCase();
        log.debug("> process receive -> name[{}] step[{}], raw[{}]", processName, data.getCurrentStep(), data);

        if (!processMap.containsKey(processName)) {
            log.debug("process {} not handle", processName);
            throw new RuntimeException("process " + processName + " not handle");
        }

        final var dataBack =  processMap.get(processName).apply(data);

        if (!dataBack.isFinish()) {
            multiStepProcessClient.send(dataBack);
        }
    }

}
