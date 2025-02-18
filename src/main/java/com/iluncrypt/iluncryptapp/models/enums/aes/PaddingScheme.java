package com.iluncrypt.iluncryptapp.models.enums.aes;

/**
 * Enumeration of supported padding schemes, compatible with Java Cipher.getInstance().
 */
public enum PaddingScheme {
    PKCS5("PKCS5Padding"),
    NO_PADDING("NoPadding"),
    ISO10126("ISO10126Padding"),
    ZERO_PADDING("ZeroPadding");

    private final String padding;

    PaddingScheme(String padding) {
        this.padding = padding;
    }

    public String getPadding() {
        return padding;
    }

    @Override
    public String toString() {
        return padding; // Se usará en ComboBox
    }

    public static PaddingScheme fromString(String text) {
        for (PaddingScheme scheme : PaddingScheme.values()) {
            if (scheme.getPadding().equalsIgnoreCase(text)) {
                return scheme;
            }
        }
        throw new IllegalArgumentException("Invalid PaddingScheme: " + text);
    }
}
