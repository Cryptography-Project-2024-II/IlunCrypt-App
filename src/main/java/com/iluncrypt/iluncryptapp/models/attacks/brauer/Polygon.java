package com.iluncrypt.iluncryptapp.models.attacks.brauer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a polygon (multiset of vertices) in the Brauer configuration,
 * i.e., an element of Gamma_1.
 * <p>
 * For example, "L1" could contain letters [P, E, R, M, U, T, A].
 * </p>
 */
public class Polygon {
    private String name;
    private List<Vertex> vertices;

    /**
     * Creates a new Polygon with a given name and an empty list of vertices.
     *
     * @param name The name (identifier) of this polygon (e.g., "L1").
     */
    public Polygon(String name) {
        this.name = name;
        this.vertices = new ArrayList<>();
    }

    /**
     * Returns the name (identifier) of this polygon.
     *
     * @return The polygon name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of vertices (possibly with repetitions).
     *
     * @return A mutable list of Vertex objects.
     */
    public List<Vertex> getVertices() {
        return vertices;
    }

    /**
     * Adds a vertex to this polygon (multiset).
     *
     * @param v The vertex to add.
     */
    public void addVertex(Vertex v) {
        vertices.add(v);
    }

    @Override
    public String toString() {
        return name + vertices.toString();
    }
}
