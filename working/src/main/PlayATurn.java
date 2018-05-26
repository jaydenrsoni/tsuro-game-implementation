package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class PlayATurn {

    private static DocumentBuilderFactory docFactory;
    private static DocumentBuilder docBuilder;

    public static void main(String[] args) throws IOException, SAXException {
        Scanner sc = new Scanner(System.in);

        docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Game game = Game.getGame();

        InputStream is = new ByteArrayInputStream(sc.nextLine().getBytes(Charset.defaultCharset()));
        Document doc = docBuilder.parse(is);
        Node tileListNode = doc.getChildNodes().item(0);
        TilePile tilePile = new TilePile(tileListNode);

        is = new ByteArrayInputStream(sc.nextLine().getBytes(Charset.defaultCharset()));
        doc = docBuilder.parse(is);
        Node remPlayerListNode = doc.getChildNodes().item(0);
        List<SPlayer> remPlayers = NetworkAdapter.decodeListOfSPlayer(remPlayerListNode, tilePile);

        is = new ByteArrayInputStream(sc.nextLine().getBytes(Charset.defaultCharset()));
        doc = docBuilder.parse(is);
        Node elimPlayerListNode = doc.getChildNodes().item(0);
        List<SPlayer> elimPlayers= NetworkAdapter.decodeListOfSPlayer(elimPlayerListNode, tilePile);

        is = new ByteArrayInputStream(sc.nextLine().getBytes(Charset.defaultCharset()));
        doc = docBuilder.parse(is);
        Node boardNode = doc.getChildNodes().item(0);
        Board board = new Board(boardNode, remPlayers);

        is = new ByteArrayInputStream(sc.nextLine().getBytes(Charset.defaultCharset()));
        doc = docBuilder.parse(is);
        Node playTileNode = doc.getChildNodes().item(0);
        Tile playTile = new Tile(playTileNode);

        //TODO: run play-turn, encode output
        //dragon tile ownership in game is established when remaining players are created
        game.setFromPlayATurnInput(board, remPlayers, elimPlayers, tilePile);

        Set<SPlayer> losingPlayers = game.playTurn(playTile, remPlayers.get(0));

        doc = docBuilder.newDocument();
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

}
