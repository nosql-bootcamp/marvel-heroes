package env;

import com.typesafe.config.Config;

public class ElasticConfiguration {

    public final String uri;
    public final String user;
    public final String password;

    public ElasticConfiguration(String uri, String user, String password) {
        this.uri = uri;
        this.user = user;
        this.password = password;
    }

    public ElasticConfiguration(Config elasticConfig) {
        this(elasticConfig.getString("host"), elasticConfig.getString("port"), elasticConfig.getString("password"));
    }
}
