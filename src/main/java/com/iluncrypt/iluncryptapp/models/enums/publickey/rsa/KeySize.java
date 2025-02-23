// KeySize.java
package com.iluncrypt.iluncryptapp.models.enums.publickey.rsa;

public enum KeySize {
    RSA_2048(2048),
    RSA_3072(3072),
    RSA_4096(4096);

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
        throw new IllegalArgumentException("Invalid RSA key size");
    }
}