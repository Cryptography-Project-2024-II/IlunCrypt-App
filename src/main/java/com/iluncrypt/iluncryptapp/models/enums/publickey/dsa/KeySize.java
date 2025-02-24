// KeySize
package com.iluncrypt.iluncryptapp.models.enums.publickey.dsa;

public enum KeySize {
    DSA_512(512),
    DSA_1024(1024),
    DSA_4096(4096);

    private final int size;

    KeySize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public static KeySize fromSize(int size) {
        for (KeySize ks : KeySize.values()) {
            if (ks.getSize() == size) {
                return ks;
            }
        }
        throw new IllegalArgumentException("Invalid DSA key size");
    }
}