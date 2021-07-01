package coffeeAndTea.GRPCJavaLearning.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        BlogServer server = new BlogServer();
        server.run();
    }

    private void run() throws InterruptedException, IOException {
        Server server = ServerBuilder
                .forPort(50051)
                .addService(new BlogServiceImple())
                .build();

        server.start();
        System.out.println("Blog server started");
        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        () ->
                        {
                            System.out.println("Received Shutdown Request");
                            server.shutdown();
                            System.out.println("Successfully stopped the server");
                        })
        );
        server.awaitTermination();
        System.out.println("Blog server terminated");
    }
}
