package main;

import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NetworkAdapter {
    public static Element encodeFalse(Document doc) {
        Element falseElement = doc.createElement("false");
        Text falseText = doc.createTextNode(" ");
        falseElement.appendChild(falseText);
        return falseElement;
    }

    public static Element encodeListOfColors(Document doc, List<Color> list) {
        Element colorListElement = doc.createElement("list");

        for(Color color : list) {
            colorListElement.appendChild(color.encodeColor(doc));
        }

        return colorListElement;
    }

    public static Element encodeSetOfColors(Document doc, Set<Color> set) {
        Element colorSetElement = doc.createElement("set");

        for(Color color : set) {
            colorSetElement.appendChild(color.encodeColor(doc));
        }

        return colorSetElement;
    }

    public static Element encodeListOfTiles(Document doc, List<Tile> list) {
        Element tileSetElement = doc.createElement("list");

        for(Tile tile : list) {
            tileSetElement.appendChild(tile.encodeTile(doc));
        }

        return tileSetElement;
    }

    public static Element encodeSetOfTiles(Document doc, Set<Tile> set) {
        Element tileSetElement = doc.createElement("set");

        for(Tile tile : set) {
            tileSetElement.appendChild(tile.encodeTile(doc));
        }

        return tileSetElement;
    }

    public static Element encodeListOfSPlayers(Document doc, List<SPlayer> list){
        Element splayerListElement = doc.createElement("list");

        for(SPlayer splayer : list) {
            splayerListElement.appendChild(splayer.encodeSPlayer(doc));
        }

        return splayerListElement;
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
