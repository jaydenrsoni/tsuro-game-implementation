package main;

import javafx.util.Pair;

import java.util.Scanner;

/**
 * Created by vyasalwar on 5/3/18.
 */
public class UserPlayer extends APlayer {

    public UserPlayer(String name, Color color) {
        super(name, color);
    }

    @Override

    public Pair<BoardSpace, Integer> getStartingLocation() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("It is " + getName() + "'s turn.");
        System.out.println("Please choose a starting location!");
        while (true){
            try {
                System.out.print("Row (1-6): ");
                int row = scanner.nextInt();
                System.out.print("Col (1-6): ");
                int col = scanner.nextInt();
                BoardSpace boardSpace = Game.getGame().getBoard().getBoardSpace(row, col);

                System.out.print("Starting Token Space (1-8): ");
                int tokenSpace = scanner.nextInt();

                return new Pair<>(boardSpace, tokenSpace);
            }
            catch (IllegalArgumentException err){
                System.err.println("Invalid starting location. Try again.");
            }

        }
    }

    protected Tile chooseTileHelper(){
        System.out.println("It is " + getName() + "'s turn.");
        System.out.println("Type help to see commands");
        /* For input */
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine();
            if (command.startsWith("help")){
                System.out.println("Available Commands are:");
                System.out.println("\tchoose [tile] - Try to place the chosen tile");
                System.out.println("\trotate [tile] - Rotate tile");
                System.out.println("\tdisplay       - Display tiles currently in hand");
            }

            else if (command.startsWith("choose")){
                Tile tile = null;
                if      (command.endsWith("1")) tile = getTile(0);
                else if (command.endsWith("2")) tile = getTile(1);
                else if (command.endsWith("3")) tile = getTile(2);

                if (tile == null || !Game.getGame().isLegalMove(tile, this))
                    System.err.println("Error: choose a valid tile");
                else
                    return tile;
            }

            else if (command.startsWith("rotate")){
                try {
                    Tile toRotate = null;
                    if      (command.endsWith("1")) toRotate = getTile(0);
                    else if (command.endsWith("2")) toRotate = getTile(1);
                    else if (command.endsWith("3")) toRotate = getTile(2);
                    else                         throw new NullPointerException();

                    toRotate.rotateClockwise();
                    System.out.println("Tile Rotated: " + toRotate.toString());

                }
                catch (NullPointerException e){
                    System.err.println("Error: No tile found");
                }
            }

            else if (command.startsWith("display")){
                System.out.println("Displaying tiles in hand...");
                for (int i = 0; i < 3; i++){
                    String line = "\t" + (i+1) + ": ";
                    if (getTile(i) != null)
                        line += getTile(i).toString();
                    else
                        line += "No tile present";

                    System.out.println(line);
                }
            }
            else {
                System.out.println("Command not understood");
            }
        }

    }
}
