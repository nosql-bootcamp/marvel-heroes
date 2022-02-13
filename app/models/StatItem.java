package models;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

public class StatItem {

    public final String slug;
    public final String name;
    public final String imageUrl;
    public final String type;

    public StatItem(String slug, String name, String imageUrl, String type) {
        this.slug = slug;
        this.name = name;
        this.imageUrl = imageUrl;
        this.type = type;
    }


    public JsonNode toJson() {
        return Json
                .newObject()
                .put("slug", slug)
                .put("name", name)
                .put("imageUrl", imageUrl)
                .put("type", type);
    }

    public static StatItem fromJson(JsonNode json) {
        String slug = json.findValue("slug").asText();
        String name = json.findValue("name").asText();
        String imageUrl = json.findValue("imageUrl").asText();
        String type = json.findValue("type").asText();
        return new StatItem(slug, name, imageUrl, type);
    }

    public static StatItem fromJson(String json) {
        return StatItem.fromJson(Json.parse(json));
    }

    public static StatItem fromHero(Hero hero) {
        return new StatItem(hero.id, hero.name, hero.imageUrl, "hero");
    }

}
