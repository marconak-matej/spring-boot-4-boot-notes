package io.github.mm.soap.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@Configuration
public class DemoWebServiceConfig {

    @Bean("demo-service")
    public DefaultWsdl11Definition demoServiceWsdl11Definition(XsdSchema demoSchema) {
        var wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setSchema(demoSchema);
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setPortTypeName("DemoServicePortType");
        wsdl11Definition.setTargetNamespace("http://github.io/mm/soap/demo");
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema demoSchema() {
        return new SimpleXsdSchema(new ClassPathResource("schema/demo.xsd"));
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        var marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("io.github.mm.soap.gen");
        return marshaller;
    }
}
