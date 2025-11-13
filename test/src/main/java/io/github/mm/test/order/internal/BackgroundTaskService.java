package io.github.mm.test.order.internal;

import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class BackgroundTaskService implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(BackgroundTaskService.class);

    private final AtomicInteger executionCount = new AtomicInteger(0);
    private volatile boolean running = false;

    @Scheduled(fixedDelay = 100)
    public void scheduledTask() {
        if (running) {
            int count = executionCount.incrementAndGet();
            log.debug("Background task executed: {}", count);
        }
    }

    public int getExecutionCount() {
        return executionCount.get();
    }

    @Override
    public void start() {
        log.info("BackgroundTaskService STARTED");
        running = true;
    }

    @Override
    public void stop() {
        log.info("BackgroundTaskService STOPPED");
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isPauseable() {
        return true; // Allow Spring 7 to pause this component
    }
}
