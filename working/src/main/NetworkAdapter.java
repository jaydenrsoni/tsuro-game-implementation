package main;

import org.w3c.dom.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NetworkAdapter {

    public final static String HOSTNAME = "localhost";
    public final static int PORTNUMBER = 4000;
    public final static SocketAddress SOCKETADDRESS = new InetSocketAddress(HOSTNAME, PORTNUMBER);

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
}
