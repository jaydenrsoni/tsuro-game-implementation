import javafx.util.Pair;

import java.util.Random;


/**
 *
 * IPlayer that always plays the least symmetric tile
 *
 */
public class LeastSymmetricPlayer extends ScorePlayer {

    public LeastSymmetricPlayer(String name) {
        super(name);
    }

    @Override
    public Pair<BoardSpace, Integer> getStartingLocation(Board board){
        return board.getRandomAvailableStartingLocation(new Random());
    }

    // Order tiles from least to most symmetric, and choose the first legal rotation among them
    protected double ScoreTile(Tile tile, Board board){
        return -tile.calculateSymmetries();
    }

}
