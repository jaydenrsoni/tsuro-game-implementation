import org.junit.After;
import org.junit.Test;
import org.junit.Assert;

import static org.junit.Assert.*;

/**
 * Created by vyasalwar on 4/19/18.
 */
public class TileTest {

    @Test
    public void EqualsTest(){
        Tile t1 = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile t2 = new Tile(1, 0, 2, 3, 4, 5, 6, 7);
        Tile t3 = new Tile(4,5,6,7,0,1,2,3);
        Tile t4 = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        Assert.assertTrue(t1.equals(t2));
        Assert.assertTrue(t2.equals(t1));
        Assert.assertTrue(t2.equals(t3));
        Assert.assertFalse(t1.equals(t4));
    }

    @Test
    public void RotatedTilesAreEqualTest(){
        // This one was found to fail reflexivity for some reason
        Tile t1 = new Tile(7, 1, 5, 0, 2, 3, 4, 6);
        Tile t2 = new Tile(t1);
        t1.rotateClockwise();

        Assert.assertTrue(t1.equals(t1));
        Assert.assertTrue(t1.equals(t2));
    }

    @Test
    public void SymmetryTest(){
        /*
             Tile 1               Tile 2             Tile 3
              |  |                |  |                |   |
             -+  +-            -+  \/   +-       -+    \  +--
                                |  /\   |         |     \
             -+  +-            -+ /  \  +-       -+   .--+---
              |  |                |  |               /    \
                                                     |    |
             Sym=4              Sym=2                Sym=1

      */

        Tile tile1 = new Tile(1, 2, 3, 4, 5, 6, 7, 0);
        Tile tile2 = new Tile(0, 4, 1, 5, 2, 3, 6, 7);
        Tile tile3 = new Tile(0, 4, 1, 2, 3, 5, 6, 7);

        Assert.assertEquals(tile1.calculateSymmetries(), 4);
        Assert.assertEquals(tile2.calculateSymmetries(), 2);
        Assert.assertEquals(tile3.calculateSymmetries(), 1);

        // Rotations shouldn't affect symmetries
        tile1.rotateClockwise();
        tile2.rotateClockwise();
        tile3.rotateClockwise();

        Assert.assertEquals(tile1.calculateSymmetries(), 4);
        Assert.assertEquals(tile2.calculateSymmetries(), 2);
        Assert.assertEquals(tile3.calculateSymmetries(), 1);
    }

}