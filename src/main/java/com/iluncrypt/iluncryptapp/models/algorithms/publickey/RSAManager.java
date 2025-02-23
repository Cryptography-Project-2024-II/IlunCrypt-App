package com.iluncrypt.iluncryptapp.models.algorithms.publickey;

import com.iluncrypt.iluncryptapp.models.RSAConfig;
import com.iluncrypt.iluncryptapp.utils.filemanager.IlunFileManager;
import com.iluncrypt.iluncryptapp.utils.filemanager.IlunFileMetadata;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class RSAManager {
    private static final String ALGORITHM = "RSA";

    public static KeyPair generateKeyPair(RSAConfig config) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(config.getKeySize().getSize());
        return keyGen.generateKeyPair();
    }

    public static String encryptText(String text, PublicKey publicKey, RSAConfig config) throws Exception {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        validateDataSize(data.length, config);
        return Base64.getEncoder().encodeToString(encryptBytes(data, publicKey, config));
    }

    public static String decryptText(String encryptedText, PrivateKey privateKey, RSAConfig config) throws Exception {
        byte[] decrypted = decryptBytes(Base64.getDecoder().decode(encryptedText), privateKey, config);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

//    public static void encryptFile(File inputFile, File outputFile, PublicKey publicKey, RSAConfig config) throws Exception {
//        byte[] fileData = IlunFileManager.readFile(inputFile);
//        validateDataSize(fileData.length, config);
//
//        IlunFileMetadata metadata = new IlunFileMetadata(
//                config.isStoreAlgorithm() ? "RSA" : null,
//                IlunFileManager.getFileExtension(inputFile),
//                fileData.length,
//                new byte[0], // Checksum omitted for simplicity
//                config.isStoreAlgorithm()
//        );
//
//        IlunFileManager.writeIlunFile(outputFile, encryptBytes(fileData, publicKey, config), metadata);
//    }

    private static byte[] encryptBytes(byte[] data, PublicKey publicKey, RSAConfig config) throws Exception {
        Cipher cipher = Cipher.getInstance(config.getTransformation());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    private static byte[] decryptBytes(byte[] encryptedData, PrivateKey privateKey, RSAConfig config) throws Exception {
        Cipher cipher = Cipher.getInstance(config.getTransformation());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    private static void validateDataSize(int length, RSAConfig config) {
        int maxSize = config.getKeySize().getSize()/8 - 11;
        if(length > maxSize) {
            throw new IllegalArgumentException(
                    "Data exceeds maximum size of " + maxSize + " bytes for RSA-" +
                            config.getKeySize().getSize()
            );
        }
    }
}