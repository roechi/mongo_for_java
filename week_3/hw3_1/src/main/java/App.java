import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

public class App {

    private MongoDatabase db;

    public static void main(String[] args) {
        App app = null;

        if (args.length == 0) {
            app = new App("mongodb://localhost");
        } else {
            app = new App(args[0]);
        }
        app.removeLowestHomeworkForAll();
    }

    public App(String mongoUri) {
        final MongoClient client = new MongoClient(new MongoClientURI(mongoUri));
        this.db = client.getDatabase("school");
    }

    private void removeLowestHomeworkForAll() {
        MongoCollection<Document> students = db.getCollection("students");
        System.out.println(students.count());
        AggregateIterable<Document> aggregate = students.aggregate(
                Arrays.asList(
                        new Document("$unwind", "$scores"),
                        new Document("$match", new Document("scores.type", "homework")),
                        new Document("$group", new Document("_id", "$_id").append("minscore", new Document("$min", "$scores.score")))
                )
        );
        MongoCursor<Document> iterator = aggregate.iterator();
        iterator.forEachRemaining(document -> removeLowestHomework(document, students));
    }

    private void removeLowestHomework(Document document, MongoCollection collection) {
        collection.updateOne(
                new Document("_id", document.get("_id")),
                new Document("$pull",
                        new Document("scores",
                                new Document("score", document.getDouble("minscore"))
                                        .append("type", "homework")))
        );
    }
}