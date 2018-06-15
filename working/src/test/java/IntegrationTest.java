import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
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
        int blueWins = 0;
        int redWins = 0;
        int greenWins = 0;

        for(int seed = 0; seed < 10000; seed++){
            try {
                Game.resetGame();
                game = Game.getGame();
                game.getTilePile().shuffleDeck(seed);

                IPlayer vyas = new RandomPlayer("Vyas", seed); //blue
                IPlayer keith = new MostSymmetricPlayer("Keith"); //red
                IPlayer jayden = new MachinePlayer("Jayden"); //green

                game.registerPlayer(vyas);
                game.registerPlayer(keith);
                game.registerPlayer(jayden);

                Set<Color> winners = game.playGame(0);
                Assert.assertFalse(winners.isEmpty());

                for (Color winner: winners) {
                    switch (winner.ordinal()) {
                        case 0:
                            blueWins++;
                            break;
                        case 1:
                            redWins++;
                            break;
                        case 2:
                            greenWins++;
                            break;
                    }
                }
            }
            catch (Exception e){
                System.err.println("Failed with seed " + seed);
                throw e;
            }
        }
        System.out.println("random: " + blueWins + " " + "mostSym: " + redWins + " " + "machine: " + greenWins + " ");
    }

    @Ignore
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
