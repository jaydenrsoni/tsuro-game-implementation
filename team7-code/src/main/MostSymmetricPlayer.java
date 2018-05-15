import java.util.ArrayList;

public class MostSymmetricPlayer extends MPlayer {

    MostSymmetricPlayer(String name) {
        super(name);
    }

    public MostSymmetricPlayer clone() throws CloneNotSupportedException{
        MostSymmetricPlayer clonedPlayer = null;
        try{
            clonedPlayer = (MostSymmetricPlayer) super.clone();

        }catch(CloneNotSupportedException ignore){}
        clonedPlayer = new MostSymmetricPlayer(this.getName());
        clonedPlayer.color = Colors.valueOf(this.color.name());
        return clonedPlayer;
    }

    @Override
    public PTile playTurn(Board b, ArrayList<ATile> hand, int n) throws CloneNotSupportedException, RuntimeException {
        if (!nextAction.equals("playTurn")){
            throw new RuntimeException("Contract Violated by Server: The current action should not be playTurn.");
        }
        return super.playTurnSymmetric(b, hand, n, false);
    }

}
