package com.iluncrypt.iluncryptapp.models.enums.symmetrickey;

/**
 * Enumeration of authentication and integrity methods for encryption modes
 * that do not provide built-in authentication.
 */
public enum AuthenticationMethod {
    NONE(0, null),
    HMAC_SHA256(256, "HmacSHA256"),
    HMAC_SHA384(384, "HmacSHA384"),
    HMAC_SHA512(512, "HmacSHA512");

    private final int hmacSize;
    private final String hmacAlgorithm;

    AuthenticationMethod(int hmacSize, String hmacAlgorithm) {
        this.hmacSize = hmacSize;
        this.hmacAlgorithm = hmacAlgorithm;
    }

    /**
     * Devuelve el tamaño del HMAC en bits.
     */
    public int getHMACSize() {
        return hmacSize;
    }

    /**
     * Devuelve el nombre del algoritmo HMAC correspondiente.
     */
    public String getHMACAlgorithm() {
        return hmacAlgorithm;
    }

    public static AuthenticationMethod fromString(String text) {
        if (text.equalsIgnoreCase("NONE")) {  // Manejar el caso especial de "NONE"
            return AuthenticationMethod.NONE;
        }
        for (AuthenticationMethod method : AuthenticationMethod.values()) {
            if (method.hmacAlgorithm != null && method.hmacAlgorithm.equalsIgnoreCase(text)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid AuthenticationMethod: " + text);
    }

}
