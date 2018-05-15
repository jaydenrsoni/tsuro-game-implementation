import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Michael on 4/15/2018.
 */
public class PTile extends ATile implements Comparable<PTile> {

    private int[][] pattern;

    /*
    input p must be a 4*2 array of pairs of integers between 0-7
     */
    PTile(int[][] p) throws CloneNotSupportedException, RuntimeException{
        if (p.length != 4 && p[1].length != 2) {
            throw new RuntimeException("PTile pattern must be a 4x2 array");
        }

        Set<Integer> ports = new HashSet<Integer>();
        for (int[] path: p){
            for (int port:path){
                ports.add(port);
            }
        }
        for (int i = 0; i < 8; i++){
            if (!ports.contains(i)){
                throw new RuntimeException("Invalid PTile port pattern!");
            }
        }

        pattern = p;
        symmetry = 1.0 / (double) waysToPlay(); // more ways to play = lower symmetry, 1 way to play = perfect symmetry
    }

    @Override
    public PTile clone() throws CloneNotSupportedException{
        PTile t = null;
        try{
            t = (PTile) super.clone();
            t.pattern = new int[4][2];
            for (int i = 0; i < 4; i++){
                for (int j = 0; j < 2; j++){
                    t.pattern[i][j] = this.pattern[i][j];
                }
            }
        }catch(CloneNotSupportedException ignore){

        }
        return t;
    }

    @Override
    public boolean equals(Object o){
        boolean eq = false;
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;

        PTile tile = (PTile) o;

        // return true if the two patterns are possible rotations of each other
        for (int i = 0; i < 4; i++){
            if (compareTilePatterns(tile)) eq = true;
            tile.rotate();
        }
        return eq;
    }

    /*
    rotates the tile four times, no need to make a copy first
     */
    private int waysToPlay() throws CloneNotSupportedException{
        ArrayList<PTile> distinctPlays = new ArrayList<PTile>();
        for (int i = 0; i < 4; i++) {
            boolean newPlay = true;
            for (PTile wayToPlay : distinctPlays) {
                if (this.compareTilePatterns(wayToPlay)) {
                    newPlay = false;
                    break;
                }
            }
            if (newPlay) distinctPlays.add(this.clone());
            this.rotate();
        }
        return distinctPlays.size();
    }

    private int find_end_of_path(int start) throws RuntimeException {
        int end = -1;
        for (int[] pair : this.getPattern()) {
            if (start == pair[0]){
                end = pair[1];
                break;
            }
            else if(start == pair[1]){
                end = pair[0];
                break;
            }
        }
        if (end == -1) {
            throw new RuntimeException("Starting point on tile must be between 0-7");
        }
        return end;
    }

    private boolean compareTilePatterns(PTile other) {
        for (int i = 0; i < 8; i++) {
            if (this.find_end_of_path(i) != other.find_end_of_path(i)) {
                return false;
            }
        }
        return true;
    }

    public int[][] getPattern() {
        return pattern;
    }

    /*
    Rotate this tile clockwise
     */
    public void rotate(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 2; j++){
                pattern[i][j] = (pattern[i][j] + 2) % 8;
            }
        }
    }

    public int getNextMarkerLocation(int currLocation) throws RuntimeException {
        int nextMarkerLocationOnCurrTile = -1;
        for (int i = 0; i < 4; i++){
            int[] path = pattern[i];
            if (currLocation == path[0]){
                nextMarkerLocationOnCurrTile = path[1];
                break;
            }
            else if(currLocation == path[1]){
                nextMarkerLocationOnCurrTile = path[0];
                break;
            }
        }
        switch (nextMarkerLocationOnCurrTile) {
            case 0: return 5;
            case 1: return 4;
            case 2: return 7;
            case 3: return 6;
            case 4: return 1;
            case 5: return 0;
            case 6: return 3;
            case 7: return 2;
        }
        throw new RuntimeException("Current location not found in the paths on the tile.");
    }

    public int compareTo(PTile other) {
        if ((this.getSymmetry() - other.getSymmetry()) >= 0) {
            return 1;
        }
        else {
            return -1;
        }
    }

}
