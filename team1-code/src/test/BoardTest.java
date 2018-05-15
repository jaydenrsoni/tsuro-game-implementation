package test;

import main.*;
import main.Players.APlayer;
import main.Players.RandomPlayer;
import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by vyasalwar on 4/23/18.
 */
public class BoardTest {

    @Test
    public void placeTile() throws Exception {
        Board board = new Board();

        Tile tile = new Tile(0, 2, 1, 3, 4, 5, 6, 7);
        BoardSpace space = board.getBoardSpace(0, 0);
        APlayer player = new RandomPlayer("Vyas", Color.BLUE);

        player.initialize(new ArrayList<>());
        player.placeToken(space, 0);

        board.placeTile(tile, player);
        Assert.assertTrue(board.isOccupied(0, 0));
        Assert.assertFalse(player.getToken().getBoardSpace().hasTile());
        Assert.assertEquals(player.getToken().getTokenSpace(), 7);
    }

    @Test
    public void moveFromEdgeTest() throws Exception{
        /* Setup */
        Board board = new Board();

        BoardSpace upperSpace = board.getBoardSpace(0, 0);
        APlayer vyas = new RandomPlayer("Vyas", Color.BLUE);
        vyas.initialize(new ArrayList<>());
        vyas.placeToken(upperSpace, 0);
        Tile vyasTile = new Tile(0, 4, 1, 5, 2, 3, 6, 7);


        BoardSpace lowerSpace = board.getBoardSpace(1, 0);
        APlayer keith = new RandomPlayer("Keith", Color.BLACK);
        keith.initialize(new ArrayList<>());
        keith.placeToken(lowerSpace, 7);
        Tile keithTile = new Tile(7, 0, 1, 2, 3, 4, 5, 6);


        /* Goals:
            -Have Keith be forced off the board,
            -Have Vyas move two spaces at once

            Start       End
             V              K
            +----+      +----+
            |0,0 |      |0,0 |
            |    |      |    |
            +----+  ->  +----+
           K|1,0 |      |1,0 |V
            |    |      |    |
            +----+      +----+


           Keith's tile        Vyas's Tile
             @(1, 0)            @(0, 0)

              |  |                |  |
             -+  +-            -+  \/   +-
                                |  /\   |
             -+  +-            -+ /  \  +-
              |  |                |  |

         */

        Assert.assertFalse(board.isOccupied(0, 0));
        Assert.assertFalse(board.isOccupied(1, 0));
        Assert.assertFalse(upperSpace.hasTile());
        Assert.assertFalse(lowerSpace.hasTile());


        Set<Token> removedPlayers = board.placeTile(keithTile, keith);


        Assert.assertTrue(lowerSpace.hasTile());
        Assert.assertEquals(lowerSpace.getTokensOnSpace().size(), 0);
        Assert.assertEquals(upperSpace.getTokensOnSpace().size(), 2);
        Assert.assertEquals(upperSpace.findToken(vyas.getToken()), 0);
        Assert.assertEquals(upperSpace.findToken(keith.getToken()), 5);
        Assert.assertEquals(removedPlayers.size(), 0);


        removedPlayers = board.placeTile(vyasTile, vyas);


        Assert.assertTrue(lowerSpace.hasTile());
        Assert.assertTrue(upperSpace.hasTile());
        Assert.assertEquals(lowerSpace.getTokensOnSpace().size(), 0);
        Assert.assertEquals(upperSpace.getTokensOnSpace().size(), 0);
        Assert.assertEquals(vyas.getToken().getBoardSpace().getRow(), 1);
        Assert.assertEquals(vyas.getToken().getBoardSpace().getCol(), 1);
        Assert.assertEquals(vyas.getToken().getTokenSpace(), 7);
        Assert.assertEquals(removedPlayers.size(), 1);
        Assert.assertTrue(removedPlayers.contains(keith.getToken()));
    }

    @Test
    public void RotateTest() throws Exception{
        /* Setup */
        Board board = new Board();
        BoardSpace start = board.getBoardSpace(0, 0);
        APlayer keith = new RandomPlayer("Keith", Color.BLACK);
        keith.initialize(new ArrayList<>());
        keith.placeToken(start, 0);
        Tile tile = new Tile(0, 7, 2, 6, 1, 3, 4, 5);

        /* Goal:
             - If Keith places the tile as is, it will kill him.
             - If Keith rotates the tile before placing it, he will survive

             Original Tile              Rotated Tile
              |    |                     |   |
             -+  .-+----            -+    \  +--
              __/   \___             |     \
                                    -+   .--+---
               +----+                   /    \
               |    |                   |    |

         */

        Assert.assertFalse(start.hasTile());
        Assert.assertFalse(board.isOccupied(0, 0));
        Assert.assertEquals(tile.findMatch(0), 7);
        Assert.assertTrue(board.willKillPlayer(tile, keith));

        tile.rotateClockwise();

        Assert.assertEquals(tile.findMatch(0), 4);
        Assert.assertFalse(board.willKillPlayer(tile, keith));

        Set<Token> killed = board.placeTile(tile, keith);
        Assert.assertTrue(killed.isEmpty());
        Assert.assertEquals(keith.getToken().getBoardSpace().getRow(), 1);
        Assert.assertEquals(keith.getToken().getBoardSpace().getCol(), 0);
        Assert.assertEquals(keith.getToken().getTokenSpace(), 1);
    }

    @Test
    public void multipleTokensKilled() throws Exception{
        /* Setup */
        Board board = new Board();
        BoardSpace start = board.getBoardSpace(0, 0);
        APlayer vyas = new RandomPlayer("Vyas", Color.BLUE);
        vyas.initialize(new ArrayList<>());
        vyas.placeToken(start, 0);
        APlayer keith = new RandomPlayer("Keith", Color.BLACK);
        keith.initialize(new ArrayList<>());
        keith.placeToken(start, 1);
        APlayer robby = new RandomPlayer("Robby", Color.GREY);
        robby.initialize(new ArrayList<>());
        robby.placeToken(start, 6);
        APlayer christos = new RandomPlayer("Christos", Color.GREEN);
        christos.initialize(new ArrayList<>());
        christos.placeToken(start, 7);

        Tile tile = new Tile(0, 7, 1, 6, 2, 3, 4, 5);

        /*
        Goal:
            - A tile placed in a spot that leads multiple players to lose will kill them all
            - Tokens pass through each other

            Tile
              V  K
              |  |
          R --+  |  +--
                 |  |
          C -----+  +--
              +--+
              |  |


         */
        Assert.assertTrue(board.willKillPlayer(tile, vyas));
        Set<Token> losers = board.placeTile(tile, vyas);
        Assert.assertEquals(losers.size(), 4);
    }

    @Test
    public void playerKilledThroughMultipleTiles(){
        /* Setup */
        Board board = new Board();
        BoardSpace start = board.getBoardSpace(0, 0);
        APlayer vyas = new RandomPlayer("Vyas", Color.BLUE);
        vyas.initialize(new ArrayList<>());
        vyas.placeToken(start, 7);
        Tile tileSchema = new Tile(7, 2, 6, 3, 4, 5, 0, 1);
        for (int i = 1; i < 6; i++){
            Tile tile = new Tile(tileSchema);
            board.getBoardSpace(0, i).setTile(tile);
        }

        /*
        Goal:
            - A player can die even when moving across multiple tiles

            Tile            Board
                                 ____________
               | |        start |x x x x x x |  end
               +-+              |            |
          V ----------          |            |
            ----------          |            |
               +-+              |            |
               | |              |____________|
         */

        Set<Token> losers = board.placeTile(new Tile(tileSchema), vyas);
        Assert.assertEquals(losers.size(), 1);
        Assert.assertTrue(losers.contains(vyas.getToken()));
    }
}
