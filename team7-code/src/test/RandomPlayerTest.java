import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class RandomPlayerTest {

    @Test
    void initialize(){
        ArrayList<Colors> allC = new ArrayList<Colors>();
        allC.add(Colors.RED);
        MPlayer testP = new RandomPlayer("Test");
        testP.initialize(Colors.BLUE, allC);
        assertEquals(allC.size(), 2);
        assertEquals(allC.get(1), Colors.BLUE);
        assertEquals(testP.getColor(), Colors.BLUE);
        try{
            testP.initialize(Colors.RED, allC);
            fail("Exception not thrown when trying to initialize with a used color.");
        }catch(RuntimeException ignore){}
        try{
            testP.initialize(Colors.GREEN, allC);
            fail("Exception not thrown when initializing initialized player.");
        }catch(RuntimeException ignore){}
    }

    @Test
    void chooseColor() {
        MPlayer testP = new RandomPlayer("Test");
        ArrayList<Colors> availableC = new ArrayList<Colors>();
        availableC.add(Colors.BLUE);
        availableC.add(Colors.RED);
        assertEquals(testP.chooseColor(availableC), Colors.BLUE);
        assertFalse(availableC.contains(Colors.BLUE));
    }
}