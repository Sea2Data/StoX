package no.imr.sea2data.geotk.util;

import java.awt.Color;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.style.*;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.style.*;

/**
 *
 * @author trondwe
 */
public final class CartographicUtils {
    
    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    protected static final MutableSLDFactory SLDF = new DefaultSLDFactory();
    
    private CartographicUtils() {
    }
    
    public static MutableStyle colorPolygonStyle(Color fillcolor, Color outlinecolor) {

        //general informations
        final String name = "mySymbol";
        final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = NonSI.PIXEL;
        final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;
        final Expression offset = StyleConstants.LITERAL_ZERO_FLOAT;

        //stroke element
        final Expression color = SF.literal(outlinecolor);
        final Expression width = FactoryFinder.getFilterFactory(null).literal(1);
        final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
        final Stroke stroke = SF.stroke(color, width, opacity);

        //fill element
        final Fill fill = SF.fill(fillcolor);
        
        final PolygonSymbolizer symbolizer = SF.polygonSymbolizer(name, geometry, desc, unit, stroke, fill, disp, offset);
        final MutableStyle style = SF.style(symbolizer);
        return style;
        
    }
    
    public static MutableStyle markSymbol(Color fillcolor, Color outlinecolor, Integer symbolsize, Expression symboltype, String name) {
        
        final MutableStyle style = SF.style(markSymbolizer(fillcolor, outlinecolor, symbolsize, symboltype, name));
        return style;
    }
    
    public static PointSymbolizer markSymbolizer(Color fillcolor, Color outlinecolor, Integer symbolsize, Expression symboltype, String name) {
        
        final Description desc = SF.description(name, name);
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = NonSI.PIXEL;

        //the visual element
        final Expression size = FF.literal(symbolsize);
        final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
        final Expression rotation = StyleConstants.LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;
        
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        final Stroke stroke = SF.stroke(outlinecolor, 1);
        final Fill fill = SF.fill(fillcolor);
        
        final Mark mark = SF.mark(symboltype, fill, stroke);
        symbols.add(mark);
        final Graphic graphic = SF.graphic(symbols, opacity, size, rotation, anchor, disp);
        
        final PointSymbolizer symbolizer = SF.pointSymbolizer(name, geometry, desc, unit, graphic);
        return symbolizer;
    }

    /**
     * Create a style that has a line with a point on it
     *
     * @param lineColor color of the line
     * @param pointColor color of the point
     * @param outlineColorPoint color of the point outline
     * @param linewidth width of the line
     * @param pointSize size of the point
     * @param pointMark How the mark should look. Use WellKnownMarkFactory for
     * normal marks
     * @return Style that has a line with a point on it
     */
    public static MutableStyle colorLineWithStartPointStyle(Color lineColor, Color pointColor, Color outlineColorPoint, Integer linewidth, Integer pointSize, Expression pointMark) {
        LineSymbolizer linesymbol = colorLineStyleSymbolizer(lineColor, linewidth);
        PointSymbolizer pointSymbol = markSymbolizer(pointColor, outlineColorPoint, pointSize, pointMark, "myfeature");
        MutableFeatureTypeStyle fts = createIncludeEstimateStyle(Color.GREEN, Color.GREEN, Color.GREEN, linewidth, pointSize, pointMark, "notSelectedFeature", "include_estimate", "1");
        MutableFeatureTypeStyle ftsSelected = createIncludeEstimateStyle(new Color(166, 28, 63), new Color((int) (240 * 0.6), (int) (166 * 0.6), (int) (185 * 0.6)), new Color(0, 0, 0, 0.05f), linewidth, pointSize, pointMark, "selectedFeature", "include_estimate", "2");
        final MutableStyle style = SF.style(linesymbol, pointSymbol);
        style.featureTypeStyles().add(fts);
        style.featureTypeStyles().add(ftsSelected);
        return style;
    }
    
    private static MutableFeatureTypeStyle createIncludeEstimateStyle(Color lineColor, Color pointColor, Color outlineColor, Integer linewidth, Integer pointSize, Expression pointMark, String name, String property, String value) {
        LineSymbolizer linesymbolNotSelected = colorLineStyleSymbolizer(lineColor, linewidth);
        PointSymbolizer pointSymbolNotSelected = markSymbolizer(pointColor, outlineColor, pointSize, pointMark, name);
        final MutableRule notSelected = SF.rule();
        notSelected.setFilter(FF.like(FF.property(property), value));
        List<Symbolizer> symbolsNotSelected = new ArrayList<Symbolizer>();
        symbolsNotSelected.add(linesymbolNotSelected);
        symbolsNotSelected.add(pointSymbolNotSelected);
        notSelected.symbolizers().addAll(symbolsNotSelected);
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
        fts.rules().add(notSelected);
        return fts;
    }

    /**
     * Creates a style that shows a line in the given color and with the given
     * width
     *
     * @param fillcolor color of the line
     * @param linewidth width of the line
     * @return
     */
    public static MutableStyle colorLineStyle(Color fillcolor, Integer linewidth) {
        
        final MutableStyle style = SF.style(colorLineStyleSymbolizer(fillcolor, linewidth));
        return style;
    }
    
    public static LineSymbolizer colorLineStyleSymbolizer(Color fillcolor, Integer linewidth) {

        //general informations
        final String name = "mySymbol";
        final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
        final String geometry = "line"; //use the default geometry of the feature
        final Unit unit = NonSI.PIXEL;
        final Expression offset = StyleConstants.LITERAL_ZERO_FLOAT;

        //the visual element
        final Expression color = SF.literal(fillcolor);
        final Expression width = FF.literal(linewidth);
        final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
        final Stroke stroke = SF.stroke(color, width, opacity);
        
        final LineSymbolizer symbolizer = SF.lineSymbolizer(name, geometry, desc, unit, stroke, offset);
        return symbolizer;
    }
    
    public static MutableStyle pointGradedSizeStyle(String featurepropertyname, Integer dividevalue) {
        
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
        //     final MutableStyle style = CartographicUtils.lineGradedSizeStyle("log_start", 5000);
        //     final FeatureMapLayer layer = MapBuilder.createFeatureLayer(collection, style);
        //
        final MutableRule over = SF.rule();
        over.setFilter(FF.greaterOrEqual(FF.property(featurepropertyname), FF.literal(dividevalue)));
        over.symbolizers().add(colorLineStyleSymbolizer(Color.BLACK, 2));
        
        final MutableRule under = SF.rule();
        under.setFilter(FF.less(FF.property(featurepropertyname), FF.literal(dividevalue)));
        under.symbolizers().add(colorLineStyleSymbolizer(Color.BLACK, 6));
        
        final MutableStyle style = SF.style();
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
        fts.rules().add(over);
        fts.rules().add(under);
        style.featureTypeStyles().add(fts);
        return style;
    }
    
    public static MutableStyle colorInterpolationRaster() {
        
        final List<InterpolationPoint> values = new ArrayList<InterpolationPoint>();
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
    }

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
    public static Color[] interpolate(Color start, Color end, int divisions) {
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
        
        final Map<Expression, Expression> values = new HashMap<Expression, Expression>();
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
    }
}
