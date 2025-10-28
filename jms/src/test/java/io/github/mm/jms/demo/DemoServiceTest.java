package io.github.mm.jms.demo;

import static org.mockito.Mockito.*;

import io.github.mm.jms.demo.internal.DemoConsumer;
import io.github.mm.jms.demo.rest.Demo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
@DirtiesContext
class DemoServiceTest {

    @Autowired
    private DemoService service;

    @MockitoSpyBean
    private DemoConsumer consumer;

    @Test
    @DisplayName("Test send message")
    void testSendMessage() {
        var message = "Hello from JmsClient test!";
        var m = new Demo(message);

        service.sendMessage(m);

        verify(consumer, times(1)).receiveMessage(message);
    }
}
