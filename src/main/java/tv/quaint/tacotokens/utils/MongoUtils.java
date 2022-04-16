package tv.quaint.tacotokens.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import tv.quaint.tacotokens.TacoTokens;
import tv.quaint.tacotokens.balance.Balance;
import tv.quaint.tacotokens.config.ConfiguredMongo;

public class MongoUtils {
    public static MongoClient client;
    public static MongoDatabase database;

    public static MongoDatabase loadDatabase() {
        ConfiguredMongo configuredMongo = TacoTokens.CONFIG.configuredMongo;
        MongoClientURI uri = new MongoClientURI(configuredMongo.getParsedUri());
        client = new MongoClient(uri);

        MongoDatabase db = client.getDatabase(configuredMongo.name);
        return db;
    }

    public static MongoCollection<Document> getCollection(MongoDatabase database, String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName); // Gets the collection.
        return collection;
    }

    public static Object getData(String searchKey, String searchValue, String targetData, String collectionname) {
        MongoCollection<Document> collection = MongoUtils.getCollection(database, collectionname);
        Object data = null;
        if(collection.find(Filters.eq(searchKey, searchValue)).first() != null)
            data = collection.find(Filters.eq(searchKey, searchValue)).first().get(targetData);
        return data;
    }

    public static Document getDocWithIdentifier(String identifierKey, Object identifierValue, String collectionName) throws Exception {
        MongoCollection<Document> collection = MongoUtils.getCollection(database, collectionName);

        for (Document document : collection.find()) {
            if (document.get(identifierKey).equals(identifierKey)) return document;
        }

        throw new Exception("Document not found with key '" + identifierKey + "' and value '" + identifierValue + "' in '" + collectionName + "'!");
    }

    public static void updateDocument(Document document, String identifierKey, String collectionName) {
        MongoCollection<Document> collection = MongoUtils.getCollection(database, collectionName);

        for (Document doc : collection.find()) {
            if (document.getString(identifierKey).equals(doc.getString(identifierKey))) {
                collection.updateOne(doc, document);
            }
        }
    }

    public static void saveBalance(Balance balance) {
        MongoCollection<Document> collection = getCollection(database, "balances");

        Document query = new Document();
        Document setData = new Document();
        Document update = new Document();

        query.put(MongoSettingsKey.UUID.string, balance.belongsTo);
        setData.append(MongoSettingsKey.BALANCE.string, balance.balance);
        update.append("$set", setData);

        collection.updateOne(query, update);
    }

    public static void createFirstBalance(Balance balance) {
        MongoCollection<Document> collection = getCollection(database, "balances");

        Document set = new Document();
        set.append(MongoSettingsKey.UUID.string, balance.belongsTo);
        set.append(MongoSettingsKey.BALANCE.string, balance.balance);

        collection.insertOne(set);
    }

    public static double getBalance(String belongsTo, boolean ofPlayer) {
        MongoCollection<Document> collection = getCollection(database, "balances");
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(MongoSettingsKey.UUID.string, belongsTo);
        try {
            if (collection.find(whereQuery).first() == null) {
                createFirstBalance(new Balance(belongsTo, TacoTokens.CONFIG.balanceSettings.startingAmount, ofPlayer));
            }
        } catch (Exception e) {
            e.printStackTrace();
            createFirstBalance(new Balance(belongsTo, TacoTokens.CONFIG.balanceSettings.startingAmount, ofPlayer));
        }
        Document doc = collection.find(whereQuery).first();
        if (doc == null) return 0d;

        return doc.getDouble(MongoSettingsKey.BALANCE.string);
    }
}
