package io.github.mm.http.client;

import io.github.mm.http.client.demo.DemoFixture;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.test.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractIntegrationTest {

    @LocalServerPort
    protected int port;

    private final DemoFixture fixture = new DemoFixture();

    protected String baseUrl() {
        return "http://localhost:" + port + "/api/demos";
    }

    protected DemoFixture fixture() {
        return fixture;
    }
}
