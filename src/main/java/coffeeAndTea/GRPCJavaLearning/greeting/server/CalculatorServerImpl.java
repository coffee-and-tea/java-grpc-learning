package coffeeAndTea.GRPCJavaLearning.greeting.server;

import coffeeAndTea.GRPCJavaLearning.calculator.AverageRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.AverageResponse;
import coffeeAndTea.GRPCJavaLearning.calculator.CalculatorServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.calculator.PrimeNumberDecompositionRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.PrimeNumberDecompositionResponse;
import coffeeAndTea.GRPCJavaLearning.calculator.SumRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.SumResponse;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {

        SumResponse response = SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber() + request.getSecondNumber())
                .build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        Integer number = request.getNumber();
        Integer divisor = 2;
        while (number > 1) {
            if (number % divisor == 0) {
                number = number / divisor;
                responseObserver.onNext(
                        PrimeNumberDecompositionResponse.newBuilder()
                                .setPrimeFactor(divisor).build()
                );
            } else {
                divisor++;
            }

        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> responseObserver) {

        StreamObserver<AverageRequest> averageRequestStreamObserver =
                new StreamObserver<AverageRequest>() {
                    private int sum = 0;
                    private int count = 0;

                    @Override
                    public void onNext(AverageRequest value) {
                        sum += value.getRequestNumber();
                        count++;
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onCompleted() {
                        responseObserver.onNext(
                                AverageResponse.newBuilder()
                                        .setResponseNumber(sum / count).build());

                        responseObserver.onCompleted();
                    }
                };
        return averageRequestStreamObserver;
    }
}
