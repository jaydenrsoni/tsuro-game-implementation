package test;


import main.*;
import org.junit.Test;
import org.junit.Assert;

/**
 * Created by vyasalwar on 4/23/18.
 */
public class BoardSpaceTest {
    @Test
    public void advanceTokens() throws Exception {
        BoardSpace boardSpace = new BoardSpace(0, 0);
        Tile tile = new Tile(0, 1, 2, 3, 4, 5, 6, 7);
        boardSpace.setTile(tile);
        Token token1 = new Token(boardSpace, 0, null);
        Token token2 = new Token(boardSpace, 3, null);
        boardSpace.advanceTokens();

        Assert.assertTrue(token1.getTokenSpace() == 1);
        Assert.assertEquals(token2.getTokenSpace(), 2);

    }

    @Test
    public void findToken() throws Exception {
        BoardSpace boardSpace = new BoardSpace(0, 0);
        Token token = new Token(boardSpace, 0, null);
        Assert.assertEquals(boardSpace.findToken(token), 0);
    }

    @Test
    public void removeToken() throws Exception {
        BoardSpace boardSpace = new BoardSpace(0, 0);
        Token token = new Token(boardSpace, 3, null);
        Assert.assertEquals(boardSpace.findToken(token), 3);

        boardSpace.removeToken(token);
        Assert.assertEquals(boardSpace.findToken(token), -1);
    }

    @Test
    public void getTokensOnSpace() throws Exception {
        BoardSpace boardSpace = new BoardSpace(0, 0);
        for (int i = 0; i < 8; i++)
            boardSpace.addToken(new Token(boardSpace, 0, null), i);
        Assert.assertEquals(boardSpace.getTokensOnSpace().size(), 8);
    }

}