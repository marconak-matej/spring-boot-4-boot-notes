package io.github.mm.http.client;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

class LifecycleBean implements InitializingBean, SmartInitializingSingleton, ApplicationRunner, CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(LifecycleBean.class);

    public LifecycleBean() { log.info("03 Constructor"); }

    @PostConstruct
    void postConstruct() { log.info("04 @PostConstruct"); }

    @Override
    public void afterPropertiesSet() { log.info("05 InitializingBean.afterPropertiesSet()"); }

    public void start() { log.info("06 @Bean(initMethod)"); }

    @Override
    public void afterSingletonsInstantiated() { log.info("6.5 SmartInitializingSingleton"); }

    @EventListener(ContextRefreshedEvent.class)
    void onContextRefreshed() { log.info("07 ContextRefreshedEvent"); }

    @EventListener(ApplicationStartedEvent.class)
    void onStarted() { log.info("07b ApplicationStartedEvent"); }

    @Override
    public void run(ApplicationArguments args) { log.info("08 ApplicationRunner"); }

    @Override
    public void run(String... args) { log.info("09 CommandLineRunner"); }

    @EventListener(ApplicationReadyEvent.class)
    void onReady() { log.info("10 ApplicationReadyEvent"); }
}

@SpringBootApplication
public class StartupApplication {
    private static final Logger log = LoggerFactory.getLogger(StartupApplication.class);

    public static void main(String[] args) {
        var app = new SpringApplication(StartupApplication.class);
        app.addInitializers(ctx -> log.info("01 ApplicationContextInitializer"));
        app.run(args);
    }

    @Bean(initMethod = "start")
    LifecycleBean lifecycleBean() {
        log.info("02 @Bean factory method");
        return new LifecycleBean();
    }
}
