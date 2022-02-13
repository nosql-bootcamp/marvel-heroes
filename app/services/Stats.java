package services;

import models.ItemCount;
import models.StatItem;
import models.TopStatItem;
import models.YearAndUniverseStat;
import repository.MongoDBRepository;
import repository.RedisRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Singleton
public class Stats {

    private final RedisRepository redisRepository;
    private final MongoDBRepository mongoDBRepository;

    @Inject
    public Stats(RedisRepository redisRepository, MongoDBRepository mongoDBRepository) {
        this.redisRepository = redisRepository;
        this.mongoDBRepository = mongoDBRepository;
    }

    public CompletionStage<List<TopStatItem>> topsHeroes(int size) {
        return redisRepository.topHeroesVisited(size);
    }

    public CompletionStage<List<StatItem>> lastsHeroes(int size) {
        return redisRepository.lastHeroesVisited(size);
    }

    public CompletionStage<List<ItemCount>> byUniverse() {
        return mongoDBRepository.byUniverse();
    }

    public CompletionStage<List<YearAndUniverseStat>> byYearAndUniverse() {
        return mongoDBRepository.countByYearAndUniverse();
    }

    public CompletionStage<List<ItemCount>> topPowers(int top) {
        return mongoDBRepository.topPowers(top);
    }

}
