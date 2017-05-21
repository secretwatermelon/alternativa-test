package models;

import junit.framework.TestCase;
import org.ilya.models.Match;
import org.ilya.models.Player;

import java.util.ArrayList;

public class MatchTest extends TestCase {
    public void testAddPlayer() {
        Match match = new Match();
        Player player = new Player("abc", 4, 5000);
        assertEquals(0, match.getCurrentMatchSize());

        match.addPlayer(player);
        assertEquals(1, match.getCurrentMatchSize());
        assertEquals(5000, match.getMinPlayerEntryTime());
        assertEquals(5000, match.getMaxPlayerEntryTime());
        assertEquals(player, match.getPlayers().get(0));
    }

    public void testGetCurrentMatchSize() {
        Match match = new Match();
        for (int i = 0; i < 10; i++) {
            match.addPlayer(new Player(Integer.toString(i), 1, 5000));
            assertEquals(i+1, match.getCurrentMatchSize());
        }
    }

    public void testGetMinMaxPlayerEntryTime() {
        Match match = new Match();
        assertEquals(Long.MAX_VALUE, match.getMinPlayerEntryTime());
        assertEquals(0, match.getMaxPlayerEntryTime());

        match.addPlayer(new Player("a", 1, 5000));
        assertEquals(5000, match.getMinPlayerEntryTime());
        assertEquals(5000, match.getMaxPlayerEntryTime());

        match.addPlayer(new Player("a", 1, 6000));
        assertEquals(5000, match.getMinPlayerEntryTime());
        assertEquals(6000, match.getMaxPlayerEntryTime());

        match.addPlayer(new Player("a", 1, 4000));
        assertEquals(4000, match.getMinPlayerEntryTime());
        assertEquals(6000, match.getMaxPlayerEntryTime());
    }

    public void testGetPlayers() {
        Match match = new Match();
        match.addPlayer(new Player("a", 1, 5000));
        match.addPlayer(new Player("b", 1, 6000));
        match.addPlayer(new Player("c", 1, 4000));

        ArrayList<Player> players = match.getPlayers();
        assertNotNull(players);
        assertEquals(3, players.size());
    }

    public void testGetLastAddedPlayer() {
        Match match = new Match();
        Player lastPlayer = new Player("d", 1, 10000);
        assertNull(match.getLastAddedPlayer());

        match.addPlayer(new Player("a", 1, 5000));
        match.addPlayer(new Player("b", 1, 6000));
        match.addPlayer(new Player("c", 1, 4000));
        match.addPlayer(lastPlayer);

        Player actual = match.getLastAddedPlayer();
        assertNotNull(actual);
        assertEquals(lastPlayer, actual);
    }

    public void testRemoveLastAddedPlayer() {
        Match match = new Match();
        match.removeLastAddedPlayer();

        Player p1 = new Player("a", 1, 5000);
        Player p2 = new Player("b", 1, 4000);
        Player p3 = new Player("c", 1, 6000);

        match.addPlayer(p1);
        match.addPlayer(p2);
        match.addPlayer(p3);

        assertEquals(3, match.getCurrentMatchSize());
        assertEquals(p3, match.getLastAddedPlayer());

        match.removeLastAddedPlayer();

        assertEquals(2, match.getCurrentMatchSize());
        assertEquals(p2, match.getLastAddedPlayer());
    }

    public void testGetWaitingTime() {
        Match match = new Match();

        Player p1 = new Player("a", 1, 5000);
        Player p2 = new Player("b", 1, 4000);
        Player p3 = new Player("c", 1, 6000);

        match.addPlayer(p1);
        match.addPlayer(p2);
        match.addPlayer(p3);

        assertEquals(2000, match.getWaitingTime());
    }

    public void testPlayerCanJoinWithSmallWaitingTime() {
        Match match = new Match();

        Player p1 = new Player("a", 1, 5000);
        Player p2 = new Player("b", 1, 4000);
        Player p3 = new Player("c", 1, 6000);

        match.addPlayer(p1);
        match.addPlayer(p2);
        match.addPlayer(p3);

        assertTrue(match.playerCanJoin(new Player("d", 1, 1000), 10000, 5000));
        assertFalse(match.playerCanJoin(new Player("e", 30, 1000), 10000, 5000));
    }

    public void testPlayerCanJoinWithBigWaitingTime() {
        Match match = new Match();

        Player p1 = new Player("a", 10, 25000);
        Player p2 = new Player("b", 10, 25000);
        Player p3 = new Player("c", 10, 25000);

        match.addPlayer(p1);
        match.addPlayer(p2);
        match.addPlayer(p3);

        assertTrue(match.playerCanJoin(new Player("d", 7, 20000), 50000, 5000));
        assertFalse(match.playerCanJoin(new Player("e", 30, 1000), 50000, 5000));
    }

    public void testToString() {
        Match match = new Match();

        Player p1 = new Player("a", 10, 25000);
        Player p2 = new Player("b", 10, 25000);
        Player p3 = new Player("c", 10, 25000);

        match.addPlayer(p1);
        match.addPlayer(p2);
        match.addPlayer(p3);
        assertEquals("25000 a b c", match.toString());
    }
}
