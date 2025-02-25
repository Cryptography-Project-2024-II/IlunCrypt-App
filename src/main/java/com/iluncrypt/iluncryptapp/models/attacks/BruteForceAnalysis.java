package com.iluncrypt.iluncryptapp.models.attacks;

import com.iluncrypt.iluncryptapp.models.enums.Language;
import com.iluncrypt.iluncryptapp.models.enums.EnglishLetterFrequencyZ26;
import com.iluncrypt.iluncryptapp.models.enums.FrenchLetterFrequencyZ26;
import com.iluncrypt.iluncryptapp.models.enums.SpanishLetterFrequencyZ26;
import com.iluncrypt.iluncryptapp.models.enums.PortugueseLetterFrequencyZ26;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class performs brute force analysis for simple substitution ciphers:
 * Shift cipher and Multiplicative cipher. For each candidate decryption, it calculates
 * a language matching score based on the chi-square test comparing observed letter frequencies
 * with expected frequencies for the given language. It returns a list of Candidate objects,
 * ordered from best (highest score) to worst.
 */
public class BruteForceAnalysis {

    /**
     * Performs a brute force attack on the given cipher text using the specified cipher type
     * (either "Shift" or "Multiplicative") and evaluates each candidate using letter frequency analysis.
     *
     * @param cipherText The encrypted text.
     * @param cipherType "Shift" or "Multiplicative" (case-insensitive).
     * @param language   The target language (enum Language) to evaluate the decryption.
     * @return A list of Candidate objects sorted from highest to lowest probability.
     */
    public static List<Candidate> bruteForce(String cipherText, String cipherType, Language language) {
        Map<Character, Double> expectedFrequencies = getExpectedFrequencies(language);
        List<Candidate> candidates = new ArrayList<>();

        if (cipherType.equalsIgnoreCase("Shift")) {
            for (int key = 1; key < 26; key++) {
                String decryptedText = shiftDecrypt(cipherText, key);
                double score = computeLanguageScore(decryptedText, expectedFrequencies);
                candidates.add(new Candidate(String.valueOf(key), score, decryptedText));
            }
        } else if (cipherType.equalsIgnoreCase("Multiplicative")) {
            for (int key = 1; key < 26; key++) {
                if (gcd(key, 26) == 1) { // Only keys coprime with 26 are valid
                    String decryptedText = multiplicativeDecrypt(cipherText, key);
                    double score = computeLanguageScore(decryptedText, expectedFrequencies);
                    candidates.add(new Candidate(String.valueOf(key), score, decryptedText));
                }
            }
        }

        // Sort candidates in descending order (higher score means better match)
        candidates.sort(Comparator.comparingDouble(Candidate::getProbability).reversed());

        if (!candidates.isEmpty()) {
            Candidate best = candidates.get(0);
            System.out.println("Best key: " + best.getKey() + " with probability: " + best.getProbability());
        }
        return candidates;
    }

    // ------------------ Helper Methods ------------------

    /**
     * Shift (Caesar) decryption.
     */
    public static String shiftDecrypt(String cipherText, int key) {
        StringBuilder plainText = new StringBuilder();
        for (char c : cipherText.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                plainText.append((char) ((c - base - key + 26) % 26 + base));
            } else {
                plainText.append(c);
            }
        }
        return plainText.toString();
    }

    /**
     * Multiplicative decryption.
     */
    public static String multiplicativeDecrypt(String cipherText, int key) {
        StringBuilder plainText = new StringBuilder();
        int keyInverse = modInverse(key, 26);
        for (char c : cipherText.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                int decrypted = (keyInverse * (c - base)) % 26;
                plainText.append((char) (decrypted + base));
            } else {
                plainText.append(c);
            }
        }
        return plainText.toString();
    }

    /**
     * Computes the modular inverse of a modulo m.
     */
    public static int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1;
    }

    /**
     * Computes the greatest common divisor.
     */
    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    /**
     * Computes a language matching score for the given text. The score is defined as
     * 1/(1 + chiSquare), where chiSquare is computed from the observed letter frequencies.
     *
     * @param text                The text to evaluate.
     * @param expectedFrequencies A map from letter (A-Z) to expected frequency (in percentage).
     * @return A score between 0 and 1; higher is better.
     */
    private static double computeLanguageScore(String text, Map<Character, Double> expectedFrequencies) {
        String filtered = text.toUpperCase().replaceAll("[^A-Z]", "");
        if (filtered.isEmpty()) {
            return 0.0;
        }
        int n = filtered.length();
        Map<Character, Integer> counts = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            counts.put(c, 0);
        }
        for (char c : filtered.toCharArray()) {
            counts.put(c, counts.get(c) + 1);
        }
        Map<Character, Double> observed = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            observed.put(c, counts.get(c) * 100.0 / n);
        }
        double chiSquare = 0.0;
        for (char c = 'A'; c <= 'Z'; c++) {
            double expected = expectedFrequencies.getOrDefault(c, 0.0);
            if (expected > 0) {
                double obs = observed.get(c);
                chiSquare += Math.pow(obs - expected, 2) / expected;
            }
        }
        return 1.0 / (1.0 + chiSquare);
    }

    /**
     * Returns a map with expected letter frequencies (in percentages) for the given language.
     *
     * @param language The language.
     * @return Map of letter (A-Z) to expected frequency.
     */
    private static Map<Character, Double> getExpectedFrequencies(Language language) {
        Map<Character, Double> map = new HashMap<>();
        switch (language) {
            case ENGLISH:
                for (EnglishLetterFrequencyZ26 letter : EnglishLetterFrequencyZ26.values()) {
                    map.put(letter.name().charAt(0), letter.getProbability());
                }
                break;
            case FRENCH:
                for (FrenchLetterFrequencyZ26 letter : FrenchLetterFrequencyZ26.values()) {
                    map.put(letter.name().charAt(0), letter.getProbability());
                }
                break;
            case SPANISH:
                for (SpanishLetterFrequencyZ26 letter : SpanishLetterFrequencyZ26.values()) {
                    map.put(letter.name().charAt(0), letter.getProbability());
                }
                break;
            case PORTUGUESE:
                for (PortugueseLetterFrequencyZ26 letter : PortugueseLetterFrequencyZ26.values()) {
                    map.put(letter.name().charAt(0), letter.getProbability());
                }
                break;
            default:
                // Default to English if not specified
                for (EnglishLetterFrequencyZ26 letter : EnglishLetterFrequencyZ26.values()) {
                    map.put(letter.name().charAt(0), letter.getProbability());
                }
                break;
        }
        return map;
    }
}
