package main;

import javafx.util.Pair;

import java.util.*;

public abstract class APlayer {

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
        return getStartingLocation();
    }

    abstract public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft);

    public void endGame(Board board, List<Color> winningColors){
        // Do something?
    }

    //================================================================================
    // Abstract methods
    //================================================================================
    abstract public Pair<BoardSpace, Integer> getStartingLocation();

    //================================================================================
    // Public Static Methods
    //================================================================================

    public static Pair<BoardSpace, Integer> getRandomStartingLocation(Random random){
        Board board = Game.getGame().getBoard();

        int edgeNumber = random.nextInt(4);
        int indexOfEdge = random.nextInt(6);
        int leftOrRightTokenSpace = random.nextInt(2);

        int tokenSpace = edgeNumber * 2 + leftOrRightTokenSpace;

        if(edgeNumber == 0){
            return new Pair<>(board.getBoardSpace(0, indexOfEdge), tokenSpace);
        }
        else if (edgeNumber == 1){
            return new Pair<>(board.getBoardSpace(indexOfEdge, 5), tokenSpace);
        }
        else if (edgeNumber == 2){
            return new Pair<>(board.getBoardSpace(5, indexOfEdge), tokenSpace);
        }
        else{
            return new Pair<>(board.getBoardSpace(indexOfEdge, 0), tokenSpace);
        }
    }


}
