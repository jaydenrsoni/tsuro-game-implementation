//package main;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//public class NetworkPlayerThread extends Thread {
//
//    private Socket socket;
//
//    NetworkPlayerThread(Socket socket) {
//        super("NetworkPlayerThread");
//        this.socket = socket;
//    }
//
//    public void run() {
//        try
//        {
//            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
////            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            String inputLine, outputLine;
//
//
//
//
//            while ((inputLine = in.readLine()) != null) {
//                outputLine = kkp.processInput(inputLine);
//                out.println(outputLine);
//                if (outputLine.equals("Bye"))
//                    break;
//            }
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e ) {
//            e.printStackTrace();
//        }
//    }
//}
