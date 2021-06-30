package coffeeAndTea.GRPCJavaLearning.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServer {



    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello GRPC");

//        Server server = ServerBuilder
//                .forPort(50051)
//                .addService(new GreetServiceImpl())
//                .addService(new CalculatorServerImpl())
//                .build();

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new GreetServiceImpl())
                .addService(new CalculatorServerImpl())
                .useTransportSecurity(
                        new File("ssl/server.crt"),
                        new File("ssl/server.pem")
                )
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }
}
