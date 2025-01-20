package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.enums.AlphabetPreset;
import com.iluncrypt.iluncryptapp.utils.ConfigManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ManageAlphabetController {

    @FXML
    private MFXTableView<Alphabet> tableAlphabets;

    @FXML
    private MFXTableColumn<Alphabet> colName;

    @FXML
    private MFXTableColumn<Alphabet> colDescription;

    @FXML
    private MFXTableColumn<Alphabet> colSize;

    @FXML
    private MFXTableView<Map.Entry<String, Integer>> tableAlphabetDetails;

    @FXML
    private MFXTableColumn<Map.Entry<String, Integer>> colSymbol;

    @FXML
    private MFXTableColumn<Map.Entry<String, Integer>> colNumericAssignment;

    @FXML
    private Label lblSelectedMethod;

    @FXML
    private MFXButton btnAddAlphabet;

    @FXML
    private MFXButton btnEditAlphabet;

    @FXML
    private MFXButton btnCopyClipboard;

    /**
     * Inicializa la vista cargando la tabla de alfabetos.
     */
    @FXML
    public void initialize() {
        setupAlphabetTable();
        setupAlphabetDetailsTable();
        loadAllAlphabets();

        tableAlphabets.getSelectionModel().selectionProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleAlphabetSelection();
            }
        });

        btnEditAlphabet.setDisable(true);
    }

    /**
     * Configura la tabla de alfabetos.
     */
    private void setupAlphabetTable() {
        colName = new MFXTableColumn<>("Name", true, Comparator.comparing(Alphabet::getName));
        colDescription = new MFXTableColumn<>("Description", true, Comparator.comparing(Alphabet::getDescription));
        colSize = new MFXTableColumn<>("Size", true, Comparator.comparingInt(a -> a.getCharacters().size()));

        colName.setRowCellFactory(alphabet -> new MFXTableRowCell<>(Alphabet::getName));
        colDescription.setRowCellFactory(alphabet -> new MFXTableRowCell<>(Alphabet::getDescription));
        colSize.setRowCellFactory(alphabet -> new MFXTableRowCell<>(a -> String.valueOf(a.getCharacters().size())));

        tableAlphabets.getTableColumns().setAll(colName, colDescription, colSize);
    }

    /**
     * Carga todos los alfabetos (predefinidos y personalizados) en la tabla.
     */
    private void loadAllAlphabets() {
        List<Alphabet> alphabets = ConfigManager.getAllAlphabets();
        tableAlphabets.getItems().setAll(alphabets);
    }

    /**
     * Configura la tabla de detalles del alfabeto seleccionado.
     */
    private void setupAlphabetDetailsTable() {
        colSymbol = new MFXTableColumn<>("Symbol", true, Comparator.comparing(Map.Entry::getKey));
        colNumericAssignment = new MFXTableColumn<>("Numeric Assignment", true, Comparator.comparingInt(Map.Entry::getValue));

        colSymbol.setRowCellFactory(symbol -> new MFXTableRowCell<>(entry -> entry.getKey()));
        colNumericAssignment.setRowCellFactory(number -> new MFXTableRowCell<>(entry -> String.valueOf(entry.getValue())));

        tableAlphabetDetails.getTableColumns().setAll(colSymbol, colNumericAssignment);
    }

    /**
     * Carga los detalles del alfabeto seleccionado en la tabla de detalles.
     *
     * @param alphabet Alfabeto seleccionado.
     */
    private void loadAlphabetDetails(Alphabet alphabet) {
        setupAlphabetDetailsTable();

        List<Map.Entry<String, Integer>> alphabetEntries = IntStream.range(0, alphabet.getCharacters().size())
                .mapToObj(i -> Map.entry(String.valueOf(alphabet.getCharacters().get(i)), i))
                .collect(Collectors.toList());

        tableAlphabetDetails.getItems().clear();
        tableAlphabetDetails.getItems().addAll(alphabetEntries);
    }

    /**
     * Maneja la selección de un alfabeto de la tabla principal.
     *
     * @param event Evento de clic en la tabla.
     */
    @FXML
    public void handleAlphabetSelection() {
        Alphabet selectedAlphabet = tableAlphabets.getSelectionModel().getSelectedValue();
        if (selectedAlphabet != null) {
            lblSelectedMethod.setText(selectedAlphabet.getName());
            loadAlphabetDetails(selectedAlphabet);

            // Si es predefinido, deshabilitar el botón de edición
            btnEditAlphabet.setDisable(isPredefinedAlphabet(selectedAlphabet));
        }
    }

    /**
     * Verifica si un alfabeto es predefinido.
     *
     * @param alphabet Alfabeto a comprobar.
     * @return true si es un alfabeto predefinido, false si es un alfabeto personalizado.
     */
    private boolean isPredefinedAlphabet(Alphabet alphabet) {
        return AlphabetPreset.getPredefinedAlphabets().stream()
                .anyMatch(predef -> predef.getName().equals(alphabet.getName()));
    }


    /**
     * Abre un diálogo para añadir un nuevo alfabeto.
     *
     * @param event Evento de clic en el botón "Add Alphabet".
     */
    @FXML
    public void addAlphabet(MouseEvent event) {
        System.out.println("Add Alphabet dialog opened.");
        // Aquí se abriría un diálogo para añadir un alfabeto
    }

    /**
     * Abre un diálogo para editar el alfabeto seleccionado.
     *
     * @param event Evento de clic en el botón "Edit Alphabet".
     */
    @FXML
    public void editAlphabet(MouseEvent event) {
        if (!tableAlphabets.getSelectionModel().getSelectedValues().isEmpty()) {
            Alphabet selectedAlphabet = tableAlphabets.getSelectionModel().getSelectedValues().get(0);
            System.out.println("Editing Alphabet: " + selectedAlphabet.getName());
            // Aquí se abriría un diálogo para editar el alfabeto seleccionado
        } else {
            System.out.println("No alphabet selected.");
        }
    }

    /**
     * Copia los detalles del alfabeto seleccionado al portapapeles.
     *
     * @param event Evento de clic en el botón "Copy to Clipboard".
     */
    @FXML
    public void copyToClipboard(MouseEvent event) {
        if (!tableAlphabetDetails.getItems().isEmpty()) {
            StringBuilder clipboardContent = new StringBuilder("Alphabet Details:\n");

            for (Map.Entry<String, Integer> entry : tableAlphabetDetails.getItems()) {
                clipboardContent.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
            }

            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(clipboardContent.toString());
            clipboard.setContent(content);

            System.out.println("Copied to clipboard.");
        } else {
            System.out.println("No alphabet selected to copy.");
        }
    }
}
