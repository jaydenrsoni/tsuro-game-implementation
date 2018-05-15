import org.omg.SendingContext.RunTime;

import java.util.*;

/**
 * Created by Michael on 4/15/2018.
 */

/**
 * Game Invariants:
 * Player location tile is defined as the next blank space they would play a tile on (phantom tile only if eliminated).
 * Active players is empty at the end of any game.
 * Eliminated players have no tiles in their hand.
 */

public class Server {

    public boolean legalPlay(SPlayer p, Board b, PTile t) throws CloneNotSupportedException{
        // checks if the player has the tile played in his hand
        if (!p.getHand().contains(t))
            return false;

        // checks if the current rotation kills the player, if not, return true
        if(legalPlayHelper(p, b, t)){
            return true;
        }
        else{
            // checks for other possible plays given the tiles the player has
            for (int i = 0; i < p.getHand().size(); i++) {
                ATile thisTile = p.getHand().get(i);
                if (thisTile.getClass().equals(PTile.class)) {
                    PTile clonedTile = (PTile) thisTile.clone();
                    for (int j = 0; j < 4; j++) {
                        if (legalPlayHelper(p, b, clonedTile)) {
                            return false;
                        } else {
                            clonedTile.rotate();
                        }
                    }
                }
            }
            return true;
        }
    }

    private boolean legalPlayHelper(SPlayer p, Board b, PTile t) throws CloneNotSupportedException{
        Board hypBoard = (Board)b.clone();
        SPlayer hypPlayer = (SPlayer)p.clone();
        hypBoard.addTile(t, hypPlayer.getLocation());
        return hypPlayer.movePlayer_inGame(hypBoard);
    }

    public Turn playATurn(Turn t, PTile playTile) throws RuntimeException {
        if (t.getActivePlayers().isEmpty()) {
            throw new RuntimeException("Cannot play a turn with zero active players.");
        }
        SPlayer currPlayer = t.getActivePlayers().get(0);
        Board b = t.getGameBoard();

        // update player location/states using new board after tile placement
        int[] location = currPlayer.getNextTileLocation();
        b.addTile(playTile, location);
        currPlayer.getHand().remove(playTile);

        // check for eliminated players in active players loop
        for(int i = 0; i < t.getActivePlayers().size(); i++) {
            SPlayer updatePlayer = t.getActivePlayers().get(i);
            if (!updatePlayer.movePlayer_inGame(b)) {
                t.getEliminatedPlayers().add(updatePlayer);
                t.getWinners().add(updatePlayer);
                t.getActivePlayers().remove(i);
                // if no players left alive, end game
                if (t.getActivePlayers().isEmpty()){
                    updatePlayer.getHand().clear(); // preserve invariant that eliminated players have no hand
                    t.setDone(true);
                    return t;
                }
                // redistribute eliminated player's tiles to draw pile
                for(ATile currTile : updatePlayer.getHand()) {
                    if (currTile.getClass().equals(PTile.class)) {
                        t.getDrawPile().add(currTile);
                    }
                    else {
                        int nextDragonPlayerIndex = i % t.getActivePlayers().size();
                        SPlayer nextDragonPlayer = t.getActivePlayers().get(nextDragonPlayerIndex);
                        nextDragonPlayer.addTileToHand(currTile);
                    }
                }
                updatePlayer.getHand().clear();
                i--;
            }
        }
        // since active players remain, remove eliminated players from winners
        t.getWinners().clear();

        // if alive, move currPlayer to end of queue
        if (t.getActivePlayers().get(0) == currPlayer) {
            t.getActivePlayers().add(t.getActivePlayers().remove(0));
        }

        SPlayer dPlayer = t.dragonPlayer();
        // draw tiles from pile
        if (dPlayer != null) {
            while(!t.getDrawPile().isEmpty()) {
                dPlayer.addTileToHand(t.getDrawPile().remove(0));
                DTile dragonTile = null;
                // find where the dragon tile is in the dragonPlayer's hand
                for (ATile tile : dPlayer.getHand()){
                    if (tile.getClass().equals(DTile.class)){
                        dragonTile = (DTile)tile;
                    }
                }
                // pass dragon tile to the next player in the circle clockwise
                for (int i = 0; i < t.getActivePlayers().size(); i++){
                    if (t.getActivePlayers().get(i) == dPlayer){
                        int nextDPlayerIndex = (i+1) % t.getActivePlayers().size();
                        t.getActivePlayers().get(nextDPlayerIndex).addTileToHand(dragonTile);
                        dPlayer.getHand().remove(dragonTile);
                        dPlayer = t.dragonPlayer();
                        break;
                    }
                }
            }
        }
        else if (t.getActivePlayers().contains(currPlayer) && !t.getDrawPile().isEmpty()){
            currPlayer.addTileToHand(t.getDrawPile().remove(0));
        }
        else if (t.getDrawPile().isEmpty()){
            throw new RuntimeException("No tiles left, but the dragon tile is not in play? Impossible!");
        }

        //update winner and game state
        if (t.getActivePlayers().size() == 1) {
            //update winners
            t.getWinners().add(t.getActivePlayers().remove(0));
            t.setDone(true);
        }
        else if (b.getTilesPlayed() == 35) {
            t.getWinners().addAll(t.getActivePlayers());
            t.getActivePlayers().clear();
            t.setDone(true);
        }

        return t;
    }


    ArrayList<ATile> generateInitialDeck() throws CloneNotSupportedException{
        ArrayList<ATile> deck = new ArrayList<ATile>();
        deck.add(new PTile(new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}}));
        deck.add(new PTile(new int[][]{{0, 1}, {2, 4}, {3, 6}, {5, 7}}));
        deck.add(new PTile(new int[][]{{0, 6}, {1, 5}, {2, 4}, {3, 7}}));
        deck.add(new PTile(new int[][]{{0, 5}, {1, 4}, {2, 7}, {3, 6}}));
        deck.add(new PTile(new int[][]{{0, 2}, {1, 4}, {3, 7}, {5, 6}}));
        deck.add(new PTile(new int[][]{{0, 4}, {1, 7}, {2, 3}, {5, 6}}));
        deck.add(new PTile(new int[][]{{0, 1}, {2, 6}, {3, 7}, {4, 5}}));
        deck.add(new PTile(new int[][]{{0, 2}, {1, 6}, {3, 7}, {4, 5}}));
        deck.add(new PTile(new int[][]{{0, 4}, {1, 5}, {2, 6}, {3, 7}}));
        deck.add(new PTile(new int[][]{{0, 1}, {2, 7}, {3, 4}, {5, 6}}));
        deck.add(new PTile(new int[][]{{0, 2}, {1, 7}, {3, 4}, {5, 6}}));
        deck.add(new PTile(new int[][]{{0, 3}, {1, 5}, {2, 7}, {4, 6}}));
        deck.add(new PTile(new int[][]{{0, 4}, {1, 3}, {2, 7}, {5, 6}}));
        deck.add(new PTile(new int[][]{{0, 3}, {1, 7}, {2, 6}, {4, 5}}));
        deck.add(new PTile(new int[][]{{0, 1}, {2, 5}, {3, 6}, {4, 7}}));
        deck.add(new PTile(new int[][]{{0, 3}, {1, 6}, {2, 5}, {4, 7}}));
        deck.add(new PTile(new int[][]{{0, 1}, {2, 7}, {3, 5}, {4, 6}}));
        deck.add(new PTile(new int[][]{{0, 7}, {1, 6}, {2, 3}, {4, 5}}));
        deck.add(new PTile(new int[][]{{0, 7}, {1, 2}, {3, 4}, {5, 6}}));
        deck.add(new PTile(new int[][]{{0, 2}, {1, 4}, {3, 6}, {5, 7}}));
        deck.add(new PTile(new int[][]{{0, 7}, {1, 3}, {2, 5}, {4, 6}}));
        deck.add(new PTile(new int[][]{{0, 7}, {1, 5}, {2, 6}, {3, 4}}));
        deck.add(new PTile(new int[][]{{0, 4}, {1, 5}, {2, 7}, {3, 6}}));
        deck.add(new PTile(new int[][]{{0, 1}, {2, 4}, {3, 5}, {6, 7}}));
        deck.add(new PTile(new int[][]{{0, 2}, {1, 7}, {3, 5}, {4, 6}}));
        deck.add(new PTile(new int[][]{{0, 7}, {1, 5}, {2, 3}, {4, 6}}));
        deck.add(new PTile(new int[][]{{0, 4}, {1, 3}, {2, 6}, {5, 7}}));
        deck.add(new PTile(new int[][]{{0, 6}, {1, 3}, {2, 5}, {4, 7}}));
        deck.add(new PTile(new int[][]{{0, 1}, {2, 7}, {3, 6}, {4, 5}}));
        deck.add(new PTile(new int[][]{{0, 3}, {1, 2}, {4, 6}, {5, 7}}));
        deck.add(new PTile(new int[][]{{0, 3}, {1, 5}, {2, 6}, {4, 7}}));
        deck.add(new PTile(new int[][]{{0, 7}, {1, 6}, {2, 5}, {3, 4}}));
        deck.add(new PTile(new int[][]{{0, 2}, {1, 3}, {4, 6}, {5, 7}}));
        deck.add(new PTile(new int[][]{{0, 5}, {1, 6}, {2, 7}, {3, 4}}));
        deck.add(new PTile(new int[][]{{0, 5}, {1, 3}, {2, 6}, {4, 7}}));
        Collections.shuffle(deck);
        deck.add(new DTile());
        return deck;
    }

    public void runTournament(ArrayList<MPlayer> mPlayers) throws CloneNotSupportedException {
        ArrayList<Colors> availableColors = new ArrayList<Colors>(Arrays.asList(Colors.values()));
        ArrayList<Colors> allColors = new ArrayList<Colors>();
        ArrayList<Colors> allColorsOriginal;
        ArrayList<SPlayer> initialPlayers = new ArrayList<SPlayer>();

        Board b = new Board();
        for (MPlayer p: mPlayers) {
            Colors pColor = p.chooseColor(availableColors);
            allColorsOriginal = allColors;
            p.initialize(pColor, allColors);

            if (!allColorsOriginal.containsAll(allColors) || allColorsOriginal.size() != allColors.size() + 1) {
                // Contract Violated by Player: Player should only add a new color to the current list of colors.
                allColors = allColorsOriginal;
                replaceCheatingMPlayer(p, pColor, allColors);
            }

            int[] pLocation = p.placePawn(b);
            b.setPlayerLocation(pColor, pLocation);

            if (!b.getPlayerLocations().keySet().equals(new HashSet<Colors>(allColors))){
                throw new RuntimeException("Contract Violated by Server: Server should only place a new pawn with the new color.");
            }

            SPlayer pSPlayer = new SPlayer(pLocation, p);

            initialPlayers.add(pSPlayer);
        }

        // everyone gets 3 tiles
        ArrayList<ATile> deck = generateInitialDeck();
        for (SPlayer sp : initialPlayers) {
            for (int i = 0; i < 3; i++) {
                sp.drawTile(deck);
            }
        }

        // start game
        Turn t = new Turn(deck, initialPlayers, new ArrayList<SPlayer>(), new Board());

        while (!t.isDone()) {
            SPlayer currPlayer = initialPlayers.get(0);
            if (!b.getPlayerLocations().keySet().equals(new HashSet<Colors>(allColors))){
                throw new RuntimeException("Contract Violated by Server: Colors of pawns on the board inconsistent with initialized colors.");
            }

            ArrayList<ATile> currHand = currPlayer.getHand();
            if(currHand.size() > 3){
                // Contract Violated by Player: More than three tiles are found in hand.
                replaceCheatingPlayer(currPlayer);
            }

            for(int i = 0; i < currHand.size(); i++){
                if (b.isTileOnBoard(currHand.get(i))){
                    throw new RuntimeException("Contract Violated by Server: Tile in hand also found on the board.");
                }
                for(int j = i + 1; j < currHand.size(); j++){
                    if (currHand.get(i).equals(currHand.get(j))){
                        // equals() check for rotations
                        // Contract Violated by Player: Duplicate tiles found in hand.
                        replaceCheatingPlayer(currPlayer);
                    }
                }
            }

            PTile p = currPlayer.player.playTurn(b, currHand, deck.size());
            if (!legalPlay(currPlayer, b, p)){
                // Contract Violated by Player: Chosen play is not legal.
                replaceCheatingPlayer(currPlayer);
            }
            playATurn(t, p);
        }

        ArrayList<Colors> winnerColors = new ArrayList<Colors>();
        for (SPlayer sp : t.getWinners()) {
            System.out.println(sp.player.getName());
            winnerColors.add(sp.getColor());
        }

        for (MPlayer mp : mPlayers){
            mp.endGame(b, winnerColors);
        }
    }

    private void replaceCheatingPlayer(SPlayer sp){
        MPlayer playerSub = new RandomPlayer(sp.player.getName() + "Sub");
        playerSub.initialize(sp.player.getColor(), new ArrayList<Colors>());
        playerSub.setNextAction(playerSub.getNextAction());
        sp.player = playerSub;
    }

    private void replaceCheatingMPlayer(IPlayer mp, Colors c, ArrayList<Colors> allC){
        MPlayer playerSub = new RandomPlayer(mp.getName() + "Sub");
        playerSub.initialize(c, allC);
        mp = playerSub;
    }
    // Move all cheating checks to SPlayer
}
