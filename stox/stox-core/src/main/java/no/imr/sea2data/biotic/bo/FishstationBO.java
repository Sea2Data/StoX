package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.FishstationType;
import BioticTypes.v3.MissionType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import no.imr.sea2data.imrbase.map.ILatLonEvent;

/**
 *
 * @author oddrune
 */
public class FishstationBO implements ILatLonEvent {

    FishstationType fs;
    MissionBO mission;

    private String key = null; // cache
    private String stratum; // should be moved to datatype
    private Integer year; // cache
    private List<CatchSampleBO> catchSampleBOs = new ArrayList<>();

    public FishstationBO(MissionBO ms) {
        mission = ms;
    }

    public FishstationBO(MissionBO ms, FishstationType fs) {
        this(ms);
        this.fs = fs;
    }

    public FishstationBO(MissionBO ms, FishstationBO bo) {
        this(ms, bo.getFs());
        stratum = bo.getStratum();
    }

    public MissionBO getMission() {
        return mission;
    }

    public FishstationType getFs() {
        return fs;
    }

    @Override
    public Double getStartLat() {
        return fs.getLatitudestart();
    }

    @Override
    public Double getStartLon() {
        return fs.getLongitudestart();
    }

    public boolean hasCatch(String noName) {
        for (CatchSampleBO c : getCatchSampleBOs()) {
            if (c.getCs().getCommonname().equals(noName)) {
                return true;
            }
        }
        return false;
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
            if (c.getCs().getCatchcount() == null) {
                continue;
            }
            n += c.getCs().getCatchcount();
        }
        return n;
    }

    public int getCount(String code) {
        return getCountBy(code, c -> c.getCs().getCommonname());
    }

    public int getCountBySpecies(String code) {
        return getCountBy(code, c -> c.getCs().getCatchcategory());
    }

    public int getLengthSampleCount(String spec) {
        int n = 0;
        if (getCatchSampleBOs() == null) {
            return 0;
        }
        for (CatchSampleBO c : getCatchSampleBOs()) {
            if (c.getCs().getCommonname() == null && c.getCs().getCatchcategory() == null) {
                continue;
            }
            if (c.getCs().getCommonname() != null) {
                if (!c.getCs().getCommonname().equalsIgnoreCase(spec)) { // SILDG03
                    continue;
                }
            } else if (c.getCs().getCatchcategory() != null) {
                if (!c.getCs().getCatchcategory().equalsIgnoreCase(spec)) { // 161722.G03
                    continue;
                }
            }
            if (c.getCs().getLengthsamplecount() == null) {
                continue;
            }
            n += c.getCs().getLengthsamplecount();
        }
        return n;
    }

    @Override
    public Double getStopLat() {
        return fs.getLatitudeend();
    }

    @Override
    public Double getStopLon() {
        return fs.getLongitudeend();
    }

    // Helpers
    public String getStratum() {
        return stratum;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }

    public Integer getYear() {
        if (year == null) {
            year = fs.getStationstartdate() != null ? fs.getStationstartdate().getYear() : null;
        }
        return year;
    }

    @Override
    public String getKey() {
        if (key != null) {
            return key;
        }
        String cruise = getMission().getMs().getCruise();
        key = (cruise != null ? cruise : (getYear() != null ? getYear() : "")) + "/" + (fs.getSerialnumber() != null ? fs.getSerialnumber() : "");
        return key;
    }

    @Override
    public String toString() {
        return getKey();
    }

    public List<CatchSampleBO> getCatchSampleBOs() {
        return catchSampleBOs;
    }

    public CatchSampleBO addCatchSample(CatchsampleType cs) {

        if (cs == null) {
            cs = new CatchsampleType();
//            cs.setParent(fs);
        }
        CatchSampleBO cbo = new CatchSampleBO(this, cs);
        getCatchSampleBOs().add(cbo);
        return cbo;
    }

}
