package com.iluncrypt.iluncryptapp.models;

public abstract class Cryptosystem {
    protected Alphabet alphabet;
    protected Key key;

    public Cryptosystem(Alphabet alphabet, Key key) {
        this.alphabet = alphabet;
        this.key = key;
    }

    public abstract Ciphertext encrypt(Plaintext plaintext);

    public abstract Plaintext decrypt(Ciphertext ciphertext);

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
