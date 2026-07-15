package io.github.mm.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@SpringBootApplication
// 0. @Import — pull in beans from other sources (see PART 2b below).
@Import({
    BeansApplication.ImportedConfig.class, // a) import a @Configuration class
    BeansApplication.MyImportSelector.class, // b) import via ImportSelector
    BeansApplication.MyRegistrar.class // c) import via ImportBeanDefinitionRegistrar
})
public class BeansApplication {

    private static final Logger log = LoggerFactory.getLogger(BeansApplication.class);

    // Shared domain types
    static class MyService {
        private final String label;

        MyService(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    interface PaymentGateway {

        String charge();
    }

    static class StripeGateway implements PaymentGateway {

        public String charge() {
            return "charged via Stripe";
        }
    }

    static class PaypalGateway implements PaymentGateway {

        public String charge() {
            return "charged via PayPal";
        }
    }

    // PART 1 — ANNOTATION-BASED (discovered by component scan)

    // 1. @Component
    @Component
    static class NotificationComponent {

        public String ping() {
            return "component alive";
        }
    }

    // 2. @Service
    @Service
    static class GreetingService {

        public String greet(String name) {
            return "Hello, " + name + "!";
        }
    }

    // 3. @Repository
    @Repository
    static class UserRepository {

        public String findById(long id) {
            return "user-" + id;
        }
    }

    // 4. @Component + @Scope (prototype)
    @Component
    @Scope("prototype")
    static class PrototypeBean {}

    // PART 2 — @Configuration + @Bean variants
    @Configuration(proxyBeanMethods = false)
    static class AppConfig {

        // 5. Basic @Bean
        @Bean
        MyService beanFromConfig() {
            return new MyService("registered-via-@Bean");
        }

        // 6. @Primary — preferred candidate
        @Bean
        @Primary
        PaymentGateway stripeGateway() {
            return new StripeGateway();
        }

        // second candidate of same type
        @Bean
        PaymentGateway paypalGateway() {
            return new PaypalGateway();
        }

        // 7. @Bean with dependency selected via @Qualifier
        @Bean
        MyService orderServiceLabel(@Qualifier("paypalGateway") PaymentGateway gateway) {
            return new MyService("uses -> " + gateway.charge());
        }

        // 8. @ConditionalOnProperty — only if features.audit.enabled=true
        @Bean
        @ConditionalOnProperty(name = "features.audit.enabled", havingValue = "true")
        MyService auditBean() {
            return new MyService("registered-conditionally");
        }

        // 9. @Lazy — created on first use
        @Bean
        @Lazy
        MyService lazyBean() {
            return new MyService("registered-lazily");
        }

        // 10. FactoryBean via @Bean
        @Bean
        MyServiceFactoryBean factoryBeanExample() {
            return new MyServiceFactoryBean();
        }

        // 11. BeanDefinitionRegistryPostProcessor as a static @Bean
        //     -> registers a bean DEFINITION during startup.
        @Bean
        static BeanDefinitionRegistryPostProcessor registrar() {
            return new BeanDefinitionRegistryPostProcessor() {
                @Override
                public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
                    var def = BeanDefinitionBuilder.genericBeanDefinition(MyService.class)
                            .addConstructorArgValue("registered-via-RegistryPostProcessor")
                            .getBeanDefinition();
                    registry.registerBeanDefinition("beanFromRegistryPostProcessor", def);
                }

                @Override
                public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) {
                    /* no-op */
                }
            };
        }
    }

    // 10 (cont). FactoryBean<T> — manufactures the actual bean
    static class MyServiceFactoryBean implements FactoryBean<MyService> {

        @Override
        public MyService getObject() {
            return new MyService("registered-via-FactoryBean");
        }

        @Override
        public Class<?> getObjectType() {
            return MyService.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    // PART 2b — @Import targets

    // a) A plain @Configuration class imported via @Import.
    //    Its @Bean methods are registered just like AppConfig's.
    @Configuration
    static class ImportedConfig {

        @Bean
        MyService beanFromImportedConfig() {
            return new MyService("registered-via-@Import(@Configuration)");
        }
    }

    // b) ImportSelector — returns the fully-qualified names of
    //    @Configuration classes to import (decided programmatically).
    static class MyImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            // You could branch on metadata/env here to pick different configs.
            return new String[] {SelectedConfig.class.getName()};
        }
    }

    // Config class chosen by the ImportSelector above.
    @Configuration
    static class SelectedConfig {

        @Bean
        MyService beanFromImportSelector() {
            return new MyService("registered-via-@Import(ImportSelector)");
        }
    }

    // c) ImportBeanDefinitionRegistrar — register bean definitions
    //    directly, driven by the importing class's annotation metadata.
    static class MyRegistrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(
                AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            var def = BeanDefinitionBuilder.genericBeanDefinition(MyService.class)
                    .addConstructorArgValue("registered-via-@Import(ImportBeanDefinitionRegistrar)")
                    .getBeanDefinition();
            registry.registerBeanDefinition("beanFromImportRegistrar", def);
        }
    }

    // PART 3 — MANUAL / PROGRAMMATIC using the Boot context
    public static void main(String[] args) {

        var app = new SpringApplication(BeansApplication.class);

        // 12. Register DURING startup via an ApplicationContextInitializer
        app.addInitializers(context -> {
            if (context instanceof GenericApplicationContext gac) {
                gac.registerBean(
                        "serviceFromInitializer",
                        MyService.class,
                        () -> new MyService("registered-during-startup (initializer)"));
            }
        });

        // run(...) returns the live context we reuse below.
        var ctx = app.run(args);

        // 13. Register AFTER startup via registerBean(...) + Supplier
        if (ctx instanceof GenericApplicationContext gac) {
            gac.registerBean(
                    "serviceFromSupplier",
                    MyService.class,
                    () -> new MyService("registered-after-startup (registerBean + supplier)"));

            // 14. Register AFTER startup via BeanDefinitionBuilder + registry
            var def = BeanDefinitionBuilder.genericBeanDefinition(MyService.class)
                    .addConstructorArgValue("registered-after-startup (BeanDefinition)")
                    .getBeanDefinition();
            gac.registerBeanDefinition(
                    "serviceFromBeanDefinition", def); // GAC already implements BeanDefinitionRegistry
        }

        // 15. Register an ALREADY-BUILT singleton (no autowiring/init callbacks)
        var prebuilt = new MyService("registered-after-startup (registerSingleton)");
        ctx.getBeanFactory().registerSingleton("serviceFromSingleton", prebuilt);

        // ---- Pull everything from the SAME context ----
        log.info("---- All MyService beans ----");
        ctx.getBeansOfType(MyService.class).forEach((name, bean) -> log.info("{} -> {}", name, bean.label()));

        log.info("");
        log.info("---- Annotation-scanned beans ----");
        log.info("{}", ctx.getBean(GreetingService.class).greet("Spring"));
        log.info("{}", ctx.getBean(NotificationComponent.class).ping());
        log.info("{}", ctx.getBean(UserRepository.class).findById(1));
        log.info(
                "primary PaymentGateway -> {}",
                ctx.getBean(PaymentGateway.class).charge());

        ctx.close();
    }
}
