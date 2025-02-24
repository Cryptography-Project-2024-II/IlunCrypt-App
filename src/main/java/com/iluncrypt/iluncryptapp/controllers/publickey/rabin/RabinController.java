package com.iluncrypt.iluncryptapp.controllers.publickey.rabin;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.models.RabinConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.publickey.RabinManager;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
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
import java.math.BigInteger;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for Rabin encryption.
 */
public class RabinController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;
    private RabinConfig config = new RabinConfig();

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

    @FXML
    private TextArea decryption1, decryption2, decryption3, decryption4;

    @FXML
    private GridPane decryptionGrid;

    public RabinController(Stage stage) {
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
                "Rabin Encryption",
                "Rabin is an asymmetric encryption algorithm based on the difficulty of integer factorization."
        );
    }

    @FXML
    private void cipherText() {
        try {
            validateAndGenerateKeys();
            String cipherText = RabinManager.encryptText(
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
        try {
            List<String> results = RabinManager.decryptText(
                    textAreaCipherText.getText(),
                    textFieldPrivateKey.getText(),
                    textFieldPublicKey.getText()
            );

            updateDecryptionFields(results);
        } catch (Exception e) {
            showError("Decryption Error", e.getMessage());
        }
    }

    private void updateDecryptionFields(List<String> results) {
        TextArea[] fields = {decryption1, decryption2, decryption3, decryption4};
        for(int i = 0; i < 4; i++) {
            if(i < results.size()) {
                fields[i].setText(results.get(i));
                fields[i].setDisable(false);
            } else {
                fields[i].clear();
                fields[i].setDisable(true);
            }
        }
    }

    @FXML
    private void selectDecryption1() { copyToPlainText(decryption1.getText()); }

    @FXML
    private void selectDecryption2() { copyToPlainText(decryption2.getText()); }

    @FXML
    private void selectDecryption3() { copyToPlainText(decryption3.getText()); }

    @FXML
    private void selectDecryption4() { copyToPlainText(decryption4.getText()); }

    private void copyToPlainText(String text) {
        if(!text.equals("Invalid padding")) {
            textAreaPlainText.setText(text);
        }
    }

    private void validateAndGenerateKeys() throws Exception {
        if (textFieldPublicKey.getText().isEmpty()) {
            generateKeyPair();
        }
        validateKeyConsistency();
    }

    private void generateKeyPair() throws Exception {
        RabinManager.KeyPair pair = RabinManager.generateKeyPair(config);
        textFieldPublicKey.setText(pair.publicKey);
        textFieldPrivateKey.setText(pair.privateKey);
    }

    private void validateKeyConsistency() throws Exception {
        BigInteger n = new BigInteger(textFieldPublicKey.getText());
        String[] primes = textFieldPrivateKey.getText().split(",");

        if (primes.length != 2) {
            throw new Exception("Private key must contain two primes separated by comma");
        }

        BigInteger p = new BigInteger(primes[0]);
        BigInteger q = new BigInteger(primes[1]);

        if (!p.multiply(q).equals(n)) {
            throw new Exception("Public/private key mismatch");
        }
    }


    @Override
    public void setConfig(CryptosystemConfig config) {
        if (config instanceof RabinConfig) {
            this.config = (RabinConfig) config;
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
