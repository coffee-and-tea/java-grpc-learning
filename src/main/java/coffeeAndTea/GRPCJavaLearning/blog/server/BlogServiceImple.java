package coffeeAndTea.GRPCJavaLearning.blog.server;

import coffeeAndTea.GRPCJavaLearning.blog.Blog;
import coffeeAndTea.GRPCJavaLearning.blog.BlogServiceGrpc;
import coffeeAndTea.GRPCJavaLearning.blog.CreateBlogRequest;
import coffeeAndTea.GRPCJavaLearning.blog.CreateBlogResponse;
import coffeeAndTea.GRPCJavaLearning.blog.ReadBlogRequest;
import coffeeAndTea.GRPCJavaLearning.blog.ReadBlogResponse;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jdk.jshell.Snippet;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImple extends BlogServiceGrpc.BlogServiceImplBase {

    String mangoDBConnectionStr = "mongodb+srv://mangoes:raspyn-8qaMfa-vavjoc@cluster0.3ugvh.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
    MongoClient client = MongoClients.create(mangoDBConnectionStr);
    MongoDatabase database = client.getDatabase("blog");
    MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {

        Blog blog = request.getBlog();

        Document doc = new Document("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        collection.insertOne(doc);

        String id = doc.getObjectId("_id").toString();

        responseObserver.onNext(
                CreateBlogResponse.newBuilder().setBlog(
                        blog.toBuilder().setId(id).build()
                ).build()
        );

        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {

        Document doc = collection.find(eq("_id", new ObjectId(request.getId()))).first();
        if(doc == null){
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Id: " + request.getId()+ " not found")
                    .asRuntimeException()
            );
        } else {
            Blog blog = Blog.newBuilder()
                    .setId(request.getId())
                    .setAuthorId(doc.getString("author_id"))
                    .setTitle(doc.getString("title"))
                    .setContent(doc.getString("content"))
                    .build();

            responseObserver.onNext(ReadBlogResponse.newBuilder()
                    .setBlog(blog).build());

            responseObserver.onCompleted();
        }
    }
}
