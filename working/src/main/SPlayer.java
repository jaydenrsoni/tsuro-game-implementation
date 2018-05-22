package main;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SPlayer {

    private final int MAX_TILES_IN_BANK = 3;

    private State curState;
    private Token token;
    private List<Tile> hand;
    private TilePile tilePile;
    private APlayer aplayer;

    SPlayer(APlayer aplayer, TilePile tilePile){
        this.aplayer = aplayer;
        this.tilePile = tilePile;
        this.curState = State.UNINITIALIZED;
        this.token = null;
        this.hand = new ArrayList<>();

        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            drawFromPile();
        }
    }

    //================================================================================
    // APlayer calls
    //================================================================================
    public String getName() {
        return aplayer.getName();
    }

    public void initialize(Color playerColor, List<Color> otherPlayerColors){
        if (curState != State.UNINITIALIZED)
            throw new ContractException();

        aplayer.initialize(playerColor, otherPlayerColors);
        this.curState = State.INITIALIZED;
    }

    public void placeToken(Board board) {
        if (curState != State.INITIALIZED)
            throw new ContractException();

        Pair<BoardSpace, Integer> startingTokenLocation = aplayer.placePawn(board);
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

    public Tile chooseTile(Board board){
        if (curState != State.TURNPLAYABLE || !isValidHand())
            throw new ContractException();

        return aplayer.playTurn(board, hand, tilePile.size());
    }

    public void endGame(Board board, List<Color> winningColors){
        if (curState != State.TURNPLAYABLE)
            throw new ContractException();

        aplayer.endGame(board, winningColors);
        curState = State.GAMEENDED;
    }


    public Token getToken(){
        return token;
    }

    public Color getColor(){
        return aplayer.getColor();
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

    public void replaceWithRandom(){
        Color color = aplayer.getColor();
        List<Color> otherPlayers = aplayer.getOtherPlayers();
        aplayer = new RandomPlayer(aplayer.getName());
        aplayer.initialize(color, otherPlayers);
    }


    //================================================================================
    // Private methods
    //================================================================================
    private void requestDragonTile(){
        Game game = Game.getGame();
        game.requestDragonTile(this);
    }

    //================================================================================
    // Sequential Contract
    //================================================================================
    private enum State {UNINITIALIZED, INITIALIZED, TURNPLAYABLE, GAMEENDED};


}
