package com.iluncrypt.iluncryptapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

/**
 * Controller for the Affine Cipher description view.
 * Generates LaTeX images dynamically for mathematical equations.
 */
public class AffineCipherDescriptionController {

    @FXML
    private ImageView encryptionFormula;

    @FXML
    private ImageView decryptionFormula;

    @FXML
    private ImageView exampleImage;

    private static final int FIXED_HEIGHT = 35; // Ajuste menor de altura

    /**
     * Initializes the view by setting LaTeX-rendered images for formulas and examples.
     */
    @FXML
    public void initialize() {
        encryptionFormula.setImage(createLatexImage("e(x) = (a \\cdot x + b) \\mod m"));
        decryptionFormula.setImage(createLatexImage("d(x) = a^{-1} \\cdot (C(x) - b) \\mod m"));
        exampleImage.setImage(createLatexImage("H \\rightarrow e(7) = (5 \\cdot 7 + 8) \\mod 26 = 17 \\rightarrow R"));
    }

    /**
     * Generates an image containing a LaTeX-rendered mathematical expression.
     *
     * @param latex The LaTeX string to render.
     * @return A JavaFX Image containing the rendered equation.
     */
    private Image createLatexImage(String latex) {
        try {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXFormula.SERIF, 22); // Tamaño reducido
            icon.setInsets(new Insets(3, 3, 3, 3));

            int originalWidth = icon.getIconWidth();
            int originalHeight = icon.getIconHeight();
            int newWidth = (int) ((double) originalWidth / originalHeight * FIXED_HEIGHT);

            BufferedImage image = new BufferedImage(newWidth, FIXED_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(new Color(255, 255, 255, 0));
            g2d.fillRect(0, 0, newWidth, FIXED_HEIGHT);

            int yOffset = (FIXED_HEIGHT - originalHeight) / 2;
            icon.paintIcon(new JLabel(), g2d, 5, yOffset);
            g2d.dispose();

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

            return new Image(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
