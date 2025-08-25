package lda.services.libs.multistepprocess.client;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;
import lda.services.libs.multistepprocess.model.MultiStepProcessData;

@RabbitClient
public interface MultiStepProcessClient {
    @Binding(value = "${lda.steps.queue:unamed_msp}")
    void send(MultiStepProcessData<?> data);
}
