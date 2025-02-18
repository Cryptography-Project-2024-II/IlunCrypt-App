package com.iluncrypt.iluncryptapp.controllers.classic.affine;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.ContainerDialogController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.controllers.OtherSettingsController;
import com.iluncrypt.iluncryptapp.controllers.classic.ClassicCiphersDialogController;
import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.ClassicCipherConfig;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.classic.AffineCipher;
import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import com.iluncrypt.iluncryptapp.models.enums.WhitespaceHandling;
import com.iluncrypt.iluncryptapp.models.keys.AffineKey;
import com.iluncrypt.iluncryptapp.utils.ConfigManager;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

/**
 * Controller for the Affine Cipher encryption system.
 * Manages UI interactions, and related settings.
 */
public class AffineCipherController implements CipherController, Initializable {

    // Alphabets and configurations
    private Alphabet plaintextAlphabet;
    private Alphabet ciphertextAlphabet;
    private Alphabet keyAlphabet;
    private CaseHandling caseHandling;
    private UnknownCharHandling unknownCharHandling;
    private WhitespaceHandling whitespaceHandling;
    private int alphabetSize;
    private ClassicCipherConfig config;

    // Dialog helpers
    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;

    // Stores the last entered values when switching methods
    private String lastPlainText = "";
    private String lastCipherText = "";
    private int lastA = 1;
    private int lastB = 0;

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldA;

    @FXML
    private MFXTextField textFieldB;

    /**
     * Constructor for AffineCipherController.
     * Initializes dialog instances and sets up the main stage reference.
     *
     * @param stage The primary stage used for managing dialogs.
     */
    public AffineCipherController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
        this.changeMethodDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
        this.config = ConfigManager.loadClassicCipherConfig("AFFINE-CIPHER");
    }


    /**
     * Initializes the controller and configures UI layout and dialogs.
     *
     * @param location The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureGridGrowth();
        configureDialogs();
        loadConfig();
    }

    /**
     * Loads the Affine Cipher configuration from the configuration file.
     * Ensures alphabets and settings are valid and consistent.
     */
    private void loadConfig() {
        plaintextAlphabet = config.getPlaintextAlphabet();
        ciphertextAlphabet = config.getCiphertextAlphabet();

        if (plaintextAlphabet == null || ciphertextAlphabet == null) {
            throw new IllegalArgumentException("Both alphabets must be valid and cannot be null.");
        }

        if (plaintextAlphabet.size() != ciphertextAlphabet.size()) {
            throw new IllegalArgumentException("Both alphabets must have the same size.");
        }

        alphabetSize = plaintextAlphabet.size();
        keyAlphabet = Alphabet.generateZAlphabet(alphabetSize);
        config.setKeyAlphabet(keyAlphabet);
        caseHandling = config.getCaseHandling();
        unknownCharHandling = config.getUnknownCharHandling();
        whitespaceHandling = config.getWhitespaceHandling();

        if (caseHandling == null || unknownCharHandling == null) {
            throw new IllegalArgumentException("Handling settings must be valid and cannot be null.");
        }
    }

    /**
     * Configures the properties of the GridPane to support responsive layout.
     */
    private void configureGridGrowth() {
        grid.setMaxHeight(Double.MAX_VALUE);
        StackPane.setAlignment(grid, Pos.CENTER);
        grid.getRowConstraints().forEach(row -> row.setVgrow(Priority.ALWAYS));
        grid.getColumnConstraints().forEach(column -> column.setHgrow(Priority.ALWAYS));
    }

    /**
     * Configures dialog instances for consistent usage throughout the application.
     */
    private void configureDialogs() {
        infoDialog.setOwnerNode(grid);
        changeMethodDialog.setOwnerNode(grid);
        errorDialog.setOwnerNode(grid);
    }

    /**
     * Navigates back to the encryption method selection menu.
     */
    @FXML
    private void handleBackButton() {
        IlunCryptController.getInstance().loadView("CLASSIC-OPTIONS");
    }

    /**
     * Displays an informational dialog about the Affine Cipher.
     */
    @FXML
    private void showInfoDialog() {
        infoDialog.enableDynamicSize(0.6, 0.6); // Enable dynamic size (60% of the main window)

        infoDialog.showFXMLDialog(
                "Affine Cipher Information",
                "views/container-dialog-view.fxml",  // Load the container
                ContainerDialogController::new,
                new MFXFontIcon("fas-circle-info", 18),
                "mfx-dialog",
                false,
                false,
                controller -> {
                        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
                        controller.loadContent("views/classic/affine/affine-cipher-description-view.fxml", bundle); // Load the description view inside the container
                }
        );
    }

    /**
     * Displays a dialog for changing the encryption method.
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
     * Saves the current text and key values before switching methods.
     */
    @Override
    public void saveCurrentState() {
        lastPlainText = textAreaPlainText.getText();
        lastCipherText = textAreaCipherText.getText();
        lastA = parseInt(textFieldA.getText());
        lastB = parseInt(textFieldB.getText());
    }

    /**
     * Restores the saved text and key values after switching methods.
     */
    @Override
    public void restorePreviousState() {
        textAreaPlainText.setText(lastPlainText);
        textAreaCipherText.setText(lastCipherText);
        textFieldA.setText(String.valueOf(lastA));
        textFieldB.setText(String.valueOf(lastB));
    }

    /**
     * Clears the text areas for plaintext and ciphertext.
     */
    @FXML
    private void clearTextAreas() {
        textAreaPlainText.clear();
        textAreaCipherText.clear();
    }

    /** Encryption and Decryption **/

    /**
     * Encrypts the plaintext using the Affine Cipher and displays the ciphertext.
     */
    @FXML
    private void cipherText() {
        String plainText = textAreaPlainText.getText();
        if (!plainText.isEmpty()) {
            int a = parseInt(textFieldA.getText());
            int b = parseInt(textFieldB.getText());
            try {
                AffineKey key = new AffineKey(a, b, this.keyAlphabet);
                AffineCipher affineCipher = new AffineCipher(plaintextAlphabet, ciphertextAlphabet,
                                                caseHandling, unknownCharHandling, whitespaceHandling);
                textAreaCipherText.setText(affineCipher.encrypt(plainText, key));
            } catch (IllegalArgumentException e) {
                errorDialog.showInfoDialog("Encryption Error", e.getMessage());
            }
        }
    }

    /**
     * Decrypts the ciphertext using the Affine Cipher and displays the plaintext.
     */
    @FXML
    private void decipherText() {
        String cipherText = textAreaCipherText.getText();
        if (!cipherText.isEmpty()) {
            int a = parseInt(textFieldA.getText());
            int b = parseInt(textFieldB.getText());
            try {
                AffineKey key = new AffineKey(a, b, this.keyAlphabet);
                AffineCipher affineCipher = new AffineCipher(plaintextAlphabet, ciphertextAlphabet,
                                                caseHandling, unknownCharHandling, whitespaceHandling);
                textAreaPlainText.setText(affineCipher.decrypt(cipherText, key));
            } catch (IllegalArgumentException e) {
                errorDialog.showInfoDialog("Decryption Error", e.getMessage());
            }
        }
    }



    /** Increment/Decrement Controls **/

    /**
     * Decrements the value in textFieldA to the next coprime value with the alphabet size.
     * This method ensures that the updated value is within the valid range and is coprime
     * to the alphabet size.
     */
    @FXML
    private void decrementA() {
        updateCoprimeValue(textFieldA, -1, 0, this.alphabetSize - 1);
    }

    /**
     * Increments the value in textFieldA to the next coprime value with the alphabet size.
     * This method ensures that the updated value is within the valid range and is coprime
     * to the alphabet size.
     */
    @FXML
    private void incrementA() {
        updateCoprimeValue(textFieldA, 1, 0, this.alphabetSize - 1);
    }

    /**
     * Decrements the value in textFieldB by 1 while keeping the value within the valid range.
     */
    @FXML
    private void decrementB() {
        updateValue(textFieldB, -1, 0, this.alphabetSize - 1);
    }

    /**
     * Increments the value in textFieldB by 1 while keeping the value within the valid range.
     */
    @FXML
    private void incrementB() {
        updateValue(textFieldB, 1, 0, this.alphabetSize - 1);
    }

    /**
     * Updates the value of the given text field by applying the specified delta.
     * Ensures that the resulting value remains within the specified range.
     *
     * @param textField the text field to update
     * @param delta the value to add (or subtract) from the current value
     * @param min the minimum allowable value
     * @param max the maximum allowable value
     * @throws NumberFormatException if the text in the field is not a valid integer
     */
    private void updateValue(MFXTextField textField, int delta, int min, int max) {
        int value = parseInt(textField.getText());
        int newValue = (value + delta) % this.alphabetSize;
        if (newValue < min) newValue = max;
        textField.setText(String.valueOf(newValue));
    }

    /**
     * Updates the value of the given text field by applying the specified delta
     * until the new value is coprime with the alphabet size. Ensures that the
     * resulting value remains within the specified range.
     *
     * @param textField the text field to update
     * @param delta the value to add (or subtract) from the current value
     * @param min the minimum allowable value
     * @param max the maximum allowable value
     * @throws NumberFormatException if the text in the field is not a valid integer
     */
    private void updateCoprimeValue(MFXTextField textField, int delta, int min, int max) {
        int value = parseInt(textField.getText());
        int newValue = value;

        do {
            newValue = (newValue + delta) % this.alphabetSize;
            if (newValue < min) newValue = max; // Ensures the value stays within the range
        } while (AffineKey.gcd(newValue, this.alphabetSize) != 1);

        textField.setText(String.valueOf(newValue));
    }


    /** Additional Settings **/

    @FXML
    private void showOtherSettings() {
        changeMethodDialog.showFXMLDialog(
                "Other Settings (Affine Cipher)",
                "views/other-settings-view.fxml",
                () -> new OtherSettingsController(this.stage, this.config, changeMethodDialog),
                new MFXFontIcon("fas-gear", 18),
                "mfx-dialog",
                false,
                false,
                controller -> {
                        controller.setParentController(this);
                    }
        );
    }

    public void exportEncryptedText(ActionEvent actionEvent) {
    }

    public void showCryptanalysisDialog(ActionEvent actionEvent) {
    }

    public void importPlainText(ActionEvent actionEvent) {
    }

    public void copyPlainText(ActionEvent actionEvent) {
    }

    public void importCipherText(ActionEvent actionEvent) {
    }

    public void copyCipherText(ActionEvent actionEvent) {
        
    }
}
