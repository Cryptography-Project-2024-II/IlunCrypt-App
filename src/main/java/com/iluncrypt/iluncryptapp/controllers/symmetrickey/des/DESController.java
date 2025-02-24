package com.iluncrypt.iluncryptapp.controllers.symmetrickey.des;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.controllers.symmetrickey.des.AdvancedOptionsController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.SymmetricKeyConfig;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
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
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for DES encryption.
 */
public class DESController implements CipherController, Initializable {

    private SymmetricKeyConfig desConfig;

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper advancedOptionsDialog;
    private final DialogHelper errorDialog;
    private final DialogHelper alertDialog;
    private final Stage stage;

    private String lastPlainText = "";
    private String lastCipherText = "";
    private String lastKey = "";

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private Label lblMode;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldKey;

    public DESController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
        this.changeMethodDialog = new DialogHelper(stage);
        this.advancedOptionsDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
        this.alertDialog = new DialogHelper(stage);
        this.desConfig = ConfigManager.loadSymmetricKeyConfig("DES");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureDialogs();
        loadDESConfig();
    }

    private void loadDESConfig() {
        if (desConfig == null) {
            throw new IllegalArgumentException("Failed to load DES configuration.");
        }

        lblMode.setText("Mode: "+desConfig.getTransformation());

    }

    private void configureDialogs() {
        infoDialog.setOwnerNode(grid);
        changeMethodDialog.setOwnerNode(grid);
        advancedOptionsDialog.setOwnerNode(grid);
        errorDialog.setOwnerNode(grid);
        alertDialog.setOwnerNode(grid);
    }

    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("SYMMETRIC-KEY-ENCRYPTION");
    }

    @FXML
    private void showInfoDialog() {
        infoDialog.showInfoDialog(
                "DES Encryption",
                "DES (Data Encryption Standard) is a symmetric-key algorithm that encrypts blocks of 64 bits "
                        + "using a 56-bit key."
        );
    }

    @FXML
    private void cipherText() {
        // Implement DES encryption logic
    }

    @FXML
    private void decipherText() {
        // Implement DES decryption logic
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
                    //textFieldIV.clear();
                    //sourceInfoActive = false;
                    //encryptedInfoActive = false;
                    //updateUI();

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

    public void showAdvancedOptions(ActionEvent actionEvent) {
        advancedOptionsDialog.showFXMLDialog(
                "Advanced Options (AES)",
                "views/symmetric-key/des/advanced-options-view.fxml",
                () -> new AdvancedOptionsController(stage, desConfig,advancedOptionsDialog),
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

    public void encrypt(ActionEvent actionEvent) {
    }

    public void decrypt(ActionEvent actionEvent) {

    }
}
