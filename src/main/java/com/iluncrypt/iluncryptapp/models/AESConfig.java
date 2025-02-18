package com.iluncrypt.iluncryptapp.models;

import com.iluncrypt.iluncryptapp.models.enums.aes.*;

import java.util.Objects;


public class AESConfig extends CryptosystemConfig {
    private KeySize keySize; // Tamaño de clave (128, 192, 256 bits)
    private AESMode mode; // Modo de cifrado (ECB, CBC, CFB, OFB, GCM)
    private PaddingScheme paddingScheme; // Esquema de padding
    private IVSize ivSize; // Tamaño del IV (0, 12, 16 bytes)
    private GCMTagSize gcmTagSize; // Tamaño del tag de autenticación en GCM
    private AuthenticationMethod authMethod; // Método de integridad (HMAC, CRC32, etc.)
    private boolean storeAlgorithm;

    public AESConfig(KeySize keySize, AESMode mode, PaddingScheme paddingScheme, IVSize ivSize, GCMTagSize gcmTagSize, AuthenticationMethod authMethod, boolean storeAlgorithm) {
        this.keySize = keySize;
        this.mode = mode;
        this.paddingScheme = paddingScheme;
        this.ivSize = ivSize;
        this.gcmTagSize = gcmTagSize;
        this.authMethod = authMethod;
        this.storeAlgorithm = storeAlgorithm;
    }

    public KeySize getKeySize() {
        return keySize;
    }

    public AESMode getMode() {
        return mode;
    }

    public PaddingScheme getPaddingScheme() {
        return paddingScheme;
    }

    public IVSize getIvSize() {
        return ivSize;
    }

    public GCMTagSize getGcmTagSize() {
        return gcmTagSize;
    }

    public AuthenticationMethod getAuthMethod() {
        return authMethod;
    }

    public boolean isStoreAlgorithm() { // 🔹 Se agrega este método
        return storeAlgorithm;
    }

    public void setStoreAlgorithm(boolean storeAlgorithm) { // 🔹 Método setter
        this.storeAlgorithm = storeAlgorithm;
    }

    public void setKeySize(KeySize keySize) {
        this.keySize = keySize;
    }

    public void setMode(AESMode mode) {
        this.mode = mode;
    }

    public void setPaddingScheme(PaddingScheme paddingScheme) {
        this.paddingScheme = paddingScheme;
    }

    public void setIvSize(IVSize ivSize) {
        this.ivSize = ivSize;
    }

    public void setGcmTagSize(GCMTagSize gcmTagSize) {
        this.gcmTagSize = gcmTagSize;
    }

    public void setAuthMethod(AuthenticationMethod authMethod) {
        this.authMethod = authMethod;
    }

    public String getTransformation() {
        //return "AES"+keySize.getSize()+"/" + mode.getMode() + "/" + paddingScheme.getPadding();
        return "AES/" + mode.getMode() + "/" + paddingScheme.getPadding();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AESConfig that = (AESConfig) o;

        return keySize == that.keySize &&
                mode == that.mode &&
                paddingScheme == that.paddingScheme &&
                ivSize == that.ivSize &&
                gcmTagSize == that.gcmTagSize &&
                authMethod == that.authMethod &&
                storeAlgorithm == that.storeAlgorithm;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keySize, mode, paddingScheme, ivSize, gcmTagSize, authMethod, storeAlgorithm);
    }

    @Override
    public String toString() {
        return "AESConfig{" +
                "keySize=" + keySize +
                ", mode=" + mode +
                ", paddingScheme=" + paddingScheme +
                ", ivSize=" + ivSize +
                ", gcmTagSize=" + gcmTagSize +
                ", authMethod=" + authMethod +
                '}';
    }
}
