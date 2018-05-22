package main;

import javafx.util.Pair;

import java.util.List;

public class NetworkPlayer extends APlayer {

    public NetworkPlayer(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void initialize(Color playerColor, List<Color> otherPlayerColors) {
        super.initialize(playerColor, otherPlayerColors);
    }

    @Override
    public Pair<BoardSpace, Integer> placePawn(Board board) {
        return super.placePawn(board);
    }

    @Override
    public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft) {
        return null;
    }

    @Override
    public void endGame(Board board, List<Color> winningColors) {
        super.endGame(board, winningColors);
    }

    @Override
    public Pair<BoardSpace, Integer> getStartingLocation() {
        return null;
    }
}
