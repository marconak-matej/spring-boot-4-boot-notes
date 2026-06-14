package io.github.mm.grpc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestGrpcTransport
class GrpcApplicationTests {

    @Test
    void contextLoads() {}
}
