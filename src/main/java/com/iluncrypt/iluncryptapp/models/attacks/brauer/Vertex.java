package com.iluncrypt.iluncryptapp.models.attacks.brauer;

/**
 * Represents a vertex in the Brauer configuration (e.g., a letter).
 * Each vertex has:
 * <ul>
 *   <li>A label (String),</li>
 *   <li>A multiplicity mu(alpha),</li>
 *   <li>A valency val(alpha),</li>
 *   <li>A flag indicating if it is truncated or not.</li>
 * </ul>
 */
public class Vertex {
    private String label;
    private int multiplicity;
    private int valency;
    private boolean truncated;

    /**
     * Creates a new Vertex with a given label and initial multiplicity.
     *
     * @param label The label for this vertex (e.g., "A").
     * @param multiplicity The initial multiplicity mu(alpha).
     */
    public Vertex(String label, int multiplicity) {
        this.label = label;
        this.multiplicity = multiplicity;
        this.valency = 0;
        this.truncated = false;
    }

    /**
     * Returns the label of this vertex.
     *
     * @return A string label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the multiplicity mu(alpha).
     *
     * @return The current multiplicity.
     */
    public int getMultiplicity() {
        return multiplicity;
    }

    /**
     * Sets the multiplicity mu(alpha).
     *
     * @param multiplicity The new multiplicity.
     */
    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    /**
     * Returns the valency val(alpha).
     *
     * @return The current valency.
     */
    public int getValency() {
        return valency;
    }

    /**
     * Sets the valency val(alpha).
     *
     * @param valency The new valency.
     */
    public void setValency(int valency) {
        this.valency = valency;
    }

    /**
     * Indicates whether this vertex is truncated (mu(alpha)*val(alpha) == 1).
     *
     * @return True if truncated, false otherwise.
     */
    public boolean isTruncated() {
        return truncated;
    }

    /**
     * Sets the truncated flag.
     *
     * @param truncated True if this vertex is truncated.
     */
    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    @Override
    public String toString() {
        return "Vertex(" + label + ")";
    }
}
