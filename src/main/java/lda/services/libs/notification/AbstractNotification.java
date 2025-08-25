package lda.services.libs.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lda.services.libs.notification.model.NotificationData;
import lda.services.libs.utils.ClassResolver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

@Slf4j
@Getter
public abstract class AbstractNotification<T> {
    @Inject
    private ObjectMapper mapper;
    private Method notificationMethod;

    @PostConstruct
    public void onInit() throws NoSuchMethodException {
        final var type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.notificationMethod = ClassResolver.fromName(this.getClass().getName())
                .getDeclaredMethod("onNotification", (Class<?>) type);
    }

    public void apply(NotificationData<?> wrapper) {
        try {
            final var type = notificationMethod.getGenericParameterTypes()[0];
            final var data = mapper.readValue(mapper.writeValueAsString(wrapper.getData()) , Class.forName(type.getTypeName()));
            notificationMethod.invoke(this, data);
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | IOException e) {
            throw new RuntimeException("Failed to execute notification", e);
        }

    }
    public abstract void onNotification(T Notification);
}
