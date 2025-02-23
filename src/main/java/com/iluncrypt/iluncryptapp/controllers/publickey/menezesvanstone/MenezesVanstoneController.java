package com.iluncrypt.iluncryptapp.controllers.publickey.menezesvanstone;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.MenezesVanstoneConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.publickey.MenezesVanstoneManager;
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
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Menezes-Vanstone encryption.
 */
public class MenezesVanstoneController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;
    private MenezesVanstoneConfig config = new MenezesVanstoneConfig();

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

    public MenezesVanstoneController(Stage stage) {
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
                "Menezes-Vanstone Encryption",
                "Menezes-Vanstone is an asymmetric encryption algorithm based on elliptic curves, "
                        + "offering strong security with shorter key lengths compared to RSA."
        );
    }

    @FXML
    private void cipherText() {
        Platform.runLater(() -> {
            try {
                validateAndGenerateKeys();
                String cipherText = MenezesVanstoneManager.encrypt(
                        textAreaPlainText.getText(),
                        textFieldPublicKey.getText(),
                        config
                );
                textAreaCipherText.setText(cipherText);
            } catch (Exception e) {
                showError("Encryption Failed", e.getMessage());
            }
        });
    }

    @FXML
    private void decipherText() {
        Platform.runLater(() -> {
            try {
                String plainText = MenezesVanstoneManager.decrypt(
                        textAreaCipherText.getText(),
                        textFieldPrivateKey.getText(),
                        config
                );
                textAreaPlainText.setText(plainText);
            } catch (Exception e) {
                showError("Decryption Failed", e.getMessage());
            }
        });
    }

    private void validateAndGenerateKeys() throws Exception {
        if (textFieldPublicKey.getText().isEmpty() || textFieldPrivateKey.getText().isEmpty()) {
            MenezesVanstoneManager.KeyPair pair = MenezesVanstoneManager.generateKeyPair(config);
            textFieldPublicKey.setText(pair.publicKey());
            textFieldPrivateKey.setText(pair.privateKey());
        }
        validateKeyConsistency();
    }

    private void validateKeyConsistency() throws Exception {
        BigInteger d = new BigInteger(textFieldPrivateKey.getText(), 16);
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(config.getKeySize().getCurveName());

        ECPoint expectedQ = spec.getG().multiply(d).normalize();
        String actualQ = textFieldPublicKey.getText();

        if (!actualQ.equals(MenezesVanstoneManager.bytesToHex(expectedQ.getEncoded(true)))) {
            throw new Exception("Public/private key mismatch");
        }
    }
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
