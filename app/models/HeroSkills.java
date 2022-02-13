package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

public class HeroSkills {

    public final int intelligence;
    public final int strength;
    public final int speed;
    public final int durability;
    public final int combat;
    public final int power;

    @JsonCreator
    public HeroSkills(@JsonProperty("intelligence") int intelligence, @JsonProperty("strength") int strength, @JsonProperty("speed") int speed, @JsonProperty("durability") int durability, @JsonProperty("combat") int combat, @JsonProperty("power") int power) {
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
