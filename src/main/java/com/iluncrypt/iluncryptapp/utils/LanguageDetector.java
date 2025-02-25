package com.iluncrypt.iluncryptapp.utils;

import com.iluncrypt.iluncryptapp.models.enums.Language;
import com.iluncrypt.iluncryptapp.models.enums.EnglishLetterFrequencyZ26;
import com.iluncrypt.iluncryptapp.models.enums.FrenchLetterFrequencyZ26;
import com.iluncrypt.iluncryptapp.models.enums.SpanishLetterFrequencyZ26;
import com.iluncrypt.iluncryptapp.models.enums.PortugueseLetterFrequencyZ26;

import java.util.*;
import java.util.stream.Collectors;

public class LanguageDetector {

    /**
     * Ordena una lista de cadenas de texto según su similitud con la distribución de letras
     * esperada para el idioma indicado.
     *
     * @param texts    Lista de textos a evaluar.
     * @param language Idioma (según el enum Language) contra el que comparar.
     * @return Lista de textos ordenada de mayor a menor afinidad al idioma.
     */
    public static List<String> sortByLanguageMatch(List<String> texts, Language language) {
        // Obtener la distribución esperada según el idioma
        Map<Character, Double> expectedFrequencies = getExpectedFrequencies(language);

        // Calcular score para cada texto
        List<ScoredText> scoredTexts = new ArrayList<>();
        for (String text : texts) {
            double score = computeLanguageScore(text, expectedFrequencies);
            scoredTexts.add(new ScoredText(text, score));
        }

        // Ordenar de mayor score a menor score
        Collections.sort(scoredTexts, Comparator.comparingDouble(ScoredText::getScore).reversed());

        // Devolver solo los textos ordenados
        return scoredTexts.stream()
                .map(ScoredText::getText)
                .collect(Collectors.toList());
    }

    /**
     * Calcula un score de similitud (1/(1+chi-cuadrado)) para un texto, comparando la frecuencia observada
     * de las letras A–Z con la distribución esperada.
     *
     * @param text                Texto a evaluar.
     * @param expectedFrequencies Mapa de letra -> frecuencia esperada (en porcentaje).
     * @return Score de similitud; cuanto mayor, mejor se ajusta.
     */
    private static double computeLanguageScore(String text, Map<Character, Double> expectedFrequencies) {
        // Filtrar sólo letras A-Z y convertir a mayúsculas
        String filtered = text.toUpperCase().replaceAll("[^A-Z]", "");
        if (filtered.isEmpty()) {
            return 0.0;
        }

        int n = filtered.length();
        // Contar frecuencias
        Map<Character, Integer> counts = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            counts.put(c, 0);
        }
        for (char c : filtered.toCharArray()) {
            counts.put(c, counts.get(c) + 1);
        }
        // Calcular porcentajes observados
        Map<Character, Double> observed = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            observed.put(c, counts.get(c) * 100.0 / n);
        }

        // Calcular chi-cuadrado
        double chi = 0.0;
        for (char c = 'A'; c <= 'Z'; c++) {
            double exp = expectedFrequencies.getOrDefault(c, 0.0);
            // Evitar división por cero; si la frecuencia esperada es 0, saltar esa letra.
            if (exp > 0) {
                double obs = observed.get(c);
                chi += Math.pow(obs - exp, 2) / exp;
            }
        }

        // Convertir chi-cuadrado en un score: cuanto menor es chi, mayor el score.
        return 1.0 / (1.0 + chi);
    }

    /**
     * Obtiene un mapa con las frecuencias esperadas (en porcentaje) para cada letra A-Z según el idioma.
     *
     * @param language Idioma seleccionado.
     * @return Mapa de letra a frecuencia esperada.
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
                // Por defecto, English
                for (EnglishLetterFrequencyZ26 letter : EnglishLetterFrequencyZ26.values()) {
                    map.put(letter.name().charAt(0), letter.getProbability());
                }
                break;
        }
        return map;
    }

    /**
     * Clase auxiliar para almacenar un texto y su score de similitud.
     */
    private static class ScoredText {
        private final String text;
        private final double score;

        public ScoredText(String text, double score) {
            this.text = text;
            this.score = score;
        }

        public String getText() {
            return text;
        }

        public double getScore() {
            return score;
        }
    }
}
