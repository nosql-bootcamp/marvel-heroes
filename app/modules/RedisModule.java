package modules;

import com.google.inject.AbstractModule;
import io.lettuce.core.RedisClient;
import providers.RedisClientProvider;

public class RedisModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RedisClient.class).toProvider(RedisClientProvider.class);
    }
}
