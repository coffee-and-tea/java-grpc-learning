package coffeeAndTea.GRPCJavaLearning.blog.client;

import coffeeAndTea.GRPCJavaLearning.blog.Blog;
import coffeeAndTea.GRPCJavaLearning.blog.BlogServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.blog.CreateBlogRequest;
import coffeeAndTea.GRPCJavaLearning.blog.CreateBlogResponse;
import coffeeAndTea.GRPCJavaLearning.blog.ReadBlogRequest;
import coffeeAndTea.GRPCJavaLearning.blog.ReadBlogResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BlogClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);

//        createBlog(stub);

        readBlog(stub);
    }

    private static void readBlog(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        ReadBlogResponse response = stub.readBlog(ReadBlogRequest.newBuilder()
                .setId("60dd0ddc52fdf745f7b53067").build());

        System.out.println(response.toString());

        try {
            ReadBlogResponse response1 = stub.readBlog(ReadBlogRequest.newBuilder()
                    .setId("60dd0ddc52fdf745f7b53068").build());
        }catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private static void createBlog(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        CreateBlogResponse response = stub.createBlog(
                CreateBlogRequest.newBuilder()
                        .setBlog(
                                Blog.newBuilder().setAuthorId("author2")
                                .setTitle("title1")
                                .setContent("content1")
                        ).build()
        );

        System.out.println("Response: " + response.getBlog().getId());
    }
}
