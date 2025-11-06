package br.unesc.rotas;

import java.util.ArrayList; 
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    
    public static final int INFINITY = 2147483647; 

    public static void computePaths(Node source) {
        source.setMinDistance(0);
        
        // PriorityQueue usa o método compareTo da classe Node
        PriorityQueue<Node> nodeQueue = new PriorityQueue<Node>();
        nodeQueue.add(source);

        while (!nodeQueue.isEmpty()) {
            Node u = nodeQueue.poll(); 

            for (Edge e : u.getAdjacencies()) {
                Node v = e.getTarget();
                int weight = e.getWeight();
                int distanceThroughU = u.getMinDistance() + weight;

                if (distanceThroughU < v.getMinDistance()) {
                    nodeQueue.remove(v); 
                    
                    v.setMinDistance(distanceThroughU);
                    v.setPrevious(u);
                    nodeQueue.add(v);
                }
            }
        }
    }

    public static List<Node> getShortestPathTo(Node target) {
        List<Node> path = new ArrayList<Node>(); 
        for (Node node = target; node != null; node = node.getPrevious()) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }
}