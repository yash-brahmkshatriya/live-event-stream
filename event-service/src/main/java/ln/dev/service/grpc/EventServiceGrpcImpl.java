package ln.dev.service.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ln.dev.grpc.EventRequest;
import ln.dev.grpc.EventServiceGrpc;
import ln.dev.grpc.HeartbeatResponse;
import ln.dev.pojo.EventPojo;
import ln.dev.protos.common.Common;
import ln.dev.protos.event.Event;
import ln.dev.service.EventService;
import ln.dev.util.EventConvertor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@GrpcService
public class EventServiceGrpcImpl extends EventServiceGrpc.EventServiceImplBase {

    private final EventService eventService;

    private final EventConvertor eventConvertor;

    public EventServiceGrpcImpl(EventService eventService, EventConvertor eventConvertor) {
        this.eventService = eventService;
        this.eventConvertor = eventConvertor;
    }

    @Override
    public void activeEvents(EventRequest request, StreamObserver<Event> responseObserver) {
        eventService.findByFilters(request.getFilters())
                        .stream().map(eventConvertor::convert)
                        .forEach(responseObserver::onNext);

        responseObserver.onCompleted();
    }

    @Override
    public void addEvent(Event request, StreamObserver<Event> responseObserver) {
        try {
            EventPojo eventPojo = eventService.createEvent(
                    eventConvertor.convert(request)
            );
            responseObserver.onNext(eventConvertor.convert(eventPojo));
            responseObserver.onCompleted();
        } catch (ParseException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void heartbeat(Common.Empty request, StreamObserver<HeartbeatResponse> responseObserver) {
        HeartbeatResponse heartbeatResponse = HeartbeatResponse.newBuilder()
                .setMessage("Heartbeat success")
                .setTimestamp(
                        new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date())
                )
                .build();

        responseObserver.onNext(heartbeatResponse);
        responseObserver.onCompleted();
    }
}
