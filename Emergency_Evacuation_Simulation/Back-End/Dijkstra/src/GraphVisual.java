import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphVisual {
        public static void main(String[] args) {
            // Read the file
            File file = new File("file4");
            List<NodeWeighted> endNodes = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try{
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(file);
                doc.getDocumentElement().normalize();
                // Get nodes & edges
                NodeList nListOfNodes = doc.getElementsByTagName("node");
                NodeList nListOfEdges = doc.getElementsByTagName("edge");
                int nodesLength = nListOfNodes.getLength();
                int edgesLength = nListOfEdges.getLength();
                // Create Object
                GraphWeighted graphWeighted = new GraphWeighted(true);
                Graph<NodeWeighted, EdgeWeighted> g = new DirectedSparseMultigraph<>();
                // Array to store info for Nodes & Edges
                NodeWeighted [] nodes = new NodeWeighted[nodesLength];
                NodeWeighted [] emergencyNodes =new NodeWeighted[nodesLength];
                EdgeWeighted [] edges = new EdgeWeighted[edgesLength];
                int count = 0;


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
                        boolean fire = Boolean.parseBoolean(eElement.getElementsByTagName("fire").item(0).getTextContent());
                        boolean co = Boolean.parseBoolean(eElement.getElementsByTagName("co").item(0).getTextContent());
                        boolean co2 = Boolean.parseBoolean(eElement.getElementsByTagName("co2").item(0).getTextContent());
                        boolean flood = Boolean.parseBoolean(eElement.getElementsByTagName("flood").item(0).getTextContent());
                        boolean water = Boolean.parseBoolean(eElement.getElementsByTagName("water").item(0).getTextContent());
                        // create an array with nodes objects
                        nodes[temp] = new NodeWeighted(nodeId, nodeLabel, exit, shelter);
                        if(exit){
                           // endNode = nodes[temp];
                            // add nodes with exit
                            endNodes.add(nodes[temp]);
                        }
                        if((shelter) && !(exit)){
                            // add nodes with exit
                            endNodes.add(nodes[temp]);
                        }
                        // check if the node has a dangerous phenomenon
                        if(fire || co || co2 || flood || water){
                            emergencyNodes[count] = nodes[temp];
                            count++;
                            // System.out.println(emergencyNode.name);
                        }
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
                        edges[temp] = new EdgeWeighted(nodes[fromId-1], nodes[toId-1], weight);
                        g.addEdge(new EdgeWeighted(nodes[fromId-1], nodes[toId-1],weight),nodes[fromId-1], nodes[toId-1]);
                    }
                }


                // change the length of emergency nodes
               emergencyNodes = Arrays.copyOf(emergencyNodes,count);
                for(int i=0 ; i<endNodes.size();i++){
                    System.out.println(endNodes.get(i).name);
                }
                /*
                // TEST
                // size of endNodes
                int endNodesLength = endNodes.size();
                System.out.println("Length of list " + endNodesLength);
                //
                for(NodeWeighted node:endNodes)
                    System.out.println("Node is: " +node.name);
                // Run dijkstra for each node
                for(int i = 0; i < nodesLength ; i++) {
                   // if (endNode != null) {
                     if(!endNodes.isEmpty()){
                        // Make the nodes which have dangerous phenomena visited
                        // Exclude them for the shortest path
                        for (NodeWeighted emergencyNode : emergencyNodes) {
                            emergencyNode.visit();
                        }
                        for (NodeWeighted node : endNodes) {
                            graphWeighted.DijkstraShortestPath(nodes[i], node);
                            graphWeighted.resetNodesVisited();
                            System.out.println(" ");
                            // graph

                        }
                    }else {
                        System.out.println("For node " + nodes[i].name + " stay here!");
                    }
                }*/

                graphVisualization(g,emergencyNodes, edges, endNodes);

            }catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }
        private static void graphVisualization(Graph<NodeWeighted, EdgeWeighted> g,NodeWeighted [] nodes, EdgeWeighted [] edges, List<NodeWeighted> endNodes){
            Layout layout = new CircleLayout(g);
            layout.setSize(new Dimension(500, 500)); // sets the initial size of the space
            VisualizationViewer<NodeWeighted, EdgeWeighted> vs = new VisualizationViewer<>(new CircleLayout<>(g), new Dimension(400, 400)); // visualize the window
            // properties for nodes/edges
            setVertexInfo(vs);
            setEdgeInfo(vs);
            // Mouse event- click method
            final DefaultModalGraphMouse<String, Number> graphMouse = new DefaultModalGraphMouse<>();
            graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
            vs.setGraphMouse(graphMouse);
            final PickedState<NodeWeighted> pickedState = vs.getPickedVertexState();
            // For each node we clicked, dijkstra algorithm runs
            pickedState.addItemListener(e -> {
                Object subject = e.getItem();
                // The graph uses Integers for vertices.
                if (subject instanceof NodeWeighted) {
                    NodeWeighted startNode = (NodeWeighted) subject;
                    if (pickedState.isPicked(startNode)) {
                        Transformer<NodeWeighted, Paint> colorTransformer = node-> {
                            // selected node yellow colour
                            if(node.equals(startNode)){
                                return Color.YELLOW;
                            }
                            // end node green colour
                            if(endNodes.contains(node)){
                                return Color.GREEN;
                            }
                            // other nodes
                            return Color.RED;

                        };
                        vs.getRenderContext().setVertexFillPaintTransformer(colorTransformer);
                        System.out.println("-Vertex " + startNode.name + " is now selected");
                        dijkstraImplementation(g, nodes, edges, vs, startNode, endNodes);
                        System.out.println(' ');
                    }
                }
            });
            // Window Simulation
            JFrame frame = new JFrame("NetWork Graph");
            frame.setSize(500, 500);
            frame.getContentPane().add(vs);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

        }
        private static void setVertexInfo( VisualizationViewer vs){
            vs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller() {
                @Override
                public String transform(Object v) {

                    return ((NodeWeighted)v).name;
                }});
            vs.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        }
        private static void setEdgeInfo( VisualizationViewer vs) {
            vs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller() {
                public String transform(Object v) {
                    return String.valueOf(((EdgeWeighted) v).weight);
                }
            });
            vs.getRenderContext().setLabelOffset(30);
        }
        private static void setEdgesColour(java.util.List<EdgeWeighted> list,  VisualizationViewer vs){
            Transformer<EdgeWeighted, Paint> colorTransformer = e -> {
                if (list.contains(e)) {

                    return Color.RED;
                }
                return Color.BLACK;
            };
            vs.getRenderContext().setArrowFillPaintTransformer(colorTransformer);
            vs.getRenderContext().setArrowDrawPaintTransformer(colorTransformer);
            vs.getRenderContext().setEdgeDrawPaintTransformer(colorTransformer);
        }
        private static void dijkstraImplementation(Graph<NodeWeighted, EdgeWeighted> g, NodeWeighted [] emergencyNodes, EdgeWeighted [] edges, VisualizationViewer vs, NodeWeighted startNode, List<NodeWeighted> endNodes){
            Transformer<EdgeWeighted, Double> wtTransformer = edge -> edge.weight;
            DijkstraShortestPath<NodeWeighted, EdgeWeighted> alg = new DijkstraShortestPath(g, wtTransformer);
            List<EdgeWeighted> l;
            EdgeWeighted[] emergencyEdges = retrieveDangerousEdges(emergencyNodes, edges);
            double minPath = 100;
            double distance;
            NodeWeighted endNode = null;
            if(!endNodes.isEmpty()){
                // If we have nodes with dangerous phenomena delete their edges
                // But when we click in dangerous nodes we can see their shortest path to the exit
                for (EdgeWeighted emergencyEdge : emergencyEdges) {
                    for (NodeWeighted emergencyNode : emergencyNodes) {
                        if (emergencyNode.equals(emergencyEdge.source)) {
                            g.removeVertex(emergencyNode);
                            g.addVertex(emergencyNode);
                            g.addEdge(emergencyEdge, emergencyEdge.source, emergencyEdge.destination);
                        }
                    }
                }
                for(int j = 0; j < endNodes.size(); j++){
                    Number dist = alg.getDistance(startNode, endNodes.get(j));
                    if(dist != null) {
                        distance = (double) dist;
                        if (distance < minPath) {
                            endNode = endNodes.get(j);
                            minPath = distance;
                        }
                    }
                }
                if(minPath != 100){
                    l = alg.getPath(startNode, endNode);
                    setEdgesColour(l, vs);
                    System.out.println(l);
                    System.out.println("The shortest path from " + startNode.n + " to " + endNode.n + " is:");
                    System.out.println("The length of the path is: " + minPath);
                }
                else{
                    System.out.println("There is no place!Stay here!");
                }
            }
            else{
                System.out.println("Stay here");
            }
        }
        private static EdgeWeighted[] retrieveDangerousEdges(NodeWeighted [] nodes, EdgeWeighted [] edges){
            EdgeWeighted [] netEdges = new EdgeWeighted[edges.length];
            int k = 0;
            for (EdgeWeighted edge : edges) {
                for (NodeWeighted node : nodes) {
                    // Outcome dangerous edges
                    if (edge.source.equals(node)) {
                        netEdges[k] = new EdgeWeighted(edge.source, edge.destination, edge.weight);
                        k++;
                    }
                    // Income dangerous edges
                    if (edge.destination.equals(node)) {
                        netEdges[k] = new EdgeWeighted(edge.source, edge.destination, edge.weight);
                        k++;
                    }
                }
            }
            // resize the array
            netEdges = Arrays.copyOf(netEdges, k);
            return netEdges;
        }

}

