package io.github.mm.http.get.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletRegistrationConfig {

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
}
