package com.iluncrypt.iluncryptapp.controllers.publickey.rsa;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.RSAConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.publickey.RSAManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.net.URL;
import java.util.ResourceBundle;

public class RSAController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;
    private RSAConfig rsaConfig = new RSAConfig();
    private KeyPair currentKeyPair;

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

    public RSAController(Stage stage) {
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
                "RSA Encryption",
                "RSA (Rivest-Shamir-Adleman) is an asymmetric encryption algorithm that uses a public and a private key for secure communication."
        );
    }

    public void encrypt(ActionEvent actionEvent) {
        try {
            // Auto-generate keys if public key field is empty
            if (textFieldPublicKey.getText().isEmpty()) {
                generateKeyPair();
            }

            PublicKey publicKey = parsePublicKey(textFieldPublicKey.getText());
            String cipherText = RSAManager.encryptText(
                    textAreaPlainText.getText(),
                    publicKey,
                    rsaConfig
            );
            textAreaCipherText.setText(cipherText);
        } catch (Exception e) {
            showError("Encryption Failed", e.getMessage());
        }
    }

    public void decrypt(ActionEvent actionEvent) {
        try {
            // Don't auto-generate for decryption - private key must be provided
            if (textFieldPrivateKey.getText().isEmpty()) {
                throw new IllegalArgumentException("Private key required for decryption");
            }

            PrivateKey privateKey = parsePrivateKey(textFieldPrivateKey.getText());
            String plainText = RSAManager.decryptText(
                    textAreaCipherText.getText(),
                    privateKey,
                    rsaConfig
            );
            textAreaPlainText.setText(plainText);
        } catch (Exception e) {
            showError("Decryption Failed", e.getMessage());
        }
    }

    private void generateKeyPair() {
        try {
            currentKeyPair = RSAManager.generateKeyPair(rsaConfig);
            textFieldPublicKey.setText(Base64.getEncoder()
                    .encodeToString(currentKeyPair.getPublic().getEncoded()));
            textFieldPrivateKey.setText(Base64.getEncoder()
                    .encodeToString(currentKeyPair.getPrivate().getEncoded()));
        } catch (Exception e) {
            showError("Key Generation Error", e.getMessage());
            throw new RuntimeException("Failed to generate keys");
        }
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
        dialog.closeDialog();
    }

    @Override
    public void setConfig(CryptosystemConfig config) {
        if (config instanceof RSAConfig) {
            this.rsaConfig = (RSAConfig) config;
        }
    }

    private PublicKey parsePublicKey(String base64Key) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private PrivateKey parsePrivateKey(String base64Key) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Remaining methods maintained in original order
    public void showChangeMethodDialog(ActionEvent actionEvent) {}
    public void importPlainText(ActionEvent actionEvent) {}

    public void copyPlainText(ActionEvent actionEvent) {
        copyToClipboard(textAreaPlainText.getText());
    }

    public void showOtherSettings(ActionEvent actionEvent) {}
    public void exportEncryptedText(ActionEvent actionEvent) {}

    public void clearTextAreas(ActionEvent actionEvent) {
        textAreaPlainText.clear();
        textAreaCipherText.clear();
    }

    public void showCryptanalysisDialog(ActionEvent actionEvent) {}
    public void decrementA(ActionEvent actionEvent) {}
    public void incrementA(ActionEvent actionEvent) {}
    public void decrementB(ActionEvent actionEvent) {}
    public void incrementB(ActionEvent actionEvent) {}
    public void importCipherText(ActionEvent actionEvent) {}

    public void copyCipherText(ActionEvent actionEvent) {
        copyToClipboard(textAreaCipherText.getText());
    }

    public void clearAll(ActionEvent actionEvent) {
        textAreaPlainText.clear();
        textAreaCipherText.clear();
        textFieldPublicKey.clear();
        textFieldPrivateKey.clear();
    }

    public void showAdvancedOptions(ActionEvent actionEvent) {}
    public void exportEncryptedInformation(ActionEvent actionEvent) {}

    private void copyToClipboard(String content) {
        if (!content.isEmpty()) {
            javafx.scene.input.Clipboard clipboard =
                    javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent cc =
                    new javafx.scene.input.ClipboardContent();
            cc.putString(content);
            clipboard.setContent(cc);
        }
    }
}