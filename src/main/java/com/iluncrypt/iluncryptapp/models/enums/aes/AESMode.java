package com.iluncrypt.iluncryptapp.models.enums.aes;

public enum AESMode {
    ECB("ECB", false, false),
    CBC("CBC", true, false),
    CFB("CFB", true, false),
    OFB("OFB", true, false),
    CTR("CTR", true, false),
    GCM("GCM", true, true); // GCM usa IV y autenticación

    private final String mode;
    private final boolean requiresIV;
    private final boolean usesGCM;

    AESMode(String mode, boolean requiresIV, boolean usesGCM) {
        this.mode = mode;
        this.requiresIV = requiresIV;
        this.usesGCM = usesGCM;
    }

    public String getMode() {
        return mode;
    }

    public boolean requiresIV() {
        return requiresIV;
    }

    public boolean usesGCM() {
        return usesGCM;
    }
}
