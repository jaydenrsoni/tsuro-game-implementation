package main;

import java.io.IOException;
import java.net.ServerSocket;
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
    private List<SPlayer> remainingPlayers;
    private List<SPlayer> eliminatedPlayers;
    private TilePile tilePile;
    private SPlayer dragonTileOwner;

    //================================================================================
    // Getters
    //================================================================================
    public Board getBoard() {
        return board;
    }

    public TilePile getTilePile() {
        return tilePile;
    }

    public List<SPlayer> getRemainingPlayers() {
        return remainingPlayers;
    }

    public List<SPlayer> getEliminatedPlayers() {
        return eliminatedPlayers;
    }

    //================================================================================
    // Setters
    //================================================================================
    public void setTilePile(TilePile tilePile) {
        this.tilePile = tilePile;
    }

    public void setFromPlayATurnInput(Board board, List<SPlayer> remainingPlayers,
                                      List<SPlayer> eliminatedPlayers, TilePile tilePile) {
        this.board = board;
        this.remainingPlayers = remainingPlayers;
        this.eliminatedPlayers = eliminatedPlayers;
        this.tilePile = tilePile;
    }

    //================================================================================
    // Public Methods
    //================================================================================

    // Adds a player to a new game
    public void registerPlayer(String name, PlayerType type){
        APlayer aplayer;
        switch(type) {
            case RANDOM:
                aplayer = new RandomPlayer(name);
                break;
            case MOSTSYMMETRIC:
                aplayer = new MostSymmetricPlayer(name);
                break;
            case LEASTSYMMETRIC:
                aplayer = new LeastSymmetricPlayer(name);
                break;
            default:
                throw new IllegalArgumentException("player type given was not valid");
        }

        remainingPlayers.add(new SPlayer(aplayer, tilePile));
    }

    // For testing purposes only
    // TODO: Remove in production
    public void registerPlayer(APlayer aplayer){
        SPlayer newPlayer = new SPlayer(aplayer, tilePile);
        remainingPlayers.add(newPlayer);
    }

    // Determine whether a player has the ability to play the move.
    public boolean isLegalMove(Tile tile, SPlayer splayer){
        if(!splayer.holdsTile(tile))
            return false;

        if(splayer.hasSafeMove() && board.willKillPlayer(tile, splayer))
            return false;

        return true;
    }

    // Let a player with an empty hand request the TilePile
    public void requestDragonTile(SPlayer splayer){
        if (dragonTileOwner == null && tilePile.isEmpty()) {
            dragonTileOwner = splayer;
        }
    }

    public boolean hasDragonTile(SPlayer splayer){
        return splayer == dragonTileOwner;
    }

    /* TODO: MAKE PRIVATE WHEN NOT DEBUGGING */
    // Deal with when the player place the tile on the board
    //   Returns a set of players who have lost after the tile is placed
    public Set<SPlayer> playTurn(Tile tile, SPlayer splayer) throws ContractException{
            if (!isLegalMove(tile, splayer)) {
                throw new ContractException("Player made an illegal move");
            }

            Set<Token> failedTokens = board.placeTile(tile, splayer);
            Set<SPlayer> failedPlayers = new HashSet<>();
            for (Token failedToken : failedTokens)
                failedPlayers.add(failedToken.getPlayer());

            if (failedPlayers.containsAll(remainingPlayers))
                return failedPlayers;

            splayer.removeTileFromHand(tile);
            splayer.drawFromPile();

            if (!failedPlayers.isEmpty()) {
                for (SPlayer failedPlayer : failedPlayers)
                    failedPlayer.returnTilesToPile();

                SPlayer playerToDrawFirst = findPlayerToDrawFirst(failedPlayers, splayer);

                for (SPlayer failedPlayer : failedPlayers)
                    eliminatePlayer(failedPlayer);


                drawAfterElimination(playerToDrawFirst);
            }

            return failedPlayers;
    }

    public void initializePlayers(){
        List<Color> startingColorList = new ArrayList<>();
        Color[] colors = Color.values();

        for(int i = 0; i < remainingPlayers.size(); i++){
            startingColorList.add(colors[i]);
        }

        for(int i = 0; i < remainingPlayers.size(); i++){
            remainingPlayers.get(i).initializeSPlayer(startingColorList.get(i), startingColorList);
        }
    }

    public boolean isGameOverWithLoss(Set<SPlayer> losingPlayers){
        if(losingPlayers.containsAll(remainingPlayers) || remainingPlayers.size() <= 1){
            return true;
        }
        if(tilePile.isEmpty() && areAllRemainingHandsEmpty()){
            return true;
        }
        return false;
    }


    // Main game loop
    public Set<Color> playGame(){
        initializePlayers();

        for (SPlayer splayer: remainingPlayers) {
            splayer.placeToken(board);
        }

        /*
        for (int i = 0; remainingPlayers.size() <= 1; i = (i + 1) % remainingPlayers.size())
         */

//        int i = 0;
        //is blaming system good?
        while (true) {
            SPlayer splayer = remainingPlayers.get(0);
            Tile tile = splayer.chooseTile(board);
            try {
                Set<SPlayer> losingPlayers = playTurn(tile, splayer);
                if(isGameOverWithLoss(losingPlayers)){
                    break;
                }
            }
            catch (ContractException e) {
                blamePlayer(splayer);
                continue;
            }

            if(!eliminatedPlayers.contains(splayer)){
                remainingPlayers.remove(splayer);
                remainingPlayers.add(splayer);
            }

//            if (remainingPlayers.get(i).equals(splayer))
//                i = (i + 1) % remainingPlayers.size();
        }

        Set<Color> winningColors = new HashSet<>();
        for (SPlayer splayer : remainingPlayers) {
            winningColors.add(splayer.getColor());
        }

        for (SPlayer splayer : remainingPlayers) {
            splayer.endGame(board, winningColors);
        }
        for (SPlayer splayer : eliminatedPlayers) {
            splayer.endGame(board, winningColors);
        }

        return winningColors;
    }

    public Set<Color> playNetworkedGame() {
        try {
            ServerSocket serverSocket = new ServerSocket(NetworkAdapter.PORTNUMBER);
            while (remainingPlayers.size() < 2) { //can be changed to change the number of players in the game
                SPlayer splayer = new SPlayer(new NetworkPlayer(serverSocket.accept()), tilePile);
                remainingPlayers.add(splayer);
                System.out.println("connected server to " + splayer.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return playGame();
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
       for(SPlayer splayer : remainingPlayers){
           if (!splayer.hasFullHand())
               return false;
       }
       return true;
    }

    private boolean areAllRemainingHandsEmpty() {
        for(SPlayer splayer : remainingPlayers){
            if (!splayer.hasEmptyHand())
                return false;
        }
        return true;
    }

    // After a player has been eliminated, go around in a clockwise direction and have
    //   all players draw tiles if necessary
    private void drawAfterElimination(SPlayer playerToDrawFirst){
        int playerToDrawIndex = remainingPlayers.indexOf(playerToDrawFirst);
        while(!tilePile.isEmpty() && !areAllRemainingHandsFull()){
            remainingPlayers.get(playerToDrawIndex).drawFromPile();
            playerToDrawIndex = (playerToDrawIndex + 1) % remainingPlayers.size();
            resetDragonTile();
        }
    }

    // Determine which player should draw first after a player has been eliminated
    private SPlayer findPlayerToDrawFirst(Set<SPlayer> failedPlayers, SPlayer currentPlayer){
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
    private void eliminatePlayer(SPlayer eliminatedPlayer){
        if (dragonTileOwner == eliminatedPlayer){
            resetDragonTile();
        }
        eliminatedPlayer.returnTilesToPile();
        remainingPlayers.remove(eliminatedPlayer);
        eliminatedPlayers.add(eliminatedPlayer);
    }

    private void blamePlayer(SPlayer splayer){
        splayer.replaceWithRandom();
    }



    //================================================================================
    // Main
    //================================================================================

    /* Runs a simple command line UI to play a game
        EXPERIMENTAL
     */
    public static void main(String[] args){
//        Game game = getGame();
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Welcome to Tsuro!");
//
//        for (String player: args) {
//            game.registerPlayer(player, Color.BLACK, PlayerType.RANDOM);
//        }
//        game.playGame();
    }
}
