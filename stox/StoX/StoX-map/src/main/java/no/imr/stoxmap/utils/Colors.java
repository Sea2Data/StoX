package no.imr.stoxmap.utils;

import java.awt.Color;

/**
 * contains color codes used when creating the map layers
 *
 * @author sjurl
 */
public final class Colors {

    /**
     * Hidden constructor
     */
    private Colors() {
    }

    // STRATA COLORS
    public final static Color STRATA_FILL_COLOR = new Color(0, 0, 0, 0.05f);
    public final static Color STRATA_OUTLINE_COLOR = new Color(0, 0, 0, 0.2f);
    public final static Color STRATA_SELECTED_FILL_COLOR = new Color(0, 0, 0, 0.15f);
    public final static Color STRATA_SELECTED_OUTLINE_COLOR = new Color(0, 0, 0, 0.15f);

    // FISH STATION COLORS
    public final static Color FISH_STATION_FILL_COLOR = new Color(56, 141, 226, 240);
    public final static Color FISH_STATION_OUTLINE_COLOR = new Color(0, 0, 0, 0.3f);
    public final static Color FISH_STATION_SELECTED_FILL_COLOR = new Color(238, 215, 123, 240);
    public final static Color FISH_STATION_SELECTED_OUTLINE_COLOR = new Color(0, 0, 0, 0.3f);

    // DISTANCE COLORS
    public final static Color DISTANCE_LINE_COLOR = new Color(166, 28, 63);
    public final static Color DISTANCE_POINT_COLOR = new Color(240, 166, 185);
    public final static Color DISTANCE_POINT_OUTLINE_COLOR = new Color(0, 0, 0, 0.05f);
    public final static Color DISTANCE_LINE_SELECTED_COLOR = new Color(166, 28, 63);
    public final static Color DISTANCE_POINT_SELECTED_COLOR = new Color((int) (240 * 0.6), (int) (166 * 0.6), (int) (185 * 0.6));
    public final static Color DISTANCE_POINT_SELECTED_OUTLINE_COLOR = new Color(0, 0, 0, 0.05f);

    // RECTANGLES
    public final static Color RECTANGLE_FILL_COLOR = new Color(0, 0, 1, 0.05f);
    public final static Color RECTANGLE_OUTLINE_COLOR = new Color(0, 0, 1, 0.2f);
    public final static Color RECTANGLE_SELECTED_FILL_COLOR = new Color(0, 0, 0, 0.15f);
    public final static Color RECTANGLE_SELECTED_OUTLINE_COLOR = new Color(0, 0, 1, 0.2f);

    // LAND
    public final static Color LAND_FILL_COLOR = new Color(252, 255, 198);

    public final static Color LAND_OUTLINE_COLOR =  new Color(224, 227, 176);
    // Earth grid
    public final static Color GRID_COLOR =  new Color(223, 242, 255);
}
