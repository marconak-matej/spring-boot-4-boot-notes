package io.github.mm.http.client.demo;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demos")
public class DemoApi {

    private final DemoService service;

    public DemoApi(DemoService demoService) {
        this.service = demoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Demo createDemo(@RequestBody Demo demo) {
        return service.createDemo(demo);
    }

    @PutMapping("/{id}")
    public Demo updateDemo(@PathVariable Long id, @RequestBody Demo demo) {
        return service.updateDemo(id, demo);
    }

    @GetMapping
    public List<Demo> getDemos() {
        return service.getAllDemos();
    }

    @GetMapping("/{id}")
    public Demo getDemo(@PathVariable Long id) {
        return service.getDemoById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDemo(@PathVariable Long id) {
        service.deleteDemo(id);
    }
}
