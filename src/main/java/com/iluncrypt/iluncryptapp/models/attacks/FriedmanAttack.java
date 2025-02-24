package com.iluncrypt.iluncryptapp.models.attacks;

import com.iluncrypt.iluncryptapp.models.Alphabet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementa un ataque basado en el índice de coincidencia de Friedman.
 * Dado un ciphertext, un alfabeto y el idioma de referencia (inglés, español, francés o portugués),
 * se evalúa para cada posible longitud de clave (dentro de un rango especificado) el promedio de IC de
 * los subtextos (letras separadas por posición en el supuesto ciclo de la clave).
 * Se asigna un score mayor a aquellas longitudes cuya media de IC esté más próxima al valor esperado para el idioma.
 * El resultado es una lista de candidatos (cada uno representando una posible longitud de clave) ordenados de mayor a menor probabilidad.
 */
public class FriedmanAttack {

    public enum Language {
        ENGLISH, SPANISH, FRENCH, PORTUGUESE;
    }

    /**
     * Representa un candidato con su longitud de clave propuesta y su score (mayor score = mayor probabilidad).
     */
    public static class Candidate implements Comparable<Candidate> {
        public final int keyLength;
        public final double score;

        public Candidate(int keyLength, double score) {
            this.keyLength = keyLength;
            this.score = score;
        }

        @Override
        public int compareTo(Candidate o) {
            // Orden descendente: mayor score primero.
            return Double.compare(o.score, this.score);
        }

        @Override
        public String toString() {
            return "Key Length: " + keyLength + " | Score: " + score;
        }
    }

    // Valores esperados de índice de coincidencia para cada idioma (valores aproximados)
    private static double getExpectedIC(Language language) {
        switch (language) {
            case ENGLISH:
                return 0.0667;
            case SPANISH:
                return 0.077;
            case FRENCH:
                return 0.072;
            case PORTUGUESE:
                return 0.072;
            default:
                return 0.0667;
        }
    }

    /**
     * Calcula el índice de coincidencia de un texto.
     * Se utiliza la fórmula: IC = Σ f(letter)*(f(letter)-1) / (N*(N-1))
     *
     * @param text El texto (se espera que esté filtrado para contener sólo caracteres válidos del alfabeto)
     * @return El índice de coincidencia.
     */
    private static double calculateIC(String text) {
        int n = text.length();
        if (n <= 1) return 0.0;
        int[] freq = new int[alphabetSize(text)];
        // Se asume que el alfabeto es de tipo A-Z, pero se puede adaptar.
        for (char c : text.toCharArray()) {
            // Asumimos mayúsculas
            if (c >= 'A' && c <= 'Z') {
                freq[c - 'A']++;
            }
        }
        double sum = 0;
        for (int count : freq) {
            sum += count * (count - 1);
        }
        return sum / (n * (n - 1) + 1e-6);
    }

    // Método auxiliar para obtener el tamaño "teórico" del alfabeto (en este ejemplo se asume A-Z, es decir, 26).
    // Si en tu proyecto el alfabeto puede ser mayor, se debería obtener mediante el objeto Alphabet.
    private static int alphabetSize(String text) {
        return 26;
    }

    /**
     * Realiza el análisis de Friedman evaluando posibles longitudes de clave entre minKeyLength y maxKeyLength.
     * Para cada k se divide el ciphertext en k subtextos y se calcula el promedio del índice de coincidencia.
     * Se asigna un score = 1/(|IC_promedio - IC_esperado| + ε).
     *
     * @param cipherText    El texto cifrado.
     * @param alphabet      El alfabeto utilizado.
     * @param language      El idioma de referencia.
     * @param minKeyLength  Longitud mínima de clave a evaluar.
     * @param maxKeyLength  Longitud máxima de clave a evaluar.
     * @return Una lista de candidatos ordenados de mayor a menor probabilidad.
     */
    public static List<Candidate> attackFriedman(String cipherText, Alphabet alphabet, Language language,
                                                 int minKeyLength, int maxKeyLength) {
        List<Candidate> candidates = new ArrayList<>();
        String filteredText = filterText(cipherText, alphabet);
        double expectedIC = getExpectedIC(language);
        // Para cada posible clave, dividir el texto en k subtextos.
        for (int k = minKeyLength; k <= maxKeyLength; k++) {
            double avgIC = 0.0;
            int validGroups = 0;
            // Para cada posición en la clave
            for (int i = 0; i < k; i++) {
                StringBuilder group = new StringBuilder();
                for (int j = i; j < filteredText.length(); j += k) {
                    group.append(filteredText.charAt(j));
                }
                if (group.length() > 1) {
                    avgIC += calculateIC(group.toString());
                    validGroups++;
                }
            }
            if (validGroups > 0) {
                avgIC /= validGroups;
            }
            // Score basado en la cercanía al expectedIC: cuanto menor la diferencia, mayor el score.
            double score = 1.0 / (Math.abs(avgIC - expectedIC) + 1e-6);
            candidates.add(new Candidate(k, score));
        }
        Collections.sort(candidates);
        return candidates;
    }

    /**
     * Filtra el texto para dejar sólo los caracteres válidos del alfabeto (en mayúsculas).
     */
    private static String filterText(String text, Alphabet alphabet) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            char uc = Character.toUpperCase(c);
            if (alphabet.isValidChar(uc)) {
                sb.append(uc);
            }
        }
        return sb.toString();
    }
}
