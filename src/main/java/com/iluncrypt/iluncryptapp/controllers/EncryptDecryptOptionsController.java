package com.iluncrypt.iluncryptapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

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

    @FXML
    private Label contentLabel;

    @FXML
    private void handleHoverAffine() {
        contentLabel.setText("Affine Cipher: Description...");
    }

    @FXML
    private void handleHoverMultiplicative() {
        contentLabel.setText("Multiplicative Cipher: Description...");
    }

    public void handleHoverShift(MouseEvent mouseEvent) {
        contentLabel.setText("Shift Cipher: Description...");
    }

    public void handleHoverHill(MouseEvent mouseEvent) {
        contentLabel.setText("Hill Cipher: Description...");
    }

    public void handleHoverPermutation(MouseEvent mouseEvent) {
        contentLabel.setText("Permutation Cipher: Description...");
    }

    public void handleHoverSubstitution(MouseEvent mouseEvent) {
        contentLabel.setText("Substitution Cipher: Description...");
    }

    public void handleHoverVigenere(MouseEvent mouseEvent) {
        contentLabel.setText("Vigenère Cipher: Description...");
    }

    public void handleFriedman(ActionEvent actionEvent) {
    }

    public void handleBrauer(ActionEvent actionEvent) {
    }

    public void handleBruteForce(ActionEvent actionEvent) {
    }

    public void handleFrequency(ActionEvent actionEvent) {
    }
}
