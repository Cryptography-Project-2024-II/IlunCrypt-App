package com.iluncrypt.iluncryptapp.controllers.publickey.rsa;

import com.iluncrypt.iluncryptapp.utils.LatexImageGenerator;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the RSA description view.
 *
 * This controller manages both the user-friendly general explanation and the detailed technical
 * description for RSA. It utilizes the LatexImageGenerator utility class to generate LaTeX-rendered images
 * for the encryption and decryption formulas and an example. A toggle button allows the user to show or hide
 * the detailed description.
 */
public class RSAEncryptionDescriptionController {

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
     * Initializes the RSA description view.
     * Generates the LaTeX images for the formulas and example, and hides the detailed description by default.
     */
    @FXML
    public void initialize() {
        encryptionFormula.setImage(LatexImageGenerator.createLatexImage("E(m) = m^{e} \\mod n"));
        decryptionFormula.setImage(LatexImageGenerator.createLatexImage("D(c) = c^{d} \\mod n"));
        exampleImage.setImage(LatexImageGenerator.createLatexImage("HELLO \\rightarrow RSA \\rightarrow 3F8A2B1C"));

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
