package repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import env.ElasticConfiguration;
import env.MarvelHeroesConfiguration;
import models.Hero;
import models.PaginatedResults;
import models.SearchedHero;
import play.libs.Json;
import play.libs.ws.WSClient;
import utils.SearchedHeroSamples;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

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
                        "      \"query\": \"" + input + "\", \n" +
                        "      \"fields\": [ \"name^10\", \"aliases^5\", \"secretIdentities^5\", \"description^2\", \"partners^1\" ] \n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"size\": " + size + ",\n" +
                        "  \"from\": " + ((page - 1) * size) + "\n" +
                        "}"))
                .thenApply(response -> {
                    JsonNode hits = Json.parse(response.getBody()).get("hits");
                    Iterator<JsonNode> herosAsJsonNodeIterator = hits.get("hits").iterator();
                    ArrayList<SearchedHero> heros = new ArrayList<>();

                    while (herosAsJsonNodeIterator.hasNext()) {
                        JsonNode val = herosAsJsonNodeIterator.next();
                        heros.add(SearchedHero.fromJson(val.get("_source")));
                    }
                    int totalHits = hits.get("total").get("value").asInt();
                    return new PaginatedResults<SearchedHero>(totalHits, page, (int) Math.ceil(totalHits / size) + 1, heros);
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
