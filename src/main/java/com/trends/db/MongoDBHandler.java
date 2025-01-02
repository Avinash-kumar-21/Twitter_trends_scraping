package com.trends.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.List;

public class MongoDBHandler {
    private static final String URI = "your mongodb atlas url";

    public static String  saveTrendingTopics(List<String> trends, String ipAddress) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase("trends_db");
            MongoCollection<Document> collection = database.getCollection("trends");

            Document record = new Document("trend1", trends.get(0))
                    .append("trend2", trends.get(1))
                    .append("trend3", trends.get(2))
                    .append("trend4", trends.get(3))
                    .append("trend5", trends.get(4))
                    .append("ipAddress", ipAddress)
                    .append("timestamp", LocalDateTime.now().toString());

            collection.insertOne(record);
            System.out.println("Data saved: " + record.toJson());
            return record.toJson();
        }
    }
}
