/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import com.vividsolutions.jts.geom.MultiPolygon;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import no.imr.stox.util.matrix.MatrixBO;
import static no.imr.stox.factory.FactoryUtil.acquireProject;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.StratumUtils;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProject;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class ConvertKystTorskStrata {

    @Test
    public void test() {
        //convertVinterTokt();
        IntStream
                .range(2003, 2017 + 1).boxed()
                .sorted(Collections.reverseOrder())
                .forEach(year -> {
                    //convertKystTokt(year, 6, "Saithe");
                    convertKystTokt(year);
                    // appendCatchability(year);
                });

    }

    public void convertKystTokt(Integer year) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("E:/arved/nyekysttorsk.txt"));
            String strata = null;
            Map<String, String> strataMap = new HashMap<>();
            System.out.println("Year " + year);
            for (String line : lines) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.trim().isEmpty()) {
                    strata = null;
                } else if (strata != null) {
                    strataMap.put(line, strata);
                } else {
                    strata = line;
                }
            }
            String folder = "R:\\alle\\stox\\Akustikk Kysttokt Torsk\\Nye Kysttorsk strata\\";
            //String folder = "C:\\users\\aasmunds\\workspace\\stox\\project\\";
            IProject prj = acquireProject(folder, "Varanger Stad cod acoustic index in autumn " + year, null);
            MatrixBO pol = StratumUtils.getStratumPolygonByWKTFile("C:\\Users\\aasmunds\\workspace\\stox\\reference\\stratum\\kysttorsk.txt");
            MatrixBO psuStrata = AbndEstProcessDataUtil.getPSUStrata(prj.getProcessData());
            MatrixBO psuStrataCopy = psuStrata.copy();
            psuStrata.clear();
            //MatrixBO suAssignments = AbndEstProcessDataUtil.getSUAssignments(prj.getProcessData());
            //MatrixBO edsuPsus = AbndEstProcessDataUtil.getEDSUPSUs(prj.getProcessData());
            for (String psu : psuStrataCopy.getRowKeys()) {
                String oldStrata = (String) psuStrataCopy.getRowValue(psu);
                String str = oldStrata.replaceFirst("Oppdrag[\\d]*_", "");
                String newStrata = strataMap.get(str);
                if (newStrata == null) {
                    return;
                    /*strataNotMapped.add(oldStrata);
                    suAssignments.removeRowKey(psu);
                    edsuPsus.removeRowKeyByRowValue(psu);
                    continue;*/
                }
                MultiPolygon mp = (MultiPolygon) pol.getRowValue(newStrata);
                if(mp == null) {
                    return;
                }
                psuStrata.setRowValue(psu, newStrata);
            }
            // Set strata polygons
            AbndEstProcessDataUtil.getStratumPolygons(prj.getProcessData()).clear();
            for (String name : pol.getRowKeys()) {
                AbndEstProcessDataUtil.setStratumPolygon(prj.getProcessData(), name, true, (MultiPolygon) pol.getRowValue(name));
            }
            prj.save();
        } catch (IOException ex) {
            Logger.getLogger(ConvertKystTorskStrata.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
