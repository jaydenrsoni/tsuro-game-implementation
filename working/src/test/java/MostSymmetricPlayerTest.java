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
    private TilePile tilePileMock;

    private Board board;

    @Before
    public void gameReset(){
        Game.resetGame();
        Game game = Game.getGame();
        game.setTilePile(tilePileMock);
        board = game.getBoard();
    }


    @Test
    public void MostSymmetricPlayerCanBeInstantiatedTest() {
        IPlayer mostSymmetricPlayer = new MostSymmetricPlayer("Vyas");
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

        IPlayer mostSymmetricPlayer = new MostSymmetricPlayer("Vyas");
        SPlayer splayer = new SPlayer(mostSymmetricPlayer, tilePileMock);
        splayer.initializeSPlayer(Color.BLUE, new ArrayList<>());
        splayer.placeToken(board);
        Tile tile = splayer.chooseTile(board);
        Assert.assertTrue(board.willKillPlayer(tile, splayer));
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

        IPlayer mostSymmetricPlayer = new MostSymmetricPlayer("Vyas");
        SPlayer splayer = new SPlayer(mostSymmetricPlayer, tilePileMock);
        splayer.initializeSPlayer(Color.BLUE, new ArrayList<>());
        splayer.placeToken(board);
        Tile tile = splayer.chooseTile(board);
        Assert.assertFalse(board.willKillPlayer(tile, splayer));
        Assert.assertEquals(tile, new Tile(0, 5, 1, 4, 2, 7, 3, 6));
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

        IPlayer mostSymmetricPlayer = new MostSymmetricPlayer("Vyas");
        SPlayer splayer = new SPlayer(mostSymmetricPlayer, tilePileMock);
        splayer.initializeSPlayer(Color.BLUE, new ArrayList<>());
        splayer.placeToken(board);
        Tile tile = splayer.chooseTile(board);
        Assert.assertFalse(board.willKillPlayer(tile, splayer));
        Assert.assertEquals(tile, new Tile(0, 5, 1, 4, 2, 7, 3, 6));
        Assert.assertEquals(tile.calculateSymmetries(), 4);
    }

}
