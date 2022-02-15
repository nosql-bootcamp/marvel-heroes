package repository;

import env.ElasticConfiguration;
import env.MarvelHeroesConfiguration;
import models.PaginatedResults;
import models.SearchedHero;
import play.libs.ws.WSClient;
import utils.SearchedHeroSamples;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

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
        // return CompletableFuture.completedFuture(new PaginatedResults<>(3, 1, 1, Arrays.asList(SearchedHeroSamples.IronMan(), SearchedHeroSamples.MsMarvel(), SearchedHeroSamples.SpiderMan())));
        // TODO
        String query = "" +
                "{\n" +
                " \"query\" : {\n" +
                "    \"query_string\" : {\n" +
                "      \"query\" : \"*" + input + "*\",\n" +
                "      \"fields\" : [\n" +
                "        \"name\"\n" +
                "      ]\n" +
                "    }\n" +
                "}\n" +
                "}";

        return wsClient.url(elasticConfiguration.uri + "/heroes/_search")
                .post(Json.parse(query))
                .thenApply(response -> {

                    var resJson = response.asJson();

                    Iterator<JsonNode> elements = resJson.findPath("hits").findPath("hits").elements();
                    Iterable<JsonNode> iterable = () -> elements;
                    List<SearchedHero> heroes = StreamSupport.stream(iterable.spliterator(), false)
                            .map(x -> {
                                // System.out.println(x.toString());
                                return SearchedHero.fromJson(x.findPath("_source").toString());
                            })
                            .collect(Collectors.toList());
                    return new PaginatedResults<>(3, 1, 1, heroes);
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