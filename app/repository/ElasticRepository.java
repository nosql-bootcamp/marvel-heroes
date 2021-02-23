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
        int from = (page - 1) * size;
        return wsClient.url(elasticConfiguration.uri + "/_search")
                .post(Json.parse("{\"query\": {\"multi_match\" : {\"query\":  \""+input+"\", \n" +
                        " \"fields\": [ \"name\",  \"identity.secretIdentities\", \"identity.aliases\", \"description\"] \n" +
                        " }},\"from\": " + from + ",\"size\": " + size + "}"
                        ))
                .thenApply(response -> {
                    JsonNode responseJson = Json.parse(response.getBody()).get("hits");
                    int total = responseJson.get("total").get("value").asInt();
                    JsonNode hitsJson = responseJson.get("hits");
                    List<SearchedHero> results = new ArrayList<>();
                    for (JsonNode hit : hitsJson) {
                        results.add(SearchedHero.fromJson(hit.get("_source")));
                    }
                    int nbPages = 1 + (total / size);
                    return new PaginatedResults<SearchedHero>(total, page,nbPages , results);
                });
    }

    public CompletionStage<List<SearchedHero>> suggest(String input) {
        return wsClient.url(elasticConfiguration.uri + "/_search")
                .post(Json.parse("{\"query\": {\"multi_match\" : {\"query\":  \""+input+"\", \n" +
                        " \"fields\": [ \"name\",  \"identity.secretIdentities\", \"identity.aliases\", \"description\"] \n" +
                        " }},\"size\": " + 5 + "}"
                        ))
                .thenApply(response -> {
                    JsonNode responseJson = Json.parse(response.getBody()).get("hits");
                    JsonNode hitsJson = responseJson.get("hits");
                    List<SearchedHero> results = new ArrayList<>();
                    for (JsonNode hit : hitsJson) {
                        results.add(SearchedHero.fromJson(hit.get("_source")));
                    }
                    return results;
                });
    }
}
