package com.iluncrypt.iluncryptapp.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImportIlunDialogController {

    @FXML private MFXButton btnSelectFile;
    @FXML private MFXButton btnImport;
    @FXML private MFXButton btnCancel;
    @FXML private MFXTextField textFieldFilePath;
    @FXML private TextArea textAreaPreview;

    private File selectedFile;

    @FXML
    private void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ilun Files (*.Ilun)", "*.Ilun"));
        fileChooser.setTitle("Select a .Ilun File");

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            selectedFile = file;
            textFieldFilePath.setText(file.getAbsolutePath());
            loadFilePreview(file);
        }
    }

    private void loadFilePreview(File file) {
        try {
            String content = Files.readString(file.toPath());
            textAreaPreview.setText(content.length() > 500 ? content.substring(0, 500) + "..." : content);
        } catch (IOException e) {
            textAreaPreview.setText("Error loading file preview.");
        }
    }

    @FXML
    private void handleImport() {
        if (selectedFile != null) {
            System.out.println("Importing: " + selectedFile.getAbsolutePath());
            // Aquí iría la lógica para importar el archivo en la aplicación
            closeDialog();
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
