package models;

public class TopStatItem {

    public final StatItem statItem;
    public final Long hits;


    public TopStatItem(StatItem statItem, Long hits) {
        this.statItem = statItem;
        this.hits = hits;
    }
}
