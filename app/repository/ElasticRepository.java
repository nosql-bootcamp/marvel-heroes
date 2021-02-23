package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        String elasticQuery = "{\"from\":"+size * (page-1)+",\"query\": {\"query_string\": {\"query\":\"(*"+input+"*)\",\"fields\": [\"name\",\"aliases\",\"secretIdentities\",\"description\",\"partners\"]}}}";
        List<SearchedHero> searchedHeroes = new ArrayList<SearchedHero>();
        return wsClient.url(elasticConfiguration.uri + "/heroes/_search")
                .post(Json.parse(elasticQuery))
                .thenApply(response -> {
                    response.asJson().get("hits").get("hits").iterator().forEachRemaining(element -> {
                        searchedHeroes.add(SearchedHero.fromJson(element.get("_source")));
                    });
                    int totalSize = response.asJson().get("hits").get("total").get("value").asInt();
                    System.out.println("searchedHeroes size  ::::::"+searchedHeroes.size());
                    return new PaginatedResults<>(size, page, totalSize / size, searchedHeroes);
                });
        //return CompletableFuture.completedFuture(new PaginatedResults<>(3, 1, 1, Arrays.asList(SearchedHeroSamples.IronMan(), SearchedHeroSamples.MsMarvel(), SearchedHeroSamples.SpiderMan())));
    }

    public CompletionStage<List<SearchedHero>> suggest(String input) {
        System.out.println("input : " + input);
        String elasticQuery = "{\"from\": 0, \"size\": 3,\"query\": {\"query_string\":  {\"query\":\"(*"+input+"*)\",\"fields\": [\"name\",\"aliases\"] } } }";
        //return wsClient.url(elasticConfiguration.uri)
        wsClient.url(elasticConfiguration.uri + "heroes/_search").post(Json.parse(elasticQuery))
                .thenApply(response -> {
                    //System.out.println(response.asJson());
                   return null;
                });
        return CompletableFuture.completedFuture(Arrays.asList(SearchedHeroSamples.IronMan(), SearchedHeroSamples.MsMarvel(), SearchedHeroSamples.SpiderMan()));
        // TODO
        // return wsClient.url(elasticConfiguration.uri + )
        //         .post(Json.parse("{ ... }"))
        //         .thenApply(response -> {
        //             return ...
        //         });
    }
}
