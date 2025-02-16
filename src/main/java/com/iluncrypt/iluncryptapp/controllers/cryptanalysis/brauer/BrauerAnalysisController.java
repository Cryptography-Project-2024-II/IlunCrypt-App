package com.iluncrypt.iluncryptapp.controllers.cryptanalysis.brauer;

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
 * Controller for Brauer Cryptanalysis.
 */
public class BrauerAnalysisController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final Stage stage;

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private TextArea textAreaResults;

    public BrauerAnalysisController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoDialog.setOwnerNode(grid);
    }

    @FXML
    private void analyzeText() {
        String cipherText = textAreaCipherText.getText();
        if (cipherText.isEmpty()) {
            infoDialog.showInfoDialog("Error", "Cipher text cannot be empty.");
            return;
        }

        // Implement Brauer cryptanalysis logic here
        textAreaResults.setText("Brauer analysis results: (example output)");
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
