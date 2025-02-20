package com.iluncrypt.iluncryptapp.models.enums.symmetrickey.aes;

/**
 * Enumeration of valid IV sizes for AES encryption.
 */
public enum IVSize {
    IV_0(0), // Para ECB (No usa IV)
    IV_12(12), // GCM
    IV_16(16); // CBC, CFB, OFB

    private final int size;

    IVSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return size + " bytes";
    }

    public static IVSize fromSize(int size) {
        for (IVSize iv : values()) {
            if (iv.size == size) {
                return iv;
            }
        }
        throw new IllegalArgumentException("Invalid IVSize: " + size);
    }
}
