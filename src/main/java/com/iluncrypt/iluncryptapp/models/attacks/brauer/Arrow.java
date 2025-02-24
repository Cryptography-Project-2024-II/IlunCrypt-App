package com.iluncrypt.iluncryptapp.models.attacks.brauer;

/**
 * Represents a directed arrow (edge) in the quiver.
 * Each arrow has:
 * <ul>
 *   <li>A source polygon,</li>
 *   <li>A target polygon,</li>
 *   <li>An inducedBy label (the vertex that caused this arrow).</li>
 * </ul>
 */
public class Arrow {
    private Polygon source;
    private Polygon target;
    private String inducedByLabel;

    /**
     * Constructs an Arrow from a source polygon to a target polygon,
     * labeled by the vertex that induces it.
     *
     * @param source The source polygon.
     * @param target The target polygon.
     * @param inducedByLabel The label of the vertex that induces this arrow.
     */
    public Arrow(Polygon source, Polygon target, String inducedByLabel) {
        this.source = source;
        this.target = target;
        this.inducedByLabel = inducedByLabel;
    }

    /**
     * Returns the source polygon.
     *
     * @return The source Polygon.
     */
    public Polygon getSource() {
        return source;
    }

    /**
     * Returns the target polygon.
     *
     * @return The target Polygon.
     */
    public Polygon getTarget() {
        return target;
    }

    /**
     * Returns the label of the vertex that induces this arrow.
     *
     * @return The vertex label (e.g., "A").
     */
    public String getInducedByLabel() {
        return inducedByLabel;
    }

    @Override
    public String toString() {
        return source.getName() + " --(" + inducedByLabel + ")--> " + target.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arrow)) return false;
        Arrow other = (Arrow) o;
        return this.source.equals(other.source)
                && this.target.equals(other.target)
                && this.inducedByLabel.equals(other.inducedByLabel);
    }

    @Override
    public int hashCode() {
        return (source.hashCode() * 31 + target.hashCode()) * 31
                + inducedByLabel.hashCode();
    }
}