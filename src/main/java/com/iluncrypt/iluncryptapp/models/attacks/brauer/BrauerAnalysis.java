package com.iluncrypt.iluncryptapp.models.attacks.brauer;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to analyze a Brauer configuration from a given text and polygon size.
 * <ul>
 *   <li>{@link #getVerticesInfo(String, int)}: Vertex info (valency, multiplicity, truncated).</li>
 *   <li>{@link #getSuccessorSequences(String, int)}: Successor sequences (normal + circular).</li>
 *   <li>{@link #getDimensionsInfo(String, int)}: Algebra dimension and center dimension.</li>
 *   <li>{@link #showQuiver(String, int)}: Displays the quiver in a JavaFX window.</li>
 *   <li>{@link #showNerve(String, int)}: Displays the nerve in a JavaFX window.</li>
 * </ul>
 */
public class BrauerAnalysis {

    /**
     * Tracks whether JavaFX has been initialized to avoid multiple Platform.startup calls.
     */
    private static boolean javafxInitialized = false;

    /**
     * Builds a BrauerConfiguration from the given text, partitioning the text
     * into polygons of the specified size. Then computes valencies, adjusts multiplicities,
     * marks truncated vertices, and builds successor sequences.
     *
     * @param text         The input text.
     * @param polygonSize  The size of each polygon (number of characters).
     * @return A fully prepared BrauerConfiguration.
     */
    private static BrauerConfiguration buildConfiguration(String text, int polygonSize) {
        // 1) Distinct letters => vertices
        List<Character> distinctLetters = getDistinctLetters(text);
        Map<Character, Vertex> letterToVertex = new HashMap<>();
        for (char c : distinctLetters) {
            letterToVertex.put(c, new Vertex(String.valueOf(c), 1));
        }

        // 2) Partition the text into polygons
        List<Polygon> polygonList = partitionTextIntoPolygons(text, polygonSize, letterToVertex);

        // 3) Build BrauerConfiguration
        BrauerConfiguration config = new BrauerConfiguration();
        // add vertices
        for (Vertex v : letterToVertex.values()) {
            config.addVertex(v);
        }
        // add polygons
        for (Polygon p : polygonList) {
            config.addPolygon(p);
        }

        // 4) Compute steps
        config.computeValencies();
        config.adjustMultiplicities();
        config.computeTruncatedVertices();
        config.buildSuccessorSequences();

        return config;
    }

    /**
     * Returns a formatted text with valency, multiplicity, and truncated info
     * for each vertex in the Brauer configuration built from the given text and polygon size.
     *
     * @param text         The input text.
     * @param polygonSize  The polygon size.
     * @return A string describing each vertex: label, val, mu, truncated.
     */
    public static String getVerticesInfo(String text, int polygonSize) {
        BrauerConfiguration config = buildConfiguration(text, polygonSize);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Vertices Info ===\n");
        for (Vertex v : config.getVertices()) {
            sb.append(String.format("Vertex: %s | val=%d | mu=%d%n",
                    v.getLabel(), v.getValency(), v.getMultiplicity()));
        }
        return sb.toString();
    }

    /**
     * Returns a formatted text with the successor sequences of each vertex,
     * showing both normal and circular parts. For example:
     * L1->L2 and then ->(L1) for the circular wrap.
     *
     * @param text         The input text.
     * @param polygonSize  The polygon size.
     * @return A string describing each vertex's successor sequence.
     */
    public static String getSuccessorSequences(String text, int polygonSize) {
        BrauerConfiguration config = buildConfiguration(text, polygonSize);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Successor Sequences ===\n");

        for (Vertex v : config.getVertices()) {
            List<Polygon> polyList = config.getOrientationMap().get(v);
            if (polyList.isEmpty()) continue;

            sb.append("Vertex ").append(v.getLabel()).append(": ");
            // Normal sequence
            for (int i = 0; i < polyList.size(); i++) {
                sb.append(polyList.get(i).getName());
                if (i < polyList.size() - 1) {
                    sb.append("->");
                }
            }
            // Circular wrap
            if (polyList.size() >= 1) {
                sb.append("->(").append(polyList.get(0).getName()).append(")");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a formatted text with the dimension of the algebra and
     * the dimension of its center, based on the formula used in BrauerConfiguration.
     *
     * @param text        The input text.
     * @param polygonSize The polygon size.
     * @return A string describing dim(Algebra) and dim(Center).
     */
    public static String getDimensionsInfo(String text, int polygonSize) {
        BrauerConfiguration config = buildConfiguration(text, polygonSize);

        int dimAlg = config.computeAlgebraDimension();
        int dimCenter = config.computeCenterDimension();

        return String.format("=== Dimensions ===%nAlgebra dimension: %d%nCenter dimension: %d%n",
                dimAlg, dimCenter);
    }

    /**
     * Builds and displays the quiver in a JavaFX window.
     * <p>
     * This method starts the JavaFX runtime if not already initialized,
     * creates a new stage, and renders the quiver using FxViewer.
     * </p>
     *
     * @param text        The input text.
     * @param polygonSize The polygon size.
     */
    public static void showQuiver(String text, int polygonSize) {
        // 1) Build config and quiver
        BrauerConfiguration config = buildConfiguration(text, polygonSize);
        Quiver quiver = new Quiver();
        quiver.buildFromConfiguration(config);

        // 2) Build a GraphStream Graph from the quiver
        org.graphstream.graph.Graph gsGraph = buildFxGraphFromQuiver(quiver);

        Platform.runLater(() -> {
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Quiver Viewer");

            FxViewer viewer = new FxViewer((MultiGraph) gsGraph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            viewer.enableAutoLayout();
            FxViewPanel panel = (FxViewPanel) viewer.addDefaultView(false, new FxGraphRenderer());

            stage.setScene(new Scene(panel, 800, 600));
            stage.show();
        });
    }

    /**
     * Builds and displays the nerve in a JavaFX window.
     * <p>
     * This method starts the JavaFX runtime if not already initialized,
     * creates a new stage, and renders the nerve using FxViewer.
     * </p>
     *
     * @param text        The input text.
     * @param polygonSize The polygon size.
     */
    public static void showNerve(String text, int polygonSize) {
        // 1) Build config and nerve
        BrauerConfiguration config = buildConfiguration(text, polygonSize);
        NerveGraph nerve = new NerveGraph();
        nerve.buildNerveFromConfiguration(config);

        // 2) Build a GraphStream Graph from the nerve
        org.graphstream.graph.Graph gsGraph = buildFxGraphFromNerve(nerve);

        Platform.runLater(() -> {
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Nerve Viewer");

            FxViewer viewer = new FxViewer((MultiGraph) gsGraph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            viewer.enableAutoLayout();
            FxViewPanel panel = (FxViewPanel) viewer.addDefaultView(false, new FxGraphRenderer());

            stage.setScene(new Scene(panel, 800, 600));
            stage.show();
        });
    }

    // ------------------------------------------------------------------------
    // PRIVATE UTILITY METHODS
    // ------------------------------------------------------------------------

    /**
     * Partitions the text into polygons of size 'polygonSize'.
     * If the text length is not a multiple of polygonSize,
     * the last polygon will be shorter.
     */
    private static List<Polygon> partitionTextIntoPolygons(String text, int polygonSize,
                                                           Map<Character, Vertex> letterMap) {
        List<Polygon> result = new ArrayList<>();
        int start = 0;
        int n = text.length();
        int polyCount = 0;

        while (start < n) {
            int end = Math.min(start + polygonSize, n);
            String sub = text.substring(start, end);
            Polygon poly = new Polygon("L" + (++polyCount));
            for (int i = 0; i < sub.length(); i++) {
                char c = sub.charAt(i);
                poly.addVertex(letterMap.get(c));
            }
            result.add(poly);
            start += polygonSize;
        }
        return result;
    }

    /**
     * Collects distinct letters from the string, preserving the order of appearance.
     */
    private static List<Character> getDistinctLetters(String text) {
        List<Character> result = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!result.contains(c)) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Creates a GraphStream Graph from a Quiver, labeling edges with arrow.getInducedByLabel().
     */
    private static org.graphstream.graph.Graph buildFxGraphFromQuiver(Quiver quiver) {
        org.graphstream.graph.Graph graph = new MultiGraph("QuiverGraph");

        // Add nodes
        for (Polygon p : quiver.getNodes()) {
            String nodeId = p.getName();
            org.graphstream.graph.Node node = graph.addNode(nodeId);
            node.setAttribute("ui.label", nodeId);
        }

        // Add edges (directed)
        int edgeCount = 0;
        for (Arrow arrow : quiver.getArrows()) {
            String sourceId = arrow.getSource().getName();
            String targetId = arrow.getTarget().getName();
            String edgeId = "E" + (edgeCount++);
            org.graphstream.graph.Edge e = graph.addEdge(edgeId, sourceId, targetId, true);
            e.setAttribute("ui.label", arrow.getInducedByLabel());
        }

        // Optional style
        graph.setAttribute("ui.stylesheet", getQuiverStylesheet());
        return graph;
    }

    /**
     * Creates a GraphStream Graph from a NerveGraph, ignoring direction.
     */
    private static org.graphstream.graph.Graph buildFxGraphFromNerve(NerveGraph nerve) {
        org.graphstream.graph.Graph graph = new MultiGraph("NerveGraph");

        // Add nodes
        for (Polygon p : nerve.getNodes()) {
            String nodeId = p.getName();
            org.graphstream.graph.Node node = graph.addNode(nodeId);
            node.setAttribute("ui.label", nodeId);
        }

        // Add edges (undirected)
        int edgeCount = 0;
        for (Edge ed : nerve.getEdges()) {
            String p1Id = ed.getP1().getName();
            String p2Id = ed.getP2().getName();
            String edgeId = "E" + (edgeCount++);
            graph.addEdge(edgeId, p1Id, p2Id);
        }

        // Optional style
        graph.setAttribute("ui.stylesheet", getNerveStylesheet());
        return graph;
    }

    /**
     * Sample stylesheet for the quiver (directed).
     */
    private static String getQuiverStylesheet() {
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

    /**
     * Sample stylesheet for the nerve (undirected).
     */
    private static String getNerveStylesheet() {
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
