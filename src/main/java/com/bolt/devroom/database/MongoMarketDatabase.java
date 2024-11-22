package com.bolt.devroom.database;

import com.bolt.devroom.model.MarketItem;
import com.bolt.devroom.model.MarketTransaction;
import com.bolt.devroom.util.MarketSerializer;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoMarketDatabase implements MarketDatabase {
    private final String connectionString;
    private final String databaseName;


    public MongoMarketDatabase(String connectionString, String databaseName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
    }

    @Override
    public void saveMarketItems(List<MarketItem> items) {
        MongoClient mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("items");

            for (MarketItem item : items) {

                String serializedItem = MarketSerializer.serializeItem(item.item());
                Document query = new Document();
                ObjectId itemObjectId = new ObjectId(item.id());
                query.append("_id", itemObjectId);
                Bson updates = Updates.combine(
                        Updates.set("item", serializedItem),
                        Updates.set("owner", item.owner().toString()),
                        Updates.set("price", item.price())
                );
                UpdateOptions options = new UpdateOptions().upsert(true);

                collection.updateOne(query, updates, options);
            }

            // delete items that are not in the list
            List<ObjectId> itemIds = new ArrayList<>();
            for (MarketItem item : items) {
                itemIds.add(new ObjectId(item.id()));
            }
            Bson deleteQuery = new Document("_id", new Document("$nin", itemIds));
            collection.deleteMany(deleteQuery);

        mongoClient.close();
    }

    @Override
    public List<MarketItem> loadMarketItems() throws IOException, ClassNotFoundException {
        List<MarketItem> items = new ArrayList<>();
        MongoClient mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("items");
            Bson projectionFields = Projections.fields(
                    Projections.include("item", "owner", "price", "_id")
            );

        for (Document document : collection.find().projection(projectionFields)) {
            ItemStack item = MarketSerializer.deserializeItem(document.getString("item"));
            if (item == null) {
                continue;
            }
            MarketItem marketItem = new MarketItem(
                    document.getObjectId("_id").toHexString(),
                    item,
                    UUID.fromString(document.getString("owner")),
                    document.getInteger("price")
            );
            items.add(marketItem);
        }
            mongoClient.close();
        return items;
    }

    @Override
    public List<MarketTransaction> getMarketTransactions(UUID userId) {
        List<MarketTransaction> transactions = new ArrayList<>();
        MongoClient mongoClient = MongoClients.create(connectionString);

            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("transactions");
            Document query = new Document();

            query.append("$or", List.of(
                    new Document("seller", userId.toString()),
                    new Document("buyer", userId.toString())
            ));

            for (Document document : collection.find(query)) {
                MarketTransaction transaction = new MarketTransaction(
                        document.getDouble("cost"),
                        document.getString("seller"),
                        document.getString("buyer"),
                        document.getString("item")
                );
                transactions.add(transaction);
            }

            mongoClient.close();

        return transactions;
    }

    @Override
    public void saveMarketTransaction(MarketTransaction transaction) {
        MongoClient mongoClient = MongoClients.create(connectionString);

            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("transactions");
            Document document = new Document();
            document.append("seller", transaction.seller());
            document.append("buyer", transaction.buyer());
            document.append("cost", transaction.cost());
            document.append("item", transaction.itemName());
            collection.insertOne(document);

        mongoClient.close();
    }
}

