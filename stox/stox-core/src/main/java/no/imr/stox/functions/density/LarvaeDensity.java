/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.density;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 * http://brage.bibsys.no/xmlui/bitstream/handle/11250/107824/Investigations%20on%20capelin.pdf?sequence=1
 * N = number of larvae per square meter surface n = total number of larvae
 * caught I = depth interval (0-60 meters = 60) a = area of front opening of
 * Gulf III sampler in square meters (PIr2 = P*0.095 = 0.0283529) d'=
 * calibration constant = 0.316 (see next section) R = number of flowmeter
 * revolutions Example: Station 59, date 1.6.1983:n = 44, I = 60, R = 1163 N = n
 * * I / (a * d' * R)
 *
 *
 * @author aasmunds
 */
public class LarvaeDensity extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_LARVAEDENSITY_PROCESSDATA);
        List<FishstationBO> biotic = (List<FishstationBO>) input.get(Functions.PM_LARVAEDENSITY_BIOTICDATA);
        LengthDistMatrix lengthDist = (LengthDistMatrix) input.get(Functions.PM_LARVAEDENSITY_LENGTHDIST);
        if (lengthDist == null) {
            return null;
        }
        String lenDistType = (String) lengthDist.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE);
        if (lenDistType == null || !lenDistType.equals(Functions.LENGTHDISTTYPE_LENGHTDIST)) {
            logger.error("Length distribution type must be 'LengthDistr'", null);
        }
        // 2133:0.02558,2113:0.5
        String gearOpeningArea = (String) input.get(Functions.PM_LARVAEDENSITY_GEAROPENINGAREA);
        if (gearOpeningArea == null) {
            return null;
        }

        Map<String, Double> gearOpeningAreaMap = Arrays.asList(gearOpeningArea.split(",")).stream().map(s -> s.split(":")).
                filter(s -> s.length == 2).collect(Collectors.toMap(s -> s[0], s -> Conversion.safeStringtoDouble(s[1])));

        if (gearOpeningAreaMap.isEmpty()) {
            return null;
        }

        String sampleUnitType = (String) AbndEstProcessDataUtil.getAssignmentResolutions(pd).getRowValue(Functions.RES_SAMPLEUNITTYPE);//(String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);//input.get(Functions.PM_DENSITY_SAMPLEUNITTYPE);
        //String layerType = (String) AbndEstProcessDataUtil.getAssignmentResolutions(pd).getRowValue(Functions.RES_LAYERTYPE);
        if (sampleUnitType == null) {
            return null;
        }
        DensityMatrix result = new DensityMatrix();

        // Inherit resolution set in definePSUAndAssignmentsByBioticData:
        //result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, layerType);
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, sampleUnitType);

        Double lengthInterval = lengthDist.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lengthInterval);
        // Samplesize 
        MatrixBO lDist = lengthDist.getData();
        // For each sample unit in NASC.
        MatrixBO suAssignments = AbndEstProcessDataUtil.getSUAssignments(pd);
        MatrixBO psuStrata = AbndEstProcessDataUtil.getPSUStrata(pd);

        for (String psu : psuStrata.getRowKeys()) {
            List<String> estLayers = suAssignments.getRowColKeys(psu);
            if (estLayers.size() != 1) {
                continue; // Assignments and layers not properly defined
            }
            String estLayer = estLayers.get(0); // Get single layer for swept area. Multilayer not supported.
            Collection<String> edsus = AbndEstProcessDataUtil.getEDSUsByPSU(pd, psu);
            int sampleSize = edsus.size();
            if (sampleSize == 0) {
                continue;
            }
            result.getSampleSizeMatrix().setRowValue(psu, sampleSize);
            // Assume that all psus are weighted equally in mean
            result.getDistanceMatrix().setRowValue(psu, 1.0);
            // Get assignment for the psu
            String assignment = AbndEstProcessDataUtil.getSUAssignment(pd, psu, estLayer);
            if (assignment == null) {
                continue;
            }
            // Iterate biotic stations in the assignment
            // set observation = assignemnt
            String observation = assignment;
            Collection<String> stations = AbndEstProcessDataUtil.getStationsByAssignment(pd, assignment);
            if (stations.size() != 1) {
                logger.error("PSU " + psu + " must be connected to one station", null);
            }
            boolean sampleHasValue = false;
            for (String station : stations) {
                FishstationBO fs = no.imr.stox.functions.utils.BioticUtils.findStation(biotic, station);
                Integer flowCount = null;
                Double flowConst = null;
                Double sweptVolume = getSweptVolume(flowCount, flowConst, gearOpeningAreaMap.get(fs.getGear()), fs.getFishingDepthMax());
                //ant * fiskedyp[m] / volum[m^3] ; ant / 0.5
                //volum = 0.3 * flowcount * 0.02835
                //0.5 er åpning ved håv
                // gjør om fra n/m^2 til n/nmi^2 ved å gange med 1852d * 1852
                Double fac = ImrMath.safeMult(ImrMath.safeDivide(fs.getFishingDepthMax(), sweptVolume), 1852d * 1852d);
                if (fac == null) {
                    continue;
                }
                for (String specCat : lDist.getKeys()) {
                    MatrixBO row = (MatrixBO) lDist.getGroupRowValue(specCat, observation);
                    if (row == null) {
                        // No LFQ collected for this station (edsu) and species.
                        continue;
                    }
                    MatrixBO cell = row.getDefaultValueAsMatrix();
                    for (String lenGrp : cell.getKeys()) {
                        Double n = cell.getValueAsDouble(lenGrp);
                        if (n == null) {
                            continue;
                        }

                        Double density = ImrMath.safeMult(n, fac);
                        // psu = observation of station (samplesize=1)
                        sampleHasValue = sampleHasValue || density != null && density > 0d;
                        result.getData().setGroupRowColCellValue(specCat, psu, estLayer, lenGrp, density);
                    }
                }
            }
            result.getPosSampleSizeMatrix().setRowValue(psu, sampleHasValue ? sampleSize : 0);
        }
        return result;
    }

    /**
     * Return the swept volume representing the water flowing through the gear
     * options will come later.
     *
     * @param bioticData
     * @param station fish station key
     * @return
     */
    private Double getSweptVolume(Integer flowCount, Double flowConst, Double gearAreaOpening, Double deltaFishDepth) {
        Double flowLength = flowConst == null || flowCount == null || flowCount == 0 || flowConst == 0 ? deltaFishDepth : flowConst * flowCount;
        return ImrMath.safeMult(gearAreaOpening, flowLength);
    }

}
