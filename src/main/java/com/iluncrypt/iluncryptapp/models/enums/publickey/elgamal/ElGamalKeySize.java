package com.iluncrypt.iluncryptapp.models.enums.publickey.elgamal;

public enum ElGamalKeySize {
    ELGAMAL_256(256),
    ELGAMAL_512(512),
    ELGAMAL_1024(1024);

    private final int size;

    ElGamalKeySize(int size) { this.size = size; }
    public int getSize() { return size; }
}