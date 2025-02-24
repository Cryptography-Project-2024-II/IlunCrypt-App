package com.iluncrypt.iluncryptapp.models.attacks;

import com.iluncrypt.iluncryptapp.models.Alphabet;

import java.util.*;

/**
 * Implementa un ataque por análisis de frecuencias para cifrados por sustitución.
 * Se generan múltiples candidatos utilizando un mapeo base (obtenido de la frecuencia observada)
 * y se refinan mediante hill-climbing. Los candidatos se ordenan de mayor a menor probabilidad.
 */
public class FrequencyAnalysisAttacker {

    public enum Language {
        ENGLISH, SPANISH, FRENCH, PORTUGUESE;
    }

    /**
     * Representa un candidato (texto en claro) junto a su score, donde un score mayor indica
     * mayor probabilidad de ser el mensaje original.
     */
    public static class Candidate implements Comparable<Candidate> {
        public final String plaintext;
        public final double score;

        public Candidate(String plaintext, double score) {
            this.plaintext = plaintext;
            this.score = score;
        }

        @Override
        public int compareTo(Candidate other) {
            // Orden descendente: mayor score primero.
            return Double.compare(other.score, this.score);
        }
    }

    // Distribuciones esperadas (porcentaje) para los 26 caracteres (A-Z) para cada idioma.
    private static final Map<Language, double[]> expectedFrequencies = new HashMap<>();
    static {
        expectedFrequencies.put(Language.ENGLISH, new double[]{
                8.2, 1.5, 2.8, 4.3, 13.0, 2.2, 2.0, 6.1, 7.0, 0.15, 0.77, 4.0, 2.4,
                6.7, 7.5, 1.9, 0.095, 6.0, 6.3, 9.1, 2.8, 0.98, 2.4, 0.15, 2.0, 0.074
        });
        expectedFrequencies.put(Language.SPANISH, new double[]{
                12.53, 1.42, 4.68, 5.86, 13.68, 0.69, 1.01, 0.70, 6.25, 0.44, 0.01, 4.97,
                3.15, 6.71, 8.68, 2.51, 0.88, 6.87, 7.98, 4.63, 3.93, 0.90, 0.02, 0.22, 0.90, 0.52
        });
        expectedFrequencies.put(Language.FRENCH, new double[]{
                7.636, 0.901, 3.260, 3.669, 14.715, 1.066, 0.866, 0.737, 7.529, 0.613,
                0.049, 5.456, 2.968, 7.095, 5.796, 2.521, 1.362, 6.693, 7.948, 7.244,
                6.311, 1.838, 0.049, 0.427, 0.128, 0.326
        });
        expectedFrequencies.put(Language.PORTUGUESE, new double[]{
                14.63, 1.04, 3.88, 4.99, 12.57, 1.02, 1.30, 1.28, 6.18, 0.40,
                0.02, 4.74, 2.97, 5.05, 10.73, 2.52, 1.20, 6.53, 7.81, 4.34,
                4.63, 1.67, 0.01, 0.21, 0.01, 0.47
        });
    }

    /**
     * Devuelve una lista de candidatos de texto en claro para un ciphertext dado,
     * utilizando un ataque por análisis de frecuencias y hill-climbing.
     *
     * @param cipherText         El texto cifrado.
     * @param alphabet           El alfabeto usado en el cifrado.
     * @param language           El idioma para obtener la distribución esperada.
     * @param numRestarts        Número de reinicios aleatorios en hill-climbing.
     * @param iterationsPerRestart Número de iteraciones por reinicio.
     * @return Lista de candidatos ordenados de mayor a menor probabilidad.
     */
    public static List<Candidate> attackSubstitution(String cipherText, Alphabet alphabet, Language language,
                                                     int numRestarts, int iterationsPerRestart) {
        List<Candidate> candidateList = new ArrayList<>();
        Map<Character, Integer> observedFreq = getObservedFrequencies(cipherText, alphabet);

        // Obtén la lista de letras presentes en el ciphertext ordenadas por frecuencia descendente.
        List<Character> cipherOrder = new ArrayList<>(observedFreq.keySet());
        cipherOrder.sort((a, b) -> observedFreq.get(b) - observedFreq.get(a));

        // Lista de letras del alfabeto (se asume que las primeras 26 son letras; ajustar si es necesario)
        List<Character> alphabetLetters = new ArrayList<>();
        int limit = Math.min(26, alphabet.size());
        for (int i = 0; i < limit; i++) {
            alphabetLetters.add(alphabet.getChar(i));
        }
        // Ordena las letras según la frecuencia esperada del idioma (de mayor a menor)
        double[] expFreq = expectedFrequencies.get(language);
        alphabetLetters.sort((a, b) -> Double.compare(expFreq[b - 'A'], expFreq[a - 'A']));

        // Mapeo base: asigna la i-ésima letra del ciphertext (por frecuencia) a la i-ésima letra esperada.
        Map<Character, Character> baseMapping = new HashMap<>();
        int n = Math.min(cipherOrder.size(), alphabetLetters.size());
        for (int i = 0; i < n; i++) {
            baseMapping.put(cipherOrder.get(i), alphabetLetters.get(i));
        }
        // Para las letras que no aparezcan en el ciphertext, se mantienen sin cambios (o se asigna de forma arbitraria)
        for (char c : alphabetLetters) {
            if (!baseMapping.containsKey(c)) {
                baseMapping.put(c, c);
            }
        }

        Random random = new Random();
        // Realizar múltiples reinicios para explorar diferentes vecindarios
        for (int restart = 0; restart < numRestarts; restart++) {
            // Clonar el mapeo base para iniciar este reinicio
            Map<Character, Character> currentMapping = new HashMap<>(baseMapping);
            String currentPlain = decodeWithMapping(cipherText, currentMapping, alphabet);
            double currentScore = scorePlaintext(currentPlain, language, alphabet);

            // Hill-climbing: se intentan iteraciones de swaps aleatorios en el mapeo.
            for (int iter = 0; iter < iterationsPerRestart; iter++) {
                // Seleccionar dos letras (de las que aparecen en el ciphertext) para intercambiar sus asignaciones.
                List<Character> keys = new ArrayList<>(currentMapping.keySet());
                char key1 = keys.get(random.nextInt(keys.size()));
                char key2 = keys.get(random.nextInt(keys.size()));
                while (key1 == key2) {
                    key2 = keys.get(random.nextInt(keys.size()));
                }
                // Clonar el mapeo y realizar el swap.
                Map<Character, Character> newMapping = new HashMap<>(currentMapping);
                char temp = newMapping.get(key1);
                newMapping.put(key1, newMapping.get(key2));
                newMapping.put(key2, temp);
                String newPlain = decodeWithMapping(cipherText, newMapping, alphabet);
                double newScore = scorePlaintext(newPlain, language, alphabet);
                // Si el nuevo mapping mejora el score, se acepta.
                if (newScore > currentScore) {
                    currentMapping = newMapping;
                    currentScore = newScore;
                    currentPlain = newPlain;
                }
            }
            candidateList.add(new Candidate(currentPlain, currentScore));
        }
        Collections.sort(candidateList);
        return candidateList;
    }

    /**
     * Calcula la frecuencia de cada carácter en el ciphertext según el alfabeto.
     */
    private static Map<Character, Integer> getObservedFrequencies(String cipherText, Alphabet alphabet) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : cipherText.toUpperCase().toCharArray()) {
            if (alphabet.isValidChar(c)) {
                freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
            }
        }
        return freqMap;
    }

    /**
     * Decodifica el ciphertext aplicando un mapeo de sustitución.
     */
    private static String decodeWithMapping(String cipherText, Map<Character, Character> mapping, Alphabet alphabet) {
        StringBuilder sb = new StringBuilder();
        for (char c : cipherText.toCharArray()) {
            char uc = Character.toUpperCase(c);
            if (alphabet.isValidChar(uc)) {
                char mapped = mapping.getOrDefault(uc, uc);
                // Preserva la case original
                sb.append(Character.isLowerCase(c) ? Character.toLowerCase(mapped) : mapped);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Calcula un score para el texto en claro basado en chi-cuadrado (invirtiendo el valor para que
     * un score mayor indique mejor concordancia).
     */
    public static double scorePlaintext(String plaintext, Language language, Alphabet alphabet) {
        String text = plaintext.toUpperCase().replaceAll("[^A-Z]", "");
        if (text.length() == 0) return Double.MIN_VALUE;
        double[] expFreq = expectedFrequencies.get(language);
        double[] observed = new double[26];
        for (char c : text.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                observed[c - 'A']++;
            }
        }
        double chiSquare = 0.0;
        double total = text.length();
        for (int i = 0; i < 26; i++) {
            double expected = total * (expFreq[i] / 100.0);
            double diff = observed[i] - expected;
            chiSquare += (diff * diff) / (expected + 1e-6);
        }
        return 1.0 / (chiSquare + 1);
    }
}
