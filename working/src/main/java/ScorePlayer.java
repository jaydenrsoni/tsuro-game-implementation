import java.util.List;


/**
 *
 * Abstract implementation of an IPlayer that selects tiles to play based on some score
 *
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
