package com.iluncrypt.iluncryptapp.models.enums.aes;

/**
 * Enumeration of supported GCM authentication tag sizes.
 */
public enum GCMTagSize {
    TAG_96(96),
    TAG_104(104),
    TAG_112(112),
    TAG_120(120),
    TAG_128(128);

    private final int size;

    GCMTagSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return size + " bits";
    }

    public static GCMTagSize fromSize(int size) {
        for (GCMTagSize tag : values()) {
            if (tag.size == size) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Invalid GCMTagSize: " + size);
    }
}
