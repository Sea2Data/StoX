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
        MatrixBO tableM = getTable(table, lengthDistMatrix.getData().getKeys());
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

        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHDISTTYPE, lenDistTypeOut);
        for (String specCat : lengthDist.getKeys()) {
            MatrixBO m = null;
            Double alpha = null;
            Double beta = null;
            Double lmin = null;
            Double lmax = null;
            switch (catchabilityMethod) {
                case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH:
                case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY: {
                    m = tableM.getRowValueAsMatrix(specCat);
                    if (m != null) {
                        alpha = m.getValueAsDouble("alpha");
                        beta = m.getValueAsDouble("beta");
                        lmin = m.getValueAsDouble("lmin");
                        lmax = m.getValueAsDouble("lmax");
                        if (alpha == null || beta == null || lmin == null || lmax == null) {
                            logger.error("Parameters alpha, beta, lmin, lmax must have a value.", null);
                        }
                    }
                }
            }
            for (String obsKey : lengthDist.getGroupRowKeys(specCat)) {
                //FishstationBO fs = BioticUtils.findStation(fishStations, obsKey);
                MatrixBO obs = (MatrixBO) lengthDist.getGroupRowValue(specCat, obsKey);
                MatrixBO lfq = obs.getDefaultValueAsMatrix();
                for (String lenGrp : lfq.getKeys()) {
                    Double lGroup = Conversion.safeStringtoDoubleNULL(lenGrp);
                    Double value = lfq.getValueAsDouble(lenGrp);
                    Double length = StoXMath.getLength(lGroup, lenInterval);
                    Double adjFac = null;
                    Double adjValue = value;
                    switch (catchabilityMethod) {
                        case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH:
                        case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY:
                            if (m != null) {
                                Double l = length < lmin ? lmin : length > lmax ? lmax : length;
                                switch (catchabilityMethod) {
                                    case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH:
                                        // Implement Formula according to IMR/PINRO Joint Report 2014(2)
                                        //        alpha beta lmin lmax
                                        // Cod     5.91 0.43 15 cm 62 cm
                                        // Haddock 2.08 0.75 15 cm 48 cm
                                        Double sweepWidthInM = alpha * Math.pow(l, beta);
                                        Double sweepWidthNM = ImrMath.safeDivide(sweepWidthInM, 1852.0);
                                        //Double sweptArea = StoXMath.getSweptArea(fs.getDistance(), sweepWidthInM);
                                        adjFac = ImrMath.safeDivide(1d, sweepWidthNM);
                                        break;
                                    case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY:
                                        // Implement Formula according to IMR/PINRO Joint Report 2014(2)
                                        //        alpha beta lmin lmax
                                        // Cod     5.91 0.43 15 cm 62 cm
                                        // Haddock 2.08 0.75 15 cm 48 cm
                                        adjFac = alpha * Math.exp(beta * l);
                                }
                            }
                            adjValue = ImrMath.safeMult(value, adjFac);
                    }
                    if (adjValue != null) {
                        result.getData().addGroupRowCellValue(specCat, obsKey, lenGrp, adjValue);
                    }
                }
            }
        }
        return result;
    }

    private MatrixBO getTable(String table, List<String> specKeys) {

        MatrixBO res = new MatrixBO();
        if (table != null) {
            String lines[] = table.split("/");
            for (int row = 0; row < lines.length; row++) {
                String line = lines[row];
                if (line.isEmpty()) {
                    continue;
                }
                String cells[] = line.split(";");
                String specCat = cells[0];
                if (specCat.equals("-") && specKeys.size() == 1 && lines.length == 1) {
                    // Special case where we have only one species
                    specCat = specKeys.get(0);
                } else {
                    // Wrap the spec cat to same case used by total length dist
                    Optional<String> opt = specKeys.stream().filter(s -> s.equalsIgnoreCase(cells[0])).findFirst();
                    specCat = opt.isPresent() ? opt.get() : specCat;
                }
                Double alpha = Conversion.safeStringtoDoubleNULL(cells[1]);
                Double beta = Conversion.safeStringtoDoubleNULL(cells[2]);
                Double lmin = Conversion.safeStringtoDoubleNULL(cells[3]);
                Double lmax = Conversion.safeStringtoDoubleNULL(cells[4]);
                res.setRowColValue(specCat, "SpecCat", specCat);
                res.setRowColValue(specCat, "alpha", alpha);
                res.setRowColValue(specCat, "beta", beta);
                res.setRowColValue(specCat, "lmin", lmin);
                res.setRowColValue(specCat, "lmax", lmax);
            }
        }
        return res;
    }
}
