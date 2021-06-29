package coffeeAndTea.GRPCJavaLearning.greeting.client;

import coffeeAndTea.GRPCJavaLearning.calculator.CalculatorServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.calculator.SumRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");
        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 50051)
                        .usePlaintext()
                        .build();
        System.out.println("Creating stub");

        CalculatorServiceGrpc.CalculatorServiceBlockingStub
                calculatorBlockingClient = CalculatorServiceGrpc.newBlockingStub(channel);

        SumResponse response = calculatorBlockingClient.sum(SumRequest.newBuilder().setFirstNumber(10).setSecondNumber(20).build());

        System.out.println("10 + 20 = " + response.getSumResult());
        channel.shutdown();
    }
}
