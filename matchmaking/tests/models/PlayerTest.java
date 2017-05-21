package models;

import junit.framework.TestCase;
import org.ilya.models.Player;
import org.ilya.models.RankRange;

public class PlayerTest extends TestCase {
    public void testConstructor() {
        Player player = new Player("abc", 4, 5000);
        assertEquals("abc", player.getUsername());
        assertEquals(4, player.getRank());
        assertEquals(5000, player.getEntryTime());
    }

    public void testGetWaitingTime() {
        Player player = new Player("abc", 4, 5000);
        assertEquals(1000, player.getWaitingTime(6000));
    }

    public void testGetAllowedRankRange() {
        Player player = new Player("abc", 4, 5000);
        RankRange rankRange = player.getAllowedRanksRange(20000, 5000);
        assertNotNull(rankRange);
        assertEquals(1, rankRange.getMinRank());
        assertEquals(7, rankRange.getMaxRank());
    }

    public void testToString() {
        Player player = new Player("abc", 4, 5000);
        assertEquals("abc", player.toString());
    }
}
