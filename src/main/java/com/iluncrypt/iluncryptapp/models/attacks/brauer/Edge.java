package com.iluncrypt.iluncryptapp.models.attacks.brauer;

/**
 * Represents an undirected edge between two polygons
 * in the nerve graph.
 */
public class Edge {
    private Polygon p1;
    private Polygon p2;

    /**
     * Constructs an undirected Edge between p1 and p2.
     * The order is normalized to ensure uniqueness in a HashSet.
     *
     * @param p1 First polygon.
     * @param p2 Second polygon.
     */
    public Edge(Polygon p1, Polygon p2) {
        if (p1.getName().compareTo(p2.getName()) <= 0) {
            this.p1 = p1;
            this.p2 = p2;
        } else {
            this.p1 = p2;
            this.p2 = p1;
        }
    }

    /**
     * Returns the first polygon in the normalized order.
     *
     * @return A Polygon object.
     */
    public Polygon getP1() {
        return p1;
    }

    /**
     * Returns the second polygon in the normalized order.
     *
     * @return A Polygon object.
     */
    public Polygon getP2() {
        return p2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge other = (Edge) o;
        return p1.equals(other.p1) && p2.equals(other.p2);
    }

    @Override
    public int hashCode() {
        return p1.hashCode() * 31 + p2.hashCode();
    }

    @Override
    public String toString() {
        return "(" + p1.getName() + " -- " + p2.getName() + ")";
    }
}
