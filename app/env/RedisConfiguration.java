package env;

import com.typesafe.config.Config;

public class RedisConfiguration {

    public final String host;
    public final int port;
    public final String password;

    public RedisConfiguration(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public RedisConfiguration(Config redisConfig) {
        this(redisConfig.getString("host"), redisConfig.getInt("port"), redisConfig.getString("password"));
    }
}
