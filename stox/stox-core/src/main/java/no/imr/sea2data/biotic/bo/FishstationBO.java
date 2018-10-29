package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.FishstationType;
import BioticTypes.v3.MissionType;
import java.time.LocalDate;
import java.time.LocalTime;
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
    
    private String key = null;
    private String stratum;
    private Integer year; // cache
    private List<CatchSampleBO> catchSampleBOs = new ArrayList<>();

    public FishstationBO(MissionType ms) {
        fs = new FishstationType();
        fs.setParent(ms);
    }
    public FishstationBO(FishstationType fs) {
        this.fs = fs;
    }
    public FishstationBO(FishstationBO bo) {
        this(bo.getFs());
        stratum = bo.getStratum();
    }

    public MissionType getMission() {
        return (MissionType) fs.getParent();
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
            if (c.getCommonname().equals(noName)) {
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
            if (c.getCatchcount() == null) {
                continue;
            }
            n += c.getCatchcount();
        }
        return n;
    }

    public int getCount(String code) {
        return getCountBy(code, c -> c.getCommonname());
    }

    public int getCountBySpecies(String code) {
        return getCountBy(code, c -> c.getCatchcategory());
    }

    public int getLengthSampleCount(String spec) {
        int n = 0;
        if (getCatchSampleBOs() == null) {
            return 0;
        }
        for (CatchSampleBO c : getCatchSampleBOs()) {
            if (c.getCommonname() == null && c.getCatchcategory() == null) {
                continue;
            }
            if (c.getCommonname() != null) {
                if (!c.getCommonname().equalsIgnoreCase(spec)) { // SILDG03
                    continue;
                }
            } else if (c.getCatchcategory() != null) {
                if (!c.getCatchcategory().equalsIgnoreCase(spec)) { // 161722.G03
                    continue;
                }
            }
            if (c.getLengthsamplecount() == null) {
                continue;
            }
            n += c.getLengthsamplecount();
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
        if(year == null) {
            year = fs.getStationstartdate() != null ? fs.getStationstartdate().getYear() : null;
        }
        return year;
    }

    @Override
    public String getKey() {
        if (key != null) {
            return key;
        }
        String cruise = ((MissionType)fs.getParent()).getCruise();
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

    public CatchSampleBO addCatchSample() {
        CatchSampleBO cbo = new CatchSampleBO(this);
        getCatchSampleBOs().add(cbo);
        return cbo;
    }

}
