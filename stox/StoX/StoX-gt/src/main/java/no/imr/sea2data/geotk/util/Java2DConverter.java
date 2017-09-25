package no.imr.sea2data.geotk.util;

import com.vividsolutions.jts.awt.GeometryCollectionShape;
import com.vividsolutions.jts.awt.PolygonShape;
import com.vividsolutions.jts.geom.*;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

/**
 * Converts JTS Geometry objects into Java 2D Shape objects
 */
public class Java2DConverter {

    public Java2DConverter() {
    }

    private Shape toShape(Polygon p)
            throws NoninvertibleTransformException {
        ArrayList<Coordinate[]> holeVertexCollection = new ArrayList<Coordinate[]>();

        for (int j = 0; j < p.getNumInteriorRing(); j++) {
            holeVertexCollection.add(p.getInteriorRingN(j).getCoordinates());
        }
        return new PolygonShape(p.getExteriorRing().getCoordinates(), holeVertexCollection);
    }

    private Shape toShape(GeometryCollection gc)
            throws NoninvertibleTransformException {
        GeometryCollectionShape shape = new GeometryCollectionShape();

        for (int i = 0; i < gc.getNumGeometries(); i++) {
            Geometry g = (Geometry) gc.getGeometryN(i);
            shape.add(toShape(g));
        }

        return shape;
    }

    private GeneralPath toShape(MultiLineString mls)
            throws NoninvertibleTransformException {
        GeneralPath path = new GeneralPath();

        for (int i = 0; i < mls.getNumGeometries(); i++) {
            LineString lineString = (LineString) mls.getGeometryN(i);
            path.append(toShape(lineString), false);
        }
        return path;
    }

    /**
     * Converts a Geometry object into a Shape object
     *
     * @param geometry
     * @return
     * @throws NoninvertibleTransformException
     */
    public Shape toShape(Geometry geometry)
            throws NoninvertibleTransformException {

        if (geometry instanceof Polygon) {
            return toShape((Polygon) geometry);
        }

        if (geometry instanceof MultiPolygon) {
            return toShape((MultiPolygon) geometry);
        }

        if (geometry instanceof LineString) {
            return toShape((LineString) geometry);
        }

        if (geometry instanceof MultiLineString) {
            return toShape((MultiLineString) geometry);
        }

        if (geometry instanceof Point) {
            return toShape((Point) geometry);
        }

        if (geometry instanceof GeometryCollection) {
            return toShape((GeometryCollection) geometry);
        }

        throw new IllegalArgumentException(
                "Unrecognized Geometry class: " + geometry.getClass());
    }
}