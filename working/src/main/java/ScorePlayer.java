import java.util.List;
import java.util.Set;

/**
 * Created by vyasalwar on 5/3/18.
 */
public abstract class ScorePlayer extends APlayer {

    public ScorePlayer(String name){
        super(name);
    }

    @Override
    public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft){
        List<Tile> legalMoves = board.findLegalMovesForPlayer(hand, getColor());
        Tile bestTile = null;
        int bestScore = Integer.MIN_VALUE;

        for (Tile tile: legalMoves){
            int curScore = ScoreTile(tile);
            if (curScore > bestScore){
                bestScore = curScore;
                bestTile = tile;
            }
        }

        return bestTile;
    }

    abstract protected int ScoreTile(Tile tile);
}
