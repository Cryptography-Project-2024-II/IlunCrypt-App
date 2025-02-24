package com.iluncrypt.iluncryptapp.controllers.symmetrickey.aes;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.models.SymmetricKeyConfig;
import com.iluncrypt.iluncryptapp.models.enums.symmetrickey.*;
import com.iluncrypt.iluncryptapp.utils.config.ConfigManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdvancedOptionsController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private MFXComboBox<String> comboOperationMode;
    @FXML private MFXComboBox<String> comboKeySize;
    @FXML private MFXComboBox<String> comboPadding;
    @FXML private MFXComboBox<String> comboGCMTagSize;
    @FXML private MFXComboBox<String> comboAuthentication;

    @FXML private MFXTextField txtIVSize;

    @FXML private MFXCheckbox checkGenerateKey, checkSeeIV, checkGenerateIV;

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

            double comboBoxWidth = comboOperationMode.getWidth();
            txtIVSize.setPrefWidth(comboBoxWidth);

        });
    }

    private void loadAdvancedOptions() {

        checkGenerateIV.setSelected(config.isGenerateIV());
        checkGenerateKey.setSelected(config.isGenerateKey());
        checkSeeIV.setSelected(config.isShowIV());

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
            comboAuthentication.getItems().add(authMethod.getHMACAlgorithm());
        }

        comboOperationMode.setValue(config.getMode().getMode());
        comboKeySize.setValue(config.getKeySize().toString());
        comboPadding.setValue(config.getPaddingScheme().toString());

        SymmetricKeyAlgorithm selectedAlgorithm = config.getAlgorithm();
        SymmetricKeyMode selectedMode = SymmetricKeyMode.fromString(comboOperationMode.getValue());

        if (selectedMode.requiresIV()) {
            int ivSize = selectedMode.getFixedIVSize() > 0 ? selectedMode.getFixedIVSize() : selectedAlgorithm.getBaseIVSize();
            txtIVSize.setText(ivSize + " bytes");
        } else {
            txtIVSize.setText("N/A");
        }

        if (config.getGCMTagSize() != null) {
            comboGCMTagSize.setValue(config.getGCMTagSize().toString());
        } else {
            comboGCMTagSize.setValue("N/A");
        }
        comboAuthentication.setValue(
                config.getAuthenticationMethod().getHMACAlgorithm() != null
                        ? config.getAuthenticationMethod().getHMACAlgorithm()
                        : "No authentication"
        );

        adjustOptionsBasedOnMode();
    }

    private void setupDynamicOptions() {
        comboOperationMode.setOnAction(event -> adjustOptionsBasedOnMode());
    }

    private void adjustOptionsBasedOnMode() {
        String selectedMode = comboOperationMode.getValue();
        SymmetricKeyMode mode = SymmetricKeyMode.fromString(selectedMode);
        SymmetricKeyAlgorithm selectedAlgorithm = config.getAlgorithm();

        if (mode == null) return;


        // Determinar el tamaño del IV
        if (mode.requiresIV()) {
            int ivSize = mode.getFixedIVSize() > 0 ? mode.getFixedIVSize() : selectedAlgorithm.getBaseIVSize();
            txtIVSize.setText(ivSize + " bytes");
            checkSeeIV.setSelected(config.isShowIV());
            checkSeeIV.setDisable(false);
            checkGenerateIV.setSelected(config.isGenerateIV());
            checkGenerateIV.setDisable(false);
        } else {
            txtIVSize.setText("N/A");
            checkSeeIV.setSelected(false);
            checkSeeIV.setDisable(true);
            checkGenerateIV.setSelected(false);
            checkGenerateIV.setDisable(true);
        }

        boolean isGCM = mode == SymmetricKeyMode.GCM;
        boolean supportsPadding = mode == SymmetricKeyMode.ECB || mode == SymmetricKeyMode.CBC;

        // --- Ajuste del Padding ---
        comboPadding.getItems().clear();
        comboPadding.setDisable(!supportsPadding);
        if (supportsPadding) {
            for (PaddingScheme padding : mode.getSupportedPadding()) {
                comboPadding.getItems().add(padding.toString());
            }
            if (mode.supportsPadding(config.getPaddingScheme())) {
                comboPadding.setValue(config.getPaddingScheme().toString());
            } else {
                comboPadding.setValue(mode.getPaddingScheme().toString()); // Primer padding válido
            }
        } else {
            comboPadding.getItems().add(PaddingScheme.NO_PADDING.toString());
            comboPadding.setValue(PaddingScheme.NO_PADDING.toString());
        }

        // --- Ajuste del tamaño del GCM Tag ---
        comboGCMTagSize.getItems().clear();
        comboGCMTagSize.setDisable(!isGCM);
        if (isGCM) {
            for (GCMTagSize tagSize : GCMTagSize.values()) {
                comboGCMTagSize.getItems().add(tagSize.toString());
            }
            comboGCMTagSize.setValue(config.getGCMTagSize() != null ? config.getGCMTagSize().toString() : GCMTagSize.TAG_128.toString());
        } else {
            comboGCMTagSize.getItems().add("N/A");
            comboGCMTagSize.setValue("N/A");
        }

        // --- Ajuste de tamaños de clave permitidos según el algoritmo ---
        comboKeySize.getItems().clear();
        List<KeySize> validKeySizes = KeySize.getValidKeySizes(selectedAlgorithm);

        for (KeySize keySize : validKeySizes) {
            comboKeySize.getItems().add(keySize.toString());
        }

        // Verifica si hay valores en la lista antes de asignar el primero
        if (!comboKeySize.getItems().isEmpty()) {
            if (comboKeySize.getItems().contains(config.getKeySize().toString())) {
                comboKeySize.setValue(config.getKeySize().toString());
            } else {
                comboKeySize.setValue(comboKeySize.getItems().get(0)); // Primer valor válido solo si hay elementos
            }
        }

        // Verifica si hay valores en la lista antes de asignar el primero
        if (!comboKeySize.getItems().isEmpty()) {
            if (comboKeySize.getItems().contains(config.getKeySize().toString())) {
                comboKeySize.setValue(config.getKeySize().toString());
            } else {
                comboKeySize.setValue(comboKeySize.getItems().get(0)); // Primer valor válido solo si hay elementos
            }
        }

        // --- Ajuste de métodos de autenticación ---
        comboAuthentication.getItems().clear();
        comboAuthentication.setDisable(isGCM);
        if (isGCM) {
            comboAuthentication.getItems().add("N/A");
            comboAuthentication.setValue("N/A");
        } else {
            for (AuthenticationMethod method : AuthenticationMethod.values()) {
                String algorithm = method.getHMACAlgorithm();
                comboAuthentication.getItems().add(algorithm != null ? algorithm : "No authentication");
            }

            // Establecer el valor actual o "No authentication" si es null
            comboAuthentication.setValue(config.getAuthenticationMethod().getHMACAlgorithm() != null
                    ? config.getAuthenticationMethod().getHMACAlgorithm()
                    : "No authentication"
            );
        }
    }


    private void saveConfig() {
        // Validar que el algoritmo esté presente antes de continuar
        if (config.getAlgorithm() == null) {
            throw new IllegalStateException("Algorithm must be set before saving configuration.");
        }

        // Asignar modo de operación
        config.setMode(SymmetricKeyMode.fromString(comboOperationMode.getValue()));

        // Validar y asignar tamaño de clave
        try {
            String keySizeStr = comboKeySize.getValue();
            if (keySizeStr != null && !keySizeStr.isEmpty()) {
                int keySize = Integer.parseInt(keySizeStr.replace(" bits", ""));
                config.setKeySize(KeySize.fromSize(keySize, config.getAlgorithm())); // Verifica que KeySize.fromSize() funcione bien
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid key size format: " + comboKeySize.getValue(), e);
        }

        // Asignar esquema de padding
        if (comboPadding.getValue() != null) {
            config.setPaddingScheme(PaddingScheme.fromString(comboPadding.getValue()));
        }

        // Asignar tamaño de tag GCM si aplica
        if (!comboGCMTagSize.isDisabled() && comboGCMTagSize.getValue() != null && !comboGCMTagSize.getValue().equals("N/A")) {
            try {
                int gcmTagSize = Integer.parseInt(comboGCMTagSize.getValue().replace(" bits", ""));
                config.setGCMTagSize(GCMTagSize.fromSize(gcmTagSize));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid GCM tag size format: " + comboGCMTagSize.getValue(), e);
            }
        } else {
            config.setGCMTagSize(null);
        }

        // Asignar método de autenticación
        if (!comboAuthentication.isDisabled() && comboAuthentication.getValue() != null) {
            String authMethod = comboAuthentication.getValue();
            if (!authMethod.equals("N/A") && !authMethod.equals("No authentication")) {
                config.setAuthenticationMethod(AuthenticationMethod.fromString(authMethod));
            } else {
                config.setAuthenticationMethod(AuthenticationMethod.NONE);
            }
        } else {
            config.setAuthenticationMethod(AuthenticationMethod.NONE);
        }

        config.setShowIV(checkSeeIV.isSelected());
        config.setGenerateIV(checkGenerateIV.isSelected());
        config.setGenerateKey(checkGenerateKey.isSelected());
        //config.setSaveAlgorithm();

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
        // Cargar la configuración predeterminada
        SymmetricKeyConfig defaultConfig = ConfigManager.getDefaultSymmetricKeyConfig("AES");

        // Restablecer las opciones de cifrado
        comboOperationMode.setValue(defaultConfig.getMode().getMode());
        comboKeySize.setValue(defaultConfig.getKeySize().toString());
        comboPadding.setValue(defaultConfig.getPaddingScheme().toString());
        comboGCMTagSize.setValue(
                defaultConfig.getGCMTagSize() != null ? defaultConfig.getGCMTagSize().toString() : "N/A"
        );

        // Manejo especial para autenticación, asegurando que se muestre "No authentication" si es null
        comboAuthentication.setValue(
                defaultConfig.getAuthenticationMethod().getHMACAlgorithm() != null
                        ? defaultConfig.getAuthenticationMethod().getHMACAlgorithm()
                        : "No authentication"
        );

        // Restablecer valores de checkboxes
        checkGenerateIV.setSelected(defaultConfig.isGenerateIV());
        checkGenerateKey.setSelected(defaultConfig.isGenerateKey());
        checkSeeIV.setSelected(defaultConfig.isShowIV());

        config.setShowIV(checkSeeIV.isSelected());
        config.setGenerateIV(checkGenerateIV.isSelected());
        config.setGenerateKey(checkGenerateKey.isSelected());

        // Ajustar opciones dinámicamente según el modo seleccionado
        adjustOptionsBasedOnMode();

        // Guardar y cerrar el diálogo
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
