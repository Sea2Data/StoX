package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.FishstationType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import no.imr.sea2data.imrbase.map.ILatLonEvent;

public class FishstationBO extends BaseBO implements ILatLonEvent {

    private String stratum; // should be moved to datatype
    private List<CatchSampleBO> catchSampleBOs = new ArrayList<>();

    public FishstationBO(MissionBO ms, FishstationType fs) {
        super(ms, fs);
    }

    public FishstationBO(MissionBO ms, FishstationBO bo) {
        this(ms, bo.bo());
        stratum = bo.getStratum();
    }

    public MissionBO getMission() {
        return (MissionBO) getParent();
    }

    public FishstationType bo() {
        return (FishstationType) bo;
    }

    @Override
    public Double getStartLat() {
        return bo().getLatitudestart();
    }

    @Override
    public Double getStartLon() {
        return bo().getLongitudestart();
    }

    public int getCountBy(String species, Function<CatchSampleBO, String> spcodeFunc) {
        int n = 0;
        if (getCatchSampleBOs() == null) {
            return 0;
        }
        for (CatchSampleBO c : getCatchSampleBOs()) {
            String spcode = spcodeFunc.apply(c);
            if (spcode == null) {
                continue;
            }
            if (!spcode.equals(species)) {
                continue;
            }
            if (c.bo().getCatchcount() == null) {
                continue;
            }
            n += c.bo().getCatchcount();
        }
        return n;
    }

    public int getCount(String code) {
        return getCountBy(code, c -> c.bo().getCommonname());
    }

    public int getCountBySpecies(String code) {
        return getCountBy(code, c -> c.bo().getCatchcategory());
    }

    @FilterField(category = "function", help = "hasCatch(commonName) returns true if a fishstation has catch with the given species common name")
    public boolean hasCatch(String spec) {
        String s[] = spec.split("==");
        String field = "commonname";
        if (s.length == 2) {
            spec = s[1].trim();
            field = s[0].trim();
        }
        for (CatchSampleBO c : getCatchSampleBOs()) {
            String cspec;
            switch (field) {
                case "catchcategory":
                    cspec = c.bo().getCatchcategory();
                    break;
                case "commonname":
                    cspec = c.bo().getCommonname();
                    break;
                case "aphia":
                    cspec = c.bo().getAphia();
                    break;
                default:
                    cspec = null;
            }

            if (cspec == null || !cspec.equalsIgnoreCase(spec)) {
                continue;
            }
            return true;
        }
        return false;
    }

    @FilterField(category = "function", help = "getLengthSampleCount(spec) returns the number of length samples for a given species common name")
    public int getLengthSampleCount(String spec) {
        int n = 0;
        if (spec == null || getCatchSampleBOs() == null) {
            return 0;
        }
        String s[] = spec.split("==");
        String field = "commonname";
        if (s.length == 2) {
            spec = s[1].trim();
            field = s[0].trim();
        }
        for (CatchSampleBO c : getCatchSampleBOs()) {
            String cspec;
            switch (field) {
                case "catchcategory":
                    cspec = c.bo().getCatchcategory();
                    break;
                case "commonname":
                    cspec = c.bo().getCommonname();
                    break;
                case "aphia":
                    cspec = c.bo().getAphia();
                    break;
                default:
                    cspec = null;
            }

            if (cspec == null || !cspec.equalsIgnoreCase(spec)) {
                continue;
            }
            if (c.bo().getLengthsamplecount() == null) {
                continue;
            }
            n += c.bo().getLengthsamplecount();
        }
        return n;
    }

    @Override
    public Double getStopLat() {
        return bo().getLatitudeend();
    }

    @Override
    public Double getStopLon() {
        return bo().getLongitudeend();
    }

    // Helpers
    public String getStratum() {
        return stratum;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }

    @Override
    public String getInternalKey() {
        return bo().getSerialnumber() != null ? bo().getSerialnumber() + "" : "";
    }

    public List<CatchSampleBO> getCatchSampleBOs() {
        return catchSampleBOs;
    }

    public CatchSampleBO addCatchSample() {
        return addCatchSample((CatchsampleType) null);
    }

    public CatchSampleBO addCatchSample(CatchsampleType cs) {
        if (cs == null) {
            cs = new CatchsampleType();
        }
        return addCatchSample(new CatchSampleBO(this, cs));
    }

    public CatchSampleBO addCatchSample(CatchSampleBO cbo) {
        getCatchSampleBOs().add(cbo);
        return cbo;
    }

}
