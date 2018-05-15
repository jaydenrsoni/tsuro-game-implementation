import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by Michael on 4/17/2018.
 */
public class PTileTest {

    @Test
    void rotateAndGetPattern() throws CloneNotSupportedException{
        PTile testP = new PTile(new int[][] {{0,3},{2,1},{4,5},{6,7}});
        testP.rotate();
        PTile testP1 = new PTile(new int[][] {{2,5},{4,3},{6,7},{0,1}});
        assertArrayEquals(testP.getPattern(), testP1.getPattern());
    }

    @Test
    void getNextLocation() throws CloneNotSupportedException{
        PTile testP = new PTile(new int[][] {{0,3},{2,1},{4,5},{6,7}});
        assertEquals(6, testP.getNextMarkerLocation(0));
        assertEquals(1, testP.getNextMarkerLocation(5));
    }

    @Test
    void getNextLocationException() throws CloneNotSupportedException{
        PTile testP = new PTile(new int[][] {{0,3},{2,1},{4,5},{6,7}});
        try{
            testP.getNextMarkerLocation(10);
            fail("Exception not thrown");
        }catch(RuntimeException ignored){
        }
    }

    @Test
    void equals() throws CloneNotSupportedException{
        PTile testP = new PTile(new int[][] {{0,3},{2,1},{4,5},{6,7}});
        PTile testP1 = new PTile(new int[][] {{2,5},{4,3},{6,7},{0,1}});
        PTile testP2 = new PTile(new int[][] {{6,1},{0,7},{2,3},{4,5}});
        assertFalse(testP.equals(new int[] {2,3}));
        assertFalse(testP.equals(null));
        assertTrue(testP.equals(testP));
        assertTrue(testP.equals(testP1));
        assertTrue(testP.equals(testP2));
    }

    @Test
    void testClone() throws CloneNotSupportedException{
        PTile testP = new PTile(new int[][] {{0,3},{2,1},{4,5},{6,7}});
        PTile testPClone = (PTile) testP.clone();
        testP.getPattern()[0][0] = 10;
        assertEquals(0, testPClone.getPattern()[0][0]);
    }
}