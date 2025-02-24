package com.iluncrypt.iluncryptapp.utils.filemanager;

import java.util.Arrays;

/**
 * Represents metadata stored in .ilun encrypted files.
 */
public class IlunFileMetadata {
    private final String algorithm;
    private final String extension;
    private final int originalSize;
    private final byte[] checksum;
    private final boolean storeAlgorithm;

    /**
     * Constructs file metadata for an encrypted file.
     *
     * @param algorithm      The encryption algorithm used.
     * @param extension      The original file extension.
     * @param originalSize   The original file size before encryption.
     * @param checksum       The checksum of the original file.
     * @param storeAlgorithm Whether to store the algorithm name in the file.
     */
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

    @Override
    public String toString() {
        return "IlunFileMetadata{" +
                "algorithm='" + algorithm + '\'' +
                ", extension='" + extension + '\'' +
                ", originalSize=" + originalSize +
                ", checksum=" + Arrays.toString(checksum) +
                ", storeAlgorithm=" + storeAlgorithm +
                '}';
    }
}