# JMS Module - Spring Boot 4.0 JmsClient Demonstration

This module showcases the new **JmsClient** feature introduced in **Spring Framework 7.0**, which provides a fluent and modern API for JMS operations.

## Overview

The `JmsClient` is a new API variant introduced in Spring Framework 7.0, following the design of `JdbcClient` and `RestClient`. It offers a simplified, fluent interface for sending and receiving messages to/from JMS destinations.

## Key Features

- **JmsClient**: New fluent API for JMS operations
- **Embedded Artemis**: Uses embedded Apache ActiveMQ Artemis for simplified development
- **Producer/Consumer Pattern**: Demonstrates message production and consumption
- **REST API Integration**: HTTP endpoint to trigger message sending

## Technology Stack

- **Spring Boot**: 4.0.0-RC1
- **Spring Framework**: 7.x
- **JMS Provider**: Apache ActiveMQ Artemis (embedded)
- **Java**: 21

## Configuration

The module uses embedded Artemis JMS broker configured in `application.properties`:

```properties
spring.artemis.mode=embedded
spring.artemis.embedded.enabled=true
spring.artemis.embedded.persistent=false
```

## Usage

### Starting the Application

```bash
./mvnw spring-boot:run -pl jms
```

### Sending a Message via REST API

```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello from JmsClient!"}'
```

### Expected Output

The application will log:
1. Producer sending the message
2. Consumer receiving and processing the message

### Sending Messages

```
jmsClient.destination("demo-queue").send(message.message());
```

### Consuming Messages

Using traditional `@JmsListener` annotation:

```java
@JmsListener(destination = "demo-queue")
public void receiveMessage(String message) {
    log.info("Received message: {}", message);
}
```

## JmsClient vs JmsTemplate

The `JmsClient` offers several advantages over traditional `JmsTemplate`:

1. **Fluent API**: Chainable method calls for better readability
2. **MessagingException**: Throws `MessagingException` instead of `JmsException` for consistency
3. **Modern Design**: Follows the pattern of other Spring 6.1+ clients (JdbcClient, RestClient)
4. **Simplified Operations**: Common operations are more straightforward

## Advanced JmsClient Features

While this demo shows basic send operations, `JmsClient` supports:

- **Receive with Timeout**: `jmsClient.destination("queue").withReceiveTimeout(1000).receive(String.class)`
- **Time-to-Live**: `jmsClient.destination("queue").withTimeToLive(1000).send(payload)`
- **Custom Headers**: Pass headers map alongside payload
- **Message Objects**: Work with Spring's `Message<?>` abstraction

## Testing

Run tests with:

```bash
./mvnw test -pl jms
```

The test demonstrates:
- Sending messages using JmsClient
- Verifying message consumption
