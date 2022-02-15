package repository;

import com.fasterxml.jackson.databind.JsonNode;
import env.ElasticConfiguration;
import env.MarvelHeroesConfiguration;
import models.PaginatedResults;
import models.SearchedHero;
import play.libs.Json;
import play.libs.ws.WSClient;
import utils.SearchedHeroSamples;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class ElasticRepository {

    private final WSClient wsClient;
    private final ElasticConfiguration elasticConfiguration;

    @Inject
    public ElasticRepository(WSClient wsClient, MarvelHeroesConfiguration configuration) {
        this.wsClient = wsClient;
        this.elasticConfiguration = configuration.elasticConfiguration;
    }


    public CompletionStage<PaginatedResults<SearchedHero>> searchHeroes(String input, int size, int page) {
        return wsClient.url(elasticConfiguration.uri + "/heroes/_search")
                .setBody(Json.parse("{ \"from\": " + page * size + ", \"size\": " + size + ", \"query\": { \"multi_match\": { \"query\": \"" + input + "\" } } }"))
                .get()
                .thenApply(response -> {
                    JsonNode jsonNode = Json.parse(response.getBody());
                    int total = jsonNode.get("hits").get("total").get("value").asInt();
                    int totalPage = total / size;

                    List<SearchedHero> heroes = new ArrayList<>();
                    for (JsonNode node : jsonNode.get("hits").get("hits")) {
                        heroes.add(SearchedHero.fromJson(node.get("_source")));
                    }
                    return new PaginatedResults<>(total, page, totalPage, heroes);
                });
    }

    public CompletionStage<List<SearchedHero>> suggest(String input) {
        return CompletableFuture.completedFuture(Arrays.asList(SearchedHeroSamples.IronMan(), SearchedHeroSamples.MsMarvel(), SearchedHeroSamples.SpiderMan()));
        // TODO
        // return wsClient.url(elasticConfiguration.uri + "...")
        //         .post(Json.parse("{ ... }"))
        //         .thenApply(response -> {
        //             return ...
        //         });
    }
}
