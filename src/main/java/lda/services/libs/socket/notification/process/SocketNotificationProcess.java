package lda.services.libs.socket.notification.process;

import io.micronaut.core.annotation.ReflectiveAccess;
import jakarta.inject.Inject;
import lda.services.libs.multistepprocess.AbstractMultiStepProcess;
import lda.services.libs.multistepprocess.annotation.MultiStepProcess;
import lda.services.libs.multistepprocess.annotation.Step;
import lda.services.libs.multistepprocess.exception.MultiStepProcessRetryException;
import lda.services.libs.multistepprocess.exception.MultiStepProcessStopException;
import lda.services.libs.socket.notification.model.SocketNotificationData;
import lda.services.libs.socket.notification.model.SocketNotificationIdentifier;
import lda.services.libs.socket.notification.port.SocketNotificationRegisterPort;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@ReflectiveAccess
@MultiStepProcess(name = SocketNotificationProcess.PROCESS_NAME)
public class SocketNotificationProcess extends AbstractMultiStepProcess<SocketNotificationData> {
    public static final String PROCESS_NAME = "SOCKET_NOTIFICATION" ;
    private final UUID currentProcessServerId = UUID.randomUUID();

    @Inject
    private SocketNotificationRegisterPort notificationRegister;

    @Step(stepValue = 0)
    public SocketNotificationData init(SocketNotificationData data) {
//        log.debug("SocketNotificationProcess sessionId[{}]", data.getSessionId());

        //Pseudo Broadcast
        if (data.getProcessed() == null) {
            data.setProcessed(new ArrayList<>());
        }
        if (data.getProcessed().contains(currentProcessServerId)) {
            throw new MultiStepProcessStopException(data, "end of round (replace by exchange/fanout)");
        }

        //Check if target/user is registered on this server
        final var identifier = SocketNotificationIdentifier.fromSessionData(data.getSessionId());
        if (notificationRegister.isIdentifierRegister(identifier)) {
            notificationRegister.getListWebSocketSessions(identifier)
                    .forEach(socketSession -> socketSession.sendSync(data.getData()));
        }

        //Reloop msg for pseudo broadcast
        data.getProcessed().add(currentProcessServerId);
        throw new MultiStepProcessRetryException(data, 0, "searching sessionId to another instance server");
    }

}
