package com.iluncrypt.iluncryptapp.models;

import com.iluncrypt.iluncryptapp.models.enums.publickey.elgamal.*;

public class ElGamalConfig extends CryptosystemConfig {
    private ElGamalKeySize keySize = ElGamalKeySize.ELGAMAL_512;
    private boolean storeAlgorithm = true;
    private PaddingScheme paddingScheme = PaddingScheme.NO_PADDING;

    public PaddingScheme getPaddingScheme() { return paddingScheme; }
    public void setPaddingScheme(PaddingScheme scheme) { this.paddingScheme = scheme; }
    public ElGamalKeySize getKeySize() { return keySize; }
    public void setKeySize(ElGamalKeySize keySize) { this.keySize = keySize; }
    public boolean isStoreAlgorithm() { return storeAlgorithm; }
    public void setStoreAlgorithm(boolean store) { this.storeAlgorithm = store; }
}