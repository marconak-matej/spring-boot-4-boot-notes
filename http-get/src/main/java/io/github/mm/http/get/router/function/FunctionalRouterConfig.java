package io.github.mm.http.get.router.function;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class FunctionalRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> functionalRoutes(HelloHandler handler) {
        return route(GET("/api/functional/hello"), handler::hello)
                .andRoute(GET("/api/functional/hello-json"), handler::helloJson);
    }
}
