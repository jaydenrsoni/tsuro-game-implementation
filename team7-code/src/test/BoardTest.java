import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by Michael on 4/17/2018.
 */
public class BoardTest {

    @Test
    void addAndGetTile()  throws CloneNotSupportedException{
        Board testBoard = new Board();
        PTile[][] testPTiles = new PTile[6][6];
        assertArrayEquals(testBoard.getPTiles(), testPTiles);
        PTile testP = new PTile(new int[][] {{0,1},{2,3},{4,5},{6,7}});
        testBoard.addTile(testP, new int[]{0,0});
        testPTiles[0][0] = testP;
        assertArrayEquals(testBoard.getPTiles(), testPTiles);
    }

    @Test
    void getPTile() throws CloneNotSupportedException {
        Board testBoard = new Board();
        PTile testP = new PTile(new int[][] {{0,1},{2,3},{4,5},{6,7}});
        testBoard.addTile(testP, new int[]{0,0});
        assertTrue(testBoard.getPTile(new int[]{0,0}) == testP);
    }

    @Test
    void isLocationEmpty() throws CloneNotSupportedException {
        Board testBoard = new Board();
        PTile testP = new PTile(new int[][] {{0,1},{2,3},{4,5},{6,7}});
        testBoard.addTile(testP, new int[]{0,0});
        assertTrue(testBoard.isLocationEmpty(new int[]{1,1}));
        assertFalse(testBoard.isLocationEmpty(new int[]{0,0}));
    }

    @Test
    void cloneMethod() throws CloneNotSupportedException{
        Board testBoard = new Board();
        PTile testP = new PTile(new int[][] {{0,1},{2,3},{4,5},{6,7}});
        testBoard.addTile(testP, new int[]{0,0});
        Board dupBoard = (Board)testBoard.clone();
        PTile testP1 = new PTile(new int[][] {{0,3},{2,1},{4,5},{6,7}});
        testBoard.addTile(testP1, new int[]{0,1});
        assertFalse(testBoard.isLocationEmpty(new int[]{0,1}));
        assertTrue(dupBoard.isLocationEmpty(new int[]{0,1}));
    }

    @Test
    void playerLocationsTest(){
        Board b = new Board();
        b.setPlayerLocation(Colors.HOTPINK, new int[]{0,1,2});
        assertTrue(Arrays.equals(b.getPlayerLocations().get(Colors.HOTPINK), new int[]{0,1,2}));
        b.removePlayer(Colors.HOTPINK);
        try{
            b.removePlayer(Colors.HOTPINK);
            fail("Error not thrown when removing nonexistent player from board!");
        }
        catch(RuntimeException ignore){}
    }

    @Test
    void playerLocationTest(){
        Board b = new Board();
        b.setPlayerLocation(Colors.HOTPINK, new int[]{0,1,2});
        b.setPlayerLocation(Colors.BLUE, new int[]{0,2,3});
        b.setPlayerLocation(Colors.RED, new int[]{2,3,0});
        assertTrue(Arrays.equals(b.getPlayerLocation(Colors.HOTPINK), new int[]{0,1,2}));
    }
}