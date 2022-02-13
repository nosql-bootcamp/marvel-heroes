package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.Heroes;
import services.Stats;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private static final int SIZE = 10;
    private final Stats stats;
    private final Heroes heroes;

    @Inject
    public HomeController(Stats stats, Heroes heroes) {
        this.stats = stats;
        this.heroes = heroes;
    }

    public CompletionStage<Result> heroes(Http.Request request) {
        CompletableFuture<?>[] completableFutures = new CompletableFuture[]{
                heroes.searchHeroes("*", SIZE, 1).toCompletableFuture(),
                stats.topsHeroes(5).toCompletableFuture(),
                stats.lastsHeroes(5).toCompletableFuture()};
        return CompletableFuture.allOf(
                completableFutures
        ).thenApply(v -> {
            List<?> collect = Arrays.stream(completableFutures).map(CompletableFuture::join).collect(Collectors.toList());
            PaginatedResults<SearchedHero> heroesRetrieved = (PaginatedResults<SearchedHero>) collect.get(0);
            List<TopStatItem> tops = (List<TopStatItem>) collect.get(1);
            List<StatItem> lasts = (List<StatItem>) collect.get(2);
            return ok(views.html.heroes.render(request, heroesRetrieved, tops, lasts));
        });

    }

    public CompletionStage<Result> searchHeroes(String q, int page) {
        return heroes.searchHeroes(q, SIZE, page)
                .thenApply(results -> {
                    return ok(Json.stringify(results.toJson()));
                });
    }

    public CompletionStage<Result> suggestHeroes(String q) {
        return heroes.suggest(q)
                .thenApply(results -> {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode resultsAsTree = mapper.valueToTree(results);
                    return ok(Json.stringify(resultsAsTree));
                });
    }

    public CompletionStage<Result> hero(Http.Request request, String heroId) {
        return heroes.hero(heroId)
                .thenApply(maybeHero -> {
                    if (maybeHero.isPresent()) {
                        return ok(views.html.hero.render(request, maybeHero.get()));
                    } else {
                        return notFound(views.html.error.render(request, "Hero not found", "Hero not found"));
                    }
                });

    }

    public CompletionStage<Result> stats(Http.Request request) {
        CompletableFuture<?>[] completableFutures = new CompletableFuture[]{
                stats.byUniverse().toCompletableFuture(),
                stats.byYearAndUniverse().toCompletableFuture(),
                stats.topPowers(5).toCompletableFuture(),
        };
        return CompletableFuture.allOf(
                completableFutures
        ).thenApply(v -> {
            List<?> collect = Arrays.stream(completableFutures).map(CompletableFuture::join).collect(Collectors.toList());
            List<ItemCount> countByUniverse = (List<ItemCount>) collect.get(0);
            List<YearAndUniverseStat> yearAndUniverseStats = (List<YearAndUniverseStat>) collect.get(1);
            List<ItemCount> topPowers = (List<ItemCount>) collect.get(2);
            return ok(views.html.stats.render(request, countByUniverse, yearAndUniverseStats, topPowers));
        });

    }

}
