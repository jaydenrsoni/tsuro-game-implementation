package main;

import javafx.util.Pair;
import org.w3c.dom.*;

/**
 * Created by vyasalwar on 4/16/18.
 */
public class Token {

    //================================================================================
    // Instance Variables
    //================================================================================
    private BoardSpace space;
    private SPlayer player;
    private Color color;

    //================================================================================
    // Constructor
    //================================================================================
    public Token(BoardSpace startingLocation, int startingTokenSpace, SPlayer player, Color color){
        this.space = startingLocation;
        this.player = player;
        space.addToken(this, startingTokenSpace);
        this.color = color;
    }

    public Token(Board board, SPlayer player, Color color, Node pawnLocNode){
        this.player = player;
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

    public SPlayer getPlayer(){
        return player;
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

    public Element encodePawnLoc(Document doc) {
        int tokenSpace = getTokenSpace();
        int row = getBoardSpace().getRow();
        int col = getBoardSpace().getCol();

        Element pawnLocElement = doc.createElement("pawn-loc");
        Element hvNode = encodeHv(doc, tokenSpace);
        Element n1Node = encodeN1(doc, tokenSpace, row, col);
        Element n2Node = encodeN2(doc, tokenSpace, row, col);


        pawnLocElement.appendChild(hvNode);
        pawnLocElement.appendChild(n1Node);
        pawnLocElement.appendChild(n2Node);

        return pawnLocElement;
    }

    //================================================================================
    // Private helper methods
    //================================================================================

    private Element encodeHv(Document doc, int tokenSpace) {
        Element hvNode;
        switch(tokenSpace){
            case 0:
            case 1:
            case 4:
            case 5:
                hvNode = doc.createElement("h");
                break;
            case 2:
            case 3:
            case 6:
            case 7:
                hvNode = doc.createElement("v");
                break;
            default:
                throw new IllegalArgumentException("invalid tokenSpace");
        }

        Text hvText = doc.createTextNode(" ");
        hvNode.appendChild(hvText);

        return hvNode;
    }

    private Element encodeN1(Document doc, int tokenSpace, int row, int col) {
        Element n1Node = doc.createElement("n");
        Text n1Text;
        switch(tokenSpace) {
            case 0:
            case 1:
                n1Text = doc.createTextNode(String.valueOf(row));
                break;
            case 2:
            case 3:
                n1Text = doc.createTextNode(String.valueOf(col + 1));
                break;
            case 4:
            case 5:
                n1Text = doc.createTextNode(String.valueOf(row + 1));
                break;
            case 6:
            case 7:
                n1Text = doc.createTextNode(String.valueOf(col));
                break;
            default:
                throw new IllegalArgumentException("invalid tokenSpace");
        }

        n1Node.appendChild(n1Text);
        return n1Node;
    }

    private Element encodeN2(Document doc, int tokenSpace, int row, int col) {
        Element n2Node = doc.createElement("n");
        Text n2Text;
        switch (tokenSpace) {
            case 0:
            case 5:
                n2Text = doc.createTextNode(String.valueOf(col*2));
                break;
            case 1:
            case 4:
                n2Text = doc.createTextNode(String.valueOf(col*2 + 1));
                break;
            case 2:
            case 7:
                n2Text = doc.createTextNode(String.valueOf(row*2));
                break;
            case 3:
            case 6:
                n2Text = doc.createTextNode(String.valueOf(row*2 + 1));
                break;
            default:
                throw new IllegalArgumentException("invalid tokenSpace");
        }

        n2Node.appendChild(n2Text);
        return n2Node;
    }

}
