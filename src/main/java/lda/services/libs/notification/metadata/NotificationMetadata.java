package lda.services.libs.notification.metadata;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Serdeable
public class NotificationMetadata {
    boolean enabled;
    String notificationQueue;
    String notificationProcess;
}
