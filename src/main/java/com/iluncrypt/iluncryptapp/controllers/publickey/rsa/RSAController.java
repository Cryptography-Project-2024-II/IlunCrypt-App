package com.iluncrypt.iluncryptapp.controllers.publickey.rsa;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for RSA encryption.
 */
public class RSAController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;

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
        IlunCryptController.getInstance().loadView("ENCRYPT-DECRYPT-OPTIONS");
    }

    @FXML
    private void showInfoDialog() {
        infoDialog.showInfoDialog(
                "RSA Encryption",
                "RSA (Rivest-Shamir-Adleman) is an asymmetric encryption algorithm that uses a public and a private key for secure communication."
        );
    }

    @FXML
    private void cipherText() {
        // Implement RSA encryption logic
    }

    @FXML
    private void decipherText() {
        // Implement RSA decryption logic
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
    public void closeOptionsDialog() {
        changeMethodDialog.closeDialog();
    }
}
