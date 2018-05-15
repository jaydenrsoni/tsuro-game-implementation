/**
 * Created by Michael on 4/15/2018.
 */
import java.util.ArrayList;

public class SPlayer {
    private int[] marker;
    private Colors color;
    private ArrayList<ATile> hand = new ArrayList<ATile>(3);
    MPlayer player;

    private SPlayer(){}

    SPlayer(int[] startLocation, MPlayer player){
        this.player = player;
        this.color = Colors.valueOf(player.getColor().name());
        this.marker = startLocation;
    }

    /*
    player starts with a color for the marker and a location on the board
     */
    SPlayer(int[] startLocation, Colors color){
        marker = startLocation;
        this.color = color;
    }

    public SPlayer clone() throws CloneNotSupportedException {
        SPlayer p = new SPlayer();
        try{
            p = (SPlayer) super.clone();
        }catch(CloneNotSupportedException ignore){ }

        p.color = Colors.valueOf(this.color.name());
        p.marker = marker.clone();
        p.hand = new ArrayList<ATile>(3);
        for (int i = 0; i < this.hand.size(); i++) {
            p.hand.add(i, (ATile)this.getHand().get(i).clone());
        }
        if (this.player != null) {
            p.player = this.player.clone();
        }
        return p;
    }

    public Colors getColor(){
        return color;
    }

    public int[] getLocation(){
        return marker;
    }

    public ArrayList<ATile> getHand(){
        return hand;
    }

    public void drawTile(ArrayList<ATile> deck){
        addTileToHand(deck.remove(0));
    }

    public void addTileToHand(ATile t) {
        hand.add(t);
    }

    public void removeTileFromHand(ATile t) {
        if (!hand.remove(t)){
            throw new RuntimeException("No such tile found in hand, illegal play.");
        }
    }

    public int[] getNextTileLocation() {
        return new int[] {marker[0], marker[1]};
    }

    /*
    Given a board, moves marker to new location and checks whether current player is still on the board.
     */
    public boolean movePlayer_inGame(Board b) {
        int[] loc = getLocation();

        while(!b.isLocationEmpty(loc)){
            // update location
            PTile nextTile = b.getPTile(loc);

            loc[2] = nextTile.getNextMarkerLocation(loc[2]);

            if(loc[2] < 2) loc[1]--;
            else if(loc[2] < 4) loc[0]--;
            else if(loc[2] < 6) loc[1]++;
            else loc[0]++;

            // check if still on the board, if not, return false
            if(loc[0] < 0 || loc[0] > 5 || loc[1] < 0 || loc[1] > 5){

                return false;
            }
        }
        b.setPlayerLocation(getColor(), loc);
        return true;
    }


}
