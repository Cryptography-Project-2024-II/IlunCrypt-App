package com.iluncrypt.iluncryptapp.models;

import com.iluncrypt.iluncryptapp.models.enums.Language;

/**
 * Represents the general settings of the application.
 */
public class ApplicationSettings {
    private Language language;
    private boolean showNotifications;
    private boolean darkMode;
    private boolean autoSave;

    /**
     * Application configuration builder.
     *
     * @param language          Application language.
     * @param showNotifications Indicates whether notifications are enabled.
     * @param darkMode          Indicates whether dark mode is enabled.
     * @param autoSave          Indicates whether settings are automatically saved.
     */
    public ApplicationSettings(Language language, boolean showNotifications, boolean darkMode, boolean autoSave) {
        this.language = language;
        this.showNotifications = showNotifications;
        this.darkMode = darkMode;
        this.autoSave = autoSave;
    }

    /**
     * Gets the language of the application.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the language of the application.
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Indicates whether notifications are enabled.
     */
    public boolean isShowNotifications() {
        return showNotifications;
    }

    /**
     * Turn notifications on or off.
     */
    public void setShowNotifications(boolean showNotifications) {
        this.showNotifications = showNotifications;
    }

    /**
     * Indicates whether dark mode is enabled.
     */
    public boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Turn dark mode on or off.
     */
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    /**
     * Indicates whether auto-save settings is enabled.
     */
    public boolean isAutoSave() {
        return autoSave;
    }

    /**
     * Enables or disables auto-saving of settings.
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    @Override
    public String toString() {
        return "ApplicationSettings{" +
                "language=" + language +
                ", showNotifications=" + showNotifications +
                ", darkMode=" + darkMode +
                ", autoSave=" + autoSave +
                '}';
    }
}
