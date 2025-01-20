package com.iluncrypt.iluncryptapp.models.algorithms;

import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.Cryptosystem;
import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import com.iluncrypt.iluncryptapp.models.enums.WhitespaceHandling;
import com.iluncrypt.iluncryptapp.models.keys.AffineKey;
import com.iluncrypt.iluncryptapp.models.keys.Key;

/**
 * Implements the Affine Cipher encryption and decryption, inheriting from the Cryptosystem class.
 * The Affine Cipher is a monoalphabetic substitution cipher where each letter is transformed
 * using the mathematical function: E(x) = (ax + b) mod m.
 */
public class AffineCipher extends Cryptosystem {

    /**
     * Constructs an AffineCipher using specified plaintext and ciphertext alphabets.
     *
     * @param plaintextAlphabet  The alphabet used for plaintext.
     * @param ciphertextAlphabet The alphabet used for ciphertext.
     * @throws IllegalArgumentException If the sizes of the plaintext and ciphertext alphabets do not match.
     */
    public AffineCipher(Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet,
                        CaseHandling caseHandling, UnknownCharHandling unknownCharHandling, WhitespaceHandling whitespaceHandling) {
        super(plaintextAlphabet, ciphertextAlphabet, caseHandling, unknownCharHandling, whitespaceHandling);

        if (plaintextAlphabet.size() != ciphertextAlphabet.size()) {
            throw new IllegalArgumentException("Plaintext and ciphertext alphabets must have the same size.");
        }
    }

    /**
     * Encrypts the given plaintext using the Affine Cipher formula: E(x) = (ax + b) mod m.
     *
     * @param plaintext The text to be encrypted.
     * @param key       The key used for encryption. Must be an instance of AffineKey.
     * @return The encrypted ciphertext.
     * @throws IllegalArgumentException If the provided key is not an instance of AffineKey.
     */
    @Override
    public String encryptMethod(String plaintext, Key key) {
        if (!(key instanceof AffineKey affineKey)) {
            throw new IllegalArgumentException("Invalid key type. AffineCipher requires an AffineKey.");
        }

        int a = affineKey.getA();
        int b = affineKey.getB();
        int alphabetSize = affineKey.getKeyAlphabet().size();

        StringBuilder encrypted = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (whitespaceHandling == WhitespaceHandling.PRESERVE) {
                    encrypted.append(c);
                }
                continue;
            }
            int index = plaintextAlphabet.getIndex(c);
            int encryptedIndex = (a * index + b) % alphabetSize;
            encrypted.append(ciphertextAlphabet.getChar(encryptedIndex));
        }
        return encrypted.toString();
    }

    /**
     * Decrypts the given ciphertext using the Affine Cipher formula: D(x) = a⁻¹(x - b) mod m.
     *
     * @param ciphertext The text to be decrypted.
     * @param key        The key used for decryption. Must be an instance of AffineKey.
     * @return The decrypted plaintext.
     * @throws IllegalArgumentException If the provided key is not an instance of AffineKey.
     */
    @Override
    public String decryptMethod(String ciphertext, Key key) {
        if (!(key instanceof AffineKey affineKey)) {
            throw new IllegalArgumentException("Invalid key type. AffineCipher requires an AffineKey.");
        }

        int a = affineKey.getA();
        int b = affineKey.getB();
        int alphabetSize = affineKey.getKeyAlphabet().size();
        int modInverse = findModInverse(a, alphabetSize);

        StringBuilder decrypted = new StringBuilder();
        for (char c : ciphertext.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (whitespaceHandling == WhitespaceHandling.PRESERVE) {
                    decrypted.append(c);
                }
                continue;
            }
            int index = ciphertextAlphabet.getIndex(c);
            int decryptedIndex = (modInverse * (index - b + alphabetSize)) % alphabetSize;
            decrypted.append(plaintextAlphabet.getChar(decryptedIndex));
        }
        return decrypted.toString();
    }

    /**
     * Finds the modular inverse of 'a' modulo 'm' using brute force.
     *
     * @param a The number to find the inverse for.
     * @param m The modulus.
     * @return The modular inverse of 'a' modulo 'm'.
     * @throws IllegalArgumentException If no modular inverse exists.
     */
    private int findModInverse(int a, int m) {
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        throw new IllegalArgumentException("No modular inverse exists for the given values.");
    }
}