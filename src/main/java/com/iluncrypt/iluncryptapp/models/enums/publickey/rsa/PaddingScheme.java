// PaddingScheme.java 
package com.iluncrypt.iluncryptapp.models.enums.publickey.rsa;

public enum PaddingScheme {
    PKCS1("PKCS1Padding"),
    NO_PADDING("NoPadding"),
    OAEP("OAEPWithSHA-256AndMGF1Padding");

    private final String padding;

    PaddingScheme(String padding) {
        this.padding = padding;
    }

    public String getPadding() {
        return padding;
    }

    public static PaddingScheme fromString(String text) {
        for (PaddingScheme ps : PaddingScheme.values()) {
            if (ps.padding.equalsIgnoreCase(text)) {
                return ps;
            }
        }
        throw new IllegalArgumentException("Invalid padding scheme");
    }
}