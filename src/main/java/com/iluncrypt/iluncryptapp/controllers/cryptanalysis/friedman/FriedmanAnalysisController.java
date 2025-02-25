package com.iluncrypt.iluncryptapp.controllers.cryptanalysis.friedman;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.enums.Language;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Controller for Friedman Test Cryptanalysis.
 */
public class FriedmanAnalysisController implements CipherController, Initializable {
    private final DialogHelper infoDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;

    @FXML
    private MFXTableView tableCandidates;
    @FXML
    private MFXTableColumn colCandidate;

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaCandidates, textAreaTextToAttack;

    @FXML
    private MFXComboBox<String> comboBoxFormat;
    @FXML
    private MFXButton btnBack, btnInfo, btnChangeMethod, btnImportTextToAttack, btnClearTextToAttack, btnCopyTextToAttack, btnClear, btnAttack, btnCopyCandidate, btnSaveCandidate;


    public FriedmanAnalysisController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoDialog.setOwnerNode(grid);
        errorDialog.setOwnerNode(grid);
        comboBoxFormat.getItems().setAll(
                Arrays.stream(Language.values())
                        .map(Language::getDisplayName)
                        .toList()
        );
        setupButtonActions();
    }

    /**
     * Configures button actions for copying, regenerating, and importing files.
     */
    private void setupButtonActions() {
        btnBack.setOnAction(e -> handleBackButton());
        btnInfo.setOnAction(e -> showInfoDialog());
        btnChangeMethod.setOnAction(e -> showChangeMethodDialog());
        btnImportTextToAttack.setOnAction(e -> importTextToAttack());
        btnClearTextToAttack.setOnAction(e -> clearTextToAttack());
        btnCopyTextToAttack.setOnAction(e -> copyTextToAttack());
        btnClear.setOnAction(e -> clearAll());
        btnAttack.setOnAction(e -> attack());
        btnCopyCandidate.setOnAction(e -> copyCandidate());
        btnSaveCandidate.setOnAction(e -> saveCandidate());
    }

    private void saveCandidate() {

        if (textAreaCandidates.getText().isEmpty()) {
            errorDialog.showInfoDialog("Error","No decrypted data available to save.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Candidate");

        String originalExtension = "txt";

        // Configurar el filtro con la extensión original o txt
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Original Format (*." + originalExtension + ")", "*." + originalExtension);
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialFileName("decrypted." + originalExtension);

        File fileToSave = fileChooser.showSaveDialog(stage);
        if (fileToSave == null) {
            return; // Usuario canceló la operación
        }

        try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
            byte[] dataToSave;

            dataToSave = textAreaCandidates.getText().getBytes(StandardCharsets.UTF_8);

            fos.write(dataToSave);
            infoDialog.showInfoDialog("Success","Decrypted file saved successfully.");
        } catch (IOException e) {
            errorDialog.showInfoDialog("Failed to save decrypted file: ", e.getMessage());
        }
    }

    private void copyCandidate() {
        copyToClipboard(textAreaCandidates.getText());
    }

    private void attack() {
    }

    private void copyTextToAttack() {
        copyToClipboard(textAreaTextToAttack.getText());
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

    private void clearTextToAttack() {
        textAreaTextToAttack.clear();
    }

    private void importTextToAttack() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Encrypt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                textAreaTextToAttack.setText(content);
            } catch (IOException e) {
                errorDialog.showInfoDialog("Error", "Could not read file: " + e.getMessage());
            }
        }

    }

    @Override
    public void saveCurrentState() {}

    @Override
    public void restorePreviousState() {}

    @Override
    public void switchEncryptionMethod(String methodView) {}

    @Override
    public void closeDialog(DialogHelper dialog) {

    }

    @Override
    public void setConfig(CryptosystemConfig config) {

    }

    public void handleBackButton() {
        IlunCryptController.getInstance().loadView("CRIPTANALYSIS-OPTIONS");
    }

    public void showInfoDialog() {
    }

    public void showChangeMethodDialog() {
    }


    public void clearAll() {
        tableCandidates.getItems().clear();
        textAreaTextToAttack.clear();
        textAreaCandidates.clear();
    }
}