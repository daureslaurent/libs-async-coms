package lda.services.libs.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Requires;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lda.services.libs.notification.AbstractNotification;
import lda.services.libs.notification.annotation.Notification;
import lda.services.libs.notification.model.NotificationData;
import lda.services.libs.utils.ClassResolver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Requires(property = "lda.notification.enabled", value = "true")
//@ConditionalOnProperty(name = "lda.notification.enabled", havingValue = "true")
@Slf4j
@RabbitListener
public class NotificationListener {
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private Set<AbstractNotification<?>> abstractNotifications;
    private final Map<String, AbstractNotification<?>> processMap = new HashMap<>();

    @PostConstruct
    public void init() {
        abstractNotifications.forEach(this::registerProcess);
        log.info("Found {} notification : {}", processMap.size(), processMap.keySet());
    }

    private void registerProcess(AbstractNotification<?> notification) {
        final var classAbs = ClassResolver.fromName(notification.getClass().getName());
        if (classAbs.isAnnotationPresent(Notification.class)) {
            final var processName = classAbs.getAnnotation(Notification.class).name().toUpperCase();
            processMap.put(processName, notification);
        }
    }

    @Queue("${lda.notification.queue:unamed_notif}")
    public void receive(byte[] dataBytes) {
        try {
            onMessage(mapJsonToData(dataBytes));
        } catch (IOException e) {
            log.error("Error listener {lda.steps.queue:unamed_msp} ", e);
            throw new RuntimeException(e);
        }
    }

    private NotificationData<LinkedHashMap<String, String>> mapJsonToData(byte[] bytes) throws IOException {
        final var json = new String(bytes);
        log.debug("decoding: {}", json);
        final var hashMap = objectMapper.readValue(json, LinkedHashMap.class);
        return NotificationData.<LinkedHashMap<String, String>>builder()
                .process((String) hashMap.get("process"))
                .retry((Boolean) hashMap.get("retry"))
                .data((LinkedHashMap<String, String>) hashMap.get("data"))
                .build();
    }


    private void onMessage(NotificationData<?> data) {
        final var processName = data.getProcess().toUpperCase();
        log.debug("> process receive -> name[{}]]", processName);

        if (!processMap.containsKey(processName)) {
            log.debug("process {} not handle", processName);
            throw new RuntimeException("process " + processName + " not handle");
        }
        processMap.get(processName).apply(data);
    }

}