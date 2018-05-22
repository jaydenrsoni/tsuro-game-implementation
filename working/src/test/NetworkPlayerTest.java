package test;

import main.Color;
import main.NetworkPlayer;
import org.junit.Test;
import sun.nio.ch.Net;

import java.util.ArrayList;
import java.util.List;

public class NetworkPlayerTest {
    @Test
    public void getNameTest() {
        NetworkPlayer player = new NetworkPlayer();
        player.getName();
    }

    @Test
    public void initializeTest() {
        NetworkPlayer player = new NetworkPlayer();
        List<Color> colorList = new ArrayList<Color>();
        colorList.add(Color.BLUE);
        colorList.add(Color.GREEN);
        player.initialize(Color.BLUE, colorList);
    }
}
