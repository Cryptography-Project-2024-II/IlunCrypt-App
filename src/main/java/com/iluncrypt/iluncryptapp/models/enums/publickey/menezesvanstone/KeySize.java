package com.iluncrypt.iluncryptapp.models.enums.publickey.menezesvanstone;

public enum KeySize {
    MV_256("secp256r1", 256),
    MV_384("secp384r1", 384);

    private final String curveName;
    private final int bitLength;

    KeySize(String curveName, int bitLength) {
        this.curveName = curveName;
        this.bitLength = bitLength;
    }

    public String getCurveName() { return curveName; }
    public int getBitLength() { return bitLength; }
}