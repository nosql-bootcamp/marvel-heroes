package models;

import org.junit.Assert;
import org.junit.Test;

public class HeroSkillsTest {

    @Test
    public void testParsingSkills() {
        HeroSkills heroSkills = HeroSkills.fromJson("{\n" +
                "    \"intelligence\": 78,\n" +
                "    \"strength\": 88,\n" +
                "    \"speed\": 94,\n" +
                "    \"durability\": 81,\n" +
                "    \"power\": 100,\n" +
                "    \"combat\": 80\n" +
                "}");
        Assert.assertNotNull(heroSkills);
        Assert.assertEquals(heroSkills.intelligence, 78);
        Assert.assertEquals(heroSkills.strength, 88);
        Assert.assertEquals(heroSkills.speed, 94);
        Assert.assertEquals(heroSkills.durability, 81);
        Assert.assertEquals(heroSkills.combat, 80);
        Assert.assertEquals(heroSkills.power, 100);
    }
}
