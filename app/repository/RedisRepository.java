package repository;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import models.StatItem;
import models.TopStatItem;
import play.Logger;
import utils.StatItemSamples;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
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
        // TODO
        // connect to DB + close co + use synchonous
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        // create a command set
        RedisCommands<String, String> syncCommands = connection.sync();
        // retrieve score for statItem
        Double score = syncCommands.zscore("heroesInTop", statItem.toJson().toString());
        // update score
        syncCommands.zadd("heroesInTop",  score != null ?  score + 1 : 1, statItem.toJson().toString());
        // close connection
        connection.close();
        return CompletableFuture.completedFuture(true);
    }


    private CompletionStage<Long> addHeroAsLastVisited(StatItem statItem) {
        // TODO
        // connect to DB
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        // create a command set
        RedisCommands<String, String> syncCommands = connection.sync();
        // add hero in the sorted set
        syncCommands.zadd("lastVisitedHeroes", (double)(new Date().getTime()), statItem.toJson().toString());
        // close connection
        connection.close();
        return CompletableFuture.completedFuture(1L);
    }

    public CompletionStage<List<StatItem>> lastHeroesVisited(int count) {
        logger.info("Retrieved last heroes");
        // TODO
        // connect to DB
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        // create a command set
        RedisCommands<String, String> syncCommands = connection.sync();
        // retrieve 'count' heroes from  bottom
        List<StatItem> res = syncCommands
                .zrevrange("lastVisitedHeroes", 0, count)
                .stream()
                .map(x -> StatItem.fromJson(x)).collect(Collectors.toList());
        // close connection
        connection.close();
        //List<StatItem> lastsHeroes = Arrays.asList(StatItemSamples.IronMan(), StatItemSamples.Thor(), StatItemSamples.CaptainAmerica(), StatItemSamples.BlackWidow(), StatItemSamples.MsMarvel());
        return CompletableFuture.completedFuture(res);
    }

    public CompletionStage<List<TopStatItem>> topHeroesVisited(int count) {
        logger.info("Retrieved tops heroes");
        // TODO
        //List<TopStatItem> tops = Arrays.asList(new TopStatItem(StatItemSamples.MsMarvel(), 8L), new TopStatItem(StatItemSamples.Starlord(), 6L), new TopStatItem(StatItemSamples.SpiderMan(), 5L), new TopStatItem(StatItemSamples.BlackPanther(), 5L), new TopStatItem(StatItemSamples.Thanos(), 4L));
        // connect to DB
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        // create a command set
        RedisCommands<String, String> syncCommands = connection.sync();
        // retrieve 'count' heroes from  bottom
        List<TopStatItem> res = syncCommands.zrevrange("heroesInTop", 0, count)
                .stream()
                .map(x -> {
                    StatItem item = StatItem.fromJson(x);
                    Double score = syncCommands.zscore("heroesInTop", item.toJson().toString());
                    System.out.println("SCORE : " + score + "\nITEM : " + item);
                    return new TopStatItem(item, score.longValue());
                }).collect(Collectors.toList());
        // close connection
        connection.close();
        return CompletableFuture.completedFuture(res);
    }
}
