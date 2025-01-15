package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.utils.Dialog;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class VigenereCipherController implements Initializable {

    // FXML bindings
    @FXML
    private GridPane grid; // Reference to the GridPane from FXML

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldA;

    @FXML
    private MFXTextField textFieldB;

    @FXML
    private MFXButton btnInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureGridGrowth();
    }

    /**
     * Configures the properties of the GridPane for responsive behavior.
     * Ensures the GridPane grows dynamically within its parent container.
     */
    private void configureGridGrowth() {
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setMaxHeight(Double.MAX_VALUE);

        // Ensures the GridPane is centered within the StackPane
        StackPane.setAlignment(grid, Pos.CENTER);

        // Configures row and column constraints for dynamic resizing
        grid.getRowConstraints().forEach(row -> row.setVgrow(Priority.ALWAYS));
        grid.getColumnConstraints().forEach(column -> column.setHgrow(Priority.ALWAYS));
    }

    /**
     * Handles the action for the back button.
     * Navigates back to the menu of encryption methods.
     */
    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("ENCRYPT-DECRYPT-OPTIONS");;
    }

    /**
     * Displays a dialog with information about the Affine Cipher.
     */
    @FXML
    private void showInfoDialog(ActionEvent event) {
    }

    /**
     * Displays a dialog to change the encryption method.
     */
    @FXML
    private void showChangeMethodDialog() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Change Encryption Method");
        alert.setHeaderText("Choose a new encryption method.");
        alert.setContentText("This will replace the current method.");
        alert.showAndWait();
    }

    /**
     * Clears the content of the plain text and cipher text areas.
     */
    @FXML
    private void clearTextAreas() {
        textAreaPlainText.clear();
        textAreaCipherText.clear();
        System.out.println("Text areas cleared.");
    }

    /**
     * Displays a confirmation dialog for cryptanalysis.
     * Checks if there is cipher text before proceeding.
     */
    @FXML
    private void showCryptanalysisDialog() {
        if (textAreaCipherText.getText().isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Cryptanalysis Error");
            alert.setHeaderText("No Cipher Text Found");
            alert.setContentText("Please enter cipher text to perform cryptanalysis.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Perform Cryptanalysis");
            alert.setHeaderText("Confirm Cryptanalysis");
            alert.setContentText("Proceed with cryptanalysis on the cipher text?");
            alert.showAndWait();
        }
    }

    /**
     * Encrypts the plain text and displays the result in the cipher text area.
     */
    @FXML
    private void cipherText() {
        String plainText = textAreaPlainText.getText();
        if (plainText.isEmpty()) {
            System.out.println("No plain text to encrypt.");
            return;
        }
        int a = Integer.parseInt(textFieldA.getText());
        int b = Integer.parseInt(textFieldB.getText());
        String cipherText = affineEncrypt(plainText, a, b);
        textAreaCipherText.setText(cipherText);
    }

    /**
     * Decrypts the cipher text and displays the result in the plain text area.
     */
    @FXML
    private void decipherText() {
        String cipherText = textAreaCipherText.getText();
        if (cipherText.isEmpty()) {
            System.out.println("No cipher text to decrypt.");
            return;
        }
        int a = Integer.parseInt(textFieldA.getText());
        int b = Integer.parseInt(textFieldB.getText());
        String plainText = affineDecrypt(cipherText, a, b);
        textAreaPlainText.setText(plainText);
    }

    /**
     * Decreases the value of A, ensuring it stays within bounds.
     */
    @FXML
    private void decrementA() {
        int value = Integer.parseInt(textFieldA.getText());
        if (value > 0) {
            textFieldA.setText(String.valueOf(value-1));
        }
    }

    /**
     * Increases the value of A, ensuring it stays within bounds.
     */
    @FXML
    private void incrementA() {
        int value = Integer.parseInt(textFieldA.getText());
        if (value < 25) {
            textFieldA.setText(String.valueOf(value+1));
        }
    }

    /**
     * Decreases the value of B, ensuring it stays within bounds.
     */
    @FXML
    private void decrementB() {
        int value = Integer.parseInt(textFieldB.getText());
        if (value > 0) {
            textFieldB.setText(String.valueOf(value-1));
        }
    }

    /**
     * Increases the value of B, ensuring it stays within bounds.
     */
    @FXML
    private void incrementB() {
        int value = Integer.parseInt(textFieldB.getText());
        if (value < 25) {
            textFieldB.setText(String.valueOf(value+1));
        }
    }

    /**
     * Encrypts the text using the Affine Cipher algorithm.
     */
    private String affineEncrypt(String plainText, int a, int b) {
        // Example encryption logic (placeholder)
        return "EncryptedText"; // Replace with real implementation
    }

    /**
     * Decrypts the text using the Affine Cipher algorithm.
     */
    private String affineDecrypt(String cipherText, int a, int b) {
        // Example decryption logic (placeholder)
        return "DecryptedText"; // Replace with real implementation
    }

    public void exportEncryptedMessage(ActionEvent actionEvent) {
        System.out.println("Exporting encrypted message...");
    }

    public void showOtherSettings(ActionEvent actionEvent) {
        System.out.println("Opening other settings...");
    }
}
