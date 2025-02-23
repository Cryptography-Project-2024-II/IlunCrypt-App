package com.iluncrypt.iluncryptapp.models.algorithms.publickey;

import com.iluncrypt.iluncryptapp.models.ElGamalConfig;
import com.iluncrypt.iluncryptapp.models.enums.publickey.elgamal.PaddingScheme;
import java.math.BigInteger;
import java.security.SecureRandom;

public class ElGamalManager {
    private static final SecureRandom random = new SecureRandom();

    public static class KeyPair {
        public final String publicKey; // Format: "p,g,h"
        public final String privateKey; // Format: "x"

        public KeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }

    public static KeyPair generateKeyPair(ElGamalConfig config) {
        BigInteger p = generatePrime(config.getKeySize().getSize());
        BigInteger g = generateGenerator(p);
        BigInteger x = generatePrivateKey(p, config.getKeySize().getSize());
        BigInteger h = g.modPow(x, p);

        return new KeyPair(
                p.toString() + "," + g.toString() + "," + h.toString(),
                x.toString()
        );
    }

    public static String encryptText(String text, String publicKey, ElGamalConfig config) throws Exception {
        String[] parts = publicKey.split(",");
        validatePublicKey(parts);

        BigInteger p = new BigInteger(parts[0]);
        BigInteger g = new BigInteger(parts[1]);
        BigInteger h = new BigInteger(parts[2]);

        validatePrime(p, "p");
        validateGenerator(g, p);

        byte[] paddedData = applyPadding(text.getBytes(), p, config.getPaddingScheme());
        BigInteger m = new BigInteger(1, paddedData); // Unsigned

        if(m.compareTo(p) >= 0) {
            throw new Exception("Padded message too large for key size");
        }

        BigInteger k = generateEphemeralKey(p, config.getKeySize().getSize());
        BigInteger c1 = g.modPow(k, p);
        BigInteger c2 = m.multiply(h.modPow(k, p)).mod(p);

        return c1.toString() + "," + c2.toString();
    }

    public static String decryptText(String cipherText, String publicKey, String privateKey, ElGamalConfig config) throws Exception {
        String[] pubParts = publicKey.split(",");
        validatePublicKey(pubParts);

        BigInteger p = new BigInteger(pubParts[0]);
        BigInteger x = new BigInteger(privateKey);

        String[] cipherParts = cipherText.split(",");
        if(cipherParts.length != 2) {
            throw new Exception("Invalid ciphertext format");
        }

        BigInteger c1 = new BigInteger(cipherParts[0]);
        BigInteger c2 = new BigInteger(cipherParts[1]);

        BigInteger s = c1.modPow(x, p);
        BigInteger m = c2.multiply(s.modInverse(p)).mod(p);

        byte[] paddedData = m.toByteArray();
        return new String(removePadding(paddedData, config.getPaddingScheme()));
    }

    private static byte[] applyPadding(byte[] data, BigInteger p, PaddingScheme scheme) {
        int maxSize = (p.bitLength() - 1) / 8; // Bytes
        int padLength = maxSize - data.length;

        switch(scheme) {
            case PKCS7:
                return pkcs7Pad(data, padLength);
            case OAEP:
                return oaepPad(data, maxSize);
            default:
                return data;
        }
    }

    private static byte[] removePadding(byte[] data, PaddingScheme scheme) {
        switch(scheme) {
            case PKCS7:
                return pkcs7Unpad(data);
            case OAEP:
                return oaepUnpad(data);
            default:
                return data;
        }
    }

    private static byte[] pkcs7Pad(byte[] data, int padLength) {
        if(padLength <= 0) throw new IllegalArgumentException("Message too long");

        byte[] padded = new byte[data.length + padLength];
        System.arraycopy(data, 0, padded, 0, data.length);
        byte padValue = (byte) padLength;

        for(int i = data.length; i < padded.length; i++) {
            padded[i] = padValue;
        }
        return padded;
    }

    private static byte[] pkcs7Unpad(byte[] data) {
        int padValue = data[data.length - 1] & 0xFF;
        if(padValue < 1 || padValue > data.length) {
            throw new IllegalArgumentException("Invalid padding");
        }

        byte[] unpadded = new byte[data.length - padValue];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }

    private static byte[] oaepPad(byte[] data, int maxSize) {
        // Implementation would go here
        return data; // Placeholder
    }

    private static byte[] oaepUnpad(byte[] data) {
        // Implementation would go here
        return data; // Placeholder
    }

    private static BigInteger generatePrime(int bitLength) {
        return BigInteger.probablePrime(bitLength, random);
    }

    private static BigInteger generateGenerator(BigInteger p) {

        return BigInteger.valueOf(2);
    }

    private static BigInteger generatePrivateKey(BigInteger p, int bitLength) {
        return new BigInteger(bitLength, random)
                .mod(p.subtract(BigInteger.ONE))
                .add(BigInteger.ONE);
    }

    private static BigInteger generateEphemeralKey(BigInteger p, int bitLength) {
        return new BigInteger(bitLength, random)
                .mod(p.subtract(BigInteger.ONE))
                .add(BigInteger.ONE);
    }

    private static void validatePublicKey(String[] parts) throws Exception {
        if(parts.length != 3) {
            throw new Exception("Public key must contain p,g,h separated by commas");
        }
    }

    private static void validatePrime(BigInteger p, String name) throws Exception {
        if(!p.isProbablePrime(100)) {
            throw new Exception(name + " must be a prime number");
        }
    }

    private static void validateGenerator(BigInteger g, BigInteger p) throws Exception {
        if(g.compareTo(BigInteger.ONE) <= 0 || g.compareTo(p) >= 0) {
            throw new Exception("Generator g must be between 2 and p-1");
        }
    }
}