package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

public class HeroSkills {

    public final float intelligence;
    public final float strength;
    public final float speed;
    public final float durability;
    public final float combat;
    public final float power;

    @JsonCreator
    public HeroSkills(@JsonProperty("intelligence") float intelligence, @JsonProperty("strength") float strength, @JsonProperty("speed") float speed, @JsonProperty("durability") float durability, @JsonProperty("combat") float combat, @JsonProperty("power") float power) {
        this.intelligence = intelligence;
        this.strength = strength;
        this.speed = speed;
        this.durability = durability;
        this.combat = combat;
        this.power = power;
    }

    public static HeroSkills fromJson(JsonNode json) {
        return Json.fromJson(json, HeroSkills.class);
    }

    public static HeroSkills fromJson(String json) {
        return HeroSkills.fromJson(Json.parse(json));
    }
}
