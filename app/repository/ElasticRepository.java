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
        return wsClient.url(elasticConfiguration.uri + "/_search")
                .post(Json.parse("{\n" +
                        "  \"query\": {\n" +
                        "    \"multi_match\" : {\n" +
                        "      \"query\":  \""+input+"\", \n" +
                        "      \"fields\": [ \"name^4\",  \"identity.secretIdentities^3\", \"identity.aliases^3\", \"description^2\", \"partners\"] \n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"size\": " + size + "," +
                        "  \"from\": " + (page - 1) * size +
                        "}"))
                .thenApply(response -> {
                    JsonNode json = Json.parse(response.getBody()).get("hits");
                    int total = json.get("total").get("value").asInt();
                    JsonNode resJson = json.get("hits");
                    List<SearchedHero> results = new ArrayList<>();
                    for (JsonNode item : resJson) {
                        JsonNode itemSource = item.get("_source");
                        results.add(new SearchedHero(
                                item.get("_id").asText(),
                                itemSource.get("imageUrl").asText(),
                                itemSource.get("name").asText(),
                                itemSource.get("identity").get("universe").asText(),
                                itemSource.get("appearance").get("gender").asText()
                        ));
                    }
                    System.out.println("Recherche terminée avec " + results.size() + " résultats.");
                    return new PaginatedResults<SearchedHero>(total, page, 1 + (total / size), results);
                });
    }

    public CompletionStage<List<SearchedHero>> suggest(String input) {
        return wsClient.url(elasticConfiguration.uri + "/_search")
                .post(Json.parse("{\n" +
                        "  \"query\": {\n" +
                        "    \"multi_match\" : {\n" +
                        "      \"query\":  \""+input+"\", \n" +
                        "      \"fields\": [ \"name^4\",  \"identity.secretIdentities^3\", \"identity.aliases^3\" ]" +
                        "    }\n" +
                        "  },\n" +
                        "  \"size\": 5" +
                        "}"))
                .thenApply(response -> {
                    JsonNode json = Json.parse(response.getBody()).get("hits").get("hits");
                    List<SearchedHero> results = new ArrayList<>();
                    for (JsonNode item : json) {
                        JsonNode itemSource = item.get("_source");
                        results.add(new SearchedHero(
                                item.get("_id").asText(),
                                itemSource.get("imageUrl").asText(),
                                itemSource.get("name").asText(),
                                itemSource.get("identity").get("universe").asText(),
                                itemSource.get("appearance").get("gender").asText()
                        ));
                    }
                    System.out.println("Recherche terminée avec " + results.size() + " résultats.");
                    return results;
                });
    }
}
