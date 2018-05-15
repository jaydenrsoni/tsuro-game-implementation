import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;

public abstract class MPlayer implements IPlayer {

    private String playerName;
    Colors color;

    String nextAction = "initialize";

    MPlayer(String name){
        playerName = name;
    }

    public MPlayer clone() throws CloneNotSupportedException {
        MPlayer p = null;
        try{
            p = (MPlayer) super.clone();
        }catch(CloneNotSupportedException ignore){

        }
        return p;
    }

    @Override
    public Colors getColor() {
        return color;
    }

    public String getName(){
        return playerName;
    }

    public String getNextAction() {
        return nextAction;
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    // assign color to player, add color to all colors, remove color from available colors
    public void initialize(Colors thisColor, ArrayList<Colors> allColor) throws RuntimeException{
        if (!nextAction.equals("initialize")){
            throw new RuntimeException("Contract Violated by Server: The current action should not be initialize.");
        }
        else{
            nextAction = "placePawn";
        }
        if (allColor.contains(thisColor)){
            throw new RuntimeException("The color assigned to player is not available!");
        }
        if (this.color != null){
            throw new RuntimeException("Player has already been initialized!");
        }
        color = thisColor;
        allColor.add(thisColor);
    }

    // choose and remove first color from the list by default
    public Colors chooseColor(ArrayList<Colors> availableColors){
        return Colors.valueOf(availableColors.remove(0).name());
    }

    // does not update marker in this implementation, random by default
    public int[] placePawn(Board b){
        if (!nextAction.equals("placePawn")){
            throw new RuntimeException("Contract Violated by Server: The current action should not be placePawn.");
        }
        else{
            nextAction = "playTurn";
        }
        Map<Colors, int[]> playerLocations = b.getPlayerLocations();
        int[] chosenLocation = new int[3];
        // chooses starting location from available options randomly
        // randomly chooses a side, then a tile, then one of two locations
        Random generator = new Random();

        do {
            // generate random int from 0 to 3, similarly for the other two
            int randSide = generator.nextInt(4);
            int randTile = generator.nextInt(6);
            int randPos = generator.nextInt(2);
            switch (randSide) {
                case 0:
                    chosenLocation[1] = 0; // bottom side
                    chosenLocation[0] = randTile;
                    chosenLocation[2] = randPos + 4; // pos 4 or 5
                    break;
                case 1:
                    chosenLocation[0] = 5; // right side
                    chosenLocation[1] = randTile;
                    chosenLocation[2] = randPos + 2; // pos 2 or 3
                    break;
                case 2:
                    chosenLocation[1] = 5; // top side
                    chosenLocation[0] = randTile;
                    chosenLocation[2] = randPos; // pos 0 or 1
                    break;
                case 3:
                    chosenLocation[0] = 0; // left side
                    chosenLocation[1] = randTile;
                    chosenLocation[2] = randPos + 6; // pos 6 or 7
                    break;
            }
        }while(playerLocations.containsValue(chosenLocation));

        return chosenLocation;
    }

    // n: number of tiles left in the draw pile
    // random play
    public PTile playTurn(Board b, ArrayList<ATile> hand, int n) throws CloneNotSupportedException {
        if (!nextAction.equals("playTurn")){
            throw new RuntimeException("Contract Violated by Server: The current action should not be playTurn.");
        }
        Server s = new Server();
        int[] thisMarker = b.getPlayerLocation(color);
        SPlayer tempPlayer = new SPlayer(thisMarker, color);
        ArrayList<ATile> h = tempPlayer.getHand();
        h.addAll(hand);
        Random generator = new Random();
        int randTile;
        PTile p;
        do{
            // get a PTile index, cannot play dragon tile
            do {
                randTile = generator.nextInt(hand.size());
            }while(!hand.get(randTile).getClass().equals(PTile.class));

            p = (PTile)hand.get(randTile).clone();

            int randRotation = generator.nextInt(4);
            for (int i = 0; i < randRotation; i++){
                p.rotate();
            }
        }while(!s.legalPlay(tempPlayer, b, p));

        return p;
    }

    public void endGame(Board b, ArrayList<Colors> colors){
        if (!nextAction.equals("playTurn")){
            throw new RuntimeException("Contract Violated by Server: The current action should not be endGame.");
        }
        else{
            nextAction = "initialize";
        }
    }

    public PTile playTurnSymmetric(Board b, ArrayList<ATile> hand, int n, boolean ascendingOrder) throws CloneNotSupportedException, RuntimeException {
        Server s = new Server();
        int[] thisMarker = b.getPlayerLocation(color);
        SPlayer tempPlayer = new SPlayer(thisMarker, color);
        tempPlayer.getHand().addAll(hand);

        List<PTile> h = new ArrayList<PTile>();
        for (ATile tile : hand) {
            if (tile instanceof PTile) { //TODO: Replace all with InstanceOf
                h.add((PTile) tile);
            }
        }

        PTile p;
        // sort tile symmetry in ascending order
        Collections.sort(h);
        if (ascendingOrder) {
            for (int i = 0; i < h.size(); i++) {
                p = h.get(i);
                for (int j = 0; j < 4; j++) {
                    if (s.legalPlay(tempPlayer, b, p)) {
                        return p;
                    }
                    p.rotate();
                }
            }
        }
        else {
            for (int i = h.size() - 1; i >= 0; i--) {
                p = h.get(i);
                for (int j = 0; j < 4; j++) {
                    if (s.legalPlay(tempPlayer, b, p)) {
                        return p;
                    }
                    p.rotate();
                }
            }
        }
        throw new RuntimeException("No legal play found!");
    }
}
