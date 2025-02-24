package com.iluncrypt.iluncryptapp.models.enums.symmetrickey;

/**
 * Enumeration of supported padding schemes for symmetric encryption.
 * Padding is required for block cipher modes that operate on fixed-size blocks.
 */
public enum PaddingScheme {
    /** PKCS5 padding (commonly used for AES and DES in block modes). */
    PKCS5("PKCS5Padding"),

    /** No padding (used in stream cipher modes like CTR, CFB, OFB, and GCM). */
    NO_PADDING("NoPadding"),

    /** ISO 10126 padding (random padding scheme for block ciphers). */
    ISO10126("ISO10126Padding");

    private final String padding;

    PaddingScheme(String padding) {
        this.padding = padding;
    }

    /**
     * Gets the padding scheme name as used in Java's Cipher.getInstance().
     *
     * @return the padding scheme name.
     */
    public String getPadding() {
        return padding;
    }

    @Override
    public String toString() {
        return padding;
    }

    /**
     * Retrieves a PaddingScheme enum from a string.
     *
     * @param text the padding scheme name.
     * @return the corresponding PaddingScheme enum.
     * @throws IllegalArgumentException if the padding scheme is not recognized.
     */
    public static PaddingScheme fromString(String text) {
        for (PaddingScheme scheme : PaddingScheme.values()) {
            if (scheme.getPadding().equalsIgnoreCase(text)) {
                return scheme;
            }
        }
        throw new IllegalArgumentException("Invalid PaddingScheme: " + text);
    }
}
