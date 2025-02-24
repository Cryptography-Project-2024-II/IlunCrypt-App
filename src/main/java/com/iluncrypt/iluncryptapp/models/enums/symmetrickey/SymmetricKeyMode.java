package com.iluncrypt.iluncryptapp.models.enums.symmetrickey;

/**
 * Enumeration of symmetric encryption modes.
 * Defines whether an IV is required and which padding schemes are allowed.
 */
public enum SymmetricKeyMode {
    /** ECB (Electronic Codebook) does not use an IV and requires padding. */
    ECB("ECB", false, 0, new PaddingScheme[]{PaddingScheme.PKCS5, PaddingScheme.ISO10126}),

    /** CBC (Cipher Block Chaining) requires an IV and supports padding. */
    CBC("CBC", true, -1, new PaddingScheme[]{PaddingScheme.PKCS5, PaddingScheme.ISO10126}),

    /** CFB (Cipher Feedback) requires an IV and does not use padding. */
    CFB("CFB", true, -1, new PaddingScheme[]{PaddingScheme.NO_PADDING}),

    /** OFB (Output Feedback) requires an IV and does not use padding. */
    OFB("OFB", true, -1, new PaddingScheme[]{PaddingScheme.NO_PADDING}),

    /** CTR (Counter Mode) requires an IV and does not use padding. */
    CTR("CTR", true, -1, new PaddingScheme[]{PaddingScheme.NO_PADDING}),

    /** GCM (Galois/Counter Mode) requires an IV of 12 bytes and does not use padding. */
    GCM("GCM", true, 12, new PaddingScheme[]{PaddingScheme.NO_PADDING});

    private final String mode;
    private final boolean requiresIV;
    private final int fixedIVSize;
    private final PaddingScheme[] supportedPadding;

    SymmetricKeyMode(String mode, boolean requiresIV, int fixedIVSize, PaddingScheme[] supportedPadding) {
        this.mode = mode;
        this.requiresIV = requiresIV;
        this.fixedIVSize = fixedIVSize;
        this.supportedPadding = supportedPadding;
    }

    public String getMode() {
        return mode;
    }

    public boolean requiresIV() {
        return requiresIV;
    }

    public int getFixedIVSize() {
        return fixedIVSize;
    }

    /**
     * Returns a list of supported padding schemes for this encryption mode.
     *
     * @return An array of `PaddingScheme` that are valid for this mode.
     */
    public PaddingScheme[] getSupportedPadding() {
        return supportedPadding;
    }

    /**
     * Returns the first valid padding scheme for this mode.
     * If no padding is allowed, returns NO_PADDING.
     *
     * @return The default padding scheme for this mode.
     */
    public PaddingScheme getPaddingScheme() {
        return supportedPadding.length > 0 ? supportedPadding[0] : PaddingScheme.NO_PADDING;
    }

    /**
     * Retrieves a `SymmetricKeyMode` from a string representation.
     *
     * @param modeName The string representation of the mode.
     * @return The corresponding `SymmetricKeyMode` enum value.
     * @throws IllegalArgumentException If no matching mode is found.
     */
    public static SymmetricKeyMode fromString(String modeName) {
        for (SymmetricKeyMode mode : values()) {
            if (mode.getMode().equalsIgnoreCase(modeName)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Invalid SymmetricKeyMode: " + modeName);
    }

    public boolean supportsPadding(PaddingScheme padding) {
        for (PaddingScheme p : supportedPadding) {
            if (p == padding) {
                return true;
            }
        }
        return false;
    }
}
