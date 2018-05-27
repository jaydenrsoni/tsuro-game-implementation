package main;

import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
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
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkPlayer implements IPlayer {

    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public NetworkPlayer(Socket socket) {
        docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        this.socket = socket;

        try {
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

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
        Document messageToSend = docBuilder.newDocument();

        Element nameTag = messageToSend.createElement("get-name");
        Text empty = messageToSend.createTextNode(" ");
        nameTag.appendChild(empty);
        messageToSend.appendChild(nameTag);
        sendMessage(messageToSend);

        String stringResponse = readMessage();
        InputStream streamResponse = new ByteArrayInputStream(stringResponse.getBytes());
        try {
            Document messageResponse = docBuilder.parse(streamResponse);
            Node playerNameNode = messageResponse.getChildNodes().item(0);

            if(!playerNameNode.getNodeName().equals("player-name")){
                throw new ContractException("invalid response to get-name");
            }

            return NetworkAdapter.decodePlayerName(playerNameNode);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void initialize(Color playerColor, List<Color> otherPlayerColors) {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("initialize");
        Element colorNode = playerColor.encodeColor(message);
        Element listOfColorsNode = NetworkAdapter.encodeListOfColors(message, otherPlayerColors);

        nameTag.appendChild(colorNode);
        nameTag.appendChild(listOfColorsNode);

        message.appendChild(nameTag);

        sendMessage(message);
        String response = readMessage();
    }

    @Override
    public Pair<BoardSpace, Integer> placePawn(Board board) {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("place-pawn");
        Element boardNode = board.encodeBoard(message);

        nameTag.appendChild(boardNode);
        message.appendChild(nameTag);

        sendMessage(message);
        String response = readMessage();

        return null;
    }

    @Override
    public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft) {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("play-turn");
        Element boardNode = board.encodeBoard(message);
        Element setOfTileNode = NetworkAdapter.encodeSetOfTiles(message, new HashSet<>(hand));

        Element nNode = message.createElement("n");
        Text nText = message.createTextNode(String.valueOf(numberTilesLeft));
        nNode.appendChild(nText);

        nameTag.appendChild(boardNode);
        nameTag.appendChild(setOfTileNode);
        nameTag.appendChild(nNode);
        message.appendChild(nameTag);

        sendMessage(message);
        String response = readMessage();

        return null;
    }

    @Override
    public void endGame(Board board, Set<Color> winningColors) {
        Document message = docBuilder.newDocument();

        Element nameTag = message.createElement("play-turn");
        Element boardNode = board.encodeBoard(message);
        Element setOfColorNode = NetworkAdapter.encodeSetOfColors(message, winningColors);

        nameTag.appendChild(boardNode);
        nameTag.appendChild(setOfColorNode);
        message.appendChild(nameTag);

        sendMessage(message);
        String response = readMessage();
    }

    private void sendMessage(Document message) {
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

    private String readMessage(){
        try {
            return input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
