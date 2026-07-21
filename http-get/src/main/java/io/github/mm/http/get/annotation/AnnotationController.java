package io.github.mm.http.get.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/annotation")
@Tag(name = "Annotation-Based", description = "Standard @GetMapping / @RequestMapping controllers")
public class AnnotationController {

    @GetMapping("/hello")
    @Operation(summary = "GET using @GetMapping")
    public String helloGetMapping() {
        return "Hello from @GetMapping";
    }

    @RequestMapping(value = "/hello-classic", method = RequestMethod.GET)
    @Operation(summary = "GET using @RequestMapping(method = GET)")
    public String helloRequestMapping() {
        return "Hello from @RequestMapping(method = GET)";
    }
}
