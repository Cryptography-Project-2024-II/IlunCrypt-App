package com.iluncrypt.iluncryptapp.models.enums;

public enum PortugueseLetterFrequencyZ26 {
    A(14.63), B(1.04), C(3.88), D(4.99), E(12.57), F(1.02),
    G(1.00), H(0.73), I(6.18), J(0.47), K(0.02), L(3.76),
    M(4.74), N(3.61), O(10.73), P(2.52), Q(1.20), R(6.53),
    S(7.81), T(4.34), U(4.63), V(1.67), W(0.01), X(0.40),
    Y(0.01), Z(0.27);

    private final double probability;

    PortugueseLetterFrequencyZ26(double probability) {
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }
}
