package test;

import jdk.nashorn.api.tree.NewTree;
import main.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
