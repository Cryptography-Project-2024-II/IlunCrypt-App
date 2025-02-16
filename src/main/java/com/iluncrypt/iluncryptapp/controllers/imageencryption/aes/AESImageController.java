package com.iluncrypt.iluncryptapp.controllers.imageencryption.aes;

import com.iluncrypt.iluncryptapp.controllers.CipherController;
import com.iluncrypt.iluncryptapp.utils.DialogHelper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for AES image encryption.
 */
public class AESImageController implements CipherController, Initializable {

    private final DialogHelper infoDialog;
    private final Stage stage;

    @FXML
    private GridPane grid;

    @FXML
    private ImageView imageView;

    @FXML
    private MFXTextField textFieldKey;

    public AESImageController(Stage stage) {
        this.stage = stage;
        this.infoDialog = new DialogHelper(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoDialog.setOwnerNode(grid);
    }

    @FXML
    private void loadImage() {
        // Implement image loading logic
    }

    @FXML
    private void saveImage() {
        // Implement image saving logic
    }

    @FXML
    private void encryptImage() {
        // Implement AES image encryption logic
    }

    @FXML
    private void decryptImage() {
        // Implement AES image decryption logic
    }

    @Override
    public void saveCurrentState() {
        // Save current encryption state if needed
    }

    @Override
    public void restorePreviousState() {
        // Restore previous encryption state if needed
    }

    @Override
    public void switchEncryptionMethod(String methodView) {
        // Handle switching encryption methods
    }

    @Override
    public void closeOptionsDialog() {
        // Handle closing options dialog
    }
}
