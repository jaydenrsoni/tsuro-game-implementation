//package test;
//
//import main.*;
//import main.Players.APlayer;
//import main.Players.RandomPlayer;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.util.ArrayList;
//
//import static org.mockito.Mockito.*;
//
//@RunWith(MockitoJUnitRunner.class)
//public class RandomPlayerTest {
//
//    @Mock
//    TilePile tilePileMock;
//
//    @Before
//    public void gameReset(){
//        Game.resetGame();
//        Game.getGame().setTilePile(tilePileMock);
//    }
//
//    @Test
//    public void randomPlayerCanBeInstantiatedTest() {
//        APlayer randomPlayer = new RandomPlayer("Vyas", Color.BLACK, 0);
//    }
//
//    @Test
//    public void RandomPlayerMakesUnsafeMovesOnlyWhenNecessaryTest(){
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(new Tile(0, 1, 2, 3, 4, 5, 6, 7))
//                .thenReturn(null);
//
//        /*
//        This tile will always kill a player on an edge.
//
//              |  |
//          -+  +--+  +-
//           |        |
//          -+  +--+  +-
//              |  |
//
//        Test whether or not a randomPlayer plays it when it's the only choice available
//
//         */
//
//        APlayer randomPlayer = new RandomPlayer("Vyas", Color.BLACK, 0);
//        randomPlayer.initialize(new ArrayList<>());
//        randomPlayer.placeToken();
//        Tile tile = randomPlayer.chooseTile();
//        Assert.assertTrue(Game.getGame().getBoard().willKillPlayer(tile, randomPlayer));
//    }
//
//    @Test
//    public void RandomPlayerMakesSafeMoves() {
//
//        Tile tile1 = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
//        Tile tile2 = new Tile(0, 5, 1, 4, 2, 7, 3, 6);
//
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(tile2)
//                .thenReturn(tile1)
//                .thenReturn(null);
//
//        /*
//            Tile 1 will kill all player on any edge, while Tile 2 never will.
//
//             Tile 1                Tile 2
//
//              |  |                |    |
//          -+  +--+  +-          --+----+--
//           |        |             |    |
//          -+  +--+  +-          --+----+--
//              |  |                |    |
//
//            Test to make sure that even when tile 1 is in a RandomPlayer's hand, it will always choose Tile 2
//
//         */
//
//        APlayer randomPlayer = new RandomPlayer("Vyas", Color.BLACK, 0);
//        randomPlayer.initialize(new ArrayList<>());
//        randomPlayer.placeToken();
//        Tile tile = randomPlayer.chooseTile();
//        Assert.assertFalse(Game.getGame().getBoard().willKillPlayer(tile, randomPlayer));
//        Assert.assertTrue(tile.equals(new Tile(0, 5, 1, 4, 2, 7, 3, 6)));
//    }
//
//}
