package lda.services.libs.multistepprocess.client;

import jakarta.inject.Singleton;
import lda.services.libs.multistepprocess.model.MultiStepProcessData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class MultiStepProcessClientRabbitMQ {
    private final MultiStepProcessClient client;

    public void send(MultiStepProcessData<?> data) {
        try {
            final int delay = Optional.ofNullable(data.getDelay()).orElse(0);
            Thread.sleep(delay);
            client.send(data);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

}
