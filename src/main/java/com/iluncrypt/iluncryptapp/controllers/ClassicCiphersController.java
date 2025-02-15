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

/**
 * Controller for managing classic cipher views and dynamic content updates.
 *
 * This controller handles user interactions with cipher buttons and updates the dynamic
 * content area accordingly. It also manages a delay transition that resets the view to the
 * default description when the user stops hovering over a cipher button, unless the mouse is
 * over the description container.
 */
public class ClassicCiphersController {

    /**
     * The dynamic content container (an MFXScrollPane) that displays cipher descriptions.
     */
    @FXML
    private MFXScrollPane dynamicContent;

    /**
     * Delay transition used to reset the view after mouse exit.
     */
    private final PauseTransition delayTransition;

    /**
     * Flag indicating whether the mouse is currently over the description container.
     */
    private boolean isMouseOverDescription = false;

    /**
     * Constructs a new ClassicCiphersController and initializes the delay transition.
     *
     * The delay is set to 500 ms. When finished, it loads the default description view
     * if the mouse is not over the dynamic content container.
     */
    public ClassicCiphersController() {
        delayTransition = new PauseTransition(Duration.millis(500));
        delayTransition.setOnFinished(event -> {
            if (!isMouseOverDescription) {
                loadView("views/classic-methods-description-view.fxml");
            }
        });
    }

    /**
     * Initializes the controller.
     *
     * Loads the default encryption methods description view and sets up mouse event listeners on the
     * dynamic content container to track mouse enter and exit events.
     */
    @FXML
    public void initialize() {
        showClassicMethodsDescription();
        dynamicContent.setOnMouseEntered(e -> {
            isMouseOverDescription = true;
            cancelResetDelay();
        });
        dynamicContent.setOnMouseExited(e -> {
            isMouseOverDescription = false;
            delayTransition.playFromStart();
        });
    }

    /**
     * Handles the Affine Cipher button click.
     */
    @FXML
    private void handleAffineCipher() {
        loadMainView("AFFINE-CIPHER");
    }

    /**
     * Handles the Multiplicative Cipher button click.
     */
    @FXML
    private void handleMultiplicativeCipher() {
        loadMainView("MULTIPLICATIVE-CIPHER");
    }

    /**
     * Handles the Shift (Caesar) Cipher button click.
     */
    @FXML
    private void handleShiftCipher() {
        loadMainView("SHIFT-CIPHER");
    }

    /**
     * Handles the Hill Cipher button click.
     */
    @FXML
    private void handleHillCipher() {
        loadMainView("HILL-CIPHER");
    }

    /**
     * Handles the Permutation Cipher button click.
     */
    @FXML
    private void handlePermutationCipher() {
        loadMainView("PERMUTATION-CIPHER");
    }

    /**
     * Handles the Substitution Cipher button click.
     */
    @FXML
    private void handleSubstitutionCipher() {
        loadMainView("SUBSTITUTION-CIPHER");
    }

    /**
     * Handles the Vigenère Cipher button click.
     */
    @FXML
    private void handleVigenereCipher() {
        loadMainView("VIGENERE-CIPHER");
    }

    /**
     * Loads the main cipher view corresponding to the specified cipher.
     *
     * @param cipherView the name of the cipher view to load.
     */
    private void loadMainView(String cipherView) {
        IlunCryptController controller = IlunCryptController.getInstance();
        controller.loadView(cipherView);
    }

    /**
     * Handles the mouse hover event for the Affine Cipher button.
     *
     * @param event the mouse event.
     */
    @FXML
    private void handleHoverAffine(MouseEvent event) {
        updateHoverView("views/affine-cipher-description-view.fxml");
    }

    /**
     * Handles the mouse hover event for the Multiplicative Cipher button.
     *
     * @param event the mouse event.
     */
    @FXML
    private void handleHoverMultiplicative(MouseEvent event) {
        updateHoverView("views/multiplicative-cipher-description-view.fxml");
    }

    /**
     * Handles the mouse hover event for the Shift Cipher button.
     *
     * @param event the mouse event.
     */
    @FXML
    private void handleHoverShift(MouseEvent event) {
        updateHoverView("views/shift-cipher-description-view.fxml");
    }

    /**
     * Handles the mouse hover event for the Hill Cipher button.
     *
     * @param event the mouse event.
     */
    @FXML
    private void handleHoverHill(MouseEvent event) {
        updateHoverView("views/hill-cipher-description-view.fxml");
    }

    /**
     * Handles the mouse hover event for the Permutation Cipher button.
     *
     * @param event the mouse event.
     */
    @FXML
    private void handleHoverPermutation(MouseEvent event) {
        updateHoverView("views/permutation-cipher-description-view.fxml");
    }

    /**
     * Handles the mouse hover event for the Substitution Cipher button.
     *
     * @param event the mouse event.
     */
    @FXML
    private void handleHoverSubstitution(MouseEvent event) {
        updateHoverView("views/substitution-cipher-description-view.fxml");
    }

    /**
     * Handles the mouse hover event for the Vigenère Cipher button.
     *
     * @param event the mouse event.
     */
    @FXML
    private void handleHoverVigenere(MouseEvent event) {
        updateHoverView("views/vigenere-cipher-description-view.fxml");
    }

    /**
     * Updates the view to display the specified description and cancels the reset delay.
     *
     * @param fxmlPath the path to the FXML file for the description view.
     */
    private void updateHoverView(String fxmlPath) {
        cancelResetDelay();
        loadView(fxmlPath);
    }

    /**
     * Initiates the reset delay when the mouse exits a button.
     *
     * @param event the mouse event.
     */
    @FXML
    private void handleHoverExited(MouseEvent event) {
        delayTransition.playFromStart();
    }

    /**
     * Cancels the current reset delay.
     */
    private void cancelResetDelay() {
        delayTransition.stop();
    }

    /**
     * Displays the default encryption methods description view.
     */
    @FXML
    private void showClassicMethodsDescription() {
        loadView("views/classic-methods-description-view.fxml");
    }

    /**
     * Loads an FXML view into the dynamic content container.
     *
     * @param fxmlPath the path to the FXML file to load.
     */
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
