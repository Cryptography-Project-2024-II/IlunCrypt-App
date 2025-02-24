package com.iluncrypt.iluncryptapp.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Provides static utility methods for converting strings among:
 * <ul>
 *   <li>Printable Characters (UTF-8)</li>
 *   <li>Base64</li>
 *   <li>Hex</li>
 *   <li>Binary</li>
 * </ul>
 *
 * Also includes a {@link #convert(String, String)} method that dispatches
 * based on a "mode" string, such as "Printable Characters to Binary", etc.
 */
public class ConversionUtils {

    /**
     * Converts the input string according to the specified mode.
     *
     * Supported modes (case-sensitive) are:
     * <ul>
     *   <li>"Printable Characters" (no change)</li>
     *   <li>"Printable Characters to Binary"</li>
     *   <li>"Printable Characters to Hex"</li>
     *   <li>"Printable Characters to Base64"</li>
     *   <li>"Base64" (no change)</li>
     *   <li>"Base64 to Binary"</li>
     *   <li>"Base64 to Hex"</li>
     *   <li>"Hex" (no change)</li>
     *   <li>"Hex to Binary"</li>
     *   <li>"Hex to Base64"</li>
     *   <li>"Binary" (no change)</li>
     *   <li>"Binary to Base64"</li>
     *   <li>"Binary to Hex"</li>
     * </ul>
     *
     * @param mode  The conversion mode (e.g. "Base64 to Hex").
     * @param input The input string to convert.
     * @return The converted string, or the original input if the mode is unrecognized.
     */
    public static String convert(String mode, String input) {
        if (input == null || input.isEmpty()) {
            return input; // nothing to convert
        }
        switch (mode) {
            case "Printable Characters":
                return input;
            case "Printable Characters to Binary":
                return stringToBinary(input);
            case "Printable Characters to Hex":
                return stringToHex(input);
            case "Printable Characters to Base64":
                return stringToBase64(input);

            case "Base64":
                return input;
            case "Base64 to Binary":
                return base64ToBinary(input);
            case "Base64 to Hex":
                return base64ToHex(input);

            case "Hex":
                return input;
            case "Hex to Binary":
                return hexToBinary(input);
            case "Hex to Base64":
                return hexToBase64(input);

            case "Binary":
                return input;
            case "Binary to Base64":
                return binaryToBase64(input);
            case "Binary to Hex":
                return binaryToHex(input);

            default:
                // Optionally throw an exception or return input
                return input;
        }
    }

    // ------------------------------------------------------------------------
    // Printable Characters (UTF-8) -> Binary / Hex / Base64
    // ------------------------------------------------------------------------

    /**
     * Converts a printable-characters string (UTF-8) to a binary representation.
     * Each byte is converted to an 8-bit binary string.
     */
    public static String stringToBinary(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return sb.toString();
    }

    /**
     * Converts a printable-characters string (UTF-8) to hex (uppercase).
     */
    public static String stringToHex(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Converts a printable-characters string (UTF-8) to Base64.
     */
    public static String stringToBase64(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    // ------------------------------------------------------------------------
    // Base64 -> Binary / Hex / Printable Characters
    // ------------------------------------------------------------------------

    /**
     * Decodes a Base64 string and converts it to binary.
     */
    public static String base64ToBinary(String base64) {
        byte[] decoded = Base64.getDecoder().decode(base64);
        StringBuilder sb = new StringBuilder();
        for (byte b : decoded) {
            sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return sb.toString();
    }

    /**
     * Decodes a Base64 string and converts it to hex.
     */
    public static String base64ToHex(String base64) {
        byte[] decoded = Base64.getDecoder().decode(base64);
        StringBuilder sb = new StringBuilder();
        for (byte b : decoded) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Decodes a Base64 string to printable characters (UTF-8).
     */
    public static String base64ToString(String base64) {
        byte[] decoded = Base64.getDecoder().decode(base64);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------------
    // Hex -> Binary / Base64 / Printable Characters
    // ------------------------------------------------------------------------

    /**
     * Converts a hex string to binary.
     * The hex string length must be even.
     */
    public static String hexToBinary(String hex) {
        byte[] bytes = hexToBytes(hex);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return sb.toString();
    }

    /**
     * Converts a hex string to Base64.
     */
    public static String hexToBase64(String hex) {
        byte[] bytes = hexToBytes(hex);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Converts a hex string to printable characters (UTF-8).
     */
    public static String hexToString(String hex) {
        byte[] bytes = hexToBytes(hex);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Helper method: convert a hex string to a byte array.
     */
    private static byte[] hexToBytes(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string length must be even.");
        }
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            String sub = hex.substring(i, i + 2);
            bytes[i / 2] = (byte) Integer.parseInt(sub, 16);
        }
        return bytes;
    }

    // ------------------------------------------------------------------------
    // Binary -> Base64 / Hex / Printable Characters
    // ------------------------------------------------------------------------

    /**
     * Converts a binary string to Base64.
     * The binary string length must be a multiple of 8.
     */
    public static String binaryToBase64(String binary) {
        byte[] bytes = binaryToBytes(binary);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Converts a binary string to hex.
     */
    public static String binaryToHex(String binary) {
        byte[] bytes = binaryToBytes(binary);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Converts a binary string to printable characters (UTF-8).
     */
    public static String binaryToString(String binary) {
        byte[] bytes = binaryToBytes(binary);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Helper method: convert a binary string (e.g. "01000001") to a byte array.
     * The length must be a multiple of 8.
     */
    private static byte[] binaryToBytes(String binary) {
        if (binary.length() % 8 != 0) {
            throw new IllegalArgumentException("Binary string length must be multiple of 8.");
        }
        int length = binary.length();
        byte[] result = new byte[length / 8];
        for (int i = 0; i < length; i += 8) {
            String byteStr = binary.substring(i, i + 8);
            int value = Integer.parseInt(byteStr, 2);
            result[i / 8] = (byte) value;
        }
        return result;
    }
}
