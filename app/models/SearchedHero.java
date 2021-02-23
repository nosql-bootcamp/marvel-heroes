package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchedHero {

    @JsonProperty("id")
    public final String id;

    @JsonProperty("imageUrl")
    public final String imageUrl;

    @JsonProperty("name")
    public final String name;

    @JsonProperty("universe")
    public final String universe;

    @JsonProperty("gender")
    public final String gender;

    @JsonCreator
    public SearchedHero(@JsonProperty("id") String id, @JsonProperty("thumbnail") String imageUrl, @JsonProperty("name") String name, @JsonProperty("universe") String universe, @JsonProperty("gender") String gender) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.universe = universe;
        this.gender = gender;
    }

    public static SearchedHero fromJson(JsonNode json) {
        return Json.fromJson(json, SearchedHero.class);
    }

    public static SearchedHero fromJson(String json) {
        return SearchedHero.fromJson(Json.parse(json));
    }

}
