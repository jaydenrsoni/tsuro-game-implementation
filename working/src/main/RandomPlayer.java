package main;

//import apple.laf.JRSUIConstants;
import javafx.util.Pair;

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
    public RandomPlayer(String name){
        super(name);
        random = new Random();
    }

    //For testing
    public RandomPlayer(String name, int seed){
        super(name);
        random = new Random(seed);
    }



    //================================================================================
    // Override methods
    //================================================================================
    public Pair<BoardSpace, Integer> getStartingLocation(){
        return getRandomStartingLocation(random);
    }

    @Override
    public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft) {
        List<Tile> legalMoves =  board.findLegalMovesForPlayer(hand, getColor());
        Tile[] legalMovesArr = legalMoves.toArray(new Tile[legalMoves.size()]);
        int randomIndex = random.nextInt(legalMovesArr.length);
        return legalMovesArr[randomIndex];
    }



}
