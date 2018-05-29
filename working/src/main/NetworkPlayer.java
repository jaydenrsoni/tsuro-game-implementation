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
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkPlayer implements IPlayer {

    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;

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
        NetworkAdapter.sendMessage(messageToSend, output);

        String stringResponse = readMessage();

        try {
            Document messageResponse = NetworkAdapter.stringToDom(stringResponse);
            Node playerNameNode = messageResponse.getChildNodes().item(0);

            if(!playerNameNode.getNodeName().equals("player-name")){
                throw new ContractException("invalid response to get-name");
            }

            return NetworkAdapter.decodePlayerName(playerNameNode);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void initialize(Color playerColor, List<Color> otherPlayerColors) {
        Document messageToSend = docBuilder.newDocument();

        Element nameTag = messageToSend.createElement("initialize");
        Element colorNode = playerColor.encodeColor(messageToSend);
        Element listOfColorsNode = NetworkAdapter.encodeListOfColors(messageToSend, otherPlayerColors);

        nameTag.appendChild(colorNode);
        nameTag.appendChild(listOfColorsNode);

        messageToSend.appendChild(nameTag);

        NetworkAdapter.sendMessage(messageToSend, output);
        String stringResponse = readMessage();

        try {
            Document messageResponse = NetworkAdapter.stringToDom(stringResponse);
            Node initializeNode = messageResponse.getChildNodes().item(0);

            if(!initializeNode.getNodeName().equals("void")){
                throw new ContractException("invalid response to initialize");
            }

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pair<BoardSpace, Integer> placePawn(Board board) {
        Document messageToSend = docBuilder.newDocument();

        Element nameTag = messageToSend.createElement("place-pawn");
        Element boardNode = board.encodeBoard(messageToSend);

        nameTag.appendChild(boardNode);
        messageToSend.appendChild(nameTag);

        NetworkAdapter.sendMessage(messageToSend, output);
        String stringResponse = readMessage();

        try {
            Document messageResponse = NetworkAdapter.stringToDom(stringResponse);
            Node placePawnNode = messageResponse.getChildNodes().item(0);

            if(!placePawnNode.getNodeName().equals("pawn-loc")){
                throw new ContractException("invalid response to place-pawn");
            }

            return NetworkAdapter.decodePawnLocNode(board, placePawnNode);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Tile playTurn(Board board, List<Tile> hand, int numberTilesLeft) {
        Document messageToSend = docBuilder.newDocument();

        Element nameTag = messageToSend.createElement("play-turn");
        Element boardNode = board.encodeBoard(messageToSend);
        Element setOfTileNode = NetworkAdapter.encodeSetOfTiles(messageToSend, new HashSet<>(hand));

        Element nNode = messageToSend.createElement("n");
        Text nText = messageToSend.createTextNode(String.valueOf(numberTilesLeft));
        nNode.appendChild(nText);

        nameTag.appendChild(boardNode);
        nameTag.appendChild(setOfTileNode);
        nameTag.appendChild(nNode);
        messageToSend.appendChild(nameTag);

        NetworkAdapter.sendMessage(messageToSend, output);
        String stringResponse = readMessage();
        try {
            Document messageResponse = NetworkAdapter.stringToDom(stringResponse);
            Node playTurnNode = messageResponse.getChildNodes().item(0);

            if(!playTurnNode.getNodeName().equals("tile")){
                throw new ContractException("invalid response to play-turn");
            }

            return new Tile(playTurnNode);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void endGame(Board board, Set<Color> winningColors) {
        Document messageToSend = docBuilder.newDocument();

        Element nameTag = messageToSend.createElement("end-game");
        Element boardNode = board.encodeBoard(messageToSend);
        Element setOfColorNode = NetworkAdapter.encodeSetOfColors(messageToSend, winningColors);

        nameTag.appendChild(boardNode);
        nameTag.appendChild(setOfColorNode);
        messageToSend.appendChild(nameTag);

        NetworkAdapter.sendMessage(messageToSend, output);
        String stringResponse = readMessage();
        try {
            Document messageResponse = NetworkAdapter.stringToDom(stringResponse);
            Node endGameNode = messageResponse.getChildNodes().item(0);

            if(!endGameNode.getNodeName().equals("void")){
                throw new ContractException("invalid response to end-game");
            }

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

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
