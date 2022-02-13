package models;

import org.junit.Test;
import org.junit.Assert;

public class StatItemTest {

    @Test
    public void testParsing() {
        StatItem statItem = StatItem.fromJson("{\n" +
                "    \"slug\": \"ms-marvel\",\n" +
                "    \"name\": \"Ms Marvel\",\n" +
                "    \"imageUrl\": \"https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/1.jpg\",\n" +
                "    \"type\": \"hero\"\n" +
                "}");
        Assert.assertNotNull(statItem);
        Assert.assertEquals(statItem.slug, "ms-marvel");
        Assert.assertEquals(statItem.name, "Ms Marvel");
        Assert.assertEquals(statItem.imageUrl, "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/1.jpg");
        Assert.assertEquals(statItem.type, "hero");
    }
}
