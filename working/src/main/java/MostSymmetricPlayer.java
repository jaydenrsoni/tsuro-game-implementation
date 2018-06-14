import javafx.util.Pair;

import java.util.Random;

/**
 *
 * IPlayer that always plays the most symmetric tile
 *
 */
public class MostSymmetricPlayer extends ScorePlayer {

    public MostSymmetricPlayer(String name) {
        super(name);
    }

    @Override
    public Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        return board.getRandomAvailableStartingLocation(new Random());
    }

    // Order tiles from most to least symmetric, and choose the first legal rotation among them
    protected double ScoreTile(Tile tile, Board board){
        return tile.calculateSymmetries();
    }
}
