package no.imr.stox.functions.utils;

import com.vividsolutions.jts.geom.MultiPolygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.XMLWriter;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.bo.PSUAssignmentBO;
import no.imr.stox.bo.PSUStratumBO;
import no.imr.stox.bo.PolygonAreaMatrix;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;

/**
 * A class to handle process data io operations.
 *
 * @author aasmunds
 */
public final class AbndEstProcessDataUtil {

    /**
     * Hidden constructor
     */
    private AbndEstProcessDataUtil() {
    }

    public static final String TABLE_BIOTICASSIGNMENT = "bioticassignment";
    public static final String TABLE_SUASSIGNMENT = "suassignment";
    public static final String TABLE_ASSIGNMENTRESOLUTION = "assignmentresolution";
    public static final String TABLE_EDSUPSU = "edsupsu";
    public static final String TABLE_PSUSTRATUM = "psustratum";
    public static final String TABLE_STRATUMPOLYGON = "stratumpolygon";
    public static final String TABLE_TEMPORAL = "temporal";
    public static final String TABLE_GEARFACTOR = "gearfactor";
    public static final String TABLE_SPATIAL = "spatial";
    public static final String TABLE_PLATFORM = "platform";
    public static final String TABLE_COVPARAM = "covparam";
    public static final String TABLE_AGEERROR = "ageerror";
    public static final String TABLE_STRATUMNEIGHBOUR = "stratumneighbour";

    private static final String TABLE = "TABLE";

       public static void save(int level, XMLStreamWriter xmlsw, ProcessDataBO pd, String projectFolder) {
        try {
            if (pd == null) {
                return;
            }
            XMLWriter.writeXMLElementStart(level++, xmlsw, ProjectUtils.PROCESSDATA);
            for (String table : pd.getOutputOrder()) {
                final MatrixBO m = pd.getMatrix(table);
                Boolean coldef = m.getMetaMatrix().getDimensions().get(IMetaMatrix.COL) != null;
                Map<String, String> attr = new TreeMap<>(new Comparator<String>() {
                    
                    @Override
                    public int compare(String o1, String o2) {
                        String d1 = m.getMetaMatrix().getDimensionByName(o1);
                        String d2 = m.getMetaMatrix().getDimensionByName(o2);
                        if (d1.equalsIgnoreCase("row") && d2.equalsIgnoreCase("col")) {
                            return -1;
                        } else if (d1.equalsIgnoreCase("col") && d2.equalsIgnoreCase("row")) {
                            return 1;
                        }
                        return d1.compareTo(d2);
                    }
                    
                });
                XMLWriter.writeXMLElementStart(level++, xmlsw, table.toLowerCase());
                attr.clear();
                for (String rowKey : m.getSortedRowKeys()) {
                    attr.put(m.getMetaMatrix().getDimensions().get(IMetaMatrix.ROW).toLowerCase(), rowKey);
                    if (coldef) {
                        for (String colKey : m.getSortedColKeys(rowKey)) {
                            attr.put(m.getMetaMatrix().getDimensions().get(IMetaMatrix.COL).toLowerCase(), colKey);
                            Object value = m.getRowColValue(rowKey, colKey);
                            XMLWriter.writeXMLElementStartWithoutNewline(level, xmlsw, m.getMetaMatrix().getVariable().toLowerCase(), attr);
                            if (value != null) {
                                xmlsw.writeCharacters(value.toString());
                            }
                            xmlsw.writeEndElement();
                            xmlsw.writeCharacters("\n");
                        }
                    } else {
                        Object value = m.getRowValue(rowKey);
                        XMLWriter.writeXMLElementStartWithoutNewline(level, xmlsw, m.getMetaMatrix().getVariable().toLowerCase(), attr);
                        if (value != null) {
                            xmlsw.writeCharacters(value.toString());
                        }
                        xmlsw.writeEndElement();
                        xmlsw.writeCharacters("\n");
                    }
                }
                XMLWriter.writeXMLElementEnd(--level, xmlsw);
            }
            XMLWriter.writeXMLElementEnd(--level, xmlsw);
        } catch (XMLWriter.StAXWriterException | XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @param pd Process data
     * @return PSU's from process data
     */
    public static Collection<String> getPSUs(ProcessDataBO pd) {
        return getPSUStrata(pd).getRowKeys();
    }

    /**
     *
     * @param pd Process data
     * @return Strata from process data
     */
    public static Collection<String> getStrata(ProcessDataBO pd) {
        return getStratumPolygons(pd).getRowKeys();
    }

    /**
     * @param pd
     * @param psu
     * @return psu over distance. Used in horizontal aggregation
     */
    public static Collection<String> getEDSUsByPSU(ProcessDataBO pd, String psu) {
        MatrixBO grp = (MatrixBO) getEDSUPSUs(pd).getDefaultValue();
        Set<String> res = new HashSet<>();
        for (String edsu : grp.getKeys()) {
            if (psu.equals((String) getEDSUPSUs(pd).getRowValue(edsu))) {
                res.add(edsu);
            }
        }
        return res;
    }

    /**
     *
     * @param pd
     * @param stratum
     * @return EDSUs by stratum
     */
    public static Collection<String> getEDSUsByStratum(ProcessDataBO pd, String stratum) {
        Set<String> EDSUs = new HashSet<>();
        for (String psu : getPSUsByStratum(pd, stratum)) {
            EDSUs.addAll(getEDSUsByPSU(pd, psu));
        }
        return EDSUs;
    }

    /**
     * Get psus by stratum
     *
     * @param pd
     * @param stratum
     * @return
     */
    public static Collection<String> getPSUsByStratum(ProcessDataBO pd, String stratum) {
        MatrixBO grp = (MatrixBO) getPSUStrata(pd).getDefaultValue();
        Set<String> res = new HashSet<>();
        if (grp != null) {
            for (String psu : grp.getKeys()) {
                if (stratum.equals((String) getPSUStrata(pd).getRowValue(psu))) {
                    res.add(psu);
                }
            }
        }
        return res;
    }

    /**
     * @param pd
     * @param psu
     * @return Assignments by PSU
     */
    /*public static Collection<String> getDistanceAssignmentsByPSU(ProcessDataBO pd, String psu) {
     Collection<String> psudistances = getEDSUsByPSU(pd, psu);
     Collection<String> assignments = new HashSet<>();
     MatrixBO grp = (MatrixBO) getSUAssignments(pd).getDefaultValue();
     for (String distance : grp.getKeys()) {
     if (psudistances.contains(distance)) {
     MatrixBO distanceRow = (MatrixBO) grp.getValue(distance);
     for (String channel : distanceRow.getKeys()) {
     String assignment = (String) distanceRow.getValue(channel);
     assignments.add(assignment);
     }
     }
     }
     return new ArrayList(assignments);
     }*/
    /**
     * Set association. In the gui, for each psutransect assignment, keep a list
     * of stations included
     *
     * @param pd
     * @param assignment
     * @param station
     * @param include
     * @param weight
     */
    public static void assignStation(ProcessDataBO pd, String assignment, String station, Boolean include, Double weight) {
        if (include) {
            getBioticAssignments(pd).setRowColValue(assignment, station, weight);
        } else {
            MatrixBO asgRow = (MatrixBO) getBioticAssignments(pd).getRowValue(assignment);
            if (asgRow != null) {
                asgRow.removeValue(station);
            }
        }
    }

    /**
     * Get a list of stations by an assignment key
     *
     * @param pd
     * @param assignment
     * @return
     */
    public static Collection<String> getStationsByAssignment(ProcessDataBO pd, String assignment) {
        MatrixBO asgRow = (MatrixBO) getBioticAssignments(pd).getRowValue(assignment);
        if (asgRow == null) {
            return new ArrayList<>();
        }
        return asgRow.getKeys();
    }

    /**
     *
     * Returns a collection of stations that are connected to the provided psu
     * or an empty collection if none is found
     *
     * @param pd
     * @param psu
     * @return the station keys given by psu. note that PB is used with
     * psuassignment.
     */
    /*public static Collection<String> getStationsByPSU(ProcessDataBO pd, String psu) {
        Set<String> res = new HashSet<>();
        MatrixBO estLayer = pd.getMatrix(Functions.TABLE_ESTLAYERDEF);
        estLayer.getRowKeys().stream().forEach((layer) -> {
            res.addAll(getStationsByPSUAndLayer(pd, psu, layer));
        });
        return res;
    }*/

    public static Collection<String> getStationsByPSUAndLayer(ProcessDataBO pd, String psu, String layer) {
        String asgKey = AbndEstProcessDataUtil.getSUAssignment(pd, psu, layer);
        if (asgKey == null) {
            return new ArrayList<>();
        }
        return getStationsByAssignment(pd, asgKey);
    }

    public static String getSUAssignment(ProcessDataBO pd, String sampleUnit, String layer) {
        return (String) getSUAssignments(pd).getRowColValue(sampleUnit, layer);
    }

    /**
     * Set acoustic assignment
     *
     * @param pd
     * @param layer
     * @param sampleUnit
     * @param assignment
     */
    public static void setSUAssignment(ProcessDataBO pd, String sampleUnit, String layer, String assignment) {
        getSUAssignments(pd).setRowColValue(sampleUnit, layer, assignment);
    }

    public static void setAssignmentResolution(ProcessDataBO pd, MatrixBO resolution) {
        setAssignmentResolution(pd, (String) resolution.getRowValue(Functions.RES_SAMPLEUNITTYPE));
    }

    public static void setAssignmentResolution(ProcessDataBO pd, String sampleUnitType) {
        getAssignmentResolutions(pd).setRowValue(Functions.RES_SAMPLEUNITTYPE, sampleUnitType);
        //getAssignmentResolutions(pd).setRowValue(Functions.RES_LAYERTYPE, layerType);
    }

    /*public static void setSUAssignment(ProcessDataBO pd, String sampleUnitType, String su, Integer firstChannel, Integer lastChannel, String assignment) {
     for (Integer ch = firstChannel; ch <= lastChannel; ch++) {
     setSUAssignment(pd, Functions.SAMPLEUNIT_PSU, su, ch.toString(), assignment);
     }
     }*/
    /**
     *
     * @param pd
     * @param edsu
     * @return EDSU PSU
     */
    public static String getEDSUPSU(ProcessDataBO pd, String edsu) {
        return (String) getEDSUPSUs(pd).getRowValue(edsu);
    }

    /**
     * Set EDSU PSU
     *
     * @param pd
     * @param edsu
     * @param psu
     */
    public static void setEDSUPSU(ProcessDataBO pd, String edsu, String psu) {
        getEDSUPSUs(pd).setRowValue(edsu, psu);
    }

    /**
     * Get which stratum the psu belongs to
     *
     * @param pd
     * @param psu
     * @return
     */
    public static String getPSUStratum(ProcessDataBO pd, String psu) {
        return (String) getPSUStrata(pd).getRowValue(psu);
    }

    /**
     * Set which strata the psu belongs to
     *
     * @param pd
     * @param psu
     * @param stratum
     */
    public static void setPSUStratum(ProcessDataBO pd, String psu, String stratum) {
        getPSUStrata(pd).setRowValue(psu, stratum);
    }

    /**
     * Set the stratum polygon and preserve the includeintotal flag (i.e from
     * edition of stratum)
     *
     * @param pd
     * @param stratum
     * @param mp
     */
    public static void setStratumPolygon(ProcessDataBO pd, String stratum, MultiPolygon mp) {
        getStratumPolygons(pd).setRowColValue(stratum, Functions.COL_POLVAR_POLYGON, mp);
    }

    /**
     * Set the stratum polygon with the include in total flag. (i.e when reading
     * from file, or creating a new stratum)
     *
     * @param pd
     * @param stratum
     * @param includeInTotal
     * @param mp
     */
    public static void setStratumPolygon(ProcessDataBO pd, String stratum, Boolean includeInTotal, MultiPolygon mp) {
        getStratumPolygons(pd).setRowColValue(stratum, Functions.COL_POLVAR_INCLUDEINTOTAL, includeInTotal != null ? includeInTotal.toString().toLowerCase() : null);
        setStratumPolygon(pd, stratum, mp);
    }

    /**
     * Return a matrix defined as Matrix[ROW~Assignment / COL~Station /
     * VAR~StationWeight]
     *
     * @param pd
     * @return
     */
    public static MatrixBO getBioticAssignments(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_BIOTICASSIGNMENT);
    }

    public static MatrixBO getSUAssignments(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_SUASSIGNMENT);
    }

    public static MatrixBO getAssignmentResolutions(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_ASSIGNMENTRESOLUTION);
    }

    /**
     * Return a matrix defined as Matrix[ROW~EDSU / VAR~PSU]
     *
     * @param pd
     * @return
     */
    public static MatrixBO getEDSUPSUs(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_EDSUPSU);
    }

    /**
     *
     * @param pd
     * @return
     */
    public static MatrixBO getPSUStrata(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_PSUSTRATUM);
    }

    public static MatrixBO getStratumPolygons(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_STRATUMPOLYGON);
    }

    public static MatrixBO getTemporal(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_TEMPORAL);
    }

    public static MatrixBO getGear(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_GEARFACTOR);
    }

    public static MatrixBO getSpatial(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_SPATIAL);
    }

    public static MatrixBO getPlatform(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_PLATFORM);
    }
    public static MatrixBO getCovParam(ProcessDataBO pd) {
        return pd.getMatrix(TABLE_COVPARAM);
    }
    /**
     * This function assumes there are only one assignment per transect and all
     * channels and gives the tuple back: T, F, W
     *
     * @param pd
     * @return
     */
    public static List<PSUAssignmentBO> getTransectAssignments(ProcessDataBO pd) {
        List<PSUAssignmentBO> traAsgs = new ArrayList<>();
        Collection<String> psus = getPSUs(pd);
        for (String psu : psus) {
            String assignment = null;
            MatrixBO row = getSUAssignments(pd).getRowValueAsMatrix(psu);
            if (row != null && !row.getKeys().isEmpty()) {
                // all channels should have the same assignment if this function is called.
                // in future deepvision strategy will separate assignments vertically.
                assignment = (String) row.getValue(row.getKeys().get(0));
            }
            if (assignment == null) {
                continue;
            }
            Collection<String> stations = getStationsByAssignment(pd, assignment);
            for (String station : stations) {
                Double weight = getBioticAssignments(pd).getRowColValueAsDouble(assignment, station);
                traAsgs.add(new PSUAssignmentBO(psu, station, weight));
            }
        }
        return traAsgs;
    }

    /**
     *
     * @param pd
     * @return EDSU PSUBO's
     */
    public static List<PSUStratumBO> getPSUStratumBOs(ProcessDataBO pd) {
        List<PSUStratumBO> psuStrata = new ArrayList<>();
        for (String psu : getPSUStrata(pd).getRowKeys()) {
            String stratum = (String) getPSUStrata(pd).getRowValue(psu);
            psuStrata.add(new PSUStratumBO(psu, stratum));
        }
        return psuStrata;
    }

    /**
     * Set EDSU PSU BO - tagging of EDSU to PSU
     *
     * @param pd
     * @param psuStratumBOs
     */
    public static void setPSUStratumBOs(ProcessDataBO pd, List<PSUStratumBO> psuStratumBOs) {
        MatrixBO psuStrata = AbndEstProcessDataUtil.getPSUStrata(pd);
        psuStrata.clear();
        for (PSUStratumBO psuStratumBO : psuStratumBOs) {
            psuStrata.setRowValue(psuStratumBO.getPsu(), psuStratumBO.getStratum());
        }
    }

    public static MatrixBO getRectanglePolygons(ProcessDataBO pd) {
        MatrixBO result = new MatrixBO(Functions.MM_POLYGON_MATRIX);
        MatrixBO psuTable = AbndEstProcessDataUtil.getPSUStrata(pd);
        MatrixBO strataPlg = AbndEstProcessDataUtil.getStratumPolygons(pd);
        for (String psu : psuTable.getRowKeys()) {
            String strata = (String) psuTable.getRowValue(psu);
            MultiPolygon strataPol = (MultiPolygon) strataPlg.getRowColValue(strata, Functions.COL_POLVAR_POLYGON);
            if (strataPol == null) {
                continue;
            }
            MultiPolygon pol = RectangleUtil.getPSUPolygon(psu, strataPol);
            if (pol == null) {
                continue;
            }
            result.setRowColValue(psu, Functions.COL_POLVAR_POLYGON, pol);
        }
        return result;
    }

    public static PolygonAreaMatrix getPolygonArea(MatrixBO polygons) {
        PolygonAreaMatrix result = new PolygonAreaMatrix();

        for (String polKey : polygons.getRowKeys()) {
            MultiPolygon pol = (MultiPolygon) polygons.getRowColValue(polKey, Functions.COL_POLVAR_POLYGON);
            if (pol == null) {
                continue;
            }
            Double area = JTSUtils.polygonArea(pol);
            if (area == null) {
                continue;
            }
            result.getData().setRowValue(polKey, area);
        }
        return result;
    }

    /**
     *
     * @param pd
     * @param su
     * @param sampleUnitType
     * @return psu by sample unit using the assignment resolution
     */
    public static String getPSUBySampleUnit(ProcessDataBO pd, String su) {
        String sampleUnitType = (String) AbndEstProcessDataUtil.getAssignmentResolutions(pd).getRowValue(Functions.RES_SAMPLEUNITTYPE);
        if (sampleUnitType == null) {
            return null;
        }
        switch (sampleUnitType) {
            case Functions.SAMPLEUNIT_EDSU:
                return AbndEstProcessDataUtil.getEDSUPSU(pd, su);
            case Functions.SAMPLEUNIT_PSU:
                return su;
        }
        return null;
    }

    /**
     * Set assignments from vectors.
     *
     * @param m
     * @param assigmentValues
     * @param stations
     * @param weightValues
     */
    public static void setAssignments(MatrixBO m, String[] assigmentValues, String[] stations, double[] weightValues) {
        for (int i = 0; i < assigmentValues.length; i++) {
            m.setRowColValue(assigmentValues[i], stations[i], weightValues[i]);
        }
    }

    /**
     * Regroup asssignments so that SUAssignment's refers to same assignment if
     * set of stations:weight equals.
     *
     * @param pd
     */
    public static void regroupAssignments(ProcessDataBO pd) {
        List<String> ids = new ArrayList<>();
        Map<String, String> asgIdMap = new HashMap<>();
        MatrixBO bAsg = getBioticAssignments(pd);
        for (String asgKey : bAsg.getSortedRowKeys()) {
            String id = "";
            for (String stationKey : bAsg.getSortedColKeys(asgKey)) {
                if (!id.isEmpty()) {
                    id += ",";
                }
                id += stationKey;
            }
            if (!ids.contains(id)) {
                ids.add(id);
            }
            asgIdMap.put(asgKey, id);
        }
        // Update Biotic assignment
        bAsg.clear();
        for (Integer asg = 1; asg <= ids.size(); asg++) {
            String[] stws = ids.get(asg - 1).split(",");
            for (String stw : stws) {
                String[] s = stw.split(":");
                String st = s[0];
                Double w = 1d;
                bAsg.setRowColValue(asg.toString(), st, w);
            }
        }
        // Update SU assignment
        MatrixBO suAsg = AbndEstProcessDataUtil.getSUAssignments(pd);
        for (String su : suAsg.getRowKeys()) {
            for (String layer : suAsg.getRowColKeys(su)) {
                String asgKey = (String) suAsg.getRowColValue(su, layer);
                String groupedAsgId = asgIdMap.get(asgKey);
                if (groupedAsgId == null) {
                    continue;
                }
                Integer asgKeyNew = ids.indexOf(groupedAsgId);
                if (asgKeyNew == -1) {
                    continue;
                }
                asgKeyNew++;
                suAsg.setRowColValue(su, layer, asgKeyNew.toString());
            }
        }
    }

    /**
     * Check if resolution is compatible with process data resolution
     *
     * @param pd
     * @param res
     * @return
     */
    public static Boolean isResolutionCompatibleWithSUAssignment(ProcessDataBO pd, MatrixBO res) {
        MatrixBO pdRes = getAssignmentResolutions(pd);
        String pdSampleUnitType = (String) pdRes.getRowValue(Functions.RES_SAMPLEUNITTYPE);
        String sampleUnitType = (String) res.getRowValue(Functions.RES_SAMPLEUNITTYPE);
        if (sampleUnitType == null || pdSampleUnitType == null) {
            return false;
        }
        Boolean sampleUnitOK = pdSampleUnitType.equals(sampleUnitType)
                || pdSampleUnitType.equals(Functions.SAMPLEUNIT_STRATUM)
                && (sampleUnitType.equals(Functions.SAMPLEUNIT_PSU) || sampleUnitType.equals(Functions.SAMPLEUNIT_EDSU))
                || pdSampleUnitType.equals(Functions.SAMPLEUNIT_PSU) && sampleUnitType.equals(Functions.SAMPLEUNIT_EDSU);
        return sampleUnitOK;
    }

    /**
     * Get SU assignment from given layer and sampleunit and resolution by
     * translating the resolution to process data resolution
     *
     * @param pd process data
     * @param sampleUnit sample unit
     * @param estLayer
     * @param sampleUnitType
     * @return Assignment id
     */
    public static String getSUAssignmentIDBySampleUnitAndEstimationLayer(ProcessDataBO pd, String sampleUnit,
            String estLayer, String sampleUnitType) {
        MatrixBO pdRes = getAssignmentResolutions(pd);
        String pdSampleUnitType = (String) pdRes.getRowValue(Functions.RES_SAMPLEUNITTYPE);
        if (sampleUnitType == null || pdSampleUnitType == null) {
            return null;
        }
        String pdSampleUnit = sampleUnit;
        switch (pdSampleUnitType) {
            case Functions.SAMPLEUNIT_STRATUM:
                switch (sampleUnitType) {
                    case Functions.SAMPLEUNIT_PSU:
                        pdSampleUnit = getPSUStratum(pd, sampleUnit);
                        break;
                    case Functions.SAMPLEUNIT_EDSU:
                        pdSampleUnit = null;
                        String psu = getEDSUPSU(pd, sampleUnit);
                        if (psu != null) {
                            pdSampleUnit = getPSUStratum(pd, psu);
                        }
                        break;
                }
                break;
            case Functions.SAMPLEUNIT_PSU:
                switch (sampleUnitType) {
                    case Functions.SAMPLEUNIT_STRATUM:
                        pdSampleUnit = null;
                        break;
                    case Functions.SAMPLEUNIT_EDSU:
                        pdSampleUnit = getEDSUPSU(pd, sampleUnit);
                        break;
                }
                break;
            case Functions.SAMPLEUNIT_EDSU:
                switch (sampleUnitType) {
                    case Functions.SAMPLEUNIT_STRATUM:
                        pdSampleUnit = null;
                    case Functions.SAMPLEUNIT_PSU:
                        pdSampleUnit = null;
                        break;
                }
                break;
        }
        if (pdSampleUnit == null || estLayer == null) {
            return null;
        }
        return AbndEstProcessDataUtil.getSUAssignment(pd, pdSampleUnit, estLayer);
    }

    public static String getSampleUnitPath(ProcessDataBO pd, String sampleUnit, String sampleUnitType) {
        String s1 = sampleUnitType + " '" + sampleUnit + "'";
        String s2 = "";
        switch (sampleUnitType) {
            case Functions.SAMPLEUNIT_EDSU: {
                String psu = AbndEstProcessDataUtil.getPSUBySampleUnit(pd, sampleUnit);
                String stratum = AbndEstProcessDataUtil.getPSUStratum(pd, psu);
                s2 = " in PSU '" + psu + "' in Stratum '" + stratum + "'";
                break;
            }
            case Functions.SAMPLEUNIT_PSU: {
                String stratum = AbndEstProcessDataUtil.getPSUStratum(pd, sampleUnit);
                s2 = " in Stratum '" + stratum + "'";
                break;
            }
        }
        return s1 + s2;
    }

}
