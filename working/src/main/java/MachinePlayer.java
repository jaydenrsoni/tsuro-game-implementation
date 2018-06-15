import javafx.util.Pair;

import java.util.Random;

/**
 *
 * Machine player for class tournament.
 *
 */
public class MachinePlayer extends ScorePlayer {

    public MachinePlayer(String name) {
        super(name);
    }

    @Override
    public Pair<BoardSpace, Integer> getStartingLocation(Board board) {
        return board.getIntelligentAvailableStartingLocation(new Random());
    }

    // Order tiles from most to least symmetric, and choose the first legal rotation among them
    protected double ScoreTile(Tile tile, Board board){
        double symmetryScore = tile.calculateSymmetries(); // between 1/4 and 1
        int numberDefeatedOpponents = board.calculateDefeatedOpponents(this, tile); // between 0 and numRemPlayers

        // some linear combination of a number of scores
        return symmetryScore + numberDefeatedOpponents;
    }
}
