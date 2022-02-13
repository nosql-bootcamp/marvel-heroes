package providers;

import env.MarvelHeroesConfiguration;
import env.RedisConfiguration;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.CompletableFuture;

public class RedisClientProvider implements Provider<RedisClient> {

    private final RedisClient redisClient;

    @Inject
    public RedisClientProvider(MarvelHeroesConfiguration configuration, ApplicationLifecycle lifecycle) {
        RedisConfiguration redisConfiguration = configuration.redisConfiguration;
        RedisURI uri = RedisURI.builder()
                .withHost(redisConfiguration.host)
                .withPort(redisConfiguration.port)
                .withPassword(redisConfiguration.password)
                .build();

        this.redisClient = RedisClient.create(uri);
        lifecycle.addStopHook(() -> {
            this.redisClient.shutdown();
            return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public RedisClient get() {
        return redisClient;
    }
}
