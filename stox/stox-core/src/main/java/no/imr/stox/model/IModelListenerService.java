package no.imr.stox.model;

import java.util.List;

/**
 * Model listener service
 *
 * @author aasmunds
 */
public interface IModelListenerService {

    /**
     * Get function listeners registered
     *
     * @return function listeners registered
     */
    List<IModelListener> getModelListeners();

    /**
     * add a new function listener
     *
     * @param modelListener
     */
    void addModelListener(IModelListener modelListener);

    /**
     * remove function listener
     *
     * @param modelListener
     */
    void removeModelListener(IModelListener modelListener);
}
