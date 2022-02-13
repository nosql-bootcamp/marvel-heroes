package models;

import org.junit.Assert;
import org.junit.Test;

public class SimpleHeroTest {

    @Test
    public void testParsing() {
        SearchedHero simpleHero = SearchedHero.fromJson("{\n" +
                "    \"id\": \"iron-man\",\n" +
                "    \"imageUrl\": \"https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/7.jpg\",\n" +
                "    \"name\": \"Iron Man\",\n" +
                "    \"universe\": \"Marvel\",\n" +
                "    \"gender\": \"Male\"\n" +
                "}");
        Assert.assertNotNull(simpleHero);
        Assert.assertEquals(simpleHero.id,"iron-man");
        Assert.assertEquals(simpleHero.imageUrl,"https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/7.jpg");
        Assert.assertEquals(simpleHero.name,"Iron Man");
        Assert.assertEquals(simpleHero.universe,"Marvel");
        Assert.assertEquals(simpleHero.gender,"Male");
    }
}
