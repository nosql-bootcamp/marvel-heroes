package models;

import org.junit.Assert;
import org.junit.Test;

public class HeroAppearanceTest {

    @Test
    public void testParsing() {
        HeroAppearance heroAppearance = HeroAppearance.fromJson("{\n" +
                "    \"gender\": \"Male\",\n" +
                "    \"type\": \"Mutate\",\n" +
                "    \"race\": \"Human\",\n" +
                "    \"height\": 191,\n" +
                "    \"weight\": 101,\n" +
                "    \"eyeColor\": \"blue\",\n" +
                "    \"hairColor\": \"black\"\n" +
                "}");
        Assert.assertNotNull(heroAppearance);
        Assert.assertTrue(heroAppearance.gender.isPresent());
        Assert.assertEquals(heroAppearance.gender.get(), "Male");
        Assert.assertTrue(heroAppearance.type.isPresent());
        Assert.assertEquals(heroAppearance.type.get(), "Mutate");
        Assert.assertTrue(heroAppearance.race.isPresent());
        Assert.assertEquals(heroAppearance.race.get(), "Human");
        Assert.assertTrue(heroAppearance.height.isPresent());
        Assert.assertEquals(heroAppearance.height.get(), Float.valueOf(191.0f));
        Assert.assertTrue(heroAppearance.weight.isPresent());
        Assert.assertEquals(heroAppearance.weight.get(), Float.valueOf(101.0f));
        Assert.assertTrue(heroAppearance.eyeColor.isPresent());
        Assert.assertEquals(heroAppearance.eyeColor.get(), "blue");
        Assert.assertTrue(heroAppearance.hairColor.isPresent());
        Assert.assertEquals(heroAppearance.hairColor.get(), "black");
    }
}
