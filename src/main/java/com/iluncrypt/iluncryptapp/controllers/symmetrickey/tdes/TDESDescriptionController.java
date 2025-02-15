package com.iluncrypt.iluncryptapp.controllers.symmetrickey.tdes;

import com.iluncrypt.iluncryptapp.utils.LatexImageGenerator;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the Triple DES description view.
 *
 * This controller manages both the general and detailed technical explanations for Triple DES.
 * It uses the LatexImageGenerator utility class to produce LaTeX-rendered images for the encryption and decryption representations,
 * as well as an example. A toggle button allows users to show or hide the detailed description.
 */
public class TDESDescriptionController {

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
     * Initializes the Triple DES description view.
     */
    @FXML
    public void initialize() {
        encryptionFormula.setImage(LatexImageGenerator.createLatexImage("E(P, K1, K2, K3) = DES(DES(E(P, K1), K2), K3)"));
        decryptionFormula.setImage(LatexImageGenerator.createLatexImage("D(C, K1, K2, K3) = DES^{-1}(DES^{-1}(C, K3), K2), K1)"));
        exampleImage.setImage(LatexImageGenerator.createLatexImage("HELLO \\rightarrow TripleDES \\rightarrow 7A3F9C2B"));

        detailedDescriptionContainer.setVisible(false);
        detailedDescriptionContainer.setManaged(false);
        toggleDetailsButton.setText("View Detailed Description");
    }

    /**
     * Toggles the detailed description visibility.
     */
    @FXML
    private void toggleDetailedDescription() {
        boolean selected = toggleDetailsButton.isSelected();
        detailedDescriptionContainer.setVisible(selected);
        detailedDescriptionContainer.setManaged(selected);
        toggleDetailsButton.setText(selected ? "Hide Detailed Description" : "View Detailed Description");
    }
}
