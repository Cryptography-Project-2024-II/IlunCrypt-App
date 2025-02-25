package com.iluncrypt.iluncryptapp.models.attacks;

import com.iluncrypt.iluncryptapp.models.enums.Language;
import java.util.*;

public class VigenereCryptanalysis {
    // Frecuencias de letras para cada idioma (valores en decimales)
    private static final double[] ENGLISH_FREQ = {
            0.082, 0.015, 0.028, 0.043, 0.127, 0.022, 0.020, 0.061, 0.070, 0.002,
            0.008, 0.040, 0.024, 0.067, 0.075, 0.019, 0.001, 0.060, 0.063, 0.091,
            0.028, 0.010, 0.023, 0.001, 0.020, 0.001
    };

    private static final double[] SPANISH_FREQ = {
            0.1253, 0.0142, 0.0468, 0.0586, 0.1368, 0.0069, 0.0101, 0.0070, 0.0625, 0.0044,
            0.0001, 0.0497, 0.0315, 0.0671, 0.0868, 0.0251, 0.0088, 0.0687, 0.0798, 0.0463,
            0.0393, 0.0090, 0.0002, 0.0022, 0.0090, 0.0052
    };

    private static final double[] FRENCH_FREQ = {
            0.07636, 0.00901, 0.03260, 0.03669, 0.14715, 0.01066, 0.00866, 0.00737, 0.07529, 0.00613,
            0.00049, 0.05456, 0.02968, 0.07095, 0.05796, 0.02521, 0.01362, 0.06693, 0.07948, 0.07244,
            0.06311, 0.01838, 0.00074, 0.00427, 0.00128, 0.00326
    };

    private static final double[] PORTUGUESE_FREQ = {
            0.1463, 0.0104, 0.0388, 0.0499, 0.1257, 0.0102, 0.0130, 0.0128, 0.0618, 0.0040,
            0.0002, 0.0278, 0.0474, 0.0505, 0.1073, 0.0252, 0.0120, 0.0653, 0.0681, 0.0434,
            0.0463, 0.0167, 0.0001, 0.0021, 0.0001, 0.0047
    };

    // Valores esperados del índice de coincidencia para cada idioma
    private static final double ENGLISH_IC = 0.068;
    private static final double SPANISH_IC = 0.077;
    private static final double FRENCH_IC = 0.077;       // Valor aproximado
    private static final double PORTUGUESE_IC = 0.072;   // Valor aproximado

    public static double calculateIC(String text) {
        int[] letterCounts = new int[26];
        int totalLetters = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                c = Character.toLowerCase(c);
                letterCounts[c - 'a']++;
                totalLetters++;
            }
        }
        if (totalLetters <= 1) return 0.0;
        double ic = 0.0;
        for (int count : letterCounts) {
            ic += count * (count - 1);
        }
        ic /= (totalLetters * (totalLetters - 1));
        return ic;
    }

    // Método auxiliar para obtener el array de frecuencias según el idioma
    private static double[] getFrequency(Language language) {
        switch(language) {
            case ENGLISH: return ENGLISH_FREQ;
            case SPANISH: return SPANISH_FREQ;
            case FRENCH: return FRENCH_FREQ;
            case PORTUGUESE: return PORTUGUESE_FREQ;
            default: throw new IllegalArgumentException("Idioma no soportado: " + language);
        }
    }

    // Método auxiliar para obtener el IC esperado según el idioma
    private static double getExpectedIC(Language language) {
        switch(language) {
            case ENGLISH: return ENGLISH_IC;
            case SPANISH: return SPANISH_IC;
            case FRENCH: return FRENCH_IC;
            case PORTUGUESE: return PORTUGUESE_IC;
            default: throw new IllegalArgumentException("Idioma no soportado: " + language);
        }
    }

    // Búsqueda de la longitud de clave utilizando el IC esperado del idioma
    public static int findKeyLength(String cipherText, Language language) {
        int bestLength = 1;
        double bestIC = 0;
        double expectedIC = getExpectedIC(language);
        for (int m = 1; m <= 10; m++) {
            List<StringBuilder> segments = new ArrayList<>();
            for (int i = 0; i < m; i++) {
                segments.add(new StringBuilder());
            }
            for (int i = 0; i < cipherText.length(); i++) {
                segments.get(i % m).append(cipherText.charAt(i));
            }
            double icSum = 0;
            for (StringBuilder segment : segments) {
                icSum += calculateIC(segment.toString());
            }
            double averageIC = icSum / m;
            if (Math.abs(averageIC - expectedIC) < Math.abs(bestIC - expectedIC)) {
                bestIC = averageIC;
                bestLength = m;
            }
        }
        return bestLength;
    }

    // Obtiene los candidatos de desplazamiento (shift) para un segmento
    private static List<CandidateShift> getShiftCandidates(String segment, Language language) {
        int[] letterCounts = new int[26];
        int totalLetters = 0;
        for (char c : segment.toCharArray()) {
            if (Character.isLetter(c)) {
                c = Character.toLowerCase(c);
                letterCounts[c - 'a']++;
                totalLetters++;
            }
        }
        double[] freq = getFrequency(language);
        List<CandidateShift> candidates = new ArrayList<>();
        for (int shift = 0; shift < 26; shift++) {
            double score = 0;
            for (int i = 0; i < 26; i++) {
                double observed = totalLetters > 0 ? (double) letterCounts[(i + shift) % 26] / totalLetters : 0;
                score += Math.pow(observed - freq[i], 2) / freq[i];
            }
            candidates.add(new CandidateShift(shift, score));
        }
        // Ordenar de menor a mayor score (mejor ajuste)
        candidates.sort(Comparator.comparingDouble(CandidateShift::getScore));
        // Retorna los 5 mejores (o menos si no hay 5)
        return candidates.subList(0, Math.min(5, candidates.size()));
    }

    // Clase auxiliar para representar un candidato de desplazamiento
    private static class CandidateShift {
        private final int shift;
        private final double score;
        public CandidateShift(int shift, double score) {
            this.shift = shift;
            this.score = score;
        }
        public int getShift() { return shift; }
        public double getScore() { return score; }
    }

    // Clase auxiliar para almacenar una combinación de candidatos de cada segmento (clave candidata)
    private static class KeyCandidate {
        private final String key;
        private final double totalScore;
        public KeyCandidate(String key, double totalScore) {
            this.key = key;
            this.totalScore = totalScore;
        }
        public String getKey() { return key; }
        public double getTotalScore() { return totalScore; }
    }

    // Método recursivo para combinar los candidatos de cada segmento
    private static void combineCandidates(List<List<CandidateShift>> candidateLists, int index, String currentKey, double currentScore, List<KeyCandidate> results) {
        if (index == candidateLists.size()) {
            results.add(new KeyCandidate(currentKey, currentScore));
            return;
        }
        for (CandidateShift cs : candidateLists.get(index)) {
            combineCandidates(candidateLists, index + 1, currentKey + (char) ('a' + cs.getShift()), currentScore + cs.getScore(), results);
        }
    }

    // Método que aplica una clave para descifrar el texto cifrado
    public static String decryptWithKey(String cipherText, String key) {
        StringBuilder plainText = new StringBuilder();
        int keyLength = key.length();
        for (int i = 0; i < cipherText.length(); i++) {
            char c = cipherText.charAt(i);
            if (Character.isLetter(c)) {
                int shift = key.charAt(i % keyLength) - 'a';
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char decrypted = (char) ((c - base - shift + 26) % 26 + base);
                plainText.append(decrypted);
            } else {
                plainText.append(c);
            }
        }
        return plainText.toString();
    }

    /**
     * Método decryptVigenere que devuelve un List<Candidate> con los 5 mejores candidatos.
     * En cada Candidate se asigna el score total obtenido para la clave candidata.
     */
    public static List<Candidate> decryptVigenere(String cipherText, Language language) {
        int keyLength = findKeyLength(cipherText, language);

        // Dividir el texto cifrado en segmentos según la longitud de clave
        List<StringBuilder> segments = new ArrayList<>();
        for (int i = 0; i < keyLength; i++) {
            segments.add(new StringBuilder());
        }
        for (int i = 0; i < cipherText.length(); i++) {
            segments.get(i % keyLength).append(cipherText.charAt(i));
        }

        // Para cada segmento se obtienen los 5 mejores candidatos de desplazamiento
        List<List<CandidateShift>> candidateLists = new ArrayList<>();
        for (StringBuilder segment : segments) {
            candidateLists.add(getShiftCandidates(segment.toString(), language));
        }

        // Combinar los candidatos de cada segmento para formar posibles claves
        List<KeyCandidate> keyCandidates = new ArrayList<>();
        combineCandidates(candidateLists, 0, "", 0.0, keyCandidates);
        // Ordenar las combinaciones por la suma total de scores (menor es mejor)
        keyCandidates.sort(Comparator.comparingDouble(KeyCandidate::getTotalScore));

        // Seleccionar los 5 mejores candidatos y crear los objetos Candidate
        List<Candidate> result = new ArrayList<>();
        int count = Math.min(5, keyCandidates.size());
        for (int i = 0; i < count; i++) {
            KeyCandidate kc = keyCandidates.get(i);
            String decryptedText = decryptWithKey(cipherText, kc.getKey());
            // Usamos el score total como probabilidad (a menor score, mejor candidato)
            result.add(new Candidate(kc.getKey(), kc.getTotalScore(), decryptedText));
        }
        return result;
    }

    public static void main(String[] args) {
        String cipherText = "CHREEVOAHMAERATBIAXXWTNXBEEOPHBSBQMQEQERBW" +
                "RVXUOAKXAOSXXWEAHBWGJMMQMNKGRFVGXWTRZXWIAK" +
                "LXFPSKAUTEMNDCMGTSXMXBTUIADNGMGPSRELXNJELX" +
                "VRVPRTULHDNQWTWDTYGBPHXTFALJHASVBFXNGLLCHR" +
                "ZBWELEKMSJIKNBHWRJGNMGJSGLXFEYPHAGNRBIEQJT" +
                "AMRVLCRREMNDGLXRRIMGNSNRWCHRQHAEYEVTAQEBBI" +
                "PEEWEVKAKOEWADREMXMTBHHCHRTKDNVRZCHRCLQOHP" +
                "WQAIIWXNRMGWOIIFKEE";
        Language language = Language.SPANISH;
        List<Candidate> candidates = decryptVigenere(cipherText, language);
        for (Candidate c : candidates) {
            System.out.println("Clave candidata: " + c.getKey());
            System.out.println("Texto descifrado: " + c.getDecryptedText());
            System.out.println("Score (probabilidad): " + c.getProbability());
            System.out.println("-----");
        }
    }
}