import java.util.ArrayList;

/**
 * Created by Michael on 4/17/2018.
 */
public class Turn {

    Turn(ArrayList<ATile> pile, ArrayList<SPlayer> aPlayers, ArrayList<SPlayer> ePlayers, Board b){
        drawPile = pile;
        activePlayers = aPlayers;
        eliminatedPlayers = ePlayers;
        gameBoard = b;
    }

    private ArrayList<ATile> drawPile = new ArrayList<ATile>(36);
    private ArrayList<SPlayer> activePlayers = new ArrayList<SPlayer>(8);
    private ArrayList<SPlayer> eliminatedPlayers = new ArrayList<SPlayer>(8);
    private Board gameBoard = new Board();
    private boolean isDone = false;
    private ArrayList<SPlayer> winners = new ArrayList<SPlayer>(8);

    public ArrayList<ATile> getDrawPile() {
        return drawPile;
    }

    public ArrayList<SPlayer> getActivePlayers() {
        return activePlayers;
    }

    public ArrayList<SPlayer> getEliminatedPlayers() {
        return eliminatedPlayers;
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public ArrayList<SPlayer> getWinners() {
        return winners;
    }

    // returns null if dragon tile not in play
    public SPlayer dragonPlayer() {
        for (SPlayer player : getActivePlayers()) {
            ArrayList<ATile> playerHand = player.getHand();
            for (ATile aPlayerHand : playerHand) {
                if (aPlayerHand.getClass().equals(DTile.class)) {
                    return player;
                }
            }
        }
        return null;
    }
}
