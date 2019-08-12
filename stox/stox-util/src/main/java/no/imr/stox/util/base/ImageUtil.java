/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.util.base;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author aasmunds
 */
public class ImageUtil {

    public static ImageIcon rescaleImageIcon(ImageIcon image, int size) {
        final BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        final Image cursorImage = image.getImage();
        g2d.drawImage(cursorImage, new AffineTransform((double) size / cursorImage.getWidth(null), 0, 0, (double) size / cursorImage.getHeight(null), 0, 0), null);
        return new ImageIcon(img);
    }

}
