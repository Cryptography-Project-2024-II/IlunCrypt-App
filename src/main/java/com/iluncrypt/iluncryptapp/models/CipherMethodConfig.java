package com.iluncrypt.iluncryptapp.models;

import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import com.iluncrypt.iluncryptapp.models.enums.WhitespaceHandling;

/**
 * Represents the configuration settings for a specific cipher method.
 */
public class CipherMethodConfig {
    private Alphabet plaintextAlphabet;
    private Alphabet ciphertextAlphabet;
    private Alphabet keyAlphabet;
    private CaseHandling caseHandling;
    private UnknownCharHandling unknownCharHandling;
    private WhitespaceHandling whitespaceHandling;

    /**
     * Constructor for CipherMethodConfig.
     *
     * @param plaintextAlphabet The alphabet used for plaintext.
     * @param ciphertextAlphabet The alphabet used for ciphertext.
     * @param keyAlphabet The alphabet used for keys.
     * @param caseHandling How case is handled (e.g., IGNORE, STRICT).
     * @param unknownCharHandling How unknown characters are handled (e.g., REMOVE, IGNORE).
     * @param whitespaceHandling How whitespace is handled (e.g., PRESERVE, REMOVE).
     */
    public CipherMethodConfig(Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet,
                              Alphabet keyAlphabet, CaseHandling caseHandling,
                              UnknownCharHandling unknownCharHandling,
                              WhitespaceHandling whitespaceHandling) {
        this.plaintextAlphabet = plaintextAlphabet;
        this.ciphertextAlphabet = ciphertextAlphabet;
        this.keyAlphabet = keyAlphabet;
        this.caseHandling = caseHandling;
        this.unknownCharHandling = unknownCharHandling;
        this.whitespaceHandling = whitespaceHandling;
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

    public WhitespaceHandling getWhitespaceHandling() {
        return whitespaceHandling;
    }

    public void setPlaintextAlphabet(Alphabet plaintextAlphabet) {
        this.plaintextAlphabet = plaintextAlphabet;
    }

    public void setCiphertextAlphabet(Alphabet ciphertextAlphabet) {
        this.ciphertextAlphabet = ciphertextAlphabet;
    }

    public void setKeyAlphabet(Alphabet keyAlphabet) {
        this.keyAlphabet = keyAlphabet;
    }

    public void setCaseHandling(CaseHandling caseHandling) {
        this.caseHandling = caseHandling;
    }

    public void setUnknownCharHandling(UnknownCharHandling unknownCharHandling) {
        this.unknownCharHandling = unknownCharHandling;
    }

    public void setWhitespaceHandling(WhitespaceHandling whitespaceHandling) {
        this.whitespaceHandling = whitespaceHandling;
    }

    @Override
    public String toString() {
        return "CipherMethodConfig{" +
                "plaintextAlphabet=" + plaintextAlphabet.getName() +
                ", ciphertextAlphabet=" + ciphertextAlphabet.getName() +
                ", keyAlphabet=" + keyAlphabet.getName() +
                ", caseHandling=" + caseHandling +
                ", unknownCharHandling=" + unknownCharHandling +
                ", whitespaceHandling=" + whitespaceHandling +
                '}';
    }
}
