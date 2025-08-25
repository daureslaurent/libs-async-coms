package lda.services.libs.notification.sender;

public interface NotificationSenderPort<T> {
    void sendNotification(final String queue, final String processName, T data);
}
