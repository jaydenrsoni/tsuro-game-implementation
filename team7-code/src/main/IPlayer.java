import java.util.ArrayList;

/**
 * Created by Michael on 4/15/2018.
 */
public interface IPlayer {
    Colors getColor();

    String getName();

    // assign color to player, add color to all colors, remove color from available colors
    void initialize(Colors thisColor, ArrayList<Colors> allColor);

    // choose and remove first color from the list by default
    Colors chooseColor(ArrayList<Colors> availableColors);

    int[] placePawn(Board b);

    PTile playTurn(Board b, ArrayList<ATile> hand, int n) throws CloneNotSupportedException;

    void endGame(Board b, ArrayList<Colors> colors);
}
