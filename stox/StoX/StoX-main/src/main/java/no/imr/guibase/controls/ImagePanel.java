package no.imr.guibase.controls;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A panel for stretching images. Use setImage with a jpg
 *
 * @author aasmunds
 */
public class ImagePanel extends JPanel {

    private BufferedImage image;          // instance of the image to be displayed
    private BufferedImage scaledImage;    // scaled instance of the image to be displayed

    /**
     * Creates a new instance of ImagePanel
     */
    public ImagePanel() {
    }

    public void setImage(String imageFile) {
        image = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            File f = new File(imageFile);
            if (f.exists()) {
                try {
                    image = ImageIO.read(f);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        invalidate();
        updateUI();

    }

    /**
     * Calculate the scale required to correctly fit the image into panel
     */
    private double GetScale(int panelWidth, int panelHeight, int imageWidth, int imageHeight) {
        double scale = 1;
        double xScale;
        double yScale;
        xScale = (double) panelWidth / imageWidth;
        yScale = (double) panelHeight / imageHeight;
        scale = Math.min(xScale, yScale);
        return scale;
    }

    /**
     * Override paint method of the panel
     */
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        if (image != null) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            scaledImage = image;

            // Get the required sizes for display and calculations
            int panelWidth = this.getWidth();
            int panelHeight = this.getHeight();
            int imageWidth = scaledImage.getWidth();
            int imageHeight = scaledImage.getHeight();

            double scale = GetScale(panelWidth, panelHeight, imageWidth, imageHeight);

            // Calculate the center position of the panel -- with scale
            double xPos = (panelWidth - scale * imageWidth) / 2;
            double yPos = (panelHeight - scale * imageHeight) / 2;

            // Locate, scale and draw image
            AffineTransform at = AffineTransform.getTranslateInstance(xPos, yPos);
            at.scale(scale, scale);
            g2.drawRenderedImage(scaledImage, at);
        }
    }
}
