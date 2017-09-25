/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.mapgui;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import no.imr.stoxmap.map.MapPanel;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.style.MutableStyle;
import no.imr.stoxmap.style.StyleUtil;
import no.imr.stoxmap.style.Styles;
import no.imr.stoxmap.utils.FeatureBO;
import no.imr.stoxmap.utils.FeatureUtil;
import no.imr.sea2data.imrbase.map.ILatLonEvent;
import no.imr.stox.functions.utils.Functions;
import no.imr.stoxmap.utils.Colors;
import no.imr.stoxmap.utils.LayerUtil;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author aasmunds
 */
public class StoXMapSetup {

    MapPanel mapPanel;
    final InstanceContent mapSelection = new InstanceContent();
    FeatureMapLayer earthGridLayer;
    FeatureMapLayer coastLayer;
    FeatureMapLayer strataLayer;
    FeatureMapLayer acousticLayer;
    FeatureMapLayer stationLayer;
    FeatureMapLayer rectangleLayer;

    List<FeatureBO> earthGrid;
    List<FeatureBO> coastFeatures;
    List<FeatureBO> strataFeatures;
    List<FeatureBO> acousticFeatures;
    List<FeatureBO> stationFeatures;
    List<FeatureBO> rectangleFeatures;
    List<ILatLonEvent> acousticAbsenceData;
    List<ILatLonEvent> bioticAbsenceData;

    public StoXMapSetup(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    public FeatureMapLayer createLayer(String name, List<FeatureBO> fs, MutableStyle style, MutableStyle selectionStyle, Boolean updateFeatures) {
        FeatureMapLayer l = LayerUtil.createLayer(name, fs, style, selectionStyle, updateFeatures);
        mapPanel.getContext().layers().add(l);
        return l;
    }

    public FeatureMapLayer createLayer(String name, List<FeatureBO> fs, MutableStyle style, MutableStyle selectionStyle) {
        return createLayer(name, fs, style, selectionStyle, true);
    }

    public void addEarthGridLayer() {
        MutableStyle style = StyleUtil.lineStyle(Colors.GRID_COLOR, 1);
        MutableStyle selectionStyle = null;

        earthGrid = FeatureUtil.getEarthGrid();
        earthGridLayer = createLayer("grid", earthGrid, style, selectionStyle, false);
    }

    public void addCoastPolygonLayer(InputStream inp) {
        MutableStyle style = StyleUtil.polygonStyle(Colors.LAND_FILL_COLOR, Colors.LAND_OUTLINE_COLOR);
        MutableStyle selectionStyle = null;
        coastFeatures = FeatureUtil.getFeatureBOWithGeometryFromWKTStream(inp);
        coastLayer = createLayer("coast", coastFeatures, style, selectionStyle, false);
    }

    public void addCoastLineLayer(InputStream inp) {
        MutableStyle style = StyleUtil.lineStyle(Colors.LAND_OUTLINE_COLOR, 1);
        MutableStyle selectionStyle = null;
        coastFeatures = FeatureUtil.getFeatureBOWithGeometryFromWKTStream(inp);
        coastLayer = createLayer("coast", coastFeatures, style, selectionStyle, false);
    }

    public void addStrataLayer(MatrixBO strata) {
        if (strataLayer != null) {
            mapPanel.getContext().layers().remove(strataLayer);
        }
        MutableStyle style = StyleUtil.polygonStyle(Styles.STRATA_FILL_COLOR, Styles.STRATA_OUTLINE_COLOR);
        MutableStyle selectionStyle = StyleUtil.polygonStyle(Styles.STRATA_SELECTED_FILL_COLOR, Styles.STRATA_SELECTED_OUTLINE_COLOR);
        strataFeatures = FeatureUtil.createFeatureBOFromGeometryMatrix(strata, Functions.COL_POLVAR_POLYGON);
        strataLayer = createLayer("strata", strataFeatures, style, selectionStyle);
    }

    public void addAcousticLayer(List<ILatLonEvent> acousticData, List<ILatLonEvent> acousticAbsenceData) {
        this.acousticAbsenceData = acousticAbsenceData;
        if (acousticData != null) {
            if (acousticLayer != null) {
                mapPanel.getContext().layers().remove(acousticLayer);
            }
            MutableStyle style = StyleUtil.acousticLayerStyle();
            acousticFeatures = FeatureUtil.createFeatureBOWithLineFromPosList(acousticData, 10d);
            acousticLayer = createLayer("acoustic", (List) acousticFeatures, style/*, selectionStyle*/, null);
        }
    }

    public void addStationLayer(List<ILatLonEvent> stations, List<ILatLonEvent> bioticAbsenceData) {
        this.bioticAbsenceData = bioticAbsenceData;
        if (stationLayer != null) {
            mapPanel.getContext().layers().remove(stationLayer);
        }
        MutableStyle style = StyleUtil.stationLayerStyle();
        /*MutableStyle selectionStyle = StyleUtil.markSymbol(Styles.FISH_STATION_SELECTED_FILL_COLOR,
         Styles.FISH_STATION_SELECTED_OUTLINE_COLOR, Styles.FISH_STATION_POINT_SIZE, null, "");*/
        stationFeatures = FeatureUtil.createFeatureBOWithPointFromPosList(stations);
        //setFeatureZeroCatch(stationFeatures);
        updateBioticFeatures(true);
        stationLayer = createLayer("stations", stationFeatures, style, null);
    }

    public void addRectangleLayer(List<String> rKeys) {
        MutableStyle style = StyleUtil.polygonStyle(Styles.STRATA_FILL_COLOR, Styles.STRATA_OUTLINE_COLOR);
        MutableStyle selectionStyle = StyleUtil.polygonStyle(Styles.STRATA_SELECTED_FILL_COLOR, Styles.STRATA_SELECTED_OUTLINE_COLOR);
        rectangleFeatures = FeatureUtil.createFeatureBOFromRectangleKeys(rKeys);
        rectangleLayer = createLayer("rectangles", rectangleFeatures, style, selectionStyle);
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public List<FeatureBO> getAcousticFeatures() {
        return (List) acousticFeatures;
    }

    public FeatureMapLayer getAcousticLayer() {
        return acousticLayer;
    }

    public List<FeatureBO> getCoastFeatures() {
        return coastFeatures;
    }

    public FeatureMapLayer getCoastLayer() {
        return coastLayer;
    }

    public List<FeatureBO> getStrataFeatures() {
        return strataFeatures;
    }

    public FeatureMapLayer getStrataLayer() {
        return strataLayer;
    }

    public List<FeatureBO> getStationFeatures() {
        return stationFeatures;
    }

    public FeatureMapLayer getStationLayer() {
        return stationLayer;
    }

    public List<FeatureBO> getRectangleFeatures() {
        return rectangleFeatures;
    }

    public FeatureMapLayer getRectangleLayer() {
        return rectangleLayer;
    }

    void clean() {
        mapSelection.set(Collections.emptySet(), null);
        mapPanel.getMap().setHandler(null);
        mapPanel.getContext().layers().remove(strataLayer);
        mapPanel.getContext().layers().remove(acousticLayer);
        mapPanel.getContext().layers().remove(stationLayer);
        mapPanel.getContext().layers().remove(rectangleLayer);
        acousticLayer = null;
        stationLayer = null;
        strataLayer = null;
        rectangleLayer = null;
        strataFeatures = null;
        acousticFeatures = null;
        acousticAbsenceData = null;
        stationFeatures = null;
        rectangleFeatures = null;
    }

    public InstanceContent getMapSelection() {
        return mapSelection;
    }

    /*private void setFeatureZeroCatch(List<FeatureBO> stationFeatures) {
        for (FeatureBO b : stationFeatures) {
            FishstationBO fs = (FishstationBO) b.getUserData();
            if (fs.getCatchBOCollection().isEmpty()) {
                b.setSelection(1);
            }
        }
    }*/
    public void updateAcousticFeatures(boolean absence) {
        if (acousticAbsenceData == null) {
            return;
        }
        acousticFeatures.stream().forEach(f -> {
            Integer pl = absence ? (acousticAbsenceData.contains((ILatLonEvent) f.getUserData()) ? 0 : 1) : 1;
            f.setPresencelevel(pl);
        });
    }

    public void updateBioticFeatures(boolean absence) {
        if (bioticAbsenceData == null) {
            return;
        }
        stationFeatures.stream().forEach(f -> {
            Integer pl = absence ? (bioticAbsenceData.contains((ILatLonEvent) f.getUserData()) ? 0 : 1) : 1;
            f.setPresencelevel(pl);
        });
    }
}
