package lda.services.libs.notification.metadata;

import io.micronaut.serde.annotation.Serdeable;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Value
@Serdeable
public class NotificationMetadataEntity {
    boolean notification;
    String notificationQueue;
    String notificationProcess;
}
