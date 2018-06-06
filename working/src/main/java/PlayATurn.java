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

public class PlayATurn {

    private static DocumentBuilderFactory docFactory;
    private static DocumentBuilder docBuilder;

    public static void main(String[] args) throws IOException, SAXException {
        Scanner scanner = new Scanner(System.in);
        docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        while (true) {
            //System.err.println("began turn " + i);
            Game.resetGame();
            Game game = Game.getGame();

            Node tileListNode = parseNextNode(scanner);
            //System.err.println("parse first node turn " + i);
            Node remPlayerListNode = parseNextNode(scanner);
            Node elimPlayerListNode = parseNextNode(scanner);
            Node boardNode = parseNextNode(scanner);
            Node playTileNode = parseNextNode(scanner);

            TilePile tilePile = new TilePile(tileListNode);
            Board board = new Board(boardNode);
            List<SPlayer> remPlayers = NetworkAdapter.decodeListOfSPlayers(remPlayerListNode, tilePile, board);
            List<SPlayer> elimPlayers = NetworkAdapter.decodeListOfSPlayers(elimPlayerListNode, tilePile, board);
            Tile playTile = new Tile(playTileNode);

            //this adds the play tile to the current player's hand to work with our code
            remPlayers.get(0).addTileToHand(playTile);

            //dragon tile ownership in game is established when remaining players are created
            game.setFromPlayATurnInput(board, remPlayers, elimPlayers, tilePile);

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
            //System.err.println("ended turn " + i);
        }
    }

    public static void printDoc(Document doc) {
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
