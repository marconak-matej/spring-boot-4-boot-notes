package io.github.mm.jms.demo.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.mm.jms.demo.DemoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DemoApi.class)
class DemoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DemoService service;

    @Test
    @DisplayName("POST /api/messages with valid message should return 202 ACCEPTED")
    void testSendValidMessage() throws Exception {
        var requestBody = """
                {
                    "message": "Hello JMS!"
                }
                """;

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());

        verify(service, times(1)).sendMessage(any(Demo.class));
    }

    @Test
    @DisplayName("POST /api/messages with blank message should return 400 BAD REQUEST")
    void testSendBlankMessage() throws Exception {
        var requestBody = """
                {
                    "message": ""
                }
                """;

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(service, never()).sendMessage(any());
    }

    @Test
    @DisplayName("POST /api/messages with oversized message should return 400 BAD REQUEST")
    void testSendOversizedMessage() throws Exception {
        var longMessage = "a".repeat(51);
        var requestBody = """
                {
                    "message": "%s"
                }
                """.formatted(longMessage);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(service, never()).sendMessage(any());
    }
}
