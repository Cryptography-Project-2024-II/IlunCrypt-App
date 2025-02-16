package com.iluncrypt.iluncryptapp.controllers.imageencryption;

import com.iluncrypt.iluncryptapp.ResourcesLoader;
import com.iluncrypt.iluncryptapp.controllers.IlunCryptController;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import java.io.IOException;

public class ImageEncryptionController {

    @FXML
    private MFXScrollPane dynamicContent;

    private final PauseTransition delayTransition;
    private boolean isMouseOverDescription = false;

    public ImageEncryptionController() {
        delayTransition = new PauseTransition(Duration.millis(500));
        delayTransition.setOnFinished(event -> {
            if (!isMouseOverDescription) {
                loadView("views/image-encryption/image-encryption-methods-description-view.fxml");
            }
        });
    }

    @FXML
    public void initialize() {
        showImageEncryptionDescription();
        dynamicContent.setOnMouseEntered(e -> {
            isMouseOverDescription = true;
            cancelResetDelay();
        });
        dynamicContent.setOnMouseExited(e -> {
            isMouseOverDescription = false;
            delayTransition.playFromStart();
        });
    }

    @FXML
    private void handleAESCipher() { loadMainView("AES-IMAGE"); }

    @FXML
    private void handleDESCipher() { loadMainView("DES-IMAGE"); }

    @FXML
    private void handleHillCipher() { loadMainView("HILL-IMAGE"); }

    @FXML
    private void handlePermutationCipher() { loadMainView("PERMUTATION-IMAGE"); }

    private void loadMainView(String cipherView) {
        IlunCryptController controller = IlunCryptController.getInstance();
        controller.loadView(cipherView);
    }

    @FXML
    private void handleHoverAES(MouseEvent event) { updateHoverView("views/image-encryption/aes/aes-image-description-view.fxml"); }

    @FXML
    private void handleHoverDES(MouseEvent event) { updateHoverView("views/image-encryption/des/des-image-description-view.fxml"); }

    @FXML
    private void handleHoverHill(MouseEvent event) { updateHoverView("views/image-encryption/hill/hill-image-cipher-description-view.fxml"); }

    @FXML
    private void handleHoverPermutation(MouseEvent event) { updateHoverView("views/image-encryption/permutation/permutation-image-cipher-description-view.fxml"); }

    @FXML
    private void handleHoverExited(MouseEvent event) { delayTransition.playFromStart(); }

    private void updateHoverView(String fxmlPath) {
        cancelResetDelay();
        loadView(fxmlPath);
    }

    private void cancelResetDelay() { delayTransition.stop(); }

    @FXML
    private void showImageEncryptionDescription() { loadView("views/image-encryption/image-encryption-methods-description-view.fxml"); }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourcesLoader.loadURL(fxmlPath));
            Parent view = loader.load();
            dynamicContent.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
