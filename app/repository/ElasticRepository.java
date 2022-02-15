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
import java.util.Iterator;
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
        int from = size * (page-1);
        String jsonRequest = "{\"from\": " + from + ",\"size\": " + size + ",\"query\": {\"wildcard\": {\"name\": {\"value\": \"*" + input + "*\"}}}}";
        return wsClient.url(elasticConfiguration.uri + "/heroes/_search")
             .post(Json.parse(jsonRequest))
             .thenApply(response -> {
                 JsonNode searchResult = Json.parse(response.getBody()).get("hits");
                 int total = searchResult.get("total").get("value").asInt();

                 List<SearchedHero> hits = new ArrayList<>(total);
                 for (Iterator<JsonNode> it = searchResult.get("hits").elements(); it.hasNext(); ) {
                    JsonNode hit = it.next().get("_source");
                    hits.add(
                            new SearchedHero(
                                    hit.get("id").asText(),
                                    hit.get("imageUrl").asText(),
                                    hit.get("name").asText(),
                                    hit.get("universe").asText(),
                                    hit.get("gender").asText()
                            )
                    );
                 }
                 return new PaginatedResults<>(total, page, total/size + 1, hits);
             });
    }

    public CompletionStage<List<SearchedHero>> suggest(String input) {
         return CompletableFuture.completedFuture(Arrays.asList(SearchedHeroSamples.IronMan(), SearchedHeroSamples.MsMarvel(), SearchedHeroSamples.SpiderMan()));
        //String jsonRequest = "{\"from\": " + from + ",\"size\": " + size + ",\"query\": {\"wildcard\": {\"name\": {\"value\": \"*" + input + "*\"}}}}";
        /*return wsClient.url(elasticConfiguration.uri + "/heroes/_suggest")
                .post(Json.parse(jsonRequest))
                .thenApply(response -> {
                    JsonNode searchResult = Json.parse(response.getBody()).get("hits");
                    int total = searchResult.get("total").get("value").asInt();

                    List<SearchedHero> hits = new ArrayList<>(total);
                    for (Iterator<JsonNode> it = searchResult.get("hits").elements(); it.hasNext(); ) {
                        JsonNode hit = it.next().get("_source");
                        hits.add(
                                new SearchedHero(
                                        hit.get("id").asText(),
                                        hit.get("imageUrl").asText(),
                                        hit.get("name").asText(),
                                        hit.get("universe").asText(),
                                        hit.get("gender").asText()
                                )
                        );
                    }
                    return new PaginatedResults<>(total, page, total/size, hits);
                });*/
    }
}
