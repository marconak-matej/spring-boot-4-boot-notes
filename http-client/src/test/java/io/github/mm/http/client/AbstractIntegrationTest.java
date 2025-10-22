package io.github.mm.http.client;

import io.github.mm.http.client.demo.DemoFixture;
import io.github.mm.http.client.demo.internal.DemoService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.test.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    private DemoService demoService;

    private final DemoFixture fixture = new DemoFixture();

    protected String baseUrl() {
        return "http://localhost:" + port + "/api/demos";
    }

    protected DemoFixture fixture() {
        return fixture;
    }

    @AfterEach
    void cleanUp() {
        fixture.getCreatedDemoIds().forEach(id -> {
            try {
                demoService.deleteDemo(id);
            } catch (Exception e) {
                // Ignore if demo was already deleted in the test
            }
        });
        fixture.clearTrackedIds();
    }
}
