package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.utils.ConfigManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class ChangeAlphabetController implements Initializable {

    @FXML
    private MFXTableView<Map<String, String>> tableAlphabets;

    @FXML
    private MFXTableView<Map<String, String>> tableAlphabetDetails;

    @FXML
    private MFXButton btnCancel, btnSelect, btnCopyClipboard;

    @FXML
    private Label lblSelectedMethod, lblAlphabetDetails;

    private final Stage stage;
    private final String cipherMethod;
    private final String textType;
    private Map<String, String> selectedAlphabet;
    private final DialogHelper dialogHelper;

    /**
     * Constructor with parameters to define the encryption method and type of text.
     */
    public ChangeAlphabetController(Stage stage, String cipherMethod, String textType) {
        this.stage = stage;
        this.cipherMethod = cipherMethod;
        this.textType = textType;
        this.dialogHelper = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblSelectedMethod.setText(cipherMethod);
        loadAlphabets();
        setupTableSelectionHandler();
    }

    /**
     * Loads available alphabets into the table.
     */
    private void loadAlphabets() {
        /*List<Map<String, String>> alphabets = ConfigManager.getAvailableAlphabets();
        ObservableList<Map<String, String>> observableAlphabets = FXCollections.observableArrayList(alphabets);
        tableAlphabets.setItems(observableAlphabets);

        MFXTableColumn<Map<String, String>> colName = new MFXTableColumn<>("Name", true);
        MFXTableColumn<Map<String, String>> colDescription = new MFXTableColumn<>("Description", true);
        MFXTableColumn<Map<String, String>> colSize = new MFXTableColumn<>("Size", true);

        colName.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.getOrDefault("name", "Unknown")));
        colDescription.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.getOrDefault("description", "No description")));
        colSize.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.getOrDefault("size", "0")));

        tableAlphabets.getTableColumns().setAll(colName, colDescription, colSize);*/
    }

    /**
     * Handles alphabet selection and updates the details table.
     */
    private void setupTableSelectionHandler() {
        tableAlphabets.getSelectionModel().selectionProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && !newSelection.isEmpty()) {
                selectedAlphabet = newSelection.get(0);
                updateAlphabetDetails(selectedAlphabet);
            }
        });
    }

    /**
     * Updates the table with the details of the selected alphabet.
     */
    private void updateAlphabetDetails(Map<String, String> alphabet) {
        /*lblAlphabetDetails.setText("Alphabet Details: " + alphabet.getOrDefault("name", "Unknown"));

        List<Map<String, String>> details = ConfigManager.getAlphabetDetails(alphabet.get("name"));
        ObservableList<Map<String, String>> observableDetails = FXCollections.observableArrayList(details);
        tableAlphabetDetails.setItems(observableDetails);

        MFXTableColumn<Map<String, String>> colSymbol = new MFXTableColumn<>("Symbol", true);
        MFXTableColumn<Map<String, String>> colNumericAssignment = new MFXTableColumn<>("Numeric Assignment", true);

        colSymbol.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.getOrDefault("symbol", "?")));
        colNumericAssignment.setRowCellFactory(data -> new MFXTableRowCell<>(map -> map.getOrDefault("numeric", "N/A")));

        tableAlphabetDetails.getTableColumns().setAll(colSymbol, colNumericAssignment);*/
    }

    /**
     * Saves the selected alphabet to the configuration.
     */
    @FXML
    private void saveSelection() {
       /* if (selectedAlphabet != null) {
            ConfigManager.setAlphabetFor(cipherMethod, textType, selectedAlphabet.get("name"));
            dialogHelper.closeDialog();
        }*/
    }

    /**
     * Closes the dialog without saving.
     */
    @FXML
    private void closeDialog() {
        dialogHelper.closeDialog();
    }

    /**
     * Copies the details of the selected alphabet to the clipboard.
     */
    @FXML
    private void copyToClipboard() {
        if (selectedAlphabet != null) {
            StringBuilder clipboardText = new StringBuilder("Alphabet: " + selectedAlphabet.getOrDefault("name", "Unknown") + "\n");

            for (Map<String, String> entry : tableAlphabetDetails.getItems()) {
                clipboardText.append(entry.getOrDefault("symbol", "?"))
                        .append(": ")
                        .append(entry.getOrDefault("numeric", "N/A"))
                        .append("\n");
            }

            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(clipboardText.toString());
            clipboard.setContent(content);
        }
    }

    public void handleAlphabetSelection(MouseEvent mouseEvent) {

    }
}
