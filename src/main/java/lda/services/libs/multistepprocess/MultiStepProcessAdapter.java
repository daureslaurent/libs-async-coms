package lda.services.libs.multistepprocess;

import jakarta.inject.Singleton;
import lda.services.libs.multistepprocess.client.MultiStepProcessClient;
import lda.services.libs.multistepprocess.model.MultiStepProcessData;
import lda.services.libs.multistepprocess.port.MultiStepProcessOutput;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Singleton
public class MultiStepProcessAdapter<T> implements MultiStepProcessOutput<T> {
    private final MultiStepProcessClient multiStepProcessClient;

    @Override
    public void runAsyncProcess(T data, String process) {
        MultiStepProcessData<T> wrapper = MultiStepProcessData.<T>builder()
                .data(data)
                .process(process)
                .currentStep(MultiStepProcessConstant.INIT)
                .build();
        multiStepProcessClient.send(wrapper);
    }

    @Override
    public void injectAsyncProcess(T data, String process) {
        MultiStepProcessData<T> wrapper = MultiStepProcessData.<T>builder()
                .data(data)
                .process(process)
                .currentStep(MultiStepProcessConstant.RECV)
                .build();
        multiStepProcessClient.send(wrapper);
    }

}
