package main;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.*;

/**
 *
 * Represents a Tsuro Board
 *
 * Created by vyasalwar on 4/16/18.
 */
public class Board {

    //================================================================================
    // Constructor
    //================================================================================

    public Board() {
        this.spaces = new BoardSpace[BOARD_LENGTH][BOARD_LENGTH];
        for (int i = 0; i < BOARD_LENGTH; i++) {
            for (int j = 0; j < BOARD_LENGTH; j++) {
                spaces[i][j] = new BoardSpace(i, j);
            }
        }
    }

    //================================================================================
    // Instance Variables
    //================================================================================
    private BoardSpace[][] spaces;
    final static int BOARD_LENGTH = 6;

    //================================================================================
    // Getters
    //================================================================================
    public BoardSpace getBoardSpace(int row, int col) {
        if (!isValidCoordinate(row, col))
            throw new IllegalArgumentException("Invalid Tile Access");

        return spaces[row][col];
    }

    //================================================================================
    // Public methods
    //================================================================================

    // Returns true if there is a tile on the row and col
    public boolean isOccupied(int row, int col) {
        return getBoardSpace(row, col).hasTile();
    }

    // Returns true if placing the tile in front of the token will lead to the player's death
    // Does not actually place the tile
    public boolean willKillPlayer(Tile tile, SPlayer splayer) {
        Token token = splayer.getToken();
        BoardSpace curSpace = token.getBoardSpace();
        int curTokenSpace = token.getTokenSpace();

        return willKillPlayerFromLocation(tile, curSpace, curTokenSpace);
    }

    // Places the tile in front of the splayer, regardless of whether it will kill the splayer
    //   Returns the Set of tokens driven off the board
    public Set<Token> placeTile(Tile tile, SPlayer splayer) {

        // Place the tile on the space
        BoardSpace space = splayer.getToken().getBoardSpace();
        space.setTile(tile);

        // Gather every token currently on the space
        Set<Token> tokensToMove = space.getTokensOnSpace();
        Set<Token> eliminatedPlayers = new HashSet<>();

        // Advance each token to the end of their path
        for (Token token: tokensToMove){
            advanceToEnd(token);

            // Eliminate the token if necessary
            if (isOnEdge(token)) {
                eliminatedPlayers.add(token);
                token.removeFromBoard();
            }
        }

        return eliminatedPlayers;
    }

    public boolean hasSafeMove(List<Tile> hand, BoardSpace boardSpace, int tokenSpace){
        for (Tile tile: hand){
            Tile copy = new Tile(tile);
            for (int i = 0; i < 4; i++){
                copy.rotateClockwise();
                if (!willKillPlayerFromLocation(copy, boardSpace, tokenSpace))
                    return true;
            }
        }
        return false;
    }

    public List<Tile> findLegalMovesForPlayer(List<Tile> hand, Color color){
        List<Tile> legalMoves = new ArrayList<>();
        Pair<BoardSpace, Integer> location = findLocationFromColor(color);
        boolean hasSafeMoves = hasSafeMove(hand, location.getKey(), location.getValue());

        for (Tile tile: hand){
            for (int rotation = 0; rotation < 4; rotation++){
                if (!hasSafeMoves || !willKillPlayerFromLocation(tile, location.getKey(), location.getValue()))
                    legalMoves.add(new Tile (tile));
                tile.rotateClockwise();
            }
        }

        return legalMoves;
    }


    public BoardSpace findLocationOfTile(Tile tile){
        for (int row = 0; row < BOARD_LENGTH; row++) {
            for (int col = 0; col < BOARD_LENGTH; col++) {
                BoardSpace space = getBoardSpace(row, col);
                if (space.hasTile() && space.getTile().equals(tile))
                    return space;
            }
        }

        return null;
    }

    public Element encodeBoard(Document doc) {
        Element boardElement = doc.createElement("board");
        Element tilesNode = encodeTiles(doc);
        Element pawnsNode = encodePawns(doc);

        boardElement.appendChild(tilesNode);
        boardElement.appendChild(pawnsNode);

        return boardElement;
    }

    //================================================================================
    // Private Helpers
    //================================================================================
    private Pair<BoardSpace, Integer> findLocationFromColor(Color color){
        for (int row = 0; row < BOARD_LENGTH; row++){
            for (int col = 0; col < BOARD_LENGTH; col++){
                BoardSpace boardSpace = getBoardSpace(row, col);
                int tokenSpace = boardSpace.findColor(color);
                if (tokenSpace != -1){
                    return new Pair<BoardSpace, Integer>(boardSpace, tokenSpace);
                }
            }
        }
        return new Pair<BoardSpace, Integer>(null, null);
    }

    private boolean willKillPlayerFromLocation(Tile tile, BoardSpace curSpace, int curTokenSpace){
        try {
            // Move to the space across the tile
            curTokenSpace = tile.findMatch(curTokenSpace);
            curSpace = getNextSpace(curSpace, curTokenSpace);
            curTokenSpace = Token.getMirroredTokenSpace(curTokenSpace);

            // Trace out a path by moving across spaces with tiles on them
            while (curSpace.hasTile()){
                curTokenSpace = curSpace.getTile().findMatch(curTokenSpace);
                curSpace = getNextSpace(curSpace, curTokenSpace);
                curTokenSpace = Token.getMirroredTokenSpace(curTokenSpace);
            }
            // We've walked to a place on the board without a tile
            return false;
        }

        catch (IllegalArgumentException e){
            // We've walked to a place off the board since we're trying to
            //   access a space with an invalid coordinate
            return true;
        }
    }

    // Gets the adjacent space of an arbitrary board space and token space.
    private BoardSpace getNextSpace(BoardSpace boardSpace, int tokenSpace) {
        int row = boardSpace.getRow();
        int col = boardSpace.getCol();
        int direction = tokenSpace / 2;

        if      (direction == 0) row--; // Move up
        else if (direction == 1) col++; // Move right
        else if (direction == 2) row++; // Move down
        else if (direction == 3) col--; // Move left
        else throw new IllegalArgumentException("Illegal value for tokenSpace");

        return getBoardSpace(row, col);
    }

    // Gets the adjacent space that the token is on.
    //  Returns null if token is on the edge
    private BoardSpace getNextSpace(Token token) {
        return getNextSpace(token.getBoardSpace(), token.getTokenSpace());
    }

    // Moves a token from a board space to its adjacent board space.
    //  Assumes the token is not on the edge
    private void transferToken(Token token) {

        int nextTokenSpace = token.findNextTokenSpace();
        BoardSpace nextSpace = getNextSpace(token);
        token.moveToken(nextSpace, nextTokenSpace);
    }

    private void advanceToEnd(Token token){
        BoardSpace curSpace = token.getBoardSpace();

        while (curSpace.hasTile()){
            // Move the token to the other side of the tile
            curSpace.advanceToken(token);

            // Check before we transfer
            if (isOnEdge(token)) {
                break;
            }
            transferToken(token);
            curSpace = token.getBoardSpace();
        }
    }

    // Returns true if the row and col pair are a valid address in the board
    public static boolean isValidCoordinate(int row, int col) {
        return (0 <= row && row < BOARD_LENGTH) && (0 <= col && col < BOARD_LENGTH);
    }

    private static boolean isOnEdge(int row, int col, int tokenSpace) {
        if (!isValidCoordinate(row, col))
            throw new IllegalArgumentException("Invalid Tile Access");

        int direction = tokenSpace / 2;
        boolean topEdge    = row == 0 && direction == 0;
        boolean rightEdge  = col == 5 && direction == 1;
        boolean bottomEdge = row == 5 && direction == 2;
        boolean leftEdge   = col == 0 && direction == 3;

        return topEdge || rightEdge || bottomEdge || leftEdge;
    }

    private static boolean isOnEdge(Token token) {

        int row = token.getBoardSpace().getRow();
        int col = token.getBoardSpace().getCol();
        int tokenSpace = token.getTokenSpace();
        return isOnEdge(row, col, tokenSpace);
    }

    private Element encodeTiles(Document doc) {
        Element tilesElement = doc.createElement("map");

        for (int row = 0; row < BOARD_LENGTH; row++) {
            for (int col = 0; col < BOARD_LENGTH; col++) {
                BoardSpace boardSpace = getBoardSpace(row, col);
                if (boardSpace.hasTile()) {
                    Element entElement = doc.createElement("ent");
                    Element xyNode = encodeXY(doc, row, col);
                    Element tileNode = boardSpace.getTile().encodeTile(doc);

                    entElement.appendChild(xyNode);
                    entElement.appendChild(tileNode);
                    tilesElement.appendChild(entElement);
                }
            }
        }

        return tilesElement;
    }

    private Element encodeXY(Document doc, int x, int y) {
        Element xyElement = doc.createElement("xy");
        Element xNode = doc.createElement("x");
        Element yNode = doc.createElement("y");
        Text xText = doc.createTextNode(String.valueOf(x));
        Text yText = doc.createTextNode(String.valueOf(y));

        xNode.appendChild(xText);
        yNode.appendChild(yText);
        xyElement.appendChild(xNode);
        xyElement.appendChild(yNode);

        return xyElement;
    }
}
