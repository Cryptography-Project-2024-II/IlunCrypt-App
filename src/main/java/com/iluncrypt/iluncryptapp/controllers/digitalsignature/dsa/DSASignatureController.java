package com.iluncrypt.iluncryptapp.controllers.digitalsignature.dsa;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.DSAConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.publickey.DSAManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * Controller for DSA digital signature.
 */
public class DSASignatureController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;
    private DSAConfig DSAConfig = new DSAConfig();
    private KeyPair currentKeyPair;
    private File selectedFile;

    @FXML
    private MFXTextField textFileName;

    @FXML
    private GridPane grid;

    @FXML
    private Label lblStatus;

    @FXML
    private TextArea textAreaSignature;

    @FXML
    private MFXTextField textFieldPrivateKey;

    @FXML
    private MFXTextField textFieldPublicKey;

    public DSASignatureController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoDialog.setOwnerNode(grid);
        errorDialog.setOwnerNode(grid);

        // Initialize status
        lblStatus.setText("Ready to sign or verify files");
    }

    @FXML
    public void handleBackButton(ActionEvent actionEvent) {
        IlunCryptController.getInstance().loadView("DIGITAL-SIGNATURE");
    }

    @FXML
    public void showInfoDialog(ActionEvent actionEvent) {
        infoDialog.showInfoDialog(
                "DSA Digital Signature",
                "DSA Digital Signatures use the DSA algorithm to verify the authenticity and integrity of a file. " +
                        "The sender signs the file with their private key, and the recipient can verify the signature using the sender's public key."
        );
    }

    @FXML
    public void generateKeyPair(ActionEvent actionEvent) {
        try {
            currentKeyPair = DSAManager.generateKeyPair(DSAConfig);
            textFieldPublicKey.setText(Base64.getEncoder()
                    .encodeToString(currentKeyPair.getPublic().getEncoded()));
            textFieldPrivateKey.setText(Base64.getEncoder()
                    .encodeToString(currentKeyPair.getPrivate().getEncoded()));
            lblStatus.setText("Key pair generated successfully!");
        } catch (Exception e) {
            showError("Key Generation Error", e.getMessage());
        }
    }

    @FXML
    public void signData(ActionEvent actionEvent) {
        try {
            if (selectedFile == null) {
                throw new IllegalArgumentException("Please select a file to sign");
            }

            // If private key is empty, generate a new key pair
            if (textFieldPrivateKey.getText().isEmpty()) {
                generateKeyPair(null);
            }

            PrivateKey privateKey = parsePrivateKey(textFieldPrivateKey.getText());
            String signature = DSAManager.signFile(selectedFile, privateKey);
            textAreaSignature.setText(signature);
            lblStatus.setText("File signed successfully! To verify: share the file, signature, and public key with the recipient.");
        } catch (Exception e) {
            showError("Signature Creation Failed", e.getMessage());
        }
    }

    @FXML
    public void verifySignature(ActionEvent actionEvent) {
        try {
            if (selectedFile == null) {
                throw new IllegalArgumentException("Please select a file to verify");
            }

            if (textAreaSignature.getText().isEmpty()) {
                throw new IllegalArgumentException("Please provide a signature to verify");
            }

            if (textFieldPublicKey.getText().isEmpty()) {
                throw new IllegalArgumentException("Please provide a public key to verify the signature");
            }

            PublicKey publicKey = parsePublicKey(textFieldPublicKey.getText());
            boolean isValid = DSAManager.verifyFileSignature(
                    selectedFile,
                    textAreaSignature.getText(),
                    publicKey
            );

            if (isValid) {
                lblStatus.setText("SIGNATURE VERIFIED ✓ - The file is authentic and has not been modified.");
            } else {
                lblStatus.setText("INVALID SIGNATURE ✗ - The file may have been tampered with or the wrong public key was used.");
            }
        } catch (Exception e) {
            showError("Signature Verification Failed", e.getMessage());
        }
    }

    @FXML
    public void importFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            selectedFile = file;
            textFileName.setText(file.getName());
            lblStatus.setText("File selected: " + file.getAbsolutePath() +
                    "\nSize: " + (file.length() / 1024) + " KB");
        }
    }

    @FXML
    public void copySignature(ActionEvent actionEvent) {
        copyToClipboard(textAreaSignature.getText());
    }

    private void copyToClipboard(String content) {
        if (!content.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent cc = new ClipboardContent();
            cc.putString(content);
            clipboard.setContent(cc);
            lblStatus.setText("Copied to clipboard!");
        }
    }

    @FXML
    public void clearTextAreas(ActionEvent actionEvent) {
        textAreaSignature.clear();
        lblStatus.setText("Cleared signature and status.");
    }

    private PublicKey parsePublicKey(String base64Key) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("DSA").generatePublic(spec);
    }

    private PrivateKey parsePrivateKey(String base64Key) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("DSA").generatePrivate(spec);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Required interface methods
    @Override
    public void saveCurrentState() {
        // Not needed for this implementation
    }

    @Override
    public void restorePreviousState() {
        // Not needed for this implementation
    }

    @Override
    public void switchEncryptionMethod(String methodView) {
        // Not needed for this implementation
    }

    @Override
    public void closeDialog(DialogHelper dialog) {
        dialog.closeDialog();
    }

    @Override
    public void setConfig(CryptosystemConfig config) {
        if (config instanceof DSAConfig) {
            this.DSAConfig = (DSAConfig) config;
        }
    }

    // Stubs for other action handlers
    public void showChangeMethodDialog(ActionEvent actionEvent) {
        // Implement if needed
    }
}