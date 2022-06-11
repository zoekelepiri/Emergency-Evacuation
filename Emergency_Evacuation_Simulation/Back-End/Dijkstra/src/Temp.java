import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Temp {
    private static void graphVisualization(Graph<NodeWeighted, EdgeWeighted> g, NodeWeighted [] nodes, EdgeWeighted [] edges, NodeWeighted endNode){
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
                    System.out.println("-Vertex " + startNode.name + " is now selected");
                    dijkstraImplementation(g, nodes, edges, vs, startNode, endNode);
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
    private static void dijkstraImplementation(Graph<NodeWeighted, EdgeWeighted> g, NodeWeighted [] emergencyNodes, EdgeWeighted [] edges, VisualizationViewer vs, NodeWeighted startNode, NodeWeighted endNode){
        Transformer<EdgeWeighted, Double> wtTransformer = edge -> edge.weight;
        DijkstraShortestPath<NodeWeighted, EdgeWeighted> alg = new DijkstraShortestPath(g, wtTransformer);
        List<EdgeWeighted> l;
        EdgeWeighted[] emergencyEdges = retrieveDangerousEdges(emergencyNodes, edges);
        // In case we have path for exit or shelter
        if(endNode != null) {
            // this work
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
            // Get the path
            l = alg.getPath(startNode, endNode);
            setEdgesColour(l, vs);
            Number dist = alg.getDistance(startNode, endNode);
            System.out.println("The shortest path from " + startNode.n + " to " + endNode.n + " is:");
            System.out.println(l);
            System.out.println("The length of the path is: " + dist);
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
