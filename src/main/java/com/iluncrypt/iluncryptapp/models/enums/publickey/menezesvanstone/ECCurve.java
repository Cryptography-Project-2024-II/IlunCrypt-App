package com.iluncrypt.iluncryptapp.models.enums.publickey.menezesvanstone;

import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public enum ECCurve {
    SECP256R1("secp256r1"),
    SECP384R1("secp384r1");

    private final String name;

    ECCurve(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}