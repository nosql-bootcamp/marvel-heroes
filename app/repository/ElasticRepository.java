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
        return wsClient.url(elasticConfiguration.uri + "/marvel/_search")
                .post(Json.parse("{ \"query\" : { \"match\" : { \"name\" : \" " + input + "\" } } }"))
                .thenApply(response -> {
                    if (response.getStatus() != 200) {
                        return new PaginatedResults<>(0, 1, 1, new ArrayList<>());
                    }
                    ArrayList<SearchedHero> results = new ArrayList<>();
                    JsonNode parsedJson = Json.parse(response.getBody()).get("hits").get("hits");
                    for (int i = 0 ; i<parsedJson.size(); i++){
                        JsonNode parsedHero = parsedJson.get(i).get("_source");
                        SearchedHero searchedHero = SearchedHero.fromJson(parsedHero);
                        results.add(searchedHero);
                    }
                    return new PaginatedResults<>(3, 1, 1, results);
                });
    }

    public CompletionStage<List<SearchedHero>> suggest(String input) {
        //return CompletableFuture.completedFuture(Arrays.asList(SearchedHeroSamples.IronMan(), SearchedHeroSamples.MsMarvel(), SearchedHeroSamples.SpiderMan()));
        return wsClient.url(elasticConfiguration.uri + "/marvel/_search")
                .post(Json.parse("{ \"suggest\": { \"my-suggest-1\" : { \"text\" : \"" + input + "\", \"term\" : { \"field\" : \"name\" } } } }"))
                .thenApply(response -> {
                    return Arrays.asList(SearchedHeroSamples.IronMan(), SearchedHeroSamples.MsMarvel(), SearchedHeroSamples.SpiderMan());
                });
    }
}
