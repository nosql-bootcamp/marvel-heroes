package utils;

import models.SearchedHero;

public final class SearchedHeroSamples {

    private SearchedHeroSamples(){}

    public static SearchedHero IronMan() {
        return new SearchedHero("iron-man", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/7.jpg", "Iron Man", "Marvel", "Male");
    }

    public static SearchedHero SpiderMan() {
        return new SearchedHero("spider-man", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/3.jpg", "Spider-Man", "Marvel", "Male");
    }
    public static SearchedHero MsMarvel() {
        return new SearchedHero("ms-marvel", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/1.jpg", "Ms Marvel", "Marvel", "Female");
    }

}
