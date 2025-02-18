package com.iluncrypt.iluncryptapp.models.algorithms.symmetrickey;

import com.iluncrypt.iluncryptapp.models.AESConfig;
import com.iluncrypt.iluncryptapp.utils.filemanager.IlunFileManager;
import com.iluncrypt.iluncryptapp.utils.filemanager.IlunFileMetadata;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.Base64;

/**
 * Handles AES encryption and decryption for text, files, and images.
 */
public class AESManager {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a random AES key based on the given configuration.
     */
    public static SecretKey generateKey(AESConfig config) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(config.getKeySize().getSize());
        return keyGenerator.generateKey();
    }

    /**
     * Encrypts a plain text string and returns the encrypted text as a Base64-encoded string.
     *
     * @param text   the plain text to encrypt.
     * @param key    the SecretKey used for encryption.
     * @param config the AES configuration.
     * @return the Base64-encoded encrypted text.
     * @throws Exception if an error occurs during encryption.
     */
    public static String encryptText(String text, SecretKey key, AESConfig config) throws Exception {
        byte[] encryptedBytes = encrypt(text.getBytes(StandardCharsets.UTF_8), key, config);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    /**
     * Decrypts a Base64-encoded encrypted text string and returns the original plain text.
     *
     * @param encryptedText the Base64-encoded encrypted text.
     * @param key           the SecretKey used for decryption.
     * @param config        the AES configuration.
     * @return the decrypted plain text.
     * @throws Exception if an error occurs during decryption.
     */
    public static String decryptText(String encryptedText, SecretKey key, AESConfig config) throws Exception {
        byte[] cipherBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = decrypt(cipherBytes, key, config);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


    /**
     * Encrypts a given byte array using AES.
     */
    public static byte[] encrypt(byte[] data, SecretKey key, AESConfig config) throws Exception {
        Cipher cipher = Cipher.getInstance(config.getTransformation());
        byte[] iv = new byte[config.getIvSize().getSize()];
        SECURE_RANDOM.nextBytes(iv);

        if (config.getMode().requiresIV()) {
            if (config.getMode().usesGCM()) {
                GCMParameterSpec gcmSpec = new GCMParameterSpec(config.getGcmTagSize().getSize(), iv);
                cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            } else {
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            }
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }

        byte[] encryptedData = cipher.doFinal(data);
        ByteBuffer buffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        buffer.put(iv);
        buffer.put(encryptedData);

        return buffer.array();
    }

    /**
     * Decrypts an encrypted byte array using AES.
     */
    public static byte[] decrypt(byte[] encryptedData, SecretKey key, AESConfig config) throws Exception {
        Cipher cipher = Cipher.getInstance(config.getTransformation());
        ByteBuffer buffer = ByteBuffer.wrap(encryptedData);
        byte[] iv = new byte[config.getIvSize().getSize()];
        buffer.get(iv);
        byte[] cipherText = new byte[buffer.remaining()];
        buffer.get(cipherText);

        if (config.getMode().requiresIV()) {
            if (config.getMode().usesGCM()) {
                GCMParameterSpec gcmSpec = new GCMParameterSpec(config.getGcmTagSize().getSize(), iv);
                cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            } else {
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            }
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }

        return cipher.doFinal(cipherText);
    }

    /**
     * Encrypts a file and saves it as .ilun.
     */
    public static void encryptFile(File inputFile, File outputFile, SecretKey key, AESConfig config) throws Exception {
        byte[] fileBytes = readFile(inputFile);
        byte[] encryptedData = encrypt(fileBytes, key, config);

        IlunFileMetadata metadata = new IlunFileMetadata(
                config.isStoreAlgorithm() ? "AES" : null,
                getFileExtension(inputFile),
                fileBytes.length,
                calculateChecksum(fileBytes),
                config.isStoreAlgorithm()
        );

        IlunFileManager.writeIlunFile(outputFile, encryptedData, metadata, config.getIvSize().getSize() > 0 ? Arrays.copyOfRange(encryptedData, 0, config.getIvSize().getSize()) : null);
    }

    /**
     * Decrypts an .ilun file back to its original format.
     */
    public static void decryptFile(File inputFile, File outputFile, SecretKey key, AESConfig config) throws Exception {
        IlunFileMetadata metadata = IlunFileManager.readIlunMetadata(inputFile);
        byte[] encryptedData = readFile(inputFile);
        byte[] decryptedData = decrypt(encryptedData, key, config);
        writeFile(outputFile, decryptedData);
    }

    /**
     * Encrypts an image and saves it in the same format.
     */
    public static void encryptImage(File inputImage, File outputImage, SecretKey key, AESConfig config) throws Exception {
        BufferedImage image = ImageIO.read(inputImage);
        byte[] imageBytes = flattenImage(image);
        byte[] encryptedData = encrypt(imageBytes, key, config);
        BufferedImage encryptedImage = reconstructImage(encryptedData, image.getWidth(), image.getHeight());
        ImageIO.write(encryptedImage, getFileExtension(inputImage), outputImage);
    }

    /**
     * Decrypts an encrypted image back to its original form.
     */
    public static void decryptImage(File encryptedImageFile, File outputImage, SecretKey key, AESConfig config) throws Exception {
        BufferedImage image = ImageIO.read(encryptedImageFile);
        byte[] encryptedData = flattenImage(image);
        byte[] decryptedData = decrypt(encryptedData, key, config);
        BufferedImage originalImage = reconstructImage(decryptedData, image.getWidth(), image.getHeight());
        ImageIO.write(originalImage, getFileExtension(encryptedImageFile), outputImage);
    }

    /* Utility Methods */

    private static byte[] readFile(File file) throws IOException {
        return java.nio.file.Files.readAllBytes(file.toPath());
    }

    private static void writeFile(File file, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }

    private static byte[] calculateChecksum(byte[] data) throws NoSuchAlgorithmException {
        return java.security.MessageDigest.getInstance("SHA-256").digest(data);
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        return name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : "";
    }

    private static byte[] flattenImage(BufferedImage image) {
        return ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    private static BufferedImage reconstructImage(byte[] pixelData, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, width, height, pixelData);
        return image;
    }
}
