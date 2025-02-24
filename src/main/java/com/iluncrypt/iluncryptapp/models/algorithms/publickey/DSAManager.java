package com.iluncrypt.iluncryptapp.models.algorithms.publickey;

import com.iluncrypt.iluncryptapp.models.DSAConfig;
import com.iluncrypt.iluncryptapp.utils.filemanager.IlunFileManager;
import com.iluncrypt.iluncryptapp.utils.filemanager.IlunFileMetadata;
import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class DSAManager {
    private static final String ALGORITHM = "DSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withDSA";

    public static KeyPair generateKeyPair(DSAConfig config) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(config.getKeySize().getSize());
        return keyGen.generateKeyPair();
    }

    public static String encryptText(String text, PublicKey publicKey, DSAConfig config) throws Exception {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        validateDataSize(data.length, config);
        return Base64.getEncoder().encodeToString(encryptBytes(data, publicKey, config));
    }

    public static String decryptText(String encryptedText, PrivateKey privateKey, DSAConfig config) throws Exception {
        byte[] decrypted = decryptBytes(Base64.getDecoder().decode(encryptedText), privateKey, config);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static byte[] encryptBytes(byte[] data, PublicKey publicKey, DSAConfig config) throws Exception {
        Cipher cipher = Cipher.getInstance(config.getTransformation());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    private static byte[] decryptBytes(byte[] encryptedData, PrivateKey privateKey, DSAConfig config) throws Exception {
        Cipher cipher = Cipher.getInstance(config.getTransformation());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    private static void validateDataSize(int length, DSAConfig config) {
        int maxSize = config.getKeySize().getSize()/8 - 11;
        if(length > maxSize) {
            throw new IllegalArgumentException(
                    "Data exceeds maximum size of " + maxSize + " bytes for DSA-" +
                            config.getKeySize().getSize()
            );
        }
    }

    // New methods for digital signature

    /**
     * Generates a digital signature for a file
     *
     * @param file File to sign
     * @param privateKey Private key to sign with
     * @return Base64 encoded signature
     */
    public static String signFile(File file, PrivateKey privateKey) throws Exception {
        byte[] fileHash = calculateFileHash(file);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(fileHash);
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    /**
     * Verifies a digital signature for a file
     *
     * @param file File to verify
     * @param signatureBase64 Base64 encoded signature
     * @param publicKey Public key to verify with
     * @return True if signature is valid, false otherwise
     */
    public static boolean verifyFileSignature(File file, String signatureBase64, PublicKey publicKey) throws Exception {
        byte[] fileHash = calculateFileHash(file);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(fileHash);

        return signature.verify(signatureBytes);
    }

    /**
     * Calculates SHA-256 hash of a file
     *
     * @param file File to hash
     * @return Hash bytes
     */
    private static byte[] calculateFileHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        return digest.digest();
    }
}