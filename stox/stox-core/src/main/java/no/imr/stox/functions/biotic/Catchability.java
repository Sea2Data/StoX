/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.CatchabilityParam;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class Catchability extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        //List<FishstationBO> fishStations = (List<FishstationBO>) input.get(Functions.PM_CATCHABILITY_BIOTICDATA);
        // Set the resolution matrix as Observation type and length interval
        LengthDistMatrix lengthDistMatrix = (LengthDistMatrix) input.get(Functions.PM_CATCHABILITY_LENGTHDIST);
        MatrixBO lengthDist = lengthDistMatrix.getData();
        String lengthDistType = (String) lengthDistMatrix.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE);
        if (lengthDistType == null) {
            logger.error("LengthDist parameter must have LengthDistType.", null);
            return null;
        }
        String table = (String) input.get(Functions.PM_CATCHABILITY_TABLE);
        List<CatchabilityParam> tableM = getTable(table, lengthDistMatrix.getData().getKeys());
        LengthDistMatrix result = new LengthDistMatrix();
        String catchabilityMethod = (String) input.get(Functions.PM_CATCHABILITY_CATCHABILITYMETHOD);
        Double lenInterval = lengthDistMatrix.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        String lenDistTypeIn = (String) lengthDistMatrix.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE);
        String lenDistTypeOut = lenDistTypeIn;
        result.getResolutionMatrix().setRowValue(Functions.RES_OBSERVATIONTYPE, Functions.OBSERVATIONTYPE_STATION);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lenInterval);
        switch (catchabilityMethod) {
            case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH:
                if (lengthDistType.equals(Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST)) {
                    logger.error("LengthDist-LengthDistType cannot be in percent.", null);
                }
                if (lengthDistType.startsWith(Functions.LENGTHDISTTYPE_STD_SWEEPW)) {
                    logger.error("LengthDist-LengthDistType cannot start with SweepW.", null);
                }
                // Standardize the LFQ by adjusted sweep with.
                lenDistTypeOut = Functions.LENGTHDISTTYPE_STD_SWEEPW + lenDistTypeIn;
                break;
            case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY:
                // Adjuste the LFQ (number) directly by selection curve
                break;
        }

        switch (catchabilityMethod) {
            case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH:
            case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY: {
                result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHDISTTYPE, lenDistTypeOut);
                tableM.forEach(cp -> {
                    if (cp.getAlpha() == null || cp.getBeta() == null || cp.getlMin() == null || cp.getlMax() == null) {
                        logger.error("Parameters alpha, beta, lmin, lmax must have a value.", null);
                    }

                    for (String obsKey : lengthDist.getGroupRowKeys(cp.getSpecCat())) {
                        //FishstationBO fs = BioticUtils.findStation(fishStations, obsKey);
                        MatrixBO obs = (MatrixBO) lengthDist.getGroupRowValue(cp.getSpecCat(), obsKey);
                        MatrixBO lfq = obs.getDefaultValueAsMatrix();
                        for (String lenGrp : lfq.getKeys()) {
                            Double lGroup = Conversion.safeStringtoDoubleNULL(lenGrp);
                            Double value = lfq.getValueAsDouble(lenGrp);
                            Double length = StoXMath.getLength(lGroup, lenInterval);
                            Double adjFac = 1.0;
                            Double adjValue = value;
                            switch (catchabilityMethod) {
                                case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH:
                                case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY:
                                    switch (catchabilityMethod) {
                                        case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH: {
                                            // Implement Formula according to IMR/PINRO Joint Report 2014(2)
                                            //        alpha beta lmin lmax
                                            // Cod     5.91 0.43 15 cm 62 cm
                                            // Haddock 2.08 0.75 15 cm 48 cm
                                            Double l = length < cp.getlMin() ? cp.getlMin() : length > cp.getlMax() ? cp.getlMax() : length;
                                            Double sweepWidthInM = cp.getAlpha() * Math.pow(l, cp.getBeta());
                                            Double sweepWidthNM = ImrMath.safeDivide(sweepWidthInM, 1852.0);
                                            //Double sweptArea = StoXMath.getSweptArea(fs.getDistance(), sweepWidthInM);
                                            adjFac = ImrMath.safeDivide(1d, sweepWidthNM);
                                            break;
                                        }
                                        case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY: {
                                            // Implement Formula according to IMR/PINRO Joint Report 2014(2)
                                            //        alpha beta lmin lmax
                                            // Cod     5.91 0.43 15 cm 62 cm
                                            // Haddock 2.08 0.75 15 cm 48 cm
                                            //Double l = length < cp.getlMin() ? cp.getlMin() : length > cp.getlMax() ? cp.getlMax() : length;
                                            Double l = length;
                                            if (l >= cp.getlMin() && l <= cp.getlMax()) {
                                                adjFac = cp.getAlpha() * Math.exp(cp.getBeta() * l);
                                            }
                                            break;
                                        }
                                    }
                                    adjValue = ImrMath.safeMult(value, adjFac);
                            }
                            if (adjValue != null) {
                                lfq.setValue(obsKey, adjValue);
                                //result.getData().setGroupRowCellValue(cp.getSpecCat(), obsKey, lenGrp, adjValue);
                            }
                        }
                    }
                });
            }
        }
        return result;
    }

    private List<CatchabilityParam> getTable(String table, List<String> specKeys) {
        List<CatchabilityParam> res = CatchabilityParam.fromString(table);
        if (table != null) {
            res.stream().forEach(cp -> {
                String specCat = cp.getSpecCat();
                if (specCat.equals("") && specKeys.size() == 1) {
                    // Special case where we have only one species
                    specCat = specKeys.get(0);
                } else {
                    // Wrap the spec cat to same case used by total length dist
                    Optional<String> opt = specKeys.stream().filter(s -> s.equalsIgnoreCase(cp.getSpecCat())).findFirst();
                    specCat = opt.isPresent() ? opt.get() : specCat;
                }
                cp.setSpecCat(specCat);
            });
        }
        return res;
    }
}
