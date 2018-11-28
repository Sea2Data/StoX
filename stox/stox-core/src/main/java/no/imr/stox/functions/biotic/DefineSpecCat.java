package no.imr.stox.functions.biotic;

import BioticTypes.v3.CatchsampleType;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.ReflectionUtil;
import no.imr.stox.model.IProcess;

/**
 * This class is used to filter data with special attributes among all biotic
 * data. It uses Java Expression Language (JEXL) to do the filtering.
 *
 * @author atlet
 * @author esmaelmh
 */
public class DefineSpecCat extends AbstractFunction {

    /**
     * Used when checking if expression is to be used on separate levels like
     * station, catch and sample.
     */
    public static List<String> getHeader(IProcess pr) {
        try {
            String fileName = ProjectUtils.resolveParameterFileName((String) pr.getParameterValue(Functions.PM_DEFINESPECCAT_FILENAME),
                    (String) pr.getModel().getProject().getProjectFolder());
            if (fileName == null) {
                return null;
            }
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            if (lines.size() <= 1) {
                return null;
            }
            return Arrays.asList(lines.get(0).split("[;,\\t]"));
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * This is the method that performs the filtering task. TODO Fix cyclic
     * complexity
     *
     * @param input
     * @return
     * @throws no.imr.stox.exception.UserErrorException
     */
    @Override
    public Object perform(Map<String, Object> input) {
        List<MissionBO> mList = (List<MissionBO>) input.get(Functions.PM_DEFINESPECCAT_BIOTICDATA);
        List<MissionBO> missions = BioticUtils.copyBioticData(mList);
        String specCat = (String) input.get(Functions.PM_DEFINESPECCAT_SPECCAT);
        String specCatMethod = (String) input.get(Functions.PM_DEFINESPECCAT_SPECCATMETHOD);
        String specVarStoX = (String) input.get(Functions.PM_DEFINESPECCAT_SPECVARBIOTIC);
        String fileName = (String) input.get(Functions.PM_DEFINESPECCAT_FILENAME);
        String specVarRef = (String) input.get(Functions.PM_DEFINESPECCAT_SPECVARREF);
        String specCatRef = (String) input.get(Functions.PM_DEFINESPECCAT_SPECCATREF);
        if (specVarStoX == null) {
            specVarStoX = "commonname";
        }
        Method specVarStoXGetter = ReflectionUtil.getGetter(CatchsampleType.class, specVarStoX);
        Map<String, String> m = new HashMap<>();
        switch (specCatMethod) {
            case Functions.SPECCATMETHOD_EXPRESSION: {
                if (specCat != null) {
                    String[] lines = specCat.split("/");
                    Arrays.stream(lines)
                            .forEach(l -> {
                                String[] groups = l.split(":");
                                if (groups.length == 2) {
                                    String[] elms = groups[1].split(",");
                                    Arrays.stream(elms)
                                            .forEach(e -> {
                                                m.put(e.toLowerCase(), groups[0]);
                                            });
                                }
                            });
                }
                break;
            }
            case Functions.SPECCATMETHOD_RESOURCEFILE: {
                fileName = ProjectUtils.resolveParameterFileName(fileName, (String) input.get(Functions.PM_PROJECTFOLDER));
                if (fileName == null) {
                    break;
                }
                try {
                    List<String> lines = Files.readAllLines(Paths.get(fileName));
                    if (lines.size() <= 1) {
                        break;
                    }
                    List<String> hdr = Arrays.asList(lines.get(0).split("[;,\\t]"));
                    Integer idxVarRef = hdr.indexOf(specVarRef);
                    Integer idxCatRef = hdr.indexOf(specCatRef);
                    if (idxVarRef < 0 || idxCatRef < 0 || idxCatRef.equals(idxVarRef)) {
                        break;
                    }
                    lines.forEach(l -> {
                        List<String> str = Arrays.asList(l.split("[;,\\t]"));
                        if (idxVarRef >= str.size() || idxCatRef >= str.size()) {
                            return;
                        }
                        String varRef = str.get(idxVarRef);
                        String catRef = str.get(idxCatRef);
                        m.put(varRef, catRef);
                    });
                } catch (IOException ex) {
                    Logger.getLogger(DefineSpecCat.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }

        missions.forEach(
                (ms) -> {
                    ms.getFishstationBOs().forEach((fs) -> {
                        fs.getCatchSampleBOs().forEach((cs) -> {
                            String spec = null;
                            if (specCat != null && m.isEmpty()) {
                                spec = specCat;
                            } else {
                                Object obj = ReflectionUtil.invoke(specVarStoXGetter, cs.bo());
                                if (obj != null) {
                                    String str = m.get(obj.toString().toLowerCase());
                                    if (str != null) {
                                        spec = str;
                                    }
                                }
                            }
                            cs.setSpecCat(spec); // Set spec cat to all catches
                        });
                    });
                }
        );
        return missions;
    }

}
