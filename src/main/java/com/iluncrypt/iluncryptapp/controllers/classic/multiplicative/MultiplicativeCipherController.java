package com.iluncrypt.iluncryptapp.controllers.classic.multiplicative;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.controllers.classic.ClassicCiphersDialogController;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Multiplicative Cipher encryption system.
 * This class manages encryption, decryption, and UI interactions for the Multiplicative Cipher.
 */
public class MultiplicativeCipherController implements CipherController, Initializable {

    private static final int ALPHABET_SIZE = 26;

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;

    // Stores the last entered values when switching methods
    private String lastPlainText = "";
    private String lastCipherText = "";
    private int lastKey = 1;

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldKey;

    @FXML
    private MFXButton btnInfo;

    /**
     * Constructor for MultiplicativeCipherController.
     * Initializes separate dialog instances to prevent interference.
     *
     * @param stage The primary stage used for managing dialogs.
     */
    public MultiplicativeCipherController(Stage stage) {
        this.infoDialog = new DialogHelper(stage);
        this.changeMethodDialog = new DialogHelper(stage);
    }

    /**
     * Initializes the controller and configures the UI layout.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureGridGrowth();
        configureDialogs();
    }

    /**
     * Configures the properties of the GridPane for responsive behavior.
     */
    private void configureGridGrowth() {
        grid.setMaxHeight(Double.MAX_VALUE);
        StackPane.setAlignment(grid, Pos.CENTER);
        grid.getRowConstraints().forEach(row -> row.setVgrow(Priority.ALWAYS));
        grid.getColumnConstraints().forEach(column -> column.setHgrow(Priority.ALWAYS));
    }

    /**
     * Configures both dialogs to ensure they are correctly initialized before being displayed.
     */
    private void configureDialogs() {
        infoDialog.setOwnerNode(grid);
        changeMethodDialog.setOwnerNode(grid);
    }

    /**
     * Navigates back to the encryption method selection menu.
     */
    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("ENCRYPT-DECRYPT-OPTIONS");
    }

    /**
     * Displays an informational dialog about the Multiplicative Cipher.
     */
    @FXML
    private void showInfoDialog() {
        infoDialog.showInfoDialog(
                "Affine Cipher",
                """
                The Affine Cipher is a type of substitution cipher that uses a mathematical function 
                to encrypt and decrypt text.
    
                Encryption formula:
                E(x) = (a * x + b) mod m
    
                Where:
                - 'x' is the position of the plaintext letter in the alphabet (starting from 0).
                - 'a' and 'b' are keys used for encryption.
                - 'm' is the size of the alphabet (e.g., 26 for English letters).
    
                Decryption formula:
                D(x) = a_inv * (x - b) mod m
    
                Where 'a_inv' is the modular multiplicative inverse of 'a' modulo 'm'.
    
                Important Notes:
                - The key 'a' must be coprime with 'm' for the cipher to work correctly.
                - This cipher is vulnerable to frequency analysis if the ciphertext is long enough.
                """
        );

            /*infoDialog.showFXMLDialog(
                    "Affine Cipher Information", // Dialog title
                    "views/affine-cipher-description-view.fxml", // Path to the FXML file
                    new MFXFontIcon("fas-info-circle", 18), // Icon
                    "mfx-fxml-dialog", // Custom style class for the dialog
                    false // Not blocking
            );*/

    }

    /**
     * Displays a dialog to change the encryption method.
     */
    @FXML
    private void showChangeMethodDialog() {
        changeMethodDialog.showFXMLDialog(
                "Cipher/Decipher Methods",
                "views/classic-ciphers-dialog-view.fxml",
                new MFXFontIcon("fas-list", 18),
                "mfx-fxml-dialog",
                false,
                false,
                controller -> {
                    if (controller instanceof ClassicCiphersDialogController dialogController) {
                        dialogController.setParentController(this);
                    }
                }
        );
    }

    /**
     * Switches the encryption method, updates the main view, and restores text values.
     *
     * @param methodView The encryption method view to load.
     */
    @Override
    public void switchEncryptionMethod(String methodView) {
        saveCurrentState();
        IlunCryptController.getInstance().loadView(methodView);
        restorePreviousState();
        closeOptionsDialog();
    }

    @Override
    public void closeOptionsDialog() {
        changeMethodDialog.closeDialog();
    }

    /**
     * Saves the current text values before switching methods.
     */
    @Override
    public void saveCurrentState() {
        lastPlainText = textAreaPlainText.getText();
        lastCipherText = textAreaCipherText.getText();
        lastKey = parseInt(textFieldKey.getText(), 1);
    }

    /**
     * Restores the previously entered values after switching methods.
     */
    @Override
    public void restorePreviousState() {
        textAreaPlainText.setText(lastPlainText);
        textAreaCipherText.setText(lastCipherText);
        textFieldKey.setText(String.valueOf(lastKey));
    }

    /** Utility Methods **/

    @FXML
    private void clearTextAreas() {
        textAreaPlainText.clear();
        textAreaCipherText.clear();
    }

    @FXML
    private void showCryptanalysisDialog() {
        if (textAreaCipherText.getText().isEmpty()) {
            showAlert("Cryptanalysis Error", "No Cipher Text Found", "Please enter cipher text to perform cryptanalysis.");
        } else {
            showAlert("Perform Cryptanalysis", "Confirm Cryptanalysis", "Proceed with cryptanalysis on the cipher text?");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /** Encryption and Decryption **/

    @FXML
    private void cipherText() {
        String plainText = textAreaPlainText.getText();
        if (!plainText.isEmpty()) {
            int key = parseInt(textFieldKey.getText(), 1);
            textAreaCipherText.setText(multiplicativeEncrypt(plainText, key));
        }
    }

    @FXML
    private void decipherText() {
        String cipherText = textAreaCipherText.getText();
        if (!cipherText.isEmpty()) {
            int key = parseInt(textFieldKey.getText(), 1);
            textAreaPlainText.setText(multiplicativeDecrypt(cipherText, key));
        }
    }

    private String multiplicativeEncrypt(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                result.append((char) ((key * (c - base) % ALPHABET_SIZE) + base));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String multiplicativeDecrypt(String text, int key) {
        int inverseKey = multiplicativeInverse(key, ALPHABET_SIZE);
        if (inverseKey == -1) {
            return "Error: No multiplicative inverse exists.";
        }
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                result.append((char) ((inverseKey * (c - base) % ALPHABET_SIZE + ALPHABET_SIZE) % ALPHABET_SIZE + base));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private int multiplicativeInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1;
    }

    /** Increment/Decrement Controls **/

    @FXML
    private void decrementKey() {
        updateValue(textFieldKey, -1, 1, ALPHABET_SIZE - 1);
    }

    @FXML
    private void incrementKey() {
        updateValue(textFieldKey, 1, 1, ALPHABET_SIZE - 1);
    }

    private void updateValue(MFXTextField textField, int delta, int min, int max) {
        int value = parseInt(textField.getText(), min);
        textField.setText(String.valueOf(Math.max(min, Math.min(value + delta, max))));
    }

    private int parseInt(String text, int defaultValue) {
        return Optional.ofNullable(text).filter(t -> !t.isEmpty()).map(Integer::parseInt).orElse(defaultValue);
    }

    /** Placeholder methods **/

    public void exportEncryptedMessage(ActionEvent actionEvent) {}

    public void showOtherSettings(ActionEvent actionEvent) {}
}
