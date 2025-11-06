package br.unesc.rotas;

import java.util.HashMap;
import java.util.Map;

public class Graph {
    
    private final Map<String, Node> nodes = new HashMap<>();

    public void addNode(String name) {
        if (!nodes.containsKey(name)) {
            nodes.put(name, new Node(name));
        }
    }

    public void addEdge(String sourceName, String destName, int weight) {
        // Garantir que os nós existam (o RouteParser já faz isso, mas é bom garantir)
        addNode(sourceName);
        addNode(destName);

        Node source = nodes.get(sourceName);
        Node destination = nodes.get(destName);

        // Adiciona arestas bidirecionais
        source.addAdjacency(new Edge(destination, weight));
        destination.addAdjacency(new Edge(source, weight));
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }

    public Map<String, Node> getAllNodes() {
        return nodes;
    }
}