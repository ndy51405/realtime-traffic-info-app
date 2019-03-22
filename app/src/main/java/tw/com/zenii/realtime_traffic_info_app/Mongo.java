package tw.com.zenii.realtime_traffic_info_app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

class Mongo {

    static MongoCollection getCollection(String collection){
        String MONGO_HOST = "mongodb://192.168.1.237:27017";
        String DB = "test";

//        MongoClient mongoClient = MongoClients.create(getSystem().getString(R.string.mongo_host));
        MongoClient mongoClient = MongoClients.create(MONGO_HOST);
//        MongoDatabase mdb = mongoClient.getDatabase(getSystem().getString(R.string.db));
        MongoDatabase mdb = mongoClient.getDatabase(DB);
        return mdb.getCollection(collection);
    }
}
