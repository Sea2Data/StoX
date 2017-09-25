/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.components.table;

import java.io.StringWriter;

/**
 * Format table from tabseparated to right aligned with space
 *
 * @author aasmunds
 */
public class FormattedOutput {

    public static String formatTable(String table) {
        if (table == null || table.isEmpty()) {
            return "";
        }
        String[] lines = table.split("\n");
        int[] colSizes = new int[lines[0].split("\t").length];
        for (int i = 0; i < colSizes.length; i++) {
            colSizes[i] = 0;
        }
        if (colSizes.length <= 1) {
            return table;
        }
        for (String line : lines) {
            String[] cells = line.split("\t");
            if (cells.length > colSizes.length) {
                return table;
            }
            for (int i = 0; i < cells.length; i++) {
                String cell = cells[i];
                int colSize = cell.length();
                colSizes[i] = Math.max(colSizes[i], colSize);
            }
        }
        StringWriter sw = new StringWriter();
        for (String line : lines) {
            String[] cells = line.split("\t");
            for (int i = 0; i < cells.length; i++) {
                String cell = cells[i];
                int colSize = colSizes[i] + 1;
                sw.append(String.format("%" + colSize + "s", cell));
            }
            sw.append("\n");
        }
        return sw.toString();
    }
}
