import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClientAdministratorThread extends Thread {

    //================================================================================
    // Instance Variables
    //================================================================================

    private IPlayer iplayer;
    private DocumentBuilder docBuilder;

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    //================================================================================
    // Constructors
    //================================================================================

    public ClientAdministratorThread(IPlayer iplayer){
        super(iplayer.getName());
        this.iplayer = iplayer;

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        socket = new Socket();
    }

    //================================================================================
    // Override Methods
    //================================================================================

    @Override
    public void run() {
        System.out.println("thread " + iplayer.getName() + " running");
        try {

            socket.connect(NetworkAdapter.SOCKETADDRESS, 0);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            System.out.println("thread " + iplayer.getName() + " connected");

            while (!socket.isClosed()) {
                if (socket.getInputStream().available() > 0) {
                    //System.out.println("input available on " + iplayer.getName());
                    InputStream is = new ByteArrayInputStream(input.readLine().replaceAll("\\s+", "").getBytes());
                    Document inputDocument = docBuilder.parse(is);
                    Node functionNode = inputDocument.getChildNodes().item(0);

                    Document responseDocument = docBuilder.newDocument();
                    //System.out.println("input has function name " + functionNode.getNodeName());
                    switch (functionNode.getNodeName()) {
                        case "get-name":
                            //System.out.println("player " + iplayer.getName() + " received get-name call");
                            Element playerNameNode = getNameCaller(responseDocument);
                            responseDocument.appendChild(playerNameNode);
                            break;
                        case "initialize":
                            //System.out.println("player " + iplayer.getName() + " received initialize call");
                            Element initializeVoidNode = initializeCaller(responseDocument, functionNode);
                            responseDocument.appendChild(initializeVoidNode);
                            break;
                        case "place-pawn":
                            //System.out.println("player " + iplayer.getName() + " received place-pawn call");
                            Element pawnLocNode = placePawnCaller(responseDocument, functionNode);
                            responseDocument.appendChild(pawnLocNode);
                            break;
                        case "play-turn":
                            //System.out.println("player " + iplayer.getName() + " received play-turn call");
                            Element tileNode = playTurnCaller(responseDocument, functionNode);
                            responseDocument.appendChild(tileNode);
                            break;
                        case "end-game":
                            //System.out.println("player " + iplayer.getName() + " received end-game call");
                            Element endGameVoidNode = endGameCaller(responseDocument, functionNode);
                            responseDocument.appendChild(endGameVoidNode);
                            break;
                        default:
                            throw new ContractException("given function is not part of the interface for iplayer");
                    }

                    NetworkAdapter.sendMessage(responseDocument, output);
                }
            }
        } catch (SAXException e) {
            System.out.println("SAXException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
    }

    //================================================================================
    // Private Helpers
    //================================================================================

    private Element getNameCaller(Document responseDocument) {
        String playerName = iplayer.getName();
        return NetworkAdapter.encodePlayerName(responseDocument, playerName);
    }

    private Element initializeCaller(Document responseDocument, Node initializeNode) {
        NodeList initializeNodeChildren = initializeNode.getChildNodes();
        Color playerColor = Color.decodeColor(initializeNodeChildren.item(0));
        List<Color> otherPlayerColors = NetworkAdapter.decodeListOfColors(initializeNodeChildren.item(1));

        iplayer.initialize(playerColor, otherPlayerColors);

        return NetworkAdapter.encodeVoid(responseDocument);
    }

    private Element placePawnCaller(Document responseDocument, Node placePawnNode) {
        NodeList placePawnNodeChildren = placePawnNode.getChildNodes();
        Board board = new Board(placePawnNodeChildren.item(0));

        Pair<BoardSpace, Integer> location = iplayer.placePawn(board);

        return NetworkAdapter.encodePawnLoc(responseDocument, location.getKey(), location.getValue());
    }

    private Element playTurnCaller(Document responseDocument, Node playTurnNode) {
        NodeList playTurnNodeChildren = playTurnNode.getChildNodes();
        Board board = new Board(playTurnNodeChildren.item(0));
        List<Tile> hand = new ArrayList<>(NetworkAdapter.decodeSetOfTiles(playTurnNodeChildren.item(1)));
        int numberTilesLeft = NetworkAdapter.decodeN(playTurnNodeChildren.item(2));

        Tile playTile = iplayer.playTurn(board, hand, numberTilesLeft);

        return playTile.encodeTile(responseDocument);
    }

    private Element endGameCaller(Document responseDocument, Node endGameNode) {
        NodeList endGameNodeChildren = endGameNode.getChildNodes();
        Board board = new Board(endGameNodeChildren.item(0));
        Set<Color> winners = NetworkAdapter.decodeSetOfColors(endGameNodeChildren.item(1));

        iplayer.endGame(board, winners);

        return NetworkAdapter.encodeVoid(responseDocument);
    }


    public static void main(String[] args) {
        IPlayer player = new RandomPlayer(args[0]);
        ClientAdministratorThread playerAdmin = new ClientAdministratorThread(player);
        playerAdmin.start();
    }
}
