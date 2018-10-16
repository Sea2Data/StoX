/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion;

import HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Methods for exporting as delimited files
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class DelimitedOutputWriter {

    protected TableMaker tableMaker;
    protected String delim;
    protected String escape;
    protected String comment;
    protected String suffix;
    protected String NA;

    /**
     *
     * @param delim string to use for delimiter
     * @param escape escapes sequence
     * @param comment comment character
     * @param suffix file suffix for exported files
     * @param NA for encoding not assigned values.
     */
    public DelimitedOutputWriter(String delim, String escape, String comment, String suffix, String NA) {
        this.delim = delim;
        this.escape = escape;
        this.comment = comment;
        this.suffix = suffix;
        this.NA = NA;
    }

    /**
     * @param line
     * @param stream 
     */
    public void writeLine(List<String> line, PrintStream stream) {
        Iterator<String> it = line.iterator();
        while (it.hasNext()) {
            String cell = it.next();
            if (cell == null) {
                cell = this.NA;
            }
            cell = cell.replace(this.escape, this.escape + this.escape);
            if (this.comment != null) {
                cell = cell.replace(this.comment, this.escape + this.comment);
            }
            cell = cell.replace("\n", this.escape + "\n");
            cell = cell.replace(this.delim, this.escape + this.delim);
            stream.print(cell);
            if (it.hasNext()) {
                stream.print(delim);
            }
        }
        stream.println();
    }

    /**
     *
     * @param tables tables to write
     * @param targetDir target directory where csv files will be created
     * @param formatdocumentation documentation for format that will be written
     * as comments in csv. May be null
     * @throws IOException
     * @throws ITableMakerNamingConvention.NamingException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws RelationalConvertionException
     */
    public void writeDelimitedFiles(Map<String, List<List<String>>> tables, File targetDir, String formatdocumentation) throws IOException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException {
        if (!targetDir.isDirectory()) {
            throw new IOException("Provided file is not a directory");
        }
        Map<String, File> files = new HashMap<>();
        for (String c : tables.keySet()) {
            File out = new File(targetDir, c + this.suffix);
            if (out.exists()) {
                throw new FileAlreadyExistsException(out.getAbsolutePath() + " already exists.");
            }
            files.put(c, out);
        }
        for (String c : tables.keySet()) {
            FileOutputStream s = new FileOutputStream(files.get(c));
            BufferedOutputStream b = new BufferedOutputStream(s);
            PrintStream stream = new PrintStream(b);
            if (formatdocumentation != null && this.comment != null) {
                String[] lines = formatdocumentation.split("\n");
                for (String l : lines) {
                    stream.println(this.comment + " " + l);
                }
            }
            for (List<String> row : tables.get(c)) {
                this.writeLine(row, stream);
            }
            stream.close();
            b.close();
            s.close();
        }
    }
}
