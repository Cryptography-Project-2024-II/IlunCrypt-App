package com.iluncrypt.iluncryptapp.models.attacks.brauer;

import java.util.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

/**
 * Represents the "nerve" graph (undirected) for the Brauer configuration:
 * <ul>
 *   <li>Nodes = polygons.</li>
 *   <li>Edges = an undirected edge (p1, p2) if polygons p1 and p2 share at least one vertex.</li>
 * </ul>
 * <p>
 * This ignores the cyclic ordering. It's simply about intersections of vertices.
 * </p>
 */
public class NerveGraph {
    private List<Polygon> nodes;
    private List<Edge> edges;

    /**
     * Creates an empty NerveGraph.
     */
    public NerveGraph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * Returns the list of nodes (polygons).
     *
     * @return A list of Polygon objects.
     */
    public List<Polygon> getNodes() {
        return nodes;
    }

    /**
     * Returns the list of undirected edges.
     *
     * @return A list of Edge objects.
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Builds the nerve graph from the Brauer configuration,
     * ignoring orientation. We add an edge if two polygons share at least one vertex.
     *
     * @param config The BrauerConfiguration to read from.
     */
    public void buildNerveFromConfiguration(BrauerConfiguration config) {
        nodes.clear();
        edges.clear();

        // 1. Nodos = todos los polígonos
        nodes.addAll(config.getPolygons());

        // 2. Para cada vértice, tomamos su lista de polígonos y
        //    conectamos solo pares consecutivos (i, i+1), sin wrap cíclico
        Set<Edge> edgeSet = new HashSet<>();

        for (Vertex v : config.getVertices()) {
            // Tomar la sucesión de polígonos
            List<Polygon> polyList = config.getOrientationMap().get(v);

            // Conectar (p[i], p[i+1]) para i=0..(n-2)
            for (int i = 0; i < polyList.size() - 1; i++) {
                Polygon p1 = polyList.get(i);
                Polygon p2 = polyList.get(i + 1);

                // Crear arista no dirigida
                if(p1 != p2){
                    Edge e = new Edge(p1, p2);
                    edgeSet.add(e);
                }

            }
        }

        edges.addAll(edgeSet);
    }

    /**
     * Checks if two polygons share at least one vertex.
     *
     * @param p1 The first polygon.
     * @param p2 The second polygon.
     * @return True if there is a common vertex, false otherwise.
     */
    private boolean shareCommonVertex(Polygon p1, Polygon p2) {
        Set<Vertex> set1 = new HashSet<>(p1.getVertices());
        for (Vertex v : p2.getVertices()) {
            if (set1.contains(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the nerve graph is connected (undirected).
     *
     * @return True if all nodes are in one connected component.
     */
    public boolean isConnected() {
        if (nodes.isEmpty()) return true;

        Set<Polygon> visited = new HashSet<>();
        Queue<Polygon> queue = new LinkedList<>();

        Polygon start = nodes.get(0);
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            Polygon current = queue.poll();
            List<Polygon> neighbors = getNeighbors(current);
            for (Polygon nb : neighbors) {
                if (!visited.contains(nb)) {
                    visited.add(nb);
                    queue.add(nb);
                }
            }
        }
        return visited.size() == nodes.size();
    }

    /**
     * Returns the neighbors of a polygon in this undirected nerve.
     *
     * @param p The polygon to examine.
     * @return A list of adjacent polygons.
     */
    private List<Polygon> getNeighbors(Polygon p) {
        List<Polygon> result = new ArrayList<>();
        for (Edge e : edges) {
            if (e.getP1().equals(p) && !result.contains(e.getP2())) {
                result.add(e.getP2());
            } else if (e.getP2().equals(p) && !result.contains(e.getP1())) {
                result.add(e.getP1());
            }
        }
        return result;
    }

    /**
     * Displays the nerve graph using GraphStream in an undirected manner.
     * <p>
     * Each node is labeled by the polygon name.
     * Edges have no special label by default.
     * </p>
     */
    public void displayNerveGraph() {
        Graph graph = new SingleGraph("Nerve");

        // Add nodes
        for (Polygon p : nodes) {
            String nodeId = p.getName();
            Node node = graph.addNode(nodeId);
            node.setAttribute("ui.label", nodeId);
        }

        // Add edges
        int edgeCounter = 0;
        for (Edge e : edges) {
            String p1Id = e.getP1().getName();
            String p2Id = e.getP2().getName();
            String edgeId = "E" + (edgeCounter++);
            org.graphstream.graph.Edge gsEdge = graph.addEdge(edgeId, p1Id, p2Id);
            // We do not label these edges by default. If you want, you can do:
            // gsEdge.setAttribute("ui.label", "shared");
        }

        // Optional styling
        graph.setAttribute("ui.stylesheet", getDefaultStylesheet());
        graph.display();
    }

    /**
     * Returns a default stylesheet for GraphStream to make labels visible.
     *
     * @return A string with CSS for node and edge styles.
     */
    private String getDefaultStylesheet() {
        return "node {" +
                "   fill-color: pink;" +
                "   text-size: 16;" +
                "   size: 30px;" +
                "   text-alignment: above;" +
                "}" +
                "edge {" +
                "   fill-color: gray;" +
                "   text-size: 14;" +
                "}";
    }
}
