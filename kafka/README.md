# Kafka Module

This module demonstrates **Apache Kafka integration with Spring Boot 4.0**, showcasing modern messaging patterns, Spring Kafka features, **Kafka Share Groups** (new in Spring Boot 4.0), and **Testcontainers** for local development.

## Features

- ✅ **Spring Kafka** integration with Spring Boot 4.0
- ✅ **Kafka Share Groups** - Spring Boot 4.0's new load balancing feature
- ✅ **Producer & Consumer** implementations using `KafkaTemplate` and `@KafkaListener`
- ✅ **JSON serialization/deserialization** for messages
- ✅ **Testcontainers** for local development
- ✅ **Auto-configured Kafka topics** with `TopicBuilder`
- ✅ **REST API** for sending demos
- ✅ **Spring Modulith** package structure with internal/infrastructure separation

## Technology Stack

- **Spring Boot**: 4.0.0-RC1
- **Spring Kafka**: 3.3+ (with Share Group support)
- **Apache Kafka**: 8.1.0+ (via Testcontainers)
- **Testcontainers**: For local Kafka instance
- **Java**: 21

## Architecture

### Package Structure
```
io.github.mm.kafka
├── KafkaApplication.java                    # Main application entry point
├── infrastructure/
│   └── config/
│       └── KafkaShareConsumerConfig.java   # Share Group configuration
└── demo/
    ├── DemoService.java                    # Business logic for messaging
    ├── DemoEvent.java                      # Kafka event record
    ├── rest/
    │   ├── Demo.java                       # REST DTO
    │   └── DemoApi.java                    # REST API endpoints
    └── internal/
        ├── DemoConsumer.java               # Kafka consumer with Share Groups
        └── config/
            ├── DemoKafkaProperties.java    # Configuration properties
            └── KafkaTopicConfig.java       # Topic auto-configuration
```

### Test Structure
```
io.github.mm.kafka
├── TestKafkaApplication.java               # Test application with Testcontainers
└── config/
    └── TestKafkaConfiguration.java         # Testcontainers configuration
```

### Key Components

#### 1. Demo Record (REST DTO)
```java
public record Demo(@NotBlank String id, @NotBlank String message) {}
```

#### 2. DemoEvent Record (Kafka Message)
```java
public record DemoEvent(@NotBlank String id, @NotBlank String message) {}
```

#### 3. Kafka Producer (via KafkaTemplate)
```java
@Service
public class DemoService {
    private final KafkaTemplate<String, DemoEvent> kafkaTemplate;
    
    public void sendMessage(String id, String message) {
        kafkaTemplate.send(properties.topic(), "key-" + id, new DemoEvent(id, message));
    }
}
```

#### 4. Kafka Consumer with Share Groups
```java
@Component
public class DemoConsumer {
    @KafkaListener(
            topics = "${demo.kafka.topic}",
            containerFactory = "shareKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.listener.concurrency}")
    public void listen(ConsumerRecord<String, DemoEvent> record) {
        var demo = record.value();
        log.info("Received event for key {} from partition {}: {}",
                record.key(), record.partition(), demo);
    }
}
```

#### 5. Share Consumer Factory Configuration
```java
@Configuration
public class KafkaShareConsumerConfig {
    @Bean
    public ShareConsumerFactory<?, ?> shareConsumerFactory(
            KafkaProperties properties, KafkaConnectionDetails connectionDetails) {
        Map<String, Object> props = properties.buildConsumerProperties();
        // auto.offset.reset, isolation.level cannot be set when using a share group
        props.remove("isolation.level");
        props.remove("auto.offset.reset");
        return new DefaultShareConsumerFactory<>(props);
    }
    
    @Bean
    public ShareKafkaListenerContainerFactory<?, ?> shareKafkaListenerContainerFactory(
            ShareConsumerFactory<?, ?> shareConsumerFactory) {
        return new ShareKafkaListenerContainerFactory<>(shareConsumerFactory);
    }
}
```

#### 6. Testcontainers Configuration
```java
@TestConfiguration(proxyBeanMethods = false)
public class TestKafkaConfiguration {
    @Bean
    @ServiceConnection
    public ConfluentKafkaContainer kafkaContainer() {
        return new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:8.1.0"))
                .withEnv("KAFKA_GROUP_COORDINATOR_REBALANCE_PROTOCOLS", "classic,consumer,share")
                .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_SHARE_COORDINATOR_ENABLE", "true")
                .withEnv("KAFKA_SHARE_COORDINATOR_STATE_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_SHARE_COORDINATOR_STATE_TOPIC_MIN_ISR", "1")
                .withEnv("KAFKA_GROUP_SHARE_ENABLE", "true");
    }
}
```

## Configuration

### application.yml
```yaml
spring:
  application:
    name: kafka
  kafka:
    listener:
      concurrency: 6
    consumer:
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      group-id: ${spring.application.name}
      client-id: ${spring.application.name}
      isolation-level: read_committed
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    properties:
      spring.json.trusted.packages: io.github.mm.kafka.demo

demo:
  kafka:
    topic: demo-topic
```

## REST API

### Endpoints

#### Send Demo Message
```bash
POST /api/demos
Content-Type: application/json

{
  "id": "demo-123",
  "message": "Hello Kafka!"
}

Response: 202 Accepted
```

The REST API accepts a `Demo` object and publishes a `DemoEvent` to Kafka. The consumer with Share Groups then processes the message.

## Running the Application

### Prerequisites
- **Java 21+**
- **Maven 3.8+**
- **Docker** (for Testcontainers)

### Build
```bash
./mvnw clean install -pl kafka
```

### Run with Testcontainers
The easiest way to run locally is using the test application that automatically starts a Kafka container:

```bash
./mvnw spring-boot:test-run -pl kafka
```

Or run the `TestKafkaApplication` main method from your IDE.

### Run Tests
```bash
./mvnw test -pl kafka
```

All integration tests use Testcontainers to spin up a real Kafka instance automatically.

## Usage Examples

### Using curl

#### Send a demo message
```bash
curl -X POST http://localhost:8080/api/demos \
  -H "Content-Type: application/json" \
  -d '{"id": "demo-1", "message": "Hello from Kafka!"}'
```

The message will be consumed by the `DemoConsumer` using Kafka Share Groups, which provides automatic load balancing across multiple consumer instances.

## Spring Boot 4.0 Features Demonstrated

### 1. **Kafka Share Groups** (New in Spring Boot 4.0)
- **`ShareKafkaListenerContainerFactory`** for share group support
- **`ShareConsumerFactory`** configuration
- Automatic load balancing across consumer instances
- No manual offset management or consumer group coordination needed
- Enable share groups in Kafka with specific environment variables

### 2. **Enhanced Testcontainers Support**
- `@ServiceConnection` annotation for automatic configuration
- Seamless integration with Spring Boot test infrastructure
- Dynamic property source from container
- Support for Kafka Share Groups in test environment

### 3. **Modern Spring Kafka**
- Constructor injection (no field injection)
- Record-based DTOs
- JSON serialization out of the box
- SpEL expressions in `@KafkaListener`

### 4. **Configuration Properties with Records**
```java
@ConfigurationProperties(prefix = "demo.kafka")
public record DemoKafkaProperties(String topic) {}
```

### 5. **Auto-configured Topics**
```java
@Bean
public NewTopic demoTopic() {
    return TopicBuilder.name(properties.topic())
            .partitions(3)
            .replicas(1)
            .build();
}
```

## Testing Strategy

### Local Development with Testcontainers
The module uses `TestKafkaApplication` for local development:
- **Real Kafka instance** via Docker container
- **Automatic cleanup** after stopping
- **Share Groups enabled** in test Kafka configuration
- **Full end-to-end** testing through REST API

### TestKafkaApplication
Run the test application to start the module with a real Kafka container:
```java
@SpringBootApplication
public class TestKafkaApplication {
    public static void main(String[] args) {
        SpringApplication.from(KafkaApplication::main)
                .with(TestKafkaConfiguration.class)
                .run(args);
    }
}
```

This automatically starts a Confluent Kafka container (8.1.0) with Share Groups enabled.

## Key Learnings

1. **Kafka Share Groups**: Spring Boot 4.0 introduces native support for Kafka Share Groups, eliminating the need for manual consumer group coordination
2. **Share Group Configuration**: Must remove `isolation.level` and `auto.offset.reset` properties when using share groups
3. **Testcontainers Integration**: Spring Boot 4.0's `@ServiceConnection` makes it trivial to use Testcontainers
4. **JSON Serialization**: Spring Kafka's JSON support works seamlessly with records
5. **Topic Auto-creation**: Spring Boot automatically creates topics defined as beans
6. **Confluent Kafka**: Using Confluent's Kafka image (8.1.0+) for Share Groups support

## Common Patterns

### Producer Pattern
```java
@Service
public class DemoService {
    public void sendMessage(String id, String message) {
        log.info("Sending message to topic '{}': {} from {}", properties.topic(), id, message);
        kafkaTemplate.send(properties.topic(), "key-" + id, new DemoEvent(id, message));
        log.info("Message sent successfully");
    }
}
```

### Consumer Pattern with Share Groups
```java
@Component
public class DemoConsumer {
    @KafkaListener(
            topics = "${demo.kafka.topic}",
            containerFactory = "shareKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.listener.concurrency}")
    public void listen(ConsumerRecord<String, DemoEvent> record) {
        var demo = record.value();
        log.info("Received event for key {} from partition {}: {}",
                record.key(), record.partition(), demo);
    }
}
```

### Testing Pattern
```java
@SpringBootApplication
public class TestKafkaApplication {
    public static void main(String[] args) {
        SpringApplication.from(KafkaApplication::main)
                .with(TestKafkaConfiguration.class)
                .run(args);
    }
}
```

## Troubleshooting

### Docker Issues
Ensure Docker is running before starting tests or the test application:
```bash
docker ps
```

### Port Conflicts
Testcontainers automatically assigns random ports, so no conflicts should occur.

### Consumer Not Receiving Messages
- Verify the Share Group configuration is correctly set up
- Check that the consumer containerFactory is set to `shareKafkaListenerContainerFactory`
- Ensure JSON deserialization trusted packages are configured: `io.github.mm.kafka.demo`
- Verify Kafka container environment variables for Share Groups are set

## What's Next?

Potential enhancements:
- **Integration tests** with `@SpringBootTest` and Testcontainers
- **Error handling** with Dead Letter Topics (DLT)
- **Batch processing** for high throughput
- **Kafka Streams** for stream processing
- **Schema Registry** integration with Avro
- **Transactional messaging** for exactly-once semantics
- **Observability** with metrics and tracing
- **Multiple consumer groups** comparison (Share Groups vs. traditional)

## References

- [Spring for Apache Kafka Documentation](https://docs.spring.io/spring-kafka/reference/)
- [Spring Boot Kafka Documentation](https://docs.spring.io/spring-boot/reference/messaging/kafka.html)
- [Kafka Share Groups (KIP-932)](https://cwiki.apache.org/confluence/display/KAFKA/KIP-932%3A+Queues+for+Kafka)
- [Testcontainers Kafka Module](https://java.testcontainers.org/modules/kafka/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)

