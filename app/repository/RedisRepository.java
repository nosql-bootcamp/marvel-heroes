package repository;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import models.StatItem;
import models.TopStatItem;
import play.Logger;
import utils.StatItemSamples;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Singleton
public class RedisRepository {

    private static Logger.ALogger logger = Logger.of("RedisRepository");

    private final static String TOP_VISITED_HEROES = "topVisitedHeroes";
    private final static String LAST_VISITED_HEROES = "lastVisitedHeroes";

    private final RedisClient redisClient;

    @Inject
    public RedisRepository(RedisClient redisClient) {
        this.redisClient = redisClient;
    }


    public CompletionStage<Boolean> addNewHeroVisited(StatItem statItem) {
        logger.info("hero visited " + statItem.name);
        return addHeroAsLastVisited(statItem).thenCombine(incrHeroInTops(statItem), (aLong, aBoolean) -> {
            return aBoolean && aLong > 0;
        });
    }

    private CompletionStage<Boolean> incrHeroInTops(StatItem statItem) {
        final StatefulRedisConnection<String, String> connection = this.redisClient.connect();

        Double score = connection.sync().zscore(TOP_VISITED_HEROES, fromItemToJsonString(statItem));
        if(score != null) {
            connection.sync().zadd(TOP_VISITED_HEROES, score + 1, fromItemToJsonString(statItem));
        } else {
            connection.sync().zadd(TOP_VISITED_HEROES, 1, fromItemToJsonString(statItem));
        }

        connection.close();
        return CompletableFuture.completedFuture(true);
    }


    private CompletionStage<Long> addHeroAsLastVisited(StatItem statItem) {
        final StatefulRedisConnection<String, String> connection = this.redisClient.connect();

        connection.sync().zadd(LAST_VISITED_HEROES, System.currentTimeMillis(), fromItemToJsonString(statItem));

        connection.close();
        return CompletableFuture.completedFuture(1L);
    }

    public CompletionStage<List<StatItem>> lastHeroesVisited(int count) {
        logger.info("Retrieved last heroes");
        final StatefulRedisConnection<String, String> connection = this.redisClient.connect();

        List<StatItem> result = connection.sync().zrevrange(LAST_VISITED_HEROES, 0, count)
                .stream()
                .map(json -> StatItem.fromJson(json))
                .collect(Collectors.toList());

        connection.close();
        return CompletableFuture.completedFuture(result);
    }

    public CompletionStage<List<TopStatItem>> topHeroesVisited(int count) {
        logger.info("Retrieved tops heroes");
        final StatefulRedisConnection<String, String> connection = this.redisClient.connect();

        List<TopStatItem> result = connection.sync().zrevrange(TOP_VISITED_HEROES, 0, count)
                .stream()
                .map(json -> {
                    StatItem item = StatItem.fromJson(json);
                    Double score = connection.sync().zscore(TOP_VISITED_HEROES, json);
                    return new TopStatItem(item, score.longValue());
                }).collect(Collectors.toList());

        connection.close();
        return CompletableFuture.completedFuture(result);
    }

    private String fromItemToJsonString(StatItem item) {
        return item.toJson().toString();
    }
}
