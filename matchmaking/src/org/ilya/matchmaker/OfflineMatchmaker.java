package org.ilya.matchmaker;

import org.ilya.models.Match;
import org.ilya.models.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/**
 * @deprecated
 */
public class OfflineMatchmaker {
    private final int MATCH_SIZE = 8;
    private final long TIMESPAN = 5000;
    private final long CURRENT_TIME = 1000 * 1000;

    private HashSet<Integer>[] playersGraph;
    private boolean used[];

    public ArrayList<Match> make(ArrayList<Player> players) {
        players.sort(new PlayerWaitingTimeComparator());
        buildPlayersGraph(players);

        ArrayList<Match> matches = new ArrayList<>();

        int[] playersIndexes = findMatch();
        while (playersIndexes != null) {
            Match match = new Match();
            for (int playerIndex : playersIndexes) {
                match.addPlayer(players.get(playerIndex));
            }
            matches.add(match);

            playersIndexes = findMatch();
        }

        return matches;
    }

    /**
     * Метод строит граф, в котором вершина означает игрока, а ребро (a,b) означает, что
     * игрок a может играть в одном матче с игроком b. Таким образом, задача сводится к
     * нахождению полных подграфов фиксированного размера в графе.
     *
     * @param players players list
     */
    private void buildPlayersGraph(ArrayList<Player> players) {
        ArrayList<Integer>[] playersByRank = countPlayersByRank(players);
        int[] partialSums = calculatePartialSums(playersByRank);

        HashSet<Integer>[] graph = (HashSet<Integer>[])new HashSet[players.size()];
        for (int i = 0; i < graph.length; i++) {
            graph[i] = new HashSet<>();
        }

        for (int i = 0; i < players.size(); i++) {
            RankRange rankRange = getAllowedRanksRange(players.get(i));
            if (partialSums[rankRange.getMaxRank()] - partialSums[rankRange.getMinRank()-1] - 1 >= MATCH_SIZE-1) {
                for (int j = rankRange.getMinRank()-1; j <= rankRange.getMaxRank()-1; j++) {
                    for (Integer v : playersByRank[j]) {
                        graph[i].add(v);
                        graph[v].add(i);
                    }
                }
                graph[i].remove(i);
            }
        }

        playersGraph = graph;
        used = new boolean[graph.length];
    }

    private ArrayList<Integer>[] countPlayersByRank(ArrayList<Player> players) {
        ArrayList<Integer>[] playersByRank = (ArrayList<Integer>[])new ArrayList[Player.MAX_RANK];
        for (int i = 0; i < Player.MAX_RANK; i++) {
            playersByRank[i] = new ArrayList<>();
        }

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            playersByRank[player.getRank()-1].add(i);
        }

        return playersByRank;
    }

    private int[] calculatePartialSums(ArrayList<Integer>[] playersByRank) {
        int[] partialSums = new int[playersByRank.length+1];
        for (int i = 0; i < playersByRank.length; i++) {
            partialSums[i+1] = partialSums[i] + playersByRank[i].size();
        }

        return partialSums;
    }

    private RankRange getAllowedRanksRange(Player player) {
        int maxRankVariation = (int)Math.min((long)Player.MAX_RANK-1, player.getWaitingTime(CURRENT_TIME) / TIMESPAN);

        return new RankRange(player.getRank()-maxRankVariation, player.getRank()+maxRankVariation);
    }

    private int[] findMatch() {
        int[] currentSolution = new int[MATCH_SIZE];
        for (int i = 0; i < playersGraph.length; i++) {
            if (!used[i] && playersGraph[i].size() >= MATCH_SIZE-1) {
                if (backtrack(i, currentSolution, 0)) {
                    return currentSolution;
                }
            }
        }

        return null;
    }

    private boolean backtrack(int v, int[] currentSolution, int size) {
        currentSolution[size++] = v;
        used[v] = true;

        if (size == MATCH_SIZE) {
            return true;
        }

        for (Integer nextNode: playersGraph[v]) {
            if (!used[nextNode] && isNodeValid(nextNode, currentSolution, size)) {
                if (backtrack(nextNode, currentSolution, size)) {
                    return true;
                }
            }
        }

        used[v] = false;
        return false;
    }

    private boolean isNodeValid(int v, int[] currentSolution, int size) {
        if (playersGraph[v].size() < MATCH_SIZE-1) {
            return false;
        }

        boolean isNextNodeConnectedWithCurrentSolution = true;
        for (int i = 0; i < size; i++) {
            if (!playersGraph[v].contains(currentSolution[i])) {
                isNextNodeConnectedWithCurrentSolution = false;
                break;
            }
        }

        return isNextNodeConnectedWithCurrentSolution;
    }

    private class PlayerWaitingTimeComparator implements Comparator<Player> {
        @Override
        public int compare(Player o1, Player o2) {
            if (o1.getWaitingTime(CURRENT_TIME) - o2.getWaitingTime(CURRENT_TIME) < 0) {
                return 1;
            } else if (o1.getWaitingTime(CURRENT_TIME) - o2.getWaitingTime(CURRENT_TIME) == 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private class RankRange {
        private int minRank;
        private int maxRank;

        RankRange(int minRank, int maxRank) {
            this.minRank = Math.max(1, minRank);
            this.maxRank = Math.min(Player.MAX_RANK, maxRank);
        }

        int getMinRank() {
            return minRank;
        }

        int getMaxRank() {
            return maxRank;
        }
    }
}
