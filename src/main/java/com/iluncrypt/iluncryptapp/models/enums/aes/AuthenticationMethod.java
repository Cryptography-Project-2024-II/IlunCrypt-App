package com.iluncrypt.iluncryptapp.models.enums.aes;

/**
 * Authentication and integrity methods for AES modes without built-in authentication.
 */
public enum AuthenticationMethod {
    NONE("No Authentication"),
    HMAC_SHA256("HMAC-SHA256"),
    HMAC_SHA512("HMAC-SHA512"),
    CRC32("CRC32");

    private final String label;

    AuthenticationMethod(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label; // Lo que se mostrará en el ComboBox
    }

    public static AuthenticationMethod fromString(String text) {
        for (AuthenticationMethod method : AuthenticationMethod.values()) {
            if (method.label.equalsIgnoreCase(text)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid AuthenticationMethod: " + text);
    }
}
