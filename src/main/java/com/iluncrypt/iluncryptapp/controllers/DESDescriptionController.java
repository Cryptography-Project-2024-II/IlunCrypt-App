package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.utils.LatexImageGenerator;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the DES description view.
 *
 * This controller manages both the user-friendly general explanation and the detailed technical
 * description for DES. It uses the LatexImageGenerator utility to create LaTeX-rendered images for
 * the encryption and decryption formulas, as well as an example. A toggle button allows users to
 * show or hide the detailed description.
 */
public class DESDescriptionController {

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
     * Initializes the DES description view.
     * Generates the LaTeX images for the encryption and decryption formulas and an example,
     * and hides the detailed description by default.
     */
    @FXML
    public void initialize() {
        encryptionFormula.setImage(LatexImageGenerator.createLatexImage("E(P, K) = DES(P, K)"));
        decryptionFormula.setImage(LatexImageGenerator.createLatexImage("D(C, K) = DES^{-1}(C, K)"));
        exampleImage.setImage(LatexImageGenerator.createLatexImage("HELLO \\rightarrow DES \\rightarrow 3F8A2B1C"));

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
