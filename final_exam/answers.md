# Final Exam

### Question 1

```bash
    db.messages.find({"headers.From":"andrew.fastow@enron.com", "headers.To":"jeff.skilling@enron.com"}).count()
    > 3
```



### Question 2

```json
    db.messages.aggregate([
        {"$unwind" : "$headers.To"},
        {"$group" : { "_id" : { "_id": "$_id", "from": "$headers.From", "to": "$headers.To" }}},
        {"$group" : { "_id" : { "from" : "$_id.from", "to": "$_id.to" }, "count": {"$sum" :1}}},
        {"$sort" : {"count":-1}},
        {"$limit": 10}
    ], {"allowDiskUse" : true})
```

result :
 
```json 
    { "_id" : { "from" : "susan.mara@enron.com", "to" : "jeff.dasovich@enron.com" }, "count" : 750 }
    { "_id" : { "from" : "soblander@carrfut.com", "to" : "soblander@carrfut.com" }, "count" : 679 }
    { "_id" : { "from" : "susan.mara@enron.com", "to" : "james.steffes@enron.com" }, "count" : 646 }
    { "_id" : { "from" : "susan.mara@enron.com", "to" : "richard.shapiro@enron.com" }, "count" : 616 }
    { "_id" : { "from" : "evelyn.metoyer@enron.com", "to" : "kate.symes@enron.com" }, "count" : 567 }
    { "_id" : { "from" : "susan.mara@enron.com", "to" : "karen.denne@enron.com" }, "count" : 552 }
    { "_id" : { "from" : "susan.mara@enron.com", "to" : "alan.comnes@enron.com" }, "count" : 550 }
    { "_id" : { "from" : "susan.mara@enron.com", "to" : "paul.kaufman@enron.com" }, "count" : 506 }
    { "_id" : { "from" : "susan.mara@enron.com", "to" : "harry.kingerski@enron.com" }, "count" : 489 }
    { "_id" : { "from" : "sgovenar@govadv.com", "to" : "paul.kaufman@enron.com" }, "count" : 488 }
```

### Question 3

```json
    db.messages.update(
        {"headers.Message-ID" : "<8147308.1075851042335.JavaMail.evans@thyme>"}, 
        {"$push": {"headers.To" :  "mrpotatohead@mongodb.com"}}
    )   
```

result: 

```json 
    WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
```

### Question 4

Solution in ```BlogPostDao.java```:
```java 
    public void likePost(final String permalink, final int ordinal) {
        Bson selector = eq("permalink", permalink);
        Bson operator = new Document("$inc",new Document("comments." + ordinal + ".num_likes", 1));
    
        postsCollection.updateOne(selector, operator);
    }
```
 
###  Question 5

[ ] _id_
[x] a_1_b_1
[x] a_1_b_1_c_-1
[x] a_1_c_1
[x] c_1

### Question 6

- ```[ ]``` Add an index on last_name, first_name if one does not already exist.
- ```[x]``` Remove all indexes from the collection, leaving only the index on _id in place
- ```[ ]``` Provide a hint to MongoDB that it should not use an index for the inserts
- ```[x]``` Set w=0, j=0 on writes
- ```[ ]``` Build a replica set and insert data into the secondary nodes to free up the primary nodes.
  
### Question 7 

```java
    package course;

    import com.mongodb.MongoClient;
    import com.mongodb.client.FindIterable;
    import com.mongodb.client.MongoCollection;
    import com.mongodb.client.MongoDatabase;
    import org.bson.Document;

    public class PictureCleanse {

        public static void main(String[] args) {
            MongoClient c = new MongoClient();
            MongoDatabase db = c.getDatabase("test");
            MongoCollection<Document> images = db.getCollection("images");
            final MongoCollection<Document> albums = db.getCollection("albums");

            FindIterable<Document> imageIterator = images.find();

            for(Document image : imageIterator) {
                FindIterable<Document> containingAlbums = albums.find(new Document("images", image.get("_id")));
                if (!containingAlbums.iterator().hasNext()) {
                    System.out.println("deleting image: " + image.get("_id"));
                    images.deleteOne(new Document("_id", image.get("_id")));
                }
            }

        }
    }
```

Also creating an index for ```images``` could speed things up.

After program execution:

```bash  
    > db.images.find({"tags":"sunrises"}).count() 
    44787
```

### Question 8

When running the application, we get duplicate key errors after the first insertion. It seems like only one document was inserted. A quick look at the mongo shell verifies this:

```bash
    > db.animals.find().pretty()
    { "_id" : ObjectId("57829461e9058160d96b204b"), "animal" : "monkey" }
```

### Question 9

Using ```patient_id``` seems reasonable, since this will evenly distribute all records among the shards. Still records of individual patients will be stored on the same shard.

### Question 10

- ```[x]``` The query scanned every document in the collection.
- ```[ ]``` The query returned 120,477 documents.
- ```[x]``` The query used an index to figure out which documents match the find criteria.
- ```[x]``` The query avoided sorting the documents because it was able to use an index's ordering.
