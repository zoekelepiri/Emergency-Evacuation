import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ParseDom {
    public static void main(String[] args){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("file");
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            // Node Name
            // System.out.println(root.getNodeName());
            NodeList nListOfNodes = doc.getElementsByTagName("node");
            NodeList nListOfEdges = doc.getElementsByTagName("edge");
            // System.out.println("============================");
            System.out.println("");
            System.out.println(nListOfNodes.getLength());
            System.out.println("---------Nodes---------");
            for (int temp = 0; temp < nListOfNodes.getLength(); temp++)
            {
                Node node = nListOfNodes.item(temp);
                System.out.println("");
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    // Print each node's detail
                    Element eElement = (Element) node;
                    System.out.println("Node Id : "  + eElement.getElementsByTagName("id").item(0).getTextContent());
                    System.out.println("Label : "  + eElement.getElementsByTagName("label").item(0).getTextContent());
                }
            }
            // System.out.println("Length of nodes is: " + nListOfNodes.getLength());
            System.out.println("");
            System.out.println("--------Edges--------");
            for (int temp = 0; temp < nListOfEdges.getLength(); temp++)
            {
                Node node = nListOfEdges.item(temp);
                System.out.println("");    //Just a separator
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    // Print each edge's details
                    Element eElement = (Element) node;
                    System.out.println("Edge Id : "  + eElement.getElementsByTagName("id").item(0).getTextContent());
                    System.out.println("Label : "  + eElement.getElementsByTagName("label").item(0).getTextContent());
                    System.out.println("From : "  + eElement.getElementsByTagName("from").item(0).getTextContent());
                    System.out.println("To : "  + eElement.getElementsByTagName("to").item(0).getTextContent());
                }
            }
        }catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

}