package no.imr.stoxmap.action;

import java.awt.event.ActionEvent;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import no.imr.sea2data.imrbase.util.ImageUtil;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import no.imr.stoxmap.handler.StationSelectHandler;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;

/**
 * Map action which enable the transect edition tool. Transect is build up from
 * acoustic distances
 *
 * @author Johann Sorel (Geomatys)
 * @author trondwe, IMR
 * @author Sjur Ringheim Lid, IMR
 */
public class StationSelectAction extends AbstractMapAction {

    StoXMapSetup setup;


    public StationSelectAction(StoXMapSetup setup) {
        this.setup = setup;
        putValue(SMALL_ICON, ImageUtil.rescaleImageIcon(new ImageIcon(StationSelectHandler.class.getResource("/no/imr/sea2data/stox/icon/cursorI.png")), 16));
        putValue(SHORT_DESCRIPTION, "Select fishing stations to view details");
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setup.getMapPanel().getMap().setHandler(new StationSelectHandler(setup));
    }

}
