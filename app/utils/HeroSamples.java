package utils;

import models.Hero;
import play.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class HeroSamples {

    private HeroSamples() {}

    public static Hero ironMan() throws IOException {
        List<String> lines = Files.readAllLines(Environment.simple().getFile("./conf/data/iron-man-sample.json").toPath());
        return Hero.fromJson(String.join("", lines));
    }

    public static Hero spiderMan() throws IOException {
        List<String> lines = Files.readAllLines(Environment.simple().getFile("./conf/data/spider-man-sample.json").toPath());
        return Hero.fromJson(String.join("", lines));
    }

    public static Hero batman() throws IOException {
        List<String> lines = Files.readAllLines(Environment.simple().getFile("./conf/data/batman-sample.json").toPath());
        return Hero.fromJson(String.join("", lines));
    }

    public static Hero superman() throws IOException {
        List<String> lines = Files.readAllLines(Environment.simple().getFile("./conf/data/superman-sample.json").toPath());
        return Hero.fromJson(String.join("", lines));
    }

    public static CompletionStage<Optional<Hero>> staticHero(String heroId) {
        Hero hero = null;
        try {
            switch (heroId) {
                case "iron-man":
                    hero = HeroSamples.ironMan();
                    break;
                case "batman":
                    hero = HeroSamples.batman();
                    break;
                case "superman":
                    hero = HeroSamples.superman();
                    break;
                case "spider-man":
                    hero = HeroSamples.spiderMan();
                    break;
                default:

            }
        } catch (IOException e) {
        }
        return CompletableFuture.completedFuture(Optional.ofNullable(hero));
    }
}
