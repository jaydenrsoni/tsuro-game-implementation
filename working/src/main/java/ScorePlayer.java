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
        double bestScore = -Double.MAX_VALUE;

        for (Tile tile: legalMoves){
            double curScore = ScoreTile(tile, board);
            if (curScore > bestScore){
                bestScore = curScore;
                bestTile = tile;
            }
        }

        return bestTile;
    }

    abstract protected double ScoreTile(Tile tile, Board board);
}
