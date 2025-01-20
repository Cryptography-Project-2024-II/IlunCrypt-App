package com.iluncrypt.iluncryptapp.models;

import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import com.iluncrypt.iluncryptapp.models.keys.Key;

/**
 * Abstract class representing a generic cryptographic system.
 * Provides common configuration settings for handling uppercase letters and unknown characters.
 */
public abstract class Cryptosystem {
    protected Alphabet plaintextAlphabet;
    protected Alphabet ciphertextAlphabet;
    protected CaseHandling caseHandling;
    protected UnknownCharHandling unknownCharHandling;

    /**
     * Constructs a Cryptosystem with specified alphabets and handling configurations.
     *
     * @param plaintextAlphabet   The alphabet used for plaintext.
     * @param ciphertextAlphabet  The alphabet used for ciphertext.
     * @param caseHandling        The case handling strategy.
     * @param unknownCharHandling The unknown character handling strategy.
     */
    public Cryptosystem(Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet,
                        CaseHandling caseHandling, UnknownCharHandling unknownCharHandling) {
        this.plaintextAlphabet = plaintextAlphabet;
        this.ciphertextAlphabet = ciphertextAlphabet;
        this.caseHandling = caseHandling;
        this.unknownCharHandling = unknownCharHandling;
    }

    /**
     * Encrypts a given plaintext message after normalizing it.
     *
     * @param plaintext The text to encrypt.
     * @param key       The encryption key.
     * @return The encrypted text.
     */
    public String encrypt(String plaintext, Key key) {
        return encryptMethod(normalizeText(plaintext, plaintextAlphabet), key);
    }

    /**
     * Decrypts a given ciphertext message after normalizing it.
     *
     * @param ciphertext The text to decrypt.
     * @param key        The decryption key.
     * @return The decrypted text.
     */
    public String decrypt(String ciphertext, Key key) {
        return decryptMethod(normalizeText(ciphertext, ciphertextAlphabet), key);
    }

    /**
     * The actual encryption method to be implemented by subclasses.
     *
     * @param plaintext The normalized text to encrypt.
     * @param key       The encryption key.
     * @return The encrypted text.
     */
    protected abstract String encryptMethod(String plaintext, Key key);

    /**
     * The actual decryption method to be implemented by subclasses.
     *
     * @param ciphertext The normalized text to decrypt.
     * @param key        The decryption key.
     * @return The decrypted text.
     */
    protected abstract String decryptMethod(String ciphertext, Key key);

    /**
     * Normalizes the input text based on case handling and unknown character handling settings.
     *
     * @param text      The input text.
     * @param alphabet  The reference alphabet for validation.
     * @return The normalized text.
     */

    /**
     * Normalizes the input text based on case handling and unknown character handling settings.
     *
     * @param text      The input text.
     * @param alphabet  The reference alphabet for validation.
     * @return The normalized text.
     */
    protected String normalizeText(String text, Alphabet alphabet) {
        StringBuilder normalized = new StringBuilder();

        for (char c : text.toCharArray()) {
            char processedChar = c;

            // Apply case handling rules
            switch (caseHandling) {
                case IGNORE:
                    processedChar = Character.toLowerCase(c);
                    break;
                case PRESERVE:
                    processedChar = (alphabet.isValidChar(Character.toLowerCase(c))) ? c : Character.toLowerCase(c);
                    break;
                case STRICT:
                    // No changes needed; keep original case
                    break;
            }

            // Apply unknown character handling rules
            if (alphabet.isValidChar(processedChar)) {
                normalized.append(processedChar);
            } else {
                switch (unknownCharHandling) {
                    case IGNORE:
                        normalized.append(c); // Keep as is
                        break;
                    case REMOVE:
                        // Do not append (skip the character)
                        break;
                    case REPLACE:
                        normalized.append('?'); // Replace with placeholder
                        break;
                }
            }
        }
        return normalized.toString();
    }
}
