package io.github.mm.test.order.internal;

import java.util.UUID;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class RequestIdGenerator {

    public String generate() {
        return "REQ-" + UUID.randomUUID();
    }
}
