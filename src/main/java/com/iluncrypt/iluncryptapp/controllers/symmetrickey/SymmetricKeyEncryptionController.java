package com.iluncrypt.iluncryptapp.controllers.symmetrickey;

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

public class SymmetricKeyEncryptionController {

    @FXML
    private MFXScrollPane dynamicContent;

    private final PauseTransition delayTransition;
    private boolean isMouseOverDescription = false;

    public SymmetricKeyEncryptionController() {
        delayTransition = new PauseTransition(Duration.millis(500));
        delayTransition.setOnFinished(event -> {
            if (!isMouseOverDescription) {
                loadView("views/symmetric-key/symmetric-methods-description-view.fxml");
            }
        });
    }

    @FXML
    public void initialize() {
        showSymmetricMethodsDescription();
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
    private void handleAESCipher() { loadMainView("AES"); }

    @FXML
    private void handleDESCipher() { loadMainView("DES"); }

    @FXML
    private void handleTDESCipher() { loadMainView("TDES"); }

    @FXML
    private void handleSDESCipher() { loadMainView("SDES"); }

    private void loadMainView(String cipherView) {
        IlunCryptController controller = IlunCryptController.getInstance();
        controller.loadView(cipherView);
    }

    @FXML
    private void handleHoverAES(MouseEvent event) { updateHoverView("views/symmetric-key/aes/aes-description-view.fxml"); }

    @FXML
    private void handleHoverDES(MouseEvent event) { updateHoverView("views/symmetric-key/des/des-description-view.fxml"); }

    @FXML
    private void handleHoverTDES(MouseEvent event) { updateHoverView("views/symmetric-key/tdes/tdes-description-view.fxml"); }

    @FXML
    private void handleHoverSDES(MouseEvent event) { updateHoverView("views/symmetric-key/sdes/sdes-description-view.fxml"); }

    @FXML
    private void handleHoverExited(MouseEvent event) { delayTransition.playFromStart(); }

    private void updateHoverView(String fxmlPath) {
        cancelResetDelay();
        loadView(fxmlPath);
    }

    private void cancelResetDelay() { delayTransition.stop(); }

    @FXML
    private void showSymmetricMethodsDescription() { loadView("views/symmetric-key/symmetric-methods-description-view.fxml"); }

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
