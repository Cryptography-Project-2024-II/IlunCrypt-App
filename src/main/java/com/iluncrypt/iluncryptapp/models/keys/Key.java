package com.iluncrypt.iluncryptapp.models.keys;

import com.iluncrypt.iluncryptapp.models.Alphabet;

/**
 * Abstract class representing a cryptographic key.
 * Each key is associated with a specific alphabet.
 */
public abstract class Key {
    protected Alphabet keyAlphabet;

    /**
     * Constructs a key associated with a specific alphabet.
     *
     * @param keyAlphabet The alphabet used for this key.
     */
    public Key(Alphabet keyAlphabet) {
        this.keyAlphabet = keyAlphabet;
    }

    /**
     * Gets the alphabet associated with this key.
     *
     * @return The key's alphabet.
     */
    public Alphabet getKeyAlphabet() {
        return keyAlphabet;
    }

    /**
     * Validates whether the key follows the necessary constraints.
     * This must be implemented by subclasses.
     */
    public abstract void validate();
}
