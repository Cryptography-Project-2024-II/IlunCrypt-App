package com.iluncrypt.iluncryptapp.models.enums;

public enum SpanishLetterFrequencyZ26 {
    A(12.53), B(1.49), C(2.92), D(5.86), E(13.68), F(0.69),
    G(1.01), H(0.70), I(6.25), J(0.52), K(0.11), L(4.97),
    M(3.15), N(7.01), O(8.68), P(2.51), Q(0.87), R(6.87),
    S(7.98), T(4.63), U(3.93), V(0.90), W(0.01), X(0.22),
    Y(0.90), Z(0.52);

    private final double probability;

    SpanishLetterFrequencyZ26(double probability) {
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }
}
