package com.iluncrypt.iluncryptapp.controllers.symmetrickey.des;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.models.SymmetricKeyConfig;
import com.iluncrypt.iluncryptapp.models.enums.symmetrickey.*;
import com.iluncrypt.iluncryptapp.utils.config.ConfigManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdvancedOptionsController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private MFXComboBox<String> comboOperationMode;
    @FXML private MFXComboBox<String> comboKeySize;
    @FXML private MFXComboBox<String> comboPadding;
    @FXML private MFXComboBox<String> comboGCMTagSize;
    @FXML private MFXComboBox<String> comboAuthentication;

    @FXML private MFXButton btnSaveAsDefault;
    @FXML private MFXButton btnReset;
    @FXML private MFXButton btnSave;
    @FXML private MFXButton btnCancel;

    private final DialogHelper dialog;
    private final Stage stage;
    private final SymmetricKeyConfig config;

    private CipherController parentController;
    private ResourceBundle bundle;

    public AdvancedOptionsController(Stage stage, SymmetricKeyConfig config, DialogHelper dialog) {
        this.stage = stage;
        this.dialog = dialog;
        this.config = config;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = LanguageManager.getInstance().getBundle();
        Platform.runLater(() -> {
            loadAdvancedOptions();
            setupDynamicOptions();
        });
    }

    private void loadAdvancedOptions() {
        for (SymmetricKeyMode mode : SymmetricKeyMode.values()) {
            comboOperationMode.getItems().add(mode.getMode());
        }
        for (KeySize keySize : KeySize.values()) {
            comboKeySize.getItems().add(keySize.toString());
        }
        for (PaddingScheme padding : PaddingScheme.values()) {
            comboPadding.getItems().add(padding.toString());
        }
        for (GCMTagSize gcmTagSize : GCMTagSize.values()) {
            comboGCMTagSize.getItems().add(gcmTagSize.toString());
        }
        for (AuthenticationMethod authMethod : AuthenticationMethod.values()) {
            comboAuthentication.getItems().add(authMethod.toString());
        }

        comboOperationMode.setValue(config.getMode().getMode());
        comboKeySize.setValue(config.getKeySize().toString());
        comboPadding.setValue(config.getPaddingScheme().toString());
        if (config.getGCMTagSize() != null) {
            comboGCMTagSize.setValue(config.getGCMTagSize().toString());
        } else {
            comboGCMTagSize.setValue("N/A");
        }
        comboAuthentication.setValue(config.getAuthenticationMethod().toString());

        adjustOptionsBasedOnMode();
    }

    private void setupDynamicOptions() {
        comboOperationMode.setOnAction(event -> adjustOptionsBasedOnMode());
    }

    private void adjustOptionsBasedOnMode() {
        String selectedMode = comboOperationMode.getValue();
        SymmetricKeyMode mode = SymmetricKeyMode.fromString(selectedMode);
        if (mode == null) return;

        boolean isGCM = mode == SymmetricKeyMode.GCM;
        boolean requiresIV = mode.requiresIV();
        boolean supportsPadding = mode == SymmetricKeyMode.ECB || mode == SymmetricKeyMode.CBC;

        // Ajuste del Padding
        comboPadding.getItems().clear();
        comboPadding.setDisable(!supportsPadding);
        if (supportsPadding) {
            for (PaddingScheme padding : mode.getSupportedPadding()) {
                comboPadding.getItems().add(padding.toString());
            }
            comboPadding.setValue(config.getPaddingScheme().toString());
        } else {
            comboPadding.getItems().add(PaddingScheme.NO_PADDING.toString());
            comboPadding.setValue(PaddingScheme.NO_PADDING.toString());
        }

        // Ajuste del tamaño del tag GCM
        comboGCMTagSize.setDisable(!isGCM);
        if (isGCM) {
            comboGCMTagSize.setValue(config.getGCMTagSize() != null ? config.getGCMTagSize().toString() : GCMTagSize.TAG_128.toString());
        } else {
            comboGCMTagSize.setValue("N/A");
        }

        // Ajuste de autenticación
        comboAuthentication.setDisable(isGCM);
        if (isGCM) {
            comboAuthentication.setValue("N/A");
        } else {
            comboAuthentication.setValue(config.getAuthenticationMethod().toString());
        }
    }

    private void saveConfig() {
        config.setMode(SymmetricKeyMode.fromString(comboOperationMode.getValue()));
        config.setKeySize(KeySize.fromSize(
                Integer.parseInt(comboKeySize.getValue().replace(" bits", "")),
                config.getAlgorithm() // Pasar el algoritmo seleccionado
        ));
        config.setPaddingScheme(PaddingScheme.fromString(comboPadding.getValue()));

        // Si GCM está activo, obtener el tamaño del tag, de lo contrario establecerlo como null
        if (!comboGCMTagSize.isDisabled() && !comboGCMTagSize.getValue().equals("N/A")) {
            config.setGCMTagSize(GCMTagSize.fromSize(Integer.parseInt(comboGCMTagSize.getValue().replace(" bits", ""))));
        } else {
            config.setGCMTagSize(null);
        }

        // Ajuste de autenticación
        if (!comboAuthentication.isDisabled() && !comboAuthentication.getValue().equals("N/A")) {
            config.setAuthenticationMethod(AuthenticationMethod.fromString(comboAuthentication.getValue()));
        } else {
            config.setAuthenticationMethod(AuthenticationMethod.NONE);
        }
    }

    @FXML
    private void handleSave() {
        saveConfig();
        closeDialog();
    }

    public void handleDefaultSave(ActionEvent actionEvent) {
        saveConfig();
        ConfigManager.saveSymmetricKeyConfig(config);
        closeDialog();
    }

    @FXML
    private void handleResetConfiguration() {
        SymmetricKeyConfig defaultConfig = ConfigManager.loadSymmetricKeyConfig("AES");
        comboOperationMode.setValue(defaultConfig.getMode().getMode());
        comboKeySize.setValue(defaultConfig.getKeySize().toString());
        comboPadding.setValue(defaultConfig.getPaddingScheme().toString());
        comboGCMTagSize.setValue(defaultConfig.getGCMTagSize() != null ? defaultConfig.getGCMTagSize().toString() : "N/A");
        comboAuthentication.setValue(defaultConfig.getAuthenticationMethod().toString());
        adjustOptionsBasedOnMode();
        saveConfig();
        ConfigManager.saveSymmetricKeyConfig(config);
        closeDialog();
    }

    @FXML
    private void closeDialog() {
        if (parentController != null) {
            parentController.closeDialog(dialog);
        }
    }

    public void setParentController(CipherController parentController) {
        this.parentController = parentController;
    }
}
