package main;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkPlayer implements IPlayer {

    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;

    public NetworkPlayer() {
        docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("get-name");
        Text empty = message.createTextNode(" ");
        nameTag.appendChild(empty);
        message.appendChild(nameTag);

        transformText(message);

        //decode here
        return "";
    }

    @Override
    public void initialize(Color playerColor, List<Color> otherPlayerColors) {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("initialize");
        Element colorNode = playerColor.encodeColor(message);
        Element listOfColorsNode = this.encodeListOfColors(message, otherPlayerColors);

        nameTag.appendChild(colorNode);
        nameTag.appendChild(listOfColorsNode);

        message.appendChild(nameTag);

        transformText(message);
    }

    @Override
    public Pair<BoardSpace, Integer> placePawn(Board board) {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("place-pawn");
        Element boardNode = board.encodeBoard(message);

        nameTag.appendChild(boardNode);
        message.appendChild(nameTag);

        transformText(message);

        return null;
    }

    @Override
    public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft) {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("play-turn");
        Element boardNode = board.encodeBoard(message);
        Element setOfTileNode = encodeSetOfTiles(message, new HashSet<>(hand));

        Element nNode = message.createElement("n");
        Text nText = message.createTextNode(String.valueOf(numberTilesLeft));
        nNode.appendChild(nText);

        nameTag.appendChild(boardNode);
        nameTag.appendChild(setOfTileNode);
        nameTag.appendChild(nNode);
        message.appendChild(nameTag);

        transformText(message);

        return null;
    }

    @Override
    public void endGame(Board board, Set<Color> winningColors) {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("play-turn");
        Element boardNode = board.encodeBoard(message);
        Element setOfColorNode = encodeSetOfColors(message, winningColors);

        nameTag.appendChild(boardNode);
        nameTag.appendChild(setOfColorNode);
        message.appendChild(nameTag);

        transformText(message);
    }

    private Element encodeListOfColors(Document doc, List<Color> list) {
        Element colorListElement = doc.createElement("list");

        for(Color color : list) {
            colorListElement.appendChild(color.encodeColor(doc));
        }

        return colorListElement;
    }

    private Element encodeSetOfColors(Document doc, Set<Color> set) {
        Element colorSetElement = doc.createElement("set");

        for(Color color : set) {
            colorSetElement.appendChild(color.encodeColor(doc));
        }

        return colorSetElement;
    }

    private Element encodeSetOfTiles(Document doc, Set<Tile> set) {
        Element tileSetElement = doc.createElement("set");

        for(Tile tile : set) {
            tileSetElement.appendChild(tile.encodeTile(doc));
        }

        return tileSetElement;
    }

    private void transformText(Document doc) {
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        System.out.println("XML String: " + writer.toString());
    }
}
