package com.iluncrypt.iluncryptapp.controllers;

/**
 * Common interface for all cipher controllers.
 * Ensures they can restore previous encryption settings when switching methods.
 */
public interface CipherController {

    /**
     * Saves the current encryption state before switching methods.
     */
    void saveCurrentState();

    /**
     * Restores the previous encryption state after switching methods.
     */
    void restorePreviousState();

    /**
     * Switches to a new encryption method.
     *
     * @param methodView The encryption method view to load.
     */
    void switchEncryptionMethod(String methodView);
}
