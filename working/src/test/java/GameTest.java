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
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//
//@RunWith(MockitoJUnitRunner.class)
//public class GameTest {
//
//    Game game;
//    Board board;
//
//    @Mock
//    TilePile tilePileMock;
//
//    @Before
//    public void reset() {
//        Game.resetGame();
//        game = Game.getGame();
//        board = game.getBoard();
//        game.setTilePile(tilePileMock);
//    }
//
//
//    @Test
//    public void isLegalMoveIsTrueWithLegalMove(){
//        Tile testTile = new Tile(0, 2, 1, 3, 4, 5, 6, 7);
//
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(testTile)
//                .thenReturn(null)
//                .thenReturn(null);
//
//        BoardSpace space = board.getBoardSpace(0, 0);
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.initialize(new ArrayList<>());
//        player.placeToken(space, 0);
//        game.registerPlayer(player);
//
//        Assert.assertTrue(game.isLegalMove(testTile, player));
//    }
//
//    @Test
//    public void isLegalMoveIsTrueWithNoMoves() {
//        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
//
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(testTile)
//                .thenReturn(null)
//                .thenReturn(null);
//
//        BoardSpace space = board.getBoardSpace(0, 0);
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.initialize(new ArrayList<>());
//        player.placeToken(space, 0);
//        game.registerPlayer(player);
//
//        Assert.assertTrue(game.isLegalMove(testTile, player));
//    }
//
//    @Test
//    public void isLegalMoveFalseWithOtherMove() {
//
//
//        Tile testTileCantMove = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
//        Tile testTileCanMove = new Tile(0, 2, 1, 3, 4, 5, 6, 7);
//
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(testTileCantMove)
//                .thenReturn(testTileCanMove)
//                .thenReturn(null);
//
//        BoardSpace space = board.getBoardSpace(0, 0);
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.initialize(new ArrayList<>());
//        player.placeToken(space, 0);
//        game.registerPlayer(player);
//
//        Assert.assertTrue(game.isLegalMove(testTileCanMove, player));
//        Assert.assertFalse(game.isLegalMove(testTileCantMove, player));
//    }
//
//    @Test
//    public void isLegalMoveIsFalseWithRotationMove() {
//
//
//        Tile testTile = new Tile(0, 1, 2, 3, 4, 6, 5, 7);
//
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(testTile)
//                .thenReturn(null)
//                .thenReturn(null);
//
//        BoardSpace space = board.getBoardSpace(0, 0);
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.initialize(new ArrayList<>());
//        player.placeToken(space, 0);
//        game.registerPlayer(player);
//
//        Assert.assertFalse(game.isLegalMove(testTile, player));
//    }
//
//    @Test
//    public void playMoveEliminatesPlayersThatLose() {
//        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
//
//        when(tilePileMock.isEmpty())
//                .thenReturn(false)
//                .thenReturn(true);
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(testTile)
//                .thenReturn(null);
//
//        BoardSpace spaceOne = board.getBoardSpace(0, 0);
//        BoardSpace spaceTwo = board.getBoardSpace(3, 5);
//        APlayer vyas = new RandomPlayer("Vyas", Color.BLUE);
//        vyas.initialize(new ArrayList<>());
//        vyas.placeToken(spaceOne, 0);
//        APlayer keith = new RandomPlayer("Keith", Color.BLACK);
//        keith.initialize(new ArrayList<>());
//        keith.placeToken(spaceTwo, 2);
//
//        game.registerPlayer(vyas);
//        game.registerPlayer(keith);
//        game.playTurn(testTile, vyas);
//
//        Assert.assertNull(vyas.getTile(0));
//        Assert.assertNull(vyas.getTile(1));
//        Assert.assertNull(vyas.getTile(2));
//        Assert.assertEquals(spaceOne.findToken(vyas.getToken()), -1);
//        Assert.assertNull(vyas.getToken().getBoardSpace());
//    }
//
//    @Test
//    public void dragonTileWithNoneDrawnTest() {
//
//
//        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
//
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(testTile, testTile, null)
//                .thenReturn(testTile, testTile, null)
//                .thenReturn(testTile, testTile, null)
//                .thenReturn(testTile, testTile, null)
//                .thenReturn(null)
//                .thenReturn(testTile);
//        when(tilePileMock.isEmpty())
//                .thenReturn(false, false, true)
//                .thenReturn(false, false, true)
//                .thenReturn(false, false, true)
//                .thenReturn(false, false, true)
//                .thenReturn(false);
//
//        BoardSpace spaceOne = board.getBoardSpace(1, 0);
//        BoardSpace spaceTwo = board.getBoardSpace(5, 5);
//
//        APlayer vyas = new RandomPlayer("Vyas", Color.BLUE);
//        vyas.initialize(new ArrayList<>());
//        vyas.placeToken(spaceOne, 7);
//        APlayer keith =  new RandomPlayer("Keith", Color.BLACK);
//        keith.initialize(new ArrayList<>());
//        keith.placeToken(spaceOne, 6);
//        APlayer robby =  new RandomPlayer("Robby", Color.GREY);
//        robby.initialize(new ArrayList<>());
//        robby.placeToken(spaceTwo, 2);
//        APlayer christos =  new RandomPlayer("Christos", Color.GREEN);
//        christos.initialize(new ArrayList<>());
//        christos.placeToken(spaceTwo, 5);
//
//        game.registerPlayer(vyas);
//        game.registerPlayer(keith);
//        game.registerPlayer(robby);
//        game.registerPlayer(christos);
//
//        Assert.assertEquals(game.playTurn(testTile, vyas).size(), 2);
//        Assert.assertTrue(robby.hasFullHand());
//        Assert.assertTrue(christos.hasFullHand());
//
//        verify(tilePileMock, times(14)).drawFromDeck();
//    }
//
//}
