package com.iluncrypt.iluncryptapp.controllers;

import com.iluncrypt.iluncryptapp.utils.LatexImageGenerator;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the ElGamal Cryptosystem description view.
 *
 * This controller manages both the general explanation and the detailed technical description for ElGamal.
 * It uses the LatexImageGenerator utility class to create LaTeX-rendered images for the encryption and decryption formulas,
 * as well as an example. A toggle button allows users to show or hide the detailed description.
 */
public class ElGamalEncryptionDescriptionController {

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
     * Initializes the ElGamal description view.
     */
    @FXML
    public void initialize() {
        encryptionFormula.setImage(LatexImageGenerator.createLatexImage("E(m) = (g^{k}, m \\cdot y^{k})"));
        decryptionFormula.setImage(LatexImageGenerator.createLatexImage("D(c1,c2) = m = c2 \\cdot (c1^{x})^{-1}"));
        exampleImage.setImage(LatexImageGenerator.createLatexImage("HELLO \\rightarrow ElGamal \\rightarrow (C1,C2)"));

        detailedDescriptionContainer.setVisible(false);
        detailedDescriptionContainer.setManaged(false);
        toggleDetailsButton.setText("View Detailed Description");
    }

    /**
     * Toggles the detailed technical description visibility.
     */
    @FXML
    private void toggleDetailedDescription() {
        boolean selected = toggleDetailsButton.isSelected();
        detailedDescriptionContainer.setVisible(selected);
        detailedDescriptionContainer.setManaged(selected);
        toggleDetailsButton.setText(selected ? "Hide Detailed Description" : "View Detailed Description");
    }
}
