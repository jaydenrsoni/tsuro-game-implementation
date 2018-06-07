import javafx.util.Pair;

import java.util.*;


/**
 *
 * Abstract implementation of machine IPlayer
 *
 */
public abstract class APlayer implements IPlayer {

    //================================================================================
    // Instance Variables
    //================================================================================

    private String name;
    private Color color;
    private List<Color> otherPlayers;

    //================================================================================
    // Constructor
    //================================================================================

    public APlayer(String name){
        this.name = name;
    }

    //================================================================================
    // Getters
    //================================================================================

    public Color getColor() {
        return color;
    }

    public List<Color> getOtherPlayers() {
        return otherPlayers;
    }

    public String getName(){
        return name;
    }

    //================================================================================
    // Public Methods
    //================================================================================

    public void initialize(Color playerColor, List<Color> otherPlayerColors){
        this.color = playerColor;
        this.otherPlayers = new ArrayList<>(otherPlayerColors);
    }

    public Pair<BoardSpace, Integer> placePawn(Board board) {
        return getStartingLocation(board);
    }

    public void endGame(Board board, Set<Color> winningColors){
        //System.out.println("Winning Colors: " + winningColors.toString());
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    abstract public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft);

    abstract public Pair<BoardSpace, Integer> getStartingLocation(Board board);


}
