package models;

import org.junit.Assert;
import org.junit.Test;

public class HeroIdentitityTest {

    @Test
    public void testParsing() {
        HeroIdentity heroIdentity = HeroIdentity.fromJson("{\n" +
                "    \"secretIdentities\": [\"Anthony Edward Stark\"],\n" +
                "    \"birthPlace\": \"Long Island, New York\",\n" +
                "    \"aliases\": [\n" +
                "      \"Iron Man\",\n" +
                "      \"The Invincible Iron Man\",\n" +
                "      \"Tony Stark\"\n" +
                "    ],\n" +
                "    \"alignment\": \"good\",\n" +
                "    \"firstAppearance\": \"Tales of Suspence #39 (March, 1963)\",\n" +
                "    \"yearAppearance\": 1963,\n" +
                "    \"universe\": \"Marvel\"\n" +
                "}");
        Assert.assertNotNull(heroIdentity);
        Assert.assertEquals(1, heroIdentity.secretIdentities.size());
        Assert.assertEquals("Anthony Edward Stark", heroIdentity.secretIdentities.get(0));
        Assert.assertFalse(heroIdentity.occupation.isPresent());
        Assert.assertTrue(heroIdentity.birthPlace.isPresent());
        Assert.assertEquals("Long Island, New York", heroIdentity.birthPlace.get());
        Assert.assertEquals(3, heroIdentity.aliases.size());
        Assert.assertEquals("Iron Man", heroIdentity.aliases.get(0));
        Assert.assertEquals("The Invincible Iron Man", heroIdentity.aliases.get(1));
        Assert.assertEquals("Tony Stark", heroIdentity.aliases.get(2));
    }
}
