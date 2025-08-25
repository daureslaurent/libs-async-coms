package lda.services.libs.socket.notification.port;

import io.micronaut.websocket.WebSocketSession;
import lda.services.libs.socket.notification.model.SocketNotificationIdentifier;

import java.util.List;
import java.util.stream.Stream;

public interface SocketNotificationRegisterPort {
    void registerIdentifier(final SocketNotificationIdentifier identifier, final WebSocketSession webSocketSession);
    void removeIdentifier(final SocketNotificationIdentifier identifier);
    boolean isIdentifierRegister(final SocketNotificationIdentifier identifier);
    WebSocketSession getWebSocketSession(final SocketNotificationIdentifier identifier);
    List<WebSocketSession> getListWebSocketSessions(final SocketNotificationIdentifier identifier);
    Stream<WebSocketSession> getStreamWebSocketSession();
    int getNumberWebSocketSession();
}
