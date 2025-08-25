package lda.services.libs.socket.notification.listener;

//import io.micronaut.security.annotation.Secured;
//import io.micronaut.security.rules.SecurityRule;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import lda.services.libs.socket.notification.model.SocketNotificationData;
import lda.services.libs.socket.notification.model.SocketNotificationIdentifier;
import lda.services.libs.socket.notification.port.SocketNotificationRegisterPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
//@Secured(SecurityRule.IS_ANONYMOUS)
@ServerWebSocket("/ws/chat/{sessionData}")
public class SocketNotificationListener {

    private final SocketNotificationRegisterPort socketRegister;

    /**
     * SessionData = "userId:sessionID"
     */

    @OnOpen
    public void onOpen(String sessionData, WebSocketSession session) {
//        log.debug("onOpen [{}][{}]", sessionData, session);
        socketRegister.registerIdentifier(SocketNotificationIdentifier.fromSessionData(sessionData), session);
        session.sendAsync(SocketNotificationData.builder()
                .cmd("CONNECTED")
                .build());
    }

    @OnMessage
    public void onMessage(String sessionData, SocketNotificationData message, WebSocketSession session) {
//        log.debug("onMessage [{}][{}][{}]", sessionData, session, message);

//        if (StringUtils.isNotEmpty(message.getCmd())) {
//            if ("SESSIONS".equals(message.getCmd())) {
//                Mono.from(chatInput.retrieveSessionUser(sessionId, session))
//                        .map(s -> DataChatMessage.builder()
//                                .cmd("SESSIONS")
//                                .sessionId(s)
//                                .build())
//                        .map(session::sendAsync)
//                        .subscribe();
//            }
//            else if ("CREATE_SESSION".equals(message.getCmd())) {
//                Mono.from(chatInput.createChatSession(sessionId))
//                        .map(s -> DataChatMessage.builder()
//                                .cmd("SESSIONS")
//                                .sessionId(s)
//                                .build())
//                        .map(session::sendAsync)
//                        .subscribe();
//            }
//            /*else*/ if ("CHAT_REQ".equals(message.getCmd())) {
//            chatInput.sendChatRequest(message);
//        }
//        else {
//            log.error("No cmd know");
//        }
    }

    @OnClose
    public void onClose(String sessionData, WebSocketSession session) {
//        log.debug("onClose [{}][{}]", sessionData, session);
        socketRegister.removeIdentifier(SocketNotificationIdentifier.fromSessionData(sessionData));
//        chatInput.closeChatWebSocket(sessionData);
        session.close();
    }

}