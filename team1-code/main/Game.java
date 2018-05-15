package main;

import javafx.util.Pair;
import main.Players.APlayer;
import main.Players.LeastSymmetricPlayer;
import main.Players.MostSymmetricPlayer;
import main.Players.RandomPlayer;

import java.util.*;

public class Game {

    //================================================================================
    // Singleton Model
    //================================================================================
    private static Game game;

    // Default constructor
    private Game(){
       this.board = new Board();
       this.remainingPlayers = new ArrayList<>();
       this.eliminatedPlayers = new ArrayList<>();
       this.tilePile = new TilePile();
       dragonTileOwner = null;
   }

    public static Game getGame(){
        if (game == null) game = new Game();
        return game;
    }

    public static void resetGame(){
        game = null;
    }

    //================================================================================
    // Instance Variables
    //================================================================================
    private Board board;
    private List<APlayer> remainingPlayers;
    private List<APlayer> eliminatedPlayers;
    private TilePile tilePile;
    private APlayer dragonTileOwner;

    //================================================================================
    // Getters
    //================================================================================
    public Board getBoard() {
        return board;
    }

    public TilePile getTilePile() {
        return tilePile;
    }

    //================================================================================
    // Setters
    //================================================================================
    public void setTilePile(TilePile tilePile) {
        this.tilePile = tilePile;
    }

    //================================================================================
    // Public Methods
    //================================================================================

    // Adds a player to a new game
    public void registerPlayer(String name, Color color, PlayerType type){
        APlayer aplayer;
        switch(type) {
            case RANDOM:
                aplayer = new RandomPlayer(name, color);
                break;
            case MOSTSYMMETRIC:
                aplayer = new MostSymmetricPlayer(name, color);
                break;
            case LEASTSYMMETRIC:
                aplayer = new LeastSymmetricPlayer(name, color);
                break;
            default:
                throw new IllegalArgumentException("player type given was not valid");
        }

        remainingPlayers.add(aplayer);
    }


    // For testing purposes only
    // TODO: Remove in production
    public void registerPlayer(APlayer player){
        remainingPlayers.add(player);
    }

    // Determine whether a player has the ability to play the move.
    public boolean isLegalMove(Tile tile, APlayer player){
        if(!player.holdsTile(tile))
            return false;

        if(player.hasSafeMove() && board.willKillPlayer(tile, player))
            return false;

        return true;
    }

    // Let a player with an empty hand request the TilePile
    public void requestDragonTile(APlayer player){
        if (dragonTileOwner == null && tilePile.isEmpty()) {
            dragonTileOwner = player;
        }
    }

    /* TODO: MAKE PRIVATE WHEN NOT DEBUGGING */
    // Deal with when the player place the tile on the board
    //   Returns a set of players who have lost after the tile is placed
    public Set<APlayer> playTurn(Tile tile, APlayer player) throws ContractException{
            if (!isLegalMove(tile, player)) {
                throw new ContractException("Player made an illegal move");
            }

            Set<Token> failedTokens = board.placeTile(tile, player);
            Set<APlayer> failedPlayers = new HashSet<>();
            for (Token failedToken : failedTokens)
                failedPlayers.add(failedToken.getPlayer());

            if (failedPlayers.containsAll(remainingPlayers))
                return failedPlayers;

            player.removeTileFromHand(tile);
            player.drawFromPile();

            if (!failedPlayers.isEmpty()) {
                for (APlayer failedPlayer : failedPlayers)
                    failedPlayer.returnTilesToPile();

                APlayer playerToDrawFirst = findPlayerToDrawFirst(failedPlayers, player);

                for (APlayer failedPlayer : failedPlayers)
                    eliminatePlayer(failedPlayer);


                drawAfterElimination(playerToDrawFirst);
            }

            return failedPlayers;
    }

    public void initializePlayers(){
        List<Token> startingTokenList = new ArrayList<>();
        for(APlayer player : remainingPlayers){
            startingTokenList.add(player.getToken());
        }
        for(APlayer player : remainingPlayers){
            player.initialize(startingTokenList);
        }
    }


    // Main game loop
    public Set<APlayer> playGame(){
        initializePlayers();

        for (APlayer player: remainingPlayers) {
            player.placeToken();
        }

        /*
        for (int i = 0; remainingPlayers.size() <= 1; i = (i + 1) % remainingPlayers.size())
         */

//        int i = 0;
        while (true) {
            APlayer player = remainingPlayers.get(0);
            Tile tile = player.chooseTile();
            try {
                Set<APlayer> losingPlayers = playTurn(tile, player);
                if(losingPlayers.containsAll(remainingPlayers) || remainingPlayers.size() <= 1){
                    break;
                }
                if(tilePile.isEmpty() && areAllRemainingHandsEmpty()){
                    break;
                }
            }
            catch (ContractException e) {
                remainingPlayers.remove(player);
                player = blamePlayer(player);
                remainingPlayers.add(0, player);
                continue;
            }

            if(!eliminatedPlayers.contains(player)){
                remainingPlayers.remove(player);
                remainingPlayers.add(player);
            }

//            if (remainingPlayers.get(i).equals(player))
//                i = (i + 1) % remainingPlayers.size();
        }

        for (APlayer player : remainingPlayers){
            player.endGame();
        }
        for (APlayer player : eliminatedPlayers){
            player.endGame();
        }

        return new HashSet<>(remainingPlayers);
    }


    //================================================================================
    // Private Helpers
    //================================================================================

    // Remove the dragon tile from whatever player that has it
    private void resetDragonTile(){
        if (dragonTileOwner != null){
            dragonTileOwner = null;
        }
    }

    // Checks to see if all players still in the game have full hands
    private boolean areAllRemainingHandsFull() {
       for(APlayer player : remainingPlayers){
           if (!player.hasFullHand())
               return false;
       }
       return true;
    }

    private boolean areAllRemainingHandsEmpty() {
        for(APlayer player : remainingPlayers){
            if (!player.hasEmptyHand())
                return false;
        }
        return true;
    }

    // After a player has been eliminated, go around in a clockwise direction and have
    //   all players draw tiles if necessary
    private void drawAfterElimination(APlayer playerToDrawFirst){
        int playerToDrawIndex = remainingPlayers.indexOf(playerToDrawFirst);
        while(!tilePile.isEmpty() && !areAllRemainingHandsFull()){
            remainingPlayers.get(playerToDrawIndex).drawFromPile();
            playerToDrawIndex = (playerToDrawIndex + 1) % remainingPlayers.size();
            resetDragonTile();
        }
    }

    // Determine which player should draw first after a player has been eliminated
    private APlayer findPlayerToDrawFirst(Set<APlayer> failedPlayers, APlayer currentPlayer){
        if (dragonTileOwner != null && !failedPlayers.contains(dragonTileOwner)){
            return dragonTileOwner;
        }
        else {
            int currentIndex = remainingPlayers.indexOf(currentPlayer);
            while (failedPlayers.contains(remainingPlayers.get(currentIndex))){
                currentIndex = (currentIndex + 1) % remainingPlayers.size();
            }
            return remainingPlayers.get(currentIndex);
        }
    }

    // Eliminates a player. To be called when a player token is forced off the edge
    private void eliminatePlayer(APlayer eliminatedPlayer){
        if (dragonTileOwner == eliminatedPlayer){
            resetDragonTile();
        }
        eliminatedPlayer.returnTilesToPile();
        remainingPlayers.remove(eliminatedPlayer);
        eliminatedPlayers.add(eliminatedPlayer);
    }

    private APlayer blamePlayer(APlayer splayer){
        return new RandomPlayer(splayer);
    }



    //================================================================================
    // Main
    //================================================================================

    /* Runs a simple command line UI to play a game
        EXPERIMENTAL
     */
    public static void main(String[] args){
        Game game = getGame();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Tsuro!");

        for (String player: args) {
            game.registerPlayer(player, Color.BLACK, PlayerType.RANDOM);
        }
        game.playGame();
    }
}
