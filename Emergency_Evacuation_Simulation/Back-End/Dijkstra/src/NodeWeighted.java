import java.util.LinkedList;
public class NodeWeighted {
    int n;
    String name;
    boolean exit, shelter;
    private boolean visited;
    LinkedList<EdgeWeighted> edges;
    NodeWeighted(int n, String name, boolean exit, boolean shelter) {
        this.n = n;
        this.name = name;
        this.exit = exit;
        this.shelter = shelter;
        visited = false;
        edges = new LinkedList<>();
    }
    boolean isVisited() {
        return visited;
    }
    void visit() {
        visited = true;
    }
    void unvisit() {
        visited = false;
    }

    public void display()
    {
        System.out.println("Node ID " + n + " "
                + "NodeLabel "
                + name);
        System.out.println();
    }
}
