# HTTP GET Module

The http-get module demonstrates **4 different ways to handle HTTP GET requests** in Spring Boot 4.1 — from the standard annotation-driven controllers to programmatic functional endpoints, raw servlet registration, and Spring Cloud Function.

## Features

- ✅ **Annotation-Based Controller** -- `@RestController` with `@GetMapping` / `@RequestMapping`
- ✅ **Functional Endpoints** -- `RouterFunction<ServerResponse>` beans with a dedicated `Handler` class (servlet-stack, no WebFlux needed)
- ✅ **Raw Servlet Registration** -- `ServletRegistrationBean<HttpServlet>` bypasses Spring MVC entirely
- ✅ **Spring Cloud Function** -- `Supplier<String>` bean auto-exposed as a GET endpoint by `spring-cloud-function-web`

## Technology Stack

- **Spring Boot 4.1.0** - Application framework
- **Java 25** - Runtime
- **Spring WebMVC** - Web support

## Running the Application

```bash
./mvnw spring-boot:run -pl http-get
```

The application runs on port **8085**.

## Endpoints Overview

| Approach                       | URL                                 | Response                                             |
|--------------------------------|-------------------------------------|------------------------------------------------------|
| Annotation (`@GetMapping`)     | `GET /api/annotation/hello`         | `Hello from @GetMapping`                             |
| Annotation (`@RequestMapping`) | `GET /api/annotation/hello-classic` | `Hello from @RequestMapping(method = GET)`           |
| Functional (plain text)        | `GET /api/functional/hello`         | `Hello from Functional Handler`                      |
| Functional (JSON)              | `GET /api/functional/hello-json`    | `{"message":"Hello from Functional Handler (JSON)"}` |
| Raw Servlet                    | `GET /api/raw-servlet/hello`        | `Hello from Raw Servlet`                             |
| Spring Cloud Function          | `GET /cloud/hello-supplier`          | `Hello from Spring Cloud Function`                   |

### 1. Annotation-Based Controllers (Standard)

`@RestController` + `@GetMapping` or `@RequestMapping(method = RequestMethod.GET)` — the classic Spring MVC approach.

```java
@RestController
@RequestMapping("/api/annotation")
public class AnnotationController {

    @GetMapping("/hello")
    public String helloGetMapping() {
        return "Hello from @GetMapping";
    }

    @RequestMapping(value = "/hello-classic", method = RequestMethod.GET)
    public String helloRequestMapping() {
        return "Hello from @RequestMapping(method = GET)";
    }
}
```

```bash
curl http://localhost:8085/api/annotation/hello
curl http://localhost:8085/api/annotation/hello-classic
```

### 2. Functional Endpoints (Programmatic)

`RouterFunction<ServerResponse>` beans with a dedicated `Handler` class — no annotations, pure Java config using the servlet-stack functional API (`org.springframework.web.servlet.function.*`).

```java
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

@Configuration
public class FunctionalRouterConfig {
    @Bean
    public RouterFunction<ServerResponse> functionalRoutes(HelloHandler handler) {
        return route(GET("/api/functional/hello"), handler::hello)
                .andRoute(GET("/api/functional/hello-json"), handler::helloJson);
    }
}
```

```bash
curl http://localhost:8085/api/functional/hello
curl http://localhost:8085/api/functional/hello-json
```

### 3. Raw Servlet Registration (Old-School)

Bypasses Spring MVC entirely by registering a plain `HttpServlet` via `ServletRegistrationBean`.

```java
@Bean
public ServletRegistrationBean<HttpServlet> rawServletBean() {
    HttpServlet servlet = new HttpServlet() {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("text/plain");
            resp.getWriter().write("Hello from Raw Servlet");
        }
    };
    return new ServletRegistrationBean<>(servlet, "/api/raw-servlet/hello");
}
```

```bash
curl http://localhost:8085/api/raw-servlet/hello
```

### 4. Spring Cloud Function

A `Supplier<String>` bean auto-exposed as a GET endpoint by `spring-cloud-function-web`. The bean name becomes the path, and the base path is configured via `spring.cloud.function.web.path`.

```java
@Bean(name = "hello-supplier")
public Supplier<String> helloSupplier() {
    return () -> "Hello from Spring Cloud Function";
}
```

```bash
curl http://localhost:8085/cloud/hello-supplier
```

## Configuration

```yaml
spring:
  cloud:
    function:
      web:
        path: /cloud       # base path for SCF endpoints

server:
  port: 8085
```
