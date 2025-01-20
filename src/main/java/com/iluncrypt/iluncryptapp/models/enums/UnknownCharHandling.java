package com.iluncrypt.iluncryptapp.models.enums;

/**
 * Enum to define how unknown characters should be handled during encryption and decryption.
 */
public enum UnknownCharHandling {
    IGNORE,      // Ignore unknown characters (keep them as they are)
    REMOVE,      // Remove unknown characters from the output
    REPLACE      // Replace unknown characters with a placeholder (e.g., '?')
}
