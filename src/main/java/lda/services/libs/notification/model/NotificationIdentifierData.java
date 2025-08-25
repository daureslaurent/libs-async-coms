package lda.services.libs.notification.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Serdeable
public class NotificationIdentifierData {
    private String id;
    private Boolean finish;
}
