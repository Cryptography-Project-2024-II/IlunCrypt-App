package com.iluncrypt.iluncryptapp.models.algorithms;

import com.iluncrypt.iluncryptapp.models.*;

public class VigenereCipher extends Cryptosystem {

    public VigenereCipher(Alphabet alphabet, Key key) {
        super(alphabet, key);
    }

    @Override
    public Ciphertext encrypt(Plaintext plaintext) {
        String text = plaintext.getText();
        String keyText = key.getValue();
        StringBuilder encrypted = new StringBuilder();

        int keyIndex = 0;
        for (char c : text.toCharArray()) {
            if (alphabet.isValidChar(c)) {
                int charIndex = alphabet.getCharacters().stream().toList().indexOf(c);
                int keyCharIndex = alphabet.getCharacters().stream().toList().indexOf(keyText.charAt(keyIndex));
                int encryptedIndex = (charIndex + keyCharIndex) % alphabet.getCharacters().size();
                encrypted.append(alphabet.getCharacters().toArray()[encryptedIndex]);
                keyIndex = (keyIndex + 1) % keyText.length();
            } else {
                encrypted.append(c);
            }
        }

        return new Ciphertext(encrypted.toString());
    }

    @Override
    public Plaintext decrypt(Ciphertext ciphertext) {
        String text = ciphertext.getText();
        String keyText = key.getValue();
        StringBuilder decrypted = new StringBuilder();

        int keyIndex = 0;
        for (char c : text.toCharArray()) {
            if (alphabet.isValidChar(c)) {
                int charIndex = alphabet.getCharacters().stream().toList().indexOf(c);
                int keyCharIndex = alphabet.getCharacters().stream().toList().indexOf(keyText.charAt(keyIndex));
                int decryptedIndex = (charIndex - keyCharIndex + alphabet.getCharacters().size()) % alphabet.getCharacters().size();
                decrypted.append(alphabet.getCharacters().toArray()[decryptedIndex]);
                keyIndex = (keyIndex + 1) % keyText.length();
            } else {
                decrypted.append(c);
            }
        }

        return new Plaintext(decrypted.toString());
    }
}
