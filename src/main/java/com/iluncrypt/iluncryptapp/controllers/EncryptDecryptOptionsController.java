package com.iluncrypt.iluncryptapp.controllers;

import javafx.fxml.FXML;

public class EncryptDecryptOptionsController {

    @FXML
    private void handleAffineCipher() {
        IlunCryptController controller = getIlunCryptController();
        controller.loadView("AFFINE-CIPHER");
    }

    @FXML
    private void handleMultiplicativeCipher() {
        IlunCryptController controller = getIlunCryptController();
        controller.loadView("MULTIPLICATIVE-CIPHER");
    }

    @FXML
    private void handleShiftCipher() {
        IlunCryptController controller = getIlunCryptController();
        controller.loadView("SHIFT-CIPHER");
    }

    @FXML
    private void handleHillCipher() {
        IlunCryptController controller = getIlunCryptController();
        controller.loadView("HILL-CIPHER");
    }

    @FXML
    private void handlePermutationCipher() {
        IlunCryptController controller = getIlunCryptController();
        controller.loadView("PERMUTATION-CIPHER");
    }

    @FXML
    private void handleSubstitutionCipher() {
        IlunCryptController controller = getIlunCryptController();
        controller.loadView("SUBSTITUTION-CIPHER");
    }

    @FXML
    private void handleVigenereCipher() {
        IlunCryptController controller = getIlunCryptController();
        controller.loadView("VIGENERE-CIPHER");
    }

    /**
     * Obtiene una instancia del controlador principal.
     *
     * @return Controlador principal.
     */
    private IlunCryptController getIlunCryptController() {
        // Obtener el controlador principal según tu implementación (singleton, dependencia, etc.)
        return IlunCryptController.getInstance();
    }
}
