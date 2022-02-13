package utils;

import models.StatItem;

public class StatItemSamples {

    private StatItemSamples() {}

    public static StatItem MsMarvel() {
        return new StatItem("ms-marvel", "Ms Marvel", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/1.jpg", "hero");
    }

    public static StatItem Starlord() {
        return new StatItem("starlord", "Starlord", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/2.jpg", "hero");
    }

    public static StatItem SpiderMan() {
        return new StatItem("spider-man", "Spider-Man", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/3.jpg", "hero");
    }

    public static StatItem BlackPanther() {
        return new StatItem("black-panther", "Black Panther", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/4.jpg", "hero");
    }

    public static StatItem Thanos() {
        return new StatItem("thanos", "Thanos", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/6.jpg", "hero");
    }

    public static StatItem IronMan() {
        return new StatItem("iron-man", "Iron Man", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/7.jpg", "hero");
    }

    public static StatItem Thor() {
        return new StatItem("thor", "Thor", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/8.jpg", "hero");
    }

    public static StatItem CaptainAmerica() {
        return new StatItem("captain-americ", "Captain America", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/9.jpg", "hero");
    }

    public static StatItem BlackWidow() {
        return new StatItem("black-widow", "Black Widow", "https://s3-us-west-2.amazonaws.com/s.cdpn.io/3794/10.jpg", "hero");
    }

}
