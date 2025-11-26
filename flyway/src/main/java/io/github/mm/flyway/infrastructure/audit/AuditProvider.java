package io.github.mm.flyway.infrastructure.audit;

import org.springframework.stereotype.Component;

@Component
public class AuditProvider {

    public String user() {
        return "user"; // return actual logger user, for now static
    }
}
