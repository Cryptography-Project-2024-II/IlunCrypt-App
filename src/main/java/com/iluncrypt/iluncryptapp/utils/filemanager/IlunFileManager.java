package com.iluncrypt.iluncryptapp.utils.filemanager;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Handles the reading and writing of .ilun encrypted files.
 */
public class IlunFileManager {

    private static final byte[] MAGIC_HEADER = "ILUNCR1\0".getBytes(StandardCharsets.UTF_8);

    /**
     * Writes an encrypted file with metadata and cipher data.
     */
    public static void writeIlunFile(File outputFile, byte[] encryptedData, IlunFileMetadata metadata, byte[] iv) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(MAGIC_HEADER);
            fos.write(metadata.isStoreAlgorithm() ? 0x01 : 0x00);

            if (metadata.isStoreAlgorithm()) {
                fos.write(metadata.getAlgorithm().getBytes(StandardCharsets.UTF_8));
            }

            fos.write(padString(metadata.getExtension(), 10));
            fos.write(ByteBuffer.allocate(4).putInt(metadata.getOriginalSize()).array());
            fos.write(metadata.getChecksum());
            fos.write(new byte[4]); // Reserved bytes

            if (iv != null) {
                fos.write(iv);
            }

            fos.write(encryptedData);
        }
    }

    /**
     * Reads metadata from an encrypted .ilun file.
     */
    public static IlunFileMetadata readIlunMetadata(File inputFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            byte[] magicHeader = new byte[8];
            fis.read(magicHeader);

            if (!Arrays.equals(magicHeader, MAGIC_HEADER)) {
                throw new IOException("Invalid .ilun file format");
            }

            int flag = fis.read();
            String algorithm = null;
            if (flag == 0x01) {
                byte[] algoBytes = new byte[3];
                fis.read(algoBytes);
                algorithm = new String(algoBytes, StandardCharsets.UTF_8);
            }

            byte[] extensionBytes = new byte[10];
            fis.read(extensionBytes);
            String extension = new String(extensionBytes, StandardCharsets.UTF_8).trim();

            byte[] sizeBytes = new byte[4];
            fis.read(sizeBytes);
            int originalSize = ByteBuffer.wrap(sizeBytes).getInt();

            byte[] checksum = new byte[32];
            fis.read(checksum);

            fis.skip(4); // Reserved bytes

            return new IlunFileMetadata(algorithm, extension, originalSize, checksum, flag == 0x01);
        }
    }

    /**
     * Pads a string to a fixed length with null bytes.
     */
    private static byte[] padString(String input, int length) {
        byte[] padded = new byte[length];
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(inputBytes, 0, padded, 0, Math.min(inputBytes.length, length));
        return padded;
    }
}
