import java.util.ArrayList;

public class LeastSymmetricPlayer extends MPlayer {

    LeastSymmetricPlayer(String name) {
        super(name);
    }

    public LeastSymmetricPlayer clone() throws CloneNotSupportedException{
        LeastSymmetricPlayer clonedPlayer = null;
        try{
            clonedPlayer = (LeastSymmetricPlayer) super.clone();

        }catch(CloneNotSupportedException ignore){}
        clonedPlayer = new LeastSymmetricPlayer(this.getName());
        clonedPlayer.color = Colors.valueOf(this.color.name());
        return clonedPlayer;
    }

    @Override
    public PTile playTurn(Board b, ArrayList<ATile> hand, int n) throws CloneNotSupportedException, RuntimeException {
        if (!nextAction.equals("playTurn")){
            throw new RuntimeException("Contract Violated by Server: The current action should not be playTurn.");
        }
        return super.playTurnSymmetric(b, hand, n, true);
    }

}
