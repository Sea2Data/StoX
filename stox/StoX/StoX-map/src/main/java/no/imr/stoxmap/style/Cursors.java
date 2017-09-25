package no.imr.stoxmap.style;

import java.awt.Cursor;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

/**
 * Class that holds the Transect edit cursors and getters for them
 *
 * @author sjurl
 */
public final class Cursors {

    /**
     * Private class so that the class can't be instantiated
     */
    private Cursors() {
    }

    private static final Cursor CURSOR_E;
    private static final Cursor CURSOR_R;
    private static final Cursor CURSOR_R_DISTANCE;
    private static final Cursor CURSOR_M;
    private static final Cursor CURSOR_T;
    private static final Cursor CURSOR_B;
    private static final Cursor CURSOR_I;

    static {
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final ImageIcon cursorE = new ImageIcon(Cursors.class.getResource("/no/imr/sea2data/stox/icon/cursorE.png"));
        final ImageIcon cursorR = new ImageIcon(Cursors.class.getResource("/no/imr/sea2data/stox/icon/cursorR.png"));
        final ImageIcon cursorT = new ImageIcon(Cursors.class.getResource("/no/imr/sea2data/stox/icon/cursorT.png"));
        final ImageIcon cursorRDistance = new ImageIcon(Cursors.class.getResource("/no/imr/sea2data/stox/icon/cursorRDistance.png"));
        final ImageIcon cursorM = new ImageIcon(Cursors.class.getResource("/no/imr/sea2data/stox/icon/cursorM.png"));
        final ImageIcon cursorB = new ImageIcon(Cursors.class.getResource("/no/imr/sea2data/stox/icon/cursorB.png"));
        final ImageIcon cursorI = new ImageIcon(Cursors.class.getResource("/no/imr/sea2data/stox/icon/cursorI.png"));
        CURSOR_E = tk.createCustomCursor(cursorE.getImage(), new java.awt.Point(1, 1), "imrE");
        CURSOR_R = tk.createCustomCursor(cursorR.getImage(), new java.awt.Point(1, 1), "imrR");
        CURSOR_T = tk.createCustomCursor(cursorT.getImage(), new java.awt.Point(1, 1), "imrT");
        CURSOR_R_DISTANCE = tk.createCustomCursor(cursorRDistance.getImage(), new java.awt.Point(1, 1), "imrRDistance");
        CURSOR_M = tk.createCustomCursor(cursorM.getImage(), new java.awt.Point(1, 1), "imrM");
        CURSOR_B = tk.createCustomCursor(cursorB.getImage(), new java.awt.Point(1, 1), "imrB");
        CURSOR_I = tk.createCustomCursor(cursorI.getImage(), new java.awt.Point(1, 1), "imrI");
    }

    /**
     * Cursor used when the user is able to set the start transect distance
     *
     * @return
     */
    public static Cursor getTransectStartCursor() {
        return CURSOR_T;
    }

    /**
     * Cursor used when the user is able to set the end transect distance
     *
     * @return
     */
    public static Cursor getTransectEndCursor() {
        return CURSOR_E;
    }

    /**
     * Cursor used when the user is able to remove the whole transect
     *
     * @return
     */
    public static Cursor getRemoveWholeTransectCursor() {
        return CURSOR_R;
    }

    /**
     * Cursor used when the user is able to remove a single distance from a
     * transect
     *
     * @return
     */
    public static Cursor getRemoveDistanceFromTransectCursor() {
        return CURSOR_R_DISTANCE;
    }

    /**
     * Cursor that is used for selecting multiple items in the map
     *
     * @return
     */
    public static Cursor getMultipleSelectCursor() {
        return CURSOR_M;
    }

    public static Cursor getBioStationAssignmentCursor() {
        return CURSOR_B;
    }

    public static Cursor getStationSelectCursor() {
        return CURSOR_I;
    }
}
