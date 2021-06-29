package coffeeAndTea.GRPCJavaLearning.greeting.client;

import coffeeAndTea.GRPCJavaLearning.calculator.AverageRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.AverageResponse;
import coffeeAndTea.GRPCJavaLearning.calculator.CalculatorServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.calculator.MaxRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.MaxResponse;
import coffeeAndTea.GRPCJavaLearning.calculator.PrimeNumberDecompositionRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.SumRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");
        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 50051)
                        .usePlaintext()
                        .build();
        System.out.println("Creating stub");

        serverStreaming(channel);

        CalculatorServiceGrpc.CalculatorServiceStub stub =
                CalculatorServiceGrpc.newStub(channel);

        clientStreaming(stub);

        biDirectionStreaming(stub);
        channel.shutdown();
    }

    private static void biDirectionStreaming(CalculatorServiceGrpc.CalculatorServiceStub stub) {

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<MaxRequest> requestStreamObserver = stub.findMax(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse value) {
                System.out.println("New max found: " + value.getResponse());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server completed");
                latch.countDown();
            }
        });

        for(int i = 0; i < 10000; i++) {
            System.out.println("Sending: " + i);
            requestStreamObserver.onNext(MaxRequest.newBuilder().setRequest(i).build());
        }

        requestStreamObserver.onCompleted();

        try {
            latch.await(30L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void serverStreaming(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub
                calculatorBlockingClient = CalculatorServiceGrpc.newBlockingStub(channel);

//        SumResponse response = calculatorBlockingClient.sum(SumRequest.newBuilder().setFirstNumber(10).setSecondNumber(20).build());

//        System.out.println("10 + 20 = " + response.getSumResult());
        Integer number = 567890301;

        calculatorBlockingClient.primeNumberDecomposition(
                PrimeNumberDecompositionRequest.newBuilder().setNumber(number).build()
        ).forEachRemaining(
                primeNumberDecompositionResponse ->
                        System.out.println(primeNumberDecompositionResponse.getPrimeFactor())
        );
    }

    private static void clientStreaming(CalculatorServiceGrpc.CalculatorServiceStub stub) {
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AverageRequest> requestStreamObserver = stub.average(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                System.out.println("Average: " + value.getResponseNumber());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server completed");

                latch.countDown();
            }
        });

        requestStreamObserver.onNext(AverageRequest.newBuilder().setRequestNumber(10).build());
        requestStreamObserver.onNext(AverageRequest.newBuilder().setRequestNumber(20).build());
        requestStreamObserver.onNext(AverageRequest.newBuilder().setRequestNumber(30).build());
        requestStreamObserver.onNext(AverageRequest.newBuilder().setRequestNumber(40).build());
        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
