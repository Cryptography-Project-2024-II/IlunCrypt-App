package com.iluncrypt.iluncryptapp.models.attacks.brauer;

import javafx.application.Application;
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
 * Example JavaFX application that builds a BrauerConfiguration from
 * the string "PERMUTATIONENCRYPTEDMESSAGES" and displays both:
 * <ul>
 *   <li>The quiver (directed graph) in a JavaFX window.</li>
 *   <li>The nerve (undirected graph) in a separate JavaFX window.</li>
 * </ul>
 * <p>
 * It uses GraphStream 2.0 with the gs-ui-javafx module to render
 * the graphs inside JavaFX scenes.
 * </p>
 */
public class MainExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // ----------------------------------------------------
        // 1. Prepare data: text, polygons, brauer config
        // ----------------------------------------------------
        String text = "PERMUTATIONENCRYPTEDMESSAGES";
        System.out.println("Text: " + text + " (Length: " + text.length() + ")");

        // Polygons of length 7 each
        String L1 = text.substring(0, 7);    // "PERMUTA"
        String L2 = text.substring(7, 14);   // "TIONENC"
        String L3 = text.substring(14, 21);  // "RYPTEDM"
        String L4 = text.substring(21, 28);  // "ESSAGES"

        // Distinct letters => vertices
        List<Character> distinctLetters = getDistinctLetters(text);
        Map<Character, Vertex> letterToVertex = new HashMap<>();
        for (char c : distinctLetters) {
            letterToVertex.put(c, new Vertex(String.valueOf(c), 1));
        }

        // Build polygons
        Polygon p1 = new Polygon("L1");
        addLettersToPolygon(p1, L1, letterToVertex);

        Polygon p2 = new Polygon("L2");
        addLettersToPolygon(p2, L2, letterToVertex);

        Polygon p3 = new Polygon("L3");
        addLettersToPolygon(p3, L3, letterToVertex);

        Polygon p4 = new Polygon("L4");
        addLettersToPolygon(p4, L4, letterToVertex);

        // Build Brauer configuration
        BrauerConfiguration config = new BrauerConfiguration();
        for (Vertex v : letterToVertex.values()) {
            config.addVertex(v);
        }
        config.addPolygon(p1);
        config.addPolygon(p2);
        config.addPolygon(p3);
        config.addPolygon(p4);

        // Compute valencies, multiplicities, truncated, etc.
        config.computeValencies();
        config.adjustMultiplicities();
        config.computeTruncatedVertices();
        config.buildSuccessorSequences();

        // Print vertex info
        System.out.println("\n=== Vertices ===");
        for (Vertex v : config.getVertices()) {
            System.out.printf("Letter: %s | val=%d | mu=%d | truncated=%s\n",
                    v.getLabel(), v.getValency(), v.getMultiplicity(), v.isTruncated());
        }

        // Compute dimensions
        int dimAlg = config.computeAlgebraDimension();
        int dimCenter = config.computeCenterDimension();
        System.out.println("\nAlgebra dimension: " + dimAlg);
        System.out.println("Center dimension: " + dimCenter);

        // ----------------------------------------------------
        // 2. Build quiver
        // ----------------------------------------------------
        Quiver quiver = new Quiver();
        quiver.buildFromConfiguration(config);
        System.out.println("\nIs quiver weakly connected? " + quiver.isConnected());

        // Build a GraphStream Graph from the Quiver
        Graph quiverGraph = buildFxGraphFromQuiver(quiver);

        // Create a JavaFX Scene for the quiver
        FxViewer quiverViewer = new FxViewer((MultiGraph) quiverGraph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        quiverViewer.enableAutoLayout();
        FxViewPanel quiverPanel = (FxViewPanel) quiverViewer.addDefaultView(false, new FxGraphRenderer());

        Stage quiverStage = new Stage();
        quiverStage.setTitle("Quiver (GraphStream + JavaFX)");
        quiverStage.setScene(new Scene(quiverPanel, 800, 600));
        quiverStage.show();

        // ----------------------------------------------------
        // 3. Build nerve
        // ----------------------------------------------------
        NerveGraph nerve = new NerveGraph();
        nerve.buildNerveFromConfiguration(config);
        System.out.println("\nIs nerve connected? " + nerve.isConnected());

        // Build a GraphStream Graph from the Nerve
        Graph nerveGraph = buildFxGraphFromNerve(nerve);

        FxViewer nerveViewer = new FxViewer((MultiGraph) nerveGraph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        nerveViewer.enableAutoLayout();
        FxViewPanel nervePanel = (FxViewPanel) nerveViewer.addDefaultView(false, new FxGraphRenderer());

        Stage nerveStage = new Stage();
        nerveStage.setTitle("Nerve (GraphStream + JavaFX)");
        nerveStage.setScene(new Scene(nervePanel, 800, 600));
        nerveStage.show();
    }

    /**
     * Launches the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    /**
     * Collects distinct letters from the string, preserving the order of appearance.
     *
     * @param text The input string.
     * @return A list of distinct characters in order.
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
     * Adds each character from 'letters' into the polygon,
     * mapping them to the corresponding Vertex objects.
     *
     * @param polygon The polygon to fill.
     * @param letters The string with characters to add.
     * @param map The mapping from character to Vertex.
     */
    private static void addLettersToPolygon(Polygon polygon, String letters,
                                            Map<Character, Vertex> map) {
        for (int i = 0; i < letters.length(); i++) {
            char c = letters.charAt(i);
            polygon.addVertex(map.get(c));
        }
    }

    // ------------------------------------------------------------------------
    // Graph-building for JavaFX
    // ------------------------------------------------------------------------

    /**
     * Builds a GraphStream Graph (MultiGraph) from the Quiver.
     * <p>Nodes = polygons, edges = arrows (directed) labeled by the induced vertex.</p>
     *
     * @param quiver The Quiver to translate.
     * @return A GraphStream Graph suitable for rendering in JavaFX.
     */
    private Graph buildFxGraphFromQuiver(Quiver quiver) {
        // MultiGraph allows parallel edges and loops
        Graph graph = new MultiGraph("QuiverGraph");

        // Add nodes
        for (Polygon p : quiver.getNodes()) {
            String nodeId = p.getName();
            org.graphstream.graph.Node node = graph.addNode(nodeId);
            node.setAttribute("ui.label", nodeId);
        }

        // Add edges
        int edgeCount = 0;
        for (Arrow arrow : quiver.getArrows()) {
            String sourceId = arrow.getSource().getName();
            String targetId = arrow.getTarget().getName();
            String edgeId = "E" + (edgeCount++);
            // Directed edge
            org.graphstream.graph.Edge e = graph.addEdge(edgeId, sourceId, targetId, true);
            e.setAttribute("ui.label", arrow.getInducedByLabel());
        }

        // Optional stylesheet
        graph.setAttribute("ui.stylesheet", getQuiverStylesheet());
        return graph;
    }

    /**
     * Builds a GraphStream Graph (MultiGraph) from the Nerve (undirected).
     * <p>Nodes = polygons, edges = undirected edges if polygons share a vertex.</p>
     *
     * @param nerve The NerveGraph to translate.
     * @return A GraphStream Graph suitable for rendering in JavaFX.
     */
    private Graph buildFxGraphFromNerve(NerveGraph nerve) {
        Graph graph = new MultiGraph("NerveGraph");

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
            graph.addEdge(edgeId, p1Id, p2Id); // no 'true' => undirected
        }

        // Optional stylesheet
        graph.setAttribute("ui.stylesheet", getNerveStylesheet());
        return graph;
    }

    /**
     * Returns a sample stylesheet for the quiver (directed) graph.
     *
     * @return CSS string for GraphStream.
     */
    private String getQuiverStylesheet() {
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
     * Returns a sample stylesheet for the nerve (undirected) graph.
     *
     * @return CSS string for GraphStream.
     */
    private String getNerveStylesheet() {
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
