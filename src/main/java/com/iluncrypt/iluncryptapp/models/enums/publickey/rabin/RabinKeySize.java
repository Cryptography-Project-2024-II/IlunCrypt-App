package com.iluncrypt.iluncryptapp.models.enums.publickey.rabin;

public enum RabinKeySize {
    RABIN_1024(1024),
    RABIN_2048(2048),
    RABIN_4096(4096);

    private final int size;

    RabinKeySize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}