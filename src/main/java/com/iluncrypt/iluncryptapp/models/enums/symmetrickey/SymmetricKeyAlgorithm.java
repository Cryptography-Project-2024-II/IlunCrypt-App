package com.iluncrypt.iluncryptapp.models.enums.symmetrickey;

public enum SymmetricKeyAlgorithm {
    /** AES (Advanced Encryption Standard) supports various block modes and key sizes. */
    AES(16, new SymmetricKeyMode[]{
            SymmetricKeyMode.ECB, SymmetricKeyMode.CBC,
            SymmetricKeyMode.CFB, SymmetricKeyMode.OFB,
            SymmetricKeyMode.CTR, SymmetricKeyMode.GCM
    }, new KeySize[]{KeySize.AES_128, KeySize.AES_192, KeySize.AES_256}),

    /** DES (Data Encryption Standard) with a fixed key size of 56 bits. */
    DES(8, new SymmetricKeyMode[]{
            SymmetricKeyMode.ECB, SymmetricKeyMode.CBC,
            SymmetricKeyMode.CFB, SymmetricKeyMode.OFB,
            SymmetricKeyMode.CTR
    }, new KeySize[]{KeySize.DES_56}),

    /** TDES (Triple DES) applies DES three times for increased security. */
    TDES(8, new SymmetricKeyMode[]{
            SymmetricKeyMode.ECB, SymmetricKeyMode.CBC,
            SymmetricKeyMode.CFB, SymmetricKeyMode.OFB,
            SymmetricKeyMode.CTR
    }, new KeySize[]{KeySize.TDES_112, KeySize.TDES_168});

    private final int baseIVSize;
    private final SymmetricKeyMode[] supportedModes;
    private final KeySize[] supportedKeySizes;

    SymmetricKeyAlgorithm(int baseIVSize, SymmetricKeyMode[] supportedModes, KeySize[] supportedKeySizes) {
        this.baseIVSize = baseIVSize;
        this.supportedModes = supportedModes;
        this.supportedKeySizes = supportedKeySizes;
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
     * Returns the supported encryption modes for this algorithm.
     *
     * @return an array of supported SymmetricKeyMode.
     */
    public SymmetricKeyMode[] getSupportedModes() {
        return supportedModes;
    }

    /**
     * Returns the supported key sizes for this algorithm.
     *
     * @return an array of supported KeySize.
     */
    public KeySize[] getSupportedKeySizes() {
        return supportedKeySizes;
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
