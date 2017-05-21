package org.ilya.models;

import java.util.ArrayList;

public class Match {
    private ArrayList<Player> players;
    private long maxPlayerEntryTime;
    private long minPlayerEntryTime;

    public Match() {
        this.players = new ArrayList<>();
        this.maxPlayerEntryTime = 0;
        this.minPlayerEntryTime = Long.MAX_VALUE;
    }

    public void addPlayer(Player player) {
        maxPlayerEntryTime = Math.max(maxPlayerEntryTime, player.getEntryTime());
        minPlayerEntryTime = Math.min(minPlayerEntryTime, player.getEntryTime());

        players.add(player);
    }

    public int getCurrentMatchSize() {
        return players.size();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getLastAddedPlayer() {
        return players.size() > 0 ? players.get(players.size() - 1) : null;
    }

    public void removeLastAddedPlayer() {
        if (players.size() > 0) {
            players.remove(players.size() - 1);
        }
    }

    public long getMaxPlayerEntryTime() {
        return maxPlayerEntryTime;
    }

    public long getMinPlayerEntryTime() {
        return minPlayerEntryTime;
    }

    public long getWaitingTime() {
        return getMaxPlayerEntryTime() - getMinPlayerEntryTime();
    }

    public boolean playerCanJoin(Player player, long currentTime, long timespan) {
        RankRange playerRankRange = player.getAllowedRanksRange(currentTime, timespan);

        for (Player matchPlayer : getPlayers()) {
            RankRange matchPlayerRankRange = matchPlayer.getAllowedRanksRange(currentTime, timespan);
            if (!(playerRankRange.inRange(matchPlayer.getRank()) && matchPlayerRankRange.inRange(player.getRank()))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getMaxPlayerEntryTime());
        for (Player player : players) {
            sb.append(String.format(" %s", player.getUsername()));
        }

        return sb.toString();
    }
}
