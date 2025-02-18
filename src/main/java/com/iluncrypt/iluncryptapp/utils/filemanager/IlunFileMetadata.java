package com.iluncrypt.iluncryptapp.utils.filemanager;

/**
 * Represents metadata stored in .ilun files.
 */
public class IlunFileMetadata {
    private final String algorithm;
    private final String extension;
    private final int originalSize;
    private final byte[] checksum;
    private final boolean storeAlgorithm;

    public IlunFileMetadata(String algorithm, String extension, int originalSize, byte[] checksum, boolean storeAlgorithm) {
        this.algorithm = algorithm;
        this.extension = extension;
        this.originalSize = originalSize;
        this.checksum = checksum;
        this.storeAlgorithm = storeAlgorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getExtension() {
        return extension;
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public byte[] getChecksum() {
        return checksum;
    }

    public boolean isStoreAlgorithm() {
        return storeAlgorithm;
    }
}
