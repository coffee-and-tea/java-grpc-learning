package coffeeAndTea.GRPCJavaLearning.greeting.server;

import coffeeAndTea.GRPCJavaLearning.greet.GreetEveryoneRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetEveryoneResponse;
import coffeeAndTea.GRPCJavaLearning.greet.GreetManyTimesRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetManyTimesResponse;
import coffeeAndTea.GRPCJavaLearning.greet.GreetRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetResponse;
import coffeeAndTea.GRPCJavaLearning.greet.GreetServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.greet.GreetWithDeadlineRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetWithDeadlineResponse;
import coffeeAndTea.GRPCJavaLearning.greet.Greeting;
import coffeeAndTea.GRPCJavaLearning.greet.LongGreetRequest;
import coffeeAndTea.GRPCJavaLearning.greet.LongGreetResponse;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        // extra from request
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        // create response
        String result = "Hello " + firstName;
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        // send response
        responseObserver.onNext(response);

        // complete rpc call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();
        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + ", response number: " + i;
                GreetManyTimesResponse response = GreetManyTimesResponse
                        .newBuilder().setResult(result).build();

                responseObserver.onNext(response);

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {


        StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {

            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                // client sends a message
                result += ". Hello " + value.getGreeting().getFirstName();
            }

            @Override
            public void onError(Throwable t) {
                // client sends an error

            }

            @Override
            public void onCompleted() {
                // client is done
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> requestStreamObserve =
                new StreamObserver<GreetEveryoneRequest>() {
                    @Override
                    public void onNext(GreetEveryoneRequest value) {
                        responseObserver.onNext(
                                GreetEveryoneResponse.newBuilder()
                                        .setResult("Hello " + value.getGreeting().getFirstName())
                                        .build()
                        );
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onCompleted() {
                        responseObserver.onCompleted();
                    }
                };

        return requestStreamObserve;
    }

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {

        Context current = Context.current();

        try {

            for (int i = 0; i < 3; i++) {
                if (!current.isCancelled()) {
                    Thread.sleep(100);
                } else {
                    return;
                }
            }
            responseObserver.onNext(GreetWithDeadlineResponse.newBuilder()
                    .setResponse("Hello " + request.getGreeting().getFirstName()).build());
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
