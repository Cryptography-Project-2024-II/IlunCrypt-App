package com.iluncrypt.iluncryptapp.models;

import com.iluncrypt.iluncryptapp.models.enums.publickey.rabin.RabinKeySize;
import com.iluncrypt.iluncryptapp.models.enums.publickey.rabin.PaddingScheme;

public class RabinConfig extends CryptosystemConfig {
    private RabinKeySize keySize = RabinKeySize.RABIN_2048;
    private PaddingScheme paddingScheme = PaddingScheme.NO_PADDING;

    // Getters & Setters
    public RabinKeySize getKeySize() { return keySize; }
    public void setKeySize(RabinKeySize keySize) { this.keySize = keySize; }
    public PaddingScheme getPaddingScheme() { return paddingScheme; }
    public void setPaddingScheme(PaddingScheme scheme) { this.paddingScheme = scheme; }
}