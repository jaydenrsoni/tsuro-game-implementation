//import org.omg.PortableServer.THREAD_POLICY_ID;

import java.util.*;

/**
 *
 * Created by vyasalwar on 4/16/18.
 */
public class BoardSpace {

    private final int NUM_SPACES = 8;

    //================================================================================
    // Instance variables
    //================================================================================
    private Tile tile;
    private Map<Token, Integer> tokenSpaces;
    private int row;
    private int col;

    //================================================================================
    // Constructors
    //================================================================================
    public BoardSpace(int row, int col){
        tile = null;
        tokenSpaces = new HashMap<>();
        this.row = row;
        this.col = col;

        if (Board.isInvalidCoordinate(row, col)){
            throw new IllegalArgumentException("Invalid Tile Access");
        }
    }


    //================================================================================
    // Getters
    //================================================================================

    public Tile getTile(){
        return tile;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public Set<Token> getTokensOnSpace(){
        return new HashSet<>(tokenSpaces.keySet());
    }

    //================================================================================
    // Setters
    //================================================================================

    public void setTile(Tile tile){
        if (!hasTile())
            this.tile = tile;
        else
            throw new IllegalArgumentException("BoardSpace already occupied");
    }

    //================================================================================
    // Public Methods
    //================================================================================

    public boolean hasTile(){
        return tile != null;
    }

    public boolean hasToken() {
        return !tokenSpaces.isEmpty();
    }

    // Move all tokens on the tile to their opposite endpoints
    public void advanceTokens(){
        Set<Token> tokensOnSpace = getTokensOnSpace();
        for (Token token: tokensOnSpace){
            advanceToken(token);
        }
    }

    // Move
    public void advanceToken(Token token){
        if (hasTile()) {
            int endpoint = findToken(token);
            tokenSpaces.replace(token, tile.findMatch(endpoint));
        }
    }

    // Returns the token space the the token is on.
    //   If the token is not on the space, return -1
    public int findToken(Token token) {
        return tokenSpaces.getOrDefault(token, -1);
    }

    public Token findColor(Color color){
        for (Token token : tokenSpaces.keySet()){
            if(token.getColor() == color){
                return token;
            }
        }

        return null;
    }

    public int removeToken(Token token){
        return tokenSpaces.remove(token);
    }

    public void addToken(Token token, int tokenSpace){
        if (0 <= tokenSpace && tokenSpace < 8)
            tokenSpaces.put(token, tokenSpace);

        else
            throw new IllegalArgumentException("Invalid token space");
    }


}
