package com.iluncrypt.iluncryptapp.controllers;

import javafx.event.ActionEvent;

/**
 * Controller for handling encryption method selection from the change method dialog.
 */
public class EncryptDecryptOptionsDialogController {

    private CipherController parentController; // Generic reference to any cipher controller

    /**
     * Sets the parent controller to enable data transfer.
     *
     * @param parentController Any controller that implements CipherController.
     */
    public void setParentController(CipherController parentController) {
        this.parentController = parentController;
    }

    /**
     * Handles selection of the Affine Cipher method.
     */
    public void handleAffineCipher(ActionEvent actionEvent) {
        changeMethod("AFFINE-CIPHER");
    }

    /**
     * Handles selection of the Multiplicative Cipher method.
     */
    public void handleMultiplicativeCipher(ActionEvent actionEvent) {
        changeMethod("MULTIPLICATIVE-CIPHER");
    }

    /**
     * Handles selection of the Shift Cipher method.
     */
    public void handleShiftCipher(ActionEvent actionEvent) {
        changeMethod("SHIFT-CIPHER");
    }

    /**
     * Handles selection of the Hill Cipher method.
     */
    public void handleHillCipher(ActionEvent actionEvent) {
        changeMethod("HILL-CIPHER");
    }

    /**
     * Handles selection of the Permutation Cipher method.
     */
    public void handlePermutationCipher(ActionEvent actionEvent) {
        changeMethod("PERMUTATION-CIPHER");
    }

    /**
     * Handles selection of the Substitution Cipher method.
     */
    public void handleSubstitutionCipher(ActionEvent actionEvent) {
        changeMethod("SUBSTITUTION-CIPHER");
    }

    /**
     * Handles selection of the Vigenère Cipher method.
     */
    public void handleVigenereCipher(ActionEvent actionEvent) {
        changeMethod("VIGENERE-CIPHER");
    }

    /**
     * Changes the encryption method, updates the main view, and restores previous data.
     *
     * @param methodView The encryption method view to load.
     */
    private void changeMethod(String methodView) {
        if (parentController != null) {
            parentController.switchEncryptionMethod(methodView);
        }
    }
}
