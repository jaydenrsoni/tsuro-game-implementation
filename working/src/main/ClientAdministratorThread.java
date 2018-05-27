package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;

public class ClientAdministratorThread extends Thread {

    private IPlayer iplayer;
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public ClientAdministratorThread(IPlayer iplayer){
        super(iplayer.getName());
        this.iplayer = iplayer;

        docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        socket = new Socket();
    }

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
                    System.out.println("input available on " + iplayer.getName());
                    InputStream is = new ByteArrayInputStream(input.readLine().getBytes());
                    Document inputDocument = docBuilder.parse(is);
                    Node functionNode = inputDocument.getChildNodes().item(0);

                    Document responseDocument = docBuilder.newDocument();
                    System.out.println("input has function name " + functionNode.getNodeName());
                    switch (functionNode.getNodeName()) {
                        case "get-name":
                            System.out.println("player " + iplayer.getName() + " received get-name call");
                            Element playerNameNode = NetworkAdapter.encodePlayerName(responseDocument, iplayer);
                            responseDocument.appendChild(playerNameNode);
                            break;
                        case "initialize":
                            System.out.println("player " + iplayer.getName() + " received initialize call");
                            Element voidNode = NetworkAdapter.encodeVoid(responseDocument);
                            responseDocument.appendChild(voidNode);
                            break;
                        case "place-pawn":
                            System.out.println("player " + iplayer.getName() + " received place-pawn call");
                            break;
                        case "play-turn":
                            System.out.println("player " + iplayer.getName() + " received play-turn call");
                            break;
                        case "end-game":
                            System.out.println("player " + iplayer.getName() + " received end-game call");
                            break;
                        default:
                            throw new ContractException("given function is not part of the interface for iplayer");
                    }

                    sendMessage(responseDocument);
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
}
