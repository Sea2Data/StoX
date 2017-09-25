package no.imr.sea2data.imrmap.layer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.Workspace;
import no.imr.sea2data.imrmap.utils.JTSUtils;

/**
 *
 * @author aasmunds
 */
public class AreaUnits {

    private static List<LineString> fdOmrLokList = null;
    private static List<LineString> fdOmrList = null;

    public static List<LineString> getFDOmrLokList() {
        if (fdOmrLokList != null) {
            return fdOmrLokList;
        }
        fdOmrLokList = new ArrayList<>();
        String fileName = Workspace.getWorkspaceFolder("map/areaunits") + "/" + "FDOmrLok" + ".dat"; // /*"/home/aasmunds/Documents/SimpleMap/nmdcoast.dat"*/
        if (!(new File(fileName)).exists()) {
            return null;
        }
        AreaUnits.readPointsFromFile(fdOmrLokList, fileName, true);
        return fdOmrLokList;
    }

    public static List<LineString> getFDOmrList() {
        if (fdOmrList != null) {
            return fdOmrList;
        }
        fdOmrList = new ArrayList<>();
        String fileName = Workspace.getWorkspaceFolder("map/areaunits") + "/" + "FDOmr" + ".dat"; // /*"/home/aasmunds/Documents/SimpleMap/nmdcoast.dat"*/
        if (!(new File(fileName)).exists()) {
            return null;
        }
        AreaUnits.readPointsFromFile(fdOmrList, fileName, true);
        return fdOmrList;
    }

    public static Boolean isLocationInArea(Integer area, Integer loc) {
        if (area == null || loc == null) {
            return null;
        }
        for (LineString lr : getFDOmrLokList()) {
            String sOmrLok = JTSUtils.getLineStringName(lr);
            if (sOmrLok != null && sOmrLok.length() == 5) {
                Integer a = Conversion.safeStringtoIntegerNULL(sOmrLok.substring(0, 2));
                if (area.equals(a)) {
                    Integer l = Conversion.safeStringtoIntegerNULL(sOmrLok.substring(2, 5).trim());
                    if(loc.equals(l)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getFDOmrLokFromPos(double lat, double lon) {
        return getLineStringNameFromPos(getFDOmrLokList(), lat, lon);
    }

    public static String getFDOmrFromPos(double lat, double lon) {
        return getLineStringNameFromPos(getFDOmrList(), lat, lon);
    }

    public static String getLineStringNameFromPos(List<LineString> l, Double lat, Double lon) {
        for (LineString lr : l) {
            if (JTSUtils.within(JTSUtils.createCoordinate(lon - 0.0000000001, lat - 0.0000000001), lr)) {
                return JTSUtils.getLineStringName(lr);
            }
        }
        return null;
    }

    public static void readPointsFromFile(Object data, String fileName, boolean lonfirst) {
        BufferedReader br = null;
        int i = 0;
        //LineString lsmax = null;
        Map<Integer, Boolean> depths = new HashMap<Integer, Boolean>();
        try {
            if (!(new File(fileName)).exists()) {
                return;
            }
            br = new BufferedReader(new FileReader(fileName));
            String s;
            List<Coordinate> coords = new ArrayList<Coordinate>();
            s = "";
            String title = "";
            do { // end while 
                Coordinate c = no.imr.sea2data.imrmap.utils.JTSUtils.createCoordinateFromFileInput(s, lonfirst);
                int d = 0;
                if (c == null) {
                    if (coords.size() > 3/* && coords.get(0).equals2D(coords.get(coords.size() - 1))*/) {
                        LineString ls = no.imr.sea2data.imrmap.utils.JTSUtils.createLineString(coords);
                        //ls = discretizeLineString(ls, MIN_DIST * 2);
                        //LineString ls2 = discretizeLineString(ls, MIN_DIST);
                        if (ls != null) {
                            /*if (lsmax == null || ls.getNumPoints() > lsmax.getNumPoints()) {
                             lsmax = ls;
                             }*/
                            List<LineString> l;
                            l = (List<LineString>) data;
                            l.add(ls);
                            if (!title.isEmpty()) {
                                ls.setUserData(title);
                            }
                        }
                    }
                    coords.clear();
                } else {
                    coords.add(c);
                }
                if (s.startsWith(">")) {
                    title = s.length() > 1 ? s.substring(1) : "";
                }
            } while ((s = br.readLine()) != null);// end while
        } catch (IOException ex) {
            Logger.getLogger(no.imr.sea2data.imrmap.utils.JTSUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(no.imr.sea2data.imrmap.utils.JTSUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
