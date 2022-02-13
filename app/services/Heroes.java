package services;

import models.Hero;
import models.PaginatedResults;
import models.SearchedHero;
import models.StatItem;
import repository.ElasticRepository;
import repository.MongoDBRepository;
import repository.RedisRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Singleton
public class Heroes {


    private final MongoDBRepository mongoDBRepository;
    private final ElasticRepository elasticRepository;
    private final RedisRepository redisRepository;


    @Inject
    public Heroes(ElasticRepository elasticRepository, MongoDBRepository mongoDBRepository, RedisRepository redisRepository) {
        this.elasticRepository = elasticRepository;
        this.mongoDBRepository = mongoDBRepository;
        this.redisRepository = redisRepository;
    }

    public CompletionStage<PaginatedResults<SearchedHero>> searchHeroes(String input, int size, int page) {
        return elasticRepository.searchHeroes(input, size, page);
    }

    public CompletionStage<List<SearchedHero>> suggest(String input) {
        return elasticRepository.suggest(input);
    }

    public CompletionStage<Optional<Hero>> hero(String heroId) {
        return mongoDBRepository.heroById(heroId).thenApply(maybeHero -> {
            maybeHero.ifPresent(hero -> redisRepository.addNewHeroVisited(StatItem.fromHero(hero)));
            return maybeHero;
        });
    }
}
