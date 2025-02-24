package com.iluncrypt.iluncryptapp.controllers.classic.vigenere;

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
import java.util.ResourceBundle;

/**
 * Controller for the Vigenère Cipher encryption system.
 * This class manages encryption, decryption, and UI interactions for the Vigenère Cipher.
 */
public class VigenereCipherController implements CipherController, Initializable {

    private static final int ALPHABET_SIZE = 26;
    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;

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
     * Constructor for VigenereCipherController.
     * Initializes separate dialog instances to prevent interference.
     *
     * @param stage The primary stage used for managing dialogs.
     */
    public VigenereCipherController(Stage stage) {
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
     * Displays an informational dialog about the Vigenère Cipher.
     */
    @FXML
    private void showInfoDialog() {
        infoDialog.showInfoDialog(
                "Vigenère Cipher Information",
                """
                The Vigenère Cipher is a polyalphabetic substitution cipher that uses a keyword 
                to shift letters in the plaintext.

                Characteristics:
                - Uses a repeating key to determine the shift for each letter.
                - More secure than monoalphabetic ciphers but vulnerable to frequency analysis.

                Example:
                - Plaintext: ATTACKATDAWN
                - Key: LEMON
                - Ciphertext: LXFOPVEFRNHR

                The Vigenère cipher is commonly broken using Kasiski examination or frequency analysis.
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
        if (!plainText.isEmpty()) {
            String key = textFieldKey.getText();
            textAreaCipherText.setText(VigenereEncrypt(plainText, key));
        }
    }

    @FXML
    private void decipherText() {
        String cipherText = textAreaCipherText.getText();
        if (!cipherText.isEmpty()) {
            String key = textFieldKey.getText();
            textAreaPlainText.setText(VigenereDecrypt(cipherText, key));
        }
    }

    private String ExtendKey(String text, String key) {
        StringBuilder extendedKey = new StringBuilder(key);
        int messageLength = text.length();
        int keyLength = key.length();

        for (int i = 0; extendedKey.length() < messageLength; i++) {
            extendedKey.append(key.charAt(i % keyLength));
        }
        return extendedKey.toString();
    }

    private String VigenereEncrypt(String text, String extendedKey) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                char keyChar = extendedKey.charAt(keyIndex % extendedKey.length());
                int shift = Character.toLowerCase(keyChar) - 'a';
                result.append((char) (((c - base + shift) % ALPHABET_SIZE) + base));
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    private String VigenereDecrypt(String text, String extendedKey) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                // Determinar si la letra es minúscula o mayúscula
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                // Obtener el carácter correspondiente de la clave extendida
                char keyChar = extendedKey.charAt(keyIndex % extendedKey.length());
                // Calcular el desplazamiento basado en la letra de la clave
                int shift = Character.toLowerCase(keyChar) - 'a';

                // Aplicar el descifrado de Vigenère (restar el desplazamiento)
                result.append((char) (((c - base - shift + ALPHABET_SIZE) % ALPHABET_SIZE) + base));

                // Incrementar el índice de la clave solo si la letra es alfabética
                keyIndex++;
            } else {
                // Si no es una letra, agregar el carácter tal cual
                result.append(c);
            }
        }

        return result.toString();
    }


    /** Increment/Decrement Controls **/

    @FXML
    private void editVigenereKey(ActionEvent actionEvent) {
        System.out.println("Editing Vigenère key...");
    }

    /** Placeholder methods **/

    public void exportEncryptedMessage(ActionEvent actionEvent) {}

    public void showOtherSettings(ActionEvent actionEvent) {}
}
