package com.iluncrypt.iluncryptapp.controllers.symmetrickey.sdes;

import com.iluncrypt.iluncryptapp.utils.LatexImageGenerator;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the Simplified DES (SDES) description view.
 *
 * This controller manages both the general explanation and the detailed technical description for SDES.
 * It uses the LatexImageGenerator utility to generate LaTeX-rendered images for the encryption and decryption representations,
 * as well as an example. A toggle button allows the user to show or hide the detailed description.
 */
public class SDESDescriptionController {

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
     * Initializes the SDES description view.
     */
    @FXML
    public void initialize() {
        encryptionFormula.setImage(LatexImageGenerator.createLatexImage("E(P, K) = SDES(P, K)"));
        decryptionFormula.setImage(LatexImageGenerator.createLatexImage("D(C, K) = SDES^{-1}(C, K)"));
        exampleImage.setImage(LatexImageGenerator.createLatexImage("HELLO \\rightarrow SDES \\rightarrow Q1W2E3"));

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
