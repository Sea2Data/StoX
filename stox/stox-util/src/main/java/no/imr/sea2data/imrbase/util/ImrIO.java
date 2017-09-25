/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.imrbase.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

/**
 * Wrap checked to unckecked exceptions for IO
 *
 * @author aasmunds
 */
public class ImrIO {

    public static void write(Writer wr, String str) {
        try {
            wr.write(str);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static void writeln(Writer wr, String str) {
        write(wr, str + "\n");
    }

    public static void newLine(BufferedWriter wr) {
        try {
            wr.newLine();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}
