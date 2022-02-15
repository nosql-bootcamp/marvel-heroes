package repository;

import com.fasterxml.jackson.databind.JsonNode;
import env.ElasticConfiguration;
import env.MarvelHeroesConfiguration;
import models.PaginatedResults;
import models.SearchedHero;
import play.api.libs.json.JsonNaming;
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
        //return CompletableFuture.completedFuture(new PaginatedResults<>(3, 1, 1, Arrays.asList(SearchedHeroSamples.IronMan(), SearchedHeroSamples.MsMarvel(), SearchedHeroSamples.SpiderMan())));
        // TODO
        return wsClient.url(elasticConfiguration.uri + "/heroes/_search")
                .post(Json.parse("{\"query\": {\"match\": {\"name\": \"" + input + "\"}}}"))
                .thenApply(response -> {
                    //return new PaginatedResults<>(1, 1, 1, response);
                    ArrayList<SearchedHero> listOfHeroes = new ArrayList<SearchedHero>();
                    if(response.asJson().findValue("hits").findValue("total").findValue("value").asDouble() > 0) {
                        Iterator<JsonNode> res = response.asJson().findValue("hits").findValue("hits").elements();
                        while(res.hasNext()) {
                            JsonNode current = res.next();
                            System.out.println(current);
                            listOfHeroes.add(new SearchedHero(
                                    current.findValue("id").asText(),
                                    current.findValue("imageUrl").asText(),
                                    current.findValue("_source").findValue("name").asText(),
                                    current.findValue("universe").asText(),
                                    current.findValue("gender").asText()
                            ));
                        }
                    }
                    return new PaginatedResults<>(listOfHeroes.size(), 1, 1, listOfHeroes);
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
