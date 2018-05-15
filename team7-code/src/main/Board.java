import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael on 4/15/2018.
 */
public class Board implements Cloneable{

    Board(){
        pTiles = new PTile[6][6];
    }

    public Board clone() throws CloneNotSupportedException{
        Board b = null;
        try{
            b = (Board)super.clone();
            b.pTiles = new PTile[6][6];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (this.pTiles[i][j] != null){
                        b.pTiles[i][j] = (PTile)this.pTiles[i][j].clone();
                    }
                }
            }
        }catch(CloneNotSupportedException ignore){

        }
        return b;
    }

    private PTile[][] pTiles;
    private int tilesPlayed = 0;
    private Map<Colors, int[]> playerLocations = new HashMap<Colors, int[]>();

    void addTile(PTile pTile, int[] location){
        pTiles[location[0]][location[1]] = pTile;
        tilesPlayed++;
    }

    PTile[][] getPTiles(){
        return pTiles;
    }

    PTile getPTile(int[] loc){return pTiles[loc[0]][loc[1]];}

    boolean isLocationEmpty(int[] loc){
        return pTiles[loc[0]][loc[1]] == null;
    }

    public Map<Colors, int[]> getPlayerLocations() {
        return playerLocations;
    }

    public int[] getPlayerLocation(Colors c){ return playerLocations.get(c); }

    public int getTilesPlayed() {
        return tilesPlayed;
    }



    // add (or update) player location on the board
    public void setPlayerLocation(Colors c, int[] loc) throws RuntimeException{
        if (!playerLocations.containsKey(c) && playerLocations.size() == 8){
            throw new RuntimeException("Cannot add more player than 8!");
        }
        playerLocations.put(c, loc);
    }

    public void removePlayer(Colors c) throws RuntimeException{
        int[] loc = playerLocations.remove(c);
        if (loc == null){
            throw new RuntimeException("Player to be removed does not exist on the board!");
        }
    }

    public boolean isTileOnBoard(ATile aTile) {
        for(PTile[] rows: pTiles){
            for (PTile entry: rows){
                if(aTile.getClass().equals(PTile.class) && ((PTile) aTile).equals(entry)){
                    return true;
                }
            }
        }
        return false;
    }
}
