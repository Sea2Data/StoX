package no.imr.stox.datastorage;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.stox.model.IProcess;

/**
 * Implements IDataStorage by storing the data in memory during running of the
 * program. TODO: We need to rewrite this method so that it always returns a
 * copy of the return objects and not the stored objects. This storage lso
 * stores all results to file.
 *
 * @author atlet
 */
public class FileDataStorage implements IDataStorage {

    /**
     * File suffix.
     */
    /**
     * Charset for file.
     */
    private static final String CHARSET = "8859_1";
    private IProcess process;

    public FileDataStorage() {
    }

    @Override
    public <T> T getData() {
        return (T) process.getOutput();
    }

    @Override
    public IProcess getProcess() {
        return process;
    }

    @Override
    public void setProcess(IProcess process) {
        this.process = process;
    }

    /**
     * Returning the storage key from datatype The file name is reflected by
     * function name, process step and meta data type.
     *
     * @return storage key
     */
    public String getOutputFolder() {
        return getProcess().getModel().getOutputFolder();
    }

    public String getOutputFileName() {
        return getProcess().getOutputFileName();
    }

    @Override
    public void writeToFile() {
        onDataStored();
    }

    @Override
    public Integer getNumDataStorageFiles() {
        return 1;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        return "";
    }

    @Override
    public String getStorageFileName(Integer idxFile) {
        String postFix = getStorageFileNamePostFix(idxFile);
        String outputFolder = getOutputFolder();
        String outputFileName = getOutputFileName();
        prepareData(getData());
        return getProcess().getOutputFileName(outputFolder, outputFileName, postFix);
    }

    private <T> void onDataStored() {
        Integer numFiles = getNumDataStorageFiles();
        for (Integer idxFile = 1; idxFile <= numFiles; idxFile++) {
            prepareData(getData());
            writeData(getData(), getStorageFileName(idxFile), idxFile);
        }
    }

    public <T> void writeData(T data, String fileName, Integer level) {
        /*        if (res == null) {
         return;
         }*/
        if (/*!res.isEmpty() && */getProcess().getModel().isExportCSV()) {
//            PrintWriter writer = null;
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "UTF-8"))) {
                asTable(data, level, bw, false);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
            /*try {
             try {
             writer = new PrintWriter(file, CHARSET);
             } catch (FileNotFoundException | UnsupportedEncodingException ex) {
             Logger.getLogger(FileDataStorage.class.getName()).log(Level.SEVERE, null, ex);
             }
             writer.write(res);
             } finally {
             if (writer != null) {
             writer.close();
             }
             }*/
        }
    }

    /**
     * Set export format (number decimals, headers, group settings) for
     * variables exported to file.
     *
     * @param <T>
     * @param data
     */
    public <T> void prepareData(T data) {
        if (data instanceof MatrixBO) {
            MatrixBO bo = (MatrixBO) data;
            IMetaMatrix mm = bo.getMetaMatrix();
            switch (mm.getVariable()) {
                case Functions.VAR_NASC:
                    mm.setNumDecimalsInExports(4);
                    break;
                case Functions.VAR_DENSITY:
                    mm.setNumDecimalsInExports(0);
                    break;
                case Functions.VAR_ABUNDANCE:
                    mm.setNumDecimalsInExports(0);
                    break;
            }
            /*boolean isStationObservation = false;
             if(getData() instanceof SingleMatrixWithResolution) {
             SingleMatrixWithResolution matrs = (SingleMatrixWithResolution)getData();
             String obsRes = (String) matrs.getResolutionMatrix().getRowValue(Functions.RES_OBSERVATIONTYPE);
             if(obsRes != null && obsRes.equals(Functions.OBSERVATIONTYPE_STATION)) {
             isStationObservation = true;
             }
             }
             for (String dim : mm.getDimensions().keySet()) {
             String dimension = mm.getDimensions().get(dim);
             if (dimension.equals("Station") || dimension.equals("Observation") && isStationObservation) {
             mm.getHeaders().put(dim, ExportUtil.tabbed("Cruise", "SerialNo"));
             } else if (dimension.equals("Distance")) {
             mm.getHeaders().put(dim, ExportUtil.tabbed("Cruise", "Log", "Date", "Time"));
             return;
             }
             }*/
        }
    }

    /**
     * Return data object as table of text.
     *
     * @param <T>
     * @param data
     * @param level
     * @param wr
     * @param withUnits
     */
    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        if (data instanceof MatrixBO) {
            MatrixBO bo = (MatrixBO) data;
            bo.asTable(wr);
        }
    }

    @Override
    public <T> String asTable(T data, Integer level) {
        return asTable(data, level, false);
    }

    @Override
    public <T> String asTable(T data, Integer level, Boolean withUnits) {
        StringWriter wr = new StringWriter();
        asTable(data, level, wr, withUnits);
        return wr.toString();
    }
}
