package org.ilya;

import org.ilya.matchmaker.OfflineMatchmaker;
import org.ilya.matchmaker.OnlineMatchmaker;
import org.ilya.models.Match;
import org.ilya.models.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Specify input file");
            return;
        }

        ArrayList<Player> players = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        String line = reader.readLine();
        while (line != null && line.length() > 0) {
            String[] tokens = line.split(",");
            players.add(new Player(tokens[0], Integer.parseInt(tokens[1]), Long.parseLong(tokens[2])));
            line = reader.readLine();
        }
        reader.close();

        // RIP :(
        // OfflineMatchmaker matchmaker = new OfflineMatchmaker();
        // ArrayList<Match> matches = matchmaker.make(players);

        OnlineMatchmaker matchmaker = new OnlineMatchmaker();
        for (Player player : players) {
            matchmaker.addPlayer(player);
        }
        ArrayList<Match> matches = matchmaker.getMatches();

        long waitingTimeSum = 0;
        long maxWaitingTime = 0;
        long minWaitingTime = Long.MAX_VALUE;

        for (Match match : matches) {
            waitingTimeSum += match.getWaitingTime();
            maxWaitingTime = Math.max(maxWaitingTime, match.getWaitingTime());
            minWaitingTime = Math.min(minWaitingTime, match.getWaitingTime());

            System.out.println(match.toString());
        }

        System.out.println();
        System.out.println(String.format("total matches: %d", matches.size()));
        System.out.println(String.format("average waiting time: %.3f", (double)waitingTimeSum / matches.size()));
        System.out.println(String.format("max waiting time: %d", maxWaitingTime));
        System.out.println(String.format("min waiting time: %d", minWaitingTime));
    }

}