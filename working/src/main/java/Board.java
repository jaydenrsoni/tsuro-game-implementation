import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    // Instance Variables
    //================================================================================
    private BoardSpace[][] spaces;
    private final static int BOARD_LENGTH = 6;

    //================================================================================
    // Constructors
    //================================================================================
    public Board() {
        initializeSpaces();
    }

    public Board(Node boardNode) {
        initializeSpaces();

        NodeList boardNodeChildren = boardNode.getChildNodes();
        NodeList tileNodes = boardNodeChildren.item(0).getChildNodes();
        NodeList pawnNodes = boardNodeChildren.item(1).getChildNodes();

        for (int i = 0; i < tileNodes.getLength(); i++) {
            addTileToBoard(tileNodes.item(i));
        }

        for (int i = 0; i < pawnNodes.getLength(); i++) {
            addPawnToBoard(pawnNodes.item(i));
        }
    }

    //================================================================================
    // Getters
    //================================================================================
    public BoardSpace getBoardSpace(int row, int col) {
        if (isInvalidCoordinate(row, col))
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
        return willKillToken(tile, splayer.getToken());
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
            }
        }

        return eliminatedPlayers;
    }

    public List<Tile> findLegalMovesForPlayer(List<Tile> hand, Color color){
        List<Tile> legalMoves = new ArrayList<>();
        Token token = findTokenFromColor(color);
        boolean hasSafeMoves = hasSafeMove(hand, token);

        for (Tile tile: hand){
            for (int rotation = 0; rotation < 4; rotation++){
                if (!hasSafeMoves || !willKillToken(tile, token))
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

    public Token findTokenFromColor(Color color) {
        for (int row = 0; row < BOARD_LENGTH; row++){
            for (int col = 0; col < BOARD_LENGTH; col++){
                BoardSpace boardSpace = getBoardSpace(row, col);
                Token token = boardSpace.findColor(color);
                if (token != null){
                    return token;
                }
            }
        }
        throw new IllegalArgumentException("Color provided is not playing the game dead or alive");
    }

    //================================================================================
    // Private Helpers
    //================================================================================
    private void initializeSpaces() {
        this.spaces = new BoardSpace[BOARD_LENGTH][BOARD_LENGTH];
        for (int i = 0; i < BOARD_LENGTH; i++) {
            for (int j = 0; j < BOARD_LENGTH; j++) {
                spaces[i][j] = new BoardSpace(i, j);
            }
        }
    }

    private boolean willKillToken(Tile tile, Token token){
        BoardSpace curSpace = token.getBoardSpace();
        int curTokenSpace = token.getTokenSpace();

        // Trace out a path by moving across spaces with tiles on them
        while (curSpace.hasTile() || curSpace == token.getBoardSpace()){
            Tile curTile = getCurTile(curSpace, tile, token);

            curTokenSpace = curTile.findMatch(curTokenSpace);
            if (isOnEdge(curSpace.getRow(), curSpace.getCol(), curTokenSpace))
                return true;

            curSpace = getNextSpace(curSpace, curTokenSpace);
            curTokenSpace = Token.getMirroredTokenSpace(curTokenSpace);
        }
        // We've walked to a place on the board without a tile
        return false;
    }

    private Tile getCurTile(BoardSpace space, Tile originalTile, Token token) {
        if(space == token.getBoardSpace())
            return originalTile;
        return space.getTile();
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

    // Moves a token from a board space to its adjacent board space
    private void transferToken(Token token) {
        int nextTokenSpace = token.findNextTokenSpace();
        BoardSpace nextSpace = getNextSpace(token.getBoardSpace(), token.getTokenSpace());
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

    private boolean hasSafeMove(List<Tile> hand, Token token){
        for (Tile tile: hand){
            Tile copy = new Tile(tile);
            for (int i = 0; i < 4; i++){
                copy.rotateClockwise();
                if (!willKillToken(copy, token))
                    return true;
            }
        }
        return false;
    }

    //================================================================================
    // XML Helpers
    //================================================================================
    public Element encodeBoard(Document doc) {
        Element boardElement = doc.createElement("board");
        Element tilesNode = encodeTiles(doc);
        Element pawnsNode = encodePawns(doc);

        boardElement.appendChild(tilesNode);
        boardElement.appendChild(pawnsNode);

        return boardElement;
    }

    private Element encodeTiles(Document doc) {
        Element tilesElement = doc.createElement("map");

        Text tilesText = doc.createTextNode(" ");
        tilesElement.appendChild(tilesText);

        for (int row = 0; row < BOARD_LENGTH; row++) {
            for (int col = 0; col < BOARD_LENGTH; col++) {
                BoardSpace boardSpace = getBoardSpace(row, col);
                if (boardSpace.hasTile()) {
                    Element entElement = doc.createElement("ent");
                    Element xyNode = encodeXY(doc, col, row);
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

    private Element encodePawns(Document doc) {
        Element pawnsElement = doc.createElement("map");

        Text pawnsText = doc.createTextNode(" ");
        pawnsElement.appendChild(pawnsText);

        for (int row = 0; row < BOARD_LENGTH; row++) {
            for (int col = 0; col < BOARD_LENGTH; col++) {
                BoardSpace boardSpace = getBoardSpace(row, col);
                if (boardSpace.hasToken()) {
                    Set<Token> tokens = boardSpace.getTokensOnSpace();
                    for(Token token : tokens){
                        Element entElement = doc.createElement("ent");
                        Element colorNode = token.getColor().encodeColor(doc);
                        Element pawnLocNode = token.encodePawnLoc(doc);

                        entElement.appendChild(colorNode);
                        entElement.appendChild(pawnLocNode);
                        pawnsElement.appendChild(entElement);
                    }
                }
            }
        }

        return pawnsElement;
    }

    private void addPawnToBoard(Node pawnsEntryNode) {
        NodeList pawnsEntryNodeChildren = pawnsEntryNode.getChildNodes();
        Color pawnColor = Color.decodeColor(pawnsEntryNodeChildren.item(0));
        //the new token will add itself to the boardspace that is decoded, adding it to the board
        new Token(this, pawnColor, pawnsEntryNodeChildren.item(1));

    }

    private void addTileToBoard(Node tilesEntryNode) {
        NodeList tilesEntryNodeChildren = tilesEntryNode.getChildNodes();
        BoardSpace boardSpace = boardSpaceFromXYNode(tilesEntryNodeChildren.item(0));
        Tile tile = new Tile(tilesEntryNodeChildren.item(1));
        boardSpace.setTile(tile);
    }

    private BoardSpace boardSpaceFromXYNode(Node XYNode) {
        NodeList XYNodeChildren = XYNode.getChildNodes();
        int row = Integer.parseInt(XYNodeChildren.item(1).getTextContent());
        int col = Integer.parseInt(XYNodeChildren.item(0).getTextContent());
        return getBoardSpace(row, col);
    }

    //================================================================================
    // Static Functions
    //================================================================================
    // Returns true if the row and col pair are a valid address in the board
    public static boolean isInvalidCoordinate(int row, int col) {
        return (row < 0 || row >= BOARD_LENGTH) || (col < 0 || col >= BOARD_LENGTH);
    }

    public static boolean isOnEdge(Token token) {

        int row = token.getBoardSpace().getRow();
        int col = token.getBoardSpace().getCol();
        int tokenSpace = token.getTokenSpace();
        return isOnEdge(row, col, tokenSpace);
    }

    private static boolean isOnEdge(int row, int col, int tokenSpace) {
        if (isInvalidCoordinate(row, col))
            throw new IllegalArgumentException("Invalid Tile Access");

        int direction = tokenSpace / 2;
        boolean topEdge    = row == 0 && direction == 0;
        boolean rightEdge  = col == 5 && direction == 1;
        boolean bottomEdge = row == 5 && direction == 2;
        boolean leftEdge   = col == 0 && direction == 3;

        return topEdge || rightEdge || bottomEdge || leftEdge;
    }
}
