import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GameTest {

    private Game game;
    private Board board;

    @Mock
    private TilePile tilePileMock;

    @Before
    public void reset() {
        Game.resetGame();
        game = Game.getGame();
        board = game.getBoard();
        game.setTilePile(tilePileMock);
    }


    @Test
    public void isLegalMoveIsTrueWithLegalMove(){
        Tile testTile = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        IPlayer iplayer = new RandomPlayer("Keith");
        SPlayer splayer = game.registerPlayer(iplayer);
        splayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
        splayer.placeToken(space, 0);

        Assert.assertTrue(game.isLegalMove(testTile, splayer));
    }

    @Test
    public void isLegalMoveIsTrueWithNoMoves() {
        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        IPlayer iplayer = new RandomPlayer("Keith");
        SPlayer splayer = game.registerPlayer(iplayer);
        splayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
        splayer.placeToken(space, 0);

        Assert.assertTrue(game.isLegalMove(testTile, splayer));
    }

    @Test
    public void isLegalMoveFalseWithOtherMove() {

        Tile testTileCantMove = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        Tile testTileCanMove = new Tile(0, 2, 1, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTileCantMove)
                .thenReturn(testTileCanMove)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        IPlayer iplayer = new RandomPlayer("Keith");
        SPlayer splayer = game.registerPlayer(iplayer);
        splayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
        splayer.placeToken(space, 0);

        Assert.assertTrue(game.isLegalMove(testTileCanMove, splayer));
        Assert.assertFalse(game.isLegalMove(testTileCantMove, splayer));
    }

    @Test
    public void isLegalMoveIsFalseWithRotationMove() {


        Tile testTile = new Tile(0, 1, 2, 3, 4, 6, 5, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null)
                .thenReturn(null);

        BoardSpace space = board.getBoardSpace(0, 0);
        IPlayer iplayer = new RandomPlayer("Keith");
        SPlayer splayer = game.registerPlayer(iplayer);
        splayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
        splayer.placeToken(space, 0);

        Assert.assertFalse(game.isLegalMove(testTile, splayer));
    }

    @Test
    public void playMoveEliminatesPlayersThatLose() {
        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);

        when(tilePileMock.isEmpty())
                .thenReturn(false)
                .thenReturn(true);
        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile)
                .thenReturn(null);

        BoardSpace spaceOne = board.getBoardSpace(0, 0);
        BoardSpace spaceTwo = board.getBoardSpace(3, 5);
        IPlayer vyasIplayer = new RandomPlayer("Vyas");
        SPlayer vyasSplayer = game.registerPlayer(vyasIplayer);
        vyasSplayer.initializeSPlayer(Color.BLUE, new ArrayList<>());
        vyasSplayer.placeToken(spaceOne, 0);
        IPlayer keithIplayer = new RandomPlayer("Vyas");
        SPlayer keithSplayer = game.registerPlayer(keithIplayer);
        keithSplayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
        keithSplayer.placeToken(spaceTwo, 2);

        game.playTurn(testTile, vyasSplayer);

        Token vyasToken = vyasSplayer.getToken();

        Assert.assertNull(vyasSplayer.getTile(0));
        Assert.assertNull(vyasSplayer.getTile(1));
        Assert.assertNull(vyasSplayer.getTile(2));
        Assert.assertTrue(Board.isOnEdge(vyasToken));
        Assert.assertEquals(vyasSplayer.getToken().getBoardSpace(), spaceOne);
    }

    @Test
    public void dragonTileWithNoneDrawnTest() {
        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);

        when(tilePileMock.drawFromDeck())
                .thenReturn(testTile);
        when(tilePileMock.isEmpty())
                .thenReturn(false, false, true, true)
                .thenReturn(false, false, true)
                .thenReturn(false, false, true)
                .thenReturn(false, false, true)
                .thenReturn(false);

        BoardSpace spaceOne = board.getBoardSpace(1, 0);
        BoardSpace spaceTwo = board.getBoardSpace(5, 5);

        IPlayer vyasIplayer = new RandomPlayer("Vyas");
        SPlayer vyasSplayer = game.registerPlayer(vyasIplayer);
        vyasSplayer.initializeSPlayer(Color.BLUE, new ArrayList<>());
        vyasSplayer.placeToken(spaceOne, 7);
        IPlayer keithIplayer =  new RandomPlayer("Keith");
        SPlayer keithSplayer = game.registerPlayer(keithIplayer);
        keithSplayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
        keithSplayer.placeToken(spaceOne, 6);
        IPlayer robbyIplayer =  new RandomPlayer("Robby");
        SPlayer robbySplayer = game.registerPlayer(robbyIplayer);
        robbySplayer.initializeSPlayer(Color.RED, new ArrayList<>());
        robbySplayer.placeToken(spaceTwo, 2);
        IPlayer christosIplayer =  new RandomPlayer("Christos");
        SPlayer christosSplayer = game.registerPlayer(christosIplayer);
        christosSplayer.initializeSPlayer(Color.ORANGE, new ArrayList<>());
        christosSplayer.placeToken(spaceTwo, 5);


        Assert.assertEquals(game.playTurn(testTile, vyasSplayer).size(), 2);
        Assert.assertTrue(robbySplayer.hasFullHand());
        Assert.assertTrue(christosSplayer.hasFullHand());

        verify(tilePileMock, times(17)).isEmpty();
        verify(tilePileMock, times(10)).drawFromDeck();

    }

}
