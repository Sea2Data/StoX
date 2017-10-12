package no.imr.stox.functions.utils;

import no.imr.sea2data.imrbase.math.ImrMath;
import static no.imr.sea2data.imrbase.math.ImrMath.safeDivide;
import static no.imr.sea2data.imrbase.math.ImrMath.safeMinus;
import static no.imr.sea2data.imrbase.math.ImrMath.safeMult;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.matrix.MatrixBO;

/**
 * Contains safe math operations. The idea is that a sequence of operations can
 * handle nulls so that the calcluation are not interrupted. Check of nulls is
 * handle outside the operations.
 *
 * @author Åsmund
 */
public final class StoXMath {

    public static Double relativeToTotal(Double value, Double total) {
        return ImrMath.safeDivide(value, total);
    }

    /**
     * Return raise factor
     *
     * @param value
     * @param raiseTo
     * @return
     */
    public static Double raiseFac(Double value, Double raiseTo) {
        return ImrMath.safeDivide(value, raiseTo);
    }

    public static Double combineWFac(Double wFac1, Double wFac2) {
        return ImrMath.safeMult(wFac1, wFac2);
    }

    /**
     * convert m to cm
     *
     * @param v
     * @return
     */
    public static Double mToCM(Double v) {
        return ImrMath.safeMult(v, 100d);
    }

    /**
     * Percent is the relative proportion of 100
     *
     * @param value
     * @param tot
     * @return
     */
    public static Double inPercent(Double value, Double tot) {
        return proportion(100d, relativeToTotal(value, tot));
    }

    /**
     * append a number to total
     *
     * @param value
     * @param tot
     * @return
     */
    public static Double append(Double value, Double tot) {
        return ImrMath.safePlus(value, tot);
    }

    /**
     * Depth is calculated from channel and thickness. Channel 1 starting at 0
     * meter Example ch=1, th=10, depth = 1 * 10 - 10*0.5
     *
     * @param upper
     * @param thickness
     * @param channel
     * @return upper + thickness * (channel - 0.5);
     */
    public static Double depthFromChannel(Double thickness, Integer channel) {
        Double res = ImrMath.safeMinus(ImrMath.safeIntegerToDouble(channel), 0.5d);
        res = ImrMath.safeMult(res, thickness);
        return res;
    }

    public static Double getLength(Double lGroup, Double groupInterval) {
        return ImrMath.safePlus(lGroup, ImrMath.safeMult(groupInterval, 0.5d));
    }

    /**
     * Acoustic TS formula (a * Math.log10(length * 100)) - (c * Math.log10(1 +
     * depth / 10)) + b
     *
     * @param length
     * @param depth
     * @param m
     * @param a
     * @param d
     * @return
     */
    public static Double TS(Double length, Double depth, Double m, Double a, Double d) {
        Double mm = 0d;
        Double aa = 0d;
        Double dd = 0d;
        if (m != null && length != null) {
            mm = m * Math.log10(length);
        }
        if (a != null) {
            aa = a;
        }
        if (d != null && depth != null) {
            dd = d * Math.log10(1 + depth / 10);
        }
        return mm + aa + dd;
    }

    /**
     * Acoustic sigma formula
     *
     * @return
     */
    public static Double sigma(Double length, Double depth, Double m, Double a, Double d) {
        return sigma(TS(length, depth, m, a, d));
    }

    /**
     * Acoustic sigma formula
     *
     * @param ts
     * @return
     */
    public static Double sigma(Double ts) {
        if (ts == null) {
            return null;
        }
        return 4 * Math.PI * Math.pow(10, ts / 10d);
    }

    public static Double proportion(Double value, Double rate) {
        return ImrMath.safeMult(value, rate);
    }

    /**
     * Returns the density N per sq.m from NASC proprtion and sigma for a given
     * NASC at a given species, channel, distance and length group
     *
     * @param saProportion
     * @param sigma
     * @return density
     */
    public static Double densityNPerSqNM(Double saProportion, Double sigma) {
        return ImrMath.safeDivide(saProportion, sigma);
    }

    public static Double abundance(Double density, Double area) {
        return ImrMath.safeMult(density, area);
    }

    public static Double saFromDensity(Double density) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Double kgToGrams(Double v) {
        return ImrMath.safeMult(v, 1000d);
    }

    public static Double gramsToTonnes(Double v) {
        return ImrMath.safeMult(v, 0.000001d);
    }

    public static Double gramsToKg(Double v) {
        return ImrMath.safeMult(v, 0.001d);
    }

    /**
     *
     * @param nasc the NASC value to distribute over lengths
     * @param lDist 100 % distributed over length intervals
     * @param lengthInterval the length interval to calculate length from
     * (len=lGrp+0.5lenInt)
     * @param depth the depth in TS
     * @param m a in TS
     * @param a b in TS
     * @param d c in TS
     * @return density distribution, the number per sq.nm per length group
     */
    public static MatrixBO getDensityDistribution(Double nasc, MatrixBO lDist, Double lengthInterval, Double depth, Double m, Double a, Double d) {
        MatrixBO sigmas = new MatrixBO();
        MatrixBO weightedsigmas = new MatrixBO();
        MatrixBO result = new MatrixBO();
        // CELL: For each length group, calculate sigma, weighted sigma
        if (lDist != null) {
            for (String lenGrp : lDist.getKeys()) {
                Double lGroup = Conversion.safeStringtoDoubleNULL(lenGrp);
                Double length = StoXMath.getLength(lGroup, lengthInterval);
                Double n = lDist.getValueAsDouble(lenGrp);
                Double sigma = StoXMath.sigma(length, depth, m, a, d);
                sigmas.setValue(lenGrp, sigma);
                // One density element = weightedCount * sigma
                Double weightedsigma = StoXMath.combineWFac(n, sigma);
                weightedsigmas.setValue(lenGrp, weightedsigma);
            }
        }
        // CELL: For each length group, use calculate sigma, weighted sigma and sum of weighted sigma to calculate NASC proportion and density (number per sq.m)
        Double sumweightedsigmas = weightedsigmas.getSum();
        for (String lenGrp : sigmas.getKeys()) {
            Double sigma = sigmas.getValueAsDouble(lenGrp);
            Double weightedSigma = weightedsigmas.getValueAsDouble(lenGrp);
            Double relWeightedSigma = StoXMath.relativeToTotal(weightedSigma, sumweightedsigmas);
            Double nascProportion = StoXMath.proportion(nasc, relWeightedSigma);
            Double density = StoXMath.densityNPerSqNM(nascProportion, sigma);
            if (density == null) {
                continue;
            }
            result.setRowValue(lenGrp, density);
        }
        return result;
    }

    /**
     * Calculate the proportions of SA if the length dist contains a species
     * dimension. can bes used to split nasc between species
     *
     * @param nasc
     * @param lDist
     * @param lengthInterval
     * @param depth
     * @param specTS table with m a and d parameters
     * @return
     */
    public static MatrixBO getNASCProportions(Double nasc, MatrixBO lDist, Double lengthInterval, Double depth, MatrixBO specTS) {
        MatrixBO weightedsigmas = new MatrixBO();
        MatrixBO result = new MatrixBO();
        // CELL: For each length group, calculate sigma, weighted sigma
        if (lDist == null) {
            return null;
        }
        for (String acoCat : specTS.getRowKeys()) {
            for (String lenGrp : lDist.getRowColKeys(acoCat)) {
                Double lGroup = Conversion.safeStringtoDoubleNULL(lenGrp);
                Double length = StoXMath.getLength(lGroup, lengthInterval);
                Double m = specTS.getRowColValueAsDouble(acoCat, "m");
                Double a = specTS.getRowColValueAsDouble(acoCat, "a");
                Double d = specTS.getRowColValueAsDouble(acoCat, "d");
                Double sigma = StoXMath.sigma(length, depth, m, a, d);
                // One density element = weightedCount * sigma
                Double n = lDist.getRowColValueAsDouble(acoCat, lenGrp);
                if (n == null) {
                    continue;
                }
                Double weightedsigma = StoXMath.combineWFac(n, sigma);
                weightedsigmas.setRowColValue(acoCat, lenGrp, weightedsigma);
            }
        }
        // CELL: For each length group, use calculate sigma, weighted sigma and sum of weighted sigma to calculate NASC proportion and density (number per sq.m)
        Double sumweightedsigmas = weightedsigmas.getSum();
        for (String acoCat : specTS.getRowKeys()) {
            for (String lenGrp : weightedsigmas.getRowColKeys(acoCat)) {
                Double weightedSigma = weightedsigmas.getRowColValueAsDouble(acoCat, lenGrp);
                Double relWeightedSigma = StoXMath.relativeToTotal(weightedSigma, sumweightedsigmas);
                Double saProportion = StoXMath.proportion(nasc, relWeightedSigma);
                result.setRowColValue(acoCat, lenGrp, saProportion);
            }
        }
        return result;
    }

    /**
     *
     * @param lenGrp
     * @param lenInterval
     * @return adjusted lengthgroup from interval.
     */
    public static String adjustLengthGroupFromInterval(String lenGrp, Double lenInterval) {
        Double oldLenGrp = Conversion.safeStringtoDoubleNULL(lenGrp);
        return BioticUtils.getLenGrp(oldLenGrp, lenInterval);
    }

    /**
     * check if a double is an integer
     *
     * @param d
     * @return
     */
    public static boolean isInteger(double d) {
        d = Math.abs(d);
        return d - (int) (d + ImrMath.RND_ERR) < ImrMath.RND_ERR;
    }

    /**
     * Return the swept area from a station using the distance. Different
     * options will come later.
     *
     * @param sweptDistance
     * @param sweepWidthInM the sweep width as parameter
     * @return
     */
    public static Double getSweptArea(Double sweptDistance, Double sweepWidthInM) {
        Double sweepWidthNM = ImrMath.safeDivide(sweepWidthInM, 1852.0);
        return ImrMath.safeMult(sweptDistance, sweepWidthNM);
    }

    public static Double safeSumRelativeDiffSquared(Double diff, Double ref) {
        Double rel = safeDivide(diff, ref);
        Double sq = safeMult(rel, rel);
        return sq;
    }

    public static Double safeSumRelativeSquared(Double val1, Double val2, Double ref) {
        return safeSumRelativeDiffSquared(safeMinus(val1, val2), ref);
    }

    public static Double safeAverage(Double val1, Double val2) {
        if(val1 == null) {
            return val2;
        }
        if(val2 == null) {
            return val1;
        }
        return ImrMath.safeMult(0.5, ImrMath.safePlus(val1, val2));
    }
}
