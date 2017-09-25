package no.imr.sea2data.stox.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.imr.stox.api.LFQ;
import no.imr.stox.bo.LengthDistMatrix; 
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.exception.UserErrorException;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;
import org.openide.util.Exceptions;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Åsmund
 */
@ServiceProvider(service = LFQProvider.class)
public class LFQProvider {

    private final List<InstanceContent> lfqSelection = new ArrayList<>();

    public InstanceContent createLfqSelection() {
        InstanceContent lfq = new InstanceContent();
        lfqSelection.add(lfq);
        return lfq;
    }

    public void removeFromLookup() {
        for (InstanceContent ic : lfqSelection) {
            ic.set(Collections.emptySet(), null);
        }
    }

    public void createLFQ(String observation, LengthDistMatrix totLenDist, String lengthDistType) {
        LFQ lfq = new LFQ(lengthDistType,  totLenDist.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL));
        for (String species : totLenDist.getData().getKeys()) {
            MatrixBO grp = totLenDist.getData().getValueAsMatrix(species);
            MatrixBO row = grp.getValueAsMatrix(observation);
            if (row == null) {
                continue;
            }
            // Length distribution variable
            MatrixBO cell = row.getDefaultValueAsMatrix();
            // For all Length groups.. combine relative assignment weight with length distribution
            for (String lengthGroup : cell.getKeys()) {
                Double weightedCount = cell.getValueAsDouble(lengthGroup);
                lfq.getLFQ().put(lengthGroup, weightedCount);
            }
            // only one species for now
            break;
        }
        for (InstanceContent ic : lfqSelection) {
            ic.set(Collections.singleton(lfq), null);
        }
    }

    /**
     * Lookup the assignment id from psu. attempt to show lfq
     *
     * @param model
     * @param pd
     * @param psu
     */
    public void createPSULFQ(IModel model, ProcessDataBO pd, String psu) {
        String assignmentID = AbndEstProcessDataUtil.getSUAssignment(pd, psu, "1");
        if (assignmentID != null) {
            createLFQ(assignmentID, (LengthDistMatrix) model.findProcessByFunction(Functions.FN_TOTALLENGTHDIST).performFunction(), 
                    (String) getStationLengthDistProcess(model).getParameterValue(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE));
        }
    }

    IProcess getStationLengthDistProcess(IModel model) {
        return model.findProcessByFunction(Functions.FN_STATIONLENGTHDIST);
    }

    public void createStationLFQ(IModel model, String station) {
        IProcess p = getStationLengthDistProcess(model);
        if (p == null) {
            return;
        }
        Object o = p.getOutput();
        if (o == null) {
            try {
                o = p.performFunction();
            } catch (UserErrorException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (o == null) {
                return;
            }
        }
        createLFQ(station, (LengthDistMatrix) o, (String) p.getParameterValue(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE));
    }

}
