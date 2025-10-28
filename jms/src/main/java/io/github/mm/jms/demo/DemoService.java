package io.github.mm.jms.demo;

import io.github.mm.jms.demo.internal.config.DemoProperties;
import io.github.mm.jms.demo.rest.Demo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsClient;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    private static final Logger log = LoggerFactory.getLogger(DemoService.class);

    private final JmsClient client;
    private final DemoProperties properties;

    public DemoService(JmsClient client, DemoProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    public void sendMessage(Demo message) {
        log.info("Sending message to queue '{}': {}", properties.queue(), message);
        client.destination(properties.queue()).send(message.message());
        log.info("Message sent successfully");
    }
}
