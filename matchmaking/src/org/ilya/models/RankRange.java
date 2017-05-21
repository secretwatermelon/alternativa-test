package org.ilya.models;

public class RankRange {
    private int minRank;
    private int maxRank;

    public RankRange(int minRank, int maxRank) {
        this.minRank = Math.max(Player.MIN_RANK, minRank);
        this.maxRank = Math.min(Player.MAX_RANK, maxRank);
    }

    public int getMinRank() {
        return minRank;
    }

    public int getMaxRank() {
        return maxRank;
    }

    public boolean inRange(int rank) {
        return getMinRank() <= rank && rank <= getMaxRank();
    }
}
