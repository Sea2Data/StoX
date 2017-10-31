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
import no.imr.stox.functions.utils.ReportUtil;

/**
 *
 * @author aasmunds
 */
public class SpeciesTSMix {

    String mixAcoCat;
    String acoCat;
    String specCat;
    Double m;
    Double a;
    Double d;

    public SpeciesTSMix(String mixAcoCat, String acoCat, String specCat, Double m, Double a, Double d) {
        this.mixAcoCat = mixAcoCat;
        this.acoCat = acoCat;
        this.specCat = specCat;
        this.m = m;
        this.a = a;
        this.d = d;
    }

    public String getMixAcoCat() {
        return mixAcoCat;
    }

    public void setMixAcoCat(String mixAcoCat) {
        this.mixAcoCat = mixAcoCat;
    }

    public String getAcoCat() {
        return acoCat;
    }

    public void setAcoCat(String acoCat) {
        this.acoCat = acoCat;
    }

    public String getSpecCat() {
        return specCat;
    }

    public void setSpecCat(String specCat) {
        this.specCat = specCat;
    }

    public Double getM() {
        return m;
    }

    public void setM(Double m) {
        this.m = m;
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public Double getD() {
        return d;
    }

    public void setD(Double d) {
        this.d = d;
    }

    public static List<SpeciesTSMix> fromString(String txt) {
        if (txt == null) {
            return null;
        }

        String lines[] = txt.split("/");
        return Arrays.stream(lines)
                .filter(line -> !line.isEmpty())
                .map(line -> line.split(";"))
                .filter(cells -> cells.length >= 5 && cells.length <= 6)
                .map(cells -> {
                    String mixacoCat = null;
                    String acoCat = null;
                    String specCat = null;
                    Double m = null;
                    Double a = null;
                    Double d = null;
                    if (txt.contains("=")) {
                        // tagged...
                        for (String elm : cells) {
                            String elms[] = elm.split("=");
                            if (elms.length != 2) {
                                continue;
                            }
                            String key = elms[0].toLowerCase().trim();
                            String val = elms[1].trim();
                            switch (key) {
                                case "mixacocat":
                                    mixacoCat = val;
                                    break;
                                case "acocat":
                                    acoCat = val;
                                    break;
                                case "speccat":
                                    specCat = val;
                                    break;
                                case "m":
                                    m = Conversion.safeStringtoDoubleNULL(val);
                                    break;
                                case "a":
                                    a = Conversion.safeStringtoDoubleNULL(val);
                                    break;
                                case "d":
                                    d = Conversion.safeStringtoDoubleNULL(val);
                            }
                        }

                    } else {
                        // untagged
                        int i = 0;
                        if (cells.length == 6) {
                            mixacoCat = cells[i++];
                        }
                        acoCat = cells[i++];
                        specCat = cells[i++];
                        m = Conversion.safeStringtoDoubleNULL(cells[i++]);
                        a = Conversion.safeStringtoDoubleNULL(cells[i++]);
                        d = Conversion.safeStringtoDoubleNULL(cells[i++]);
                    }
                    return new SpeciesTSMix(mixacoCat, acoCat, specCat, m, a, d);
                }
                ).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return ExportUtil.separatedMissingStr(';', "",
                ReportUtil.asgExpr("MixAcoCat", mixAcoCat),
                ReportUtil.asgExpr("AcoCat", acoCat),
                ReportUtil.asgExpr("SpecCat", specCat),
                ReportUtil.asgExpr("m", m),
                ReportUtil.asgExpr("a", a),
                ReportUtil.asgExpr("d", d));
    }

    public static String toString(List<SpeciesTSMix> l) {
        if (l == null) {
            return null;
        }
        return l.stream().map(s -> s.toString())
                .collect(Collectors.joining("/"));
    }

}
