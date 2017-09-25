package no.imr.stox.nodes;

import java.util.HashMap;
import java.util.Map;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.sea2data.imrbase.map.LatLonUtil;
import no.imr.sea2data.imrbase.util.IMRdate;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Åsmund
 */
public class StationNode extends AbstractNode {

    private final FishstationBO fs;

    public StationNode(FishstationBO fs) {
        super(Children.LEAF, Lookups.singleton(fs));
        setDisplayName(fs.getKey());
        this.fs = fs;
    }

    class StationPropertySupport extends PropertySupport.ReadOnly {

        public static final int PROP_CRUISE = 0;
        public static final int PROP_SERIALNO = 1;
        public static final int PROP_DATE = 2;
        public static final int PROP_POS = 3;
        public static final int PROP_PLATFORM = 4;
        public static final int PROP_GEAR = 5;
        public static final int PROP_BDEP = 6;
        public static final int PROP_FDEP = 7;
        public static final int PROP_DISTANCE = 8;
        private final int prop;

        public StationPropertySupport(String name, int prop) {
            super(name, String.class, name, name);
            this.prop = prop;
        }

        @Override
        public Object getValue() {
            switch (prop) {
                case PROP_CRUISE:
                    return fs.getCruise();
                case PROP_SERIALNO:
                    return fs.getSerialNo();
                case PROP_DATE:
                    return IMRdate.getDefaultDateTimeFormat().format(IMRdate.encodeDate(fs.getStartDate(), fs.getStartTime())) + " (UTC)";
                case PROP_POS:
                    return LatLonUtil.latLonToStr(fs.getLatitudeStart(), fs.getLongitudeStart());
                case PROP_PLATFORM:
                    return fs.getPlatformName() != null ? fs.getPlatformName() : fs.getCallSignal() != null ? fs.getCallSignal() : "";
                case PROP_GEAR:
                    return fs.getGear();
                case PROP_FDEP:
                    return getMinMaxStr(fs.getFishingDepthMin(), fs.getFishingDepthMax());
                case PROP_BDEP:
                    return getMinMaxStr(fs.getBottomDepthStart(), fs.getBottomDepthStop());
                case PROP_DISTANCE:
                    return fs.getDistance() != null ? fs.getDistance() : "";
            }
            return null;
        }
    };

    String getMinMaxStr(Double min, Double max) {
        String smin = min == null ? "" : min + "";
        String smax = max == null ? "" : max + "";
        String res = smin;
        if (!smin.isEmpty() && !smax.isEmpty()) {
            res += " - " + smax;
        }
        if (res.isEmpty()) {
            res = smax;
        }
        return res;
    }

    class CatchSamplePropertySupport extends PropertySupport.ReadOnly {

        Double weight;

        public CatchSamplePropertySupport(String species, Double weight) {
            super(species, String.class, species, species);
            this.weight = weight;
        }

        @Override
        public Object getValue() {
            return weight + "";
        }
    };

    @Override
    protected Sheet createSheet() {
        Sheet sh = Sheet.createDefault();

        Sheet.Set propSet = Sheet.createPropertiesSet();
        propSet.setName("Station");
        propSet.setDisplayName("Station");
        propSet.put(new StationPropertySupport("Cruise", StationPropertySupport.PROP_CRUISE));
        propSet.put(new StationPropertySupport("Serial no", StationPropertySupport.PROP_SERIALNO));
        propSet.put(new StationPropertySupport("Date", StationPropertySupport.PROP_DATE));
        propSet.put(new StationPropertySupport("Position", StationPropertySupport.PROP_POS));
        propSet.put(new StationPropertySupport("Platform", StationPropertySupport.PROP_PLATFORM));
        propSet.put(new StationPropertySupport("Gear", StationPropertySupport.PROP_GEAR));
        propSet.put(new StationPropertySupport("Fish.depth", StationPropertySupport.PROP_FDEP));
        propSet.put(new StationPropertySupport("Bot.depth", StationPropertySupport.PROP_BDEP));
        propSet.put(new StationPropertySupport("Distance", StationPropertySupport.PROP_DISTANCE));
        sh.put(propSet);

        propSet = Sheet.createPropertiesSet();
        propSet.setName("Catch weight");
        propSet.setDisplayName("Catch weight");
        Map<String, Double> m = new HashMap<>();
        for (CatchBO c : fs.getCatchBOCollection()) {
            String sKey = c.getSpeciesKey();
            for (SampleBO s : c.getSampleBOCollection()) {
                Double w = s.getWeight();
                if (w == null || w == 0) {
                    continue;
                }
                Double d = m.get(sKey);
                m.put(sKey, d == null ? w : d + w);
            }
        }
        for (String sp : m.keySet()) {
            propSet.put(new CatchSamplePropertySupport(sp, m.get(sp)));
        }
        sh.put(propSet);
        return sh;
    }

}
