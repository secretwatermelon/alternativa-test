package matchmaker;

import junit.framework.TestCase;
import org.ilya.matchmaker.OnlineMatchmaker;
import org.ilya.models.Match;
import org.ilya.models.Player;

import java.util.HashSet;

public class OnlineMatchmakerTest extends TestCase {
    public void testGetMatches() {
        OnlineMatchmaker matchmaker = new OnlineMatchmaker();
        assertNotNull(matchmaker.getMatches());
        assertEquals(0, matchmaker.getMatches().size());
    }

    public void testAddPlayer() {
        OnlineMatchmaker matchmaker = new OnlineMatchmaker();
        HashSet<String> usernames = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            usernames.add(Integer.toString(i));
            matchmaker.addPlayer(new Player(Integer.toString(i), 10, 1000 + i));
        }

        assertNotNull(matchmaker.getMatches());
        assertEquals(1, matchmaker.getMatches().size());
        Match match = matchmaker.getMatches().get(0);
        assertEquals(8, match.getCurrentMatchSize());
        for (Player player : match.getPlayers()) {
            assertTrue(usernames.contains(player.getUsername()));
        }
    }

    public void testAddPlayerWithInvalidDecision() {
        OnlineMatchmaker matchmaker = new OnlineMatchmaker();
        HashSet<String> usernames = new HashSet<>();
        matchmaker.addPlayer(new Player("oldPlayer", 30, 1000));
        for (int i = 0; i < 8; i++) {
            usernames.add(Integer.toString(i));
            matchmaker.addPlayer(new Player(Integer.toString(i), 10, 50000 + i));
        }

        assertNotNull(matchmaker.getMatches());
        assertEquals(1, matchmaker.getMatches().size());
        Match match = matchmaker.getMatches().get(0);
        assertEquals(8, match.getCurrentMatchSize());
        for (Player player : match.getPlayers()) {
            assertTrue(usernames.contains(player.getUsername()));
        }
    }
}
