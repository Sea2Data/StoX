package no.imr.stox.functions.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.bo.LengthDistMatrix;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public final class BioticUtils {

    public static Double getLengthInterval(Integer i) {
        switch (i) {
            case 6:
                return 0.05;
            case 7:
                return 0.01;
            case 1:
                return 0.1;
            case 2:
                return 0.5;
            case 3:
                return 1d;
            case 4:
                return 3d;
            case 5:
                return 5d;
        }
        return null;
    }

    /**
     * Hidden constructor
     */
    private BioticUtils() {
    }

    public static Object getIndVar(IndividualBO i, String code) {
        switch (code) {
            case Functions.COL_IND_TRAWLQUALITY:
                return i.getSample().getCatchBO().getStationBO().getTrawlQuality();
            case Functions.COL_IND_GROUP:
                return i.getSample().getGroup();
            case Functions.COL_IND_SAMPLETYPE:
                return i.getSample().getSampletype();
            case Functions.COL_IND_CRUISE:
                return i.getSample().getCatchBO().getStationBO().getCruise();
            case Functions.COL_IND_SERIALNO:
                return i.getSample().getCatchBO().getStationBO().getSerialNo();
            case Functions.COL_IND_PLATFORM:
                return i.getSample().getCatchBO().getStationBO().getCatchPlatform();
            case Functions.COL_IND_STARTDATE:
                return IMRdate.formatDate(i.getSample().getCatchBO().getStationBO().getStationStartDate());
            case Functions.COL_IND_STARTTIME:
                return IMRdate.formatTime(i.getSample().getCatchBO().getStationBO().getStationStartTime());
            case Functions.COL_IND_FISHSTATIONTYPE:
                return i.getSample().getCatchBO().getStationBO().getStationType();
            case Functions.COL_IND_LATITUDESTART:
                return Conversion.formatDoubletoDecimalString(i.getSample().getCatchBO().getStationBO().getLatitudeStart(), 4);
            case Functions.COL_IND_LONGITUDESTART:
                return Conversion.formatDoubletoDecimalString(i.getSample().getCatchBO().getStationBO().getLongitudeStart(), 4);
            case Functions.COL_IND_SYSTEM:
                return i.getSample().getCatchBO().getStationBO().getSystem();
            case Functions.COL_IND_AREA:
                return i.getSample().getCatchBO().getStationBO().getArea();
            case Functions.COL_IND_LOCATION:
                return i.getSample().getCatchBO().getStationBO().getLocation();
            case Functions.COL_IND_GEAR:
                return i.getSample().getCatchBO().getStationBO().getGear();
            case Functions.COL_IND_SPECCAT:
                return i.getSample().getCatchBO().getSpeciesCatTableKey();
            case Functions.COL_IND_SPECIES:
                return i.getSample().getCatchBO().getSpecies();
            case Functions.COL_IND_NONAME:
                return i.getSample().getCatchBO().getNoname();
            case Functions.COL_IND_APHIA:
                return i.getSample().getCatchBO().getAphia();
            case Functions.COL_IND_CATCHWEIGHT:
                return i.getSample().getWeight();
            case Functions.COL_IND_CATCHCOUNT:
                return i.getSample().getCount();
            case Functions.COL_IND_SAMPLENUMBER:
                return i.getSample().getSampleNumber();
            case Functions.COL_IND_LENGTHSAMPLEWEIGHT:
                return i.getSample().getLengthSampleWeight();
            case Functions.COL_IND_LENGTHSAMPLECOUNT:
                return i.getSample().getLengthSampleCount();
            case Functions.COL_IND_FREQUENCY:
                return 1;
            case Functions.COL_IND_NO:
                return i.getNo();
            case Functions.COL_IND_WEIGHT:
                return i.getWeight();
            case Functions.COL_IND_LENGTH:
                return i.getLength();
            case Functions.COL_IND_AGE:
                return i.getAge();
            case Functions.COL_IND_SEX:
                return i.getSex();
            case Functions.COL_IND_DEVELOPMENTALSTAGE:
                return i.getDevelopmentalStage();
            case Functions.COL_IND_STAGE:
                return i.getStage();
            case Functions.COL_IND_SPECIALSTAGE:
                return i.getSpecialStage();
            case Functions.COL_IND_DIGESTDEG:
                return i.getDigestDeg();
            case Functions.COL_IND_FAT:
                return i.getFat();
            case Functions.COL_IND_LIVER:
                return i.getLiver();
            case Functions.COL_IND_LIVERWEIGHT:
                return i.getLiverWeight();
            case Functions.COL_IND_GONADWEIGHT:
                return i.getGonadWeight();
            case Functions.COL_IND_STOMACHWEIGHT:
                return i.getStomachWeight();
            case Functions.COL_IND_VERTEBRAE:
                return i.getVertebrae();
            case Functions.COL_IND_LENGTHUNIT:
                return i.getLengthUnit();
            case Functions.COL_IND_WEIGHTMETHOD:
                return i.getWeightMethod();
            case Functions.COL_IND_STOMACHFILLFIELD:
                return i.getStomachFillField();
            case Functions.COL_IND_LIVERPARASITE:
                return i.getLiverParasite();
            case Functions.COL_IND_SPAWNINGAGE:
                return i.getSpawningAge();
            case Functions.COL_IND_SPAWNINGZONES:
                return i.getSpawningZones();
            case Functions.COL_IND_READABILITY:
                return i.getReadability();
            case Functions.COL_IND_OTOLITHTYPE:
                return i.getOtolithType();
            case Functions.COL_IND_OTOLITHEDGE:
                return i.getOtolithEdge();
            case Functions.COL_IND_OTOLITHCENTRE:
                return i.getOtolithCentre();
            case Functions.COL_IND_CALIBRATION:
                return i.getCalibration();
        }
        return null;
    }

    /**
     *
     * @param fList
     * @param key
     * @return Fishstation from key
     */
    public static FishstationBO findStation(List<FishstationBO> fList, String key) {
        if (fList == null) {
            return null;
        }
        for (FishstationBO fs : fList) {
            if (fs.getKey().equals(key)) {
                return fs;
            }
        }
        return null;
    }

    public static FishstationBO findStationBySerialNo(List<FishstationBO> fList, Integer serialNo) {
        if (fList == null) {
            return null;
        }
        return fList.stream().filter(s -> Objects.equals(s.getSerialNo(), serialNo)).findFirst().orElse(null);
    }

    public static Collection<FishstationBO> findStations(List<FishstationBO> fList, Collection<String> keys) {
        if (keys == null) {
            return null;
        }
        List<FishstationBO> stations = new ArrayList<>();
        for (String key : keys) {
            stations.add(findStation(fList, key));
        }
        return stations;
    }

    /**
     * Calculate the Length group
     *
     * @param len
     * @param intv
     * @return
     */
    public static String getLenGrp(Double len, Double intv) {
        if (intv == 0 || intv == null || len == null) {
            return null;
        }
        int numDec = (int) Math.max(0, -Math.round(Math.floor(Math.log10(intv))));
        Double nLen = ImrMath.trunc(len, intv);
        String res = String.format("%." + numDec + "f", nLen);
        if (numDec > 0) {
            res = res.replace(",", ".");
        }
        return res;
    }

    /**
     * Transform a length distribution into percent
     *
     * @param ld
     */
    public static void toPercent(LengthDistMatrix ld) {
        for (String specCatKey : ld.getData().getKeys()) {
            MatrixBO specCat = ld.getData().getValueAsMatrix(specCatKey);
            for (String obsKey : specCat.getKeys()) {
                MatrixBO obs = specCat.getValueAsMatrix(obsKey);
                MatrixBO lfq = obs.getDefaultValueAsMatrix();
                Double valueTot = 0d;
                for (String lenGrp : lfq.getKeys()) {
                    Double value = lfq.getValueAsDouble(lenGrp);
                    if (value == null) {
                        continue;
                    }
                    valueTot = StoXMath.append(value, valueTot);
                }
                if (valueTot == 0) {
                    continue;
                }
                for (String lenGrp : lfq.getKeys()) {
                    Double value = lfq.getValueAsDouble(lenGrp);
                    Double valuePct = StoXMath.inPercent(value, valueTot);
                    lfq.setValue(lenGrp, valuePct);
                }
            }
        }

    }

    public static double getLengthInterval(List<FishstationBO> fishStations) {
        Set<Integer> units = new HashSet<>();
        for (FishstationBO fs : fishStations) {
            for (CatchBO c : fs.getCatchBOCollection()) {
                for (SampleBO s : c.getSampleBOCollection()) {
                    for (IndividualBO i : s.getIndividualBOCollection()) {
                        if (i.getLengthUnit() == null || i.getLengthUnit().isEmpty()) {
                            continue;
                        }
                        units.add(Conversion.safeStringtoIntegerNULL(i.getLengthUnit()));
                    }
                }
            }
        }
        // Analyse the combinations:
        // 6 0.01cm
        // 7 0.05cm
        // 1 0.10cm
        // 2 0.50cm
        // 3 1.00cm
        // 4 3.00cm
        // 5 5.00cm
        if (units.contains(4) && units.contains(5)) {
            return 3.0 * 5.0; // 15 cm 
        } else {
            Integer i = 5;
            do {
                if (units.contains(i)) {
                    return BioticUtils.getLengthInterval(i);
                }
                if (i == 1) {
                    i = 7;
                } else {
                    i--;
                }
            } while (i != 5);
        }
        return 1d;
    }
}
