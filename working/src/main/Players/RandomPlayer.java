package main.Players;

//import apple.laf.JRSUIConstants;
import javafx.util.Pair;
import main.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomPlayer extends APlayer {

    //================================================================================
    // Instance variables
    //================================================================================
    private Random random;

    //================================================================================
    // Constructor
    //================================================================================
    public RandomPlayer(String name, Color color){
        super(name, color);
        random = new Random();
    }

    public RandomPlayer(APlayer other){
        super(other);
    }

    //For testing
    public RandomPlayer(String name, Color color, int seed){
        super(name, color);
        random = new Random(seed);
    }



    //================================================================================
    // Override methods
    //================================================================================
    public Pair<BoardSpace, Integer> getStartingLocation(){
        return getRandomStartingLocation(random);
    }

    protected Tile chooseTileHelper() {
        Set<Tile> legalMoves =  getLegalMoves();
        Tile[] legalMovesArr = legalMoves.toArray(new Tile[legalMoves.size()]);
        int randomIndex = random.nextInt(legalMovesArr.length);
        return legalMovesArr[randomIndex];
    }


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
