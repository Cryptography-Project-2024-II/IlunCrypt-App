package com.iluncrypt.iluncryptapp.models.enums;

public enum EnglishLetterFrequencyZ26 {
    A(8.17), B(1.49), C(2.78), D(4.25), E(12.70), F(2.23),
    G(2.02), H(6.09), I(6.97), J(0.15), K(0.77), L(4.03),
    M(2.41), N(6.75), O(7.51), P(1.93), Q(0.10), R(5.99),
    S(6.33), T(9.06), U(2.76), V(0.98), W(2.36), X(0.15),
    Y(1.97), Z(0.07);

    private final double probability;

    EnglishLetterFrequencyZ26(double probability) {
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }
}
