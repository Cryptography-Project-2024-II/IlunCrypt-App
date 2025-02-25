package com.iluncrypt.iluncryptapp.models.enums;

public enum FrenchLetterFrequencyZ26 {
    A(7.64), B(0.90), C(3.26), D(3.67), E(14.72), F(1.07),
    G(1.00), H(0.74), I(7.53), J(0.61), K(0.05), L(5.02),
    M(2.97), N(7.10), O(5.38), P(2.52), Q(1.36), R(6.55),
    S(7.95), T(6.07), U(4.64), V(1.84), W(0.02), X(0.43),
    Y(0.13), Z(0.33);

    private final double probability;

    FrenchLetterFrequencyZ26(double probability) {
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }
}
