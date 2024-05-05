package blackboxplus;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void testName() {
        Player player = new Player("Sean");
        assertEquals("Sean", player.getPlayerName());

        try {
            new Player(null);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid input!", e.getMessage());
        }
    }

    @Test
    public void testIncrementScore() {
        Player player = new Player("Sean");
        player.incrementScore(10);
        assertEquals(10, player.getScore());
    }

    @Test
    public void testIncrementRaysShot() {
        Player player = new Player("Sean");
        player.incrementRaysShot();
        player.incrementRaysShot();
        assertEquals(2, player.getRaysShot());
        assertEquals(2, player.getScore());
    }

    @Test
    public void testIncrementAtomsMissed() {
        Player player = new Player("Sean");
        player.incrementAtomsMissed();
        assertEquals(1, player.getAtomsMissed());
        assertEquals(5, player.getScore());
    }

    @Test
    public void testPlayerInfo() {
        Player player = new Player("Sean");
        player.incrementRaysShot();
        player.incrementRaysShot();
        player.incrementAtomsMissed();
        assertEquals("Sean had a score of 7 points with 2 rays shot in total and 1 atoms misplaced!", player.getPlayerInfo());
    }
}
