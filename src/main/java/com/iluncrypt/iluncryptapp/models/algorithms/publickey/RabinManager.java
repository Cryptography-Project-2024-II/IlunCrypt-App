package com.iluncrypt.iluncryptapp.models.algorithms.publickey;

import com.iluncrypt.iluncryptapp.models.RabinConfig;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RabinManager {
    private static final SecureRandom random = new SecureRandom();

    public static class KeyPair {
        public final String publicKey; // n
        public final String privateKey; // p,q

        public KeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }

    public static KeyPair generateKeyPair(RabinConfig config) {
        int bitLength = config.getKeySize().getSize() / 2;
        BigInteger p = generatePrime(bitLength);
        BigInteger q = generatePrime(bitLength);
        BigInteger n = p.multiply(q);
        return new KeyPair(n.toString(), p + "," + q);
    }

    public static String encryptText(String plainText, String publicKey,
                                     RabinConfig config) throws Exception {
        BigInteger n = new BigInteger(publicKey);
        // Use UTF-8 encoding to convert plain text to bytes
        byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);
        BigInteger m = new BigInteger(1, bytes);

        if (m.compareTo(n) >= 0) {
            throw new Exception("Message too large for modulus");
        }

        return m.modPow(BigInteger.TWO, n).toString();
    }

    public static List<String> decryptText(String cipherText, String privateKey,
                                           String publicKey) throws Exception {
        List<String> results = new ArrayList<>();
        String[] primes = privateKey.split(",");
        BigInteger p = new BigInteger(primes[0]);
        BigInteger q = new BigInteger(primes[1]);
        BigInteger n = new BigInteger(publicKey);
        BigInteger c = new BigInteger(cipherText);

        BigInteger[] roots = computeSquareRoots(c, p, q);

        for (BigInteger m : roots) {
            byte[] bytes = m.toByteArray();
            if (bytes.length > 0 && bytes[0] == 0) {
                bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
            }
            String text = new String(bytes, StandardCharsets.UTF_8);
            results.add(text);
        }

        return results;
    }

    private static BigInteger[] computeSquareRoots(BigInteger c, BigInteger p, BigInteger q) {
        BigInteger n = p.multiply(q);
        BigInteger mp = c.modPow(p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), p);
        BigInteger mq = c.modPow(q.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), q);

        BigInteger yp = p.modInverse(q);
        BigInteger yq = q.modInverse(p);

        return new BigInteger[] {
                mp.multiply(q).multiply(yq).add(mq.multiply(p).multiply(yp)).mod(n),
                mp.multiply(q).multiply(yq).subtract(mq.multiply(p).multiply(yp)).mod(n),
                n.subtract(mp.multiply(q).multiply(yq).add(mq.multiply(p).multiply(yp))).mod(n),
                n.subtract(mp.multiply(q).multiply(yq).subtract(mq.multiply(p).multiply(yp))).mod(n)
        };
    }

    private static BigInteger generatePrime(int bitLength) {
        while (true) {
            BigInteger prime = BigInteger.probablePrime(bitLength, random);
            if (prime.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
                return prime;
            }
        }
    }

    private static void validatePrime(BigInteger prime, String name) throws Exception {
        if (!prime.isProbablePrime(100)) {
            throw new Exception(name + " is not prime");
        }
        if (!prime.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
            throw new Exception(name + " must be ≡ 3 mod 4");
        }
    }

    private static void validateKeyConsistency(BigInteger p, BigInteger q, BigInteger n)
            throws Exception {

        if (!p.multiply(q).equals(n)) {
            throw new Exception("Key consistency check failed");
        }
    }
}