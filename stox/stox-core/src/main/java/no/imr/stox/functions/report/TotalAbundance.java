/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.report;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.ReportsBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class TotalAbundance extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ReportsBO bo = new ReportsBO();
        MatrixBO totAbnd = new MatrixBO(Functions.MM_TOTALABUNDANCEREPORT_MATRIX);
        bo.getMatrices().put(Functions.REPORT_TOTALABUNDANCE, totAbnd);
        Integer scale = (Integer) input.get(Functions.PM_TOTALABUNDANCE_SCALE);
        Stream.of(Functions.PM_TOTALABUNDANCE_WEIGHTABUNDANCE, Functions.PM_TOTALABUNDANCE_COUNTABUNDANCE)
                .forEach(param -> {
                    String fileName = (String) input.get(param);
                    if (fileName != null && !fileName.isEmpty()) {
                        if (!new File(fileName).exists()) {
                            logger.error("Missing input file for parameter " + Functions.PM_TOTALABUNDANCE_WEIGHTABUNDANCE, null);
                        }
                        String var = null;
                        switch (param) {
                            case Functions.PM_TOTALABUNDANCE_WEIGHTABUNDANCE:
                                var = Functions.CATCHVARIABLE_WEIGHT;
                                break;
                            case Functions.PM_TOTALABUNDANCE_COUNTABUNDANCE:
                                var = Functions.CATCHVARIABLE_COUNT;
                                break;
                        }
                        readAbundance(fileName, totAbnd, var, scale);
                    }
                });
        return bo;
    }

    private void readAbundance(String fileName, MatrixBO totAbnd, String var, Integer scale) {
        if (var == null) {
            return;
        }
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(fileName), Charset.forName("UTF-8"));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        if (lines == null || lines.isEmpty()) {
            return;
        }
        List<String> hdr = Arrays.asList(lines.remove(0).toLowerCase().split("\t")); // remove hdr
        int iSpecCat = hdr.indexOf(Functions.DIM_SPECCAT.toLowerCase());
        int iSU = hdr.indexOf(Functions.DIM_SAMPLEUNIT.toLowerCase());
        int iEstLayer = hdr.indexOf(Functions.DIM_ESTLAYER.toLowerCase());
        int iAbundance = hdr.indexOf(Functions.VAR_ABUNDANCE.toLowerCase());

        lines.stream()
                .map(line -> line.split("\t"))
                .forEach(line -> {
                    String specCat = line[iSpecCat];
                    String su = line[iSU];
                    String layer = line[iEstLayer];
                    String abnd = line[iAbundance];
                    Double val = Conversion.safeStringtoDoubleNULL(abnd);
                    if (val == null) {
                        return;
                    }
                    val = val / scale;
                    totAbnd.setGroupRowColCellValue(specCat, su, layer, var, val);
                });
    }

}
