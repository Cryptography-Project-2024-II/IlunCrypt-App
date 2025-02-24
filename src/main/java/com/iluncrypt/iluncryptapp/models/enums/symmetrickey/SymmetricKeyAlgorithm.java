package com.iluncrypt.iluncryptapp.models.enums.symmetrickey;

/**
 * Enumeration of supported symmetric encryption algorithms.
 * Defines the base IV size and supported encryption modes.
 */
public enum SymmetricKeyAlgorithm {
    /** AES (Advanced Encryption Standard) supports various block modes and key sizes. */
    AES(16, new SymmetricKeyMode[]{
            SymmetricKeyMode.ECB, SymmetricKeyMode.CBC,
            SymmetricKeyMode.CFB, SymmetricKeyMode.OFB,
            SymmetricKeyMode.CTR, SymmetricKeyMode.GCM}),

    /** DES (Data Encryption Standard) with a fixed key size of 56 bits. */
    DES(8, new SymmetricKeyMode[]{
            SymmetricKeyMode.ECB, SymmetricKeyMode.CBC,
            SymmetricKeyMode.CFB, SymmetricKeyMode.OFB,
            SymmetricKeyMode.CTR}),

    /** TDES (Triple DES) applies DES three times for increased security. */
    TDES(8, new SymmetricKeyMode[]{
            SymmetricKeyMode.ECB, SymmetricKeyMode.CBC,
            SymmetricKeyMode.CFB, SymmetricKeyMode.OFB,
            SymmetricKeyMode.CTR});

    private final int baseIVSize;
    private final SymmetricKeyMode[] supportedModes;

    SymmetricKeyAlgorithm(int baseIVSize, SymmetricKeyMode[] supportedModes) {
        this.baseIVSize = baseIVSize;
        this.supportedModes = supportedModes;
    }

    /**
     * Gets the base IV size required for this algorithm.
     *
     * @return the IV size in bytes.
     */
    public int getBaseIVSize() {
        return baseIVSize;
    }

    /**
     * Checks if the given encryption mode is supported by this algorithm.
     *
     * @param mode the encryption mode.
     * @return true if the mode is supported, false otherwise.
     */
    public boolean supportsMode(SymmetricKeyMode mode) {
        for (SymmetricKeyMode m : supportedModes) {
            if (m == mode) {
                return true;
            }
        }
        return false;
    }
}
