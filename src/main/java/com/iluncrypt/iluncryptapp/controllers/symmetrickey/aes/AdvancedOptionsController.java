package com.iluncrypt.iluncryptapp.controllers.symmetrickey.aes;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.models.AESConfig;
import com.iluncrypt.iluncryptapp.utils.ConfigManager;
import com.iluncrypt.iluncryptapp.models.enums.aes.*;
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


    @FXML
    private StackPane rootPane;

    @FXML
    private MFXComboBox<String> comboOperationMode;
    @FXML
    private MFXComboBox<String> comboKeySize;
    @FXML
    private MFXComboBox<String> comboPadding;
    @FXML
    private MFXComboBox<String> comboIVSize;
    @FXML
    private MFXComboBox<String> comboGCMTagSize;
    @FXML
    private MFXComboBox<String> comboAuthentication;

    @FXML
    private MFXButton btnSaveAsDefault;
    @FXML
    private MFXButton btnReset;
    @FXML
    private MFXButton btnSave;
    @FXML
    private MFXButton btnCancel;

    private final DialogHelper dialog;
    private final Stage stage;
    private final AESConfig config;

    private CipherController parentController;
    private ResourceBundle bundle;

    // Constructor with Stage and config injection
    public AdvancedOptionsController(Stage stage, AESConfig config, DialogHelper dialog){
        this.stage = stage;
        this.dialog = dialog;
        this.config = config;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = LanguageManager.getInstance().getBundle();
        // Use Platform.runLater to ensure UI is loaded
        Platform.runLater(() -> {
            loadAdvancedOptions();
            setupDynamicOptions();
        });
    }

    /**
     * Loads current configuration values into UI controls.
     */
    private void loadAdvancedOptions() {
        for (AESMode mode : AESMode.values()) {
            comboOperationMode.getItems().add(mode.getMode());
        }
        for (KeySize keySize : KeySize.values()) {
            comboKeySize.getItems().add(keySize.toString());
        }
        for (PaddingScheme padding : PaddingScheme.values()) {
            comboPadding.getItems().add(padding.toString());
        }
        for (IVSize ivSize : IVSize.values()) {
            comboIVSize.getItems().add(ivSize.toString());
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
        comboIVSize.setValue(config.getIvSize().toString());
        if (config.getGcmTagSize() != null) {
            comboGCMTagSize.setValue(config.getGcmTagSize().toString());
        } else {
            comboGCMTagSize.setValue("N/A");
        }
        comboAuthentication.setValue(config.getAuthMethod().toString());

        //  Aplica restricciones iniciales
        adjustOptionsBasedOnMode();
    }


    /**
     * Dynamically adjusts available options based on selected values.
     *
     * Ensures that incompatible options are disabled:
     * - `GCM` mode requires `NoPadding`, `IV_12`, and a valid `GCMTagSize`.
     * - `CBC`, `CFB`, and `OFB` require `IV_16`.
     * - `ECB` does not require IV but must have padding.
     */
    private void setupDynamicOptions() {
        comboOperationMode.setOnAction(event -> adjustOptionsBasedOnMode());
    }

    private void adjustOptionsBasedOnMode() {
        AESConfig defaultConfig = ConfigManager.getDefaultAESConfig();
        String selectedMode = comboOperationMode.getValue();

        // Obtener el modo seleccionado desde el Enum
        AESMode mode = null;
        for (AESMode m : AESMode.values()) {
            if (m.getMode().equals(selectedMode)) {
                mode = m;
                break;
            }
        }

        if (mode == null) {
            // Si el modo es desconocido, salir de la función
            return;
        }

        boolean isGCM = mode.usesGCM();
        boolean requiresIV = mode.requiresIV();
        boolean supportsPadding = mode == AESMode.ECB || mode == AESMode.CBC;

        // Ajuste del tamaño del IV
        comboIVSize.getItems().clear();
        if (requiresIV) {
            if (isGCM) {
                comboIVSize.getItems().add("12 bytes");
                comboIVSize.setValue("12 bytes");
            } else {
                comboIVSize.getItems().add("16 bytes");
                comboIVSize.setValue("16 bytes");
            }
        } else {
            comboIVSize.getItems().add("0 bytes");
            comboIVSize.setValue("0 bytes");
        }

        // Ajuste del Padding
        comboPadding.getItems().clear();
        comboPadding.setDisable(!supportsPadding); // Deshabilitar si el modo no requiere padding
        if (supportsPadding) {
            for (PaddingScheme padding : PaddingScheme.values()) {
                comboPadding.getItems().add(padding.toString());
            }
            comboPadding.setValue("PKCS5Padding"); // Valor por defecto si es ECB o CBC
        } else {
            comboPadding.getItems().add("NoPadding");
            comboPadding.setValue("NoPadding");
        }

        // Ajuste del tamaño del tag GCM
        comboGCMTagSize.setDisable(!isGCM);
        if (isGCM) {
            String currentTag = comboGCMTagSize.getValue();
            if (currentTag == null || currentTag.isEmpty() || "N/A".equals(currentTag)) {
                comboGCMTagSize.setValue(defaultConfig.getGcmTagSize().toString());
            }
        } else {
            comboGCMTagSize.setValue("N/A"); // Si no es GCM, el tag se deja en blanco
        }

        // Ajuste de autenticación
        comboAuthentication.setDisable(isGCM);
        if (isGCM) {
            comboAuthentication.setValue("N/A"); // GCM no necesita autenticación
        } else {
            String currentAuth = comboAuthentication.getValue();
            if (currentAuth == null || currentAuth.isEmpty() || "N/A".equals(currentAuth)) {
                comboAuthentication.setValue(defaultConfig.getAuthMethod().toString());
            }
        }
    }



    /**
     * Action to load a key from file.
     */
    @FXML
    private void handleLoadKey() {
    }

    /**
     * Action to select and encrypt a file.
     */
    @FXML
    private void handleEncryptFile() {
    }

    /**
     * Action to select and decrypt a file.
     */
    @FXML
    private void handleDecryptFile() {
    }

    /**
     * Restores configuration to default values.
     */
    @FXML
    private void handleResetConfiguration() {
        // Obtiene la configuración predeterminada desde ConfigManager
        AESConfig defaultConfig = ConfigManager.getDefaultAESConfig();

        // Aplica los valores de configuración por defecto
        comboOperationMode.setValue(defaultConfig.getMode().getMode());
        comboKeySize.setValue(defaultConfig.getKeySize().toString());
        comboPadding.setValue(defaultConfig.getPaddingScheme().toString());
        comboIVSize.setValue(defaultConfig.getIvSize().toString());
        comboGCMTagSize.setValue(defaultConfig.getGcmTagSize().toString());
        comboAuthentication.setValue(defaultConfig.getAuthMethod().toString());
        adjustOptionsBasedOnMode();
        saveConfig();
        ConfigManager.saveAESConfig(config);
        closeDialog();
    }

    /**
     * Closes the advanced options dialog.
     */
    @FXML
    private void closeDialog() {
        if (parentController != null) {
            parentController.closeDialog(dialog);
        }
    }

    /**
     * Saves the current configuration to the config object.
     */
    private void saveConfig() {
        // Obtener los valores seleccionados en los combobox y asignarlos al objeto config
        config.setMode(AESMode.valueOf(comboOperationMode.getValue()));
        config.setKeySize(KeySize.fromSize(Integer.parseInt(comboKeySize.getValue().replace(" bits", ""))));
        config.setPaddingScheme(PaddingScheme.fromString(comboPadding.getValue()));
        config.setIvSize(IVSize.fromSize(Integer.parseInt(comboIVSize.getValue().replace(" bytes", ""))));

        // Si GCM está activo, obtener el tamaño del tag, de lo contrario establecerlo como null
        if (!comboGCMTagSize.isDisabled() && comboGCMTagSize.getValue() != null && !comboGCMTagSize.getValue().isEmpty()) {
            config.setGcmTagSize(GCMTagSize.fromSize(Integer.parseInt(comboGCMTagSize.getValue().replace(" bits", ""))));
        }

        // Si la autenticación no está deshabilitada, guardar el método de autenticación
        if (!comboAuthentication.isDisabled() && comboAuthentication.getValue() != null && !comboAuthentication.getValue().isEmpty()) {
            config.setAuthMethod(AuthenticationMethod.fromString(comboAuthentication.getValue()));
        } else {
            config.setAuthMethod(AuthenticationMethod.NONE);
        }
    }

    /**
     * Saves the configuration and closes the dialog.
     */
    @FXML
    private void handleSave() {
        saveConfig();
        closeDialog();
    }

    /**
     * Saves the configuration as default and persists it.
     */
    public void handleDefaultSave(ActionEvent actionEvent) {
        saveConfig();
        ConfigManager.saveAESConfig(config);
        closeDialog();
    }


    /**
     * Allows injecting the parent controller for interaction.
     *
     * @param parentController The parent controller.
     */
    public void setParentController(CipherController parentController) {
        this.parentController = parentController;
    }
}
