package com.iluncrypt.iluncryptapp.controllers.symmetrickey.aes;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.ContainerDialogController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.models.AESConfig;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.symmetrickey.AESManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import com.iluncrypt.iluncryptapp.utils.ConfigManager;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * Controller for AES encryption.
 */
public class AESController implements CipherController, Initializable {



    private AESConfig aesConfig;

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final DialogHelper advancedOptionsDialog;
    private final DialogHelper alertDialog;
    private final Stage stage;

    private boolean sourceInfoActive = false;
    private boolean encryptedInfoActive = false;



    private String lastPlainText = "";
    private String lastCipherText = "";
    private String lastKey = "";

    @FXML
    private MFXCheckbox checkGenerateKey;

    @FXML
    private GridPane grid;

    @FXML
    private Label lblMode;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldKey;

    @FXML
    private MFXTextField textFieldIV;


    @FXML
    private MFXButton btnImportPlainText;

    @FXML
    private MFXButton btnImportCipherText;

    public AESController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
        this.changeMethodDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
        this.advancedOptionsDialog = new DialogHelper(stage);
        this.alertDialog = new DialogHelper(stage);
        this.aesConfig = ConfigManager.loadAESConfig();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        configureDialogs();
        loadAESConfig();

        setupTextAreaListeners();
    }

    private void loadAESConfig() {
        if (aesConfig == null) {
            throw new IllegalArgumentException("Failed to load AES configuration.");
        }

        lblMode.setText("Mode: "+aesConfig.getTransformation());

    }

    private void configureDialogs() {
        infoDialog.setOwnerNode(grid);
        changeMethodDialog.setOwnerNode(grid);
        errorDialog.setOwnerNode(grid);
        advancedOptionsDialog.setOwnerNode(grid);
        alertDialog.setOwnerNode(grid);
    }

    private void setupTextAreaListeners() {
        // Listener para detectar cuando se escribe en Source Information
        textAreaPlainText.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                sourceInfoActive = true;
                encryptedInfoActive = false;
            } else {
                sourceInfoActive = false;
            }
            updateUI();
        });

        // Listener para detectar cuando se escribe en Encrypted Information
        textAreaCipherText.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                encryptedInfoActive = true;
                sourceInfoActive = false;
            } else {
                encryptedInfoActive = false;
            }
            updateUI();
        });

        // Focus Listener para Source Information
        textAreaPlainText.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (newFocus) {
                textFieldKey.setVisible(true);
            }
        });

        // Focus Listener para Encrypted Information
        textAreaCipherText.focusedProperty().addListener((obs, oldFocus, newFocus) -> {
            if (newFocus) {
                textFieldKey.setVisible(!sourceInfoActive); // Oculta si ya se escribió en Source
            }
        });
    }

    private void updateUI() {
        textAreaCipherText.setDisable(sourceInfoActive);
        btnImportCipherText.setDisable(sourceInfoActive);

        textAreaPlainText.setDisable(encryptedInfoActive);
        btnImportPlainText.setDisable(encryptedInfoActive);
    }


    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("SYMMETRIC-KEY-ENCRYPTION");
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
                    sourceInfoActive = false;
                    encryptedInfoActive = false;
                    updateUI();

                    // Cierra el diálogo
                    alertDialog.closeDialog();
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



    @FXML
    private void copyPlainText(ActionEvent actionEvent) {
        if (!textAreaPlainText.getText().isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(textAreaPlainText.getText());
            clipboard.setContent(content);
        }
    }

    @FXML
    private void copyEncryptedText(ActionEvent actionEvent) {
        if (!textAreaCipherText.getText().isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(textAreaCipherText.getText());
            clipboard.setContent(content);
        }
    }

    @FXML
    private void importFile(ActionEvent actionEvent) {
        sourceInfoActive = true;
        encryptedInfoActive = false;
        updateUI();
    }

    @FXML
    private void importEncryptedData(ActionEvent actionEvent) {
        encryptedInfoActive = true;
        sourceInfoActive = false;
        updateUI();
    }

    /**
     * Encrypts either plain text or a file based on the user input.
     *
     * <p>If the plain text area is not empty, it encrypts the text and displays the Base64-encoded result.
     * Otherwise, it opens file choosers to select an input file and a destination file, then encrypts the file.</p>
     *
     * @param event the action event triggered by the encrypt button.
     */
    public void encrypt(ActionEvent event) {
        try {
            SecretKey key = obtainSecretKey(); // Gets the key based on UI settings

            if (!textAreaPlainText.getText().trim().isEmpty()) {
                // Encrypt plain text
                String plainText = textAreaPlainText.getText();
                String encryptedBase64 = AESManager.encryptText(plainText, key, aesConfig);
                textAreaCipherText.setText(encryptedBase64);
            } else {
                // Encrypt file: prompt for input and output files
                File inputFile = selectInputFile();
                if (inputFile == null) {
                    showError("No input file selected.");
                    return;
                }
                File outputFile = selectOutputFile();
                if (outputFile == null) {
                    showError("No output file selected.");
                    return;
                }
                AESManager.encryptFile(inputFile, outputFile, key, aesConfig);
                showMessage("File encrypted successfully.");
            }
        } catch (Exception e) {
            showError("Encryption error: " + e.getMessage());
        }
    }

    /**
     * Decrypts either cipher text or a file based on the user input.
     *
     * <p>If the cipher text area is not empty, it decrypts the Base64-encoded text and shows the result.
     * Otherwise, it opens file choosers to select an encrypted file and a destination file, then decrypts the file.</p>
     *
     * @param event the action event triggered by the decrypt button.
     */
    public void decrypt(ActionEvent event) {
        try {
            SecretKey key = obtainSecretKey(); // Gets the key based on UI settings

            if (!textAreaCipherText.getText().trim().isEmpty()) {
                // Decrypt plain text
                String cipherTextBase64 = textAreaCipherText.getText();
                String decryptedText = AESManager.decryptText(cipherTextBase64, key, aesConfig);
                textAreaPlainText.setText(decryptedText);
            } else {
                // Decrypt file: prompt for input and output files
                File inputFile = selectInputFile();
                if (inputFile == null) {
                    showError("No input file selected.");
                    return;
                }
                File outputFile = selectOutputFile();
                if (outputFile == null) {
                    showError("No output file selected.");
                    return;
                }
                AESManager.decryptFile(inputFile, outputFile, key, aesConfig);
                showMessage("File decrypted successfully.");
            }
        } catch (Exception e) {
            showError("Decryption error: " + e.getMessage());
        }
    }

    /**
     * Obtains the SecretKey based on UI settings.
     *
     * <p>If the generate key checkbox is selected, it generates a new key and updates the key field.
     * Otherwise, it retrieves the key from the key text field (assuming it is Base64-encoded).</p>
     *
     * @return the SecretKey for encryption/decryption.
     * @throws Exception if key generation or conversion fails.
     */
    private SecretKey obtainSecretKey() throws Exception {
        if (checkGenerateKey.isSelected()) {
            // Generate a new key and update the key text field with its Base64 representation
            SecretKey generatedKey = AESManager.generateKey(aesConfig);
            textFieldKey.setText(Base64.getEncoder().encodeToString(generatedKey.getEncoded()));
            return generatedKey;
        } else {
            // Retrieve the key from the text field (assumed to be Base64-encoded)
            String keyString = textFieldKey.getText().trim();
            if (keyString.isEmpty()) {
                throw new Exception("No key provided.");
            }
            byte[] keyBytes = Base64.getDecoder().decode(keyString);
            return new SecretKeySpec(keyBytes, "AES");
        }
    }

    /**
     * Opens a file chooser dialog for selecting an input file.
     *
     * @return the selected input File, or null if no file was chosen.
     */
    private File selectInputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input File");
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Opens a file chooser dialog for selecting an output file.
     *
     * @return the selected output File, or null if no file was chosen.
     */
    private File selectOutputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Output File");
        return fileChooser.showSaveDialog(stage);
    }

    /**
     * Displays an information message to the user.
     *
     * @param message the message to be displayed.
     */
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message the error message to be displayed.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void exportEncryptedInformation(ActionEvent actionEvent) {
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

    @Override
    public void saveCurrentState() {
        lastPlainText = textAreaPlainText.getText();
        lastCipherText = textAreaCipherText.getText();
        lastKey = textFieldKey.getText();
    }

    @Override
    public void restorePreviousState() {
        textAreaPlainText.setText(lastPlainText);
        textAreaCipherText.setText(lastCipherText);
        textFieldKey.setText(lastKey);
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
        lblMode.setText("Mode: "+aesConfig.getTransformation());
    }

    @Override
    public void setConfig(CryptosystemConfig config) {
        this.aesConfig = (AESConfig) config;
    }

    public void showChangeMethodDialog(ActionEvent actionEvent) {
    }

    public void setAesConfig(AESConfig aesConfig) {
        this.aesConfig = aesConfig;
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
}
