package com.iluncrypt.iluncryptapp.models.algorithms.publickey;

import com.iluncrypt.iluncryptapp.models.MenezesVanstoneConfig;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

public class MenezesVanstoneManager {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static final SecureRandom random = new SecureRandom();

    public record KeyPair(String publicKey, String privateKey) {}

    public static KeyPair generateKeyPair(MenezesVanstoneConfig config) throws Exception {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(config.getKeySize().getCurveName());
        KeyPairGenerator gen = KeyPairGenerator.getInstance("EC", "BC");
        gen.initialize(ecSpec, random);

        java.security.KeyPair keyPair = gen.generateKeyPair();
        BigInteger d = ((org.bouncycastle.jce.interfaces.ECPrivateKey) keyPair.getPrivate()).getD();
        ECPoint Q = ((org.bouncycastle.jce.interfaces.ECPublicKey) keyPair.getPublic()).getQ();

        return new KeyPair(
                bytesToHex(Q.getEncoded(true)),
                d.toString(16)
        );
    }

    public static String encrypt(String plaintext, String publicKeyHex, MenezesVanstoneConfig config) throws Exception {
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(config.getKeySize().getCurveName());
        ECPoint Q = spec.getCurve().decodePoint(hexToBytes(publicKeyHex));

        // Generate ephemeral key
        BigInteger k = new BigInteger(spec.getN().bitLength(), random).mod(spec.getN());
        ECPoint kG = spec.getG().multiply(k).normalize();
        ECPoint kQ = Q.multiply(k).normalize();

        // Split and encrypt message
        byte[] plainBytes = plaintext.getBytes();
        int splitPoint = (plainBytes.length + 1) / 2;
        BigInteger m1 = new BigInteger(1, java.util.Arrays.copyOfRange(plainBytes, 0, splitPoint));
        BigInteger m2 = new BigInteger(1, java.util.Arrays.copyOfRange(plainBytes, splitPoint, plainBytes.length));

        BigInteger p = spec.getCurve().getField().getCharacteristic();
        BigInteger c1 = m1.multiply(kQ.getXCoord().toBigInteger()).mod(p);
        BigInteger c2 = m2.multiply(kQ.getYCoord().toBigInteger()).mod(p);

        return bytesToHex(kG.getEncoded(true)) + "|" + c1.toString(16) + "|" + c2.toString(16);
    }

    public static String decrypt(String ciphertext, String privateKeyHex, MenezesVanstoneConfig config) throws Exception {
        String[] parts = ciphertext.split("\\|");
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(config.getKeySize().getCurveName());

        ECPoint kG = spec.getCurve().decodePoint(hexToBytes(parts[0]));
        BigInteger c1 = new BigInteger(parts[1], 16);
        BigInteger c2 = new BigInteger(parts[2], 16);
        BigInteger d = new BigInteger(privateKeyHex, 16);

        ECPoint kQ = kG.multiply(d).normalize();
        BigInteger p = spec.getCurve().getField().getCharacteristic();

        BigInteger m1 = c1.multiply(kQ.getXCoord().toBigInteger().modInverse(p)).mod(p);
        BigInteger m2 = c2.multiply(kQ.getYCoord().toBigInteger().modInverse(p)).mod(p);

        byte[] decrypted = concatenateArrays(m1.toByteArray(), m2.toByteArray());
        return new String(decrypted).trim();
    }

    private static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            bytes[i] = (byte) Integer.parseInt(hex.substring(index, index + 2), 16);
        }
        return bytes;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] concatenateArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}