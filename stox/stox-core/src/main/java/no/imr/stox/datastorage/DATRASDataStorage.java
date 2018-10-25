/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Arrays;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrmap.layer.AreaUnits;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author aasmunds
 */
public class DATRASDataStorage extends FileDataStorage {

//  ALK age length key
//
//HH Meta data
//Ices tables
//TS_Ship
//58G2 G.O Sars
//JHJ  Johan Hjort
//
//Gear
//GOV (Grande Ouverture Verticale IBTS)
//SweepLngt  60
//GearExeption B=Bobbins used, D=Double sweeps, S=Standard)
//Doortype = P Polyvalent
//Redskap 3191 (Enkel sveiper 60 m, inkl hanefot, standard stender 2 m
//Redskap 3191 (Enkel sveiper 60 m, inkl hanefot, stender 1.8 m
//
//HL
//HaulNo StationNo
//Year Moht Day StartDate
//TimeShot StartTime HHMM
//Stratum 9 (unknown)
//HaulDur StopTime-StartTime in minutes
//DayNight D
//ShootLat/ShootLon Start Lat/Lon
//HaulLat/HaulLon StopLat/Lon
//StatRec (Statistical Rectangle) Område/Lokasjon
//TotalNo = 60/(StopTime - StartTime) * Fangst antall
//NoMeas = Ind.prøve antall
//CatCatchWgt = 60/(StopTime - StartTime) * Fangst vekt
//LengthClass Length group in cm
//HLNoAtLength = # ind in Length class * (Fangst ant / LPrøve ant.) * 60/(StopTime - StartTime)
    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((List<FishstationBO>) data, level, wr);
    }

    @Override
    public Integer getNumDataStorageFiles() {
        return 3;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        switch (idxFile) {
            case 1:
                return "HH";
            case 2:
                return "HL";
            case 3:
                return "CA";
        }
        return "";
    }

    public static String getTSCountryByIOC(String nation) {
        switch (nation) {
            case "58":
                return "NOR";
        }
        return "";
    }

    public static String getTSShipByPlatform(String platform) {
        switch (platform) {
            case "4174":
                return "58G2";
            case "1019":
                return "JHJ";
            case "1002":
                return "HAV";
        }
        return "";
    }

    public static Integer getGOVSweepByEquipment(String eqp) {
        if(eqp == null) return -9;
        switch (eqp) {
            case "3120":
                return -9;
            case "3190":
                return 60;
            case "3191":
                return 60;
            case "3192":
                return 60;
            case "3193":
                return 110;
            case "3194":
                return 110;
            case "3195":
                return 110;
            case "3196":
                return 60;
            case "3197":
                return 110;
            case "3198":
                return 60;
        }
        return null;
    }

    public static String getGearExp(Integer sweep, Integer year, Integer serialNo, Double depth) {
        if (year != null && serialNo != null && depth != null) {
            if (year == 2011 && serialNo > 24362 && depth >= 70 || year == 2012
                    || year == 2011 && serialNo >= 24135 && depth >= 70) {
                return "ST"; // Strapping
            }
        }
        //switch (sweep) {
        //    case 110:
        //        return "D";
        //    default:
        //        return "S";
        //}
        return "S";
    }
    /*
     ICES survey protocol for IBTS
     Size grades
     http://www.ices.dk/sites/pub/Publication%20Reports/ICES%20Survey%20Protocols%20%28SISP%29/SISP1-IBTSVIII.pdf     
     
     Datras FAQ - information about subsampling and sizegrades
     http://www.ices.dk/marine-data/Documents/DATRAS/DATRAS_FAQs.pdf
    
     Different size grades are summed up as CPUE's
     HlNoAtLngt will be derived either from 1 the individuals (if present) or 2 the catch count. This is the if-then-else in the loop.
     The raising factor (SubFac) is calculated differently in those 2 cases. In the first case from the individuals 
     as 60 / minutes * catch weight / lengthsampleweight, But in the second case as 60 / minutes.
    
     Jennifers comment on C or R DataType reporting:
     All years up to 2015, the catch was not fully sorted. They would pull out large individuals and take a 'representative subsample' of the rest to sort. From Q1 2015, I've been trying to change to match the manual - but old habits are still sometimes used in the fishlab...
     This is a bit of a nightmare. I suggest we report DataType C for now because of inconsistent practices in the fishlab on these surveys.
     Once I get a common 'new' procedure up and engrained, we can discuss DataType = R.
     We will need a code in S2D then, to assist with this.
     So that we can identify hauls were fish were subsampled vs entire catch sorted
    
    
     */

    public static void asTable(List<FishstationBO> list, Integer level, Writer wr) {
        switch (level) {
            case 1:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.csv("RecordType", "Quarter", "Country", "Ship", "Gear",
                        "SweepLngt", "GearExp", "DoorType", "StNo", "HaulNo", "Year", "Month", "Day",
                        "TimeShot", "Stratum", "HaulDur", "DayNight", "ShootLat", "ShootLong", "HaulLat", "HaulLong",
                        "StatRec", "Depth", "HaulVal", "HydroStNo", "StdSpecRecCode", "BycSpecRecCode", "DataType", "Netopening",
                        "Rigging", "Tickler", "Distance", "Warpingt", "Warpdia", "WarpDen", "DoorSurface", "DoorWgt",
                        "DoorSpread", "WingSpread", "Buoyancy", "KiteDim", "WgtGroundRope", "TowDir", "GroundSpeed",
                        "SpeedWater", "SurCurDir", "SurCurSpeed", "BotCurDir", "BotCurSpeed", "WindDir", "WindSpeed",
                        "SwellDir", "SwellHeight", "SurTemp", "BotTemp", "SurSal", "BotSal", "ThermoCline", "ThClineDepth")));
                for (FishstationBO fs : list) {
                    Integer sweep = getGOVSweepByEquipment(fs.getGear());
                    if (sweep == null) { // Sweep filter
                        // other gears have sweep == null...
                        continue;
                    }
                    // Filter other station types that blank (vanlig)
                    if (!(fs.getStationtype() == null || fs.getStationtype().equals(""))) {
                        continue;
                    }
                    if(fs.getStationstartdate() == null)
                        continue;
                    Integer year = IMRdate.getYear(fs.getStationstartdate(), true);
                    Integer month = IMRdate.getMonth(fs.getStationstartdate(), true);
                    Integer day = IMRdate.getDayOfMonth(fs.getStationstartdate(), true);
                    Integer quarter = (month - 1) / 3 + 1;
                    // Invalid also less/more than 5..90 min. og uten for 62 grader, evt kvartal.
                    String haulVal = (fs.getGearcondition() == null || fs.getGearcondition().equals("1") || fs.getGearcondition().equals("2"))
                            && (fs.getSamplequality() == null || fs.getSamplequality().equals("0") || fs.getSamplequality().equals("1")) ? "V" : "I";

                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.csv(// RecordType
                            "HH",
                            // Quarter
                            quarter,
                            // Country
                            getTSCountryByIOC(fs.getNation()), //TODO reference list
                            // Ship
                            getTSShipByPlatform(fs.getCatchplatform()), // TODO reference list
                            // Gear
                            "GOV", // TODO reference list
                            // SweepLngth
                            sweep,
                            // GearExp
                            getGearExp(sweep, fs.getYear(), fs.getSerialnumber(), fs.getBottomdepthstart()), // TODO: S=Single, D=Double, -9 not given
                            // DoorType
                            "P", fs.getSerialnumber(),
                            // HaulNo
                            fs.getStation(),
                            // Year
                            year,
                            // Month
                            month,
                            // Day
                            day,
                            // TimeShot
                            StringUtils.leftPad(IMRdate.formatDate(fs.getStationstarttime(), "HHmm"), 4, '0'),
                            // Stratum
                            -9,
                            // HaulDur
                            Math.round(IMRdate.minutesDiffD(IMRdate.encodeDate(fs.getStationstartdate(), fs.getStationstarttime()), IMRdate.encodeDate(fs.getStationstopdate(), fs.getStationstoptime()))),
                            // DayNight
                            // 15 minutes before  official sunrise, 15 min after official sunset.
                            IMRdate.isDayTime(IMRdate.encodeDate(fs.getStationstartdate(), fs.getStationstarttime()), fs.getLatitudestart(),
                                    fs.getLongitudestart()) ? "D" : "N",
                            // ShootLat
                            unkD(fs.getLatitudestart(), "0.0000"),
                            // ShootLong
                            unkD(fs.getLongitudestart(), "0.0000"),
                            // HaulLat
                            unkD(fs.getLatitudeend(), "0.0000"),
                            // HaulLong
                            unkD(fs.getLongitudeend(), "0.0000"),
                            // StatRec
                            AreaUnits.getFDOmrLokFromPos(fs.getLatitudestart(), fs.getLongitudestart()),
                            // Depth
                            Math.round(fs.getBottomdepthstart()),
                            //HaulVal
                            haulVal,
                            // TODO stasjon : match på logg mot toktlogger 5 nm, tid.
                            // , DoorSpread, fra Biotic, Warplngth=wire, TowDir
                            // HydroStNo 
                            -9,
                            // StdSpecRecCode
                            1,
                            // BycSpecRecCode
                            1,
                            // DataType
                            "R",
                            fs.getVerticaltrawlopening(),
                            // Rigging
                            -9,
                            // Tickler
                            -9,
                            // Distance
                            unkO(fs.getDistance() != null ? fs.getDistance() * 1852 : null),
                            // Warplngt
                            unkO(fs.getwirelength()),
                            //Warpdia
                            -9,
                            //Warpden
                            -9,
                            //Doorsurface
                            4.46,
                            //DoorWgt
                            1075,
                            //DoorSpread
                            unkD(fs.getTrawldoorspread(), "0.0"),
                            //WingSpread
                            -9,
                            //Buoyancy
                            -9,
                            //KiteDim
                            0.85,
                            //WgtGroundRope
                            -9,
                            // Tow dir
                            unkO(fs.getDirection() != null ? Math.round(fs.getDirection()) : null),
                            // Ground speed (speed of trawl over ground)
                            unkD(fs.getVesselspeed(), "0.0"),
                            //Speed water
                            -9,
                            //SurCurDir
                            -9,
                            //SurCurSpeed
                            -9,
                            //BotCurDir
                            -9,
                            //BotCurSpeed
                            -9,
                            //WinDir
                            -9,
                            //WindSpeed
                            -9,
                            //SwellDir
                            -9,
                            //SwellHeight
                            -9,
                            //SurTemp
                            -9,
                            //BotTemp
                            -9,
                            //SurSal
                            -9,
                            //BotSal
                            -9,
                            //ThermoCline
                            -9,
                            //ThClineDepth
                            -9)));
                }
                break;

            case 2:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.csv("RecordType", "Quarter", "Country", "Ship", "Gear",
                        "SweepLngt", "GearExp", "DoorType", "StNo", "HaulNo", "Year", "SpecCodeType", "SpecCode", "SpecVal", "Sex", "TotalNo",
                        "CatIdentifier", "NoMeas", "SubFactor", "SubWgt", "CatCatchWgt", "LngtCode", "LngtClass", "HLNoAtLngt")));
                for (FishstationBO fs : list) {
                    Integer sweep = getGOVSweepByEquipment(fs.getGear());
                    if (sweep == null) { // Sweep filter
                        continue;
                    }
                    if(fs.getStationstartdate() == null)
                        continue;
                    Integer year = IMRdate.getYear(fs.getStationstartdate(), true);
                    Integer month = IMRdate.getMonth(fs.getStationstartdate(), true);
                    Integer quarter = (int) Math.ceil(month / 3.0);
                    String haulVal = (fs.getGearcondition() == null || fs.getGearcondition().equals("1") || fs.getGearcondition().equals("2"))
                            && (fs.getSamplequality() == null || fs.getSamplequality().equals("0") || fs.getSamplequality().equals("1")) ? "V" : "I";

                    for (CatchBO c : fs.getCatchBOs()) {

                        // IU: Use aphia for comparison and add crustacean boolean
                        boolean isHerringOrSprat = c.getAphia().equals("126417") || c.getAphia().equals("126425");

                        List<String> crustList = Arrays.asList( "107275", "107276", "107369", "107253", "107703", "107704", "107350", "107254", "107205", "140712", "140687", "140658" );
                        boolean isCrustacean = crustList.contains(c.getAphia());

                        for (SampleBO s : c.getSampleBOs()) {

                            //Double raiseFac = 60.0 / IMRdate.minutesDiffD(IMRdate.encodeDate(fs.getStationstartdate(), fs.getStationstarttime()), IMRdate.encodeDate(fs.getStationstopdate(), fs.getStationstoptime()));
                            MatrixBO hlNoAtLngth = new MatrixBO();
                            MatrixBO lsCountTot = new MatrixBO();

                            //Double sampleFac = s.getCatchcount().doubleValue() / s.getLengthsamplecount();
                            //if (s.getWeight() == null || s.getCatchcount() == null) {
                            //    continue;
                            //}

                            Integer specVal = haulVal.equals("I") ? 0 :
                                s.getCatchcount() != null && s.getLengthsamplecount() != null && s.getCatchweight() != null ? 1 :
                                s.getCatchcount() != null && s.getLengthsamplecount() == null && s.getCatchweight() == null ? 4 :
                                s.getCatchcount() == null && s.getLengthsamplecount() == null && s.getCatchweight() != null ? 6 :
                                s.getCatchcount() != null && s.getLengthsamplecount() == null && s.getCatchweight() != null ? 7 :
                                haulVal.equals("V") && s.getCatchcount() == null && s.getLengthsamplecount() == null && s.getCatchweight() == null ? 5 :
                                s.getCatchcount() != null && s.getLengthsamplecount() != null && s.getCatchweight() == null ? 0 : -9;

                            String lngtCode = s.getSampletype() != null ? isCrustacean ? "."/*1mm*/ : isHerringOrSprat ? "0"/*5mm*/ : "1"/*1cm*/ : "-9"/*1cm*/;
                            Integer lenInterval = lngtCode.equals("0") ? 5 : 1;
                            Boolean reportInMM = !lngtCode.equals("1");

                            Double catCatchWgt = (s.getCatchweight() != null ? s.getCatchweight() : 0) * 1000;

                            // If weight is below 1, raise it into 1
                            if(catCatchWgt < 1 && catCatchWgt > 0)
                                catCatchWgt = Math.ceil(catCatchWgt) * 1.0;

                            Double subWeight = (s.getlengthsampleweight() != null ? s.getlengthsampleweight() : 0) * 1000;

                            // If weight is below 1, raise it into 1
                            if(subWeight < 1 && subWeight > 0)
                                subWeight = Math.ceil(subWeight) * 1.0;

                            if (s.getIndividualBOs().isEmpty() || s.getlengthsampleweight() == null) {
                                String lngtClass = "-9";
                                String sex = "-9";
                                hlNoAtLngth.addGroupRowColValue(c.getAphia(), sex, lngtClass, (s.getCatchcount() != null ? s.getCatchcount() : 0) * 1.0);
                                lsCountTot.addGroupRowColValue(c.getAphia(), sex, lngtClass, (s.getLengthsamplecount() != null ? s.getLengthsamplecount() : 0) * 1.0);
                            } else {
                                //if (s.getlengthsampleweight() == null) {
                                //    continue;
                                //}
                                Double sampleFac = s.getCatchweight() / s.getlengthsampleweight();
                                for (IndividualBO i : s.getIndividualBOs()) {
                                    Double length = i.getLength();/*cm*/;
                                    if (i.getLength() == null) {
                                        continue;
                                    }
                                    if (reportInMM) {
                                        if (length > 0) length *= 10;
                                    } else {
                                        // Some species have very small length in cm, use mm instead
                                        if (length < 1) {
                                            lngtCode = ".";
                                            lenInterval = 1;
                                            length *= 10;
                                            reportInMM = true;
                                        }
                                    }
                                    String lngtClass = "" + ImrMath.trunc(length, lenInterval.doubleValue());
                                    String sex = i.getSex() == null || i.getSex().trim().isEmpty()
                                            ? "-9" : i.getSex().equals("1") ? "F" : "M";
                                    hlNoAtLngth.addGroupRowColValue(c.getAphia(), sex, lngtClass, 1.0 * sampleFac);
                                    lsCountTot.addGroupRowColValue(c.getAphia(), sex, lngtClass, 1.0);
                                }
                            }
                            // Group Sex and Length class and report counts at catch level (raw)
                            String aphia = (String) c.getAphia();
                            for (String sex : hlNoAtLngth.getGroupRowKeys(aphia)) {
                                Double totalNo = hlNoAtLngth.getGroupRowValueAsMatrix(aphia, sex).getSum();
                                Double noMeas = lsCountTot.getGroupRowValueAsMatrix(aphia, sex).getSum();
                                Double subFactor = (totalNo != 0 && noMeas != 0) ? totalNo / noMeas : -9;

                                // If subFactor == 1, we can use catCatchWgt
                                if(subFactor == 1)
                                    subWeight = catCatchWgt;

                                for (String lngtClass : hlNoAtLngth.getGroupRowColKeys(aphia, sex)) {
                                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.csv(
                                        "HL",
                                        quarter,
                                        getTSCountryByIOC(fs.getNation()),
                                        getTSShipByPlatform(fs.getCatchplatform()),
                                        "GOV",
                                        sweep,
                                        "S",
                                        "P",
                                        fs.getSerialnumber(),
                                        fs.getStation(),
                                        year,
                                        "W", // Worms
                                        aphia,
                                        specVal,
                                        sex,
                                        totalNo == 0 ? -9 : unkD(totalNo, "0.00"), // n per Hour
                                        s.getCatchpartnumber(), //CatIdentifier
                                        noMeas == 0 ? -9 : Math.round(noMeas), // n measured as individual
                                        unkD(subFactor, "0.0000"), // SubFactor
                                        subWeight == 0 ? -9 : Math.round(subWeight),
                                        catCatchWgt == 0 ? -9 : Math.round(catCatchWgt), /* g */
                                        lngtCode,
                                        lngtClass,
                                        lsCountTot.getGroupRowColValueAsDouble(aphia, sex, lngtClass) > 0 ? unkD(lsCountTot.getGroupRowColValueAsDouble(aphia, sex, lngtClass), "0.00") : -9.00 )));
                                }
                            }
                        }
                    }
                }
                break;

            case 3:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.csv("RecordType", "Quarter", "Country", "Ship", "Gear",
                        "SweepLngt", "GearExp", "DoorType", "StNo", "HaulNo", "Year", "SpecCodeType", "SpecCode", "AreaType", "AreaCode", "LngtCode",
                        "LngtClass", "Sex", "Maturity", "PlusGr", "AgeRings", "CANoAtLngt", "IndWgt")));
                for (FishstationBO fs : list) {
                    Integer sweep = getGOVSweepByEquipment(fs.getGear());
                    if (sweep == null) { // Sweep filter
                        continue;
                    }
                    if(fs.getStationstartdate() == null)
                        continue;
                    Integer year = IMRdate.getYear(fs.getStationstartdate(), true);
                    Integer month = IMRdate.getMonth(fs.getStationstartdate(), true);
                    Integer quarter = (int) Math.ceil(month / 3.0);
                    String areaLoc = fs.getArea() != null && fs.getLocation() != null ? fs.getArea() + fs.getLocation() : "";

                    for (CatchBO c : fs.getCatchBOs()) {
                        // IU: Use aphia for comparison and add crustacean boolean
                        boolean isHerringOrSprat = c.getAphia().equals("126417") || c.getAphia().equals("126425");

                        List<String> crustList = Arrays.asList( "107275", "107276", "107369", "107253", "107703", "107704", "107350", "107254", "107205", "140712", "140687", "140658" );
                        boolean isCrustacean = crustList.contains(c.getAphia());

                        for (SampleBO s : c.getSampleBOs()) {
                            if (s.getIndividualBOs().isEmpty()) {
                                continue;
                            }
                            MatrixBO nInd = new MatrixBO();
                            MatrixBO nWithWeight = new MatrixBO();
                            MatrixBO totWeight = new MatrixBO();
                            String lngtCode = s.getSampletype() != null ? isCrustacean ? "."/*1mm*/ : isHerringOrSprat ? "0"/*5mm*/ : "1"/*1cm*/ : "-9"/*1cm*/;
                            Integer lenInterval = lngtCode.equals("0") ? 5 : 1;
                            Boolean reportInMM = !lngtCode.equals("1");
                            for (IndividualBO i : s.getIndividualBOs()) {
                                Double length = i.getLength();/*cm*/;
                                if (i.getLength() == null) {
                                    continue;
                                }
                                if (reportInMM) {
                                    if (length > 0) length *= 10;
                                } else {
                                    // Some species have very small length in cm, use mm instead
                                    if (length < 1) {
                                        lngtCode = ".";
                                        lenInterval = 1;
                                        length *= 10;
                                        reportInMM = true;
                                    }
                                }
                                String lngtClass = "" + ImrMath.trunc(length, lenInterval.doubleValue());
                                String sex = i.getSex() == null || i.getSex().trim().isEmpty() ? "-9" : i.getSex().equals("1") ? "F" : "M";
                                String maturity = getDATRASMaturity(i);
                                String age = i.getAge() != null ? i.getAge() + "" : "-9";
                                nInd.addGroupRowColCellValue(lngtClass, sex, maturity, age, 1.0);
                                if (i.getWeight()/*g*/ != null) {
                                    nWithWeight.addGroupRowColCellValue(lngtClass, sex, maturity, age, 1.0);
                                    totWeight.addGroupRowColCellValue(lngtClass, sex, maturity, age, i.getWeight());
                                }
                            }
                            // Group Length class - Sex - Maturity - Age and report meanweight and category count at individual level
                            for (String lngtClass : nInd.getKeys()) {
                                for (String sex : nInd.getGroupRowKeys(lngtClass)) {
                                    for (String maturity : nInd.getGroupRowColKeys(lngtClass, sex)) {
                                        for (String age : nInd.getGroupRowColCellKeys(lngtClass, sex, maturity)) {
                                            Long n = Math.round(nInd.getGroupRowColCellValueAsDouble(lngtClass, sex, maturity, age));
                                            Double nWithW = nWithWeight.getGroupRowColCellValueAsDouble(lngtClass, sex, maturity, age);
                                            Double totW = totWeight.getGroupRowColCellValueAsDouble(lngtClass, sex, maturity, age);
                                            Double meanW = null;
                                            if (nWithW != null) {
                                                meanW = totW / nWithW;
                                            }
                                            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.csv(
                                                    "CA",
                                                    quarter,
                                                    getTSCountryByIOC(fs.getNation()),
                                                    getTSShipByPlatform(fs.getCatchplatform()),
                                                    "GOV",
                                                    sweep,
                                                    "S",
                                                    "P",
                                                    fs.getSerialnumber(),
                                                    fs.getStation(),
                                                    year,
                                                    "W", // Worms
                                                    c.getAphia(),
                                                    0,
                                                    areaLoc,
                                                    lngtCode,
                                                    lngtClass,
                                                    sex,
                                                    maturity,
                                                    -9,
                                                    age,
                                                    n,
                                                    unkD(meanW, "0.0"))));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private static String getDATRASMaturity(IndividualBO i) {
        CatchBO c = i.getSample().getCatchBO();
        String noName = c.getCommonname().toUpperCase();
        boolean isHerringOrSpratOrMackerel = c.getAphia().equals("126417") || c.getAphia().equals("126425") || c.getAphia().equals("127023");

        Integer res = -9;
        if (i.getSpecialstage() != null) {
            Integer sp = Conversion.safeStringtoIntegerNULL(i.getSpecialstage());
            if (sp != null) {
                if (isHerringOrSpratOrMackerel) {
                    res = sp <= 2 ? 61 : sp <= 5 ? 62 : 60 + sp - 3;
                } else {
                    res = 60 + sp;
                }
            }
        } else if (i.getMaturationstage() != null) {
            FishstationBO fs = c.getStationBO();
            Integer month = IMRdate.getMonth(fs.getStationstartdate(), true);
            Integer quarter = month / 4 + 1;
            Integer st = Conversion.safeStringtoIntegerNULL(i.getMaturationstage());
            if (st != null) {
                res = 60 + st;
                if (st == 5 && quarter == 3) {
                    res = -9;
                }
            }
        }
        return res + "";
    }

    private static String unkD(Double d, String format) {
        return unkO(d != null ? (new DecimalFormat(format)).format(d) : null);
    }

    private static String unkO(Object o) {
        return o != null ? o.toString() : "-9";
    }
}
