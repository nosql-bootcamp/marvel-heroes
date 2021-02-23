package repository;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import models.Hero;
import models.ItemCount;
import models.YearAndUniverseStat;
import org.bson.Document;
import org.bson.conversions.Bson;
import play.libs.Json;
import utils.ReactiveStreamsUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Singleton
public class MongoDBRepository {

    private final MongoCollection<Document> heroesCollection;

    @Inject
    public MongoDBRepository(MongoDatabase mongoDatabase) {
        this.heroesCollection = mongoDatabase.getCollection("heroes");
    }


    public CompletionStage<Optional<Hero>> heroById(String heroId) {
        String query = "{\"id\": \"" + heroId + "\"}";
        Document document = Document.parse(query);
        return ReactiveStreamsUtils.fromSinglePublisher(heroesCollection.find(document).first())
                .thenApply(result -> Optional.ofNullable(result).map(Document::toJson).map(Hero::fromJson));
    }

    public CompletionStage<List<YearAndUniverseStat>> countByYearAndUniverse() {
        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Aggregates.match(Document.parse("{ \"identity.yearAppearance\" : { $exists: true, $ne: \"\" } }")));
        pipeline.add(Aggregates.group(
                Document.parse("{ yearAppearance:\"$identity.yearAppearance\",byUniverse:\"$identity.universe\"}"),
                Accumulators.sum("count", 1)));
        return ReactiveStreamsUtils.fromMultiPublisher(heroesCollection.aggregate(pipeline))
                .thenApply(documents -> {
                    HashMap<Integer, List<ItemCount>> all = new HashMap<>();
                    documents.stream()
                            .map(Document::toJson)
                            .map(Json::parse)
                            .forEach(jsonNode -> {
                                System.out.println(jsonNode);
                                int year = jsonNode.findPath("_id").findPath("yearAppearance").asInt();
                                String universe = jsonNode.findPath("_id").findPath("byUniverse").asText();
                                int count = jsonNode.findPath("count").asInt();
                                if (!all.containsKey(year)) {
                                    all.put(year, new ArrayList<>());
                                }
                                all.get(year).add(new ItemCount(universe, count));
                            });
                    return all.keySet().stream().map(year -> new YearAndUniverseStat(year, all.get(year))).collect(Collectors.toList());
                });
    }


    public CompletionStage<List<ItemCount>> topPowers(int top) {
        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Aggregates.unwind("$powers"));
        pipeline.add(Aggregates.group("$powers", Accumulators.sum("count", 1)));
        pipeline.add(Aggregates.sort(Document.parse("{ count : -1}")));
        pipeline.add(Aggregates.match(Document.parse("{ _id : { $exists: true, $ne: \"\" }}")));
        return ReactiveStreamsUtils.fromMultiPublisher(heroesCollection.aggregate(pipeline))
                .thenApply(documents -> {
                    return documents.subList(0, 5).stream()
                            .map(Document::toJson)
                            .map(Json::parse)
                            .map(jsonNode -> {
                                return new ItemCount(jsonNode.findPath("_id").asText(), jsonNode.findPath("count").asInt());
                            })
                            .collect(Collectors.toList());
                });
    }

    public CompletionStage<List<ItemCount>> byUniverse() {
        List<Bson> pipeline = Arrays.asList(
                Aggregates.group("$identity.universe", Accumulators.sum("count", 1))
        );
        return ReactiveStreamsUtils.fromMultiPublisher(heroesCollection.aggregate(pipeline))
                .thenApply(documents -> {
                    return documents.stream()
                            .map(Document::toJson)
                            .map(Json::parse)
                            .map(jsonNode -> {
                                return new ItemCount(jsonNode.findPath("_id").asText(), jsonNode.findPath("count").asInt());
                            })
                            .collect(Collectors.toList());
                });
    }

}
