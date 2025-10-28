package io.github.mm.jms.demo.rest;

import io.github.mm.jms.demo.DemoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class DemoApi {

    private final DemoService service;

    public DemoApi(DemoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendMessage(@RequestBody @Valid Demo message) {
        service.sendMessage(message);
    }
}
