package com.iluncrypt.iluncryptapp.controllers.publickey.elgamal;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.ElGamalConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.publickey.ElGamalManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for ElGamal encryption.
 */
public class ElGamalController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;
    private ElGamalConfig config = new ElGamalConfig();

    private String lastPlainText = "";
    private String lastCipherText = "";
    private String lastPublicKey = "";
    private String lastPrivateKey = "";

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldPublicKey;

    @FXML
    private MFXTextField textFieldPrivateKey;

    public ElGamalController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
        this.changeMethodDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureDialogs();
    }

    private void configureDialogs() {
        infoDialog.setOwnerNode(grid);
        changeMethodDialog.setOwnerNode(grid);
        errorDialog.setOwnerNode(grid);
    }

    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("PUBLIC-KEY-ENCRYPTION");
    }

    @FXML
    private void showInfoDialog() {
        infoDialog.showInfoDialog(
                "ElGamal Encryption",
                "ElGamal is an asymmetric encryption algorithm based on discrete logarithms."
        );
    }

    @FXML
    private void cipherText() {
        // Implement ElGamal encryption logic
        try {
            validateInputs();
            String cipherText = ElGamalManager.encryptText(
                    textAreaPlainText.getText(),
                    textFieldPublicKey.getText(),
                    config
            );
            textAreaCipherText.setText(cipherText);
        } catch (Exception e) {
            showError("Encryption Error", e.getMessage());
        }
    }

    @FXML
    private void decipherText() {
        // Implement ElGamal decryption logic
        try {
            validateInputs();
            String plainText = ElGamalManager.decryptText(
                    textAreaCipherText.getText(),
                    textFieldPublicKey.getText(),
                    textFieldPrivateKey.getText(),
                    config
            );
            textAreaPlainText.setText(plainText);
        } catch (Exception e) {
            showError("Decryption Error", e.getMessage());
        }
    }

    private void validateInputs() throws Exception {
        if (textFieldPublicKey.getText().isEmpty()) {
            ElGamalManager.KeyPair pair = ElGamalManager.generateKeyPair(config);
            textFieldPublicKey.setText(pair.publicKey);
            textFieldPrivateKey.setText(pair.privateKey);
        }

        // Basic format validation
        if (textFieldPublicKey.getText().split(",").length != 3) {
            throw new Exception("Invalid public key format. Use: p,g,h");
        }

        if (textFieldPrivateKey.getText().isEmpty()) {
            throw new Exception("Private key required for decryption");
        }
    }
    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    @Override
    public void saveCurrentState() {
        lastPlainText = textAreaPlainText.getText();
        lastCipherText = textAreaCipherText.getText();
        lastPublicKey = textFieldPublicKey.getText();
        lastPrivateKey = textFieldPrivateKey.getText();
    }

    @Override
    public void restorePreviousState() {
        textAreaPlainText.setText(lastPlainText);
        textAreaCipherText.setText(lastCipherText);
        textFieldPublicKey.setText(lastPublicKey);
        textFieldPrivateKey.setText(lastPrivateKey);
    }

    @Override
    public void switchEncryptionMethod(String methodView) {
        saveCurrentState();
        IlunCryptController.getInstance().loadView(methodView);
        restorePreviousState();
    }

    @Override
    public void closeDialog(DialogHelper dialog) {

    }

    @Override
    public void setConfig(CryptosystemConfig config) {

    }


    public void showChangeMethodDialog(ActionEvent actionEvent) {
    }

    public void importPlainText(ActionEvent actionEvent) {
    }

    public void copyPlainText(ActionEvent actionEvent) {
    }

    public void showOtherSettings(ActionEvent actionEvent) {
    }

    public void exportEncryptedText(ActionEvent actionEvent) {
    }

    public void clearTextAreas(ActionEvent actionEvent) {
    }

    public void showCryptanalysisDialog(ActionEvent actionEvent) {
    }

    public void decrementA(ActionEvent actionEvent) {
    }

    public void incrementA(ActionEvent actionEvent) {
    }

    public void decrementB(ActionEvent actionEvent) {
    }

    public void incrementB(ActionEvent actionEvent) {
    }

    public void importCipherText(ActionEvent actionEvent) {
    }

    public void copyCipherText(ActionEvent actionEvent) {

    }
}