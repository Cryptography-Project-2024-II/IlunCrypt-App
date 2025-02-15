package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.utils.LatexImageGenerator;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the Rabin Cryptosystem description view.
 *
 * This controller manages both the general explanation and the detailed technical description for Rabin.
 * It uses the LatexImageGenerator utility class to generate LaTeX-rendered images for the formulas and example.
 * A toggle button allows the user to show or hide the detailed description.
 */
public class RabinEncryptionDescriptionController {

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
     * Initializes the Rabin description view.
     */
    @FXML
    public void initialize() {
        encryptionFormula.setImage(LatexImageGenerator.createLatexImage("E(m) = m^{2} \\mod n"));
        decryptionFormula.setImage(LatexImageGenerator.createLatexImage("D(c) = square roots \\mod n"));
        exampleImage.setImage(LatexImageGenerator.createLatexImage("HELLO \\rightarrow Rabin \\rightarrow ???"));

        detailedDescriptionContainer.setVisible(false);
        detailedDescriptionContainer.setManaged(false);
        toggleDetailsButton.setText("View Detailed Description");
    }

    /**
     * Toggles the visibility of the detailed technical description.
     */
    @FXML
    private void toggleDetailedDescription() {
        boolean selected = toggleDetailsButton.isSelected();
        detailedDescriptionContainer.setVisible(selected);
        detailedDescriptionContainer.setManaged(selected);
        toggleDetailsButton.setText(selected ? "Hide Detailed Description" : "View Detailed Description");
    }
}
