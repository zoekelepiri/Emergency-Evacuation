import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GraphShow {
    public static void main(String[] args) {
        // Read the file
        File file = new File("file");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nListOfNodes = doc.getElementsByTagName("node");
            NodeList nListOfEdges = doc.getElementsByTagName("edge");
            int nodesLength = nListOfNodes.getLength();
            int edgesLength = nListOfEdges.getLength();
            // Create Object
            GraphWeighted graphWeighted = new GraphWeighted(true);
            Graph<NodeWeighted, EdgeWeighted> g = new DirectedSparseMultigraph<NodeWeighted, EdgeWeighted>();
            // Array to store info for Nodes & Edges
            NodeWeighted [] nodes = new NodeWeighted[nodesLength];
            EdgeWeighted [] edges = new EdgeWeighted[edgesLength]; // maybe does not need

            //Nodes Implementation
            for (int temp = 0; temp < nListOfNodes.getLength(); temp++)
            {
                Node node = nListOfNodes.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) node;
                    int nodeId = Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent());
                    String nodeLabel = eElement.getElementsByTagName("label").item(0).getTextContent();
                    boolean exit = Boolean.parseBoolean(eElement.getElementsByTagName("exit").item(0).getTextContent());
                    boolean shelter = Boolean.parseBoolean(eElement.getElementsByTagName("shelter").item(0).getTextContent());
                    // create an array with nodes objects
                    nodes[temp] = new NodeWeighted(nodeId, nodeLabel, exit, shelter);
                    g.addVertex(nodes[temp]);
                   // nodes[temp].display();
                }
            }
            // Edge Implementation
            for (int temp = 0; temp < edgesLength; temp++) {
                    Node node = nListOfEdges.item(temp);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) node;
                        int fromId = Integer.parseInt(eElement.getElementsByTagName("from").item(0).getTextContent());
                        int toId = Integer.parseInt(eElement.getElementsByTagName("to").item(0).getTextContent());
                        boolean disability = Boolean.parseBoolean((eElement.getElementsByTagName("from").item(0).getTextContent()));
                        int speed,weight;
                        int length = Integer.parseInt(eElement.getElementsByTagName("lengths").item(0).getTextContent());
                        // if room has people with disabilities problems
                        if(disability){

                            speed = Integer.parseInt(eElement.getElementsByTagName("disability_speed").item(0).getTextContent());
                        }
                        else{
                            speed = Integer.parseInt(eElement.getElementsByTagName("speed").item(0).getTextContent());
                        }
                        weight = length / speed; // in order to find the weight of edge
                        // create the graph with edges and nodes
                        graphWeighted.addEdge(nodes[fromId-1], nodes[toId-1], weight);
                        g.addEdge(new EdgeWeighted(nodes[fromId-1], nodes[toId-1],weight),nodes[fromId-1], nodes[toId-1]);
                    }
            }
            // Run dijkstra for each node
            for(int i = 0; i < nodesLength ; i++){
//                System.out.println("Name  " + nodes[i].exit);
                if(nodes[i].exit) {
                    // last node is the exit for the safe place (default)
                    graphWeighted.DijkstraShortestPath(nodes[i], nodes[nodesLength - 1]);
                    graphWeighted.resetNodesVisited();
                    System.out.println(' ');
                }
                else if((nodes[i].shelter) && (!nodes[i].exit)){
                    // pre-last is the shelter for safe place, if people do not have exit
                    graphWeighted.DijkstraShortestPath(nodes[i], nodes[nodesLength - 2]);
                    graphWeighted.resetNodesVisited();
                    System.out.println(' ');
                }
                else{
                    // if the node (room) has not exit & shelter
                    System.out.println("There is not a path. Stay there!");
                }
            }
            // visualization
            graphVisualization(g,nodes);

        }catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }
    private static void graphVisualization(Graph<NodeWeighted, EdgeWeighted> g,NodeWeighted [] nodes){
        VisualizationImageServer vs = new VisualizationImageServer(new CircleLayout(g), new Dimension(700, 600));
        setVertexInfo(vs);
        setEdgeInfo(vs);
        dijkstraImplementation(g,nodes,vs);
        // Window Simulation
        JFrame frame = new JFrame("NetWork Graph");
        frame.setSize(890, 800);
        frame.getContentPane().add(vs);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        JButton button = new JButton();
        button.setText("Run");
        button.setBounds(40,30,200,40);
        panel.add(button);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }

        });
    }
    private static void setVertexInfo(VisualizationImageServer vs){
        vs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller() {
            @Override
            public String transform(Object v) {

                return ((NodeWeighted)v).name;
            }});
        vs.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
    }
    private static void setEdgeInfo(VisualizationImageServer vs){
        vs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller(){
            public String transform(Object v) {
                String weight = String.valueOf(((EdgeWeighted)v).weight);
                return weight;
            }
        });
        vs.getRenderContext().setLabelOffset(30);
    }
    private static void setEdgesColour(List<EdgeWeighted> list, VisualizationImageServer vs){
        Transformer<EdgeWeighted, Paint> colorTransformer = new Transformer<EdgeWeighted, Paint>()
        {
            @Override
            public Paint transform(EdgeWeighted e)
            {
                if (list.contains(e)) {

                    return Color.RED;
                }
                return Color.BLACK;
            }
        };
        vs.getRenderContext().setArrowFillPaintTransformer(colorTransformer);
        vs.getRenderContext().setArrowDrawPaintTransformer(colorTransformer);
        vs.getRenderContext().setEdgeDrawPaintTransformer(colorTransformer);
    }
    private static void dijkstraImplementation(Graph<NodeWeighted, EdgeWeighted> g,NodeWeighted [] nodes,VisualizationImageServer vs){
        Transformer<EdgeWeighted, Double> wtTransformer = new Transformer<EdgeWeighted,Double>() {
            public Double transform(EdgeWeighted edge) {
                return edge.weight;
            }
        };
        DijkstraShortestPath<NodeWeighted, EdgeWeighted> alg = new DijkstraShortestPath(g, wtTransformer);
        List<EdgeWeighted> l;
        l = alg.getPath(nodes[0], nodes[6]);
        setEdgesColour(l,vs);
        Number dist = alg.getDistance(nodes[0], nodes[6]);
        System.out.println("The shortest path from " + nodes[0].n + " to " + nodes[6].n + " is:");
        //System.out.println(l.toString());
        System.out.println("and the length of the path is: " + dist);
    }

}