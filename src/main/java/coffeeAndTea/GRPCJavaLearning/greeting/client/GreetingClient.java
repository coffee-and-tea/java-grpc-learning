package coffeeAndTea.GRPCJavaLearning.greeting.client;

import coffeeAndTea.GRPCJavaLearning.dummy.DummyServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.greet.GreetManyTimesRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetResponse;
import coffeeAndTea.GRPCJavaLearning.greet.GreetServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");

        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 50051)
                        .usePlaintext()
                        .build();

        System.out.println("Creating stub");

        // old and dummy
//        DummyServiceGrpc.DummyServiceBlockingStub syncClient =
//                DummyServiceGrpc.newBlockingStub(channel);

//        DummyServiceGrpc.DummyServiceFutureStub asyncClient =
//                DummyServiceGrpc.newFutureStub(channel);

        // created a greet service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

//        // create a greeting request
//        Greeting greeting = Greeting.newBuilder().setFirstName("Wei").setLastName("Xue").build();
//        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();
//
//        // call rpc get response
//        GreetResponse greetResponse = greetClient.greet(greetRequest);
//
//        System.out.println(greetResponse.getResult());

        // server streaming
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest
                .newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Wei").build())
                .build();
        greetClient.greetManyTimes(greetManyTimesRequest)
        .forEachRemaining(
                greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                }
        );

        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
