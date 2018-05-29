package main;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    private IPlayer iplayer;
    private Color color;
    private List<Color> otherPlayerColors;

    public SPlayer(IPlayer iplayer, TilePile tilePile){
        this.iplayer = iplayer;
        this.tilePile = tilePile;
        this.curState = State.GAMEENDED;
        this.token = null;
        this.hand = new ArrayList<>();

        for(int i = 0; i < MAX_TILES_IN_BANK; i++){
            drawFromPile();
        }
    }

    public SPlayer(Node splayerNode, TilePile tilePile, Board board){
        if (splayerNode.getNodeName().equals("splayer-dragon")) {
            requestDragonTile();
        }
        NodeList splayerNodeChildren = splayerNode.getChildNodes();
        this.color = Color.decodeColor(splayerNodeChildren.item(0));

        this.hand = new ArrayList<>(NetworkAdapter.decodeSetOfTiles(splayerNodeChildren.item(1)));
        this.tilePile = tilePile;
        this.iplayer = null;
        this.curState = State.GAMEENDED;
        this.token = board.findTokenFromColor(this.color);

    }

    //================================================================================
    // APlayer calls
    //================================================================================
    public String getName() {
        return iplayer.getName();
    }

    public void decodeAddToken(Board board, Color color, Node pawnLocNode) {
        token = new Token(board, color, pawnLocNode);
    }


    public void initializeSPlayer(Color playerColor, List<Color> otherPlayerColors){
        if (curState != State.GAMEENDED)
            throw new ContractException();

        this.color = playerColor;
        this.otherPlayerColors = otherPlayerColors;
        iplayer.initialize(playerColor, otherPlayerColors);
        this.curState = State.INITIALIZED;
    }

    public void placeToken(Board board) {
        if (curState != State.INITIALIZED)
            throw new ContractException();

        Pair<BoardSpace, Integer> startingTokenLocation = iplayer.placePawn(board);
        token = new Token(startingTokenLocation.getKey(), startingTokenLocation.getValue(), color);
        curState = State.TURNPLAYABLE;
    }

    //Same as placeToken but with a provided location for testing
    public void placeToken(BoardSpace startingLocation, int startingTokenSpace){
        if (curState != State.INITIALIZED)
            throw new ContractException();

        token = new Token(startingLocation, startingTokenSpace, color);
        curState = State.TURNPLAYABLE;
    }

    public Tile chooseTile(Board board){
        if (curState != State.TURNPLAYABLE || !isValidHand())
            throw new ContractException();

        return iplayer.playTurn(board, hand, tilePile.size());
    }

    public void endGame(Board board, Set<Color> winningColors){
        if (curState != State.TURNPLAYABLE)
            throw new ContractException();

        iplayer.endGame(board, winningColors);
        curState = State.GAMEENDED;
    }


    public Token getToken(){
        return token;
    }

    public Color getColor() { return this.color; }


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
        iplayer = new RandomPlayer(iplayer.getName());
        iplayer.initialize(color, otherPlayerColors);
    }

    public Element encodeSPlayer(Document doc){
        Element splayerElement = encodeSPlayerTagName(doc);

        Element colorNode = color.encodeColor(doc);
        splayerElement.appendChild(colorNode);

        Element tileSetNode = NetworkAdapter.encodeSetOfTiles(doc, new HashSet<>(hand));
        splayerElement.appendChild(tileSetNode);

        return splayerElement;
    }

    //================================================================================
    // Private methods
    //================================================================================
    private void requestDragonTile(){
        Game game = Game.getGame();
        game.requestDragonTile(this);
    }

    private Element encodeSPlayerTagName(Document doc){
        if(Game.getGame().hasDragonTile(this)){
            return doc.createElement("splayer-dragon");
        }
        else {
            return doc.createElement("splayer-nodragon");
        }
    }

    //================================================================================
    // Sequential Contract
    //================================================================================
    private enum State {INITIALIZED, TURNPLAYABLE, GAMEENDED};


}
