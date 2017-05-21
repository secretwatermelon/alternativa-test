package org.ilya.models;

public class Player {
    public static final int MIN_RANK = 1;
    public static final int MAX_RANK = 30;

    private String username;
    private int rank;
    private long entryTime;

    public Player(String username, int rank, long entryTime) {
        this.username = username;
        this.rank = rank;
        this.entryTime = entryTime;
    }

    public String getUsername() {
        return username;
    }

    public int getRank() {
        return rank;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public long getWaitingTime(long currentTime) {
        return currentTime - entryTime;
    }

    public RankRange getAllowedRanksRange(long currentTime, long timespan) {
        int maxRankVariation = (int)Math.min((long)Player.MAX_RANK - 1, getWaitingTime(currentTime) / timespan);

        return new RankRange(getRank()-maxRankVariation, getRank()+maxRankVariation);
    }

    @Override
    public String toString() {
        return getUsername();
    }
}