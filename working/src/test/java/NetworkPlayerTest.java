import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NetworkPlayerTest {

    private TilePile tilePile;

    @Before
    public void start() {
        tilePile = new TilePile();
    }


    @Test
    public void getNameTest() {
        NetworkPlayer player = new NetworkPlayer();
        player.getName();
    }

    @Test
    public void initializeTest() {
        NetworkPlayer player = new NetworkPlayer();
        List<Color> colorList = new ArrayList<>();
        colorList.add(Color.BLUE);
        colorList.add(Color.GREEN);
        player.initialize(Color.BLUE, colorList);
    }

    @Test
    public void placePawnEmptyBoardTest() {
        NetworkPlayer player = new NetworkPlayer();

        Board board = new Board();

        player.placePawn(board);
    }

    @Test
    public void placePawnNonEmptyBoardTest() {
        Board board = new Board();

        Tile tile = new Tile(0, 1, 2, 4, 3, 6, 5, 7);
        BoardSpace space = board.getBoardSpace(0, 0);
        SPlayer player = new SPlayer(new RandomPlayer("keith"), tilePile);

        List<Color> colorList = new ArrayList<>();
        colorList.add(Color.RED);
        colorList.add(Color.GREEN);
        player.initializeSPlayer(Color.RED, colorList);
        player.placeToken(space, 6);

        board.placeTile(tile, player);

        NetworkPlayer networkPlayer = new NetworkPlayer();

        networkPlayer.placePawn(board);
    }

    @Test
    public void playTurnTest() {
        Board board = new Board();

        Tile tile = new Tile(0, 1, 2, 4, 3, 6, 5, 7);
        BoardSpace space = board.getBoardSpace(0, 0);
        SPlayer player = new SPlayer(new RandomPlayer("keith"), tilePile);

        List<Color> colorList = new ArrayList<>();
        colorList.add(Color.RED);
        colorList.add(Color.GREEN);
        player.initializeSPlayer(Color.RED, colorList);
        player.placeToken(space, 6);

        board.placeTile(tile, player);

        List<Tile> hand = new ArrayList<>();
        hand.add(new Tile(0, 1, 2, 3, 4, 5, 6, 7));
        hand.add(new Tile(0, 1, 2, 3, 4, 6, 5, 7));

        NetworkPlayer networkPlayer = new NetworkPlayer();

        networkPlayer.playTurn(board, hand, 20);
    }

    @Test
    public void endGameTest() {
        Board board = new Board();

        Tile tile = new Tile(0, 1, 2, 4, 3, 6, 5, 7);
        BoardSpace space = board.getBoardSpace(0, 0);
        SPlayer player = new SPlayer(new RandomPlayer("keith"), tilePile);

        List<Color> colorList = new ArrayList<>();
        colorList.add(Color.RED);
        colorList.add(Color.GREEN);
        player.initializeSPlayer(Color.RED, colorList);
        player.placeToken(space, 6);

        board.placeTile(tile, player);

        NetworkPlayer networkPlayer = new NetworkPlayer();

        networkPlayer.endGame(board, new HashSet<>(colorList));
    }

    @Test
    public void encodeDecodeListOfColorsTest() throws ParserConfigurationException {
        List<Color> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element listOfColorsNode = NetworkAdapter.encodeListOfColors(doc, colors);
        List<Color> newColors = NetworkAdapter.decodeListOfColors(listOfColorsNode);
        Assert.assertEquals(colors, newColors);
    }
}
