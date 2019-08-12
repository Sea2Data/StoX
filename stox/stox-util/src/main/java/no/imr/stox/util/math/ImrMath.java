/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.util.math;

/**
 *
 * @author aasmunds
 */
public class ImrMath {

    public static final double RND_ERR = 0.0000000000001;

    public static Double safePlus(Double a, Double b) {
        if (a == null || b == null) {
            return null;
        }
        // comment
        return a + b;
    }

    public static Double safeIntegerToDouble(Integer a) {
        if (a != null) {
            return a.doubleValue();
        }
        return null;
    }

    public static Double safeMinus(Double a, Double b) {
        if (a == null || b == null) {
            return null;
        }
        return a - b;
    }

    public static Double safeMult(Double a, Double b) {
        if (a == null || b == null) {
            return null;
        }
        return a * b;
    }

    public static Integer safeMult(Integer a, Integer b) {
        if (a == null || b == null) {
            return null;
        }
        return a * b;
    }

    public static Double safeDivide(Double a, Double b) {
        if (a == null || b == null || b == 0) {
            return null;
        }
        return a / b;
    }

    public static Double safeDivide(Integer a, Integer b) {
        if (a == null || b == null) {
            return null;
        }
        return safeDivide(a.doubleValue(), b.doubleValue());
    }

    public static Double safeTrunc(Double a) {
        if (a == null) {
            return null;
        }
        return Integer.valueOf(a.intValue()).doubleValue();
    }

    public static Double safeCeil(Double a) {
        if (a == null) {
            return null;
        }
        return Math.ceil(a);
    }

    /**
     * trunc a number against interval. This is useful i.e. calc. length groups
     * Note: rounding error before int operand is taken into account rounding
     * error
     *
     * @param v
     * @param intv
     * @return
     */
    public static Double trunc(Double v, Double intv) {
        v = safePlus(v, RND_ERR);
        v = safeDivide(v, intv);
        Double newv = safeTrunc(v);
        return safeMult(newv, intv);
    }

    public static Integer safeMod(Integer a, Integer b) {
        if (a == null || b == null) {
            return null;
        }
        return a % b;
    }

    public static Integer safeRound(Double a) {
        if (a == null) {
            return null;
        }
        return (int) Math.round(a);
    }

    public static Integer safeMin(Integer a, Integer b) {
        if (a == null || b == null) {
            return null;
        }
        return Math.min(a, b);
    }

    public static Double safeMin(Double a, Double b) {
        if (a == null || b == null) {
            return null;
        }
        return Math.min(a, b);
    }

    public static Integer safeMax(Integer a, Integer b) {
        if (a == null || b == null) {
            return null;
        }
        return Math.max(a, b);
    }

    public static Double safeMax(Double a, Double b) {
        if (a == null || b == null) {
            return null;
        }
        return Math.max(a, b);
    }
}
