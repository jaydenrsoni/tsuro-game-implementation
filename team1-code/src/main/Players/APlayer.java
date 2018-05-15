package main.Players;

import javafx.util.Pair;
import main.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class APlayer {

    //================================================================================
    // Instance Variables
    //================================================================================
    private final int MAX_TILES_IN_BANK = 3;

    private String name;
    private Color color;
    private List<Token> otherPlayers;
    private State curState;
    private Token token;
    private List<Tile> hand;
    private TilePile tilePile;

    //================================================================================
    // Constructor
    //================================================================================
    public APlayer(String name, Color color){
        this.name = name;
        this.color = color;
        curState = State.UNINITIALIZED;

        token = null;
        hand = new ArrayList<>();
        this.tilePile = Game.getGame().getTilePile();

        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            drawFromPile();
        }
    }

    public APlayer(APlayer other){
        name = other.name;
        color = other.color;
        otherPlayers = new ArrayList<>(other.otherPlayers);
        curState = other.curState;
        token = other.token;
        this.hand = new ArrayList<>(other.hand);
        tilePile = other.tilePile;
    }

    //================================================================================
    // Getters
    //================================================================================
    public Color getColor() {
        return color;
    }

    public String getName(){
        return name;
    }

    public Token getToken(){
        return token;
    }

    public Tile getTile(int i){
        if (0 <= i && i < 3) {
            if (i > hand.size() - 1)
                return null;

            return hand.get(i);
        }
        else
            throw new IndexOutOfBoundsException("Illegal Hand Access");
    }

    //================================================================================
    // Public methods
    //================================================================================
    public void placeToken() {
        if (curState != State.INITIALIZED)
            throw new ContractException();

        Pair<BoardSpace, Integer> startingTokenLocation = getStartingLocation();
        token = new Token(startingTokenLocation.getKey(), startingTokenLocation.getValue(), this);
        curState = State.TURNPLAYABLE;
    }

    //Same as placeToken but with a provided location for testing
    public void placeToken(BoardSpace startingLocation, int startingTokenSpace){
        if (curState != State.INITIALIZED)
            throw new ContractException();

        token = new Token(startingLocation, startingTokenSpace, this);
        curState = State.TURNPLAYABLE;
    }


    public void initialize(List<Token> otherPlayers){
        if (curState != State.UNINITIALIZED)
            throw new ContractException();

        this.otherPlayers = new ArrayList<>(otherPlayers);
        this.curState = State.INITIALIZED;
    }

    public void endGame(){
        if (curState != State.TURNPLAYABLE)
            throw new ContractException();
        // Do something?

        curState = State.GAMEENDED;
    }

    public Tile chooseTile(){
        if (curState != State.TURNPLAYABLE || !isValidHand())
            throw new ContractException();

        return chooseTileHelper();
    }

    public boolean holdsTile(Tile tile){
        return hand.contains(tile);
    }

    public void drawFromPile() {

        if (!hasFullHand() && !tilePile.isEmpty()) {
            Tile drawnTile = tilePile.drawFromDeck();

            // TODO: This check exists only for the benefit of our tests. Refactor tests to render it unneccesary
            if (drawnTile != null)
                hand.add(drawnTile);
        }
        else
            requestDragonTile();
    }

    public boolean hasFullHand() {
        return hand.size() == MAX_TILES_IN_BANK;
    }

    public boolean hasEmptyHand() {
        return hand.isEmpty();
    }

    public void removeTileFromHand(Tile tile){
        hand.remove(tile);
    }

    public void returnTilesToPile(){
        for (Tile tile: hand) {
            tilePile.returnToDeck(tile);
        }

        hand = new ArrayList<>();
    }

    public boolean isSafeMove(Tile tile){
        return !Game.getGame().getBoard().willKillPlayer(tile, this);
    }

    public boolean hasSafeMove(){
        for (Tile tile: hand){

            Tile copy = new Tile(tile);
            for (int i = 0; i < 4; i++){
                copy.rotateClockwise();
                if (isSafeMove(copy))
                    return true;
            }
        }
        return false;
    }

    public Set<Tile> getLegalMoves(){
        Set<Tile> legalMoves = new HashSet<>();
        boolean hasSafeMoves = hasSafeMove();

        for (Tile tile: hand){
            for (int rotation = 0; rotation < 4; rotation++){
                if (!hasSafeMoves || isSafeMove(tile))
                    legalMoves.add(new Tile (tile));
                tile.rotateClockwise();
            }
        }

        return legalMoves;
    }

    public boolean isValidHand(){
        if (hand.size() > 3)
            return false;

        for (Tile tile: hand){
            if (Game.getGame().getBoard().findLocationOfTile(tile) != null)
                return false;
        }

        for (int i = 0; i < hand.size(); i++){
            for (int j = i + 1; j < hand.size(); j++ ){
                if (hand.get(i).equals(hand.get(j)))
                    return false;
            }
        }

        return true;
    }

    //================================================================================
    // Private methods
    //================================================================================
    private void requestDragonTile(){
        Game game = Game.getGame();
        game.requestDragonTile(this);
    }

    //================================================================================
    // Abstract methods
    //================================================================================
    abstract public Pair<BoardSpace, Integer> getStartingLocation();

    // TODO: Think of a better name for this method
    abstract protected Tile chooseTileHelper();


    //================================================================================
    // Sequential Contract
    //================================================================================
    private enum State {UNINITIALIZED, INITIALIZED, TURNPLAYABLE, GAMEENDED};
}
