package br.unesc.rotas;

import java.util.List;
import java.util.ArrayList;

public class Node implements Comparable<Node> {
    
    private final String name;
    private List<Edge> adjacencies = new ArrayList<>();
    private int minDistance = Dijkstra.INFINITY;
    private Node previous;

    public Node(String name) {
        this.name = name;
    }
    
    public void addAdjacency(Edge edge) {
        this.adjacencies.add(edge);
    }

    public String getName() {
        return name;
    }

    public List<Edge> getAdjacencies() {
        return adjacencies;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(minDistance, other.minDistance);
    }
    
    @Override
    public String toString() {
        return name;
    }
}