package com.iluncrypt.iluncryptapp.controllers.publickey.menezesvanstone;

import com.iluncrypt.iluncryptapp.utils.LatexImageGenerator;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for the Menezes‑Vanstone Cryptosystem description view.
 *
 * This controller manages both the general explanation and the detailed technical description for the Menezes‑Vanstone cryptosystem.
 * It utilizes the LatexImageGenerator utility class to generate LaTeX-rendered images for the encryption and decryption representations,
 * as well as an example. A toggle button allows the user to show or hide the detailed description.
 */
public class MenezesVanstoneEncryptionDescriptionController {

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
     * Initializes the Menezes‑Vanstone description view.
     */
    @FXML
    public void initialize() {
        encryptionFormula.setImage(LatexImageGenerator.createLatexImage("E(P) = P + H(k)\\cdot G"));
        decryptionFormula.setImage(LatexImageGenerator.createLatexImage("D(C) = C - H(k)\\cdot G"));
        exampleImage.setImage(LatexImageGenerator.createLatexImage("HELLO \\rightarrow MV \\rightarrow 7B2F9A1C"));

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
