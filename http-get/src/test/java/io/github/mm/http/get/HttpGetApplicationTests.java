package io.github.mm.http.get;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class HttpGetApplicationTests {

    @Autowired
    private RestTestClient client;

    @Test
    void contextLoads() {}

    @Test
    void annotationGetMapping() {
        var body = client.get()
                .uri("/api/annotation/hello")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class);

        assertThat(body.getResponseBody()).isEqualTo("Hello from @GetMapping");
    }

    @Test
    void annotationRequestMapping() {
        var body = client.get()
                .uri("/api/annotation/hello-classic")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class);
        assertThat(body.getResponseBody()).isEqualTo("Hello from @RequestMapping(method = GET)");
    }

    @Test
    void functionalHello() {
        var body = client.get()
                .uri("/api/functional/hello")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class);
        assertThat(body.getResponseBody()).isEqualTo("Hello from Functional Handler");
    }

    @Test
    void functionalHelloJson() {
        var body = client.get()
                .uri("/api/functional/hello-json")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class);
        assertThat(body.getResponseBody()).contains("Hello from Functional Handler (JSON)");
    }

    @Test
    void rawServlet() {
        var body = client.get()
                .uri("/api/raw-servlet/hello")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class);
        assertThat(body.getResponseBody()).isEqualTo("Hello from Raw Servlet");
    }

    @Test
    void cloudFunction() {
        var body = client.get()
                .uri("/cloud/hello-supplier")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class);
        assertThat(body.getResponseBody()).isEqualTo("Hello from Spring Cloud Function");
    }
}
