package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.ResourcesLoader;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Controller for managing encryption method options and dynamically updating the content view.
 */
public class ClassicCiphersController {

    @FXML
    private StackPane dynamicContent; // StackPane where dynamic content is displayed.

    private final PauseTransition delayTransition; // Delay transition before resetting the view.

    /**
     * Constructor initializes the delay transition.
     */
    public ClassicCiphersController() {
        this.delayTransition = new PauseTransition(Duration.millis(500));
        this.delayTransition.setOnFinished(event -> loadView("views/ed-methods-description-view.fxml"));
    }

    /**
     * Initializes the controller by setting the default view.
     */
    @FXML
    public void initialize() {
        showEDMethodsDescription();
    }

    /**
     * Handles button clicks to load the Affine Cipher view.
     */
    @FXML
    private void handleAffineCipher() {
        loadMainView("AFFINE-CIPHER");
    }

    /**
     * Handles button clicks to load the Multiplicative Cipher view.
     */
    @FXML
    private void handleMultiplicativeCipher() {
        loadMainView("MULTIPLICATIVE-CIPHER");
    }

    /**
     * Handles button clicks to load the Shift (Caesar) Cipher view.
     */
    @FXML
    private void handleShiftCipher() {
        loadMainView("SHIFT-CIPHER");
    }

    /**
     * Handles button clicks to load the Hill Cipher view.
     */
    @FXML
    private void handleHillCipher() {
        loadMainView("HILL-CIPHER");
    }

    /**
     * Handles button clicks to load the Permutation Cipher view.
     */
    @FXML
    private void handlePermutationCipher() {
        loadMainView("PERMUTATION-CIPHER");
    }

    /**
     * Handles button clicks to load the Substitution Cipher view.
     */
    @FXML
    private void handleSubstitutionCipher() {
        loadMainView("SUBSTITUTION-CIPHER");
    }

    /**
     * Handles button clicks to load the Vigenère Cipher view.
     */
    @FXML
    private void handleVigenereCipher() {
        loadMainView("VIGENERE-CIPHER");
    }

    /**
     * Loads the main encryption view based on the given cipher type.
     *
     * @param cipherView Name of the cipher view to load.
     */
    private void loadMainView(String cipherView) {
        IlunCryptController controller = IlunCryptController.getInstance();
        controller.loadView(cipherView);
    }

    /**
     * Loads the Affine Cipher description when hovered.
     */
    @FXML
    private void handleHoverAffine(MouseEvent event) {
        updateHoverView("views/affine-cipher-description-view.fxml");
    }

    /**
     * Loads the Multiplicative Cipher description when hovered.
     */
    @FXML
    private void handleHoverMultiplicative(MouseEvent event) {
        updateHoverView("views/multiplicative-cipher-description-view.fxml");
    }

    /**
     * Loads the Shift Cipher description when hovered.
     */
    @FXML
    private void handleHoverShift(MouseEvent event) {
        updateHoverView("views/shift-cipher-description-view.fxml");
    }

    /**
     * Loads the Hill Cipher description when hovered.
     */
    @FXML
    private void handleHoverHill(MouseEvent event) {
        updateHoverView("views/hill-cipher-description-view.fxml");
    }

    /**
     * Loads the Permutation Cipher description when hovered.
     */
    @FXML
    private void handleHoverPermutation(MouseEvent event) {
        updateHoverView("views/permutation-cipher-description-view.fxml");
    }

    /**
     * Loads the Substitution Cipher description when hovered.
     */
    @FXML
    private void handleHoverSubstitution(MouseEvent event) {
        updateHoverView("views/substitution-cipher-description-view.fxml");
    }

    /**
     * Loads the Vigenère Cipher description when hovered.
     */
    @FXML
    private void handleHoverVigenere(MouseEvent event) {
        updateHoverView("views/vigenere-cipher-description-view.fxml");
    }

    /**
     * Updates the content view when hovering over a button.
     * Cancels the reset delay to avoid unnecessary changes.
     *
     * @param fxmlPath Path to the description view.
     */
    private void updateHoverView(String fxmlPath) {
        cancelResetDelay();
        loadView(fxmlPath);
    }

    /**
     * Initiates a delayed reset to the default view when the cursor exits the buttons.
     */
    @FXML
    private void handleHoverExited(MouseEvent event) {
        delayTransition.playFromStart();
    }

    /**
     * Stops the delayed reset when the user is still interacting with buttons.
     */
    private void cancelResetDelay() {
        delayTransition.stop();
    }

    /**
     * Loads the default encryption methods description view.
     */
    @FXML
    private void showEDMethodsDescription() {
        loadView("views/ed-methods-description-view.fxml");
    }

    /**
     * Loads a given FXML view into the dynamic content area.
     *
     * @param fxmlPath Path to the FXML file to load.
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourcesLoader.loadURL(fxmlPath));
            Parent view = loader.load();

            dynamicContent.getChildren().clear();
            dynamicContent.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
