package io.github.mm.http.get.router.function;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class HelloHandler {

    public ServerResponse hello(ServerRequest request) {
        return ServerResponse.ok().body("Hello from Functional Handler");
    }

    public ServerResponse helloJson(ServerRequest request) {
        return ServerResponse.ok().body(new Message("Hello from Functional Handler (JSON)"));
    }

    record Message(String message) {}
}
