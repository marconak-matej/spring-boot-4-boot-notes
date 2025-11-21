package io.github.mm.grpc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureInProcessTransport;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureInProcessTransport
class GrpcApplicationTests {

    @Test
    void contextLoads() {}
}
