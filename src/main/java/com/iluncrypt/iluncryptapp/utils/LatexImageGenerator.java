package com.iluncrypt.iluncryptapp.utils;

import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import javafx.scene.image.Image;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.Insets;
import javax.imageio.ImageIO;

/**
 * Utility class for generating JavaFX Images from LaTeX strings.
 *
 * This class generates images based on the natural dimensions of the LaTeX rendering,
 * using the specified (or default) font size without adjusting the image width.
 */
public class LatexImageGenerator {

    /**
     * Default font size used when not specified.
     */
    public static final float DEFAULT_FONT_SIZE = 22f;

    /**
     * Default insets applied to the generated TeXIcon.
     */
    public static final Insets DEFAULT_INSETS = new Insets(3, 3, 3, 3);

    /**
     * Generates a JavaFX Image containing a LaTeX-rendered mathematical expression
     * using the specified font size.
     *
     * The image is generated using the natural dimensions of the TeXIcon.
     *
     * @param latex    The LaTeX string to render.
     * @param fontSize The font size (as a float) to use for rendering.
     * @return A JavaFX Image of the rendered LaTeX expression.
     */
    public static Image createLatexImage(String latex, float fontSize) {
        try {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXFormula.SERIF, fontSize);
            icon.setInsets(DEFAULT_INSETS);

            int width = icon.getIconWidth();
            int height = icon.getIconHeight();

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setColor(new Color(255, 255, 255, 0)); // Transparent background
            g2d.fillRect(0, 0, width, height);
            icon.paintIcon(new JLabel(), g2d, 0, 0);
            g2d.dispose();

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

            return new Image(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Overloaded method that uses the default font size.
     *
     * @param latex The LaTeX string to render.
     * @return A JavaFX Image of the rendered LaTeX expression using the default font size.
     */
    public static Image createLatexImage(String latex) {
        return createLatexImage(latex, DEFAULT_FONT_SIZE);
    }
}
