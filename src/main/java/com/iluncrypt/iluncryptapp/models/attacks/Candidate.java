package com.iluncrypt.iluncryptapp.models.attacks;

public class Candidate {
    private final String key;
    private final double probability;
    private final String decryptedText;

    public Candidate(String key, double probability, String decryptedText) {
        this.key = key;
        this.probability = probability;
        this.decryptedText = decryptedText;
    }

    public String getKey() {
        return key;
    }

    public double getProbability() {
        return probability;
    }

    public String getDecryptedText() {
        return decryptedText;
    }



    @Override
    public String toString() {
        return "Key: " + key + " -> " + decryptedText + " | Probability: " + probability;
    }
}
