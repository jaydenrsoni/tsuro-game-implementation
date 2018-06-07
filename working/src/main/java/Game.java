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

    public SPlayer registerPlayer(IPlayer iplayer){
        SPlayer newPlayer = new SPlayer(iplayer, tilePile);
        remainingPlayers.add(newPlayer);
        return newPlayer;
    }

    // Determine whether a player has the ability to play the move.
    public boolean isLegalMove(Tile tile, SPlayer splayer){
        if(!splayer.holdsTile(tile))
            return false;

        if(splayer.hasSafeMove(board) && board.willKillPlayer(tile, splayer))
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

    // Deal with when the player place the tile on the board
    //   Returns a set of players who have lost after the tile is placed
    public Set<SPlayer> playTurn(Tile tile, SPlayer splayer) throws ContractException{
        if (!isLegalMove(tile, splayer)) {
            throw new ContractException(splayer.getName() + " has made an illegal move");
        }

        splayer.removeTileFromHand(tile);
        Set<Token> failedTokens = board.placeTile(tile, splayer);
        Set<SPlayer> failedPlayers = new HashSet<>();
        for (SPlayer remainingPlayer : remainingPlayers) {
            if(failedTokens.contains(remainingPlayer.getToken())){
                failedPlayers.add(remainingPlayer);
            }
        }

        if (failedPlayers.containsAll(remainingPlayers))
            return failedPlayers;

        if(!failedPlayers.contains(splayer))
            splayer.drawFromPile();

        if (!failedPlayers.isEmpty()) {
            for (SPlayer failedPlayer : failedPlayers)
                eliminatePlayer(failedPlayer);

            dragonPlayerDrawTileLoop();
        }

        if(!eliminatedPlayers.contains(splayer)){
            remainingPlayers.remove(splayer);
            remainingPlayers.add(splayer);
        }

        return failedPlayers;
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
    public Set<Color> playGame(int numberOfRemotePlayers){
        connectRemotePlayers(numberOfRemotePlayers);
        initializePlayers();

        for (SPlayer splayer: remainingPlayers) {
            splayer.placeToken(board);
        }

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
            }
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

    //================================================================================
    // Private Helpers
    //================================================================================

    private void connectRemotePlayers(int numberOfRemotePlayers) {
        try {
            ServerSocket serverSocket = new ServerSocket(NetworkAdapter.PORTNUMBER);
            while (remainingPlayers.size() < numberOfRemotePlayers) { //can be changed to change the number of players in the game
                SPlayer splayer = new SPlayer(new NetworkPlayer(serverSocket.accept()), tilePile);
                remainingPlayers.add(splayer);
                System.out.println("connected server to " + splayer.getName());
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializePlayers(){
        List<Color> startingColorList = new ArrayList<>();
        Color[] colors = Color.values();

        for(int i = 0; i < remainingPlayers.size(); i++){
            startingColorList.add(colors[i]);
        }

        for(int i = 0; i < remainingPlayers.size(); i++){
            remainingPlayers.get(i).initializeSPlayer(startingColorList.get(i), startingColorList);
        }
    }

    private void dragonPlayerDrawTileLoop() {
        //if there are tiles in the pile and if someone has dragon tile
        while(dragonTileOwner != null && !tilePile.isEmpty()) {
            //person w/ dragon tile draws
            dragonTileOwner.drawFromPile();

            //person w/ dragon tile then passes to next person if they have less than 3 tiles
            SPlayer nextOwner = getNextRemainingPlayer(dragonTileOwner);
            if(nextOwner.hasFullHand())
                dragonTileOwner = null;
            else
                dragonTileOwner = nextOwner;
        }
    }

    //Gets the next player after the given player
    private SPlayer getNextRemainingPlayer(SPlayer splayer) {
        int splayerIndex = remainingPlayers.indexOf(splayer);
        int nextIndex = (splayerIndex + 1) % remainingPlayers.size();
        return remainingPlayers.get(nextIndex);
    }

    private boolean areAllRemainingHandsEmpty() {
        for(SPlayer splayer : remainingPlayers){
            if (!splayer.hasEmptyHand())
                return false;
        }
        return true;
    }

    // Eliminates a player. To be called when a player token is forced off the edge
    private void eliminatePlayer(SPlayer eliminatedPlayer){
        if (dragonTileOwner == eliminatedPlayer){
            dragonTileOwner = getNextRemainingPlayer(eliminatedPlayer);
        }
        eliminatedPlayer.returnTilesToPile();
        remainingPlayers.remove(eliminatedPlayer);
        eliminatedPlayers.add(eliminatedPlayer);
    }

    private void blamePlayer(SPlayer splayer){
        splayer.replaceWithRandom();
    }
}
