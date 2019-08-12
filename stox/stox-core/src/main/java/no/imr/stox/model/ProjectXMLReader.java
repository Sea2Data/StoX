/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.model;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.stox.util.exceptions.XMLReaderException;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.util.base.XMLReader;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;

/**
 *
 * @author aasmunds
 */
public class ProjectXMLReader extends XMLReader {

    IProject project;
    IModel currentModel;
    String currentParameterName;
    String currentTable;
    WKTReader wktReader = new WKTReader();

    public ProjectXMLReader(IProject project) {
        this.project = project;
    }

    @Override
    protected void onObjectValue(final Object object, final String key, final String value) {
        if (object instanceof IProcess) {
            IProcess p = (IProcess) object;
            switch (key) {
                case "function":
                    p.setMetaFunction(p.getModel().getLibrary().findMetaFunction(value));
                    break;
                case "respondingui":
                    p.setRespondInGUI(value.isEmpty() ? null : Boolean.valueOf(value));
                    break;
                case "breakingui":
                    p.setBreakInGUI(value.isEmpty() ? null : Boolean.valueOf(value));
                    break;
                case "enabled":
                    p.setEnabled(value.isEmpty() ? null : Boolean.valueOf(value));
                    break;
                case "fileoutput":
                    p.setFileOutput(value.isEmpty() ? null : Boolean.valueOf(value));
                    break;
                case "parameter":
                    // currentParameterName stored in getObject.
                    // Connect name -> Value here
                    if (!value.isEmpty()) {
                        p.setParameterValue(currentParameterName, value);
                    }
                    break;

            }
        }
    }

    @Override
    protected Object getObject(final Object current, final String elmName) {
        Object result = null;
        if (current == null && elmName.equals("project")) {
            String ver = getCurrentAttributeValue("version");
            if (ver == null) {
                ver = getCurrentAttributeValue("resourceversion");
            }
            Double resver = Conversion.safeStringtoDouble(ver);
            project.setResourceVersion(resver);
            return project;
        } else if (current instanceof IProject) {
            switch (elmName) {
                case "model":
                    String name = getCurrentAttributeValue("name");
                    // translate old names to new:
                    switch (name) {
                        case "rmodel":
                            name = ProjectUtils.R;
                            break;
                        case "rmodel-report":
                            name = ProjectUtils.R_REPORT;
                            break;
                    }
                    IModel m = project.getModel(name);
                    m.getProcessList().clear();
                    return m;
                case ProjectUtils.PROCESSDATA:
                    return project.getProcessData();
            } // switch
        } else if (current instanceof ProcessDataBO) {
            // Check tag against process data table
            currentTable = elmName;
            MatrixBO m = project.getProcessData().getMatrix(elmName);
            m.clear();
            return m;
        } else if (current instanceof MatrixBO) {
            return null; // Handle in onObjectelement
        } else if (current instanceof IModel && elmName.equals("process")) {
            String name = getCurrentAttributeValue("name");
            IModel m = ((IModel) current);
            IProcess pr = new Process(m);
            pr.setProcessName(name);
            m.getProcessList().add(pr);
            return pr;
        } else if (current instanceof IProcess && elmName.equals("parameter")) {
            currentParameterName = getCurrentAttributeValue("name");
            return null; // Handle parameter value in onObjectValue and connect to currentParameterName
        } 
        return result;
    }

    /**
     * Overrided to handle attributes and element values
     *
     * @param current
     * @param elmName
     * @return
     */
    @Override
    protected boolean onObjectElement(Object current, String elmName) {
        if (current != null && current instanceof MatrixBO) {
            try {
                switch (currentTable) {
                    case AbndEstProcessDataUtil.TABLE_BIOTICASSIGNMENT:
                        if (elmName.equalsIgnoreCase("stationweight")) {
                            String assignmentId = getCurrentAttributeValue("assignmentid");
                            String station = getCurrentAttributeValue("station");
                            Double stationweight = Conversion.safeStringtoDouble(getCurrentElementValue());
                            AbndEstProcessDataUtil.assignStation(project.getProcessData(), assignmentId, station, Boolean.TRUE, stationweight);
                        }
                        break;
                    case AbndEstProcessDataUtil.TABLE_SUASSIGNMENT:
                        if (elmName.equalsIgnoreCase("assignmentid")) {
                            String sampleUnit = getCurrentAttributeValue("sampleunit");
                            String estLayer = getCurrentAttributeValue("layer");
                            if (estLayer == null) {
                                estLayer = getCurrentAttributeValue("estlayer");
                            }
                            if (estLayer == null) {
                                break;
                            }
                            if (estLayer.equals(Functions.WATERCOLUMN_PELBOT)) {
                                estLayer = "1"; // old way was to assign to acoustic layers. now only est. layers can be assigned to. suggest 1 here in read xml.
                            }
                            String assignmentId = getCurrentElementValue();
                            AbndEstProcessDataUtil.setSUAssignment(project.getProcessData(), sampleUnit, estLayer, assignmentId);
                        }
                        break;
                    case AbndEstProcessDataUtil.TABLE_ASSIGNMENTRESOLUTION:
                        if (elmName.equalsIgnoreCase("value")) {
                            String variable = getCurrentAttributeValue("variable");
                            String value = getCurrentElementValue();
                            if (value.equals("LayerType")) {
                                // Do not set LayerType in ass. old stuff. now only est.layer in assignment
                                break;
                            }
                            AbndEstProcessDataUtil.getAssignmentResolutions(project.getProcessData()).setRowValue(variable, value);
                        }
                        break;
                    case AbndEstProcessDataUtil.TABLE_EDSUPSU:
                        if (elmName.equalsIgnoreCase("psu")) {
                            String edsu = getCurrentAttributeValue("edsu");
                            String psu = getCurrentElementValue();
                            AbndEstProcessDataUtil.setEDSUPSU(project.getProcessData(), edsu, psu);
                        }
                        break;
                    case AbndEstProcessDataUtil.TABLE_PSUSTRATUM:
                        if (elmName.equalsIgnoreCase("stratum")) {
                            String psu = getCurrentAttributeValue("psu");
                            String stratum = stratumName(getCurrentElementValue());
                            AbndEstProcessDataUtil.setPSUStratum(project.getProcessData(), psu, stratum);
                        }
                        break;
                    case AbndEstProcessDataUtil.TABLE_TEMPORAL:
                    case AbndEstProcessDataUtil.TABLE_GEARFACTOR:
                    case AbndEstProcessDataUtil.TABLE_SPATIAL:
                    case AbndEstProcessDataUtil.TABLE_PLATFORM:
                        if (elmName.equalsIgnoreCase("value")) {
                            String sourceType = getCurrentAttributeValue("covariatesourcetype");
                            String covariate = getCurrentAttributeValue("covariate");
                            String valueS = getCurrentElementValue();
                            project.getProcessData().getMatrix(currentTable).setRowColValue(sourceType, covariate, valueS);
                        }
                        break;
                    case AbndEstProcessDataUtil.TABLE_STRATUMNEIGHBOUR:
                        if (elmName.equalsIgnoreCase("value")) {
                            String var = stratumName(getCurrentAttributeValue("variable"));
                            String valueS = getCurrentElementValue();
                            project.getProcessData().getMatrix(currentTable).setRowValue(var, valueS);
                        }
                        break;
                    case AbndEstProcessDataUtil.TABLE_COVPARAM:
                        if (elmName.equalsIgnoreCase("value")) {
                            String covariatetable = getCurrentAttributeValue("covariatetable");
                            String parameter = stratumName(getCurrentAttributeValue("parameter"));
                            String valueS = getCurrentElementValue();
                            project.getProcessData().getMatrix(currentTable).setRowColValue(covariatetable, parameter, valueS);
                        }
                        break;
                    case AbndEstProcessDataUtil.TABLE_AGEERROR:
                        if (elmName.equalsIgnoreCase("probability")) {
                            String readAge = getCurrentAttributeValue("readage");
                            String realAge = getCurrentAttributeValue("realage");
                            String valueS = getCurrentElementValue();
                            project.getProcessData().getMatrix(currentTable).setRowColValue(readAge, realAge, valueS);
                        }
                        break;
                    case AbndEstProcessDataUtil.TABLE_STRATUMPOLYGON:
                        if (elmName.equalsIgnoreCase("value")) {
                            String polygonKey = stratumName(getCurrentAttributeValue("polygonkey"));
                            String polygonVariable = getCurrentAttributeValue("polygonvariable");
                            String valueS = getCurrentElementValue();
                            switch (polygonVariable) {
                                case "polygon": {
                                    String wkt = valueS;
                                    wkt = wkt.replace('−', '-'); // wkt writer creates wrong minus sign on java 11?
                                    MultiPolygon mp = null;
                                    if (wkt.startsWith("POLYGON")) {
                                        Polygon p = (Polygon) wktReader.read(wkt);
                                        mp = JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(p.getCoordinates())));
                                    } else {
                                        mp = (MultiPolygon) wktReader.read(wkt);
                                    }
                                    mp.setSRID(4326);
                                    AbndEstProcessDataUtil.setStratumPolygon(project.getProcessData(), polygonKey, mp);
                                    break;
                                }
                                case "includeintotal": {
                                    Boolean includeInTotal = valueS.isEmpty() ? null : Boolean.valueOf(valueS);
                                    AbndEstProcessDataUtil.getStratumPolygons(project.getProcessData()).setRowColValue(polygonKey, Functions.COL_POLVAR_INCLUDEINTOTAL,
                                            includeInTotal != null ? includeInTotal.toString().toLowerCase() : null);
                                    break;
                                }
                            }
                        }
                        break;
                }
                return true; // Handle in onObjectelement
            } catch (XMLReaderException ex) {
                Logger.getLogger(ProjectXMLReader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(ProjectXMLReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.onObjectElement(current, elmName); //To change body of generated methods, choose Tools | Templates.
    }

    String stratumName(String stratum) {
        return stratum.replace("/", "_");
    }
}
