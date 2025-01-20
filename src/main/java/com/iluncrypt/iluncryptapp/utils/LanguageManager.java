package com.iluncrypt.iluncryptapp.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static LanguageManager instance;
    private ResourceBundle bundle;
    private Locale currentLocale;

    private LanguageManager() {
        this.currentLocale = Locale.ENGLISH;  // Idioma predeterminado
        this.bundle = ResourceBundle.getBundle("com.iluncrypt.iluncryptapp.locales.messages", currentLocale);
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public void setLanguage(String languageCode) {
        this.currentLocale = new Locale(languageCode);
        this.bundle = ResourceBundle.getBundle("com.iluncrypt.iluncryptapp.locales.messages", currentLocale);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }
}
