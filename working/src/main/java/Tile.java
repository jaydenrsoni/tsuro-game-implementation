import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * Represents a single Tile in Tsuro
 *
 */
public class Tile {

    //================================================================================
    // Instance Variables
    //================================================================================

    private final int ROTATIONS_PER_CYCLE = 4;
    private Set<TileConnection> connections;

    //================================================================================
    // Constructors
    //================================================================================

    // Constructor from explicit values
    public Tile(int startA, int endA,
                int startB, int endB,
                int startC, int endC,
                int startD, int endD){

        connections = new HashSet<>();
        connections.add(new TileConnection(startA, endA));
        connections.add(new TileConnection(startB, endB));
        connections.add(new TileConnection(startC, endC));
        connections.add(new TileConnection(startD, endD));

        assertValid();
    }

    // Constructor that reads from an input file.
    //  See TilePile.fillAllTiles to see usage
    public Tile(String fileLine){
        connections = new HashSet<>();

        // endpoints are separated by single spaces when reading from file
        String[] endpoints = fileLine.split(" ");
        for (int i = 0; i < 4; i++) {
            int endpointA = Integer.parseInt(endpoints[2 * i]);
            int endpointB = Integer.parseInt(endpoints[2 * i + 1]);
            connections.add(new TileConnection(endpointA, endpointB));
        }

        assertValid();
    }

    // Constructor from other object. Clones other tile into this one
    public Tile(Tile other){
        connections = new HashSet<>();
        for (TileConnection tileConnection : other.connections){
            connections.add(new TileConnection(tileConnection));
        }
    }

    public Tile(Node tileNode){
        connections = new HashSet<>();

        NodeList connectionList = tileNode.getChildNodes();
        for (int i = 0; i < connectionList.getLength(); i++) {
            connections.add(new TileConnection(connectionList.item(i)));
        }
    }


    //================================================================================
    // Public Methods
    //================================================================================

    // Rotates the entire tile clockwise.
    //  Modifies the tile state instead of creating a new object.
    public void rotateClockwise() {
        /* Important: modifying the state of a Hash Table in a way that changes hashes will
            cause the hash table to be unusable! Reassign connections to a new HashSet instead.
         */
        Set<TileConnection> newConnections = new HashSet<>();
        for (TileConnection connection : connections){
            connection.rotateClockwise();
            newConnections.add(connection);
        }
        connections = newConnections;
    }

    // Given one endpoint in the tile, returns the connected endpoint
    public int findMatch(int endpoint){
        for (TileConnection connection: connections){
            if (connection.contains(endpoint)){
                return connection.otherEndpoint(endpoint);
            }
        }
        throw new IllegalArgumentException("Endpoint must be between 0 and 7");
    }

    public double calculateSymmetries(){
        Tile copy = new Tile(this);
        Set<Set> uniqueTileConnections = new HashSet<>();

        for (int i = 0; i < 4; i++){
            uniqueTileConnections.add(copy.connections);
            copy.rotateClockwise();
        }

        return 1.0/uniqueTileConnections.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile) {
            Tile other = new Tile((Tile) obj);
            for (int i = 0; i < ROTATIONS_PER_CYCLE; i++) {
                if (connections.equals(other.connections)) {
                    return true;
                }
                other.rotateClockwise();
            }
        }

        return false;
    }

    @Override
    public int hashCode(){
        return connections.hashCode();
    }

    @Override
    public String toString() {
        String ret = "Tile{";
        for (TileConnection connection: connections){
            ret += " " + connection.toString();
        }
        return ret + "}";
    }

    //================================================================================
    // Private Helpers
    //================================================================================

    // Checks to make sure the tile contains all numbers 0..7 exactly once
    private void assertValid(){
        // A valid tile is a bijective map from {0..7} to itself

        Set<Integer> nums = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            nums.add(findMatch(i));
        }

        // if nums.size() != 8, then the map is not onto
        if(nums.size() != 8)
            throw new IllegalArgumentException("Tile connections are not valid");
    }

    //================================================================================
    // XML Helper
    //================================================================================

    public Element encodeTile(Document doc) {
        Element tileElement = doc.createElement("tile");

        for(TileConnection connect : connections) {
            Element connectElement = connect.encodeConnect(doc);
            tileElement.appendChild(connectElement);
        }

        return tileElement;
    }


    //================================================================================
    // Private Helper Class
    //================================================================================
    private class TileConnection {

        //================================================================================
        // Instance Variables
        //================================================================================

        private int endpointA;
        private int endpointB;

        //================================================================================
        // Constructors
        //================================================================================

        // Explicit constructor
        public TileConnection (int endpointA, int endpointB) {
            this.endpointA = endpointA;
            this.endpointB = endpointB;
        }

        // Constructor from another TileConnection, i.e. clone
        public TileConnection (TileConnection other) {
            this.endpointA = other.endpointA;
            this.endpointB = other.endpointB;
        }

        public TileConnection (Node tileConnectionNode) {

            NodeList endpointPair = tileConnectionNode.getChildNodes();
            this.endpointA = Integer.parseInt(endpointPair.item(0).getTextContent());
            this.endpointB = Integer.parseInt(endpointPair.item(1).getTextContent());

        }

        //================================================================================
        // Public Methods
        //================================================================================

        // Returns true if the argument is one of the endpoints
        public boolean contains(int endpoint){
            return endpoint == endpointA || endpoint == endpointB;
        }

        // Finds the matching endpint of the argument if it exists
        public int otherEndpoint(int endpoint){
            if (endpoint == endpointA) return endpointB;
            if (endpoint == endpointB) return endpointA;
            throw new IllegalArgumentException("Endpoint object does not contain input");
        }

        // Moves both of the endpoints clockwise around
        public void rotateClockwise(){
            endpointA = (endpointA + 2) % 8;
            endpointB = (endpointB + 2) % 8;
        }

        @Override
        public boolean equals(Object obj){
            if (obj instanceof TileConnection){
                TileConnection other = (TileConnection) obj;
                return (this.endpointA == other.endpointA && this.endpointB == other.endpointB) ||
                        (this.endpointB == other.endpointA && this.endpointA == other.endpointB);
            }
            else
                return false;
        }

        @Override
        public int hashCode(){
            // We'll see if this is a good hash function later :P
            int x = endpointA + 1, y = endpointB + 1;
            return x * x * y + y * y * x;
        }

        @Override
        public String toString() {
            return "(" + endpointA + ", " + endpointB + ")";
        }


        //================================================================================
        // XML Helper
        //================================================================================

        public Element encodeConnect(Document doc) {
            Element connectElement = doc.createElement("connect");

            Element nNodeA = doc.createElement("n");
            Element nNodeB = doc.createElement("n");
            Text natA = doc.createTextNode(String.valueOf(endpointA));
            Text natB = doc.createTextNode(String.valueOf(endpointB));

            nNodeA.appendChild(natA);
            nNodeB.appendChild(natB);
            connectElement.appendChild(nNodeA);
            connectElement.appendChild(nNodeB);

            return connectElement;
        }
    }

}
