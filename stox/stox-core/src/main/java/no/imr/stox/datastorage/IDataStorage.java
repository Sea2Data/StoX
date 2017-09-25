package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.stox.model.IProcess;

/**
 * Implementations of this interface should provide data to the implementations
 * of IFunction.
 *
 * @author sjurl
 */
public interface IDataStorage {

    /**
     * return identified data by key. Each function uses temporarily data.
     *
     * @param <T>
     * @return An object referenced by the key
     */
    <T> T getData();

    void writeToFile();

    Integer getNumDataStorageFiles();

    String getStorageFileName(Integer idxFile);

    String getStorageFileNamePostFix(Integer idxFile);

    void setProcess(IProcess process);

    IProcess getProcess();

    <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits);

    <T> String asTable(T data, Integer level);
    <T> String asTable(T data, Integer level, Boolean withUnits); 
}
