package lda.services.libs.notification.sender;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import lda.services.libs.notification.model.NotificationData;
import lombok.RequiredArgsConstructor;

@Requires(property = "lda.notification.enabled", value = "true")
@RequiredArgsConstructor
@Singleton
public class NotificationSenderAdapter<T> implements NotificationSenderPort<T> {
    private final NotificationSenderClient<T> senderClient;

    @Override
    public void sendNotification(String queue, String processName, T data) {
        senderClient.sendMessage(queue, NotificationData.<T>builder()
                .data(data)
                .process(processName)
                .build());
    }
}
