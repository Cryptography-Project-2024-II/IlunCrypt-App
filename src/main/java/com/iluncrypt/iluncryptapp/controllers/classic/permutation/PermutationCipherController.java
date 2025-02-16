package com.iluncrypt.iluncryptapp.controllers.classic.permutation;

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
import java.util.ResourceBundle;

/**
 * Controller for the Permutation Cipher encryption system.
 * Manages encryption, decryption, and UI interactions.
 */
public class PermutationCipherController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;

    // Stores the last entered values when switching methods
    private String lastPlainText = "";
    private String lastCipherText = "";
    private String lastPermutationKey = "";

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldPermutationKey;

    @FXML
    private MFXButton btnInfo;

    /**
     * Constructor for PermutationCipherController.
     * Initializes separate dialog instances to prevent interference.
     *
     * @param stage The primary stage used for managing dialogs.
     */
    public PermutationCipherController(Stage stage) {
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
        IlunCryptController.getInstance().loadView("CLASSIC-OPTIONS");
    }

    /**
     * Displays an informational dialog about the Permutation Cipher.
     */
    @FXML
    private void showInfoDialog() {
        infoDialog.showInfoDialog(
                "Permutation Cipher Information",
                """
                The Permutation Cipher is a transposition cipher that rearranges 
                the order of characters in a plaintext based on a given permutation key.

                Characteristics:
                - The key defines the rearrangement pattern.
                - The same key must be used for encryption and decryption.
                - Does not change letter frequencies, making it vulnerable to cryptanalysis.
                
                Example:
                - Plaintext: HELLO
                - Key: 3 1 4 5 2
                - Ciphertext: LHOEL
                """
        );
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

        if (textFieldPermutationKey != null) {
            lastPermutationKey = textFieldPermutationKey.getText();
        }
    }

    /**
     * Restores the previously entered values after switching methods.
     */
    @Override
    public void restorePreviousState() {
        textAreaPlainText.setText(lastPlainText);
        textAreaCipherText.setText(lastCipherText);

        if (textFieldPermutationKey != null) {
            textFieldPermutationKey.setText(lastPermutationKey);
        }
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
            String key = textFieldPermutationKey.getText();
            textAreaCipherText.setText(permutationEncrypt(plainText, key));
        }
    }

    @FXML
    private void decipherText() {
        String cipherText = textAreaCipherText.getText();
        if (!cipherText.isEmpty()) {
            String key = textFieldPermutationKey.getText();
            textAreaPlainText.setText(permutationDecrypt(cipherText, key));
        }
    }

    private String permutationEncrypt(String plainText, String key) {
        return "EncryptedText"; // Placeholder implementation
    }

    private String permutationDecrypt(String cipherText, String key) {
        return "DecryptedText"; // Placeholder implementation
    }

    /** Increment/Decrement Controls **/

    @FXML
    private void editPermutation(ActionEvent actionEvent) {
        System.out.println("Editing permutation key...");
    }

    /** Placeholder methods **/

    public void exportEncryptedMessage(ActionEvent actionEvent) {}

    public void showOtherSettings(ActionEvent actionEvent) {}
}
