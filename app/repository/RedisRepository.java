package repository;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import models.StatItem;
import models.TopStatItem;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Singleton
public class RedisRepository {

  private static Logger.ALogger logger = Logger.of("RedisRepository");


  private final RedisClient redisClient;
  private StatefulRedisConnection<String, String> redisConnection;

  @Inject
  public RedisRepository(RedisClient redisClient) {
    this.redisClient = redisClient;
  }

  public RedisCommands<String, String> getRedisAPI() {
    redisConnection = redisClient.connect();
    return redisConnection.sync();
  }


  public CompletionStage<Boolean> addNewHeroVisited(StatItem statItem) {
    logger.info("hero visited " + statItem.name);
    return addHeroAsLastVisited(statItem).thenCombine(incrHeroInTops(statItem), (aLong, aBoolean) -> {
      return aBoolean && aLong > 0;
    });
  }

  private CompletionStage<Boolean> incrHeroInTops(StatItem statItem) {
    // TODO
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    Double score = syncCommands.zscore("heroesInTop", statItem.toJson().toString());
    syncCommands.zadd("heroesInTop", score != null ? score + 1 : 1, statItem.toJson().toString());
    connection.close();
    return CompletableFuture.completedFuture(true);
  }


  private CompletionStage<Long> addHeroAsLastVisited(StatItem statItem) {
    // TODO
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    syncCommands.zadd("lastVisitedHeroes", (double) (new Date().getTime()), statItem.toJson().toString());
    connection.close();
    return CompletableFuture.completedFuture(1L);
  }

  public CompletionStage<List<StatItem>> lastHeroesVisited(int count) {
    logger.info("Retrieved last heroes");
    // TODO
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    List<StatItem> res = syncCommands
      .zrevrange("lastVisitedHeroes", 0, count - 1)
      .stream()
      .map(x -> StatItem.fromJson(x)).collect(Collectors.toList());
    connection.close();
    return CompletableFuture.completedFuture(res);

    //        List<StatItem> lastsHeroes = Arrays.asList(StatItemSamples.IronMan(), StatItemSamples.Thor(), StatItemSamples.CaptainAmerica(), StatItemSamples.BlackWidow(), StatItemSamples.MsMarvel());
    //    return CompletableFuture.completedFuture(lastsHeroes);
  }

  public CompletionStage<List<TopStatItem>> topHeroesVisited(int count) {
    logger.info("Retrieved tops heroes");
    // TODO
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    List<TopStatItem> top = syncCommands.zrevrange("heroesInTop", 0, count - 1)
      .stream()
      .map(x -> {
        StatItem item = StatItem.fromJson(x);
        Double score = syncCommands.zscore("heroesInTop", item.toJson().toString());
        System.out.println("SCORE : " + score + "\nITEM : " + item);
        return new TopStatItem(item, score.longValue());
      }).collect(Collectors.toList());
    connection.close();
    return CompletableFuture.completedFuture(top);


    //    logger.info("Retrieved tops heroes");
    //    // TODO
    //    List<TopStatItem> tops = Arrays.asList(new TopStatItem(StatItemSamples.MsMarvel(), 8L), new TopStatItem(StatItemSamples.Starlord(), 6L), new TopStatItem(StatItemSamples.SpiderMan(), 5L), new TopStatItem(StatItemSamples.BlackPanther(), 5L), new TopStatItem(StatItemSamples.Thanos(), 4L));
    //    return CompletableFuture.completedFuture(tops);
  }
}
