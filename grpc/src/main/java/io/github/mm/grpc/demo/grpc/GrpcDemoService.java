package io.github.mm.grpc.demo.grpc;

import io.github.mm.grpc.demo.internal.Demo;
import io.github.mm.grpc.demo.internal.DemoService;
import io.github.mm.grpc.proto.*;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.util.StringUtils;

@GrpcService
public class GrpcDemoService extends DemoServiceGrpc.DemoServiceImplBase {

    private final DemoService service;

    public GrpcDemoService(DemoService service) {
        this.service = service;
    }

    @Override
    public void createDemo(CreateDemoRequest request, StreamObserver<DemoResponse> responseObserver) {
        validateName(request.getName());

        var demo = service.createDemo(request.getName());
        var response =
                DemoResponse.newBuilder().setDemo(DemoMapper.toProto(demo)).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateDemo(UpdateDemoRequest request, StreamObserver<DemoResponse> responseObserver) {
        validateId(request.getId());
        validateName(request.getName());

        var demo = service.updateDemo(request.getId(), request.getName());
        var response =
                DemoResponse.newBuilder().setDemo(DemoMapper.toProto(demo)).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getDemo(GetDemoRequest request, StreamObserver<DemoResponse> responseObserver) {
        validateId(request.getId());

        var demo = service.getDemoById(request.getId());
        var response =
                DemoResponse.newBuilder().setDemo(DemoMapper.toProto(demo)).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listDemos(ListDemosRequest request, StreamObserver<ListDemosResponse> responseObserver) {
        var demos = service.getAllDemos();
        var responseBuilder = ListDemosResponse.newBuilder();

        demos.forEach(demo -> responseBuilder.addDemos(DemoMapper.toProto(demo)));

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteDemo(DeleteDemoRequest request, StreamObserver<DeleteDemoResponse> responseObserver) {
        validateId(request.getId());

        service.deleteDemo(request.getId());
        var response = DeleteDemoResponse.newBuilder().setSuccess(true).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
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

    private static class DemoMapper {
        static io.github.mm.grpc.proto.Demo toProto(Demo domain) {
            return io.github.mm.grpc.proto.Demo.newBuilder()
                    .setId(domain.id())
                    .setName(domain.name())
                    .build();
        }
    }
}
