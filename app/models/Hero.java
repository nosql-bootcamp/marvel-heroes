package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Hero {

    public final String id;
    public final String name;
    public final String imageUrl;
    public final String backgroundImageUrl;
    public final Optional<String> externalLink;
    public final Optional<String> description;
    public final HeroIdentity identity;
    public final HeroAppearance appearance;
    public final List<String> teams;
    public final List<String> powers;
    public final List<String> partners;
    public final HeroSkills skills;
    public final List<String> creators;

    @JsonCreator
    public Hero(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("imageUrl") String imageUrl, @JsonProperty("backgroundImageUrl") String backgroundImageUrl, @JsonProperty("externalLink") Optional<String> externalLink, @JsonProperty("description") Optional<String> description, @JsonProperty("identity") HeroIdentity identity, @JsonProperty("appearance") HeroAppearance appearance, @JsonProperty("teams") List<String> teams, @JsonProperty("powers") List<String> powers, @JsonProperty("partners") List<String> partners, @JsonProperty("skills") HeroSkills skills, @JsonProperty("creators") List<String> creators) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.externalLink = externalLink;
        this.description = description;
        this.identity = identity;
        this.appearance = appearance;
        this.teams = teams;
        this.powers = powers;
        this.partners = partners;
        this.skills = skills;
        this.creators = creators;
    }


    public boolean isMarvel() {
        return identity.universe.equalsIgnoreCase("marvel");
    }

    public static Hero fromJson(JsonNode json) {
        return Json.fromJson(json, Hero.class);
    }

    public static Hero fromJson(String json) {
        return Hero.fromJson(Json.parse(json));
    }
}
