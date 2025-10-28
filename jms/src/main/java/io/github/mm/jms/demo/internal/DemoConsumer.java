package io.github.mm.jms.demo.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class DemoConsumer {

    private static final Logger log = LoggerFactory.getLogger(DemoConsumer.class);

    @JmsListener(destination = "${mm.demo.queue}")
    public void receiveMessage(String message) {
        log.info("Received message from queue: {}", message);
    }
}
