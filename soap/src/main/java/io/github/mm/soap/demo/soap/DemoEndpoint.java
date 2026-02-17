package io.github.mm.soap.demo.soap;

import io.github.mm.soap.demo.Demo;
import io.github.mm.soap.demo.DemoService;
import io.github.mm.soap.gen.*;
import org.springframework.util.StringUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class DemoEndpoint {

    private static final String NAMESPACE_URI = "http://github.io/mm/soap/demo";
    private final DemoService service;

    public DemoEndpoint(DemoService service) {
        this.service = service;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateDemoRequest")
    @ResponsePayload
    public DemoResponse createDemo(@RequestPayload CreateDemoRequest request) {
        validateName(request.getName());

        var demo = service.createDemo(request.getName());
        var response = new DemoResponse();
        response.setDemo(DemoMapper.toXml(demo));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateDemoRequest")
    @ResponsePayload
    public DemoResponse updateDemo(@RequestPayload UpdateDemoRequest request) {
        validateId(request.getId());
        validateName(request.getName());

        var demo = service.updateDemo(request.getId(), request.getName());
        var response = new DemoResponse();
        response.setDemo(DemoMapper.toXml(demo));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetDemoRequest")
    @ResponsePayload
    public DemoResponse getDemo(@RequestPayload GetDemoRequest request) {
        validateId(request.getId());

        var demo = service.getDemoById(request.getId());
        var response = new DemoResponse();
        response.setDemo(DemoMapper.toXml(demo));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ListDemosRequest")
    @ResponsePayload
    public ListDemosResponse listDemos(@RequestPayload ListDemosRequest request) {
        var demos = service.getAllDemos();
        var response = new ListDemosResponse();

        demos.forEach(demo -> response.getDemo().add(DemoMapper.toXml(demo)));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeleteDemoRequest")
    @ResponsePayload
    public DeleteDemoResponse deleteDemo(@RequestPayload DeleteDemoRequest request) {
        validateId(request.getId());

        service.deleteDemo(request.getId());
        var response = new DeleteDemoResponse();
        response.setSuccess(true);

        return response;
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Name must not exceed 50 characters");
        }
    }

    private void validateId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("ID must not be blank");
        }
    }

    private static final class DemoMapper {

        public static io.github.mm.soap.gen.Demo toXml(Demo domain) {
            io.github.mm.soap.gen.Demo xmlDemo = new io.github.mm.soap.gen.Demo();
            xmlDemo.setId(domain.id());
            xmlDemo.setName(domain.name());
            return xmlDemo;
        }
    }
}
