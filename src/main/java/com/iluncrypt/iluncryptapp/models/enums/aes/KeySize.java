package com.iluncrypt.iluncryptapp.models.enums.aes;

/**
 * Enumeration of supported AES key sizes.
 */
public enum KeySize {
    AES_128(128),
    AES_192(192),
    AES_256(256);

    private final int size;

    KeySize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return size + " bits";
    }

    public static KeySize fromSize(int size) {
        for (KeySize ks : values()) {
            if (ks.size == size) {
                return ks;
            }
        }
        throw new IllegalArgumentException("Invalid KeySize: " + size);
    }

}
