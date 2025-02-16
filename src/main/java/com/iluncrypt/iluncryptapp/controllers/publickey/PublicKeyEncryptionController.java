package com.iluncrypt.iluncryptapp.controllers.publickey;

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

public class PublicKeyEncryptionController {

    @FXML
    private MFXScrollPane dynamicContent;

    private final PauseTransition delayTransition;
    private boolean isMouseOverDescription = false;

    public PublicKeyEncryptionController() {
        delayTransition = new PauseTransition(Duration.millis(500));
        delayTransition.setOnFinished(event -> {
            if (!isMouseOverDescription) {
                loadView("views/public-key/public-key-methods-description-view.fxml");
            }
        });
    }

    @FXML
    public void initialize() {
        showPublicKeyMethodsDescription();
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
    private void handleRSACipher() { loadMainView("RSA-ENCRYPTION"); }

    @FXML
    private void handleRabinCipher() { loadMainView("RABIN-ENCRYPTION"); }

    @FXML
    private void handleElGamalCipher() { loadMainView("ELGAMAL-ENCRYPTION"); }

    @FXML
    private void handleMenezesVanstoneCipher() { loadMainView("MENEZES-VANSTONE-ENCRYPTION"); }

    private void loadMainView(String cipherView) {
        IlunCryptController controller = IlunCryptController.getInstance();
        controller.loadView(cipherView);
    }

    @FXML
    private void handleHoverRSA(MouseEvent event) { updateHoverView("views/public-key/rsa/rsa-encryption-description-view.fxml"); }

    @FXML
    private void handleHoverRabin(MouseEvent event) { updateHoverView("views/public-key/rabin/rabin-encryption-description-view.fxml"); }

    @FXML
    private void handleHoverElGamal(MouseEvent event) { updateHoverView("views/public-key/elgamal/elgamal-encryption-description-view.fxml"); }

    @FXML
    private void handleHoverMenezesVanstone(MouseEvent event) { updateHoverView("views/public-key/menezes-vanstone/menezes-vanstone-encryption-description-view.fxml"); }

    @FXML
    private void handleHoverExited(MouseEvent event) { delayTransition.playFromStart(); }

    private void updateHoverView(String fxmlPath) {
        cancelResetDelay();
        loadView(fxmlPath);
    }

    private void cancelResetDelay() { delayTransition.stop(); }

    @FXML
    private void showPublicKeyMethodsDescription() { loadView("views/public-key/public-key-methods-description-view.fxml"); }

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
