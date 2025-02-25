package com.iluncrypt.iluncryptapp.controllers.symmetrickey.des;

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
    @FXML private MFXComboBox<String> comboAlgorithm;
    @FXML private MFXComboBox<String> comboOperationMode;
    @FXML private MFXComboBox<String> comboKeySize;
    @FXML private MFXComboBox<String> comboPadding;
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

        comboAlgorithm.getItems().clear();
        for (SymmetricKeyAlgorithm a : SymmetricKeyAlgorithm.values())
            if (a == SymmetricKeyAlgorithm.DES || a == SymmetricKeyAlgorithm.TDES)
                comboAlgorithm.getItems().add(a.name());
        comboAlgorithm.setValue(config.getAlgorithm().name());

        SymmetricKeyAlgorithm alg = SymmetricKeyAlgorithm.valueOf(comboAlgorithm.getValue());

        comboOperationMode.getItems().clear();
        for (SymmetricKeyMode m : alg.getSupportedModes())
            comboOperationMode.getItems().add(m.getMode());

        comboKeySize.getItems().clear();
        for (KeySize ks : alg.getSupportedKeySizes())
            comboKeySize.getItems().add(ks.toString());

        comboPadding.getItems().clear();
        for (PaddingScheme p : PaddingScheme.values())
            comboPadding.getItems().add(p.toString());

        comboAuthentication.getItems().clear();
        for (AuthenticationMethod a : AuthenticationMethod.values())
            comboAuthentication.getItems().add(a.getHMACAlgorithm());

        comboOperationMode.setValue(config.getMode().getMode());
        comboKeySize.setValue(config.getKeySize().toString());
        comboPadding.setValue(config.getPaddingScheme().toString());
        comboAuthentication.setValue(config.getAuthenticationMethod().getHMACAlgorithm() != null
                ? config.getAuthenticationMethod().getHMACAlgorithm()
                : "No authentication");

        SymmetricKeyMode mode = SymmetricKeyMode.fromString(comboOperationMode.getValue());
        txtIVSize.setText(mode.requiresIV()
                ? (mode.getFixedIVSize() > 0 ? mode.getFixedIVSize() : alg.getBaseIVSize()) + " bytes"
                : "N/A");

        adjustOptionsBasedOnMode();
        adjustOptionsBasedOnAlgorithm();
    }

    private void adjustOptionsBasedOnAlgorithm() {
        SymmetricKeyAlgorithm alg = SymmetricKeyAlgorithm.valueOf(comboAlgorithm.getValue());
        comboKeySize.getItems().clear();
        for (KeySize ks : KeySize.getValidKeySizes(config.getAlgorithm()))
            comboKeySize.getItems().add(ks.toString());
        String currentKey = config.getKeySize().toString();
        if (comboKeySize.getItems().contains(currentKey))
            comboKeySize.setValue(currentKey);
        else {
            comboKeySize.selectFirst();
        }
    }




    private void setupDynamicOptions() {
        comboOperationMode.setOnAction(event -> adjustOptionsBasedOnMode());
        comboAlgorithm.setOnAction(event -> adjustOptionsBasedOnAlgorithm());
    }

    private void adjustOptionsBasedOnMode() {
        String modeStr = comboOperationMode.getValue();
        SymmetricKeyMode mode = SymmetricKeyMode.fromString(modeStr);
        SymmetricKeyAlgorithm alg = config.getAlgorithm();
        if (mode == null) return;

        if (mode.requiresIV()) {
            int ivSize = mode.getFixedIVSize() > 0 ? mode.getFixedIVSize() : alg.getBaseIVSize();
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

        comboPadding.getItems().clear();
        comboPadding.setDisable(!supportsPadding);
        if (supportsPadding) {
            for (PaddingScheme p : mode.getSupportedPadding())
                comboPadding.getItems().add(p.toString());
            comboPadding.setValue(mode.supportsPadding(config.getPaddingScheme())
                    ? config.getPaddingScheme().toString()
                    : mode.getPaddingScheme().toString());
        } else {
            comboPadding.getItems().add(PaddingScheme.NO_PADDING.toString());
            comboPadding.setValue(PaddingScheme.NO_PADDING.toString());
        }

        comboKeySize.getItems().clear();
        for (KeySize ks : alg.getSupportedKeySizes())
            comboKeySize.getItems().add(ks.toString());
        if (!comboKeySize.getItems().isEmpty()) {
            String current = config.getKeySize().toString();
            comboKeySize.setValue(comboKeySize.getItems().contains(current)
                    ? current
                    : comboKeySize.getItems().get(0));
        }

        comboAuthentication.getItems().clear();
        comboAuthentication.setDisable(isGCM);
        if (isGCM) {
            comboAuthentication.getItems().add("N/A");
            comboAuthentication.setValue("N/A");
        } else {
            for (AuthenticationMethod m : AuthenticationMethod.values())
                comboAuthentication.getItems().add(m.getHMACAlgorithm() != null ? m.getHMACAlgorithm() : "No authentication");
            comboAuthentication.setValue(config.getAuthenticationMethod().getHMACAlgorithm() != null
                    ? config.getAuthenticationMethod().getHMACAlgorithm()
                    : "No authentication");
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
        SymmetricKeyConfig defaultConfig = ConfigManager.getDefaultSymmetricKeyConfig("DES");

        // Restablecer las opciones de cifrado
        comboOperationMode.setValue(defaultConfig.getMode().getMode());
        comboKeySize.setValue(defaultConfig.getKeySize().toString());
        comboPadding.setValue(defaultConfig.getPaddingScheme().toString());

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
