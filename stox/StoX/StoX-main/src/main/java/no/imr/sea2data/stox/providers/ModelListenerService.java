package no.imr.sea2data.stox.providers;

import no.imr.stox.model.IModelListenerService;
import java.util.ArrayList;
import java.util.List;
import no.imr.stox.model.IModelListener;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implementation of StoX function listener service, a place where to register
 * function listeners
 *
 * @author aasmunds
 */
@ServiceProvider(service = IModelListenerService.class)
public class ModelListenerService implements IModelListenerService {

    private final List<IModelListener> modelListeners = new ArrayList<IModelListener>();

    public ModelListenerService() {
    }

    @Override
    public void addModelListener(IModelListener modelListener) {
        modelListeners.add(modelListener);
    }

    @Override
    public void removeModelListener(IModelListener modelListener) {
        modelListeners.remove(modelListener);
    }

    @Override
    public List<IModelListener> getModelListeners() {
        return modelListeners;
    }
}
