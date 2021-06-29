package coffeeAndTea.GRPCJavaLearning.greeting.server;

import coffeeAndTea.GRPCJavaLearning.calculator.CalculatorServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.calculator.SumRequest;
import coffeeAndTea.GRPCJavaLearning.calculator.SumResponse;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {

        SumResponse response = SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber()+ request.getSecondNumber())
                .build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }
}
