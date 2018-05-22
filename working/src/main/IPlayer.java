package main;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public interface IPlayer {

    String getName();

    void initialize(Color playerColor, List<Color> otherPlayerColors);

    Pair<BoardSpace, Integer> placePawn(Board board);

    Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft);

    void endGame(Board board, List<Color> winningColors);
}
