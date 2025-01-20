package com.iluncrypt.iluncryptapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an alphabet definition with metadata.
 */
public class Alphabet {
    private final String name;
    private final String description;
    private final List<Character> characters;

    public Alphabet(String name, String description, List<Character> characters) {
        this.name = name;
        this.description = description;
        this.characters = characters;
    }

    // Get the name of the alphabet
    public String getName() {
        return name;
    }

    // Get the description of the alphabet
    public String getDescription() {
        return description;
    }

    // Check if a character is part of the alphabet
    public boolean isValidChar(char c) {
        return characters.contains(c);
    }

    // Get the index of a character in the alphabet
    public int getIndex(char c) {
        return characters.indexOf(c);
    }

    // Get a character at a specific index in the alphabet
    public char getChar(int index) {
        return characters.get(index);
    }

    // Get the size of the alphabet
    public int size() {
        return characters.size();
    }

    @Override
    public String toString() {
        return name + " (" + size() + " chars)";
    }

    /**
     * Generates an alphabet ℤ_n with n elements (from 0 to n-1).
     *
     * @param n The size of the alphabet (n).
     * @return An Alphabet object representing ℤ_n.
     */
    public static Alphabet generateZAlphabet(int n) {
        // Create a list of characters representing the numbers 0, 1, ..., n-1
        List<Character> characters = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            // Convert the number i to its corresponding character
            characters.add((char) ('0' + i));
        }

        // Create an alphabet named "ℤ_n" with an appropriate description
        return new Alphabet("ℤ_" + n, "Alphabet of integers modulo " + n, characters);
    }

    /**
     * Returns the list of characters in the alphabet.
     *
     * @return A list of characters in this alphabet.
     */
    public List<Character> getCharacters() {
        return characters;
    }
}
