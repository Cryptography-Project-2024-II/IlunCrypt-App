package com.iluncrypt.iluncryptapp.models.attacks;

import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.algorithms.classic.AffineCipher;
import com.iluncrypt.iluncryptapp.models.algorithms.classic.PermutationCipher;
// Supón que existen clases ShiftCipher, VigenereCipher, SubstitutionCipher y HillCipher
// o que implementas funciones similares.
import com.iluncrypt.iluncryptapp.models.keys.AffineKey;
import com.iluncrypt.iluncryptapp.models.keys.PermutationKey;
import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import com.iluncrypt.iluncryptapp.models.enums.WhitespaceHandling;

import java.util.*;

public class BruteForceAttacker {

    public enum Language {
        ENGLISH, SPANISH, FRENCH, PORTUGUESE;
    }

    public static class Candidate implements Comparable<Candidate> {
        public final String plaintext;
        public final double score; // score mayor implica mayor probabilidad

        public Candidate(String plaintext, double score) {
            this.plaintext = plaintext;
            this.score = score;
        }

        @Override
        public int compareTo(Candidate o) {
            // Ordenamos de mayor a menor probabilidad (score mayor es mejor)
            return Double.compare(o.score, this.score);
        }
    }

    // Para efectos del análisis, usamos frecuencias aproximadas para A-Z.
    private static final Map<Language, double[]> languageFrequencies = new HashMap<>();

    static {
        // Frecuencias en porcentaje para el alfabeto inglés (A-Z)
        languageFrequencies.put(Language.ENGLISH, new double[]{
                8.2, 1.5, 2.8, 4.3, 13.0, 2.2, 2.0, 6.1, 7.0, 0.15, 0.77, 4.0, 2.4,
                6.7, 7.5, 1.9, 0.095, 6.0, 6.3, 9.1, 2.8, 0.98, 2.4, 0.15, 2.0, 0.074
        });
        // Frecuencias para español
        languageFrequencies.put(Language.SPANISH, new double[]{
                12.53, 1.42, 4.68, 5.86, 13.68, 0.69, 1.01, 0.70, 6.25, 0.44, 0.01, 4.97,
                3.15, 6.71, 8.68, 2.51, 0.88, 6.87, 7.98, 4.63, 3.93, 0.90, 0.02, 0.22, 0.90, 0.52
        });
        // Frecuencias para francés
        languageFrequencies.put(Language.FRENCH, new double[]{
                7.636, 0.901, 3.260, 3.669, 14.715, 1.066, 0.866, 0.737, 7.529, 0.613,
                0.049, 5.456, 2.968, 7.095, 5.796, 2.521, 1.362, 6.693, 7.948, 7.244,
                6.311, 1.838, 0.049, 0.427, 0.128, 0.326
        });
        // Frecuencias para portugués
        languageFrequencies.put(Language.PORTUGUESE, new double[]{
                14.63, 1.04, 3.88, 4.99, 12.57, 1.02, 1.30, 1.28, 6.18, 0.40,
                0.02, 4.74, 2.97, 5.05, 10.73, 2.52, 1.20, 6.53, 7.81, 4.34,
                4.63, 1.67, 0.01, 0.21, 0.01, 0.47
        });
    }

    // Función de scoring basada en chi-cuadrado (mientras menor sea chi-cuadrado, mejor la concordancia)
    // Se invierte el valor para que un score mayor indique mayor probabilidad.
    private double scorePlaintext(String plaintext, Language language) {
        String text = plaintext.toUpperCase().replaceAll("[^A-Z]", "");
        if (text.length() == 0) return Double.MIN_VALUE;
        double[] frequencies = languageFrequencies.get(language);
        double[] observed = new double[26];
        for (char c : text.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                observed[c - 'A']++;
            }
        }
        double chiSquare = 0.0;
        double total = text.length();
        for (int i = 0; i < 26; i++) {
            double expected = total * (frequencies[i] / 100.0);
            double diff = observed[i] - expected;
            chiSquare += (diff * diff) / (expected + 1e-6);
        }
        // Invertir: score = 1/(chiSquare+1) para que a menor chi, mayor score.
        return 1.0 / (chiSquare + 1);
    }

    // -------------------- FUNCIONES DE GENERACIÓN DE CLAVE Y BLOCK SIZE --------------------

    /**
     * Genera el tamaño del bloque (n) en función de la longitud de la entrada y una cota mínima.
     * Si la longitud es menor que la cota, retorna la longitud; de lo contrario, elige un tamaño
     * aleatorio entre la cota mínima y (cota mínima + inputLength/4) sin exceder la longitud total.
     */
    private int generateBlockSize(int inputLength, int minBlockSize) {
        if (inputLength < minBlockSize) {
            return inputLength;
        }
        int maxBlockSize = Math.min(inputLength, minBlockSize + inputLength / 4);
        Random random = new Random();
        return random.nextInt(maxBlockSize - minBlockSize + 1) + minBlockSize;
    }

    /**
     * Genera una clave de permutación aleatoria para un bloque de tamaño blockSize.
     * Por ejemplo, para blockSize = 3, puede generar "231" o "312", etc.
     */
    private String generateRandomPermutationKey(int blockSize) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= blockSize; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        StringBuilder sb = new StringBuilder();
        for (Integer num : numbers) {
            sb.append(num);
        }
        return sb.toString();
    }

    // -------------------- ATAQUES POR FUERZA BRUTA --------------------

    /**
     * Ataque por fuerza bruta al cifrado SHIFT.
     * Se asume que la clave es un número entre 0 y (alfabeto.size()-1).
     */
    public List<Candidate> attackShift(String cipherText, Alphabet alphabet, Language language) {
        List<Candidate> candidates = new ArrayList<>();
        int modulus = alphabet.size();
        for (int key = 0; key < modulus; key++) {
            try {
                String candidatePlain = shiftDecrypt(cipherText, key, alphabet);
                double score = scorePlaintext(candidatePlain, language);
                candidates.add(new Candidate(candidatePlain, score));
            } catch (Exception e) {
                // Ignorar claves inválidas
            }
        }
        Collections.sort(candidates);
        return candidates;
    }

    /**
     * Ataque por fuerza bruta al cifrado AFFINE.
     * Se exploran todas las parejas (a,b) válidas donde a es coprimo con el tamaño del alfabeto.
     */
    public List<Candidate> attackAffine(String cipherText, Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet, Language language) {
        List<Candidate> candidates = new ArrayList<>();
        int modulus = plaintextAlphabet.size();
        for (int a = 0; a < modulus; a++) {
            if (gcd(a, modulus) != 1) continue;
            for (int b = 0; b < modulus; b++) {
                try {
                    AffineKey key = new AffineKey(a, b, Alphabet.generateZAlphabet(modulus));
                    AffineCipher affineCipher = new AffineCipher(
                            plaintextAlphabet,
                            ciphertextAlphabet,
                            CaseHandling.IGNORE,
                            UnknownCharHandling.REMOVE,
                            WhitespaceHandling.PRESERVE
                    );
                    String candidatePlain = affineCipher.decrypt(cipherText, key);
                    double score = scorePlaintext(candidatePlain, language);
                    candidates.add(new Candidate(candidatePlain, score));
                } catch (Exception e) {
                    // Clave inválida, omitir
                }
            }
        }
        Collections.sort(candidates);
        return candidates;
    }

    /**
     * Ataque por fuerza bruta al cifrado MULTIPLICATIVE (solo la parte multiplicativa de Affine, b = 0).
     */
    public List<Candidate> attackMultiplicative(String cipherText, Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet, Language language) {
        List<Candidate> candidates = new ArrayList<>();
        int modulus = plaintextAlphabet.size();
        for (int a = 0; a < modulus; a++) {
            if (gcd(a, modulus) != 1) continue;
            try {
                AffineKey key = new AffineKey(a, 0, Alphabet.generateZAlphabet(modulus));
                AffineCipher multiplicativeCipher = new AffineCipher(
                        plaintextAlphabet,
                        ciphertextAlphabet,
                        CaseHandling.IGNORE,
                        UnknownCharHandling.REMOVE,
                        WhitespaceHandling.PRESERVE
                );
                String candidatePlain = multiplicativeCipher.decrypt(cipherText, key);
                double score = scorePlaintext(candidatePlain, language);
                candidates.add(new Candidate(candidatePlain, score));
            } catch (Exception e) {
                // Omitir errores
            }
        }
        Collections.sort(candidates);
        return candidates;
    }

    /**
     * Ataque por fuerza bruta al cifrado PERMUTATION para un blockSize dado.
     * Se generan todas las permutaciones posibles y se prueba cada una.
     * Nota: Solo es viable para blockSize pequeños (por ejemplo, 3 o 4).
     */
    public List<Candidate> attackPermutation(String cipherText, Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet, Language language, int blockSize) {
        List<Candidate> candidates = new ArrayList<>();
        List<int[]> perms = generateAllPermutations(blockSize);
        for (int[] perm : perms) {
            try {
                StringBuilder keyStrBuilder = new StringBuilder();
                for (int num : perm) {
                    keyStrBuilder.append(num);
                }
                String keyStr = keyStrBuilder.toString();
                PermutationKey key = new PermutationKey(keyStr, Alphabet.generateZAlphabet(blockSize));
                PermutationCipher permutationCipher = new PermutationCipher(
                        plaintextAlphabet,
                        ciphertextAlphabet,
                        CaseHandling.IGNORE,
                        UnknownCharHandling.REMOVE,
                        WhitespaceHandling.PRESERVE
                );
                String candidatePlain = permutationCipher.decrypt(cipherText, key);
                double score = scorePlaintext(candidatePlain, language);
                candidates.add(new Candidate(candidatePlain, score));
            } catch (Exception e) {
                // Ignorar errores
            }
        }
        Collections.sort(candidates);
        return candidates;
    }

    /**
     * Ataque al cifrado VIGENERE.
     * Debido a la complejidad de un ataque completo, este método es un stub a implementar con
     * técnicas heurísticas (por ejemplo, análisis de índice de coincidencia y hill-climbing).
     */
    public List<Candidate> attackVigenere(String cipherText, Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet, Language language, int maxKeyLength) {
        List<Candidate> candidates = new ArrayList<>();
        // TODO: Implementar ataque heurístico a Vigenere
        return candidates;
    }

    /**
     * Ataque al cifrado SUBSTITUTION.
     * TODO: hill-climbing o basado en diccionarios.
     */
    public List<Candidate> attackSubstitution(String cipherText, Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet, Language language) {
        List<Candidate> candidates = new ArrayList<>();
        // TODO: Implementar ataque para cifrado por sustitución
        return candidates;
    }

    /**
     * Ataque al cifrado HILL.
     * Solo es viable para matrices pequeñas (por ejemplo, 2x2) y se debe explorar
     * el espacio de claves invertibles modulo el tamaño del alfabeto.
     */
    public List<Candidate> attackHill(String cipherText, Alphabet plaintextAlphabet, Alphabet ciphertextAlphabet, Language language, int matrixSize) {
        List<Candidate> candidates = new ArrayList<>();
        // TODO: Implementar ataque por fuerza bruta para cifrado Hill
        return candidates;
    }

    // -------------------- MÉTODOS AUXILIARES --------------------
    private List<int[]> generateAllPermutations(int n) {
        List<int[]> results = new ArrayList<>();
        permuteHelper(n, new int[n], new boolean[n + 1], 0, results);
        return results;
    }

    private void permuteHelper(int n, int[] current, boolean[] used, int index, List<int[]> results) {
        if (index == n) {
            results.add(Arrays.copyOf(current, n));
            return;
        }
        for (int i = 1; i <= n; i++) {
            if (!used[i]) {
                used[i] = true;
                current[index] = i;
                permuteHelper(n, current, used, index + 1, results);
                used[i] = false;
            }
        }
    }


    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private String shiftDecrypt(String cipherText, int shiftKey, Alphabet alphabet) {
        StringBuilder sb = new StringBuilder();
        int size = alphabet.size();
        for (char c : cipherText.toCharArray()) {
            int index = alphabet.getIndex(c);
            int newIndex = (index - shiftKey + size) % size;
            sb.append(alphabet.getChar(newIndex));
        }
        return sb.toString();
    }
}
