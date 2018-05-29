package main;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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

        Game game = Game.getGame();

        Node tileListNode = parseNextNode(scanner);
        Node remPlayerListNode = parseNextNode(scanner);
        Node elimPlayerListNode = parseNextNode(scanner);
        Node boardNode = parseNextNode(scanner);
        Node playTileNode = parseNextNode(scanner);

        TilePile tilePile = new TilePile(tileListNode);
        Board board = new Board(boardNode);
        List<SPlayer> remPlayers = NetworkAdapter.decodeListOfSPlayers(remPlayerListNode, tilePile, board);
        List<SPlayer> elimPlayers= NetworkAdapter.decodeListOfSPlayers(elimPlayerListNode, tilePile, board);
        Tile playTile = new Tile(playTileNode);

        //dragon tile ownership in game is established when remaining players are created
        game.setFromPlayATurnInput(board, remPlayers, elimPlayers, tilePile);

        Set<SPlayer> losingPlayers = game.playTurn(playTile, remPlayers.get(0));

        Document doc = docBuilder.newDocument();
        doc.appendChild(game.getTilePile().encodeTilePile(doc));
        printdoc(doc);

        doc = docBuilder.newDocument();
        doc.appendChild(NetworkAdapter.encodeListOfSPlayers(doc, game.getRemainingPlayers()));
        printdoc(doc);

        doc = docBuilder.newDocument();
        doc.appendChild(NetworkAdapter.encodeListOfSPlayers(doc, game.getEliminatedPlayers()));
        printdoc(doc);

        doc = docBuilder.newDocument();
        doc.appendChild(game.getBoard().encodeBoard(doc));
        printdoc(doc);

        doc = docBuilder.newDocument();
        if(game.isGameOverWithLoss(losingPlayers)){
            doc.appendChild(NetworkAdapter.encodeListOfSPlayers(doc, game.getRemainingPlayers()));
        }
        else {
            NetworkAdapter.encodeFalse(doc);
        }
        printdoc(doc);
    }

    public static void printdoc(Document doc) {
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, result);
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }
        System.out.println(writer.toString());
    }

    private static Node parseNextNode(Scanner scanner) throws IOException, SAXException {
        InputStream inputStream = new ByteArrayInputStream(scanner.nextLine().getBytes(Charset.defaultCharset()));
        Document doc = docBuilder.parse(inputStream);
        return doc.getChildNodes().item(0);
    }

}
