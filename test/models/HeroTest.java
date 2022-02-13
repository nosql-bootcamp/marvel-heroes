package models;

import org.junit.Assert;
import org.junit.Test;

public class HeroTest {

    @Test
    public void testParsing() {
        Hero hero = Hero.fromJson("{\n" +
                "    \"id\": \"blabla\",\n" +
                "    \"imageUrl\": \"https://image.url\",\n" +
                "    \"backgroundImageUrl\": \"https://backgroundimage.url\",\n" +
                "    \"externalLink\": \"https://www.external.link\",\n" +
                "    \"description\": \"Wounded, captured and forced to build a weapon by his enemies, billionaire industrialist Tony Stark instead created an advanced suit of armor to save his life and escape captivity. Now with a new outlook on life, Tony uses his money and intelligence to make the world a safer, better place as Iron Man.\",\n" +
                "    \"identity\": {\n" +
                "        \"secretIdentities\": [\"Anthony Edward Stark\"],\n" +
                "        \"birthPlace\": \"Long Island, New York\",\n" +
                "        \"aliases\": [\n" +
                "        \"Iron Man\",\n" +
                "        \"The Invincible Iron Man\",\n" +
                "        \"Tony Stark\"\n" +
                "        ],\n" +
                "        \"alignment\": \"good\",\n" +
                "        \"firstAppearance\": \"Tales of Suspence #39 (March, 1963)\",\n" +
                "        \"yearAppearance\": 1963,\n" +
                "        \"universe\": \"Marvel\"\n" +
                "    },\n" +
                "    \"appearance\": {\n" +
                "        \"gender\": \"Male\",\n" +
                "        \"type\": \"Mutate\",\n" +
                "        \"race\": \"Human\",\n" +
                "        \"height\": 191,\n" +
                "        \"weight\": 101,\n" +
                "        \"eyeColor\": \"blue\",\n" +
                "        \"hairColor\": \"black\"\n" +
                "    },\n" +
                "    \"teams\": [\n" +
                "      \"Avengers\",\n" +
                "      \"Department of Defense\",\n" +
                "      \"Force Works\",\n" +
                "      \"Guardians of the Galaxy\",\n" +
                "      \"Guardians of the Galaxy (2008 team)\",\n" +
                "      \"Illuminati\",\n" +
                "      \"New Avengers\",\n" +
                "      \"S.H.I.E.L.D\",\n" +
                "      \"Stark Industries\",\n" +
                "      \"Stark Resilient\",\n" +
                "      \"The Mighty Avengers\",\n" +
                "      \"The New Avengers\",\n" +
                "      \"Thunderbolts\",\n" +
                "      \"United States Department of Defense\",\n" +
                "      \"West Coast Avengers\"\n" +
                "    ],\n" +
                "    \"powers\": [\n" +
                "      \"Businessperson\",\n" +
                "      \"Cyberpathic\",\n" +
                "      \"Durability\",\n" +
                "      \"Energy repulsors\",\n" +
                "      \"Engineer\",\n" +
                "      \"Genius\",\n" +
                "      \"Genius-level intellect\",\n" +
                "      \"Life support\",\n" +
                "      \"Missile\",\n" +
                "      \"Missiles\",\n" +
                "      \"Powered armored suit\",\n" +
                "      \"Regenerative\",\n" +
                "      \"Regenerative life support\",\n" +
                "      \"Superhuman strength\",\n" +
                "      \"Via\"\n" +
                "    ],\n" +
                "    \"partners\": [\n" +
                "      \"Pepper Potts\",\n" +
                "      \"Rescue\",\n" +
                "      \"War Machine\"\n" +
                "    ],\n" +
                "    \"skills\": {\n" +
                "      \"intelligence\": 100,\n" +
                "      \"strength\": 85,\n" +
                "      \"speed\": 58,\n" +
                "      \"durability\": 85,\n" +
                "      \"power\": 100,\n" +
                "      \"combat\": 64\n" +
                "    },\n" +
                "    \"creators\": [\n" +
                "      \"Don Heck\",\n" +
                "      \"Jack Kirby\",\n" +
                "      \"Larry Lieber\",\n" +
                "      \"Stan Lee\"\n" +
                "    ]\n" +
                "}");
        Assert.assertNotNull(hero);
        Assert.assertNotNull(hero.identity);
        Assert.assertTrue(hero.isMarvel());
        Assert.assertNotNull(hero.skills);
        Assert.assertNotNull(hero.appearance);
        Assert.assertTrue(hero.identity.yearAppearance.isPresent());
        Assert.assertEquals(hero.identity.yearAppearance.get(), Integer.valueOf(1963));
    }
}
