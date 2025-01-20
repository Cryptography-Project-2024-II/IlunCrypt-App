package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.models.CipherMethodConfig;
import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import com.iluncrypt.iluncryptapp.models.enums.WhitespaceHandling;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
    private MFXComboBox<String> comboCaseHandling;

    @FXML
    private MFXComboBox<String> comboUnknownChars;

    @FXML
    private MFXComboBox<String> comboWhitespaceHandling;

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

    private CipherController parentController;

    private ResourceBundle bundle;

    private final Map<String, CaseHandling> caseHandlingMap = new HashMap<>();
    private final Map<String, UnknownCharHandling> unknownCharHandlingMap = new HashMap<>();
    private final Map<String, WhitespaceHandling> whitespaceHandlingMap = new HashMap<>();

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

    public void setParentController(CipherController parentController) {
        this.parentController = parentController;
    }

    /**
     * Initializes the controller and loads current settings.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = LanguageManager.getInstance().getBundle();
        Platform.runLater(() -> {
            loadCaseHandlingOptions();
            loadUnknownCharHandlingOptions();
            loadWhitespaceHandlingOptions();
            loadAlphabets();
        });
    }

    private void loadCaseHandlingOptions() {
        for (CaseHandling handling : CaseHandling.values()) {
            String localizedText = bundle.getString("caseHandling." + handling.name());
            caseHandlingMap.put(localizedText, handling);
            comboCaseHandling.getItems().add(localizedText);
        }
        comboCaseHandling.setValue(bundle.getString("caseHandling." + config.getCaseHandling().name()));
    }

    private void loadUnknownCharHandlingOptions() {
        for (UnknownCharHandling handling : UnknownCharHandling.values()) {
            String localizedText = bundle.getString("unknownCharHandling." + handling.name());
            unknownCharHandlingMap.put(localizedText, handling);
            comboUnknownChars.getItems().add(localizedText);
        }
        comboUnknownChars.setValue(bundle.getString("unknownCharHandling." + config.getUnknownCharHandling().name()));
    }

    private void loadWhitespaceHandlingOptions() {
        for (WhitespaceHandling handling : WhitespaceHandling.values()) {
            String localizedText = bundle.getString("whitespaceHandling." + handling.name());
            whitespaceHandlingMap.put(localizedText, handling);
            comboWhitespaceHandling.getItems().add(localizedText);
        }
        comboWhitespaceHandling.setValue(bundle.getString("whitespaceHandling." + config.getWhitespaceHandling().name()));
    }

    /**
     * Devuelve el valor seleccionado en el ComboBox mapeado a su respectivo Enum.
     */
    public CaseHandling getSelectedCaseHandling() {
        return caseHandlingMap.get(comboCaseHandling.getValue());
    }

    public UnknownCharHandling getSelectedUnknownCharHandling() {
        return unknownCharHandlingMap.get(comboUnknownChars.getValue());
    }

    public WhitespaceHandling getSelectedWhitespaceHandling() {
        return whitespaceHandlingMap.get(comboWhitespaceHandling.getValue());
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
    @FXML
    private void closeDialog() {
        if (parentController != null) {
            parentController.closeOptionsDialog();
        }
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
