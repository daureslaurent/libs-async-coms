package lda.services.libs.socket.notification.process;

import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lda.services.libs.socket.notification.model.SocketNotificationData;
import lda.services.libs.socket.notification.register.SocketNotificationRegisterAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class SocketNotificationAliveScheduler {

    @Inject
    private SocketNotificationRegisterAdapter registerAdapter;

    @Scheduled(fixedDelay = "10s")
    public void checkCurrentWebSocket() {
        if (registerAdapter.getNumberWebSocketSession() > 0) {
            log.debug("AliveScheduler - WS: [{}]", registerAdapter.getNumberWebSocketSession());
            registerAdapter.getStreamWebSocketSession()
                    .forEach(webSocketSession -> webSocketSession
                            .sendAsync(SocketNotificationData.builder()
                                    .cmd("PING")
                                    .build()));
        }
    }

}
