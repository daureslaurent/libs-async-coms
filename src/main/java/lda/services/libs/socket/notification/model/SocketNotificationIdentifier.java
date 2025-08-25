package lda.services.libs.socket.notification.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@Serdeable
public class SocketNotificationIdentifier {
    private String userId;
    private String sessionId;

    public String toStringData() {
        return userId + ':' + sessionId;
    }

    public static SocketNotificationIdentifier fromSessionData(final String sessionData) {
        final var splited = sessionData.split(":");
        return SocketNotificationIdentifier.builder()
                .userId(splited[0])
                .sessionId(splited[1])
                .build();
    }
}
