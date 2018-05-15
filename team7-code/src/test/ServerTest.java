import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Created by Michael on 4/22/2018.
 */
public class ServerTest {

    @Test
    void legalPlayTest() throws CloneNotSupportedException{
        // moving from the edge
        Server server = new Server();
        Board testBoard = new Board();
        PTile testP = new PTile(new int[][] {{5,6},{3,4},{2,7},{0,1}});
        SPlayer s = new SPlayer(new int[]{0,0,5}, Colors.SIENNA);
        s.addTileToHand(testP);
        // test if the player can kill himself when he has other options
        assertFalse(server.legalPlay(s, testBoard, testP));
        testP.rotate();
        testP.rotate();
        testP.rotate();
        // test if the player can play a safe tile
        assertTrue(server.legalPlay(s, testBoard, testP));
        testP.rotate();
        PTile testP1 = new PTile(new int[][] {{5,0},{2,4},{3,7},{6,1}});
        // test if the player can play a tile that doesn't belong to him
        assertFalse(server.legalPlay(s, testBoard, testP1));
        PTile testP2 = new PTile(new int[][] {{0,7},{1,2},{3,4},{5,6}});
        // test if the player can kill himself when it's the only option
        s.addTileToHand(testP2);
        assertFalse(server.legalPlay(s, testBoard, testP2));
        s.removeTileFromHand(testP);
        assertTrue(server.legalPlay(s, testBoard, testP2));
    }

    @Test
    void complexMovingTests() throws CloneNotSupportedException{
        Server server = new Server();
        Board testBoard = new Board();
        PTile testP1 = new PTile(new int[][] {{5,0},{3,4},{6,7},{2,1}});
        PTile testP2 = new PTile(new int[][] {{0,7},{1,2},{3,4},{5,6}});
        PTile testP3 = new PTile(new int[][] {{6,5},{7,1},{0,3},{2,4}});
        PTile testP4 = new PTile(new int[][] {{5,0},{1,4},{2,7},{6,3}});
        PTile testP5 = new PTile(new int[][] {{5,0},{3,4},{2,7},{6,1}}); // s1 draws from here
        PTile testP6 = new PTile(new int[][] {{5,0},{3,4},{2,7},{6,1}}); // s2 draws from here
        PTile testP7 = new PTile(new int[][] {{5,0},{3,4},{2,7},{6,1}}); // s3 draws from here
        DTile testD = new DTile();
        PTile bP1 = new PTile(new int[][] {{0,1},{2,7},{3,4},{5,6}});
        PTile bP2 = new PTile(new int[][] {{0,1},{2,7},{3,4},{5,6}});
        PTile bP3 = new PTile(new int[][] {{0,1},{2,7},{3,4},{5,6}});
        PTile bP4 = new PTile(new int[][] {{0,1},{2,7},{3,4},{5,6}});
        SPlayer s1 = new SPlayer(new int[]{0,0,5}, Colors.SIENNA);
        SPlayer s2 = new SPlayer(new int[]{0,1,7}, Colors.HOTPINK);
        SPlayer s3 = new SPlayer(new int[]{5,1,2}, Colors.GREEN);
        SPlayer s4 = new SPlayer(new int[]{0,0,7}, Colors.PURPLE);
        s1.addTileToHand(testP1);
        s1.addTileToHand(testP2);
        s2.addTileToHand(testP3);
        s3.addTileToHand(testP4);
        ArrayList<ATile> pile = new ArrayList<ATile> ();
        pile.add(testP5);
        pile.add(testP6);
        pile.add(testP7);
        pile.add(testD);
        ArrayList<SPlayer> ePlayers = new ArrayList<SPlayer>();
        ArrayList<SPlayer> aPlayers = new ArrayList<SPlayer>();
        aPlayers.add(s1);
        aPlayers.add(s2);
        aPlayers.add(s3);
        aPlayers.add(s4);

        Turn t = new Turn(pile, aPlayers, ePlayers, testBoard);
        assertTrue(server.legalPlay(aPlayers.get(0), testBoard, testP1));
        t = server.playATurn(t, testP1); // p1 makes move from edge, eliminate p4 before their first turn
        assertEquals(t.getActivePlayers().get(0), s2);
        assertEquals(t.getActivePlayers().get(1), s3);
        assertEquals(t.getActivePlayers().get(2), s1);
        assertEquals(t.getEliminatedPlayers().get(0), s4);
        assertEquals(t.getEliminatedPlayers().size(), 1);
        assertFalse(t.isDone());
        assertTrue(Arrays.equals(s4.getLocation(), new int[]{-1,0,3}));
        assertTrue(Arrays.equals(s1.getLocation(), new int[]{0,1,5}));
        testBoard.addTile(bP1, new int[]{1, 1});
        testBoard.addTile(bP2, new int[]{2, 1});
        testBoard.addTile(bP3, new int[]{3, 1});
        testBoard.addTile(bP4, new int[]{4, 1});
        testP3.rotate();
        assertTrue(server.legalPlay(s2, testBoard, testP3));
        t = server.playATurn(t, testP3); // p2 places rotated tile, move multiple players and multiple tiles
        assertFalse(t.isDone());
        assertTrue(Arrays.equals(s2.getLocation(), new int[]{0,2,5}));
        assertTrue(Arrays.equals(s1.getLocation(), new int[]{5,1,7}));
        t = server.playATurn(t, testP4); // p3 eliminates s1 and self, check winners/done state
        assertTrue(s3.getHand().isEmpty()); // added to ensure self-eliminated players cannot draw tiles
        assertTrue(t.isDone());
        assertEquals(t.getActivePlayers().size(), 0);
        assertEquals(t.getWinners().size(), 1);
        assertEquals(t.getWinners().get(0), s2);
    }

    @Test
    void dragonTests() throws CloneNotSupportedException{
        Server server = new Server();
        Board testBoard = new Board();
        PTile testP1 = new PTile(new int[][] {{5,0},{3,4},{6,7},{2,1}});
        PTile testP2 = new PTile(new int[][] {{0,4},{1,7},{3,2},{5,6}});
        PTile testP3 = new PTile(new int[][] {{0,7},{1,5},{2,3},{4,6}});
        PTile testP4 = new PTile(new int[][] {{5,0},{1,4},{2,7},{6,3}});
        PTile testP5 = new PTile(new int[][] {{5,0},{1,4},{2,7},{6,3}});
        PTile testP6 = new PTile(new int[][] {{5,0},{3,4},{2,7},{6,1}});
        PTile testP7 = new PTile(new int[][] {{0,7},{1,2},{3,4},{5,6}});  // s1 draws after turn 1, tile of elimination
        DTile testD = new DTile(); // s2 draws after turn 2
        SPlayer s1 = new SPlayer(new int[]{0,0,5}, Colors.SIENNA);
        SPlayer s2 = new SPlayer(new int[]{0,1,7}, Colors.HOTPINK);
        SPlayer s3 = new SPlayer(new int[]{5,1,2}, Colors.GREEN);
        SPlayer s4 = new SPlayer(new int[]{0,0,7}, Colors.PURPLE);
        s1.addTileToHand(testP1);
        s1.addTileToHand(testP2);
        s2.addTileToHand(testP3);
        s2.addTileToHand(testP4);
        s3.addTileToHand(testP5);
        s3.addTileToHand(testP6);
        ArrayList<ATile> pile = new ArrayList<ATile> ();
        pile.add(testP7);
        pile.add(testD);
        ArrayList<SPlayer> ePlayers = new ArrayList<SPlayer>();
        ArrayList<SPlayer> aPlayers = new ArrayList<SPlayer>();
        aPlayers.add(s1);
        aPlayers.add(s2);
        aPlayers.add(s3);
        aPlayers.add(s4);

        Turn t = new Turn(pile, aPlayers, ePlayers, testBoard);
        assertTrue(server.legalPlay(aPlayers.get(0), testBoard, testP1));
        assertTrue(t.dragonPlayer() == null); // tests that no one has dragon tile before moving
        t = server.playATurn(t, testP1); // make move from edge, eliminate p4 before their first turn
        assertTrue(t.dragonPlayer() == null); // tests that no one has dragon tile after moving
        assertEquals(t.getActivePlayers().get(0), s2);
        assertEquals(t.getActivePlayers().get(1), s3);
        assertEquals(t.getActivePlayers().get(2), s1);
        assertEquals(t.getEliminatedPlayers().get(0), s4);
        assertEquals(t.getEliminatedPlayers().size(), 1);
        assertTrue(Arrays.equals(s4.getLocation(), new int[]{-1,0,3}));
        assertTrue(Arrays.equals(s1.getLocation(), new int[]{0,1,5}));

        // test dragon tile drawn by player 2
        t = server.playATurn(t, testP3);
        assertEquals(t.dragonPlayer(), s2);

        int nTilesS1 = s1.getHand().size();
        int nTilesS2 = s2.getHand().size();
        int nTilesS3 = s3.getHand().size();

        t = server.playATurn(t, testP5);

        assertEquals(s1.getHand().size(), nTilesS1);
        assertEquals(s2.getHand().size(), nTilesS2);
        assertEquals(s3.getHand().size(), nTilesS3 - 1);

        // test elimination of s2 with dragon tile in play (in hand of s3)
        nTilesS1 = s1.getHand().size();
        nTilesS3 = s3.getHand().size();

        t = server.playATurn(t, testP2);

        assertTrue(t.getEliminatedPlayers().contains(s2));
        assertEquals(t.dragonPlayer(), s1);

        assertEquals(s1.getHand().size(), nTilesS1);
        assertEquals(s2.getHand().size(), 0);
        assertEquals(s3.getHand().size(), nTilesS3 + 1);

        t = server.playATurn(t, (PTile)s3.getHand().get(0));

        // test dragon player s1 self-elimination
        nTilesS3 = s3.getHand().size();
        t = server.playATurn(t, testP7);
        // assertEquals(t.dragonPlayer(), s3); removed because no active players after game ends
        assertEquals(s1.getHand().size(), 0);
        assertEquals(s3.getHand().size(), nTilesS3 + 1);
        assertTrue(t.isDone());
        assertEquals(t.getWinners().get(0), s3);

    }

    @Test
    public void complexTest2() throws CloneNotSupportedException{
        // test dragon player kills someone else
        Server server = new Server();
        Board testBoard = new Board();
        PTile testP1 = new PTile(new int[][] {{5,0},{3,4},{6,7},{2,1}});
        PTile testP2 = new PTile(new int[][] {{0,5},{1,4},{2,7},{3,6}});
        PTile testP3 = new PTile(new int[][] {{0,7},{1,5},{2,3},{4,6}});
        PTile testP4 = new PTile(new int[][] {{0,5},{1,2},{3,6},{4,7}});
        PTile testP5 = new PTile(new int[][] {{5,0},{1,4},{2,7},{6,3}});
        PTile testP6 = new PTile(new int[][] {{5,0},{3,4},{2,7},{6,1}});
        PTile testP7 = new PTile(new int[][] {{0,7},{1,2},{3,4},{5,6}});  // s1 draws after turn 1, tile of elimination
        DTile testD = new DTile(); // s2 draws after turn 2
        SPlayer s1 = new SPlayer(new int[]{0,0,5}, Colors.SIENNA);
        SPlayer s2 = new SPlayer(new int[]{0,1,7}, Colors.HOTPINK);
        SPlayer s3 = new SPlayer(new int[]{5,1,2}, Colors.GREEN);
        SPlayer s4 = new SPlayer(new int[]{0,0,7}, Colors.PURPLE);
        s1.addTileToHand(testP1);
        s1.addTileToHand(testP2);
        s2.addTileToHand(testP3);
        s2.addTileToHand(testP4);
        s3.addTileToHand(testP5);
        s3.addTileToHand(testP6);
        ArrayList<ATile> pile = new ArrayList<ATile> ();
        pile.add(testP7); // drew by s1
        pile.add(testD); // drew by s2
        ArrayList<SPlayer> ePlayers = new ArrayList<SPlayer>();
        ArrayList<SPlayer> aPlayers = new ArrayList<SPlayer>();
        aPlayers.add(s1);
        aPlayers.add(s2);
        aPlayers.add(s3);
        aPlayers.add(s4);

        Turn t = new Turn(pile, aPlayers, ePlayers, testBoard);
        t = server.playATurn(t, testP1); // make move from edge, eliminate p4 before their first turn
        // dragon tile drawn by player 2
        t = server.playATurn(t, testP3);
        // let player 3 play a random turn (safe)
        t = server.playATurn(t, (PTile)s3.getHand().get(0));
        // let player 1 play an ineffective turn
        t = server.playATurn(t, testP2);
        // dragon player, player 2, eliminates player 1

        int nTilesS1 = s1.getHand().size();
        int nTilesS2 = s2.getHand().size();
        int nTilesS3 = s3.getHand().size();

        t = server.playATurn(t, testP4);

        assertEquals(s1.getHand().size(), 0);
        // plays a tile and loses dragon tile but draws a tile from s1
        assertEquals(s2.getHand().size(), nTilesS2 - 1);
        // draws a dragon tile
        assertEquals(s3.getHand().size(), nTilesS3 + 1);
        assertEquals(t.dragonPlayer(), s3);
    }

    @Test
    public void testEmptyException() throws CloneNotSupportedException{
        Server s = new Server();
        Turn t = new Turn(new ArrayList<ATile>(), new ArrayList<SPlayer>(), new ArrayList<SPlayer>(), new Board());
        PTile testP = new PTile(new int[][] {{5,0},{3,4},{6,7},{2,1}});
        try{
        t = s.playATurn(t, testP);
        fail("Exception not thrown when playATurn is given no active players");}
        catch(RuntimeException ignore){}
    }

    @Test
    public void testDragonPlayerNotFoundException() throws CloneNotSupportedException{
        Server s = new Server();
        SPlayer s1 = new SPlayer(new int[]{0,0,5}, Colors.SIENNA);
        SPlayer s2 = new SPlayer(new int[]{1,0,5}, Colors.BLUE);
        ArrayList<SPlayer> aPlayers = new ArrayList<SPlayer>();
        aPlayers.add(s1);
        aPlayers.add(s2);
        Turn t = new Turn(new ArrayList<ATile>(), aPlayers, new ArrayList<SPlayer>(), new Board());
        PTile testP = new PTile(new int[][] {{5,0},{3,4},{6,7},{2,1}});
        try{
            t = s.playATurn(t, testP);
            fail("Exception not thrown when dragon player is not found and pile is empty");
        }
        catch(RuntimeException ignore){}
    }

    @Test
    public void testMultipleWinners() throws CloneNotSupportedException{
        Server s = new Server();
        SPlayer s1 = new SPlayer(new int[]{0,1,7}, Colors.SIENNA);
        SPlayer s2 = new SPlayer(new int[]{0,0,4}, Colors.BLUE);
        SPlayer s3 = new SPlayer(new int[]{0,0,5}, Colors.GREEN);
        ArrayList<SPlayer> aPlayers = new ArrayList<SPlayer>();
        aPlayers.add(s1);
        aPlayers.add(s2);
        aPlayers.add(s3);
        PTile testP1 = new PTile(new int[][] {{7,6},{0,1},{2,3},{4,5}});
        PTile testP2 = new PTile(new int[][] {{7,6},{0,1},{2,3},{4,5}});
        PTile testP3 = new PTile(new int[][] {{7,6},{0,1},{2,3},{4,5}});
        PTile testP4 = new PTile(new int[][] {{7,6},{0,1},{2,3},{4,5}});
        PTile testP5 = new PTile(new int[][] {{7,6},{0,1},{2,3},{4,5}});
        s1.getHand().add(testP1);
        s2.getHand().add(testP2);
        ArrayList<ATile> pile = new ArrayList<ATile>();
        pile.add(testP3);
        pile.add(testP4);
        pile.add(testP5);
        Board b = new Board();
        Turn t = new Turn(pile, aPlayers, new ArrayList<SPlayer>(), b);
        assertTrue(s.legalPlay(s1, b, testP1));
        t = s.playATurn(t, testP1);
        assertTrue(s.legalPlay(s2, b, testP2));
        t = s.playATurn(t, testP2);
        assertTrue(t.isDone());
        assertEquals(t.getWinners().size(), 2);
        assertTrue(t.getWinners().contains(s2));
        assertTrue(t.getWinners().contains(s3));
    }

    @Test
    public void simulateBasicTournament() throws CloneNotSupportedException{
        int[] winnerStats = new int[3];
        for (int k = 0; k < 100000; k++) {
            Server s = new Server();
            RandomPlayer rPlayer = new RandomPlayer("Random Player");
            LeastSymmetricPlayer lsPlayer = new LeastSymmetricPlayer("Least Symmetric Player");
            MostSymmetricPlayer msPlayer = new MostSymmetricPlayer("Most Symmetric Player");
            ArrayList<Colors> availableColors = new ArrayList<Colors>(Arrays.asList(Colors.values()));
            ArrayList<Colors> allColors = new ArrayList<Colors>();

            Colors rColor = rPlayer.chooseColor(availableColors);
            rPlayer.initialize(rColor, allColors);
            Colors lsColor = lsPlayer.chooseColor(availableColors);
            lsPlayer.initialize(lsColor, allColors);
            Colors msColor = msPlayer.chooseColor(availableColors);
            msPlayer.initialize(msColor, allColors);

            Board b = new Board();

            int[] rLocation = rPlayer.placePawn(b);
            int[] lsLocation = lsPlayer.placePawn(b);
            int[] msLocation = msPlayer.placePawn(b);
            b.setPlayerLocation(rColor, rLocation);
            b.setPlayerLocation(lsColor, lsLocation);
            b.setPlayerLocation(msColor, msLocation);

            SPlayer rSPlayer = new SPlayer(rLocation, rPlayer);
            SPlayer lsSPlayer = new SPlayer(lsLocation, lsPlayer);
            SPlayer msSPlayer = new SPlayer(msLocation, msPlayer);

            ArrayList<SPlayer> initialPlayers = new ArrayList<SPlayer>();
            initialPlayers.add(rSPlayer);
            initialPlayers.add(lsSPlayer);
            initialPlayers.add(msSPlayer);

            // everyone gets 3 tiles
            ArrayList<ATile> deck = s.generateInitialDeck();
            for (SPlayer sp : initialPlayers) {
                for (int i = 0; i < 3; i++) {
                    sp.drawTile(deck);
                }
            }

            // start game
            Turn t = new Turn(deck, initialPlayers, new ArrayList<SPlayer>(), new Board());

            while (!t.isDone()) {
                SPlayer currPlayer = initialPlayers.get(0);
                PTile p = currPlayer.player.playTurn(b, currPlayer.getHand(), deck.size());
                s.playATurn(t, p);
            }

            ArrayList<Colors> winnerColors = new ArrayList<Colors>();

            for (SPlayer sp : t.getWinners()) {
                winnerColors.add(sp.getColor());
                switch(sp.player.getName()){
                    case "Random Player":
                        winnerStats[0] += 1;
                        break;
                    case "Most Symmetric Player":
                        winnerStats[2] += 1;
                        break;
                    case "Least Symmetric Player":
                        winnerStats[1] += 1;
                        break;
                }
            }

            for (SPlayer initialPlayer : initialPlayers) {
                initialPlayer.player.endGame(b, winnerColors);
            }
        }
        System.out.println(Arrays.toString(winnerStats));
    }

    @Test
    void runTournamentTest() throws CloneNotSupportedException {
        Server s = new Server();
        ArrayList<MPlayer> players = new ArrayList<>();
        s.runTournament(players);
    }
}