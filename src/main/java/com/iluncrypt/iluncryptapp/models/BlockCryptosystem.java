package com.iluncrypt.iluncryptapp.models;

public abstract class BlockCryptosystem extends Cryptosystem {

    public BlockCryptosystem(Alphabet alphabet, Key key) {
        super(alphabet, key);
    }

    public abstract List<Ciphertext> encryptBlocks(List<Plaintext> blocks);

    public abstract List<Plaintext> decryptBlocks(List<Ciphertext> blocks);
}