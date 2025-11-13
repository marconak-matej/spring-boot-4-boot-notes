package io.github.mm.test.beanoverride;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.mm.test.order.OrderService;
import io.github.mm.test.order.internal.RequestIdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@DisplayName("Prototype Bean Mocking with @MockitoBean (Spring 7)")
class PrototypeBeanOverrideTest {

    @MockitoBean //  Spring 7: Works with prototype beans!
    RequestIdGenerator generator;

    @Autowired
    OrderService service;

    @Test
    @DisplayName("Should mock prototype bean with @MockitoBean")
    void shouldMockPrototypeBean() {

        // Given - Mock the prototype bean
        given(generator.generate()).willReturn("MOCKED-ID-12345");

        // When - OrderService uses the mocked generator
        var customerId = "CUST-001";
        var order = service.createOrder(customerId);

        // Then - Order should use mocked ID
        assertThat(order.requestId())
                .as("Order should use mocked request ID from prototype bean")
                .isEqualTo("MOCKED-ID-12345");

        assertThat(order.customerId()).isEqualTo(customerId);

        // Verify mock was called
        verify(generator).generate();
    }

    @Test
    @DisplayName("Should stub prototype bean multiple times")
    void shouldStubMultipleTimes() {
        // Given - Stub multiple return values
        given(generator.generate()).willReturn("ID-1", "ID-2", "ID-3");

        // When - Create multiple orders
        var order1 = service.createOrder("CUST-1");
        var order2 = service.createOrder("CUST-2");
        var order3 = service.createOrder("CUST-3");

        // Then - Each order gets different stubbed ID
        assertThat(order1.requestId()).isEqualTo("ID-1");
        assertThat(order2.requestId()).isEqualTo("ID-2");
        assertThat(order3.requestId()).isEqualTo("ID-3");

        verify(generator, times(3)).generate();
    }

    @Test
    @DisplayName("Should verify interactions with prototype bean mock")
    void shouldVerifyInteractions() {
        // Given
        given(generator.generate()).willReturn("VERIFIED-ID");

        // When
        service.createOrder("CUST-001");
        service.createOrder("CUST-002");

        // Then - Verify number of calls
        verify(generator, times(2)).generate();
    }

    @Test
    @DisplayName("Should demonstrate singleton mock replaces prototype bean")
    void shouldReplaceBeanWithSingleton() {
        // Given - Same mock instance is used for all calls
        given(generator.generate()).willReturn("SINGLETON-MOCK-ID");

        // When - Create multiple orders
        var order1 = service.createOrder("CUST-1");
        var order2 = service.createOrder("CUST-2");

        // Then - Both use same mock (prototype replaced with singleton)
        assertThat(order1.requestId()).isEqualTo("SINGLETON-MOCK-ID");
        assertThat(order2.requestId()).isEqualTo("SINGLETON-MOCK-ID");
    }
}
