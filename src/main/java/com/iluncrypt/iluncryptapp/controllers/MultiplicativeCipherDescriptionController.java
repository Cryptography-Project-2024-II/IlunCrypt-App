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

public class MultiplicativeCipherDescriptionController {

    @FXML
    private ImageView encryptionFormula;

    @FXML
    private ImageView decryptionFormula;

    @FXML
    private ImageView exampleImage;

    private static final int FIXED_HEIGHT = 60; // Altura fija para todas las ecuaciones

    @FXML
    public void initialize() {
        encryptionFormula.setImage(createLatexImage("C(x) = (ax + b) \\mod m"));
        decryptionFormula.setImage(createLatexImage("P(x) = a^{-1} (C(x) - b) \\mod m"));
        exampleImage.setImage(createLatexImage("HELLO \\rightarrow C(7) = (5 \\times 7 + 8) \\mod 26 = 17 \\rightarrow R"));
    }

    private Image createLatexImage(String latex) {
        try {
            // Crear la fórmula LaTeX
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXFormula.SERIF, 40);
            icon.setInsets(new Insets(5, 5, 5, 5));

            // Obtener el tamaño original del icono
            int originalWidth = icon.getIconWidth();
            int originalHeight = icon.getIconHeight();

            // Calcular nuevo ancho manteniendo la proporción
            int newWidth = (int) ((double) originalWidth / originalHeight * FIXED_HEIGHT);

            // Crear imagen con fondo transparente y tamaño uniforme
            BufferedImage image = new BufferedImage(newWidth, FIXED_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(new Color(255, 255, 255, 0));
            g2d.fillRect(0, 0, newWidth, FIXED_HEIGHT);

            // Dibujar la ecuación alineada a la izquierda
            int yOffset = (FIXED_HEIGHT - originalHeight) / 2;
            icon.paintIcon(new JLabel(), g2d, 5, yOffset); // Alineación izquierda
            g2d.dispose();

            // Convertir BufferedImage a Image de JavaFX
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
