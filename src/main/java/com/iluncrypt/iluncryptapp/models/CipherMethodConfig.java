package com.iluncrypt.iluncryptapp.models;

import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;

/**
 * Represents the configuration settings for a specific cipher method.
 */
public class CipherMethodConfig {
    private final Alphabet plaintextAlphabet;
    private final Alphabet ciphertextAlphabet;
    private final Alphabet keyAlphabet;
    private final CaseHandling caseHandling;
    private final UnknownCharHandling unknownCharHandling;

    /**
     * Constructor for CipherMethodConfig.
     *
     * @param plaintextAlphabet The alphabet used for plaintext.
     * @param ciphertextAlphabet The alphabet used for ciphertext.
     * @param keyAlphabet The alphabet used for keys.
     * @param caseHandling How case is handled (e.g., IGNORE, STRICT).
     * @param unknownCharHandling How unknown characters are handled (e.g., REMOVE, IGNORE).
     */
    public CipherMethodConfig(Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet,
                              Alphabet keyAlphabet, CaseHandling caseHandling,
                              UnknownCharHandling unknownCharHandling) {
        this.plaintextAlphabet = plaintextAlphabet;
        this.ciphertextAlphabet = ciphertextAlphabet;
        this.keyAlphabet = keyAlphabet;
        this.caseHandling = caseHandling;
        this.unknownCharHandling = unknownCharHandling;
    }

    public Alphabet getPlaintextAlphabet() {
        return plaintextAlphabet;
    }

    public Alphabet getCiphertextAlphabet() {
        return ciphertextAlphabet;
    }

    public Alphabet getKeyAlphabet() {
        return keyAlphabet;
    }

    public CaseHandling getCaseHandling() {
        return caseHandling;
    }

    public UnknownCharHandling getUnknownCharHandling() {
        return unknownCharHandling;
    }

    @Override
    public String toString() {
        return "CipherMethodConfig{" +
                "plaintextAlphabet=" + plaintextAlphabet.getName() +
                ", ciphertextAlphabet=" + ciphertextAlphabet.getName() +
                ", keyAlphabet=" + keyAlphabet.getName() +
                ", caseHandling=" + caseHandling +
                ", unknownCharHandling=" + unknownCharHandling +
                '}';
    }
}
