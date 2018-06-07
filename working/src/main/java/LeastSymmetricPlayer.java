import javafx.util.Pair;

import java.util.Random;


/**
 * Created by vyasalwar on 4/30/18.
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
    protected int ScoreTile(Tile tile){
        return -tile.calculateSymmetries();
    }

}
