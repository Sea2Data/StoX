package no.imr.stoxmap.action;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import no.imr.stoxmap.handler.BioStationAssignmentHandler;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;

/**
 * Map action which enable the association edition tool. This is the Action that
 * is triggered when the toolbar button (Arrow with a T) is clicked and handles
 * what should happen when it is clicked
 *
 * @author Johann Sorel (Geomatys)
 */
public class BioStationAssignmentAction extends AbstractMapAction {

    StoXMapSetup setup;

    public BioStationAssignmentAction(StoXMapSetup setup) {
        this.setup = setup;
        //create a small 16x16 icon for the action
        final ImageIcon cursorT = new ImageIcon(BioStationAssignmentHandler.class.getResource("/no/imr/sea2data/stox/icon/cursorB.png"));
        final BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        final Image cursorImage = cursorT.getImage();
        g2d.drawImage(cursorImage, new AffineTransform(16.0 / cursorImage.getWidth(null), 0, 0, 16.0 / cursorImage.getHeight(null), 0, 0), null);

        putValue(SMALL_ICON, new ImageIcon(img));
        putValue(SHORT_DESCRIPTION, "Assign biostations to transect");
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setup.getMapPanel().getMap().setHandler(new BioStationAssignmentHandler(setup));
    }

}
