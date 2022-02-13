package models;

import java.util.List;

public class YearAndUniverseStat {

    public final int yearAppearance;

    public final List<ItemCount> byUniverse;


    public YearAndUniverseStat(int yearAppearance, List<ItemCount> byUniverse) {
        this.yearAppearance = yearAppearance;
        this.byUniverse = byUniverse;
    }
}
