# Spring Boot SOAP Web Service Demo

This module demonstrates how to build a SOAP web service using Spring Boot 4.0 and Spring Web Services (Spring-WS). It showcases a complete CRUD (Create, Read, Update, Delete) service for managing "Demo" entities using SOAP over HTTP.

## Overview

This project implements a contract-first SOAP service using Spring-WS and demonstrates:

- **Spring Boot SOAP integration** using Spring Web Services
- **Contract-First Development** with WSDL and XML Schema (XSD)
- **JAXB Code Generation** from XSD schemas
- **Endpoint Development** with `@Endpoint` and `@PayloadRoot` annotations
- **Exception Handling** with SOAP faults
- **Comprehensive Testing** with unit and integration tests
- **Spring Framework 7 Best Practices** using Java 21 features (records, sealed classes)

## Tech Stack

- **Spring Boot** 4.0.2
- **Spring Web Services (Spring-WS)** - Latest
- **Spring Framework 7** - Latest
- **Java** 25
- **JAXB** (Jakarta XML Binding) for XML serialization
- **Maven** with `jaxb2-maven-plugin` for code generation
- **Protocol**: SOAP 1.1 over HTTP

## SOAP Service Definition

The `DemoService` provides the following operations:

| Operation | Request | Response | Description |
|-----------|---------|----------|-------------|
| `CreateDemo` | `CreateDemoRequest` | `DemoResponse` | Creates a new demo and returns it with a generated UUID |
| `UpdateDemo` | `UpdateDemoRequest` | `DemoResponse` | Updates an existing demo by ID |
| `GetDemo` | `GetDemoRequest` | `DemoResponse` | Retrieves a demo by its unique ID |
| `ListDemos` | `ListDemosRequest` | `ListDemosResponse` | Lists all demos in the system |
| `DeleteDemo` | `DeleteDemoRequest` | `DeleteDemoResponse` | Deletes a demo by ID |

### Request/Response Types

#### CreateDemoRequest
```xml
<CreateDemoRequest>
    <name>string (required, max 50 chars)</name>
</CreateDemoRequest>
```

#### UpdateDemoRequest
```xml
<UpdateDemoRequest>
    <id>string (required)</id>
    <name>string (required, max 50 chars)</name>
</UpdateDemoRequest>
```

#### GetDemoRequest
```xml
<GetDemoRequest>
    <id>string (required)</id>
</GetDemoRequest>
```

#### ListDemosRequest
```xml
<ListDemosRequest>
    <!-- Empty, no parameters -->
</ListDemosRequest>
```

#### DeleteDemoRequest
```xml
<DeleteDemoRequest>
    <id>string (required)</id>
</DeleteDemoRequest>
```

#### Demo (Response Entity)
```xml
<Demo>
    <id>string</id>
    <name>string</name>
</Demo>
```

## Architecture

### Component Description

#### SoapApplication.java
Main Spring Boot application class with `@SpringBootApplication` annotation. Starts the embedded Tomcat server on port 8088.

#### Demo.java
Java `record` representing an immutable Demo entity with `id` and `name` fields. Uses Java 21 records for modern, concise domain modeling.

#### DemoService.java
Business logic layer providing CRUD operations:
- `createDemo(String name): Demo` - Creates new demo with UUID
- `updateDemo(String id, String name): Demo` - Updates existing demo
- `getDemoById(String id): Demo` - Retrieves demo by ID
- `getAllDemos(): List<Demo>` - Returns all demos
- `deleteDemo(String id): void` - Deletes demo by ID

Includes validation for name constraints (not blank, max 50 chars).

#### SoapDemoEndpoint.java
Spring-WS `@Endpoint` handler for SOAP requests:
- Annotated with `@PayloadRoot` to route SOAP operations
- Uses `@ResponsePayload` to mark response methods
- Delegates to DemoService for business logic
- Performs input validation
- Similar to a Spring MVC controller but for SOAP

#### SoapDemoMapper.java
Bidirectional converter between:
- JAXB-generated XML classes (from XSD) ↔ Domain models (records)
- Provides `toXml(Demo)` methods
- Encapsulates XML serialization/deserialization

#### WebServiceConfig.java and DemoWebServiceConfig.java
Spring configuration for Spring-WS:
- `@Configuration` and `@EnableWs` for Spring-WS support
- `ServletRegistrationBean<MessageDispatcher>` - Registers SOAP servlet at `/ws/*`
- `DefaultWsdl11Definition` - Auto-generates WSDL from XSD
- `XsdSchema` - Loads XML Schema from classpath

#### SoapExceptionHandler.java
Global exception handler using `SoapFaultMappingExceptionResolver`:
- Maps exceptions to SOAP faults
- `NotFoundException` → SOAP Server fault
- `IllegalArgumentException` → SOAP Client fault
- Handles validation errors gracefully

#### demoService.wsdl
WSDL (Web Services Description Language) document:
- Defines service interface and operations
- References XSD schema for type definitions
- SOAP/HTTP binding for transport
- Service endpoint at `http://localhost:8088/ws/demo-service`

#### demo.xsd
XML Schema Definition:
- Defines request/response message structures
- Complex types: `CreateDemoRequest`, `UpdateDemoRequest`, `GetDemoRequest`, etc.
- Simple types for validation constraints
- JAXB code generator creates Java classes from this schema

## Getting Started

### Prerequisites

- Java 25 or later
- Maven 3.9+
- Spring Boot 4.0+

### Building the Project

```bash
# Navigate to the soap module
cd soap

# Build and generate JAXB classes from XSD
./mvnw clean package

# The jaxb2-maven-plugin will generate Java classes from demo.xsd
# Generated sources will be in: target/generated-sources/jaxb/
```

### Running the Service

```bash
# Option 1: Run from project root
./mvnw spring-boot:run -pl soap

# Option 2: Run from soap module
cd soap
./mvnw spring-boot:run
```

The SOAP service will start on **port 8088** with the WSDL available at:
```
http://localhost:8088/ws/demo-service.wsdl
```

## Testing

### Running Unit Tests

```bash
# Run all unit tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=DemoServiceTest
```

### Running Integration Tests

```bash
# Run integration tests
./mvnw test -Dtest=SoapDemoEndpointIntegrationTest
```

### Manual Testing with curl

#### Get WSDL

```bash
curl -s http://localhost:8088/ws/demo.wsdl
```

#### Create Demo

```bash
curl -X POST \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://github.io/mm/soap/demo">
  <soap:Body>
    <tns:CreateDemoRequest>
      <tns:name>My First SOAP Demo</tns:name>
    </tns:CreateDemoRequest>
  </soap:Body>
</soap:Envelope>' \
  http://localhost:8088/ws/demo-service

# Response example:
# <soap:Envelope>
#   <soap:Body>
#     <ns2:DemoResponse xmlns:ns2="http://github.io/mm/soap/demo">
#       <demo>
#         <id>550e8400-e29b-41d4-a716-446655440000</id>
#         <name>My First SOAP Demo</name>
#       </demo>
#     </ns2:DemoResponse>
#   </soap:Body>
# </soap:Envelope>
```

#### Get Demo

```bash
curl -X POST \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://github.io/mm/soap/demo">
  <soap:Body>
    <tns:GetDemoRequest>
      <tns:id>550e8400-e29b-41d4-a716-446655440000</tns:id>
    </tns:GetDemoRequest>
  </soap:Body>
</soap:Envelope>' \
  http://localhost:8088/ws/demoService
```

#### List All Demos

```bash
curl -X POST \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://github.io/mm/soap/demo">
  <soap:Body>
    <tns:ListDemosRequest/>
  </soap:Body>
</soap:Envelope>' \
  http://localhost:8088/ws/demoService
```

#### Update Demo

```bash
curl -X POST \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://github.io/mm/soap/demo">
  <soap:Body>
    <tns:UpdateDemoRequest>
      <tns:id>550e8400-e29b-41d4-a716-446655440000</tns:id>
      <tns:name>Updated Demo Name</tns:name>
    </tns:UpdateDemoRequest>
  </soap:Body>
</soap:Envelope>' \
  http://localhost:8088/ws/demoService
```

#### Delete Demo

```bash
curl -X POST \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://github.io/mm/soap/demo">
  <soap:Body>
    <tns:DeleteDemoRequest>
      <tns:id>550e8400-e29b-41d4-a716-446655440000</tns:id>
    </tns:DeleteDemoRequest>
  </soap:Body>
</soap:Envelope>' \
  http://localhost:8088/ws/demoService
```

## Code Generation

The project uses **JAXB code generation** from XSD schemas:

```bash
# Manually regenerate JAXB classes
./mvnw jaxb2:xjc
```

Generated classes are placed in `target/generated-sources/jaxb/` and automatically included in the compilation classpath.

### JAXB Maven Plugin Configuration

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxb2-maven-plugin</artifactId>
    <version>4.1</version>
    <configuration>
        <sources>
            src/main/resources/schema
        </sources>
        <outputDirectory>target/generated-sources/jaxb</outputDirectory>
        <packageName>io.github.mm.soap.gen</packageName>
    </configuration>
</plugin>
```

## Extending the Service

### Adding a New Operation

1. **Add XSD Element** in `src/main/resources/schema/demo.xsd`:
   ```xml
   <xs:element name="SearchDemosRequest">
       <xs:complexType>
           <xs:sequence>
               <xs:element name="query" type="xs:string"/>
           </xs:sequence>
       </xs:complexType>
   </xs:element>
   ```

2. **Regenerate JAXB Classes**:
   ```bash
   ./mvnw jaxb2:xjc
   ```

3. **Add Method to DemoService**:
   ```java
   public List<Demo> searchDemos(String query) {
       // Implementation
   }
   ```

4. **Add Handler to SoapDemoEndpoint**:
   ```java
   @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SearchDemosRequest")
   @ResponsePayload
   public SearchDemosResponse searchDemos(@RequestPayload SearchDemosRequest request) {
       // Implementation
   }
   ```

5. **Update WSDL** - Will be auto-generated if using DefaultWsdl11Definition

### Database Integration

Replace the `ConcurrentHashMap` in `DemoService` with a Spring Data JPA repository:

```java
@Service
public class DemoService {
    @Autowired
    private DemoRepository repository;
    
    public Demo createDemo(String name) {
        var demo = new Demo(null, name);
        return repository.save(demo);
    }
    // ... rest of implementation
}
```

## Dependencies

See `pom.xml` for complete dependency list:

- `spring-boot-starter-web-services` - Spring-WS framework
- `jakarta.xml.bind-api` - JAXB API (Jakarta namespace)
- `jaxb-runtime` - JAXB runtime implementation
- `jakarta.xml.ws-api` - WS-Addressing support
- `jakarta.activation-api` - SOAP attachments support
- `spring-boot-starter-test` - Testing framework
- `wsdl4j`
