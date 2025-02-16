package com.iluncrypt.iluncryptapp.controllers.symmetrickey.sdes;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Simplified DES (SDES) encryption system.
 * This class manages encryption, decryption, and UI interactions for SDES.
 */
public class SDESController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;

    // Stores the last entered values when switching methods
    private String lastPlainText = "";
    private String lastCipherText = "";
    private String lastKey = "0000000000"; // Default 10-bit key

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldKey;

    /**
     * Constructor for SDESController.
     *
     * @param stage The primary stage used for managing dialogs.
     */
    public SDESController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
        this.changeMethodDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
    }

    /**
     * Initializes the controller and configures the UI layout.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureDialogs();
    }

    /**
     * Configures the dialogs.
     */
    private void configureDialogs() {
        infoDialog.setOwnerNode(grid);
        changeMethodDialog.setOwnerNode(grid);
        errorDialog.setOwnerNode(grid);
    }

    /**
     * Navigates back to the encryption method selection menu.
     */
    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("ENCRYPT-DECRYPT-OPTIONS");
    }

    /**
     * Displays an informational dialog about the SDES algorithm.
     */
    @FXML
    private void showInfoDialog() {
        infoDialog.showInfoDialog(
                "Simplified DES",
                """
                Simplified DES (SDES) is a reduced version of the DES algorithm used for teaching.
                
                - Uses a **10-bit key**.
                - Works on **8-bit plaintext blocks**.
                - Includes key generation, permutation, substitution, and XOR operations.
                """
        );
    }

    /**
     * Encrypts the plaintext using SDES.
     */
    @FXML
    private void cipherText() {
        String plainText = textAreaPlainText.getText();
        String key = textFieldKey.getText();

        if (!plainText.isEmpty() && isValidKey(key)) {
            try {
                //SDESCipher sdes = new SDESCipher(key);
                //textAreaCipherText.setText(sdes.encrypt(plainText));
            } catch (IllegalArgumentException e) {
                errorDialog.showInfoDialog("Encryption Error", e.getMessage());
            }
        } else {
            errorDialog.showInfoDialog("Error", "Invalid input or key.");
        }
    }

    /**
     * Decrypts the ciphertext using SDES.
     */
    @FXML
    private void decipherText() {
        String cipherText = textAreaCipherText.getText();
        String key = textFieldKey.getText();

        if (!cipherText.isEmpty() && isValidKey(key)) {
            try {
                //SDESCipher sdes = new SDESCipher(key);
                //textAreaPlainText.setText(sdes.decrypt(cipherText));
            } catch (IllegalArgumentException e) {
                errorDialog.showInfoDialog("Decryption Error", e.getMessage());
            }
        } else {
            errorDialog.showInfoDialog("Error", "Invalid input or key.");
        }
    }

    /**
     * Validates that the key is a 10-bit binary string.
     */
    private boolean isValidKey(String key) {
        return key.matches("[01]{10}");
    }

    /**
     * Clears the text areas.
     */
    @FXML
    private void clearTextAreas() {
        textAreaPlainText.clear();
        textAreaCipherText.clear();
    }

    @FXML
    private void showChangeMethodDialog() {
        changeMethodDialog.showInfoDialog("Method Change", "Select another encryption method.");
    }

    /**
     * Saves the current state.
     */
    @Override
    public void saveCurrentState() {
        lastPlainText = textAreaPlainText.getText();
        lastCipherText = textAreaCipherText.getText();
        lastKey = textFieldKey.getText();
    }

    /**
     * Restores the previous state.
     */
    @Override
    public void restorePreviousState() {
        textAreaPlainText.setText(lastPlainText);
        textAreaCipherText.setText(lastCipherText);
        textFieldKey.setText(lastKey);
    }

    /**
     * Switches the encryption method.
     */
    @Override
    public void switchEncryptionMethod(String methodView) {
        saveCurrentState();
        IlunCryptController.getInstance().loadView(methodView);
        restorePreviousState();
    }

    /**
     * Closes any open dialogs related to encryption settings.
     */
    @Override
    public void closeOptionsDialog() {
        changeMethodDialog.closeDialog();
    }
}
