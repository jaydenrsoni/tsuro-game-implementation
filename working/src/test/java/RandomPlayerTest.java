import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RandomPlayerTest {

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
    public void randomPlayerCanBeInstantiatedTest() {
        IPlayer randomPlayer = new RandomPlayer("Vyas", 0);
    }

    @Test
    public void RandomPlayerMakesUnsafeMovesOnlyWhenNecessaryTest(){
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

        Test whether or not a randomPlayer plays it when it's the only choice available

         */

        IPlayer randomPlayer = new RandomPlayer("Vyas", 0);
        SPlayer splayer = new SPlayer(randomPlayer, tilePileMock);
        splayer.initializeSPlayer(Color.BLUE, new ArrayList<>());
        splayer.placeToken(board);
        Tile tile = splayer.chooseTile(board);
        Assert.assertTrue(board.willKillPlayer(tile, splayer));
    }

    @Test
    public void RandomPlayerMakesSafeMoves() {

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

            Test to make sure that even when tile 1 is in a RandomPlayer's hand, it will always choose Tile 2

         */

        IPlayer randomPlayer = new RandomPlayer("Vyas", 0);
        SPlayer splayer = new SPlayer(randomPlayer, tilePileMock);
        splayer.initializeSPlayer(Color.BLUE, new ArrayList<>());
        splayer.placeToken(board);
        Tile tile = splayer.chooseTile(board);
        Assert.assertFalse(board.willKillPlayer(tile, splayer));
        Assert.assertEquals(tile, new Tile(0, 5, 1, 4, 2, 7, 3, 6));
    }

}
