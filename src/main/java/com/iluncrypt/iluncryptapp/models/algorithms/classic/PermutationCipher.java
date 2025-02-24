package com.iluncrypt.iluncryptapp.models.algorithms.classic;

import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.Cryptosystem;
import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import com.iluncrypt.iluncryptapp.models.enums.WhitespaceHandling;
import com.iluncrypt.iluncryptapp.models.keys.Key;
import com.iluncrypt.iluncryptapp.models.keys.PermutationKey;

import java.util.List;
import java.util.Random;

/**
 * Implementa el cifrado y descifrado por permutación.
 * Se divide el mensaje en bloques de tamaño igual al de la permutación.
 * Si el mensaje no es múltiplo del tamaño de bloque, se completa con un mismo carácter aleatorio del alfabeto.
 */
public class PermutationCipher extends Cryptosystem {

    public PermutationCipher(Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet,
                             CaseHandling caseHandling, UnknownCharHandling unknownCharHandling, WhitespaceHandling whitespaceHandling) {
        super(plaintextAlphabet, ciphertextAlphabet, caseHandling, unknownCharHandling, whitespaceHandling);
        if (plaintextAlphabet.size() != ciphertextAlphabet.size()) {
            throw new IllegalArgumentException("Los alfabetos de texto plano y cifrado deben tener el mismo tamaño.");
        }
    }

    @Override
    protected String encryptMethod(String plaintext, Key key) {
        if (!(key instanceof PermutationKey)) {
            throw new IllegalArgumentException("Clave inválida. PermutationCipher requiere una PermutationKey.");
        }
        PermutationKey permutationKey = (PermutationKey) key;
        int[] permutation = permutationKey.getPermutation();
        int blockSize = permutation.length;

        // Si el largo del texto no es múltiplo de blockSize, se rellena con un único carácter aleatorio.
        int remainder = plaintext.length() % blockSize;
        if (remainder != 0) {
            int fillCount = blockSize - remainder;
            List<Character> characters = plaintextAlphabet.getCharacters();
            Random random = new Random();
            char filler = characters.get(random.nextInt(characters.size()));
            StringBuilder fillerBuilder = new StringBuilder();
            for (int i = 0; i < fillCount; i++) {
                fillerBuilder.append(filler);
            }
            plaintext += fillerBuilder.toString();
        }

        StringBuilder encrypted = new StringBuilder();
        // Procesa el texto en bloques
        for (int i = 0; i < plaintext.length(); i += blockSize) {
            char[] block = new char[blockSize];
            for (int j = 0; j < blockSize; j++) {
                block[j] = plaintext.charAt(i + j);
            }
            // Aplica la permutación:
            // Para cada posición k en el bloque cifrado, se coloca el carácter que estaba en la posición (permutation[k] - 1) del bloque original.
            char[] permutedBlock = new char[blockSize];
            for (int k = 0; k < blockSize; k++) {
                int index = permutation[k] - 1;
                permutedBlock[k] = block[index];
            }
            encrypted.append(permutedBlock);
        }
        return encrypted.toString();
    }

    @Override
    protected String decryptMethod(String ciphertext, Key key) {
        if (!(key instanceof PermutationKey)) {
            throw new IllegalArgumentException("Clave inválida. PermutationCipher requiere una PermutationKey.");
        }
        PermutationKey permutationKey = (PermutationKey) key;
        int[] permutation = permutationKey.getPermutation();
        int blockSize = permutation.length;

        if (ciphertext.length() % blockSize != 0) {
            throw new IllegalArgumentException("El largo del texto cifrado debe ser múltiplo del tamaño de bloque.");
        }

        // Calcula la permutación inversa.
        int[] inverse = new int[blockSize];
        for (int i = 0; i < blockSize; i++) {
            // La posición original correspondiente al dígito permutation[i] es (permutation[i] - 1)
            inverse[permutation[i] - 1] = i;
        }

        StringBuilder decrypted = new StringBuilder();
        // Procesa bloque a bloque
        for (int i = 0; i < ciphertext.length(); i += blockSize) {
            char[] block = new char[blockSize];
            for (int j = 0; j < blockSize; j++) {
                block[j] = ciphertext.charAt(i + j);
            }
            char[] originalBlock = new char[blockSize];
            for (int j = 0; j < blockSize; j++) {
                // El carácter que estaba originalmente en la posición j se encuentra en block[inverse[j]].
                originalBlock[j] = block[inverse[j]];
            }
            decrypted.append(originalBlock);
        }
        return decrypted.toString();
    }
}
