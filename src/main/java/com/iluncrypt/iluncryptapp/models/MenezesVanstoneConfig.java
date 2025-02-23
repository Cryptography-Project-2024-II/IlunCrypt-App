package com.iluncrypt.iluncryptapp.models;

import com.iluncrypt.iluncryptapp.models.enums.publickey.menezesvanstone.*;

public class MenezesVanstoneConfig extends CryptosystemConfig {
    private KeySize keySize = KeySize.MV_256;

    public ECCurve getCurve() {
        return ECCurve.valueOf(
                keySize.getCurveName().toUpperCase().replace("-", "_")
        );
    }

    public KeySize getKeySize() { return keySize; }
    public void setKeySize(KeySize keySize) { this.keySize = keySize; }
}