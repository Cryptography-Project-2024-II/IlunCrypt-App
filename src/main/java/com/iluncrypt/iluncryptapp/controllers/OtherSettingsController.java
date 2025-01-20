package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.models.CipherMethodConfig;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import com.iluncrypt.iluncryptapp.utils.ConfigManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * Controller for managing additional settings for the Affine Cipher.
 * Allows users to modify alphabets and handling of unknown characters.
 */
public class OtherSettingsController implements Initializable {

    @FXML
    private StackPane rootPane;

    @FXML
    private Label lblPlainAlphabet;
    @FXML
    private Label lblCipherAlphabet;
    @FXML
    private Label lblKeyAlphabet;

    @FXML
    private MFXComboBox<String> comboUnknownChars;

    @FXML
    private MFXButton btnChangePlainAlphabet;
    @FXML
    private MFXButton btnChangeCipherAlphabet;
    @FXML
    private MFXButton btnChangeKeyAlphabet;
    @FXML
    private MFXButton btnSaveAsDefault;
    @FXML
    private MFXButton btnSave;
    @FXML
    private MFXButton btnCancel;

    private final DialogHelper dialogHelper;
    private final Stage stage;
    private final CipherMethodConfig config;

    /**
     * Constructor that initializes the controller with a specific cipher method configuration.
     *
     * @param stage The primary application stage.
     * @param config The cipher method configuration.
     */
    public OtherSettingsController(Stage stage, CipherMethodConfig config) {
        this.stage = stage;
        this.dialogHelper = new DialogHelper(stage);
        this.config = config;
    }

    /**
     * Initializes the controller and loads current settings.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.dialogHelper.setOwnerNode(rootPane);
        loadAlphabets();
        loadUnknownCharHandling();
    }

    /**
     * Loads the currently selected alphabets from the configuration file and updates UI labels.
     */
    private void loadAlphabets() {
        lblPlainAlphabet.setText(config.getPlaintextAlphabet().getName());
        lblCipherAlphabet.setText(config.getCiphertextAlphabet().getName());
        lblKeyAlphabet.setText(config.getKeyAlphabet().getName());
    }

    /**
     * Loads the unknown character handling setting and sets the combo box value.
     */
    private void loadUnknownCharHandling() {
        UnknownCharHandling handling = config.getUnknownCharHandling();
        comboUnknownChars.setValue(handling.toString());
    }

    /**
     * Sets up button actions for changing alphabets and saving settings.
     */
    private void setupButtonActions() {
        /*btnChangePlainAlphabet.setOnAction(event -> changeAlphabet(lblPlainAlphabet, "PLAIN_TEXT"));
        btnChangeCipherAlphabet.setOnAction(event -> changeAlphabet(lblCipherAlphabet, "CIPHER_TEXT"));
        btnChangeKeyAlphabet.setOnAction(event -> changeAlphabet(lblKeyAlphabet, "KEY"));

        btnSave.setOnAction(event -> saveSettings());
        btnSaveAsDefault.setOnAction(event -> saveAsDefault());
        btnCancel.setOnAction(event -> closeDialog());*/
    }

    /**
     * Saves the current settings without modifying default values.
     */
    private void saveSettings() {
        /*config.setUnknownCharHandling(UnknownCharHandling.valueOf(comboUnknownChars.getValue()));

        ConfigManager.saveCipherMethodConfig(config.getCipherMethod(), config);
        dialogHelper.showInfoDialog("Settings Saved", "Your settings have been saved successfully.");
    */}

    /**
     * Saves the current settings as default.
     */
    private void saveAsDefault() {
        /*saveSettings(); // Save current settings
        Object ConfigManager;
        ConfigManager.setDefaultCipherConfig(config.getCipherMethod(), config);
        dialogHelper.showInfoDialog("Default Settings Updated", "Your settings have been saved as default.");
    */}

    /**
     * Closes the settings dialog.
     */
    private void closeDialog() {
        dialogHelper.closeDialog();
    }

    /**
     * Opens the Change Alphabet dialog.
     *
     * @param targetLabel The label to update after selecting a new alphabet.
     * @param textType The type of text the alphabet applies to (plaintext, ciphertext, key).
     */
    private void changeAlphabet(Label targetLabel, String textType) {
        dialogHelper.showFXMLDialog(
                "Select Alphabet",
                "views/change-alphabet-view.fxml",
                () -> new ChangeAlphabetController(this.stage, "aa", textType), // Se inyecta el cifrado y tipo de texto
                new MFXFontIcon("fas-gear", 18),
                "mfx-dialog",
                false,
                false,
                controller -> {}
        );
    }
}
