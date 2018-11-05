package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.FishstationType;
import BioticTypes.v3.MissionType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import no.imr.sea2data.imrbase.map.ILatLonEvent;

public class FishstationBO extends BaseBO implements ILatLonEvent {

    private String key = null; // cache
    private String stratum; // should be moved to datatype
    private Integer year; // cache
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

    @FilterField(category="function", help="hasCatch(commonName) returns true if a fishstation has catch with the given species common name")
    public boolean hasCatch(String commonName) {
        for (CatchSampleBO c : getCatchSampleBOs()) {
            if (c.bo().getCommonname().equals(commonName)) {
                return true;
            }
        }
        return false;
    }

    @FilterField(category="function", help="getYear() returns the year of station start date")
    public Integer getYear() {
        if (year == null) {
            year = bo().getStationstartdate() != null ? bo().getStationstartdate().getYear() : null;
        }
        return year;
    }

    @FilterField(category="function", help="getLengthSampleCount(spec) returns the number of length samples for a given species common name")
    public int getLengthSampleCount(String spec) {
        int n = 0;
        if (getCatchSampleBOs() == null) {
            return 0;
        }
        for (CatchSampleBO c : getCatchSampleBOs()) {
            if (c.bo().getCommonname() == null && c.bo().getCatchcategory() == null) {
                continue;
            }
            if (c.bo().getCommonname() != null) {
                if (!c.bo().getCommonname().equalsIgnoreCase(spec)) { // SILDG03
                    continue;
                }
            } else if (c.bo().getCatchcategory() != null) {
                if (!c.bo().getCatchcategory().equalsIgnoreCase(spec)) { // 161722.G03
                    continue;
                }
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
    public String getKey() {
        if (key != null) {
            return key;
        }
        String cruise = getMission().bo().getCruise();
        key = (cruise != null ? cruise : (getYear() != null ? getYear() : "")) + "/" + 
                (bo().getSerialnumber() != null ? bo().getSerialnumber() : "");
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
