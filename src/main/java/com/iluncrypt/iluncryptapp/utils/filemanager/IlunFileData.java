package com.iluncrypt.iluncryptapp.utils.filemanager;

/**
 * Represents extracted data from an .ilun encrypted file.
 */
public class IlunFileData {
    private final IlunFileMetadata metadata;
    private final byte[] encryptedData;

    public IlunFileData(IlunFileMetadata metadata, byte[] encryptedData) {
        this.metadata = metadata;
        this.encryptedData = encryptedData;
    }

    public IlunFileMetadata getMetadata() {
        return metadata;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }
}
