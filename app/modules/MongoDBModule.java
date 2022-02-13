package modules;

import com.google.inject.AbstractModule;
import com.mongodb.reactivestreams.client.MongoDatabase;
import providers.MongoDatabaseProvider;

public class MongoDBModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MongoDatabase.class).toProvider(MongoDatabaseProvider.class);
    }
}
