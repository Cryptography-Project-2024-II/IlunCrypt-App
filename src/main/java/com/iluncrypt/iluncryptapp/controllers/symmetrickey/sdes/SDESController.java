package com.iluncrypt.iluncryptapp.controllers.symmetrickey.sdes;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.ContainerDialogController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.controllers.classic.ClassicCiphersDialogController;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.symmetrickey.SDESCryptosystem;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.LanguageManager;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Simplified DES (SDES) encryption system.
 * <p>
 * Manages encryption, decryption, and UI interactions for SDES.
 * </p>
 */
public class SDESController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;
    private final DialogHelper errorDialog;
    private final Stage stage;

    // Stores the last entered values when switching methods
    private String lastPlainTextLetters = "";
    private String lastPlainTextBits = "";
    private String lastCipherTextLetters = "";
    private String lastCipherTextBits = "";
    private String lastKey = "0000000000"; // Default 10-bit key

    // Flags to prevent infinite loops during conversion
    private boolean updatingPlain = false;
    private boolean updatingCipher = false;

    @FXML
    private GridPane grid;

    // Plain text fields
    @FXML
    private MFXTextField mfxPlainTextLetters;
    @FXML
    private MFXTextField mfxPlainTextBits;

    // Encrypted text fields
    @FXML
    private MFXTextField mfxCipherTextLetters;
    @FXML
    private MFXTextField mfxCipherTextBits;

    @FXML
    private MFXTextField textFieldKey;

    @FXML
    private MFXCheckbox checkGenerateKey;

    /**
     * Constructor.
     *
     * @param stage The primary stage.
     */
    public SDESController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
        this.changeMethodDialog = new DialogHelper(stage);
        this.errorDialog = new DialogHelper(stage);
    }

    /**
     * Initializes the controller and sets up listeners.
     *
     * @param location  The location used to resolve relative paths.
     * @param resources The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureDialogs();
        // Listener para campo de texto en modo "Plain Text"
        mfxPlainTextLetters.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!updatingPlain && newVal.length() == 2) {
                updatingPlain = true;
                String bits = letterToBits(newVal);
                mfxPlainTextBits.setText(bits);
                updatingPlain = false;
            }
        });

        mfxPlainTextBits.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!updatingPlain && newVal.length() == 8) {
                updatingPlain = true;
                String letters = bitsToLetters(newVal);
                mfxPlainTextLetters.setText(letters);
                updatingPlain = false;
            }
        });

// Listener para campo de texto en modo "Cipher Text"
        mfxCipherTextLetters.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!updatingCipher && newVal.length() == 2) {
                updatingCipher = true;
                String bits = letterToBits(newVal);
                mfxCipherTextBits.setText(bits);
                updatingCipher = false;
            }
        });

        mfxCipherTextBits.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!updatingCipher && newVal.length() == 8) {
                updatingCipher = true;
                String letters = bitsToLetters(newVal);
                mfxCipherTextLetters.setText(letters);
                updatingCipher = false;
            }
        });

    }

    /**
     * Configures the dialogs.
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
        IlunCryptController.getInstance().loadView("SYMMETRIC-KEY-ENCRYPTION");
    }

    /**
     * Displays an informational dialog about SDES.
     */
    @FXML
    private void showInfoDialog() {
        infoDialog.enableDynamicSize(0.6, 0.6);

        infoDialog.showFXMLDialog(
                "Simplified Data Encryption Standard (S-DES) Information",
                "views/container-dialog-view.fxml",  // Load the container
                ContainerDialogController::new,
                new MFXFontIcon("fas-circle-info", 18),
                "mfx-dialog",
                false,
                false,
                controller -> {
                    ResourceBundle bundle = LanguageManager.getInstance().getBundle();
                    controller.loadContent("views/symmetric-key/sdes/sdes-description-view.fxml", bundle); // Load the description view inside the container
                }
        );
    }

    /**
     * Encrypts the plain text using SDES.
     * <p>
     * If the checkbox "Generate key automatically" is selected and the key field is not full (10 characters),
     * a new key is generated. Otherwise, the existing key is used.
     * </p>
     *
     * @param actionEvent The action event triggered by the Encrypt button.
     */
    @FXML
    public void encrypt(ActionEvent actionEvent) {
        String input = "";
        boolean inputIsLetters = false;

        if (!mfxPlainTextLetters.getText().trim().isEmpty() && mfxPlainTextLetters.isEditable()) {
            // Validate that exactly 2 valid letters are provided
            String letters = mfxPlainTextLetters.getText().trim().toUpperCase();
            if (!letters.matches("^[A-P]{2}$")) {
                errorDialog.showInfoDialog("Error", "Plain Text Letters must consist of exactly 2 characters (A–P).");
                return;
            }
            input = letters;
            inputIsLetters = true;
        } else if (!mfxPlainTextBits.getText().trim().isEmpty() && mfxPlainTextBits.isEditable()) {
            if (mfxPlainTextBits.getText().trim().length() != 8) {
                errorDialog.showInfoDialog("Error", "Plain Text Bits must be exactly 8 bits.");
                return;
            }
            input = mfxPlainTextBits.getText().trim();
        } else {
            errorDialog.showInfoDialog("Error", "Please enter plain text in one of the fields.");
            return;
        }

        String key = textFieldKey.getText().trim();
        if (checkGenerateKey.isSelected()) {
            // Only generate a new key if the key field is not already full (10 characters)
            if (key.length() < 10) {
                key = SDESCryptosystem.generateRandomKey();
                textFieldKey.setText(key);
            }
        } else {
            if (key.isEmpty() || key.length() != 10 || !isValidKey(key)) {
                errorDialog.showInfoDialog("Error", "Invalid key. Ensure the key is a 10-bit binary string.");
                return;
            }
        }

        try {
            SDESCryptosystem sdes = new SDESCryptosystem();
            String cipherResult = sdes.encrypt(input, key);
            // Always update both encrypted fields using conversion methods:
            String cipherResultLetters, cipherResultBits;
            if (inputIsLetters) {
                cipherResultLetters = cipherResult;
                cipherResultBits = letterToBits(cipherResult);
            } else {
                cipherResultBits = cipherResult;
                cipherResultLetters = bitsToLetters(cipherResult);
            }
            mfxCipherTextLetters.setText(cipherResultLetters);
            mfxCipherTextBits.setText(cipherResultBits);
        } catch (IllegalArgumentException e) {
            errorDialog.showInfoDialog("Encryption Error", e.getMessage());
        }

    }

    /**
     * Decrypts the encrypted text using SDES.
     * <p>
     * If the checkbox "Generate key automatically" is selected and the key field is not full (10 characters),
     * a new key is generated. Otherwise, the existing key is used.
     * </p>
     *
     * @param actionEvent The action event triggered by the Decrypt button.
     */
    @FXML
    public void decrypt(ActionEvent actionEvent) {
        String input = "";
        boolean inputIsLetters = false;

        if (!mfxCipherTextLetters.getText().trim().isEmpty() && mfxCipherTextLetters.isEditable()) {
            String letters = mfxCipherTextLetters.getText().trim().toUpperCase();
            if (!letters.matches("^[A-P]{2}$")) {
                errorDialog.showInfoDialog("Error", "Encrypted Text Letters must consist of exactly 2 characters (A–P).");
                return;
            }
            input = letters;
            inputIsLetters = true;
        } else if (!mfxCipherTextBits.getText().trim().isEmpty() && mfxCipherTextBits.isEditable()) {
            if (mfxCipherTextBits.getText().trim().length() != 8) {
                errorDialog.showInfoDialog("Error", "Encrypted Text Bits must be exactly 8 bits.");
                return;
            }
            input = mfxCipherTextBits.getText().trim();
        } else {
            errorDialog.showInfoDialog("Error", "Please enter encrypted text in one of the fields.");
            return;
        }

        String key = textFieldKey.getText().trim();
        if (checkGenerateKey.isSelected()) {
            if (key.length() < 10) {
                key = SDESCryptosystem.generateRandomKey();
                textFieldKey.setText(key);
            }
        } else {
            if (key.isEmpty() || key.length() != 10 || !isValidKey(key)) {
                errorDialog.showInfoDialog("Error", "Invalid key. Ensure the key is a 10-bit binary string.");
                return;
            }
        }

        try {
            SDESCryptosystem sdes = new SDESCryptosystem();
            String plainResult = sdes.decrypt(input, key);
            String plainResultLetters, plainResultBits;
            if (inputIsLetters) {
                plainResultLetters = plainResult;
                plainResultBits = letterToBits(plainResult);
            } else {
                plainResultBits = plainResult;
                plainResultLetters = bitsToLetters(plainResult);
            }
            mfxPlainTextLetters.setText(plainResultLetters);
            mfxPlainTextBits.setText(plainResultBits);
        } catch (IllegalArgumentException e) {
            errorDialog.showInfoDialog("Decryption Error", e.getMessage());
        }

    }

    /**
     * Validates that the key is a 10-bit binary string.
     *
     * @param key The key.
     * @return True if valid; false otherwise.
     */
    private boolean isValidKey(String key) {
        return key.matches("[01]{10}");
    }

    /**
     * Clears all plain text, encrypted text, and key fields.
     *
     * @param actionEvent The action event triggered by the Clear All button.
     */
    @FXML
    public void clearAll(ActionEvent actionEvent) {
        mfxPlainTextLetters.clear();
        mfxPlainTextBits.clear();
        mfxCipherTextLetters.clear();
        mfxCipherTextBits.clear();
        textFieldKey.clear();  // Ahora se limpia también la clave.
    }

    /**
     * Displays a dialog to change the encryption method.
     *
     * @param actionEvent The action event triggered by the Change Method button.
     */
    @FXML
    public void showChangeMethodDialog(ActionEvent actionEvent) {
        changeMethodDialog.showFXMLDialog(
                "Symmetric-key Cryptosystems",
                "views/symmetric-key/symmetric-key-dialog-view.fxml",
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

    @Override
    public void saveCurrentState() {
        lastPlainTextLetters = mfxPlainTextLetters.getText();
        lastPlainTextBits = mfxPlainTextBits.getText();
        lastCipherTextLetters = mfxCipherTextLetters.getText();
        lastCipherTextBits = mfxCipherTextBits.getText();
        lastKey = textFieldKey.getText();
    }

    @Override
    public void restorePreviousState() {
        mfxPlainTextLetters.setText(lastPlainTextLetters);
        mfxPlainTextBits.setText(lastPlainTextBits);
        mfxCipherTextLetters.setText(lastCipherTextLetters);
        mfxCipherTextBits.setText(lastCipherTextBits);
        textFieldKey.setText(lastKey);
    }

    @Override
    public void switchEncryptionMethod(String methodView) {
        saveCurrentState();
        IlunCryptController.getInstance().loadView(methodView);
        restorePreviousState();
    }

    @Override
    public void closeDialog(DialogHelper dialog) {
        // Implementation if needed
    }

    @Override
    public void setConfig(CryptosystemConfig config) {
        // Implementation if needed
    }

    /**
     * Copies the content of the Plain Text Letters field to the system clipboard.
     *
     * @param actionEvent The action event triggered by the corresponding copy button.
     */
    public void copyPlainTextLetters(ActionEvent actionEvent) {
        copyToClipboard(mfxPlainTextLetters.getText());
    }

    /**
     * Copies the content of the Plain Text Bits field to the system clipboard.
     *
     * @param actionEvent The action event triggered by the corresponding copy button.
     */
    public void copyPlainTextBits(ActionEvent actionEvent) {
        copyToClipboard(mfxPlainTextBits.getText());
    }

    /**
     * Copies the content of the Encrypted Text Letters field to the system clipboard.
     *
     * @param actionEvent The action event triggered by the corresponding copy button.
     */
    public void copyCipherTextLetters(ActionEvent actionEvent) {
        copyToClipboard(mfxCipherTextLetters.getText());
    }

    /**
     * Copies the content of the Encrypted Text Bits field to the system clipboard.
     *
     * @param actionEvent The action event triggered by the corresponding copy button.
     */
    public void copyCipherTextBits(ActionEvent actionEvent) {
        copyToClipboard(mfxCipherTextBits.getText());
    }

    /**
     * Copies the key to the system clipboard.
     *
     * @param actionEvent The action event triggered by the Copy Key button.
     */
    public void copyKey(ActionEvent actionEvent) {
        copyToClipboard(textFieldKey.getText());
    }

    /**
     * Copies the provided text to the system clipboard.
     *
     * @param text The text to copy.
     */
    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    /**
     * Generates a new random key using SDESCryptosystem and updates the key field.
     *
     * If the checkbox "Generate key automatically" is selected and the key field is not already full (10 characters),
     * a new key is generated. Otherwise, the existing key is kept.
     *
     * @param actionEvent The action event triggered by the Generate Key (Shuffle) button.
     */
    public void shuffleKey(ActionEvent actionEvent) {
        String randomKey = SDESCryptosystem.generateRandomKey();
        textFieldKey.setText(randomKey);
    }

    /**
     * Clears the key field.
     *
     * @param actionEvent The action event triggered by the Delete Key button.
     */
    public void deleteKey(ActionEvent actionEvent) {
        textFieldKey.clear();
    }

    /**
     * Converts a string of letters (A–P) to its 8-bit binary representation.
     *
     * @param letters A string of 2 letters.
     * @return The corresponding 8-bit binary string.
     */
    private String letterToBits(String letters) {
        StringBuilder sb = new StringBuilder();
        for (char c : letters.toCharArray()) {
            int value = c - 'A';
            String binary = Integer.toBinaryString(value);
            while (binary.length() < 4) {
                binary = "0" + binary;
            }
            sb.append(binary);
        }
        return sb.toString();
    }

    /**
     * Converts an 8-bit binary string to its corresponding 2-letter representation.
     *
     * @param bits An 8-bit binary string.
     * @return The corresponding 2-letter string.
     */
    private String bitsToLetters(String bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bits.length(); i += 4) {
            String nibble = bits.substring(i, i + 4);
            int value = Integer.parseInt(nibble, 2);
            char letter = (char) ('A' + value);
            sb.append(letter);
        }
        return sb.toString();
    }
}