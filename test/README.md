# Testing Module - Spring Boot 4.0 & Spring Framework 7 Testing Enhancements

A comprehensive demonstration of **Spring Boot 4.0 and Spring Framework 7's testing improvements**, showcasing modern testing practices with JUnit Jupiter (JUnit 5), RestTestClient with AssertJ, context lifecycle management, bean overriding, and @Nested test support.

## Overview

This module is dedicated to testing enhancements in Spring Boot 4.0 and Spring Framework 7. It provides practical examples of new testing features and migration patterns from JUnit 4 to JUnit Jupiter.

## 🎯 Key Focus Areas

### 1. **JUnit 4 → JUnit 5 Migration** (Spring 7 deprecates JUnit 4)
- Side-by-side comparison of JUnit 4 vs JUnit 5
- Migration guide with before/after examples
- Modern @SpringJUnitConfig annotations
- **Status**: ✅ Phase 1 Complete

### 2. **RestTestClient with AssertJ** (Spring Boot 4.0 RC2)
- Live server testing with @SpringBootTest
- MockMvc-backed testing
- AssertJ integration for fluent assertions


### 3. **Context Pausing & Restarting** (Spring 7)
- Automatic context lifecycle management
- SmartLifecycle#isPauseable() demonstration
- Performance improvements for test suites


### 4. **Enhanced @Nested Test Support** (Spring 7)
- Consistent DI across all nesting levels
- Method parameter injection in lifecycle methods
- Better test organization

### 5. **Prototype Bean Mocking** (Spring 7)
- @MockitoBean now works with prototype beans
- @TestBean vs @MockitoBean comparison
- Non-singleton bean override strategies

### 6. **ApplicationEvents Testing** (Spring Framework 7)
- @RecordApplicationEvents annotation
- Event verification in tests

### 7. **Testcontainers 2.x Integration** (Spring Boot 4.0)
- @ServiceConnection for automatic configuration
- Container lifecycle management

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- Spring Boot 4.0.0-RC2

### Build the Module
```bash
cd test
../mvnw clean install
```

### Run Tests
```bash
# Run all tests
../mvnw test

# Run specific test class
../mvnw test -Dtest=JUnit5ComposedAnnotationsTest

# Run specific test method
../mvnw test -Dtest=JUnit5ComposedAnnotationsTest#shouldCreateProduct
```

### Run the Application
```bash
../mvnw spring-boot:run
```

