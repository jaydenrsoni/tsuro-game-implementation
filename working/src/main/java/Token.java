import javafx.util.Pair;
import org.w3c.dom.*;

/**
 *
 * Holds color and location data of a Tsuro pawn
 *
 */
public class Token {

    //================================================================================
    // Instance Variables
    //================================================================================

    private BoardSpace space;
    private Color color;

    //================================================================================
    // Constructor
    //================================================================================

    public Token(BoardSpace startingLocation, int startingTokenSpace, Color color){
        this.space = startingLocation;
        space.addToken(this, startingTokenSpace);
        this.color = color;
    }

    public Token(Board board, Color color, Node pawnLocNode){
        this.color = color;

        Pair<BoardSpace, Integer> location = NetworkAdapter.decodePawnLocNode(board, pawnLocNode);
        space = location.getKey();
        space.addToken(this, location.getValue());
    }

    //================================================================================
    // Getters
    //================================================================================

    public BoardSpace getBoardSpace(){
        return space;
    }

    public int getTokenSpace(){
        return space.findToken(this);
    }

    public Color getColor() {
        return color;
    }

    //================================================================================
    // Public Methods
    //================================================================================

    // Removes the token from the board altogether
    //   Should only be called when a player loses
    public void removeFromBoard(){
        space.removeToken(this);
        space = null;
    }

    // Places the token at the given location
    public void moveToken(BoardSpace boardSpace, int tokenSpace){
        space.removeToken(this);
        boardSpace.addToken(this, tokenSpace);
        space = boardSpace;
    }

    // Gets the tokenSpace on the adjacent tile bordering
    public int findNextTokenSpace(){
        int tokenSpace = getTokenSpace();
        return getMirroredTokenSpace(tokenSpace);
    }

    public static int getMirroredTokenSpace(int tokenSpace){
        switch (tokenSpace){
            case 0: return 5;
            case 1: return 4;
            case 2: return 7;
            case 3: return 6;
            case 4: return 1;
            case 5: return 0;
            case 6: return 3;
            case 7: return 2;
        }
        throw new IllegalArgumentException("Invalid tokenSpace");
    }

    //================================================================================
    // XML Helper
    //================================================================================

    public Element encodePawnLoc(Document doc) {
        return NetworkAdapter.encodePawnLoc(doc, space, getTokenSpace());
    }
}
