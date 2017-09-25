/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.providers;

import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import org.openide.util.Lookup;

/**
 * Distance PSU Click handler - a common class for DistanceTable and Map when a
 * distance is clicked in 'Define Distance' mode. Keeps track of selected psu,
 * ordered distance keys and last clicked distance. Note that the distance keys
 * are ordered as in the distances list.
 *
 * @author aasmunds
 */
public class DistancePSUHandler {

    private String selectedPSU = "";
    private final List<String> distanceKeys;
    private int lastClickedDistance = -1;
    ProcessDataProvider pdp;

    public DistancePSUHandler(ProcessDataProvider pdp) {
        this.pdp = pdp;
        distanceKeys = new ArrayList<>();
    }

    public void createDistances() {
        distanceKeys.clear();
        for (DistanceBO distBO : Lookup.getDefault().lookup(ProcessDataProvider.class).getDistances()) {
            distanceKeys.add(distBO.getKey());
        }
    }

    public List<String> getDistanceKeys() {
        return distanceKeys;
    }

    public List<String> getTaggedDistanceKeys(Boolean sorted) {
        MatrixBO m = AbndEstProcessDataUtil.getEDSUPSUs(getPD());
        return sorted ? m.getSortedRowKeys() : m.getRowKeys();
    } // getTaggedDistanceKeys

    public String getSelectedPSU() {
        return selectedPSU;
    }

    public void setSelectedPSU(String selectedPSU) {
        this.selectedPSU = selectedPSU;
    }

    private ProcessDataBO getPD() {
        return Lookup.getDefault().lookup(ProcessDataProvider.class).getPd();
    }

    public List<String> getSelectedDistances() {
        return getSelectedDistances(false);
    }

    public List<String> getSelectedDistances(Boolean sorted) {
        List<String> dList = new ArrayList<>();
        MatrixBO distPSU = AbndEstProcessDataUtil.getEDSUPSUs(getPD());
        for (String key : getTaggedDistanceKeys(sorted)) {
            if (distPSU.getRowValue(key).equals(selectedPSU)) {
                dList.add(key);
            }
        }
        return dList;
    }

    public void handleDistanceClick(String distKey, Boolean multiSelect) {
        handleDistanceClick(getDistanceIdxByKey(distKey), multiSelect);
    }

    public void handleDistanceClick(int clickedDistance, Boolean multiSelect) {
         if (clickedDistance < 0) {
            return;
        }
        if (selectedPSU == null || selectedPSU.isEmpty()) {
            return;
        }
        List<String> changed = new ArrayList<>();
        if (multiSelect && lastClickedDistance >= 0) {
            Boolean on = isSelected(lastClickedDistance);
            for (int i = Math.min(clickedDistance, lastClickedDistance); i <= Math.max(clickedDistance, lastClickedDistance); i++) {
                tagDistanceI(i, on);
                changed.add(getDistanceByRow(i));
            }
        } else {
            tagDistanceI(clickedDistance);
            changed.add(getDistanceByRow(clickedDistance));
        }
        lastClickedDistance = clickedDistance;
        Boolean on = isSelected(lastClickedDistance);
        Lookup.getDefault().lookup(ProcessDataProvider.class).fireDistanceTagsChanged(changed, on);

    }

    String getDistanceByRow(int r) {
        return distanceKeys.get(r);
    }

    public String getPSUByRow(int row) {
        return (String) AbndEstProcessDataUtil.getEDSUPSUs(getPD()).getRowValue(getDistanceByRow(row));
    }

    Boolean isDistanceSelected(String distance) {
        if (selectedPSU == null || selectedPSU.isEmpty()) {
            return false;
        }
        return selectedPSU.equals(AbndEstProcessDataUtil.getEDSUPSUs(getPD()).getRowValue(distance));
    }

    void tagDistance(String distance, Boolean on) {
        if (on) {
            AbndEstProcessDataUtil.getEDSUPSUs(getPD()).setRowValue(distance, selectedPSU);
        } else {
            AbndEstProcessDataUtil.getEDSUPSUs(getPD()).removeRowKey(distance);
        }
    }

    void tagDistanceI(int distance, Boolean on) {
        tagDistance(getDistanceByRow(distance), on);
    }

    void tagDistanceI(int distance) {
        tagDistanceI(distance, !isSelected(distance));
    }

    public Boolean isSelected(int row) {
        return isDistanceSelected(getDistanceByRow(row));
    }

    public int getDistanceIdxByKey(String distKey) {
        return distanceKeys.indexOf(distKey);
    }

    public int getLastClickedDistance() {
        return lastClickedDistance;
    }

    public void setLastClickedDistance(int lastClickedDistance) {
        this.lastClickedDistance = lastClickedDistance;
    }

}
