package com.iluncrypt.iluncryptapp.models;


import com.iluncrypt.iluncryptapp.models.enums.symmetrickey.*;

/**
 * Configuration class for symmetric encryption settings.
 * Stores algorithm selection, mode, key size, and various options for IV and key management.
 */
public class SymmetricKeyConfig extends CryptosystemConfig{
    private SymmetricKeyAlgorithm algorithm;
    private SymmetricKeyMode mode;
    private KeySize keySize;
    private AuthenticationMethod authenticationMethod;
    private com.iluncrypt.iluncryptapp.models.enums.symmetrickey.GCMTagSize GCMTagSize;
    private PaddingScheme paddingScheme;

    private boolean showIV;
    private boolean generateIV;
    private boolean generateKey;
    private boolean saveAlgorithm;

    /**
     * Constructs a symmetric encryption configuration.
     *
     * @param algorithm            The encryption algorithm (AES, DES, TDES).
     * @param mode                 The encryption mode (CBC, ECB, CFB, OFB, CTR, GCM).
     * @param keySize              The key size (128, 192, 256 bits, etc.).
     * @param authenticationMethod The authentication method (NONE, HMAC_SHA256, etc.).
     * @param GCMTagSize           The tag size for GCM mode (ignored for non-GCM modes).
     * @param showIV               Whether the IV should be displayed in the UI.
     * @param generateIV           Whether the IV should be automatically generated.
     * @param generateKey          Whether the encryption key should be automatically generated.
     * @param saveAlgorithm        Whether the algorithm should be saved in exported files.
     */
    public SymmetricKeyConfig(SymmetricKeyAlgorithm algorithm,
                              SymmetricKeyMode mode,
                              KeySize keySize,
                              AuthenticationMethod authenticationMethod,
                              GCMTagSize GCMTagSize,
                              PaddingScheme paddingScheme,
                              boolean showIV,
                              boolean generateIV,
                              boolean generateKey,
                              boolean saveAlgorithm) {
        this.algorithm = algorithm;
        this.mode = mode;
        this.keySize = keySize;
        this.authenticationMethod = authenticationMethod;
        this.GCMTagSize = GCMTagSize;
        this.paddingScheme = validatePaddingScheme(mode, paddingScheme);
        this.showIV = showIV;
        this.generateIV = generateIV;
        this.generateKey = generateKey;
        this.saveAlgorithm = saveAlgorithm;

        validateConfiguration();
    }


    /**
     * Validates the encryption configuration.
     * Ensures that the mode and padding are compatible and that GCM-specific options are correctly set.
     */
    private void validateConfiguration() {
        if (!algorithm.supportsMode(mode)) {
            throw new IllegalArgumentException("Mode " + mode + " is not supported by " + algorithm);
        }

        if (mode == SymmetricKeyMode.GCM && GCMTagSize == null) {
            throw new IllegalArgumentException("GCM mode requires a valid GCM tag size.");
        }

        if (mode != SymmetricKeyMode.GCM && GCMTagSize != null) {
            throw new IllegalArgumentException("GCM tag size is only applicable for GCM mode.");
        }
    }

    /** @return The selected encryption algorithm. */
    public SymmetricKeyAlgorithm getAlgorithm() {
        return algorithm;
    }

    /** @return The selected encryption mode. */
    public SymmetricKeyMode getMode() {
        return mode;
    }

    /**
     * Updates the encryption mode.
     */
    public void setMode(SymmetricKeyMode mode) {
        if (!algorithm.supportsMode(mode)) {
            throw new IllegalArgumentException("Mode " + mode + " is not supported by " + algorithm);
        }
        this.mode = mode;
    }

    /** @return The selected key size. */
    public KeySize getKeySize() {
        return keySize;
    }

    /**
     * Updates the key size.
     */
    public void setKeySize(KeySize keySize) {
        this.keySize = keySize;
    }

    /**
     * Updates the padding scheme.
     */
    public void setPaddingScheme(PaddingScheme paddingScheme) {
        this.paddingScheme = validatePaddingScheme(this.mode, paddingScheme);
    }

    /**
     * Updates the padding scheme.
     */
    public void setPaddingScheme(PaddingScheme paddingScheme, boolean force) {
        this.paddingScheme = paddingScheme;
    }

    /** @return The selected padding scheme. */
    public PaddingScheme getPaddingScheme() {
        return paddingScheme;
    }

    /**
     * Updates the GCM tag size.
     */
    public void setGCMTagSize(GCMTagSize gcmTagSize) {
        if (mode != SymmetricKeyMode.GCM && gcmTagSize != null) {
            throw new IllegalArgumentException("GCM tag size is only applicable for GCM mode.");
        }
        this.GCMTagSize = gcmTagSize;
    }

    public void setSaveAlgorithm(boolean saveAlgorithm) {
        this.saveAlgorithm = saveAlgorithm;
    }

    public void setGenerateKey(boolean generateKey) {
        this.generateKey = generateKey;
    }

    public void setGenerateIV(boolean generateIV) {
        this.generateIV = generateIV;
    }

    public void setShowIV(boolean showIV) {
        this.showIV = showIV;
    }

    public void setAlgorithm(SymmetricKeyAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    /** @return The selected GCM tag size (only for GCM mode). */
    public GCMTagSize getGCMTagSize() {
        return GCMTagSize;
    }

    /**
     * Updates the authentication method.
     */
    public void setAuthenticationMethod(AuthenticationMethod authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    /** @return The selected authentication method. */
    public AuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }

    /** @return True if the IV should be displayed in the UI. */
    public boolean isShowIV() {
        return showIV;
    }

    /** @return True if the IV should be automatically generated. */
    public boolean isGenerateIV() {
        return generateIV;
    }

    /** @return True if the encryption key should be automatically generated. */
    public boolean isGenerateKey() {
        return generateKey;
    }

    /** @return True if the algorithm should be saved in exported files. */
    public boolean isSaveAlgorithm() {
        return saveAlgorithm;
    }

    private PaddingScheme validatePaddingScheme(SymmetricKeyMode mode, PaddingScheme padding) {
        if (!mode.supportsPadding(padding)) {
            throw new IllegalArgumentException("Padding " + padding + " is not supported for mode " + mode);
        }
        return padding;
    }




    /**
     * Returns the transformation string for Java's Cipher.getInstance().
     *
     * @return A string in the format "Algorithm/Mode/Padding".
     */
    public String getTransformation() {
        return algorithm.name() + "/" + mode.getMode() + "/" + paddingScheme.getPadding();
    }

    @Override
    public String toString() {
        return "SymmetricEncryptionConfig{" +
                "algorithm=" + algorithm +
                ", mode=" + mode +
                ", keySize=" + keySize +
                ", authenticationMethod=" + authenticationMethod +
                ", GCMTagSize=" + (GCMTagSize != null ? GCMTagSize.getSize() + " bits" : "N/A") +
                ", showIV=" + showIV +
                ", generateIV=" + generateIV +
                ", generateKey=" + generateKey +
                ", saveAlgorithm=" + saveAlgorithm +
                '}';
    }
}
