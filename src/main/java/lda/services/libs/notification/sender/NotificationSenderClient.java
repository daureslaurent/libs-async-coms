package lda.services.libs.notification.sender;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;
import lda.services.libs.notification.model.NotificationData;

@RabbitClient
public interface NotificationSenderClient<T> {
    void sendMessage(@Binding String queueName, NotificationData<T> notification);
}

