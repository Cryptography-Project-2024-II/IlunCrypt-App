package com.iluncrypt.iluncryptapp.controllers.cryptanalysis.friedman;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Friedman Test Cryptanalysis.
 */
public class FriedmanTestController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final Stage stage;

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private TextArea textAreaResults;

    public FriedmanTestController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoDialog.setOwnerNode(grid);
    }

    @FXML
    private void performFriedmanTest() {
        String cipherText = textAreaCipherText.getText();
        if (cipherText.isEmpty()) {
            infoDialog.showInfoDialog("Error", "Cipher text cannot be empty.");
            return;
        }

        // Perform Friedman test (example implementation)
        double indexOfCoincidence = calculateIndexOfCoincidence(cipherText);
        double estimatedKeyLength = (0.027 * cipherText.length()) / ((0.065 - indexOfCoincidence) + cipherText.length() * (indexOfCoincidence - 0.038));

        textAreaResults.setText("Estimated key length: " + String.format("%.2f", estimatedKeyLength));
    }

    private double calculateIndexOfCoincidence(String text) {
        int[] frequencies = new int[26];
        int total = 0;

        for (char c : text.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                frequencies[c - 'A']++;
                total++;
            }
        }

        double ic = 0.0;
        for (int freq : frequencies) {
            ic += (freq * (freq - 1));
        }

        return ic / (total * (total - 1));
    }

    @Override
    public void saveCurrentState() {}

    @Override
    public void restorePreviousState() {}

    @Override
    public void switchEncryptionMethod(String methodView) {}

    @Override
    public void closeOptionsDialog() {}
}
