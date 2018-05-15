import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Created by Michael on 4/20/2018.
 */
public class SPlayerTest{

    @Test
    void testClone() throws CloneNotSupportedException {
        SPlayer s = new SPlayer(new int[]{0,0,5}, Colors.HOTPINK);
        PTile pt = new PTile(new int[][]{{0,3},{2,1},{4,5},{6,7}});
        s.addTileToHand(pt);
        SPlayer sClone = (SPlayer)s.clone();
        assertEquals(sClone.getHand().get(0), pt);
        s.removeTileFromHand(pt);
        // testing for color cloning
        assertEquals(sClone.getColor(), s.getColor());
        // testing for marker cloning
        s.getLocation()[0] = 1;
        assertFalse(Arrays.equals(s.getLocation(), sClone.getLocation()));
        // testing for hand cloning
        assertEquals(sClone.getHand().get(0), pt);
    }

    @Test
    void removeTileFromHand() throws CloneNotSupportedException{
        SPlayer s = new SPlayer(new int[]{0,0,5}, Colors.HOTPINK);
        PTile pt = new PTile(new int[][]{{0,3},{2,1},{4,5},{6,7}});
        s.addTileToHand(pt);
        assertEquals(s.getHand().get(0), pt);
        s.removeTileFromHand(pt);
        assertEquals(s.getHand().size(), 0);
        try{
            s.removeTileFromHand(pt);
            fail("Exception not thrown when removeTileFromHand is called on a player with no tiles.");
        }
        catch(RuntimeException ignore){}
    }

    @Test
    void getNextTileLocation() {
        SPlayer s = new SPlayer(new int[]{0,0,5}, Colors.HOTPINK);
        assertTrue(Arrays.equals(new int[]{0,0}, s.getNextTileLocation()));
    }

    @Test
    void remainsOnBoard() throws CloneNotSupportedException{
        Board testBoard = new Board();
        PTile testP = new PTile(new int[][] {{5,6},{3,4},{2,7},{0,1}});
        testBoard.addTile(testP, new int[]{0,0});
        PTile testP1 = new PTile(new int[][] {{0,6},{5,7},{1,4},{2,3}});
        testBoard.addTile(testP1, new int[]{1,0});
        SPlayer safePlayer1 = new SPlayer(new int[]{0,0,4}, Colors.HOTPINK);
        SPlayer safePlayer2 = new SPlayer(new int[]{0,1,7}, Colors.BLUE);
        SPlayer safePlayer3 = new SPlayer(new int[]{1,0,4}, Colors.GREEN);
        SPlayer deadPlayer1 = new SPlayer(new int[]{0,0,5}, Colors.RED);
        SPlayer deadPlayer2 = new SPlayer(new int[]{0,0,7}, Colors.PURPLE);
        SPlayer deadPlayer3 = new SPlayer(new int[]{1,0,5}, Colors.SIENNA);
        assertTrue(safePlayer1.movePlayer_inGame(testBoard));
        assertTrue(safePlayer2.movePlayer_inGame(testBoard));
        assertTrue(safePlayer3.movePlayer_inGame(testBoard));
        assertFalse(deadPlayer1.movePlayer_inGame(testBoard));
        assertFalse(deadPlayer2.movePlayer_inGame(testBoard));
        assertFalse(deadPlayer3.movePlayer_inGame(testBoard));
    }

}