package com.iluncrypt.iluncryptapp.models;

import com.iluncrypt.iluncryptapp.models.enums.publickey.rsa.*;
import java.util.Objects;

public class RSAConfig extends CryptosystemConfig {
    private KeySize keySize;
    private PaddingScheme paddingScheme;
    private boolean storeAlgorithm;

    public RSAConfig() {
        // Default values
        this.keySize = KeySize.RSA_2048;
        this.paddingScheme = PaddingScheme.NO_PADDING;
        this.storeAlgorithm = true;
    }

    public RSAConfig(KeySize keySize, PaddingScheme paddingScheme, boolean storeAlgorithm) {
        this.keySize = keySize;
        this.paddingScheme = paddingScheme;
        this.storeAlgorithm = storeAlgorithm;
    }

    // Getters & Setters
    public KeySize getKeySize() {
        return keySize;
    }

    public void setKeySize(KeySize keySize) {
        this.keySize = keySize;
    }

    public PaddingScheme getPaddingScheme() {
        return paddingScheme;
    }

    public void setPaddingScheme(PaddingScheme paddingScheme) {
        this.paddingScheme = paddingScheme;
    }

    public boolean isStoreAlgorithm() {
        return storeAlgorithm;
    }

    public void setStoreAlgorithm(boolean storeAlgorithm) {
        this.storeAlgorithm = storeAlgorithm;
    }

    public String getTransformation() {
        return "RSA/ECB/" + paddingScheme.getPadding();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSAConfig that = (RSAConfig) o;
        return storeAlgorithm == that.storeAlgorithm &&
                keySize == that.keySize &&
                paddingScheme == that.paddingScheme;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keySize, paddingScheme, storeAlgorithm);
    }

    @Override
    public String toString() {
        return "RSAConfig{" +
                "keySize=" + keySize +
                ", paddingScheme=" + paddingScheme +
                ", storeAlgorithm=" + storeAlgorithm +
                '}';
    }
}