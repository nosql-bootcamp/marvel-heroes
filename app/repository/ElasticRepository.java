package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import env.ElasticConfiguration;
import env.MarvelHeroesConfiguration;
import models.PaginatedResults;
import models.SearchedHero;
import play.libs.Json;
import play.libs.ws.WSAuthScheme;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import utils.SearchedHeroSamples;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
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

        String requestBodyJson =
                "{\"from\":" + size * (page - 1) + ",\"query\": {\"query_string\":{\"query\":\"(*" + input + "*)\"," +
                "\"fields\":[\"name\"," +
                "\"aliases\",\"secretIdentities\",\"description\",\"partners\"]}}}";
        return esRequest("heroes/_search")
                .post(Json.parse(requestBodyJson))
                .thenApply(response -> {

                    ObjectMapper om = new ObjectMapper();
                    List<SearchedHero> heroes =
                            StreamSupport.stream(response.asJson().get("hits").get("hits").spliterator(),
                            false).map(heroNode -> heroNode.get("_source").toString()).map(SearchedHero::
                    fromJson).collect(Collectors.toList());

                    int totalSize = response.asJson().get("hits").get("total").get("value").asInt();

                    return new PaginatedResults<>(size, page, totalSize / size, heroes);
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

    private WSRequest esRequest(String address) {
        if(elasticConfiguration.user != null && elasticConfiguration.password != null) {
            return wsClient.url(elasticConfiguration.uri + "/" + address).setAuth(elasticConfiguration.user,
                    elasticConfiguration.password, WSAuthScheme.BASIC);
        } else {
            return wsClient.url(elasticConfiguration.uri + "/" + address);
        }
    }
}
