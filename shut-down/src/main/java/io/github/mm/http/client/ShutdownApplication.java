package io.github.mm.http.client;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

class LifecycleShutdownBean implements DisposableBean, SmartLifecycle, CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(LifecycleShutdownBean.class);
    private volatile boolean running;

    @PreDestroy
    void preDestroy() {
        log.info("04 @PreDestroy");
    }

    @Override
    public void destroy() {
        log.info("05 DisposableBean.destroy()");
    }

    public void cleanup() {
        log.info("06 Custom destroyMethod");
    }

    @EventListener
    void onClose(ContextClosedEvent event) {
        log.info("02 ContextClosedEvent");
    }

    @Override
    public void stop() {
        log.info("03 SmartLifecycle.stop()");
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void start() {
        this.running = true;
    }

    @Override
    public void run(String... args) {
        Runtime.getRuntime()
                .addShutdownHook(new Thread(
                        () -> log.info("01 Custom JVM shutdown hook (order vs. Spring's own hook is NOT guaranteed)"),
                        "custom-shutdown-hook"));
    }
}

@SpringBootApplication
public class ShutdownApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShutdownApplication.class, args);
    }

    @Bean(destroyMethod = "cleanup")
    LifecycleShutdownBean lifecycleShutdownBean() {
        return new LifecycleShutdownBean();
    }
}
