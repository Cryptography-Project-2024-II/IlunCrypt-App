package com.iluncrypt.iluncryptapp.controllers.classic.hill;

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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Controller for the Hill Cipher encryption system.
 * Manages encryption, decryption, and UI interactions.
 */
public class HillCipherController implements CipherController, Initializable {

    private static final int ALPHABET_SIZE = 26;
    private static final int MODULO = 26;
    private final DialogHelper infoDialog;
    private final DialogHelper changeMethodDialog;

    // Stores the last entered values when switching methods
    private String lastPlainText = "";
    private String lastCipherText = "";
    @FXML
    private MFXTextField textFieldKey;
    private int lastA = 1;
    private int lastB = 0;

    @FXML
    private GridPane grid;

    @FXML
    private TextArea textAreaPlainText;

    @FXML
    private TextArea textAreaCipherText;

    @FXML
    private MFXTextField textFieldMatrixSize; // Correct field for Hill Cipher

    @FXML
    private MFXButton btnInfo;

    /**
     * Constructor for HillCipherController.
     * Initializes separate dialog instances to prevent interference.
     *
     * @param stage The primary stage used for managing dialogs.
     */
    public HillCipherController(Stage stage) {
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
     * Displays an informational dialog about the Hill Cipher.
     */
    @FXML
    private void showInfoDialog() {
        infoDialog.showInfoDialog(
                "Hill Cipher Information",
                """
                The Hill Cipher is a polygraphic substitution cipher that uses matrix multiplication 
                to encrypt and decrypt text.

                Encryption formula:
                E(P) = K * P (mod m)

                Where:
                - 'P' is the plaintext as a vector.
                - 'K' is the encryption key matrix.
                - 'm' is the size of the alphabet (26 for English letters).

                Decryption formula:
                D(C) = K⁻¹ * C (mod m)

                Where 'K⁻¹' is the modular inverse of the key matrix K.

                Characteristics:
                - This cipher requires a **square key matrix**.
                - The determinant of K must be invertible modulo 26.
                - It is **more secure** than simple substitution ciphers.
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

        // Ensure the field is initialized before accessing
        if (textFieldMatrixSize != null) {
            lastA = parseInt(textFieldMatrixSize.getText(), 1);
        } else {
            lastA = 1; // Default value
        }
    }

    /**
     * Restores the previously entered values after switching methods.
     */
    @Override
    public void restorePreviousState() {
        textAreaPlainText.setText(lastPlainText);
        textAreaCipherText.setText(lastCipherText);

        if (textFieldMatrixSize != null) {
            textFieldMatrixSize.setText(String.valueOf(lastA));
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

    /*********** START ENCRIPTION,  *************/

    @FXML
    private void cipherText() {
        String plainText = cleanInput(textAreaPlainText.getText());
        String keyStr = textFieldKey.getText();

        if (keyStr.isEmpty()) {
            generateRandomKey();
            keyStr = textFieldKey.getText();
        }

        int[][] keyMatrix = parseMatrix(keyStr);
        textAreaCipherText.setText(hillEncrypt(plainText, keyMatrix));
    }

    private int[][] createKeyMatrix(String Dmatrix) {
        String[] Nmatrix = Dmatrix.split(" ");
        int numberOfElements = Nmatrix.length;
        int MatrixSize = (int) Math.sqrt(numberOfElements);
        if (MatrixSize * MatrixSize != numberOfElements) {
            throw new IllegalArgumentException("El número de elementos no forma un cuadrado perfecto.");
        }
        int[][] Matrix = new int[MatrixSize][MatrixSize];
        int index = 0;
        for (int i = 0; i < MatrixSize; i++) {
            for (int j = 0; j < MatrixSize; j++) {
                Matrix[i][j] = Integer.parseInt(Nmatrix[index]);
                index++;
            }
        }
        return Matrix;
    }

/*********** END ENCRIPTION,  *************/

/*********** STAR DECRIPTION,  *************/


/*********** END DECRIPTION,  *************/
    /** Helper Methods **/

    private int parseInt(String text, int defaultValue) {
        return Optional.ofNullable(text).filter(t -> !t.isEmpty()).map(Integer::parseInt).orElse(defaultValue);
    }

    /** Placeholder methods **/

    public void exportEncryptedMessage(ActionEvent actionEvent) {}

    public void editMatrix(ActionEvent actionEvent) {}

    public void showOtherSettings(ActionEvent actionEvent) {

    }

    @FXML
    private void decipherText() {
        String cipherText = cleanInput(textAreaCipherText.getText());
        String keyStr = textFieldKey.getText();

        if (keyStr.isEmpty()) {
            infoDialog.showInfoDialog("Error", "No key provided for decryption.");
            return;
        }

        int[][] keyMatrix = parseMatrix(keyStr);
        int[][] inverseKey = invertMatrixMod26(keyMatrix);
        if (inverseKey == null) {
            infoDialog.showInfoDialog("Error", "Key matrix is not invertible in Z26.");
            return;
        }

        textAreaPlainText.setText(hillEncrypt(cipherText, inverseKey));
    }

    @FXML
    private void generateRandomKey() {
        Random rand = new Random();
        int[][] matrix;
        do {
            matrix = new int[][]{
                    {rand.nextInt(26), rand.nextInt(26)},
                    {rand.nextInt(26), rand.nextInt(26)}
            };
        } while (invertMatrixMod26(matrix) == null);

        textFieldKey.setText(formatMatrix(matrix));
    }

    private String hillEncrypt(String text, int[][] keyMatrix) {
        StringBuilder cipherText = new StringBuilder();
        int size = keyMatrix.length;

        while (text.length() % size != 0) {
            text += (char) ('A' + new Random().nextInt(26));
        }

        for (int i = 0; i < text.length(); i += size) {
            int[] vector = new int[size];
            for (int j = 0; j < size; j++) {
                vector[j] = text.charAt(i + j) - 'A';
            }

            int[] encryptedVector = multiplyMatrixByVector(keyMatrix, vector);
            for (int value : encryptedVector) {
                cipherText.append((char) (value + 'A'));
            }
        }
        return cipherText.toString();
    }

    private int[] multiplyMatrixByVector(int[][] matrix, int[] vector) {
        int[] result = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = 0;
            for (int j = 0; j < matrix[i].length; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
            result[i] = (result[i] % MODULO + MODULO) % MODULO;
        }
        return result;
    }

    private int[][] invertMatrixMod26(int[][] matrix) {
        int det = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]) % MODULO;
        det = (det + MODULO) % MODULO;

        int invDet = modInverse(det, MODULO);
        if (invDet == -1) return null;

        return new int[][]{
                {(matrix[1][1] * invDet) % MODULO, (-matrix[0][1] * invDet) % MODULO},
                {(-matrix[1][0] * invDet) % MODULO, (matrix[0][0] * invDet) % MODULO}
        };
    }

    private int modInverse(int a, int m) {
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) return x;
        }
        return -1;
    }

    private String cleanInput(String input) {
        return input.toUpperCase().replaceAll("[^A-Z]", "");
    }

    private int[][] parseMatrix(String input) {
        input = input.replaceAll("\\[|\\]", "").trim();
        String[] values = input.split(",\s*");
        return new int[][]{
                {Integer.parseInt(values[0]), Integer.parseInt(values[1])},
                {Integer.parseInt(values[2]), Integer.parseInt(values[3])}
        };
    }

    private String formatMatrix(int[][] matrix) {
        return String.format("[[%d, %d], [%d, %d]]", matrix[0][0], matrix[0][1], matrix[1][0], matrix[1][1]);
    }


}
