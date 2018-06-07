import javafx.util.Pair;

import java.util.*;

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
    // Public methods
    //================================================================================
    public void initialize(Color playerColor, List<Color> otherPlayerColors){
        this.color = playerColor;
        this.otherPlayers = new ArrayList<>(otherPlayerColors);
    }

    public Pair<BoardSpace, Integer> placePawn(Board board) {
        return getStartingLocation(board);
    }

    abstract public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft);

    public void endGame(Board board, Set<Color> winningColors){
        // Do something?
    }

    //================================================================================
    // Abstract methods
    //================================================================================
    abstract public Pair<BoardSpace, Integer> getStartingLocation(Board board);

    //================================================================================
    // Public Static Methods
    //================================================================================


}
