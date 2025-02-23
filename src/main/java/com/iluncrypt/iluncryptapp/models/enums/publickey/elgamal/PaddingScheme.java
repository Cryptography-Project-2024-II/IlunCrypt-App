package com.iluncrypt.iluncryptapp.models.enums.publickey.elgamal;

public enum PaddingScheme {
    NO_PADDING("NoPadding"),
    PKCS7("PKCS7"),
    OAEP("OAEP");

    private final String paddingName;

    PaddingScheme(String paddingName) {
        this.paddingName = paddingName;
    }

    public String getPaddingName() {
        return paddingName;
    }
}