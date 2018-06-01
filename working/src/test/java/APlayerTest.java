import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class APlayerTest {

    @Mock
    private TilePile tilePileMock;
    @Mock
    private Board boardMock;
    @Mock
    private IPlayer iplayerMock;

    private Tile tileOne;
    private Tile tileTwo;
    private Tile tileThree;

    @Before
    public void tileInitialization(){
        tileOne = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        tileTwo = new Tile(0, 2, 1, 3, 4, 6, 5, 7);
        tileThree = new Tile (0, 5, 1, 4, 2, 7, 3, 6);

        Game.resetGame();
    }


    @Test
    public void drawFromPileDoesntDrawWithFullHandTest() {

        Game.getGame().setTilePile(tilePileMock);
        when(tilePileMock.drawFromDeck())
                .thenReturn(tileOne, tileTwo, tileThree);

        SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
        splayer.drawFromPile();

        verify(tilePileMock, times(3)).drawFromDeck();
    }

    @Test
    public void drawFromPileDrawsWithOpenSpaceTest() {
        Game.getGame().setTilePile(tilePileMock);

        when(tilePileMock.drawFromDeck())
                .thenReturn(null)
                .thenReturn(null, null, null)
                .thenReturn(tileOne);

        SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
        splayer.drawFromPile();

        verify(tilePileMock, times(4)).drawFromDeck();
    }

    @Test
    public void hasTileReturnsTrueWithEqualTile() {
        Game.getGame().setTilePile(tilePileMock);

        when(tilePileMock.drawFromDeck())
                .thenReturn(tileOne, tileTwo, tileThree);

        Tile testTile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
        splayer.drawFromPile();

        Assert.assertTrue(splayer.holdsTile(testTile));
        testTile.rotateClockwise();
        Assert.assertTrue(splayer.holdsTile(testTile));
    }

    @Test
    public void hasTileReturnsFalseWithoutTile() {
        Game.getGame().setTilePile(tilePileMock);

        when(tilePileMock.drawFromDeck())
                .thenReturn(tileOne, tileTwo, tileThree);

        Tile testTile = new Tile(0, 4, 1, 5, 2, 6, 3, 7);
        SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
        splayer.drawFromPile();

        Assert.assertFalse(splayer.holdsTile(testTile));
    }

    @Test
    public void initFirstSucceeds() {
        try {
            SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
            splayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
        }
        catch (ContractException e) {
            throw new AssertionError();
        }
    }

    @Test(expected = ContractException.class)
    public void placeTokenBeforeInitFails() {
        when(iplayerMock.placePawn(boardMock))
                .thenReturn(new Pair<BoardSpace, Integer>(new BoardSpace(0, 0), 0));

        SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
        splayer.placeToken(boardMock);
    }

    @Test(expected = ContractException.class)
    public void chooseTileBeforeInitFails() {
        SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
        splayer.chooseTile(boardMock);
    }

    @Test(expected = ContractException.class)
    public void chooseTileBeforePlaceTokenFails() {
        SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
        splayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
        splayer.chooseTile(boardMock);
    }

    @Test (expected = ContractException.class)
    public void endGameBeforePlaceTokenFails() {
        SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
        splayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
        splayer.endGame(boardMock, new HashSet<>());
    }

    @Test (expected = ContractException.class)
    public void endGameBeforeInitFails() {
        SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
        splayer.endGame(boardMock, new HashSet<>());
    }

    @Test
    public void correctSequentialContractSucceeds() {
        when(iplayerMock.placePawn(boardMock))
                .thenReturn(new Pair<BoardSpace, Integer>(new BoardSpace(0, 0), 0));

        try {
            SPlayer splayer = new SPlayer(iplayerMock, tilePileMock);
            splayer.initializeSPlayer(Color.GREEN, new ArrayList<>());
            splayer.placeToken(boardMock);
            splayer.chooseTile(boardMock);
            splayer.chooseTile(boardMock);
            splayer.endGame(boardMock, new HashSet<>());
        }
        catch (ContractException e) {
            throw new AssertionError();
        }
    }
}
