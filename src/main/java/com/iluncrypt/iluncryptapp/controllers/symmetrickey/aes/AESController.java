package com.iluncrypt.iluncryptapp.controllers.symmetrickey.aes;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.ContainerDialogController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.symmetrickey.AESManager;
import com.iluncrypt.iluncryptapp.models.SymmetricKeyConfig;
import com.iluncrypt.iluncryptapp.models.enums.symmetrickey.KeySize;
import com.iluncrypt.iluncryptapp.models.enums.symmetrickey.SymmetricKeyAlgorithm;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import com.iluncrypt.iluncryptapp.utils.config.ConfigManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for AES encryption.
 */
public class AESController implements CipherController, Initializable {

    private SymmetricKeyConfig aesConfig;
    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final DialogHelper advancedOptionsDialog;
    private final DialogHelper alertDialog;
    private final Stage stage;
    private boolean fileMode = false; // Flag to indicate file encryption/decryption mode
    private File noEncryptedFile;
    private File encryptedFile;
    private byte[] iv;

    @FXML
    private VBox boxIV;
    @FXML
    private GridPane grid;
    @FXML
    private TextArea textAreaPlainText, textAreaCipherText;
    @FXML
    private MFXTextField textFieldKey, textFieldIV;
    @FXML
    private MFXButton btnCopyPlainText, btnCopyCipherText, btnCopyKey, btnCopyIV;
    @FXML
    private MFXButton btnShuffleIV, btnDeleteIV, btnDownloadIV, btnUploadIV;
    @FXML
    private MFXButton btnShuffleKey, btnDownloadKey, btnUploadKey, btnDeleteKey;
    @FXML
    private MFXButton btnImportPlainText, btnImportCipherText;
    @FXML
    private MFXButton btnClearCipherText, btnClearPlainText;
    @FXML
    private Label lblMode;

    public AESController(Stage stage) {
        this.stage = stage;

        this.infoDialog = new DialogHelper(stage);
        this.changeMethodDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
        this.advancedOptionsDialog = new DialogHelper(stage);
        this.alertDialog = new DialogHelper(stage);

        this.aesConfig = ConfigManager.loadSymmetricKeyConfig("AES");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureDialogs();
        loadAESConfig();
        setupButtonActions();
    }

    private void loadAESConfig() {
        if (aesConfig == null) {
            throw new IllegalArgumentException("Failed to load AES configuration.");
        }
        lblMode.setText("Mode: "+aesConfig.getTransformation());

        boxIV.setVisible(aesConfig.isShowIV());
        boxIV.setManaged(aesConfig.isShowIV());
    }

    private void configureDialogs() {
        infoDialog.setOwnerNode(grid);
        changeMethodDialog.setOwnerNode(grid);
        errorDialog.setOwnerNode(grid);
        advancedOptionsDialog.setOwnerNode(grid);
        alertDialog.setOwnerNode(grid);
    }

    /**
     * Configures button actions for copying, regenerating, and importing files.
     */
    private void setupButtonActions() {
        btnCopyPlainText.setOnAction(e -> copyToClipboard(textAreaPlainText.getText()));
        btnCopyCipherText.setOnAction(e -> copyToClipboard(textAreaCipherText.getText()));
        btnCopyKey.setOnAction(e -> copyToClipboard(textFieldKey.getText()));
        btnCopyIV.setOnAction(e -> copyToClipboard(textFieldIV.getText()));

        btnShuffleKey.setOnAction(e -> regenerateKey());
        btnUploadKey.setOnAction(e -> uploadKey());
        btnDownloadKey.setOnAction(e -> downloadKey());
        btnDeleteKey.setOnAction(e -> deleteKey());

        btnShuffleIV.setOnAction(e -> regenerateIV());
        btnUploadIV.setOnAction(e -> uploadIV());
        btnDownloadIV.setOnAction(e -> downloadIV());
        btnDeleteIV.setOnAction(e -> deleteIV());

        btnClearCipherText.setOnAction(e -> clearCipherText());
        btnClearPlainText.setOnAction(e -> clearPlainText());

        btnImportPlainText.setOnAction(e -> importNoEncryptedFile());
        btnImportCipherText.setOnAction(e -> importEncryptedFile());
    }

    private void clearPlainText() {
        textAreaPlainText.clear();
    }

    private void clearCipherText() {
        textAreaCipherText.clear();
    }

    private void deleteIV() {
        textFieldIV.clear();
        iv=null;
    }

    private void downloadIV() {
    }

    private void uploadIV() {
    }

    private void deleteKey() {
        textFieldKey.clear();
    }

    private void downloadKey() {
    }

    private void uploadKey() {
    }

    /**
     * Copies the given text to the clipboard.
     */
    private void copyToClipboard(String text) {
        if (!text.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(text);
            clipboard.setContent(content);
        }
    }

    /**
     * Regenerates a new random key and updates the key field.
     */
    private void regenerateKey() {
        try {
            SecretKey generatedKey = AESManager.generateKey(aesConfig);
            textFieldKey.setText(Base64.getEncoder().encodeToString(generatedKey.getEncoded()));
        } catch (Exception e) {
            showError("Key generation failed: " + e.getMessage());
        }
    }

    /**
     * Generates a new random IV and updates the IV field.
     */
    private void regenerateIV() {
        byte[] iv = new byte[aesConfig.getMode().getFixedIVSize()];
        new java.security.SecureRandom().nextBytes(iv);
        textFieldIV.setText(Base64.getEncoder().encodeToString(iv));
    }

    /**
     * Imports a file for encryption or decryption and locks text fields.
     */
    private void importNoEncryptedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Encrypt");
        noEncryptedFile = fileChooser.showOpenDialog(stage);

        if (noEncryptedFile != null) {
            fileMode = true;
            textAreaPlainText.clear();
            textAreaCipherText.clear();
            textFieldKey.clear();
            textFieldIV.clear();

            textAreaPlainText.setText(noEncryptedFile.getAbsolutePath());
            textAreaPlainText.setEditable(false);
        }
    }

    /**
     * Imports a file for encryption or decryption and locks text fields.
     */
    private void importEncryptedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Decrypt");
        encryptedFile = fileChooser.showOpenDialog(stage);

        if (encryptedFile != null) {
            fileMode = true;
            textAreaPlainText.clear();
            textAreaCipherText.clear();
            textFieldKey.clear();
            textFieldIV.clear();

            textAreaCipherText.setText(encryptedFile.getAbsolutePath());
            textAreaCipherText.setEditable(false);
        }
    }

    /**
     * Encrypts either a text input or a file.
     */
    @FXML
    private void encrypt(ActionEvent event) {
        try {
            Boolean isValidKey = false;

            String keyText = textFieldKey.getText().trim();

            byte[] keyBytes = null;
            if (!keyText.isEmpty()) {
                try {
                    keyBytes = Base64.getDecoder().decode(keyText);
                    int keySizeBits = keyBytes.length * 8;

                    if (keySizeBits == aesConfig.getKeySize().getSize()) {
                        isValidKey = true;
                    }
                } catch (IllegalArgumentException e) {
                    showError("Invalid Base64 key format. Please enter a valid key.");
                    return;
                }
            }

            if (!isValidKey && aesConfig.isGenerateKey()) {
                SecretKey generatedKey = AESManager.generateKey(aesConfig);
                keyBytes = generatedKey.getEncoded();
                String base64Key = Base64.getEncoder().encodeToString(keyBytes);
                textFieldKey.setText(base64Key);
            }

            if (textFieldKey.getText().trim().isEmpty()) {
                showError("No key provided.");
                return;
            }

            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            if(aesConfig.getMode().requiresIV()) {
                if (aesConfig.isGenerateIV() && textFieldIV.getText().trim().isEmpty()) {
                    iv = aesConfig.getMode().requiresIV() ? AESManager.generateIV(aesConfig) : null;

                    if (aesConfig.isShowIV() && iv != null) {
                        textFieldIV.setText(Base64.getEncoder().encodeToString(iv));
                    }
                } else {
                    String ivText = textFieldIV.getText().trim();
                    if (ivText.isEmpty()) {
                        showError("No Initial Vector provided.");
                        return;
                    }

                    try {
                        iv = Base64.getDecoder().decode(ivText);

                        int expectedIVSize = aesConfig.getMode().getFixedIVSize();
                        if (expectedIVSize == -1) {
                            expectedIVSize = aesConfig.getAlgorithm().getBaseIVSize();
                        }

                        if (iv.length != expectedIVSize) {
                            showError("Invalid IV size. Expected " + expectedIVSize + " bytes, but got " + iv.length + " bytes.");
                            return;
                        }
                    } catch (IllegalArgumentException e) {
                        showError("Invalid IV format. Please enter a valid Base64-encoded IV.");
                        return;
                    }

                }
            }

            if(fileMode){
                byte[] encryptedFile = AESManager.encryptFile(noEncryptedFile,key,iv,aesConfig);
                textAreaCipherText.setText("Your file was successfully encrypted. You can now save it as a .ilun file.");
                textAreaCipherText.setEditable(false);
            }else {
                String plainText = textAreaPlainText.getText();
                if (plainText.isEmpty()) {
                    showError("Plain text cannot be empty.");
                    return;
                }
                String encryptedText = AESManager.encryptText(plainText, key, iv, aesConfig);
                textAreaCipherText.setText(encryptedText);
            }

        } catch (Exception e) {
            showError("Encryption failed: " + e.getMessage());
        }
    }



    /**
     * Decrypts either a text input or a file.
     */
    @FXML
    private void decrypt(ActionEvent event) {
        try {
            SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(textFieldKey.getText()), "AES");
            String cipherText = textAreaCipherText.getText();
            String decryptedText = AESManager.decryptText(cipherText, key, aesConfig);
            textAreaPlainText.setText(decryptedText);
        } catch (Exception e) {
            showError("Decryption failed: " + e.getMessage());
        }
    }

    /**
     * Displays an error message in an alert dialog.
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            errorDialog.showInfoDialog("Error", message);
        });
    }

    @Override
    public void saveCurrentState() {

    }

    @Override
    public void restorePreviousState() {

    }

    @Override
    public void switchEncryptionMethod(String methodView) {

    }

    @Override
    public void closeDialog(DialogHelper dialog) {
        dialog.closeDialog();
        lblMode.setText("Mode: "+aesConfig.getTransformation());
        boxIV.setVisible(aesConfig.isShowIV());
        boxIV.setManaged(aesConfig.isShowIV());
        if(!aesConfig.isShowIV()) {
            textFieldIV.clear();
        }else if(iv != null){
            textFieldIV.setText(Base64.getEncoder().encodeToString(iv));
        }

    }

    @Override
    public void setConfig(CryptosystemConfig config) {

    }

    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("SYMMETRIC-KEY-ENCRYPTION");
    }

    @FXML
    private void showInfoDialog() {
        infoDialog.enableDynamicSize(0.6, 0.6);

        infoDialog.showFXMLDialog(
                "Advanced Encryption Standard (AES) Information",
                "views/container-dialog-view.fxml",  // Load the container
                ContainerDialogController::new,
                new MFXFontIcon("fas-circle-info", 18),
                "mfx-dialog",
                false,
                false,
                controller -> {
                    ResourceBundle bundle = LanguageManager.getInstance().getBundle();
                    controller.loadContent("views/symmetric-key/aes/aes-description-view.fxml", bundle); // Load the description view inside the container
                }
        );
    }

    public void showChangeMethodDialog(ActionEvent actionEvent) {
    }

    @FXML
    private void clearAll(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            alertDialog.applyDialogChanges(dialog -> {
                alertDialog.getDialogContent().clearActions();
                alertDialog.getDialogContent().setHeaderText("Clear All");
                alertDialog.getDialogContent().setContentText("Are you sure you want to clear all fields? This action cannot be undone.");
                alertDialog.getDialogContent().setHeaderIcon(new MFXFontIcon("fas-circle-exclamation", 18));

                MFXButton btnConfirm = new MFXButton("Yes");
                btnConfirm.getStyleClass().add("mfx-primary");
                btnConfirm.setOnAction(event -> {
                    // Limpia los campos correctamente
                    textAreaPlainText.clear();
                    textAreaCipherText.clear();
                    textFieldKey.clear();
                    textFieldIV.clear();

                    // Cierra el diálogo
                    alertDialog.closeDialog();
                    textAreaCipherText.setEditable(true);
                    textAreaPlainText.setEditable(true);
                    textFieldKey.setEditable(true);
                    textFieldIV.setEditable(true);
                });

                MFXButton btnCancel = new MFXButton("Cancel");
                btnCancel.setOnAction(event -> alertDialog.closeDialog());

                // Agregar botones al diálogo
                alertDialog.getDialogContent().addActions(btnConfirm, btnCancel);
            });

            // Muestra el diálogo
            alertDialog.getDialogContent().setShowMinimize(false);
            alertDialog.getDialogContent().setShowAlwaysOnTop(false);
            alertDialog.getDialogContent().getStyleClass().add("mfx-warning-dialog");
            alertDialog.getDialog().setDraggable(false);
            alertDialog.getDialog().showDialog();
        });
    }

    public void showAdvancedOptions(ActionEvent actionEvent) {
        advancedOptionsDialog.showFXMLDialog(
                "Advanced Options (AES)",
                "views/symmetric-key/aes/advanced-options-view.fxml",
                () -> new AdvancedOptionsController(stage, aesConfig,advancedOptionsDialog),
                new MFXFontIcon("fas-gear", 18),
                "mfx-dialog",
                false,
                false,
                controller -> {
                    controller.setParentController(this);
                }
        );

    }

    public void exportEncryptedInformation(ActionEvent actionEvent) {
    }
}
