package main.Players;

import javafx.util.Pair;
import main.*;

import java.util.Random;

/**
 * Created by vyasalwar on 4/30/18.
 */
public class MostSymmetricPlayer extends ScorePlayer {
    public MostSymmetricPlayer(String name, Color color) {
        super(name, color);
    }

    // Get random starting location
    public Pair<BoardSpace, Integer> getStartingLocation() {
        return RandomPlayer.getRandomStartingLocation(new Random());
    }

    // Order tiles from least to most symmetric, and choose the first legal rotation among them
    protected int ScoreTile(Tile tile){
        return tile.calculateSymmetries();
    }
}
