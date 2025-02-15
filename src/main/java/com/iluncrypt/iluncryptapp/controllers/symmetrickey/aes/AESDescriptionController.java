package com.iluncrypt.iluncryptapp.controllers.symmetrickey.aes;

import com.iluncrypt.iluncryptapp.utils.LatexImageGenerator;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the AES description view.
 *
 * This controller manages both the user-friendly general explanation and the detailed technical
 * description for AES (Advanced Encryption Standard). It utilizes the {@code LatexImageGenerator} utility class
 * to generate LaTeX-rendered images for the encryption and decryption representations and an example.
 * A toggle button allows the user to show or hide the detailed description.
 */
public class AESDescriptionController {

    @FXML
    private ImageView encryptionFormula;

    @FXML
    private ImageView decryptionFormula;

    @FXML
    private ImageView exampleImage;

    @FXML
    private VBox detailedDescriptionContainer;

    @FXML
    private MFXToggleButton toggleDetailsButton;

    /**
     * Initializes the AES description view.
     * This method generates the LaTeX images for the encryption and decryption representations and an example,
     * and ensures that the detailed description section is hidden by default.
     */
    @FXML
    public void initialize() {
        // Generate LaTeX images using the utility class.
        encryptionFormula.setImage(LatexImageGenerator.createLatexImage("E(P, K) = AES(P, K)"));
        decryptionFormula.setImage(LatexImageGenerator.createLatexImage("D(C, K) = AES^{-1}(C, K)"));
        exampleImage.setImage(LatexImageGenerator.createLatexImage("HELLO \\rightarrow AES-128 \\rightarrow 3F8A2B1C"));

        // Hide detailed description section initially.
        detailedDescriptionContainer.setVisible(false);
        detailedDescriptionContainer.setManaged(false);

        // Set initial text for the toggle button.
        toggleDetailsButton.setText("View Detailed Description");
    }

    /**
     * Toggles the visibility of the detailed technical description section.
     * When the toggle button is selected, the detailed description is shown;
     * when it is deselected, the detailed description is hidden.
     */
    @FXML
    private void toggleDetailedDescription() {
        boolean selected = toggleDetailsButton.isSelected();
        detailedDescriptionContainer.setVisible(selected);
        detailedDescriptionContainer.setManaged(selected);
        if (selected) {
            toggleDetailsButton.setText("Hide Detailed Description");
        } else {
            toggleDetailsButton.setText("View Detailed Description");
        }
    }
}
