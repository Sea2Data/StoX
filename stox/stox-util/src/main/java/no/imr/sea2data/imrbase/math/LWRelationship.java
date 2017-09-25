package no.imr.sea2data.imrbase.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the allometric expression W=A*L^B and estimation of A and B
 * and goodness of fit R^2 using MLE and bias correction
 * http://www.pifsc.noaa.gov/library/pubs/admin/PIFSC_Admin_Rep_12-03.pdf
 *
 * @author Åsmund
 */
// Equations
// W = A*L^B
// Ek = log(Wk) - (b0 + b1*log(Lk)) 
// Elog(W) = 1/n*Sum(log(Wk))
// Elog(L) = 1/n*Sum(log(Lk))
// b1 = Sum((log(Lk)-Elog(L))*(log(Wk)-Elog(W))/Sum((log(Lk)-Elog(L))^2)
// b0 = Elog(W) - b1*ElogL
// B = b1
// sigma^2 = 1 / (n-2) * sum(Ek^2) 
// A = exp(b0) * exp(sigma^2 / 2)
// R^2 = 1 - Sum(Ek^2) / Sum((log(Wk) - Elog(W))^2)
public class LWRelationship {

    private final List<Double> obsL;
    private final List<Double> obsW;
    private Double a = null;
    private Double b = null;
    private Double r2 = null;

    public static LWRelationship getLWRelationship(Double[] lenInCM, Double[] wInGrams) {
        if (lenInCM.length != wInGrams.length) {
            return null;
        }
        List<Double> obsL = new ArrayList<>();
        List<Double> obsW = new ArrayList<>();
        for (int k = 0; k < lenInCM.length; k++) {
            Double len = lenInCM[k];
            Double w = wInGrams[k];
            if (lenInCM[k] == null || w == null) {
                continue;
            }
            obsL.add(len);
            obsW.add(w);
        }
        return new LWRelationship(obsL, obsW);
    }

    public LWRelationship(List<Double> obsL, List<Double> obsW) {
        this.obsL = obsL;
        this.obsW = obsW;
        if (obsL.size() <= 2) {
            return;
        }
        Double sumEk2 = 0d;
        Double b0 = getB0();
        Double b1 = getB1();
        for (int k = 0; k < obsW.size(); k++) {
            Double logLk = Math.log(obsL.get(k));
            Double logWk = Math.log(obsW.get(k));
            Double ek = logWk - (b0 + b1 * logLk);
            sumEk2 += Math.pow(ek, 2);
        }
        Double sigma2 = 1d / (obsW.size() - 2) * sumEk2;
        a = Math.exp(b0) * Math.exp(sigma2 / 2);
        b = getB1();
        calculateR2();
    }

    private Double getLogSum(List<Double> a) {
        Double res = 0d;
        for (Double a1 : a) {
            res += Math.log(a1);
        }
        return res;
    }

    /**
     * @return the expected values of the logtransformed observed fish weights
     */
    private Double eLogW = null;

    private Double getELogW() {
        if (eLogW == null) {
            eLogW = getLogSum(obsW) / obsW.size();
        }
        return eLogW;
    }

    /**
     * @return the expected values of the logtransformed observed fish lengths
     */
    private Double eLogL = null;

    private Double getELogL() {
        if (eLogL == null) {
            eLogL = getLogSum(obsL) / obsL.size();
        }
        return eLogL;
    }

    /**
     * return return Sum( (log(Lk)-Elog(L)) * (log(Wk)-Elog(W) ) /
     * Sum((log(Lk)-Elog(L))^2)
     *
     */
    private Double b1 = null;

    private Double getB1() {
        if (b1 == null) {
            Double s1 = 0d, s2 = 0d;
            Double eLogL = getELogL();
            Double eLogW = getELogW();
            for (int k = 0; k < obsW.size(); k++) {
                Double logLk = Math.log(obsL.get(k));
                Double logWk = Math.log(obsW.get(k));
                s1 += (logLk - eLogL) * (logWk - eLogW);
                s2 += Math.pow(logLk - eLogL, 2);
            }
            b1 = s1 / s2;
        }
        return b1;
    }

    /**
     *
     * @return Elog(W) - b1*ElogL
     */
    private Double b0 = null;

    private Double getB0() {
        if (b0 == null) {
            b0 = getELogW() - getB1() * getELogL();
        }
        return b0;
    }

    /**
     *
     * @return
     */
    // Ek = log(Wk) - (b0 + b1*log(Lk)) 
    // sigma^2 = 1 / (n-2) * sum(Ek^2)  
    // A = exp(b0) * exp(sigma^2 / 2)
    public Double getA() {
        return a;
    }

    public Double getB() {
        return b;
    }

    public Double getR2() {
        return r2;
    }

    /**
     * Sum up residual error elements into r2 correlation fit.
     */
    private void calculateR2() {
        Double eLogW = getELogW();
        Double b0 = getB0();
        Double b1 = getB1();
        Double sumEk2 = 0d, sumY = 0d;
        for (int k = 0; k < obsW.size(); k++) {
            Double logWk = Math.log(obsW.get(k));
            Double ek = Math.log(obsW.get(k)) - (b0 + b1 * Math.log(obsL.get(k)));
            sumEk2 += Math.pow(ek, 2);
            sumY += Math.pow(logWk - eLogW, 2);
        }
        r2 = 1 - sumEk2 / sumY;
    }

    public Double getWeight(Double length) {
        if (a == null || b == null || length == null) {
            return null;
        }
        return getWeight(length, a, b);
    }

    public static Double getWeight(Double length, Double a, Double b) {
        return a * Math.pow(length, b);
    }
}
