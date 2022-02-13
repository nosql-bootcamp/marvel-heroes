package providers;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.typesafe.config.Config;

import javax.inject.Inject;
import javax.inject.Provider;

public class MongoDatabaseProvider implements Provider<MongoDatabase> {

    private final Config mongoConfig;

    @Inject
    public MongoDatabaseProvider(Config config) {
        this.mongoConfig = config.getConfig("mongodb");
    }

    @Override
    public MongoDatabase get() {
        MongoClient client = MongoClients.create(new ConnectionString(mongoConfig.getString("host")));
        return client.getDatabase(mongoConfig.getString("database"));
    }
}
