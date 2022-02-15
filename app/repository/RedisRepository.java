package repository;

import io.lettuce.core.RedisClient;
import models.StatItem;
import models.TopStatItem;
import play.Logger;
import play.libs.Json;
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
        return redisClient.connect().async().incr(statItem.slug).thenApply(aLong -> true);
    }


    private CompletionStage<Long> addHeroAsLastVisited(StatItem statItem) {
        return redisClient.connect().async().sadd("lastVisited", Json.stringify(Json.toJson(statItem)));
    }

    public CompletionStage<List<StatItem>> lastHeroesVisited(int count) {
        logger.info("Retrieved last heroes");

        return redisClient.connect().async().smembers("lastVisited").thenApply(strings -> {
            return strings.stream().map(StatItem::fromJson).limit(5).collect(Collectors.toList());
        });
    }

    public CompletionStage<List<TopStatItem>> topHeroesVisited(int count) {
        logger.info("Retrieved tops heroes");
        // TODO
        List<TopStatItem> tops = Arrays.asList(new TopStatItem(StatItemSamples.MsMarvel(), 8L), new TopStatItem(StatItemSamples.Starlord(), 6L), new TopStatItem(StatItemSamples.SpiderMan(), 5L), new TopStatItem(StatItemSamples.BlackPanther(), 5L), new TopStatItem(StatItemSamples.Thanos(), 4L));
        return CompletableFuture.completedFuture(tops);
    }
}
