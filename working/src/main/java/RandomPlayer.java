import javafx.util.Pair;

import java.util.List;
import java.util.Random;

/**
 *
 * IPlayer that picks tiles to play at random
 *
 */
public class RandomPlayer extends APlayer {

    //================================================================================
    // Instance variables
    //================================================================================

    private Random random;

    //================================================================================
    // Constructors
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
    // Public Methods
    //================================================================================

    @Override
    public Pair<BoardSpace, Integer> getStartingLocation(Board board){
        return board.getRandomAvailableStartingLocation(random);
    }

    @Override
    public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft) {
        List<Tile> legalMoves =  board.findLegalMovesForPlayer(hand, getColor());
        Tile[] legalMovesArr = legalMoves.toArray(new Tile[legalMoves.size()]);
        int randomIndex = random.nextInt(legalMovesArr.length);
        return legalMovesArr[randomIndex];
    }



}
