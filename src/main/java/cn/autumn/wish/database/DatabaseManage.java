package cn.autumn.wish.database;

import cn.autumn.wish.Wish;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;

import static cn.autumn.wish.config.Configuration.DATABASE;
import static cn.autumn.wish.config.Configuration.SERVER;

/**
 * @author cf
 * Created in 2022/10/27
 */
public class DatabaseManage {

    private static Datastore dispatchDatastore;
    private static Datastore mongoDatastore;

    private static final Class<?>[] mappedClasses = new Class<?>[] {
        DatabaseCounter.class
    };

    public static Datastore getMongoDatastore() {
        return mongoDatastore;
    }

    public static MongoDatabase getMongoDatabase() {
        return getMongoDatastore().getDatabase();
    }

    public static void initialize() {

        MongoClient mongoClient = MongoClients.create(DATABASE.mongo.connectionUri);
        MapperOptions mapperOptions = MapperOptions.builder().storeEmpties(true).storeNulls(false).build();
        mongoDatastore = Morphia.createDatastore(mongoClient, DATABASE.mongo.collection, mapperOptions);
        mongoDatastore.getMapper().map(mappedClasses);

        ensureIndexes();

        if (SERVER.runMode == Wish.RunMode.BACKGROUND) {
            MongoClient dispatchMongoClient = MongoClients.create(DATABASE.server.connectionUri);
            dispatchDatastore = Morphia.createDatastore(dispatchMongoClient, DATABASE.server.collection);

            ensureIndexes();
        }
    }

    /* Current class private method */

    /**
     * Ensure indexes for dispatch server
     */
    public static void ensureIndexes() {
        try {
            mongoDatastore.ensureIndexes();
        } catch (MongoCommandException mce) {
            Wish.getLogger().warn("MongoDB index error: ", mce);
            // Duplicate index
            if (mce.getCode() == 85) {
                MongoIterable<String> collections = mongoDatastore.getDatabase().listCollectionNames();

                for (String name : collections) {
                    mongoDatastore.getDatabase().getCollection(name).dropIndexes();
                }

                // Add back indexes
                mongoDatastore.ensureIndexes();
            }
        }
    }
}
