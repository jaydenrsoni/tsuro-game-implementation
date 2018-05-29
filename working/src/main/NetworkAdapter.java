package main;

import javafx.util.Pair;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NetworkAdapter {

    public final static String HOSTNAME = "localhost";
    public final static int PORTNUMBER = 4000;
    public final static SocketAddress SOCKETADDRESS = new InetSocketAddress(HOSTNAME, PORTNUMBER);

    public static Document stringToDom(String xmlSource) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }

    public static void sendMessage(Document message, PrintWriter output) {
        DOMSource source = new DOMSource(message);
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

        output.println(writer.toString());
    }

    public static Element encodeVoid(Document doc) {
        Element voidElement = doc.createElement("void");
        Text voidText = doc.createTextNode(" ");
        voidElement.appendChild(voidText);
        return voidElement;
    }

    public static Element encodeFalse(Document doc) {
        Element falseElement = doc.createElement("false");
        Text falseText = doc.createTextNode(" ");
        falseElement.appendChild(falseText);
        return falseElement;
    }

    public static Element encodeListOfColors(Document doc, List<Color> list) {
        Element colorListElement = doc.createElement("list");
        Text spaceText = doc.createTextNode(" ");
        colorListElement.appendChild(spaceText);

        for(Color color : list) {
            colorListElement.appendChild(color.encodeColor(doc));
        }

        return colorListElement;
    }

    public static Element encodeSetOfColors(Document doc, Set<Color> set) {
        Element colorSetElement = doc.createElement("set");
        Text spaceText = doc.createTextNode(" ");
        colorSetElement.appendChild(spaceText);

        for(Color color : set) {
            colorSetElement.appendChild(color.encodeColor(doc));
        }

        return colorSetElement;
    }

    public static Element encodeListOfTiles(Document doc, List<Tile> list) {
        Element tileSetElement = doc.createElement("list");
        Text spaceText = doc.createTextNode(" ");
        tileSetElement.appendChild(spaceText);

        for(Tile tile : list) {
            tileSetElement.appendChild(tile.encodeTile(doc));
        }

        return tileSetElement;
    }

    public static Element encodeSetOfTiles(Document doc, Set<Tile> set) {
        Element tileSetElement = doc.createElement("set");
        Text spaceText = doc.createTextNode(" ");
        tileSetElement.appendChild(spaceText);

        for(Tile tile : set) {
            tileSetElement.appendChild(tile.encodeTile(doc));
        }

        return tileSetElement;
    }

    public static Element encodeListOfSPlayers(Document doc, List<SPlayer> list){
        Element splayerListElement = doc.createElement("list");
        Text spaceText = doc.createTextNode(" ");
        splayerListElement.appendChild(spaceText);

        for(SPlayer splayer : list) {
            splayerListElement.appendChild(splayer.encodeSPlayer(doc));
        }

        return splayerListElement;
    }

    public static Element encodePlayerName(Document doc, IPlayer iplayer){
        Element playerNameNode = doc.createElement("player-name");
        Text playerNameText = doc.createTextNode(iplayer.getName());
        playerNameNode.appendChild(playerNameText);
        return playerNameNode;
    }

    public static String decodePlayerName(Node playerNameNode){
        return playerNameNode.getTextContent();
    }

    public static List<SPlayer> decodeListOfSPlayer(Node splayerNode, TilePile tilePile) {
        List<SPlayer> splayers = new ArrayList<>();
        NodeList splayerNodeList = splayerNode.getChildNodes();
        for(int i = 0; i < splayerNodeList.getLength(); i++) {
            splayers.add(new SPlayer(splayerNodeList.item(i), tilePile));
        }
        return splayers;
    }

    public static Pair<BoardSpace, Integer> decodePawnLocNode(Board board, Node pawnLocNode) {
        NodeList pawnLocNodeChildren = pawnLocNode.getChildNodes();
        Node hvNode = pawnLocNodeChildren.item(0);
        int n1 = Integer.parseInt(pawnLocNodeChildren.item(1).getTextContent());
        int n2 = Integer.parseInt(pawnLocNodeChildren.item(2).getTextContent());


        if(hvNode.getNodeName().equals("h")){
            return decodePawnLocH(board, n1, n2);
        }
        else{
            return decodePawnLocV(board, n1, n2);
        }
    }

    private static Pair<BoardSpace, Integer> decodePawnLocV(Board board, int n1, int n2) {
        int colOne = n1 -1;
        int colTwo = n1;
        int row = n2 / 2;

        if(colOne == -1 || board.getBoardSpace(row, colOne).hasTile()){
            int tokenSpace = 7 - (n2 % 2);
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(row, colTwo), tokenSpace);
        }
        else {
            int tokenSpace = 2 + (n2 % 2);
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(row, colOne), tokenSpace);
        }
    }

    private static Pair<BoardSpace, Integer> decodePawnLocH(Board board, int n1, int n2) {
        int rowOne = n1 - 1;
        int rowTwo = n1;
        int col = n2 / 2;

        if(rowOne == -1 || board.getBoardSpace(rowOne, col).hasTile()){
            int tokenSpace = n2 % 2;
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(rowTwo, col), tokenSpace);
        }
        else {
            int tokenSpace = 5 - (n2 % 2);
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(rowOne, col), tokenSpace);
        }
    }
}
