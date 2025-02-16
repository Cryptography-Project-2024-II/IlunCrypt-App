package com.iluncrypt.iluncryptapp.controllers.cryptanalysis.frequency;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for Frequency Analysis Cryptanalysis.
 */
public class FrequencyAnalysisController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final Stage stage;

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private TextArea textAreaResults;

    public FrequencyAnalysisController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoDialog.setOwnerNode(grid);
    }

    @FXML
    private void analyzeFrequency() {
        String cipherText = textAreaCipherText.getText();
        if (cipherText.isEmpty()) {
            infoDialog.showInfoDialog("Error", "Cipher text cannot be empty.");
            return;
        }

        // Perform frequency analysis
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : cipherText.toCharArray()) {
            if (Character.isLetter(c)) {
                frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            }
        }

        StringBuilder result = new StringBuilder("Letter Frequencies:\n");
        frequencyMap.forEach((key, value) -> result.append(key).append(": ").append(value).append("\n"));

        textAreaResults.setText(result.toString());
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
