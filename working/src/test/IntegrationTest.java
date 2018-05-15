package test;

import main.Color;
import main.Game;
import main.Players.APlayer;
import main.Players.RandomPlayer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTest {

    Game game;

    @Before
    public void reset() {
        Game.resetGame();
        game = Game.getGame();
    }

    @Test
    public void playGameTest(){
        APlayer vyas = new RandomPlayer("Vyas", Color.BLUE, 0);
        APlayer keith = new RandomPlayer("Keith", Color.BLACK, 0);

        game.registerPlayer(vyas);
        game.registerPlayer(keith);

        Assert.assertFalse(game.playGame().isEmpty());
    }

    @Test
    public void playManyGamesTest() {
        for(int seed = 0; seed < 10000; seed++){
            try {
                game.resetGame();
                game = Game.getGame();
                game.getTilePile().shuffleDeck(seed);

                APlayer vyas = new RandomPlayer("Vyas", Color.BLUE, seed);
                APlayer keith = new RandomPlayer("Keith", Color.BLACK, seed);
                APlayer robby = new RandomPlayer("Robby", Color.GREY, seed);

                game.registerPlayer(vyas);
                game.registerPlayer(keith);
                game.registerPlayer(robby);

                Assert.assertFalse(game.playGame().isEmpty());
            }
            catch (Exception e){
                System.err.println("Failed with seed " + seed);
                throw e;
            }
        }
    }
}
