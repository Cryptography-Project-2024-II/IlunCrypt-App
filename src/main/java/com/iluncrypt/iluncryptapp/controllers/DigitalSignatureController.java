package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.ResourcesLoader;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import java.io.IOException;

public class DigitalSignatureController {

    @FXML
    private MFXScrollPane dynamicContent;

    private final PauseTransition delayTransition;
    private boolean isMouseOverDescription = false;

    public DigitalSignatureController() {
        delayTransition = new PauseTransition(Duration.millis(500));
        delayTransition.setOnFinished(event -> {
            if (!isMouseOverDescription) {
                loadView("views/digital-signature-methods-description-view.fxml");
            }
        });
    }

    @FXML
    public void initialize() {
        showDigitalSignatureDescription();
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
    private void handleRSASignature() { loadMainView("RSA-SIGNATURE"); }

    @FXML
    private void handleDSASignature() { loadMainView("DSA-SIGNATURE"); }

    private void loadMainView(String signatureView) {
        IlunCryptController controller = IlunCryptController.getInstance();
        controller.loadView(signatureView);
    }

    @FXML
    private void handleHoverRSA(MouseEvent event) { updateHoverView("views/RSA-signature-description-view.fxml"); }

    @FXML
    private void handleHoverDSA(MouseEvent event) { updateHoverView("views/DSA-signature-description-view.fxml"); }

    @FXML
    private void handleHoverExited(MouseEvent event) { delayTransition.playFromStart(); }

    private void updateHoverView(String fxmlPath) {
        cancelResetDelay();
        loadView(fxmlPath);
    }

    private void cancelResetDelay() { delayTransition.stop(); }

    @FXML
    private void showDigitalSignatureDescription() { loadView("views/digital-signature-methods-description-view.fxml"); }

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
