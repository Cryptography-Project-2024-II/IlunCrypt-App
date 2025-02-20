package com.iluncrypt.iluncryptapp.models.algorithms.symmetrickey;

/**
 * This class implements the Simplified DES (SDES) cryptosystem.
 * <p>
 * It accepts plaintext (or ciphertext) in two formats:
 * <ul>
 *   <li>A letter string where each letter (A-P) represents 4 bits (e.g., "AA" → "00000000").</li>
 *   <li>A binary string representing the data directly (e.g., "00000000").</li>
 * </ul>
 * The key must be a 10-bit binary string (e.g., "1010000010").
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * SDESCryptosystem sdes = new SDESCryptosystem();
 * String key = "1010000010";
 *
 * // Encrypting plaintext in letter format:
 * String plaintextLetters = "AA"; // 'A' represents 0000 → "00000000"
 * String ciphertextLetters = sdes.encrypt(plaintextLetters, key);
 *
 * // Encrypting plaintext in binary format:
 * String plaintextBinary = "00000000";
 * String ciphertextBinary = sdes.encrypt(plaintextBinary, key);
 *
 * // Decryption follows the same logic:
 * String decryptedLetters = sdes.decrypt(ciphertextLetters, key);
 * String decryptedBinary = sdes.decrypt(ciphertextBinary, key);
 * }</pre>
 */
public class SDESCryptosystem {

    // Permutation tables (indices are 1-indexed)
    private static final int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    private static final int[] P8  = {6, 3, 7, 4, 8, 5, 10, 9};
    private static final int[] P4  = {2, 4, 3, 1};
    private static final int[] IP  = {2, 6, 3, 1, 4, 8, 5, 7};
    private static final int[] IP_INV = {4, 1, 3, 5, 7, 2, 8, 6};
    private static final int[] EP  = {4, 1, 2, 3, 2, 3, 4, 1};

    // S-boxes
    private static final int[][] S0 = {
            {1, 0, 3, 2},
            {3, 2, 1, 0},
            {0, 2, 1, 3},
            {3, 1, 3, 2}
    };

    private static final int[][] S1 = {
            {0, 1, 2, 3},
            {2, 0, 1, 3},
            {3, 0, 1, 0},
            {2, 1, 0, 3}
    };

    /**
     * Encrypts the given plaintext using SDES.
     *
     * @param plaintext The plaintext as either a letter string (each letter A–P represents 4 bits)
     *                  or as a binary string (8-bit blocks).
     * @param key10     The 10-bit key as a binary string (e.g., "1010000010").
     * @return The ciphertext in the same format as the input (letters or binary).
     * @throws IllegalArgumentException if the key is not 10 bits or if the plaintext is invalid.
     */
    public String encrypt(String plaintext, String key10) {
        boolean inputIsBinary = plaintext.matches("[01]+");
        String binaryPlain;
        if (inputIsBinary) {
            binaryPlain = plaintext;
        } else {
            // Convert letters (A-P) to binary (each letter = 4 bits)
            binaryPlain = textToBinary(plaintext.toUpperCase());
        }
        if (binaryPlain.length() % 8 != 0) {
            throw new IllegalArgumentException("Plaintext must represent 8-bit blocks (each block of 2 letters or 8 binary digits).");
        }

        String[] keys = generateKeys(key10);
        String k1 = keys[0];
        String k2 = keys[1];

        StringBuilder binaryCipher = new StringBuilder();
        for (int i = 0; i < binaryPlain.length(); i += 8) {
            String block = binaryPlain.substring(i, i + 8);
            String cipherBlock = encryptBlock(block, k1, k2);
            binaryCipher.append(cipherBlock);
        }

        return inputIsBinary ? binaryCipher.toString() : binaryToText(binaryCipher.toString());
    }

    /**
     * Decrypts the given ciphertext using SDES.
     *
     * @param ciphertext The ciphertext as either a letter string (each letter A–P represents 4 bits)
     *                   or as a binary string (8-bit blocks).
     * @param key10      The 10-bit key as a binary string.
     * @return The decrypted plaintext in the same format as the input (letters or binary).
     * @throws IllegalArgumentException if the key is not 10 bits or if the ciphertext is invalid.
     */
    public String decrypt(String ciphertext, String key10) {
        boolean inputIsBinary = ciphertext.matches("[01]+");
        String binaryCipher;
        if (inputIsBinary) {
            binaryCipher = ciphertext;
        } else {
            binaryCipher = textToBinary(ciphertext.toUpperCase());
        }
        if (binaryCipher.length() % 8 != 0) {
            throw new IllegalArgumentException("Ciphertext must represent 8-bit blocks (each block of 2 letters or 8 binary digits).");
        }

        String[] keys = generateKeys(key10);
        // For decryption, the subkeys are used in reverse order.
        String k1 = keys[0];
        String k2 = keys[1];

        StringBuilder binaryPlain = new StringBuilder();
        for (int i = 0; i < binaryCipher.length(); i += 8) {
            String block = binaryCipher.substring(i, i + 8);
            String plainBlock = decryptBlock(block, k1, k2);
            binaryPlain.append(plainBlock);
        }

        return inputIsBinary ? binaryPlain.toString() : binaryToText(binaryPlain.toString());
    }

    /**
     * Generates the two subkeys (K1 and K2) from the provided 10-bit key.
     *
     * @param key10 The 10-bit key as a binary string.
     * @return An array containing K1 and K2.
     * @throws IllegalArgumentException if the key is not 10 bits.
     */
    private String[] generateKeys(String key10) {
        if (key10.length() != 10) {
            throw new IllegalArgumentException("Key must be 10 bits.");
        }
        // Apply P10 permutation
        String permuted = permute(key10, P10);
        // Split into two halves (5 bits each)
        String left = permuted.substring(0, 5);
        String right = permuted.substring(5);
        // Perform a circular left shift by 1
        left = leftShift(left, 1);
        right = leftShift(right, 1);
        // Combine halves and apply P8 to get K1
        String k1 = permute(left + right, P8);
        // Perform additional circular left shifts by 2
        left = leftShift(left, 2);
        right = leftShift(right, 2);
        String k2 = permute(left + right, P8);
        return new String[]{k1, k2};
    }

    /**
     * Encrypts an 8-bit block using SDES.
     *
     * @param block The 8-bit block as a binary string.
     * @param k1    The first subkey.
     * @param k2    The second subkey.
     * @return The encrypted 8-bit block as a binary string.
     */
    private String encryptBlock(String block, String k1, String k2) {
        // Initial Permutation (IP)
        String ip = permute(block, IP);
        String left = ip.substring(0, 4);
        String right = ip.substring(4);
        // First round: fK function with subkey K1
        String result = fk(left, right, k1);
        // Swap the two halves
        String swapped = result.substring(4) + result.substring(0, 4);
        // Second round: fK function with subkey K2
        result = fk(swapped.substring(0, 4), swapped.substring(4), k2);
        // Inverse Initial Permutation (IP^-1)
        return permute(result, IP_INV);
    }

    /**
     * Decrypts an 8-bit block using SDES.
     *
     * @param block The 8-bit block as a binary string.
     * @param k1    The first subkey.
     * @param k2    The second subkey.
     * @return The decrypted 8-bit block as a binary string.
     */
    private String decryptBlock(String block, String k1, String k2) {
        // Initial Permutation (IP)
        String ip = permute(block, IP);
        String left = ip.substring(0, 4);
        String right = ip.substring(4);
        // First round with K2 (subkeys used in reverse order for decryption)
        String result = fk(left, right, k2);
        // Swap halves
        String swapped = result.substring(4) + result.substring(0, 4);
        // Second round with K1
        result = fk(swapped.substring(0, 4), swapped.substring(4), k1);
        // Apply inverse IP
        return permute(result, IP_INV);
    }

    /**
     * The fK function combines the left and right halves of the data with the subkey.
     *
     * @param left   The left 4 bits.
     * @param right  The right 4 bits.
     * @param subkey The subkey to use.
     * @return An 8-bit binary string resulting from the function.
     */
    private String fk(String left, String right, String subkey) {
        // Expand and permute the right half using EP (4 -> 8 bits)
        String expanded = permute(right, EP);
        // XOR with the subkey
        String xorResult = xor(expanded, subkey);
        // Split the result into two 4-bit groups
        String leftXor = xorResult.substring(0, 4);
        String rightXor = xorResult.substring(4);
        // Apply the S-boxes
        String s0Output = sBox(leftXor, S0);
        String s1Output = sBox(rightXor, S1);
        // Combine the outputs (each 2 bits) and apply P4
        String p4Output = permute(s0Output + s1Output, P4);
        // XOR with the left half
        String leftResult = xor(left, p4Output);
        // Return the concatenation of the new left half with the unchanged right half
        return leftResult + right;
    }

    /**
     * Applies an S-box to a 4-bit input.
     *
     * @param input A 4-bit binary string.
     * @param sBox  The S-box (4x4 matrix).
     * @return A 2-bit binary string as output.
     */
    private String sBox(String input, int[][] sBox) {
        // Determine row from first and last bit
        int row = Integer.parseInt("" + input.charAt(0) + input.charAt(3), 2);
        // Determine column from the two middle bits
        int col = Integer.parseInt(input.substring(1, 3), 2);
        int sVal = sBox[row][col];
        String binary = Integer.toBinaryString(sVal);
        while (binary.length() < 2) {
            binary = "0" + binary;
        }
        return binary;
    }

    /**
     * Applies a permutation to the input string using the provided permutation table.
     *
     * @param input       The binary string to permute.
     * @param permutation The permutation table (1-indexed).
     * @return The permuted binary string.
     */
    private String permute(String input, int[] permutation) {
        StringBuilder output = new StringBuilder();
        for (int pos : permutation) {
            output.append(input.charAt(pos - 1));
        }
        return output.toString();
    }

    /**
     * Performs a circular left shift on the input string.
     *
     * @param input  The binary string to shift.
     * @param shifts The number of positions to shift.
     * @return The shifted binary string.
     */
    private String leftShift(String input, int shifts) {
        int n = input.length();
        shifts = shifts % n;
        return input.substring(shifts) + input.substring(0, shifts);
    }

    /**
     * Computes the XOR of two binary strings of equal length.
     *
     * @param a The first binary string.
     * @param b The second binary string.
     * @return The resulting binary string after XOR.
     */
    private String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    /**
     * Converts a letter-based string (each letter A–P representing 4 bits) to a binary string.
     *
     * @param text The text containing letters A–P.
     * @return The corresponding binary string.
     */
    private String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            binary.append(letterToNibble(c));
        }
        return binary.toString();
    }

    /**
     * Converts a binary string to a letter-based string using 4-bit groups (A = 0000, …, P = 1111).
     *
     * @param binary The binary string.
     * @return The corresponding letter-based string.
     * @throws IllegalArgumentException if the binary string length is not a multiple of 4.
     */
    private String binaryToText(String binary) {
        if (binary.length() % 4 != 0) {
            throw new IllegalArgumentException("Binary string length must be a multiple of 4.");
        }
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 4) {
            String nibble = binary.substring(i, i + 4);
            text.append(nibbleToLetter(nibble));
        }
        return text.toString();
    }

    /**
     * Converts a letter (A–P) to its 4-bit binary representation.
     *
     * @param letter The letter to convert.
     * @return A 4-bit binary string.
     */
    private String letterToNibble(char letter) {
        int value = letter - 'A';
        String binary = Integer.toBinaryString(value);
        while (binary.length() < 4) {
            binary = "0" + binary;
        }
        return binary;
    }

    /**
     * Converts a 4-bit binary string to its corresponding letter (A–P).
     *
     * @param nibble A 4-bit binary string.
     * @return The corresponding letter.
     */
    private char nibbleToLetter(String nibble) {
        int value = Integer.parseInt(nibble, 2);
        return (char) ('A' + value);
    }

    /**
     * Generates a random 10-bit key as a binary string.
     *
     * @return A random 10-bit binary string (e.g., "1010000010").
     */
    public static String generateRandomKey() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(Math.random() < 0.5 ? '0' : '1');
        }
        return sb.toString();
    }
}
