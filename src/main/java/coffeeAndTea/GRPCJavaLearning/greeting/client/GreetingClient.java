package coffeeAndTea.GRPCJavaLearning.greeting.client;

import coffeeAndTea.GRPCJavaLearning.greet.Greet;
import coffeeAndTea.GRPCJavaLearning.greet.GreetEveryoneRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetEveryoneResponse;
import coffeeAndTea.GRPCJavaLearning.greet.GreetManyTimesRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetRequest;
import coffeeAndTea.GRPCJavaLearning.greet.GreetResponse;
import coffeeAndTea.GRPCJavaLearning.greet.GreetServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.greet.Greeting;
import coffeeAndTea.GRPCJavaLearning.greet.LongGreetRequest;
import coffeeAndTea.GRPCJavaLearning.greet.LongGreetResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");

        GreetingClient main = new GreetingClient();
        main.run();

    }

    private void run() {
        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 50051)
                        .usePlaintext()
                        .build();

//        doUnaryCall(channel);
//        doServerStreamingCall(channel);
//        doClientStreamingCall(channel);
        doBiDiStreamingCall(channel);
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub greetingClient =
                GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestStreamObserver
                = greetingClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {

            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Greeting received from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server stream completed");
                latch.countDown();
            }
        });

        Arrays.asList("Wei", "Wei1", "Wei2", "Wei3").forEach(
                name -> {
                    System.out.println("Sending " + name);
                    requestStreamObserver.onNext(
                            GreetEveryoneRequest.newBuilder().setGreeting(
                                    Greeting.newBuilder().setFirstName(name).build()
                            ).build()
                    );
                }
        );


        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub greetClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestStreamObserver =
                greetClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // response from server
                System.out.println("Received response from server\n" + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                // error from server
            }

            @Override
            public void onCompleted() {
                // server sends done
                System.out.println("Server completed");
                latch.countDown();
            }
        });

        requestStreamObserver.onNext(
                LongGreetRequest.newBuilder()
                        .setGreeting(
                                Greeting.newBuilder().setFirstName("Wei").build()
                        ).build()
        );

        requestStreamObserver.onNext(
                LongGreetRequest.newBuilder()
                        .setGreeting(
                                Greeting.newBuilder().setFirstName("Wei1").build()
                        ).build()
        );

        requestStreamObserver.onNext(
                LongGreetRequest.newBuilder()
                        .setGreeting(
                                Greeting.newBuilder().setFirstName("Wei2").build()
                        ).build()
        );

        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void doServerStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
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
    }

    private void doUnaryCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
        // create a greeting request
        Greeting greeting = Greeting.newBuilder().setFirstName("Wei").setLastName("Xue").build();
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

        // call rpc get response
        GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());
    }
}
