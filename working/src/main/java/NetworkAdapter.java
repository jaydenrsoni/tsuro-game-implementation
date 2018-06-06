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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Static functions for use encoding and decoding
 *
 */
public class NetworkAdapter {
    //================================================================================
    // Public Final Static Variables
    //================================================================================

    public final static String HOSTNAME = "localhost";
    public final static int PORTNUMBER = 4000;
    public final static SocketAddress SOCKETADDRESS = new InetSocketAddress(HOSTNAME, PORTNUMBER);

    //================================================================================
    // XML Conversion Helpers
    //================================================================================

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

        output.println(writer.toString().replaceAll("\\s+", ""));
    }

    //================================================================================
    // Encoding Helpers
    //================================================================================

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

    public static Element encodePlayerName(Document doc, String playerName){
        Element playerNameNode = doc.createElement("player-name");
        Text playerNameText = doc.createTextNode(playerName);
        playerNameNode.appendChild(playerNameText);
        return playerNameNode;
    }

    public static Element encodeListOfSPlayers(Document doc, List<SPlayer> list) {
        Element splayerListElement = doc.createElement("list");
        Text spaceText = doc.createTextNode(" ");
        splayerListElement.appendChild(spaceText);

        for (SPlayer splayer : list) {
            splayerListElement.appendChild(splayer.encodeSPlayer(doc));
        }

        return splayerListElement;
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

    public static Element encodePawnLoc(Document doc, BoardSpace boardSpace, int tokenSpace) {
        int row = boardSpace.getRow();
        int col = boardSpace.getCol();

        Element pawnLocElement = doc.createElement("pawn-loc");
        Element hvNode = encodeHv(doc, tokenSpace);
        Element n1Node = encodeN1(doc, tokenSpace, row, col);
        Element n2Node = encodeN2(doc, tokenSpace, row, col);


        pawnLocElement.appendChild(hvNode);
        pawnLocElement.appendChild(n1Node);
        pawnLocElement.appendChild(n2Node);

        return pawnLocElement;
    }

    //================================================================================
    // Decoding Helpers
    //================================================================================

    public static int decodeN(Node nNode) {
        return Integer.parseInt(nNode.getTextContent());
    }

    public static String decodePlayerName(Node playerNameNode){
        return playerNameNode.getTextContent();
    }

    public static List<SPlayer> decodeListOfSPlayers(Node splayersNode, TilePile tilePile, Board board) {
        List<SPlayer> splayers = new ArrayList<>();
        NodeList splayerNodeList = splayersNode.getChildNodes();
        for(int i = 0; i < splayerNodeList.getLength(); i++) {
            splayers.add(new SPlayer(splayerNodeList.item(i), tilePile, board));
        }
        return splayers;
    }

    public static List<Color> decodeListOfColors(Node colorsNode) {
        List<Color> colors = new ArrayList<>();
        NodeList colorNodeList = colorsNode.getChildNodes();
        for(int i = 0; i < colorNodeList.getLength(); i++) {
            colors.add(Color.decodeColor(colorNodeList.item(i)));
        }
        return colors;
    }

    public static Set<Color> decodeSetOfColors(Node colorsNode) {
        Set<Color> colors = new HashSet<>();
        NodeList colorNodeSet = colorsNode.getChildNodes();
        for(int i = 0; i < colorNodeSet.getLength(); i++) {
            colors.add(Color.decodeColor(colorNodeSet.item(i)));
        }
        return colors;
    }

    public static Set<Tile> decodeSetOfTiles(Node tilesNode) {
        Set<Tile> tiles = new HashSet<>();
        NodeList tileNodeList = tilesNode.getChildNodes();
        for (int i = 0; i < tileNodeList.getLength(); i++) {
            tiles.add(new Tile(tileNodeList.item(i)));
        }
        return tiles;
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

    //================================================================================
    // Private Helpers
    //================================================================================

    private static Element encodeHv(Document doc, int tokenSpace) {
        Element hvNode;
        switch(tokenSpace){
            case 0:
            case 1:
            case 4:
            case 5:
                hvNode = doc.createElement("h");
                break;
            case 2:
            case 3:
            case 6:
            case 7:
                hvNode = doc.createElement("v");
                break;
            default:
                throw new IllegalArgumentException("invalid tokenSpace");
        }

        Text hvText = doc.createTextNode(" ");
        hvNode.appendChild(hvText);

        return hvNode;
    }

    private static Element encodeN1(Document doc, int tokenSpace, int row, int col) {
        Element n1Node = doc.createElement("n");
        Text n1Text;
        switch(tokenSpace) {
            case 0:
            case 1:
                n1Text = doc.createTextNode(String.valueOf(row));
                break;
            case 2:
            case 3:
                n1Text = doc.createTextNode(String.valueOf(col + 1));
                break;
            case 4:
            case 5:
                n1Text = doc.createTextNode(String.valueOf(row + 1));
                break;
            case 6:
            case 7:
                n1Text = doc.createTextNode(String.valueOf(col));
                break;
            default:
                throw new IllegalArgumentException("invalid tokenSpace");
        }

        n1Node.appendChild(n1Text);
        return n1Node;
    }

    private static Element encodeN2(Document doc, int tokenSpace, int row, int col) {
        Element n2Node = doc.createElement("n");
        Text n2Text;
        switch (tokenSpace) {
            case 0:
            case 5:
                n2Text = doc.createTextNode(String.valueOf(col*2));
                break;
            case 1:
            case 4:
                n2Text = doc.createTextNode(String.valueOf(col*2 + 1));
                break;
            case 2:
            case 7:
                n2Text = doc.createTextNode(String.valueOf(row*2));
                break;
            case 3:
            case 6:
                n2Text = doc.createTextNode(String.valueOf(row*2 + 1));
                break;
            default:
                throw new IllegalArgumentException("invalid tokenSpace");
        }

        n2Node.appendChild(n2Text);
        return n2Node;
    }

    private static Pair<BoardSpace, Integer> decodePawnLocV(Board board, int n1, int n2) {
        int colOne = n1 -1;
        int colTwo = n1;
        int row = n2 / 2;

        if(colOne == -1){
            int tokenSpace = 7 - (n2 % 2);
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(row, colTwo), tokenSpace);
        }
        else if(colTwo == 6){
            int tokenSpace = 2 + (n2 % 2);
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(row, colOne), tokenSpace);
        }
        else if(board.getBoardSpace(row, colOne).hasTile()){
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

        if(rowOne == -1) {
            int tokenSpace = n2 % 2;
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(rowTwo, col), tokenSpace);
        }
        else if(rowTwo == 6){
            int tokenSpace = 5 - (n2 % 2);
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(rowOne, col), tokenSpace);
        }
        else if(board.getBoardSpace(rowOne, col).hasTile()){
            int tokenSpace = n2 % 2;
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(rowTwo, col), tokenSpace);
        }
        else {
            int tokenSpace = 5 - (n2 % 2);
            return new Pair<BoardSpace, Integer>(board.getBoardSpace(rowOne, col), tokenSpace);
        }
    }
}
