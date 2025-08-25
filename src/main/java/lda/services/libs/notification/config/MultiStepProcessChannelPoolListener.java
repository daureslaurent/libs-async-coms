package lda.services.libs.notification.config;

import com.rabbitmq.client.Channel;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.rabbitmq.connect.ChannelInitializer;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.Collections;

@Requires(property = "lda.notification.enabled", value = "true")
@Singleton
public class MultiStepProcessChannelPoolListener extends ChannelInitializer {
    final String queueName;
    public MultiStepProcessChannelPoolListener(@Value("${lda.notification.queue:unamed_notif}") String queueName) {
        this.queueName = queueName;
    }

    @Override
    public void initialize(Channel channel, String name) throws IOException {
        channel.queueDeclare(queueName, false, false, false, Collections.emptyMap());
    }
}