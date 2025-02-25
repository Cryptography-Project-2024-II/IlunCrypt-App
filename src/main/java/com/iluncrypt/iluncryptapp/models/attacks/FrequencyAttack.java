package com.iluncrypt.iluncryptapp.models.attacks;

import com.iluncrypt.iluncryptapp.models.enums.Language;
import com.iluncrypt.iluncryptapp.models.enums.EnglishLetterFrequencyZ26;
import com.iluncrypt.iluncryptapp.models.enums.FrenchLetterFrequencyZ26;
import com.iluncrypt.iluncryptapp.models.enums.SpanishLetterFrequencyZ26;
import com.iluncrypt.iluncryptapp.models.enums.PortugueseLetterFrequencyZ26;

import java.util.*;
import java.util.stream.Collectors;

public class FrequencyAttack {

    /**
     * Analyzes the given encrypted text using frequency analysis (monograms, bigrams, trigrams)
     * and returns up to five Candidate objects. It uses the assumption that the most frequent letter(s)
     * in the ciphertext correspond to the most frequent ones in the target language.
     *
     * @param encryptedText The ciphertext (letters A–Z are considered).
     * @param language      The language to base the expected frequencies on.
     * @param cipherType    The cipher type ("shift", "multiplicative", or "affine").
     * @return A list of up to five Candidate objects.
     */
    public static List<Candidate> analyze(String encryptedText, Language language, String cipherType) {
        List<Candidate> candidates = new ArrayList<>();
        // Preprocess text: uppercase and keep only letters A-Z.
        String filtered = encryptedText.toUpperCase().replaceAll("[^A-Z]", "");
        if (filtered.isEmpty()) {
            return candidates;
        }
        
        // Count monogram frequencies.
        Map<Character, Integer> freqMap = getFrequency(filtered);
        // Sort letters by descending frequency.
        List<Map.Entry<Character, Integer>> sortedEntries = freqMap.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
        int limit = Math.min(5, sortedEntries.size());
        
        if (cipherType.equalsIgnoreCase("shift")) {
            // For a shift cipher, assume the most frequent ciphertext letter corresponds to the expected one.
            char expected = getExpectedLetter(language); // e.g. 'E' for English.
            for (int i = 0; i < limit; i++) {
                char cipherCandidate = sortedEntries.get(i).getKey();
                int shift = (cipherCandidate - expected + 26) % 26;
                String decrypted = decryptShift(encryptedText, shift);
                double score = computeCombinedScore(decrypted, language);
                candidates.add(new Candidate(String.valueOf(shift), score, decrypted));
            }
        } else if (cipherType.equalsIgnoreCase("multiplicative")) {
            // For multiplicative cipher, choose an expected plaintext letter that is invertible mod 26.
            char expected = getExpectedLetterForMultiplicative(language);
            int expectedVal = expected - 'A';
            int invExpected = multiplicativeInverse(expectedVal, 26);
            if (invExpected == -1) return candidates;
            for (int i = 0; i < limit; i++) {
                char cipherCandidate = sortedEntries.get(i).getKey();
                int cipherVal = cipherCandidate - 'A';
                int keyCandidate = (cipherVal * invExpected) % 26;
                if (keyCandidate < 0) {
                    keyCandidate += 26;
                }
                if (gcd(keyCandidate, 26) != 1) {
                    continue;
                }
                String decrypted = decryptMultiplicative(encryptedText, keyCandidate);
                double score = computeCombinedScore(decrypted, language);
                candidates.add(new Candidate(String.valueOf(keyCandidate), score, decrypted));
            }
        } else if (cipherType.equalsIgnoreCase("affine")) {
            // Use a broader set of candidate letters: top 4 from ciphertext and top 4 expected letters.
            List<Character> cipherCandidates = new ArrayList<>();
            for (int i = 0; i < Math.min(4, sortedEntries.size()); i++) {
                cipherCandidates.add(sortedEntries.get(i).getKey());
            }
            List<Character> expectedCandidates = getTopExpectedLetters(language, 4);
            Set<String> seenKeys = new HashSet<>();

            // Try every combination of two distinct letters from the ciphertext candidates
            // and two distinct letters from the expected candidates.
            for (int i = 0; i < cipherCandidates.size(); i++) {
                for (int j = 0; j < cipherCandidates.size(); j++) {
                    if (i == j) continue;
                    int c1 = cipherCandidates.get(i) - 'A';
                    int c2 = cipherCandidates.get(j) - 'A';
                    for (int m = 0; m < expectedCandidates.size(); m++) {
                        for (int n = 0; n < expectedCandidates.size(); n++) {
                            if (m == n) continue;
                            int p1 = expectedCandidates.get(m) - 'A';
                            int p2 = expectedCandidates.get(n) - 'A';
                            // Solve for a and b from:
                            //   c1 = (a * p1 + b) mod 26
                            //   c2 = (a * p2 + b) mod 26
                            // => a = (c1 - c2) * inv(p1 - p2) mod 26
                            int diffP = (p1 - p2 + 26) % 26;
                            if (diffP == 0 || gcd(diffP, 26) != 1) continue;
                            int diffC = (c1 - c2 + 26) % 26;
                            int invDiffP = multiplicativeInverse(diffP, 26);
                            if (invDiffP == -1) continue;
                            int a = (diffC * invDiffP) % 26;
                            if (a < 0) a += 26;
                            if (gcd(a, 26) != 1) continue; // a must be invertible mod 26
                            int b = (c1 - a * p1) % 26;
                            if (b < 0) b += 26;
                            String keyStr = a + "," + b;
                            if (seenKeys.contains(keyStr)) continue;
                            seenKeys.add(keyStr);
                            String decrypted = decryptAffine(encryptedText, a, b);
                            double score = computeCombinedScore(decrypted, language);
                            candidates.add(new Candidate(keyStr, score, decrypted));
                        }
                    }
                }
            }
            // If no candidates were found via frequency analysis, fall back to a limited brute-force search:
            if (candidates.isEmpty()) {
                for (int a = 1; a < 26; a++) {
                    if (gcd(a, 26) != 1) continue;
                    for (int b = 0; b < 26; b++) {
                        String decrypted = decryptAffine(encryptedText, a, b);
                        double score = computeCombinedScore(decrypted, language);
                        candidates.add(new Candidate(a + "," + b, score, decrypted));
                    }
                }
                candidates.sort((c1, c2) -> Double.compare(c2.getProbability(), c1.getProbability()));
                if (candidates.size() > 5) {
                    candidates = candidates.subList(0, 5);
                }
            }
        }

        return candidates;
    }

    // Helper: counts frequency of letters A-Z.
    private static Map<Character, Integer> getFrequency(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            freq.put(c, 0);
        }
        for (char c : text.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                freq.put(c, freq.get(c) + 1);
            }
        }
        return freq;
    }

    // Returns the expected letter (with highest probability) for monogram frequencies.
    private static char getExpectedLetter(Language language) {
        Map<Character, Double> expected = getExpectedFrequencies(language);
        char best = 'E'; // default
        double max = -1;
        for (Map.Entry<Character, Double> entry : expected.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                best = entry.getKey();
            }
        }
        return best;
    }

    // For multiplicative cipher: choose an expected letter that is invertible mod 26.
    private static char getExpectedLetterForMultiplicative(Language language) {
        Map<Character, Double> expected = getExpectedFrequencies(language);
        char best = ' ';
        double max = -1;
        for (Map.Entry<Character, Double> entry : expected.entrySet()) {
            int val = entry.getKey() - 'A';
            if (gcd(val, 26) == 1 && entry.getValue() > max) {
                max = entry.getValue();
                best = entry.getKey();
            }
        }
        if (best == ' ') {
            best = 'H'; // fallback
        }
        return best;
    }

    // Returns the top N expected letters based on frequency.
    private static List<Character> getTopExpectedLetters(Language language, int n) {
        Map<Character, Double> expected = getExpectedFrequencies(language);
        List<Map.Entry<Character, Double>> list = new ArrayList<>(expected.entrySet());
        list.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
        List<Character> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, list.size()); i++) {
            result.add(list.get(i).getKey());
        }
        return result;
    }

    // Retrieves expected monogram frequencies from language-specific enums.
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
                for (EnglishLetterFrequencyZ26 letter : EnglishLetterFrequencyZ26.values()) {
                    map.put(letter.name().charAt(0), letter.getProbability());
                }
                break;
        }
        return map;
    }

    // --- Decryption routines ---

    private static String decryptShift(String text, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int orig = (c - base - shift + 26) % 26;
                sb.append((char) (base + orig));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String decryptMultiplicative(String text, int key) {
        int inv = multiplicativeInverse(key, 26);
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int orig = (inv * (c - base)) % 26;
                sb.append((char) (base + orig));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String decryptAffine(String text, int a, int b) {
        int inv = multiplicativeInverse(a, 26);
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int x = c - base;
                int orig = (inv * ((x - b + 26) % 26)) % 26;
                sb.append((char) (base + orig));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // Computes the multiplicative inverse of a modulo m.
    private static int multiplicativeInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1;
    }

    // Computes the greatest common divisor.
    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    // --- Combined Score Calculation (Monogram, Bigram, Trigram) ---

    private static double computeCombinedScore(String text, Language language) {
        double mono = computeMonogramScore(text, language);
        double bi = computeBigramScore(text, language);
        double tri = computeTrigramScore(text, language);
        return (mono + bi + tri) / 3.0;
    }

    // Monogram score using chi-squared.
    private static double computeMonogramScore(String text, Language language) {
        String filtered = text.toUpperCase().replaceAll("[^A-Z]", "");
        if (filtered.isEmpty()) return 0.0;
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
        Map<Character, Double> expected = getExpectedFrequencies(language);
        double chi = 0.0;
        for (char c = 'A'; c <= 'Z'; c++) {
            double exp = expected.getOrDefault(c, 0.0);
            if (exp > 0) {
                double obs = observed.get(c);
                chi += Math.pow(obs - exp, 2) / exp;
            }
        }
        return 1.0 / (1.0 + chi);
    }

    // Bigram score: fraction of consecutive two-letter sequences that are common.
    private static double computeBigramScore(String text, Language language) {
        String filtered = text.toUpperCase().replaceAll("[^A-Z]", "");
        if (filtered.length() < 2) return 0.0;
        Set<String> commonBigrams = getCommonBigrams(language);
        int totalBigrams = filtered.length() - 1;
        int count = 0;
        for (int i = 0; i < filtered.length() - 1; i++) {
            String bi = filtered.substring(i, i + 2);
            if (commonBigrams.contains(bi)) {
                count++;
            }
        }
        return count / (double) totalBigrams;
    }

    // Trigram score: fraction of three-letter sequences that are common.
    private static double computeTrigramScore(String text, Language language) {
        String filtered = text.toUpperCase().replaceAll("[^A-Z]", "");
        if (filtered.length() < 3) return 0.0;
        Set<String> commonTrigrams = getCommonTrigrams(language);
        int totalTrigrams = filtered.length() - 2;
        int count = 0;
        for (int i = 0; i < filtered.length() - 2; i++) {
            String tri = filtered.substring(i, i + 3);
            if (commonTrigrams.contains(tri)) {
                count++;
            }
        }
        return count / (double) totalTrigrams;
    }

    // Returns a set of common bigrams; for demonstration, English values are used.
    private static Set<String> getCommonBigrams(Language language) {
        Set<String> bigrams = new HashSet<>();
        if (language == Language.ENGLISH) {
            bigrams.add("TH"); bigrams.add("HE"); bigrams.add("IN");
            bigrams.add("ER"); bigrams.add("AN"); bigrams.add("RE");
        } else {
            // Default values (could be refined per language)
            bigrams.add("TH"); bigrams.add("HE"); bigrams.add("IN");
            bigrams.add("ER"); bigrams.add("AN"); bigrams.add("RE");
        }
        return bigrams;
    }

    // Returns a set of common trigrams; for demonstration, English values are used.
    private static Set<String> getCommonTrigrams(Language language) {
        Set<String> trigrams = new HashSet<>();
        if (language == Language.ENGLISH) {
            trigrams.add("THE"); trigrams.add("ING"); trigrams.add("AND");
            trigrams.add("ENT"); trigrams.add("ION");
        } else {
            // Default values (could be refined per language)
            trigrams.add("THE"); trigrams.add("ING"); trigrams.add("AND");
            trigrams.add("ENT"); trigrams.add("ION");
        }
        return trigrams;
    }
}
