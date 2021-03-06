import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * Runs game.playTurn() a number of times using xml input (for use with ./test-play-a-turn)
 *
 */
public class PlayATurn {

    //================================================================================
    // Instance Variable
    //================================================================================

    private static DocumentBuilder docBuilder;

    //================================================================================
    // Main Method for ./test-play-a-turn
    //================================================================================

    public static void main(String[] args) throws IOException, SAXException {
        Scanner scanner = new Scanner(System.in);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        while (scanner.hasNext()) {
            Game.resetGame();
            Game game = Game.getGame();

            Node tileListNode = parseNextNode(scanner);
            Node remPlayerListNode = parseNextNode(scanner);
            Node elimPlayerListNode = parseNextNode(scanner);
            Node boardNode = parseNextNode(scanner);
            Node playTileNode = parseNextNode(scanner);

            //set tile pile first to deal with dragon tile handling
            TilePile tilePile = new TilePile(tileListNode);
            game.setTilePile(tilePile);

            Board board = new Board(boardNode);
            List<SPlayer> remPlayers = NetworkAdapter.decodeListOfSPlayers(remPlayerListNode, tilePile, board);
            List<SPlayer> elimPlayers = NetworkAdapter.decodeListOfSPlayers(elimPlayerListNode, tilePile, board);
            Tile playTile = new Tile(playTileNode);

            //this adds the play tile to the current player's hand to work with our code
            remPlayers.get(0).addTileToHand(playTile);

            //dragon tile ownership in game is established when remaining players are created
            game.setFromPlayATurnInput(board, remPlayers, elimPlayers);

            Set<SPlayer> losingPlayers = game.playTurn(playTile, remPlayers.get(0));

            Document doc = docBuilder.newDocument();
            doc.appendChild(game.getTilePile().encodeTilePile(doc));
            printDoc(doc);

            doc = docBuilder.newDocument();
            doc.appendChild(NetworkAdapter.encodeListOfSPlayers(doc, game.getRemainingPlayers()));
            printDoc(doc);

            doc = docBuilder.newDocument();
            doc.appendChild(NetworkAdapter.encodeListOfSPlayers(doc, game.getEliminatedPlayers()));
            printDoc(doc);

            doc = docBuilder.newDocument();
            doc.appendChild(game.getBoard().encodeBoard(doc));
            printDoc(doc);

            doc = docBuilder.newDocument();
            if (game.isGameOverWithLoss(losingPlayers)) {
                doc.appendChild(NetworkAdapter.encodeListOfSPlayers(doc, game.getRemainingPlayers()));
            } else {
                doc.appendChild(NetworkAdapter.encodeFalse(doc));
            }
            printDoc(doc);
        }
    }

    //================================================================================
    // Private Helpers
    //================================================================================

    private static void printDoc(Document doc) {
        NetworkAdapter.sendMessage(doc, new PrintWriter(System.out, true));
    }

    private static Node parseNextNode(Scanner scanner) throws IOException, SAXException {
        String nextLine = scanner.nextLine();
        //System.out.println(nextLine);
        InputStream inputStream = new ByteArrayInputStream(nextLine.getBytes(Charset.defaultCharset()));
        Document doc = docBuilder.parse(inputStream);
        return doc.getChildNodes().item(0);
    }

}
