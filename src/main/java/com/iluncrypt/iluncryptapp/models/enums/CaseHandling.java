package com.iluncrypt.iluncryptapp.models.enums;

/**
 * Enum to define how uppercase and lowercase letters should be handled in encryption.
 */
public enum CaseHandling {
    IGNORE,      // Treat uppercase and lowercase as the same (A = a)
    STRICT,      // Distinguish uppercase from lowercase (A != a)
    PRESERVE     // Maintain case in the output but treat them as the same symbol
}
