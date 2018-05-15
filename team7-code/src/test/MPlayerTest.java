import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MPlayerTest {

    @Test
    void cloneTest() throws CloneNotSupportedException {
        MPlayer testP = new RandomPlayer("test");
        testP.initialize(Colors.BLUE, new ArrayList<Colors>());
        MPlayer testPClone = testP.clone();
        assertEquals(testPClone.getColor().name(), testP.getColor().name());
        assertEquals(testPClone.getName(), testP.getName());
        assertFalse(testPClone == testP);
        assertFalse(testPClone.equals(testP));
    }

    @Test
    void getColor() {
        MPlayer testP = new RandomPlayer("Test");
        testP.initialize(Colors.BLUE, new ArrayList<Colors>());
        assertEquals(testP.getColor(), Colors.BLUE);
    }

    @Test
    void getName() {
        MPlayer testP = new RandomPlayer("Test");
        assertEquals(testP.getName(), "Test");
    }

    @Test
    void endGame() {
    }
}