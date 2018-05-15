package test;

import main.*;
import main.Players.APlayer;
import main.Players.MostSymmetricPlayer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MostSymmetricPlayerTest {

    @Mock
    TilePile tilePileMock;

    @Before
    public void gameReset(){
        Game.resetGame();
        Game.getGame().setTilePile(tilePileMock);
    }

    @Test
    public void MostSymmetricPlayerCanBeInstantiatedTest() {
        APlayer mostSymmetricPlayer = new MostSymmetricPlayer("Vyas", Color.BLACK);
    }

    @Test
    public void MostSymmetricPlayerMakesUnsafeMovesOnlyWhenNecessaryTest(){
        when(tilePileMock.drawFromDeck())
                .thenReturn(new Tile(0, 1, 2, 3, 4, 5, 6, 7))
                .thenReturn(null);

        /*
        This tile will always kill a player on an edge.

              |  |
          -+  +--+  +-
           |        |
          -+  +--+  +-
              |  |

        Test whether or not a mostSymmetricPlayer plays it when it's the only choice available

         */

        APlayer mostSymmetricPlayer = new MostSymmetricPlayer("Vyas", Color.BLACK);
        mostSymmetricPlayer.initialize(new ArrayList<>());
        mostSymmetricPlayer.placeToken();
        Tile tile = mostSymmetricPlayer.chooseTile();
        Assert.assertTrue(Game.getGame().getBoard().willKillPlayer(tile, mostSymmetricPlayer));
    }

    @Test
    public void MostSymmetricPlayerMakesSafeMoves() {
        Tile tile1 = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile tile2 = new Tile(0, 5, 1, 4, 2, 7, 3, 6);

        when(tilePileMock.drawFromDeck())
                .thenReturn(tile2)
                .thenReturn(tile1)
                .thenReturn(null);

        /*
            Tile 1 will kill all player on any edge, while Tile 2 never will.

             Tile 1                Tile 2

              |  |                |    |
          -+  +--+  +-          --+----+--
           |        |             |    |
          -+  +--+  +-          --+----+--
              |  |                |    |

            Test to make sure that even when tile 1 is in a MostSymmetricPlayer's hand, it will always choose Tile 2

         */

        APlayer mostSymmetricPlayer = new MostSymmetricPlayer("Vyas", Color.BLACK);
        mostSymmetricPlayer.initialize(new ArrayList<>());
        mostSymmetricPlayer.placeToken();
        Tile tile = mostSymmetricPlayer.chooseTile();
        Assert.assertFalse(Game.getGame().getBoard().willKillPlayer(tile, mostSymmetricPlayer));
        Assert.assertTrue(tile.equals(new Tile(0, 5, 1, 4, 2, 7, 3, 6)));
    }

    @Test
    public void MostSymmetricPlayerMakesMostSymmetricSafeMove(){
        Tile tile1 = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile tile2 = new Tile(0, 5, 1, 4, 2, 7, 3, 6);
        Tile tile3 = new Tile(0, 4, 1, 2, 3, 5, 6, 7);


        when(tilePileMock.drawFromDeck())
                .thenReturn(tile3)
                .thenReturn(tile2)
                .thenReturn(tile1);

        /*
            Tile 1 will kill all player on any edge, while Tile 2 never will.
            Tile 3 will always have a rotation that does not kill the player

             Tile 1                Tile 2           Tile 3
                                                     |   |
              |  |                |    |        -+    \  +--
          -+  +--+  +-          --+----+--       |     \
           |        |             |    |        -+   .--+---
          -+  +--+  +-          --+----+--          /    \
              |  |                |    |            |    |

            Test to make sure that even when tile 1 and tile 3 are in a MostSymmetricPlayer's hand, it will always choose Tile 2

         */

        APlayer mostSymmetricPlayer = new MostSymmetricPlayer("Vyas", Color.BLACK);
        mostSymmetricPlayer.initialize(new ArrayList<>());
        mostSymmetricPlayer.placeToken();
        Tile tile = mostSymmetricPlayer.chooseTile();
        Assert.assertFalse(Game.getGame().getBoard().willKillPlayer(tile, mostSymmetricPlayer));
        Assert.assertTrue(tile.equals(new Tile(0, 5, 1, 4, 2, 7, 3, 6)));
        Assert.assertEquals(tile.calculateSymmetries(), 4);
    }

}
