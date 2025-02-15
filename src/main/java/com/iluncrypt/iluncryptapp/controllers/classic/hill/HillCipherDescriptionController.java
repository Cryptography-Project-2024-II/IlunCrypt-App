package com.iluncrypt.iluncryptapp.controllers.classic.hill;

import com.iluncrypt.iluncryptapp.utils.LatexImageGenerator;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the Hill Cipher description view.
 *
 * This controller manages both the user-friendly general explanation and the detailed technical
 * description for the Hill Cipher. It utilizes the {@code LatexImageGenerator} utility class to
 * generate LaTeX-rendered images for the encryption and decryption formulas as well as an example.
 * A toggle button allows the user to show or hide the detailed description.
 */
public class HillCipherDescriptionController {

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
     * Initializes the Hill Cipher description view.
     * This method generates the LaTeX images for the encryption formula, decryption formula, and example,
     * and ensures that the detailed description section is hidden by default.
     */
    @FXML
    public void initialize() {
        // Generate LaTeX images using the utility class.
        encryptionFormula.setImage(LatexImageGenerator.createLatexImage("E(P) = (K \\cdot P) \\mod 26"));
        decryptionFormula.setImage(LatexImageGenerator.createLatexImage("D(P) = (K^{-1} \\cdot C) \\mod 26"));
        exampleImage.setImage(LatexImageGenerator.createLatexImage("HELP \\rightarrow E(P) = ( [3,3;2,5] \\cdot P ) \\mod 26"));

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
