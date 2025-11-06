package io.github.mm.kafka.demo.rest;

import io.github.mm.kafka.demo.DemoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demos")
public class DemoApi {

    private final DemoService service;

    public DemoApi(DemoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendMessage(@Valid @RequestBody Demo demo) {
        service.sendMessage(demo.id(), demo.message());
    }
}
