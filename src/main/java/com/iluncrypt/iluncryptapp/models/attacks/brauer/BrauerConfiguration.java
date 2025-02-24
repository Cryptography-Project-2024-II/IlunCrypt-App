package com.iluncrypt.iluncryptapp.models.attacks.brauer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Brauer Configuration consisting of:
 * <ul>
 *   <li>A set of vertices (Gamma_0),</li>
 *   <li>A set of polygons (Gamma_1),</li>
 *   <li>An orientation map that associates each vertex with the list of polygons
 *       in which it appears, in a specific (cyclic) order.</li>
 * </ul>
 *
 * <p>This class provides methods to:</p>
 * <ul>
 *   <li>Compute the valency of each vertex (how many times it appears in total),</li>
 *   <li>Adjust multiplicities so that any vertex with valency = 1 has mu=2,</li>
 *   <li>Mark vertices as truncated when mu(alpha)*val(alpha) == 1,</li>
 *   <li>Build successor sequences (the orientation map),</li>
 *   <li>Compute the dimension of the associated Brauer configuration algebra,</li>
 *   <li>Compute the dimension of its center,</li>
 *   <li>Count loops in the induced quiver (for the center's dimension formula).</li>
 * </ul>
 */
public class BrauerConfiguration {

    /**
     * The list of vertices (Gamma_0).
     */
    private List<Vertex> vertices;

    /**
     * The list of polygons (Gamma_1).
     */
    private List<Polygon> polygons;

    /**
     * The orientation map: for each vertex, the ordered list of polygons where it appears.
     * This reflects the cyclic ordering around that vertex.
     */
    private Map<Vertex, List<Polygon>> orientationMap;

    /**
     * Creates an empty BrauerConfiguration.
     */
    public BrauerConfiguration() {
        this.vertices = new ArrayList<>();
        this.polygons = new ArrayList<>();
        this.orientationMap = new HashMap<>();
    }

    /**
     * Adds a vertex to this configuration and initializes its orientation list.
     *
     * @param v The vertex to add.
     */
    public void addVertex(Vertex v) {
        vertices.add(v);
        orientationMap.put(v, new ArrayList<>());
    }

    /**
     * Adds a polygon (multiset of vertices) to this configuration.
     *
     * @param p The polygon to add.
     */
    public void addPolygon(Polygon p) {
        polygons.add(p);
    }

    /**
     * Returns the list of vertices (Gamma_0).
     *
     * @return A mutable list of Vertex objects.
     */
    public List<Vertex> getVertices() {
        return vertices;
    }

    /**
     * Returns the list of polygons (Gamma_1).
     *
     * @return A mutable list of Polygon objects.
     */
    public List<Polygon> getPolygons() {
        return polygons;
    }

    /**
     * Returns the orientation map that associates each vertex with
     * its ordered list of polygons.
     *
     * @return A Map from Vertex to List of Polygon.
     */
    public Map<Vertex, List<Polygon>> getOrientationMap() {
        return orientationMap;
    }

    // ------------------------------------------------------------------------
    // MAIN OPERATIONS
    // ------------------------------------------------------------------------

    /**
     * Computes the valency of each vertex (val(alpha)), i.e., how many times
     * the vertex appears in total across all polygons.
     * <p>All valencies are reset before counting.</p>
     */
    public void computeValencies() {
        // Reset valencies
        for (Vertex v : vertices) {
            v.setValency(0);
        }
        // Count occurrences
        for (Polygon p : polygons) {
            for (Vertex v : p.getVertices()) {
                v.setValency(v.getValency() + 1);
            }
        }
    }

    /**
     * If a vertex has valency = 1, sets its multiplicity to 2.
     * <p>This is a custom rule sometimes requested in certain Brauer configurations.</p>
     */
    public void adjustMultiplicities() {
        for (Vertex v : vertices) {
            if (v.getValency() == 1) {
                v.setMultiplicity(2);
            }
        }
    }

    /**
     * Marks a vertex as truncated if mu(alpha)*val(alpha) == 1.
     * <p>Typically called after computing valencies and adjusting multiplicities.</p>
     */
    public void computeTruncatedVertices() {
        for (Vertex v : vertices) {
            boolean isTrunc = (v.getMultiplicity() * v.getValency()) == 1;
            v.setTruncated(isTrunc);
        }
    }

    /**
     * Builds the successor sequences for each vertex, stored in orientationMap.
     * <p>
     * This clears any existing lists in orientationMap, repopulates them with the
     * polygons in which each vertex appears, then sorts them (e.g., by polygon name).
     * The actual "cyclic" link is often shown during quiver construction or printing.
     * </p>
     */
    public void buildSuccessorSequences() {
        // Clear old data
        for (Vertex v : vertices) {
            orientationMap.get(v).clear();
        }
        // Populate
        for (Polygon p : polygons) {
            for (Vertex v : p.getVertices()) {
                orientationMap.get(v).add(p);
            }
        }
        // Sort polygons for each vertex
        for (Vertex v : vertices) {
            orientationMap.get(v).sort(Comparator.comparing(Polygon::getName));
        }
    }

    // ------------------------------------------------------------------------
    // DIMENSION FORMULAS
    // ------------------------------------------------------------------------

    /**
     * Computes the dimension of the Brauer configuration algebra using the formula:
     * <pre>
     *   dim(Lambda) = 2 * |Gamma_1|
     *                 + sum over alpha of [ val(alpha) * (val(alpha)*mu(alpha) - 1 ) ]
     * </pre>
     *
     * @return The integer dimension of the associated algebra.
     */
    public int computeAlgebraDimension() {
        int numPolygons = polygons.size(); // |Gamma_1|
        int base = 2 * numPolygons;
        int sumValTerm = 0;

        for (Vertex v : vertices) {
            int val = v.getValency();
            int mu = v.getMultiplicity();
            sumValTerm += val * (val * mu - 1);
        }
        return base + sumValTerm;
    }

    /**
     * Computes the dimension of the center of the algebra using the formula:
     * <pre>
     *   dim(Z(Lambda)) = 1 + |Gamma_1| - |Gamma_0|
     *                    + sum over alpha of mu(alpha)
     *                    + #(loops in Q) - #(vertices with val=1)
     * </pre>
     * where #(loops in Q) is the number of loops in the induced quiver,
     * counted by {@link #countLoops()}.
     *
     * @return The integer dimension of the center.
     */
    public int computeCenterDimension() {
        int loops = countLoops();
        int val1Count = 0;
        int sumMu = 0;

        for (Vertex v : vertices) {
            sumMu += v.getMultiplicity();
            if (v.getValency() == 1) {
                val1Count++;
            }
        }

        int nPolygons = polygons.size(); // |Gamma_1|
        int nVertices = vertices.size(); // |Gamma_0|

        return 1 + nPolygons - nVertices + sumMu + loops - val1Count;
    }

    /**
     * Counts loops in the induced quiver:
     * <ul>
     *   <li>NODES: polygons (Gamma_1).</li>
     *   <li>For each non-truncated vertex v, connect orientationMap[v] in a cycle.</li>
     *   <li>If source == target in that cycle, it is a loop.</li>
     * </ul>
     * <p>Primarily used by {@link #computeCenterDimension()}.</p>
     *
     * @return The number of loop arrows in the quiver.
     */
    private int countLoops() {
        int loopCount = 0;
        for (Vertex v : vertices) {
            if (!v.isTruncated()) {
                List<Polygon> polyList = orientationMap.get(v);
                int n = polyList.size();
                for (int i = 0; i < n; i++) {
                    Polygon source = polyList.get(i);
                    Polygon target = polyList.get((i + 1) % n); // wrap around
                    if (source == target) {
                        loopCount++;
                    }
                }
            }
        }
        return loopCount;
    }
}
