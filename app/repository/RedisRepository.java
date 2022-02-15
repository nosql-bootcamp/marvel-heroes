package repository;

import io.lettuce.core.RedisClient;
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

    private static final String LAST_VISITED_KEY = "lasts_new";
    private static final String TOP_KEY = "top";


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
        var connection = this.redisClient.connect().async();
        String hashkey = String.format("%s:%s", TOP_KEY, statItem.slug);
        connection.exists(hashkey).thenApply(l -> {
            if (l == 0) {
                return connection.hset(hashkey, "stateItem", statItem.toJson().asText())
                        .thenApply(b -> connection.hset(hashkey, "hits", "1"))
                        .thenAccept(null);
            } else {
                return connection.hincrby(hashkey, "hits", 1);
            }
        });
        return CompletableFuture.completedFuture(true);
    }


    private CompletionStage<Long> addHeroAsLastVisited(StatItem statItem) {
        var connection = this.redisClient.connect().async();
        String json = statItem.toJson().toString();
        logger.info(json);
        connection.lpush(LAST_VISITED_KEY, json);
        return CompletableFuture.completedFuture(1L);
    }

    public CompletionStage<List<StatItem>> lastHeroesVisited(int count) {
        logger.info("Retrieved last heroes");
        var connection = this.redisClient.connect().async();
        connection.lrange(LAST_VISITED_KEY,0, count-1)
                .thenAccept(jsons -> jsons.forEach(logger::info)
                );
        return connection.lrange(LAST_VISITED_KEY,0, count-1)
                .thenApply(jsons -> jsons.stream().map(StatItem::fromJson)
                        .collect(Collectors.toList()));
    }

    public CompletionStage<List<TopStatItem>> topHeroesVisited(int count) {
        logger.info("Retrieved tops heroes");
        var connection = this.redisClient.connect().async();
        // TODO
        List<TopStatItem> tops = Arrays.asList(new TopStatItem(StatItemSamples.MsMarvel(), 8L), new TopStatItem(StatItemSamples.Starlord(), 6L), new TopStatItem(StatItemSamples.SpiderMan(), 5L), new TopStatItem(StatItemSamples.BlackPanther(), 5L), new TopStatItem(StatItemSamples.Thanos(), 4L));
        return CompletableFuture.completedFuture(tops);
    }
}