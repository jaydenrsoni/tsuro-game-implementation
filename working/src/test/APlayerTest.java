//package test;
//
//import main.*;
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
//public class APlayerTest {
//
//    @Mock TilePile tilePileMock;
//
//    Tile tileOne;
//    Tile tileTwo;
//    Tile tileThree;
//
//    @Before
//    public void tileInitialization(){
//        tileOne = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
//        tileTwo = new Tile(0, 2, 1, 3, 4, 6, 5, 7);
//        tileThree = new Tile (0, 5, 1, 4, 2, 7, 3, 6);
//
//        Game.resetGame();
//    }
//
//
//    @Test
//    public void drawFromPileDoesntDrawWithFullHandTest() {
//
//        Game.getGame().setTilePile(tilePileMock);
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(tileOne, tileTwo, tileThree);
//
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.drawFromPile();
//
//        verify(tilePileMock, times(3)).drawFromDeck();
//    }
//
//    @Test
//    public void drawFromPileDrawsWithOpenSpaceTest() {
//        Game.getGame().setTilePile(tilePileMock);
//
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(null)
//                .thenReturn(null, null, null)
//                .thenReturn(tileOne);
//
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.drawFromPile();
//
//        verify(tilePileMock, times(4)).drawFromDeck();
//    }
//
//    @Test
//    public void hasTileReturnsTrueWithEqualTile() {
//        Game.getGame().setTilePile(tilePileMock);
//
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(tileOne, tileTwo, tileThree);
//
//        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//
//        Assert.assertTrue(player.holdsTile(testTile));
//        testTile.rotateClockwise();
//        Assert.assertTrue(player.holdsTile(testTile));
//    }
//
//    @Test
//    public void hasTileReturnsFalseWithoutTile() {
//        Game.getGame().setTilePile(tilePileMock);
//
//        when(tilePileMock.drawFromDeck())
//                .thenReturn(tileOne, tileTwo, tileThree);
//
//        Tile testTile = new Tile(0, 4, 1, 5, 2, 6, 3, 7);
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//
//        Assert.assertFalse(player.holdsTile(testTile));
//    }
//
//    @Test
//    public void initFirstSucceeds() {
//        try {
//            APlayer player = new RandomPlayer("Keith", Color.BLACK);
//            player.initialize(new ArrayList<>());
//        }
//        catch (ContractException e) {
//            throw new AssertionError();
//        }
//    }
//
//    @Test(expected = ContractException.class)
//    public void placeTokenBeforeInitFails() {
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.placeToken();
//    }
//
//    @Test(expected = ContractException.class)
//    public void chooseTileBeforeInitFails() {
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.chooseTile();
//    }
//
//    @Test(expected = ContractException.class)
//    public void chooseTileBeforePlaceTokenFails() {
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.initialize(new ArrayList<>());
//        player.chooseTile();
//    }
//
//    @Test (expected = ContractException.class)
//    public void endGameBeforePlaceTokenFails() {
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.initialize(new ArrayList<>());
//        player.endGame();
//    }
//
//    @Test (expected = ContractException.class)
//    public void endGameBeforeInitFails() {
//        APlayer player = new RandomPlayer("Keith", Color.BLACK);
//        player.endGame();
//    }
//
//    @Test
//    public void correctSequentialContractSucceeds() {
//        try {
//            APlayer player = new RandomPlayer("Keith", Color.BLACK);
//            player.initialize(new ArrayList<>());
//            player.placeToken();
//            player.chooseTile();
//            player.chooseTile();
//            player.endGame();
//        }
//        catch (ContractException e) {
//            throw new AssertionError();
//        }
//    }
//}
