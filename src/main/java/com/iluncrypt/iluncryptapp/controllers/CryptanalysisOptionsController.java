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

public class CryptanalysisOptionsController {

    @FXML
    private MFXScrollPane dynamicContent;

    private final PauseTransition delayTransition;
    private boolean isMouseOverDescription = false;

    public CryptanalysisOptionsController() {
        delayTransition = new PauseTransition(Duration.millis(500));
        delayTransition.setOnFinished(event -> {
            if (!isMouseOverDescription) {
                loadView("views/cryptanalysis-methods-description-view.fxml");
            }
        });
    }

    @FXML
    public void initialize() {
        showCryptanalysisMethodsDescription();
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
    private void handleFriedman() { loadMainView("FRIEDMAN-ANALYSIS"); }

    @FXML
    private void handleBrauer() { loadMainView("BRAUER-ANALYSIS"); }

    @FXML
    private void handleBruteForce() { loadMainView("BRUTE-FORCE-ANALYSIS"); }

    @FXML
    private void handleFrequency() { loadMainView("FREQUENCY-ANALYSIS"); }

    private void loadMainView(String analysisView) {
        IlunCryptController controller = IlunCryptController.getInstance();
        controller.loadView(analysisView);
    }

    @FXML
    private void handleHoverFriedman(MouseEvent event) { updateHoverView("views/friedman-analysis-description-view.fxml"); }

    @FXML
    private void handleHoverBrauer(MouseEvent event) { updateHoverView("views/brauer-analysis-description-view.fxml"); }

    @FXML
    private void handleHoverBruteForce(MouseEvent event) { updateHoverView("views/brute-force-analysis-description-view.fxml"); }

    @FXML
    private void handleHoverFrequency(MouseEvent event) { updateHoverView("views/frequency-analysis-description-view.fxml"); }

    @FXML
    private void handleHoverExited(MouseEvent event) { delayTransition.playFromStart(); }

    private void updateHoverView(String fxmlPath) {
        cancelResetDelay();
        loadView(fxmlPath);
    }

    private void cancelResetDelay() { delayTransition.stop(); }

    @FXML
    private void showCryptanalysisMethodsDescription() { loadView("views/cryptanalysis-methods-description-view.fxml"); }

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
