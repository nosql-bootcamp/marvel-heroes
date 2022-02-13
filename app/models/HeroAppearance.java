package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.Optional;

public class HeroAppearance {
    public final Optional<String> gender;
    public final Optional<String> type;
    public final Optional<String> race;
    public final Optional<Float> height;
    public final Optional<Float> weight;
    public final Optional<String> eyeColor;
    public final Optional<String> hairColor;

    @JsonCreator
    public HeroAppearance(@JsonProperty("gender") Optional<String> gender, @JsonProperty("type") Optional<String> type, @JsonProperty("race") Optional<String> race, @JsonProperty("height") Optional<Float> height, @JsonProperty("weight") Optional<Float> weight, @JsonProperty("eyeColor") Optional<String> eyeColor, @JsonProperty("hairColor") Optional<String> hairColor) {
        this.gender = gender;
        this.type = type;
        this.race = race;
        this.height = height;
        this.weight = weight;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
    }

    public static HeroAppearance fromJson(JsonNode json) {
        return Json.fromJson(json, HeroAppearance.class);
    }

    public static HeroAppearance fromJson(String json) {
        return HeroAppearance.fromJson(Json.parse(json));
    }
}
