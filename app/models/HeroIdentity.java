package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.List;
import java.util.Optional;

public class HeroIdentity {
    public final List<String> secretIdentities;
    public final Optional<String> birthPlace;
    public final Optional<String> occupation;
    public final List<String> aliases;
    public final Optional<String> alignment;
    public final Optional<String> firstAppearance;
    public final Optional<Integer> yearAppearance;
    public final String universe;

    @JsonCreator
    public HeroIdentity(@JsonProperty("secretIdentities") List<String> secretIdentities, @JsonProperty("birthPlace") Optional<String> birthPlace, @JsonProperty("occupation") Optional<String> occupation, @JsonProperty("aliases") List<String> aliases, @JsonProperty("alignment") Optional<String> alignment, @JsonProperty("firstAppearance") Optional<String> firstAppearance, @JsonProperty("yearAppearance") Optional<Integer> yearAppearance, @JsonProperty("universe") String universe) {
        this.secretIdentities = secretIdentities;
        this.birthPlace = birthPlace;
        this.occupation = occupation;
        this.aliases = aliases;
        this.alignment = alignment;
        this.firstAppearance = firstAppearance;
        this.yearAppearance = yearAppearance;
        this.universe = universe;
    }

    public static HeroIdentity fromJson(JsonNode json) {
        return Json.fromJson(json, HeroIdentity.class);
    }

    public static HeroIdentity fromJson(String json) {
        return HeroIdentity.fromJson(Json.parse(json));
    }
}
