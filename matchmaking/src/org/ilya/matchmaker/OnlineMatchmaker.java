package org.ilya.matchmaker;

import org.ilya.models.Match;
import org.ilya.models.Player;
import org.ilya.models.RankRange;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class OnlineMatchmaker {
    private final int MATCH_SIZE = 8;
    private final long TIMESPAN = 5000;

    private ArrayList<Match> matches;
    private ArrayDeque<Player>[] playerBucketsByRank;
    private int currentNumberOfPlayers;
    private long currentTime;

    public OnlineMatchmaker() {
        this.matches = new ArrayList<>();
        this.playerBucketsByRank = (ArrayDeque<Player>[])new ArrayDeque[Player.MAX_RANK + 1];
        for (int i = 0; i < this.playerBucketsByRank.length; i++) {
            this.playerBucketsByRank[i] = new ArrayDeque<>();
        }
        this.currentNumberOfPlayers = 0;
    }

    public void addPlayer(Player player) {
        playerBucketsByRank[player.getRank()].offer(player);
        currentNumberOfPlayers++;
        currentTime = player.getEntryTime();

        if (currentNumberOfPlayers < MATCH_SIZE) {
            return;
        }

        Match match = findMatch();
        while (match != null) {
            currentNumberOfPlayers -= MATCH_SIZE;
            matches.add(match);
            match = findMatch();
        }
    }

    private Match findMatch() {
        int[] prioritizedBuckets = prioritizePlayerBuckets(Player.MIN_RANK, Player.MAX_RANK);

        Match match = new Match();
        boolean fail[] = new boolean[Player.MAX_RANK+1];
        for (int rank : prioritizedBuckets) {
            if (playerBucketsByRank[rank].size() > 0 && backtrack(rank, match, fail)) {
                return match;
            } else {
                fail[rank] = true;
            }
        }

        return null;
    }

    private int[] prioritizePlayerBuckets(int minRank, int maxRank) {
        int[] sortedRanks = new int[maxRank - minRank + 1];
        for (int i = 0; i < sortedRanks.length; i++) {
            sortedRanks[i] = i + minRank;
        }

        return Arrays.stream(sortedRanks).
                boxed().
                sorted((a, b) -> comparePlayerBucketsByWaitingTime(a, b)).
                mapToInt(i -> i).
                toArray();
    }

    private int comparePlayerBucketsByWaitingTime(int rank1, int rank2) {
        Player p1 = playerBucketsByRank[rank1].peek();
        Player p2 = playerBucketsByRank[rank2].peek();

        long waitingTime1 = p1 != null ? p1.getWaitingTime(currentTime) : -Long.MAX_VALUE;
        long waitingTime2 = p2 != null ? p2.getWaitingTime(currentTime) : -Long.MAX_VALUE;

        if (waitingTime1 < waitingTime2) {
            return 1;
        } else if (waitingTime1 == waitingTime2) {
            return 0;
        } else {
            return -1;
        }
    }

    private boolean backtrack(int rank, Match match, boolean[] fail) {
        match.addPlayer(playerBucketsByRank[rank].poll());
        if (match.getCurrentMatchSize() == MATCH_SIZE) {
            return true;
        }

        RankRange rankRange = match.getLastAddedPlayer().getAllowedRanksRange(currentTime, TIMESPAN);
        int[] prioritizedBuckets = prioritizePlayerBuckets(rankRange.getMinRank(), rankRange.getMaxRank());
        for (int nextRankBucket : prioritizedBuckets) {
            if (!fail[nextRankBucket] && playerBucketsByRank[nextRankBucket].size() > 0 && playerCanJoinMatch(playerBucketsByRank[nextRankBucket].peek(), match)) {
                if (backtrack(nextRankBucket, match, fail)) {
                    return true;
                }
            }
        }

        playerBucketsByRank[rank].addFirst(match.getLastAddedPlayer());
        match.removeLastAddedPlayer();
        return false;
    }

    private boolean playerCanJoinMatch(Player player, Match match) {
        RankRange playerRankRange = player.getAllowedRanksRange(currentTime, TIMESPAN);

        for (Player matchPlayer : match.getPlayers()) {
            RankRange matchPlayerRankRange = matchPlayer.getAllowedRanksRange(currentTime, TIMESPAN);
            if (!(playerRankRange.inRange(matchPlayer.getRank()) && matchPlayerRankRange.inRange(player.getRank()))) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }
}