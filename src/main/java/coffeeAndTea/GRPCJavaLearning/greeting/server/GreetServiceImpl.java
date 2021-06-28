package coffeeAndTea.GRPCJavaLearning.greeting.server;

import coffeeAndTea.GRPCJavaLearning.greet.GreetRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetResponse;
import coffeeAndTea.GRPCJavaLearning.greet.GreetServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.greet.Greeting;
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
}
