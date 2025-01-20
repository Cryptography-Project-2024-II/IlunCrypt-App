package com.iluncrypt.iluncryptapp.utils;

import java.util.*;

/**
 * Generates Unicode alphabets dynamically based on Unicode blocks.
 * Only one Unicode alphabet is stored in memory at a time.
 */
public class UnicodeAlphabetGenerator {
    private static List<Character> currentUnicodeAlphabet = null;

    /**
     * Generates a Unicode alphabet based on the specified Unicode block.
     * Previous Unicode alphabets are removed from memory before generating a new one.
     *
     * @param block The Unicode block to generate characters from.
     * @return A list of characters belonging to the specified Unicode block.
     */
    public static List<Character> getUnicodeBlock(Character.UnicodeBlock block) {
        currentUnicodeAlphabet = null; // Remove previous alphabet from memory

        List<Character> newAlphabet = new ArrayList<>();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (Character.UnicodeBlock.of(c) == block) {
                newAlphabet.add(c);
            }
        }
        currentUnicodeAlphabet = newAlphabet;
        return currentUnicodeAlphabet;
    }
}
