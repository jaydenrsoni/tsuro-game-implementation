package main;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;

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
        return "";
    }

    @Override
    public void initialize(Color playerColor, List<Color> otherPlayerColors) {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("initialize");
        Element color = playerColor.encodeColor(message);
        Element listOfColors = this.encodeListOfColors(message, otherPlayerColors);

        nameTag.appendChild(color);
        nameTag.appendChild(listOfColors);

        message.appendChild(nameTag);

        transformText(message);
    }

    @Override
    public Pair<BoardSpace, Integer> placePawn(Board board) {
        return null;
    }

    @Override
    public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft) {
        return null;
    }

    @Override
    public void endGame(Board board, List<Color> winningColors) {

    }

    private Element encodeListOfColors(Document doc, List<Color> list) {
        Element colorListElement = doc.createElement("list");

        for(Color color : list) {
            colorListElement.appendChild(color.encodeColor(doc));
        }

        return colorListElement;
    }

    private void transformText(Document doc) {
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        System.out.println("XML String: " + writer.toString());
    }
}
