package models;

import junit.framework.TestCase;
import org.ilya.models.Player;
import org.ilya.models.RankRange;

public class RankRangeTest extends TestCase {
    public void testConstructor() {
        RankRange rankRange = new RankRange(5, 25);
        assertEquals(5, rankRange.getMinRank());
        assertEquals(25, rankRange.getMaxRank());
    }

    public void testConstructorWithInvalidValues() {
        RankRange rankRange = new RankRange(-5, 35);
        assertEquals(Player.MIN_RANK, rankRange.getMinRank());
        assertEquals(Player.MAX_RANK, rankRange.getMaxRank());
    }

    public void testInRange() {
        RankRange rankRange = new RankRange(5, 25);

        assertTrue(rankRange.inRange(5));
        assertTrue(rankRange.inRange(15));
        assertTrue(rankRange.inRange(25));
        assertFalse(rankRange.inRange(4));
        assertFalse(rankRange.inRange(26));
        assertFalse(rankRange.inRange(30));

    }
}
