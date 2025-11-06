package io.github.mm.kafka.infrastructure.config;

import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ShareKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultShareConsumerFactory;
import org.springframework.kafka.core.ShareConsumerFactory;
import org.springframework.util.StringUtils;

@Configuration
public class KafkaShareConsumerConfig {

    @Bean
    public ShareConsumerFactory<?, ?> shareConsumerFactory(
            KafkaProperties properties, KafkaConnectionDetails connectionDetails) {
        var props = properties.buildConsumerProperties();
        // auto.offset.reset, isolation.level cannot be set when using a share group.
        props.remove("isolation.level");
        props.remove("auto.offset.reset");

        this.applyKafkaConnectionDetailsForConsumer(props, connectionDetails);
        return new DefaultShareConsumerFactory<>(props);
    }

    @Bean
    public ShareKafkaListenerContainerFactory<?, ?> shareKafkaListenerContainerFactory(
            ShareConsumerFactory<?, ?> shareConsumerFactory) {
        return new ShareKafkaListenerContainerFactory<>(shareConsumerFactory);
    }

    private void applyKafkaConnectionDetailsForConsumer(
            Map<String, Object> properties, KafkaConnectionDetails connectionDetails) {
        var consumer = connectionDetails.getConsumer();
        properties.put("bootstrap.servers", consumer.getBootstrapServers());
        applySecurityProtocol(properties, connectionDetails.getSecurityProtocol());
    }

    static void applySecurityProtocol(Map<String, Object> properties, @Nullable String securityProtocol) {
        if (StringUtils.hasLength(securityProtocol)) {
            properties.put("security.protocol", securityProtocol);
        }
    }
}
