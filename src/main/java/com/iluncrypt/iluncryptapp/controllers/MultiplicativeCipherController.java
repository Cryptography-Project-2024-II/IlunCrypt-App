package com.iluncrypt.iluncryptapp.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
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

import java.net.URL;
import java.util.ResourceBundle;

public class MultiplicativeCipherController implements Initializable {

    // FXML bindings
    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldKey;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureGridGrowth();
    }

    /**
     * Configures the properties of the GridPane for responsive behavior.
     */
    private void configureGridGrowth() {
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setMaxHeight(Double.MAX_VALUE);

        StackPane.setAlignment(grid, Pos.CENTER);

        grid.getRowConstraints().forEach(row -> row.setVgrow(Priority.ALWAYS));
        grid.getColumnConstraints().forEach(column -> column.setHgrow(Priority.ALWAYS));
    }

    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("ENCRYPT-DECRYPT-OPTIONS");
    }

    @FXML
    private void showInfoDialog(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Multiplicative Cipher Information");
        alert.setHeaderText("About the Multiplicative Cipher");
        alert.setContentText("The Multiplicative Cipher is a simple encryption method based on modular arithmetic.");
        alert.showAndWait();
    }

    @FXML
    private void showChangeMethodDialog() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Change Encryption Method");
        alert.setHeaderText("Choose a new encryption method.");
        alert.setContentText("This will replace the current method.");
        alert.showAndWait();
    }

    @FXML
    private void clearTextAreas() {
        textAreaPlainText.clear();
        textAreaCipherText.clear();
        System.out.println("Text areas cleared.");
    }

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

    @FXML
    private void cipherText() {
        String plainText = textAreaPlainText.getText();
        if (plainText.isEmpty()) {
            System.out.println("No plain text to encrypt.");
            return;
        }
        int key = Integer.parseInt(textFieldKey.getText());
        String cipherText = multiplicativeEncrypt(plainText, key);
        textAreaCipherText.setText(cipherText);
    }

    @FXML
    private void decipherText() {
        String cipherText = textAreaCipherText.getText();
        if (cipherText.isEmpty()) {
            System.out.println("No cipher text to decrypt.");
            return;
        }
        int key = Integer.parseInt(textFieldKey.getText());
        String plainText = multiplicativeDecrypt(cipherText, key);
        textAreaPlainText.setText(plainText);
    }

    @FXML
    private void decrementKey() {
        int value = Integer.parseInt(textFieldKey.getText());
        if (value > 1) { // Keys for multiplicative cipher must be >1 and coprime with 26
            textFieldKey.setText(String.valueOf(value - 1));
        }
    }

    @FXML
    private void incrementKey() {
        int value = Integer.parseInt(textFieldKey.getText());
        if (value < 25) { // Simplified range for this example
            textFieldKey.setText(String.valueOf(value + 1));
        }
    }

    private String multiplicativeEncrypt(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                result.append((char) ((key * (c - base) % 26) + base));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String multiplicativeDecrypt(String text, int key) {
        int inverseKey = multiplicativeInverse(key, 26);
        if (inverseKey == -1) {
            System.out.println("Invalid key: no multiplicative inverse exists.");
            return "Error";
        }
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                result.append((char) ((inverseKey * (c - base) % 26 + 26) % 26 + base));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private int multiplicativeInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1;
    }

    public void exportEncryptedMessage(ActionEvent actionEvent) {
        System.out.println("Exporting encrypted message...");
    }

    public void showOtherSettings(ActionEvent actionEvent) {
        System.out.println("Opening other settings...");
    }
}
