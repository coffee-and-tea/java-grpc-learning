package coffeeAndTea.GRPCJavaLearning.greeting.server;

import coffeeAndTea.GRPCJavaLearning.calculator.AverageRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.AverageResponse;
import coffeeAndTea.GRPCJavaLearning.calculator.CalculatorServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.calculator.MaxRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.MaxResponse;
import coffeeAndTea.GRPCJavaLearning.calculator.PrimeNumberDecompositionRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.PrimeNumberDecompositionResponse;
import coffeeAndTea.GRPCJavaLearning.calculator.SquareRootRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.SquareRootResponse;
import coffeeAndTea.GRPCJavaLearning.calculator.SumRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.SumResponse;
import io.grpc.Status;
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

    @Override
    public StreamObserver<MaxRequest> findMax(StreamObserver<MaxResponse> responseObserver) {
        return new StreamObserver<MaxRequest>() {
            private int currentMax = Integer.MIN_VALUE;

            @Override
            public void onNext(MaxRequest value) {
                if (currentMax < value.getRequest()) {
                    currentMax = value.getRequest();
                }
                responseObserver.onNext(MaxResponse.newBuilder().setResponse(currentMax).build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        int number = request.getRequest();

        if (number >= 0) {
            responseObserver.onNext(
                SquareRootResponse.newBuilder().setSquareRoot(
                        Math.sqrt(number)
                ).build()
            );
        } else {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Not support imaginary number")
                    .augmentDescription("Number received: " + number)
                    .asRuntimeException()
            );
        }
    }
}
