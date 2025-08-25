package lda.services.libs.socket.notification.register;

import io.micronaut.websocket.WebSocketSession;
import jakarta.inject.Singleton;
import lda.services.libs.socket.notification.model.SocketNotificationIdentifier;
import lda.services.libs.socket.notification.port.SocketNotificationRegisterPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class SocketNotificationRegisterAdapter implements SocketNotificationRegisterPort {

    private final HashMap<SocketNotificationIdentifier, WebSocketSession> container = new HashMap<>();

    @Override
    public void registerIdentifier(SocketNotificationIdentifier identifier, WebSocketSession webSocketSession) {
//        log.debug("registerIdentifier identifier[{}][{}] Hash[{}]", identifier.getUserId(), identifier.getSessionId(), identifier.hashCode());
        if (container.containsKey(identifier)) {
            container.get(identifier).close();
        }
        container.put(identifier, webSocketSession);
    }

    @Override
    public void removeIdentifier(SocketNotificationIdentifier identifier) {
//        log.debug("removeIdentifier identifier[{}][{}] Hash[{}]", identifier.getUserId(), identifier.getSessionId(), identifier.hashCode());
        container.remove(identifier).close();
    }

    @Override
    public boolean isIdentifierRegister(SocketNotificationIdentifier identifier) {
//        log.debug("isIdentifierRegister identifier[{}][{}] Hash[{}] -> [{}]",
//                identifier.getUserId(), identifier.getSessionId(), identifier.hashCode(), container.containsKey(identifier));

        return container.keySet().stream()
                .map(SocketNotificationIdentifier::getUserId)
                .anyMatch(s -> s.equals(identifier.getUserId()));
    }

    @Override
    public WebSocketSession getWebSocketSession(SocketNotificationIdentifier identifier) {
//        log.debug("getWebSocketSession identifier[{}][{}] Hash[{}]", identifier.getUserId(), identifier.getSessionId(), identifier.hashCode());
        return container.get(identifier);
    }

    @Override
    public List<WebSocketSession> getListWebSocketSessions(SocketNotificationIdentifier identifier) {
        return container.keySet().stream()
                .filter(socketNotificationIdentifier -> socketNotificationIdentifier.getUserId().equals(identifier.getUserId()))
                .map(container::get)
                .toList();
    }

    @Override
    public Stream<WebSocketSession> getStreamWebSocketSession() {
        return container.values().parallelStream();
    }

    @Override
    public int getNumberWebSocketSession() {
        return container.size();
    }
}
