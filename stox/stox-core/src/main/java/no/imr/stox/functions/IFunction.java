package no.imr.stox.functions;

import java.util.Map;
import no.imr.stox.datastorage.IDataStorage;

/**
 * Implementations of this interface should perform some form of calculation
 * based on input data and parameters. .
 *
 * @author sjurl
 */
public interface IFunction {

    /**
     * Performs instantiated using input parameters and giving output as
     * datastorage in a map of datatypes.
     *
     * @param input parameters map from metaparameter name to value
     * @return
     */
    Object perform(Map<String, Object> input);
    IDataStorage getDataStorage();
}
