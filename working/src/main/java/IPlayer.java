import javafx.util.Pair;
import java.util.List;
import java.util.Set;

public interface IPlayer {

    String getName();

    void initialize(Color playerColor, List<Color> otherPlayerColors);

    Pair<BoardSpace, Integer> placePawn(Board board);

    Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft);

    void endGame(Board board, Set<Color> winningColors);
}
