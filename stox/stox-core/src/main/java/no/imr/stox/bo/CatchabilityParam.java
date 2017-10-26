/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.ExportUtil;

/**
 *
 * @author aasmunds
 */
public class CatchabilityParam {

    Double alpha;
    Double beta;
    String specCat;
    Double lMin;
    Double lMax;

    public CatchabilityParam(String specCat, Double alpha, Double beta, Double lMin, Double lMax) {
        this.specCat = specCat;
        this.alpha = alpha;
        this.beta = beta;
        this.lMin = lMin;
        this.lMax = lMax;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public String getSpecCat() {
        return specCat;
    }

    public void setSpecCat(String specCat) {
        this.specCat = specCat;
    }

    public Double getlMin() {
        return lMin;
    }

    public void setlMin(Double lMin) {
        this.lMin = lMin;
    }

    public Double getlMax() {
        return lMax;
    }

    public void setlMax(Double lMax) {
        this.lMax = lMax;
    }

    public static List<CatchabilityParam> fromString(String txt) {
        if (txt == null) {
            return null;
        }

        String lines[] = txt.split("/");
        return Arrays.stream(lines)
                .filter(line -> !line.isEmpty())
                .map(line -> line.split(";"))
                .filter(cells -> cells.length >= 5 && cells.length <= 6)
                .map(cells -> {
                    int i = 0;
                    String s = cells[i++];
                    String specCat = s.isEmpty() ? null : s;
                    Double alpha = Conversion.safeStringtoDoubleNULL(cells[i++]);
                    Double beta = Conversion.safeStringtoDoubleNULL(cells[i++]);
                    Double lmin = Conversion.safeStringtoDoubleNULL(cells[i++]);
                    Double lmax = Conversion.safeStringtoDoubleNULL(cells[i++]);
                    return new CatchabilityParam(specCat, alpha, beta, lmin, lmax);
                }
                ).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return ExportUtil.separatedMissingStr(';', "", specCat, alpha, beta, lMin, lMax);
    }

    public static String toString(List<CatchabilityParam> l) {
        return l.stream().map(s -> s.toString())
                .collect(Collectors.joining("/"));
    }

    public static List<CatchabilityParam> find(List<CatchabilityParam> params, String specCat) {
        List<CatchabilityParam> l = params.stream().filter(p -> p.getSpecCat() != null && p.getSpecCat().equals(specCat)).collect(Collectors.toList());
        return l.isEmpty() ? null : l;
    }
}
