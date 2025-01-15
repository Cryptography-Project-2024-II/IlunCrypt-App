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

public class ShiftCipherController implements Initializable {

    // FXML bindings
    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldKey;

    @FXML
    private MFXButton btnInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureGridGrowth();
    }

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
        alert.setTitle("Shift Cipher Information");
        alert.setHeaderText("About the Shift (Caesar) Cipher");
        alert.setContentText("The Shift Cipher is a substitution cipher that shifts the letters of the alphabet by a fixed number of positions.");
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
        String cipherText = shiftEncrypt(plainText, key);
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
        String plainText = shiftDecrypt(cipherText, key);
        textAreaPlainText.setText(plainText);
    }

    @FXML
    private void decrementKey() {
        int value = Integer.parseInt(textFieldKey.getText());
        if (value > 0) {
            textFieldKey.setText(String.valueOf(value - 1));
        }
    }

    @FXML
    private void incrementKey() {
        int value = Integer.parseInt(textFieldKey.getText());
        if (value < 25) {
            textFieldKey.setText(String.valueOf(value + 1));
        }
    }

    private String shiftEncrypt(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                result.append((char) ((c - base + key) % 26 + base));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String shiftDecrypt(String text, int key) {
        return shiftEncrypt(text, 26 - key);
    }

    public void exportEncryptedMessage(ActionEvent actionEvent) {
    }

    public void openOtherSettingsDialog(ActionEvent actionEvent) {
    }
}
