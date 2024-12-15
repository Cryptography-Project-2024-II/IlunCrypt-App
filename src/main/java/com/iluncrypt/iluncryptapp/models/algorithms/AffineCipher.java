package com.iluncrypt.iluncryptapp.models.algorithms;

import com.iluncrypt.iluncryptapp.models.*;

public class AffineCipher extends Cryptosystem {

    public AffineCipher(Alphabet alphabet, Key key) {
        super(alphabet, key);
    }

    @Override
    public Ciphertext encrypt(Plaintext plaintext) {
        String text = plaintext.getText();
        StringBuilder encrypted = new StringBuilder();

        int a = Integer.parseInt(key.getValue().split(",")[0]);
        int b = Integer.parseInt(key.getValue().split(",")[1]);

        for (char c : text.toCharArray()) {
            if (alphabet.isValidChar(c)) {
                int charIndex = alphabet.getCharacters().stream().toList().indexOf(c);
                int encryptedIndex = (a * charIndex + b) % alphabet.getCharacters().size();
                encrypted.append(alphabet.getCharacters().toArray()[encryptedIndex]);
            } else {
                encrypted.append(c);
            }
        }

        return new Ciphertext(encrypted.toString());
    }

    @Override
    public Plaintext decrypt(Ciphertext ciphertext) {
        String text = ciphertext.getText();
        StringBuilder decrypted = new StringBuilder();

        int a = Integer.parseInt(key.getValue().split(",")[0]);
        int b = Integer.parseInt(key.getValue().split(",")[1]);
        int modInverse = findModInverse(a, alphabet.getCharacters().size());

        for (char c : text.toCharArray()) {
            if (alphabet.isValidChar(c)) {
                int charIndex = alphabet.getCharacters().stream().toList().indexOf(c);
                int decryptedIndex = (modInverse * (charIndex - b + alphabet.getCharacters().size())) % alphabet.getCharacters().size();
                decrypted.append(alphabet.getCharacters().toArray()[decryptedIndex]);
            } else {
                decrypted.append(c);
            }
        }

        return new Plaintext(decrypted.toString());
    }

    private int findModInverse(int a, int m) {
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        throw new IllegalArgumentException("No modular inverse exists for the given values.");
    }
}

