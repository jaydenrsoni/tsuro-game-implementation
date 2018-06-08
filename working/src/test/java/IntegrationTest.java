import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

public class IntegrationTest {

    private Game game;

    @Before
    public void reset() {
        Game.resetGame();
        game = Game.getGame();
    }

    @Test
    public void playGameTest(){
        IPlayer vyas = new RandomPlayer("Vyas", 0);
        IPlayer keith = new RandomPlayer("Keith", 0);

        game.registerPlayer(vyas);
        game.registerPlayer(keith);

        Assert.assertFalse(game.playGame(0).isEmpty());
    }

    @Test
    public void playManyGamesTest() {
        for(int seed = 0; seed < 100; seed++){
            try {
                Game.resetGame();
                game = Game.getGame();
                game.getTilePile().shuffleDeck(seed);

                IPlayer vyas = new RandomPlayer("Vyas", seed);
                IPlayer keith = new RandomPlayer("Keith", seed);
                IPlayer robby = new RandomPlayer("Robby", seed);

                game.registerPlayer(vyas);
                game.registerPlayer(keith);
                game.registerPlayer(robby);

                Assert.assertFalse(game.playGame(0).isEmpty());
            }
            catch (Exception e){
                System.err.println("Failed with seed " + seed);
                throw e;
            }
        }
    }

    @Test
    public void playNetworkedGameMinPlayersTest() {
        IPlayer keith = new RandomPlayer("keith");
        IPlayer jayden = new RandomPlayer("jayden");


        ClientAdministratorThread keithAdmin = new ClientAdministratorThread(keith);
        keithAdmin.start();
        ClientAdministratorThread jaydenAdmin = new ClientAdministratorThread(jayden);
        jaydenAdmin.start();

        Set<Color> winners = game.playGame(2);
        Assert.assertFalse(winners.isEmpty());
    }

    @Ignore
    @Test
    public void playNetworkedGameMaxPlayersTest() {
        IPlayer keith = new RandomPlayer("keith");
        IPlayer jayden = new RandomPlayer("jayden");
        IPlayer robby = new RandomPlayer("robby");
        IPlayer christos = new RandomPlayer("christos");
        IPlayer steve = new RandomPlayer("steve");
        IPlayer kayla = new RandomPlayer("kayla");
        IPlayer betty = new RandomPlayer("betty");
        IPlayer john = new RandomPlayer("john");


        ClientAdministratorThread keithAdmin = new ClientAdministratorThread(keith);
        keithAdmin.start();
        ClientAdministratorThread jaydenAdmin = new ClientAdministratorThread(jayden);
        jaydenAdmin.start();
        ClientAdministratorThread robbyAdmin = new ClientAdministratorThread(robby);
        robbyAdmin.start();
        ClientAdministratorThread christosAdmin = new ClientAdministratorThread(christos);
        christosAdmin.start();
        ClientAdministratorThread steveAdmin = new ClientAdministratorThread(steve);
        steveAdmin.start();
        ClientAdministratorThread kaylaAdmin = new ClientAdministratorThread(kayla);
        kaylaAdmin.start();
        ClientAdministratorThread bettyAdmin = new ClientAdministratorThread(betty);
        bettyAdmin.start();
        ClientAdministratorThread johnAdmin = new ClientAdministratorThread(john);
        johnAdmin.start();

        Set<Color> winners = game.playGame(8);
        Assert.assertFalse(winners.isEmpty());
    }
}
