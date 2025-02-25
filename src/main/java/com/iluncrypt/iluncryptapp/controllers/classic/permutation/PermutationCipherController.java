package com.iluncrypt.iluncryptapp.controllers.classic.permutation;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import com.iluncrypt.iluncryptapp.controllers.classic.ClassicCiphersDialogController;
import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.CryptosystemConfig;
import com.iluncrypt.iluncryptapp.models.algorithms.classic.PermutationCipher;
import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import com.iluncrypt.iluncryptapp.models.enums.WhitespaceHandling;
import com.iluncrypt.iluncryptapp.models.keys.PermutationKey;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import com.iluncrypt.iluncryptapp.utils.config.ConfigManager;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Permutation Cipher encryption system.
 * Manages encryption, decryption, and UI interactions.
 */
public class PermutationCipherController implements CipherController, Initializable {

    private Alphabet plaintextAlphabet;
    private Alphabet ciphertextAlphabet;
    private Alphabet keyAlphabet;
    private CaseHandling caseHandling;
    private UnknownCharHandling unknownCharHandling;
    private WhitespaceHandling whitespaceHandling;
    private int alphabetSize;

    private final DialogHelper infoDialog;
    private final DialogHelper errorDialog;
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
    private MFXTextField textFieldKey;

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
        this.errorDialog = new DialogHelper(stage);
    }

    /**
     * Initializes the controller and configures the UI layout.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureGridGrowth();
        configureDialogs();
        loadConfig();
    }

    private void loadConfig() {
        plaintextAlphabet = ConfigManager.loadClassicCipherConfig("permutation").getPlaintextAlphabet();
        ciphertextAlphabet = ConfigManager.loadClassicCipherConfig("permutation").getCiphertextAlphabet();

        if (plaintextAlphabet == null || ciphertextAlphabet == null) {
            throw new IllegalArgumentException("Both alphabets must be valid..");
        }
        if (plaintextAlphabet.size() != ciphertextAlphabet.size()) {
            throw new IllegalArgumentException("The alphabets must be the same size.");
        }

        alphabetSize = plaintextAlphabet.size();
        keyAlphabet = Alphabet.generateZAlphabet(alphabetSize);

        caseHandling = CaseHandling.IGNORE; // o PRESERVE, STRICT, según convenga
        unknownCharHandling = UnknownCharHandling.REMOVE; // ejemplo
        whitespaceHandling = WhitespaceHandling.PRESERVE; // ejemplo
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
            lastPermutationKey = textFieldKey.getText();
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
            textFieldKey.setText(lastPermutationKey);
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
        String keyStr = textFieldKey.getText();

        // Si la clave está vacía, generamos blockSize y clave aleatoria
        if (keyStr == null || keyStr.trim().isEmpty()) {
            int minBlockSize = 3; // Cota mínima para el tamaño del bloque
            int blockSize = generateBlockSize(plainText.length(), minBlockSize);

            // Genera la clave de permutación aleatoria para el blockSize obtenido
            keyStr = generateRandomPermutationKey(blockSize);

            // Actualiza el alfabeto de clave (ℤ_n) según el nuevo blockSize
            keyAlphabet = Alphabet.generateZAlphabet(blockSize);

            // Actualiza el campo de la UI con la clave generada
            textFieldKey.setText(keyStr);
        }

        // Si hay texto y clave, realiza el cifrado
        if (!plainText.isEmpty() && !keyStr.isEmpty()) {
            textAreaCipherText.setText(permutationEncrypt(plainText, keyStr));
        }
    }

    @FXML
    private void decipherText() {
        String cipherText = textAreaCipherText.getText();
        String keyStr = textFieldKey.getText();
        if (!cipherText.isEmpty() && !keyStr.isEmpty()) {
            textAreaPlainText.setText(permutationDecrypt(cipherText, keyStr));
        }
    }

    private String permutationEncrypt(String plainText, String keyStr) {
        try {
            PermutationKey key = new PermutationKey(keyStr, keyAlphabet);
            PermutationCipher permutationCipher = new PermutationCipher(
                    plaintextAlphabet,
                    ciphertextAlphabet,
                    caseHandling,
                    unknownCharHandling,
                    whitespaceHandling
            );
            return permutationCipher.encrypt(plainText, key);
        } catch (IllegalArgumentException e) {
            errorDialog.showInfoDialog("Encryption Error", e.getMessage());
            return "";
        }
    }

    private String permutationDecrypt(String cipherText, String keyStr) {
        try {
            PermutationKey key = new PermutationKey(keyStr, keyAlphabet);
            PermutationCipher permutationCipher = new PermutationCipher(
                    plaintextAlphabet,
                    ciphertextAlphabet,
                    caseHandling,
                    unknownCharHandling,
                    whitespaceHandling
            );
            return permutationCipher.decrypt(cipherText, key);
        } catch (IllegalArgumentException e) {
            errorDialog.showInfoDialog("Decryption Error", e.getMessage());
            return "";
        }
    }


    private int generateBlockSize(int inputLength, int minBlockSize) {
        if (inputLength < minBlockSize) {
            return inputLength;
        }
        // Calcula un máximo: por ejemplo, cota mínima + un cuarto de la longitud de la entrada,
        // sin exceder la propia longitud de la entrada.
        int maxBlockSize = Math.min(inputLength, minBlockSize + inputLength / 4);
        Random random = new Random();
        return random.nextInt(maxBlockSize - minBlockSize + 1) + minBlockSize;
    }

    private String generateRandomPermutationKey(int blockSize) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= blockSize; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        // Convertir la lista en el formato (1, 2, 3, ...)
        return "(" + numbers.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";
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
