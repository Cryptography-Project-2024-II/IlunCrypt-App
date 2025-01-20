package com.iluncrypt.iluncryptapp.models.enums;

import java.util.Locale;

public enum Language {
    ENGLISH("English", "en", Locale.ENGLISH),
    SPANISH("Español", "es", new Locale("es")),
    FRENCH("Français", "fr", Locale.FRENCH),
    PORTUGUESE("Português", "pt", new Locale("pt"));

    private final String displayName;
    private final String code;
    private final Locale locale;

    Language(String displayName, String code, Locale locale) {
        this.displayName = displayName;
        this.code = code;
        this.locale = locale;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * Get a Language enum from a string display name.
     */
    public static Language fromDisplayName(String name) {
        for (Language lang : values()) {
            if (lang.displayName.equalsIgnoreCase(name)) {
                return lang;
            }
        }
        return ENGLISH; // Default to English
    }

    /**
     * Get a Language enum from a language code.
     */
    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        return ENGLISH; // Default to English
    }
}
