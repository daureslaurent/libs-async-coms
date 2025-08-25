package lda.services.libs.notification.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Serdeable
public class NotificationData<T> {
    private String process;
    private boolean retry;
    private Integer delay;
    private T data;
}
