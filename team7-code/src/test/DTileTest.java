import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Michael on 4/20/2018.
 */
public class DTileTest {

    @Test
    void testClone() throws CloneNotSupportedException {
        DTile testD = new DTile();
        DTile testDClone = (DTile)testD.clone();
        assertTrue(testD.getClass().equals(testDClone.getClass()));
    }
}