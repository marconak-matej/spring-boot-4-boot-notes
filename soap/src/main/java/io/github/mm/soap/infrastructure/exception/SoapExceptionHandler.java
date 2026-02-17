package io.github.mm.soap.infrastructure.exception;

import java.util.Properties;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

@Configuration
public class SoapExceptionHandler extends SoapFaultMappingExceptionResolver {

    public SoapExceptionHandler() {
        var mappings = new Properties();
        // Use string representation of fault codes for Properties
        mappings.setProperty(NotFoundException.class.getName(), "SERVER");
        mappings.setProperty(IllegalArgumentException.class.getName(), "CLIENT");

        var defaultFault = new SoapFaultDefinition();
        defaultFault.setFaultCode(SoapFaultDefinition.SERVER);
        defaultFault.setFaultStringOrReason("An unexpected error occurred");

        setExceptionMappings(mappings);
        setDefaultFault(defaultFault);
        setOrder(Integer.MAX_VALUE - 1);
    }
}
