package com.iluncrypt.iluncryptapp.models.algorithms.symmetrickey;

import com.iluncrypt.iluncryptapp.models.SymmetricKeyConfig;
import com.iluncrypt.iluncryptapp.models.enums.symmetrickey.*;
import com.iluncrypt.iluncryptapp.utils.filemanager.IlunFileManager;
import com.iluncrypt.iluncryptapp.utils.filemanager.IlunFileMetadata;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

/**
 * Manages AES encryption and decryption with support for IVs and HMAC authentication.
 */
public class AESManager {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a random symmetric key based on the given configuration.
     *
     * @param config The encryption configuration.
     * @return A generated SecretKey.
     * @throws NoSuchAlgorithmException if the algorithm is not available.
     */
    public static SecretKey generateKey(SymmetricKeyConfig config) throws NoSuchAlgorithmException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance(config.getAlgorithm().name());
        keyGenerator.init(config.getKeySize().getSize());
        return keyGenerator.generateKey();
    }

    /**
     * Generates a secure Initialization Vector (IV) for encryption.
     *
     * @param config The symmetric encryption configuration.
     * @return A randomly generated IV of the correct size.
     * @throws IllegalArgumentException if the mode does not require an IV.
     */
    public static byte[] generateIV(SymmetricKeyConfig config) {
        if (!config.getMode().requiresIV()) {
            return null;
        }
        int ivSize = config.getMode().getFixedIVSize();
        if (ivSize == -1) {
            ivSize = config.getAlgorithm().getBaseIVSize();
        }
        byte[] iv = new byte[ivSize];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }

    /**
     * Encrypts a string using AES.
     *
     * @param text   The plaintext.
     * @param key    The AES key.
     * @param iv     The IV (if required).
     * @param config The encryption configuration.
     * @return Encrypted Base64 string.
     * @throws Exception If encryption fails.
     */
    public static String encryptText(String text, SecretKey key, byte[] iv, SymmetricKeyConfig config) throws Exception {
        byte[] encryptedBytes = encrypt(text.getBytes(StandardCharsets.UTF_8), key, iv, config);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts a Base64 encoded string.
     *
     * @param encryptedText The encrypted text.
     * @param key           The AES key.
     * @param config        The encryption configuration.
     * @return Decrypted plaintext.
     * @throws Exception If decryption fails.
     */
    public static String decryptText(String encryptedText, SecretKey key,SymmetricKeyConfig config) throws Exception {
        byte[] cipherBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = decrypt(cipherBytes, key, config);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Encrypts a given byte array using a symmetric encryption algorithm.
     *
     * @param data  The data to encrypt.
     * @param key   The secret key used for encryption.
     * @param iv    The initialization vector (IV). Can be null if the mode does not require it.
     * @param config The encryption configuration.
     * @return The encrypted byte array.
     * @throws Exception if encryption fails.
     */
    public static byte[] encrypt(byte[] data, SecretKey key, byte[] iv, SymmetricKeyConfig config) throws Exception {
        String transformation = config.getAlgorithm().name() + "/" + config.getMode().getMode() + "/" + config.getPaddingScheme().getPadding();
        Cipher cipher = Cipher.getInstance(transformation);

        if (config.getMode() == SymmetricKeyMode.GCM) {
            GCMParameterSpec gcmSpec = new GCMParameterSpec(config.getGCMTagSize().getSize(), iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
        } else if (config.getMode().requiresIV()) {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }

        byte[] encryptedData = cipher.doFinal(data);
        byte[] hmac = generateHMAC(encryptedData, key, config);

        int totalSize = (iv != null ? iv.length : 0) + encryptedData.length + (hmac != null ? hmac.length : 0);
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        if (iv != null) buffer.put(iv);
        buffer.put(encryptedData);
        if (hmac != null) buffer.put(hmac);

        return buffer.array();
    }

    /**
     * Decrypts an encrypted byte array using a symmetric encryption algorithm.
     *
     * @param encryptedData The encrypted data containing IV and optional HMAC.
     * @param key           The secret key used for decryption.
     * @param config        The encryption configuration.
     * @return The decrypted byte array.
     * @throws Exception if decryption fails.
     */
    public static byte[] decrypt(byte[] encryptedData, SecretKey key, SymmetricKeyConfig config) throws Exception {
        String transformation = config.getAlgorithm().name() + "/" + config.getMode().getMode() + "/" + config.getPaddingScheme().getPadding();
        Cipher cipher = Cipher.getInstance(transformation);

        ByteBuffer buffer = ByteBuffer.wrap(encryptedData);

        int ivSize = config.getMode().getFixedIVSize();
        if (ivSize == -1) {
            ivSize = config.getAlgorithm().getBaseIVSize();
        }

        byte[] iv = null;
        if (config.getMode().requiresIV()) {
            iv = new byte[ivSize];
            buffer.get(iv);
        }

        int hmacSize = config.getAuthenticationMethod().getHMACSize() / 8;
        boolean hasHMAC = config.getAuthenticationMethod() != AuthenticationMethod.NONE;

        int cipherTextSize = buffer.remaining() - (hasHMAC ? hmacSize : 0);

        if (cipherTextSize <= 0) {
            throw new SecurityException("Cipher text is too short to contain a valid HMAC.");
        }

        byte[] cipherText = new byte[cipherTextSize];
        buffer.get(cipherText);

        byte[] receivedHMAC = null;
        if (hasHMAC) {
            receivedHMAC = new byte[hmacSize];
            buffer.get(receivedHMAC);
        }

        if (hasHMAC) {
            if (!verifyHMAC(cipherText, receivedHMAC, key, config)) {
                throw new SecurityException("Authentication failed: HMAC verification failed.");
            }
        }

        if (config.getMode() == SymmetricKeyMode.GCM) {
            GCMParameterSpec gcmSpec = new GCMParameterSpec(config.getGCMTagSize().getSize(), iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
        } else if (config.getMode().requiresIV()) {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }

        return cipher.doFinal(cipherText);
    }


    /**
     * Encrypts a file and returns the encrypted data as a byte array.
     *
     * @param inputFile The input file to be encrypted.
     * @param key       The secret key used for encryption.
     * @param iv        The initialization vector (IV), required for certain modes.
     * @param config    The encryption configuration.
     * @return The encrypted file data as a byte array.
     * @throws Exception if encryption fails.
     */
    public static byte[] encryptFile(File inputFile, SecretKey key, byte[] iv, SymmetricKeyConfig config) throws Exception {
        byte[] fileBytes = readFile(inputFile);

        // Generar IV si es necesario y no se proporciona
        if (config.getMode().requiresIV() && iv == null) {
            iv = generateIV(config);
        }

        byte[] encryptedData = encrypt(fileBytes, key, iv, config);

        // Concatenar IV si es necesario
        int totalSize = (iv != null ? iv.length : 0) + encryptedData.length;
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        if (iv != null) buffer.put(iv);
        buffer.put(encryptedData);

        return buffer.array();
    }


    /**
     * Decrypts an encrypted file and returns the decrypted data as a byte array.
     *
     * @param inputFile The encrypted input file.
     * @param key       The secret key used for decryption.
     * @param config    The encryption configuration.
     * @return The decrypted file data as a byte array.
     * @throws Exception if decryption fails.
     */
    public static byte[] decryptFile(File inputFile, SecretKey key, SymmetricKeyConfig config) throws Exception {
        byte[] encryptedData = readFile(inputFile);
        ByteBuffer buffer = ByteBuffer.wrap(encryptedData);

        byte[] iv = null;
        if (config.getMode().requiresIV()) {
            int ivSize = config.getMode().getFixedIVSize();
            if (ivSize == -1) ivSize = config.getAlgorithm().getBaseIVSize();
            iv = new byte[ivSize];
            buffer.get(iv); // Leer IV del archivo
        }

        byte[] cipherText = new byte[buffer.remaining()];
        buffer.get(cipherText);

        return decrypt(cipherText, key, config);
    }


    /**
     * Generates an HMAC for data integrity verification.
     */
    private static byte[] generateHMAC(byte[] data, SecretKey key, SymmetricKeyConfig config) throws Exception {
        if (config.getAuthenticationMethod() == AuthenticationMethod.NONE) return null;

        Mac mac = Mac.getInstance(config.getAuthenticationMethod().getHMACAlgorithm());

        // Usa la clave derivada para HMAC
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), config.getAuthenticationMethod().getHMACAlgorithm());
        mac.init(keySpec);

        return mac.doFinal(data);
    }


    private static boolean verifyHMAC(byte[] data, byte[] receivedHMAC, SecretKey key, SymmetricKeyConfig config) throws Exception {
        int expectedHMACSize = config.getAuthenticationMethod().getHMACSize() / 8;
        if (receivedHMAC == null || receivedHMAC.length != expectedHMACSize) {
            System.out.println("HMAC size mismatch: Expected " + expectedHMACSize + ", but received " + (receivedHMAC != null ? receivedHMAC.length : 0));
            return false;
        }

        byte[] computedHMAC = generateHMAC(data, key, config);

        System.out.println("Computed HMAC: " + Base64.getEncoder().encodeToString(computedHMAC));
        System.out.println("Received HMAC: " + Base64.getEncoder().encodeToString(receivedHMAC));

        return MessageDigest.isEqual(computedHMAC, receivedHMAC);
    }


    /* Utility Methods */

    private static byte[] readFile(File file) throws IOException {
        return java.nio.file.Files.readAllBytes(file.toPath());
    }


    private static byte[] calculateChecksum(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(data);
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        return name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : "";
    }
}
