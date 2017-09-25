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
import no.imr.stoxmap.handler.TransectBuildHandler;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;

/**
 * Map action which enable the transect edition tool. Transect is build up from
 * acoustic distances
 *
 * @author Johann Sorel (Geomatys)
 * @author trondwe, IMR
 * @author Sjur Ringheim Lid, IMR
 */
public class TransectBuildAction extends AbstractMapAction {
    StoXMapSetup setup;
    public TransectBuildAction(StoXMapSetup setup) {
        this.setup = setup;
        final ImageIcon cursorS = new ImageIcon(TransectBuildHandler.class.getResource("/no/imr/sea2data/stox/icon/cursorT.png"));
        final BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        final Image cursorImage = cursorS.getImage();
        g2d.drawImage(cursorImage, new AffineTransform(16.0 / cursorImage.getWidth(null), 0, 0, 16.0 / cursorImage.getHeight(null), 0, 0), null);
        
        putValue(SMALL_ICON, new ImageIcon(img));
        putValue(SHORT_DESCRIPTION, "Define transect from distances");
        setEnabled(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (setup == null) {
            return;
        }
        setup.getMapPanel().getMap().setHandler(new TransectBuildHandler(setup));
    }
    
}
