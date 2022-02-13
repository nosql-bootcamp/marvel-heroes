package env;

import com.typesafe.config.Config;
import play.Environment;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MarvelHeroesConfiguration {

    public final ElasticConfiguration elasticConfiguration;
    public final RedisConfiguration redisConfiguration;

    @Inject
    public MarvelHeroesConfiguration(Config config, Environment environment) {
        this.elasticConfiguration = new ElasticConfiguration(config.getConfig("elastic"));
        this.redisConfiguration = new RedisConfiguration(config.getConfig("redis"));
    }
}
