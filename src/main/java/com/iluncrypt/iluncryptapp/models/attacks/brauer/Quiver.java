package com.iluncrypt.iluncryptapp.models.attacks.brauer;

import java.util.*;
import org.graphstream.graph.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.*;

/**
 * A quiver (directed graph) associated with a Brauer configuration.
 * <p>
 * Nodes = polygons (Gamma_1).
 * Arrows = directed edges induced by the cyclic ordering around non-truncated vertices.
 * Each arrow is labeled by the vertex that induces it.
 * </p>
 */
public class Quiver {
    private List<Polygon> nodes;
    private List<Arrow> arrows;

    /**
     * Constructs an empty Quiver.
     */
    public Quiver() {
        this.nodes = new ArrayList<>();
        this.arrows = new ArrayList<>();
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
     * Returns the list of arrows (directed edges).
     *
     * @return A list of Arrow objects.
     */
    public List<Arrow> getArrows() {
        return arrows;
    }

    /**
     * Builds the quiver from a BrauerConfiguration.
     * <p>
     * Algorithm:
     * <ul>
     *   <li>Each polygon in config is a node.</li>
     *   <li>For each non-truncated vertex v, we retrieve its ordered list of polygons
     *       (orientationMap[v]) and link them in a cycle:
     *       p[i] -> p[i+1], p[last] -> p[0], labeled by v.getLabel().</li>
     * </ul>
     *
     * @param config The BrauerConfiguration to build from.
     */
    public void buildFromConfiguration(BrauerConfiguration config) {
        this.nodes.clear();
        this.arrows.clear();

        // 1. Add all polygons as nodes
        this.nodes.addAll(config.getPolygons());

        // 2. Prepare a set for arrows, and a map to track arrow indices per vertex
        Set<Arrow> arrowSet = new HashSet<>();
        Map<Vertex, Integer> arrowIndices = new HashMap<>();

        // 3. For each non-truncated vertex, connect polygons in a cycle
        for (Vertex v : config.getVertices()) {
            if (!v.isTruncated()) {
                List<Polygon> polyList = config.getOrientationMap().get(v);
                int n = polyList.size();

                for (int i = 0; i < n; i++) {
                    Polygon source = polyList.get(i);
                    Polygon target = polyList.get((i + 1) % n);

                    // Get current index for this vertex
                    int currentIndex = arrowIndices.getOrDefault(v, 1);

                    // Build label: e.g. "\alpha_1_L"
                    String arrowLabel = "α(" + currentIndex + "," + v.getLabel()+")";

                    // Create the arrow with that label
                    Arrow arrow = new Arrow(source, target, arrowLabel);
                    arrowSet.add(arrow);

                    // Update index
                    arrowIndices.put(v, currentIndex + 1);
                }
            }
        }

        this.arrows.addAll(arrowSet);
    }

    /**
     * Checks if the quiver is connected in a "weak" sense:
     * ignoring the direction of arrows.
     *
     * @return True if all nodes are in a single connected component (undirected).
     */
    public boolean isConnected() {
        if (nodes.isEmpty()) return true;

        Set<Polygon> visited = new HashSet<>();
        Queue<Polygon> queue = new LinkedList<>();

        // Start BFS from the first node
        Polygon start = nodes.get(0);
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            Polygon current = queue.poll();
            // Undirected neighbors
            List<Polygon> neighbors = getUndirectedNeighbors(current);
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
     * Returns the undirected neighbors of a polygon p
     * by ignoring arrow direction.
     *
     * @param p The polygon to examine.
     * @return A list of adjacent polygons ignoring direction.
     */
    private List<Polygon> getUndirectedNeighbors(Polygon p) {
        List<Polygon> result = new ArrayList<>();
        for (Arrow a : arrows) {
            if (a.getSource().equals(p) && !result.contains(a.getTarget())) {
                result.add(a.getTarget());
            }
            if (a.getTarget().equals(p) && !result.contains(a.getSource())) {
                result.add(a.getSource());
            }
        }
        return result;
    }

    /**
     * Displays the quiver graphically using GraphStream, allowing parallel edges or loops.
     * <p>
     * Each node is labeled with its polygon name, and each directed edge is labeled
     * with the vertex (letter) that induces it. This method uses a <b>MultiGraph</b>
     * to avoid the <code>EdgeRejectedException</code> that would occur if multiple edges
     * or loops existed in a <code>SingleGraph</code>.
     * </p>
     * <p><b>Note:</b> You must have the GraphStream library in your classpath for this to work.</p>
     */
    public void displayQuiverGraph() {
        // Create a MultiGraph to allow parallel edges and loops
        org.graphstream.graph.Graph graph = new org.graphstream.graph.implementations.MultiGraph("Quiver");

        // 1. Add all nodes (polygons) to the graph
        for (Polygon p : nodes) {
            String nodeId = p.getName();
            org.graphstream.graph.Node node = graph.addNode(nodeId);
            // Label the node for display
            node.setAttribute("ui.label", nodeId);
        }

        // 2. Add edges (arrows)
        int edgeCounter = 0;
        for (Arrow arrow : arrows) {
            String sourceId = arrow.getSource().getName();
            String targetId = arrow.getTarget().getName();

            // Build a unique edge ID
            String edgeId = "E" + (edgeCounter++);

            // true => directed edge
            org.graphstream.graph.Edge gsEdge = graph.addEdge(edgeId, sourceId, targetId, true);

            // Label the edge with the vertex (letter) that induces it
            gsEdge.setAttribute("ui.label", arrow.getInducedByLabel());
        }

        // 3. Apply optional styling
        graph.setAttribute("ui.stylesheet", getDefaultStylesheet());

        // 4. Display the graph
        graph.display();
    }


    /**
     * Returns a default stylesheet for GraphStream to make labels visible.
     *
     * @return A string with CSS for node and edge labels.
     */
    private String getDefaultStylesheet() {
        return "node {" +
                "   fill-color: lightblue;" +
                "   text-size: 16;" +
                "   size: 30px;" +
                "   text-alignment: above;" +
                "}" +
                "edge {" +
                "   fill-color: gray;" +
                "   text-size: 14;" +
                "   arrow-size: 12, 8;" +
                "}";
    }
}
