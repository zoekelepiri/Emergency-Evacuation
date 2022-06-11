public class EdgeWeighted implements Comparable<EdgeWeighted> {

    NodeWeighted source;
    NodeWeighted destination;
    double weight;

    EdgeWeighted(NodeWeighted s, NodeWeighted d, double w) {
        source = s;
        destination = d;
        weight = w;
    }

    public String toString() {
        return String.format("(%s -> %s, %.2f)", source.name, destination.name, weight);
    }

    public int compareTo(EdgeWeighted otherEdge) {
        if (this.weight > otherEdge.weight) {
            return 1;
        }
        else return -1;
    }
}