package com.iluncrypt.iluncryptapp.controllers.classic.substitution;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.controllers.classic.ClassicCiphersDialogController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Substitution Cipher encryption system.
 * This class manages encryption, decryption, and UI interactions for the Substitution Cipher.
 */
public class SubstitutionCipherController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    @FXML
    private MFXButton btnCipher;
    @FXML
    private MFXButton btnDecipher;
    @FXML
    private MFXTextField textFieldPlainAlphabet;

    @FXML
    private MFXTextField textFieldCipherAlphabet;



    // Stores the last entered values when switching methods
    private String lastPlainText = "";
    private String lastCipherText = "";
    private String lastKey = "";

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
     * Constructor for SubstitutionCipherController.
     * Initializes separate dialog instances to prevent interference.
     *
     * @param stage The primary stage used for managing dialogs.
     */
    public SubstitutionCipherController(Stage stage) {
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
        btnCipher.setOnAction(e -> cipherText());
        btnDecipher.setOnAction(e -> decipherText());
    }

    private void generateRandomKey() {
        List<Character> shuffledAlphabet = new ArrayList<>();
        for (char c : ALPHABET.toCharArray()) {
            shuffledAlphabet.add(c);
        }
        Collections.shuffle(shuffledAlphabet);

        StringBuilder cipherAlphabet = new StringBuilder();
        for (char c : shuffledAlphabet) {
            cipherAlphabet.append(c);
        }

        // Establecer el alfabeto original (A-Z) y el cifrado en los textfields
        textFieldPlainAlphabet.setText(ALPHABET);
        textFieldCipherAlphabet.setText(cipherAlphabet.toString());
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
     * Displays an informational dialog about the Substitution Cipher.
     */
    @FXML
    private void showInfoDialog() {
        infoDialog.showInfoDialog(
                "Substitution Cipher Information",
                """
                The Substitution Cipher is a classical encryption technique 
                where each letter in the plaintext is replaced with another letter 
                based on a fixed key.

                Characteristics:
                - The key defines the letter mappings.
                - A monoalphabetic cipher uses a single substitution rule.
                - A polyalphabetic cipher changes substitution rules periodically.
                
                Example:
                - Plaintext: HELLO
                - Key: DQGZTYXWVRSMKNFJBOHELCPIAU
                - Ciphertext: VTXXM
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
                "views/classic/classic-ciphers-dialog-view.fxml",
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
        closeDialog(changeMethodDialog);
    }

    @Override
    public void closeDialog(DialogHelper dialog) {
        dialog.closeDialog();
    }

    @Override
    public void setConfig(CryptosystemConfig config) {

    }

    /**
     * Saves the current text values before switching methods.
     */
    @Override
    public void saveCurrentState() {
        lastPlainText = textAreaPlainText.getText();
        lastCipherText = textAreaCipherText.getText();

        if (textFieldKey != null) {
            lastKey = textFieldKey.getText();
        }
    }

    /**
     * Restores the previously entered values after switching methods.
     */
    @Override
    public void restorePreviousState() {
        textAreaPlainText.setText(lastPlainText);
        textAreaCipherText.setText(lastCipherText);

        if (textFieldKey != null) {
            textFieldKey.setText(lastKey);
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /** Encryption and Decryption **/

    @FXML
    private void cipherText() {
        String plainText = textAreaPlainText.getText();

        // Si no hay un alfabeto en los campos, generamos uno
        if (textFieldPlainAlphabet.getText().isEmpty() || textFieldCipherAlphabet.getText().isEmpty()) {
            generateRandomKey();
        }

        textAreaCipherText.setText(substitutionEncrypt(plainText));
    }



    @FXML
    private void decipherText() {
        String cipherText = textAreaCipherText.getText();

        if (textFieldPlainAlphabet.getText().isEmpty() || textFieldCipherAlphabet.getText().isEmpty()) {
            infoDialog.showInfoDialog("Error", "No key provided for decryption.");
            return;
        }

        textAreaPlainText.setText(substitutionDecrypt(cipherText));
    }



    private String substitutionEncrypt(String plainText) {
        plainText = cleanInput(plainText);
        String plainAlphabet = textFieldPlainAlphabet.getText();
        String cipherAlphabet = textFieldCipherAlphabet.getText();

        StringBuilder cipherText = new StringBuilder();

        for (char c : plainText.toCharArray()) {
            int index = plainAlphabet.indexOf(c);
            cipherText.append(cipherAlphabet.charAt(index));
        }
        return cipherText.toString();
    }



    private String substitutionDecrypt(String cipherText) {
        cipherText = cleanInput(cipherText);
        String plainAlphabet = textFieldPlainAlphabet.getText();
        String cipherAlphabet = textFieldCipherAlphabet.getText();

        StringBuilder plainText = new StringBuilder();

        for (char c : cipherText.toCharArray()) {
            int index = cipherAlphabet.indexOf(c);
            plainText.append(plainAlphabet.charAt(index));
        }
        return plainText.toString();
    }


    private String cleanInput(String input) {
        return input.toUpperCase().replaceAll("[^A-Z]", "");
    }

    /** Increment/Decrement Controls **/

    @FXML
    private void editSubstitution(ActionEvent actionEvent) {
        System.out.println("Editing substitution key...");
    }

    /** Placeholder methods **/

    public void exportEncryptedMessage(ActionEvent actionEvent) {}

    public void showOtherSettings(ActionEvent actionEvent) {}
}
