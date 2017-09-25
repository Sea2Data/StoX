package no.imr.stoxmap.style;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.*;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.style.*;

/**
 *
 * @author trondwe
 */
public final class StyleUtil {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    protected static final MutableSLDFactory SLDF = new DefaultSLDFactory();

    private StyleUtil() {
    }

    public static MutableStyle markSymbol(Color fillcolor, Color outlinecolor, Integer symbolsize, String markExpr, String name) {

        final MutableStyle style = SF.style(markSymbolizer(fillcolor, outlinecolor, symbolsize, markExpr));
        return style;
    }

    /**
     * Create a acoustic style that has a line with a point on it and is aware
     * of 3 levels of selection states. Note: How to bring the symbol to the
     * start of the line?
     *
     * @return Style that has a line with a point on it
     */
    public static MutableStyle acousticLayerStyle() {
        Color pointColor = Styles.DISTANCE_POINT_COLOR;
        Color pointAbsenceColor = Styles.DISTANCE_ABSENCE_POINT_COLOR;
        Integer lineWidth = Styles.DISTANCE_LINE_WIDTH;
        String markExpr = "circle";
        Integer pointSize = Styles.DISTANCE_POINT_SIZE;
        Color outlineColor = new Color(0, 0, 0, 0.1f);
        //Color pointColor = Styles.DISTANCE_POINT_COLOR;
        Color lineColor = darken(pointColor, 0.9f);
        Color pAnyColor = new Color(166, 200, 176);//darken(pointColor, 0.84f);
        Color anyLineColor = darken(pAnyColor, 0.9f);
        Color pSelColor = darken(pAnyColor, 0.5f);
        Color selLineColor = darken(pSelColor, 0.9f);

        PointSymbolizer pointSymbol = markSymbolizer(pointColor, outlineColor, pointSize, markExpr);
        PointSymbolizer pointAbsenceSymbol = markSymbolizer(pointAbsenceColor, outlineColor, pointSize, markExpr);
        LineSymbolizer linesymbol = lineSymbolizer(lineColor, lineWidth);
        PointSymbolizer pointSymbolSelectedAnywhere = markSymbolizer(pAnyColor, outlineColor, pointSize, markExpr);
        LineSymbolizer linesymbolSelectedAnywhere = lineSymbolizer(anyLineColor, lineWidth);
        PointSymbolizer pointSymbolSelected = markSymbolizer(pSelColor, outlineColor, pointSize, markExpr);
        LineSymbolizer linesymbolSelected = lineSymbolizer(selLineColor, lineWidth);

        final MutableStyle style = SF.style();
        style.featureTypeStyles().add(createFeature2TypeStyle("selection", "0", "presencelevel", "0", linesymbol, pointAbsenceSymbol));
        style.featureTypeStyles().add(createFeature2TypeStyle("selection", "0", "presencelevel", "1", linesymbol, pointSymbol));
        style.featureTypeStyles().add(createFeatureTypeStyle("selection", "1", linesymbolSelectedAnywhere, pointSymbolSelectedAnywhere));
        style.featureTypeStyles().add(createFeatureTypeStyle("selection", "2", linesymbolSelected, pointSymbolSelected));
        return style;
    }

    public static MutableStyle stationLayerStyle() {
        Color pStationColor = new Color(56, 141, 226, 240);
        Color pStationZeroColor = new Color(255, 255, 255, 240);
        Color pStationSelColor = new Color(238, 215, 123, 240);
        Color outlineColor = new Color(0, 0, 0, 0.3f);

        Integer pointSize = Styles.FISH_STATION_POINT_SIZE;

        PointSymbolizer pStationSymbol = markSymbolizer(pStationColor, outlineColor, pointSize, null);
        PointSymbolizer pStationZeroSymbol = markSymbolizer(pStationZeroColor, outlineColor, pointSize, null);
        PointSymbolizer pStationSelSymbol = markSymbolizer(pStationSelColor, outlineColor, pointSize, null);

        final MutableStyle style = SF.style();
        style.featureTypeStyles().add(createFeature2TypeStyle("selection", "0", "presencelevel", "0", pStationZeroSymbol));
        style.featureTypeStyles().add(createFeature2TypeStyle("selection", "0", "presencelevel", "1", pStationSymbol));
        // Support selection at value 2 and 3 (switch selection by +2/-2 to preserve the zero/nonzero component in selection.
        style.featureTypeStyles().add(createFeatureTypeStyle("selection", "1", pStationSelSymbol));
        return style;
    }

    private static Color darken(Color c, float f) {
        return new Color((int) (c.getRed() * f), (int) (c.getGreen() * f), (int) (c.getBlue() * f));
    }

    /**
     * Associates a vector of symbolisers with a property value in a
     * featuretypestyle style.
     *
     * @param property
     * @param value
     * @param s
     * @return
     */
    private static MutableFeatureTypeStyle createFeatureTypeStyle(String property, String value, Symbolizer... symbs) {
        final MutableRule rule = SF.rule();
        rule.setFilter(FF.like(FF.property(property), value));
        return createFeatureTypeStyle(rule, symbs);
    }

    private static MutableFeatureTypeStyle createFeature2TypeStyle(String property, String value, String property2, String value2, Symbolizer... symbs) {
        final MutableRule rule = SF.rule();
        rule.setFilter(FF.and(FF.like(FF.property(property), value), FF.like(FF.property(property2), value2)));
        return createFeatureTypeStyle(rule, symbs);
    }

    private static MutableFeatureTypeStyle createFeatureTypeStyle(MutableRule rule, Symbolizer... symbs) {
        List<Symbolizer> ruleSymbolizers = new ArrayList<>();
        for (Symbolizer s : symbs) {
            ruleSymbolizers.add(s);
        }
        rule.symbolizers().addAll(ruleSymbolizers);
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
        fts.rules().add(rule);
        return fts;
    }

    public static MutableStyle polygonStyle(Color fillcolor, Color outlinecolor) {
        return SF.style(polygonSymbolizer(fillcolor, outlinecolor));
    }

    public static MutableStyle lineStyle(Color color, Integer lineWidth) {
        return SF.style(lineSymbolizer(color, lineWidth));
    }

    public static LineSymbolizer lineSymbolizer(Color cl, Integer linewidth) {
        //the visual element
        final Expression color = SF.literal(cl);
        final Expression width = FF.literal(linewidth);
        final Stroke stroke = SF.stroke(color, width, StyleConstants.LITERAL_ONE_FLOAT);
        return SF.lineSymbolizer("", "geometry", null, NonSI.PIXEL, stroke, StyleConstants.LITERAL_ZERO_FLOAT);
    }

    public static PointSymbolizer markSymbolizer(Color fillcolor, Color outlinecolor, Integer symbolsize, String markExpr) {
        final Stroke stroke = SF.stroke(outlinecolor, 1);
        final Fill fill = SF.fill(fillcolor);
        // Expression.NIL means default mark symbol (square)
        final Mark mark = SF.mark(markExpr == null ? Expression.NIL : new DefaultLiteral(markExpr), fill, stroke);
        final List<GraphicalSymbol> symbols = new ArrayList<>();
        symbols.add(mark);
        final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
        final Expression size = FF.literal(symbolsize);
        final Expression rotation = StyleConstants.LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;
        final Graphic graphic = SF.graphic(symbols, opacity, size, rotation, anchor, disp);
        final String geometry = null; //use the default geometry of the feature
        //final Description desc = SF.description("", "");
        final Unit unit = NonSI.PIXEL;
        final PointSymbolizer smb = SF.pointSymbolizer("", geometry, null, unit, graphic);
        return smb;
    }

    public static PolygonSymbolizer polygonSymbolizer(Color fillcolor, Color outlinecolor) {

        //stroke element
        final Expression outlineColorExpr = SF.literal(outlinecolor);
        final Expression width = FactoryFinder.getFilterFactory(null).literal(1);
        final Stroke stroke = SF.stroke(outlineColorExpr, width, StyleConstants.LITERAL_ONE_FLOAT);
        //fill element
        final Fill fill = SF.fill(fillcolor);
        final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;
        final Expression offset = StyleConstants.LITERAL_ZERO_FLOAT;
        return SF.polygonSymbolizer("", (String) null, null, NonSI.PIXEL, stroke, fill, disp, offset);
    }

    /*public static MutableStyle pointGradedSizeStyle(String featurepropertyname, Integer dividevalue) {

     final MutableRule over = SF.rule();
     over.setFilter(FF.greaterOrEqual(FF.property(featurepropertyname), FF.literal(dividevalue)));
     over.symbolizers().add(markSymbolizer(Color.RED, Color.RED, 3, StyleConstants.MARK_CIRCLE, ""));

     final MutableRule under = SF.rule();
     under.setFilter(FF.less(FF.property(featurepropertyname), FF.literal(dividevalue)));
     under.symbolizers().add(markSymbolizer(Color.RED, Color.RED, 10, StyleConstants.MARK_CIRCLE, ""));

     final MutableStyle style = SF.style();
     final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
     fts.rules().add(over);
     fts.rules().add(under);
     style.featureTypeStyles().add(fts);
     return style;
     }

     public static MutableStyle lineGradedSizeStyle(String featurepropertyname, Integer dividevalue) {

     // Example usage for the DistanceVO object where one of the attributes are "log_start"
     // Here we states that the size is one size below the dividevalue and another size over this value
     // 
     //     final MutableStyle style = StyleUtil.lineGradedSizeStyle("log_start", 5000);
     //     final FeatureMapLayer layer = MapBuilder.createFeatureLayer(collection, style);
     //
     final MutableRule over = SF.rule();
     over.setFilter(FF.greaterOrEqual(FF.property(featurepropertyname), FF.literal(dividevalue)));
     over.symbolizers().add(lineSymbolizer(Color.BLACK, 2));

     final MutableRule under = SF.rule();
     under.setFilter(FF.less(FF.property(featurepropertyname), FF.literal(dividevalue)));
     under.symbolizers().add(lineSymbolizer(Color.BLACK, 6));

     final MutableStyle style = SF.style();
     final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
     fts.rules().add(over);
     fts.rules().add(under);
     style.featureTypeStyles().add(fts);
     return style;
     }

     public static MutableStyle colorInterpolationRaster() {

     final List<InterpolationPoint> values = new ArrayList<>();
     values.add(SF.interpolationPoint(-5000.0, SF.literal(new Color(46, 154, 88))));
     values.add(SF.interpolationPoint(-3000.0, SF.literal(new Color(251, 255, 128))));
     values.add(SF.interpolationPoint(-1000.0, SF.literal(new Color(224, 108, 31))));
     values.add(SF.interpolationPoint(0.0, SF.literal(new Color(200, 55, 55))));
     values.add(SF.interpolationPoint(5000.0, SF.literal(new Color(215, 244, 244))));
     final Expression lookup = StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
     final Literal fallback = StyleConstants.DEFAULT_FALLBACK;
     final Function function = SF.interpolateFunction(lookup, values, Method.COLOR, Mode.LINEAR, fallback);
     final ChannelSelection selection = null;
     final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
     final OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
     ColorMap colorMap = SF.colorMap(function);
     final ContrastEnhancement enchance = SF.contrastEnhancement(StyleConstants.LITERAL_ONE_FLOAT, ContrastMethod.NONE);
     final ShadedRelief relief = SF.shadedRelief(StyleConstants.LITERAL_ONE_FLOAT);
     final Symbolizer outline = null;
     final Unit uom = NonSI.PIXEL;
     final String geom = StyleConstants.DEFAULT_GEOM;
     final String name = "raster symbol name";
     final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
     final RasterSymbolizer symbol = SF.rasterSymbolizer(
     name, geom, desc, uom, opacity, selection, overlap, colorMap, enchance, relief, outline);
     return SF.style(symbol);
     }*/
    /**
     * Regular interpolation of colors between start and end colors. This
     * function will be a part of geotoolkit palette utility in the future
     *
     * @author Johann Sorel
     *
     * @param start range start color, not null
     * @param end range end color, not null
     * @param divisions number of colors to generate, minimum 2
     * @return Interpolated colors. never null, size equals divisions
     */
    /*public static Color[] interpolate(Color start, Color end, int divisions) {
     ArgumentChecks.ensureBetween("Divisions", 2, Integer.MAX_VALUE, divisions);

     final int argb1 = start.getRGB();
     final int argb2 = end.getRGB();
     final int sa = (argb1 >>> 24) & 0xFF;
     final int sr = (argb1 >>> 16) & 0xFF;
     final int sg = (argb1 >>> 8) & 0xFF;
     final int sb = (argb1) & 0xFF;
     final int ia = ((argb2 >>> 24) & 0xFF) - sa;
     final int ir = ((argb2 >>> 16) & 0xFF) - sr;
     final int ig = ((argb2 >>> 8) & 0xFF) - sg;
     final int ib = ((argb2) & 0xFF) - sb;

     final Color[] colors = new Color[divisions];
     for (int i = 0; i < divisions; i++) {
     final float ratio = (float) i / (divisions - 1);
     final int a = sa + (int) (ratio * ia);
     final int r = sr + (int) (ratio * ir);
     final int g = sg + (int) (ratio * ig);
     final int b = sb + (int) (ratio * ib);
     colors[i] = new Color(r, g, b, a);
     }

     return colors;
     }

     public static List<Color> makeColorScale(float hue, Integer nstep) {
     List<Color> colorlist = new ArrayList();
     float saturationstep = 1.0f / nstep;
     float saturation = 0.0f;
     for (int i = 0; i < nstep; i++) {
     saturation = saturation + saturationstep;
     Color aColor = Color.getHSBColor(hue, saturation, 0.5f);
     colorlist.add(aColor);
     }
     return colorlist;
     }

     public static List<String> makeHexColorScale(float hue, Integer nstep) {
     List<String> colorlist = new ArrayList();
     float saturationstep = 1.0f / nstep;
     float saturation = 0.0f;
     for (int i = 0; i < nstep; i++) {
     saturation = saturation + saturationstep;
     Color aColor = Color.getHSBColor(hue, saturation, 0.5f);
     String red = Integer.toHexString(aColor.getRed());
     if (red.length() == 1) {
     red = "0" + red;
     }
     String green = Integer.toHexString(aColor.getGreen());
     if (green.length() == 1) {
     green = "0" + green;
     }
     String blue = Integer.toHexString(aColor.getBlue());
     if (blue.length() == 1) {
     blue = "0" + blue;
     }
     String hexcolor = red + green + blue;
     colorlist.add(hexcolor);
     }
     return colorlist;
     }

     public static MutableStyle makeBluishRasterStyle(CoverageMapLayer cover) {
     final Integer[] levels = new Integer[10];
     double max = -20000.0;
     double min = +20000.0;
     RenderedImage renderedImage = null;
     try {
     final CoverageReference ref = cover.getCoverageReference();
     final GridCoverageReader reader = ref.acquireReader();
     final GridCoverage2D coverage = (GridCoverage2D) reader.read(ref.getImageIndex(), null);
     renderedImage = coverage.getRenderedImage();
     ref.recycle(reader);
     } catch (CoverageStoreException ex) {
     } catch (CancellationException ex) {
     }
     if (renderedImage == null) {
     return null;
     } else {
     final Raster raster = renderedImage.getData();
     final int width = raster.getWidth();
     final int height = raster.getHeight();
     final int band = 0;
     double value;
     for (int i = 0; i < width; i++) {
     for (int j = 0; j < height; j++) {
     value = raster.getSampleDouble(i, j, band);
     if (!(Double.isNaN(value) || Double.isInfinite(value))) {
     if (max < value) {
     max = value;
     }
     if (min > value) {
     min = value;
     }
     }
     }
     }
     final int minLevel = (int) (min + 0.5);
     final int maxLevel = (int) (max + 0.5);
     levels[0] = minLevel;
     final int step = (int) ((max - min) / 10.0 + 0.5);
     for (int i = 0; i < 9; i++) {
     levels[i + 1] = levels[i] + step;
     }
     levels[9] = maxLevel;
     }
     // Bluish style
     return colorCategorizeRaster(levels, new Color(6, 5, 197), new Color(8, 235, 197));
     }

     public static MutableStyle colorCategorizeRaster(Integer[] levels, Color colorStart, Color colorEnd) {

     // Next statements will become as a part of geotookit:
     // example "bluish" scale new Color(6,5,197) new Color(8,235,197)
     Color[] colors = interpolate(colorStart, colorEnd, levels.length);

     final Map<Expression, Expression> values = new HashMap<>();
     values.put(StyleConstants.CATEGORIZE_LESS_INFINITY, SF.literal(new Color(46, 154, 88)));
     for (int i = 0; i < levels.length; i++) {
     values.put(new DefaultLiteral<Number>(levels[i]), SF.literal(colors[i]));
     }

     final Expression lookup = StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
     final Literal fallback = StyleConstants.DEFAULT_FALLBACK;
     final Function function = SF.categorizeFunction(lookup, values, ThreshholdsBelongTo.SUCCEEDING, fallback);

     final ChannelSelection selection = StyleConstants.DEFAULT_RASTER_CHANNEL_RGB;

     final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
     final OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
     final ColorMap colorMap = SF.colorMap(function);
     final ContrastEnhancement enchance = SF.contrastEnhancement(StyleConstants.LITERAL_ONE_FLOAT, ContrastMethod.NONE);
     final ShadedRelief relief = SF.shadedRelief(StyleConstants.LITERAL_ONE_FLOAT);
     final Symbolizer outline = null;
     final Unit uom = NonSI.PIXEL;
     final String geom = StyleConstants.DEFAULT_GEOM;
     final String name = "raster symbol name";
     final Description desc = StyleConstants.DEFAULT_DESCRIPTION;

     final RasterSymbolizer symbol = SF.rasterSymbolizer(
     name, geom, desc, uom, opacity, selection, overlap, colorMap, enchance, relief, outline);

     return SF.style(symbol);
     }*/
}
