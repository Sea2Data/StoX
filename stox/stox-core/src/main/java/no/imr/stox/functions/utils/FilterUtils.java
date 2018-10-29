package no.imr.stox.functions.utils;

import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.bo.landing.FiskeLinje;
import no.imr.stox.bo.landing.SluttSeddel;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

/**
 * This class should contain all data attribute mappings that a user can use to
 * filter data on a given data set.
 *
 * @author esmaelmh
 */
public final class FilterUtils {

    public static boolean evaluateExpr(Object o, Expression expr) {
        JexlContext context = new MapContext();
        resolveContext(context, o);
        return Boolean.TRUE.equals(expr.evaluate(context));
    }

    public static boolean evaluate(JexlContext context, Expression expr) {
        return Boolean.TRUE.equals(expr.evaluate(context));
    }

    static public void resolveContext(JexlContext context, Object o) {
        // Field according to IFishStationBO
        if (o instanceof FishstationBO) {
            FishstationBO fs = (FishstationBO) o;
            context.set("fs", fs);
            context.set("missiontype", fs.getMission().getMissiontype());
            context.set("cruise", fs.getMission().getCruise());
            context.set("platform", fs.getFs().getCatchplatform());
            context.set("nation", fs.getFs().getNation());
            context.set("platform", fs.getFs().getCatchplatform());
            context.set("startdate", fs.getFs().getStationstartdate());
            context.set("station", fs.getFs().getStation());
            context.set("serialno", fs.getFs().getSerialnumber());
            context.set("fishstationtype", fs.getFs().getStationtype());
            context.set("latitudestart", fs.getFs().getLatitudestart());
            context.set("longitudestart", fs.getFs().getLongitudestart());
            context.set("system", fs.getFs().getSystem());
            context.set("area", fs.getFs().getArea());
            context.set("location", fs.getFs().getLocation());
            context.set("bottomdepthstart", fs.getFs().getBottomdepthstart());
            context.set("bottomdepthstop", fs.getFs().getBottomdepthstop());
            context.set("equipmentnumber", fs.getFs().getGearno());
            context.set("gearno", fs.getFs().getGearno());
            context.set("gear", fs.getFs().getGear());
            context.set("gearcount", fs.getFs().getGearcount());
            context.set("directiongps", fs.getFs().getDirection());
            context.set("gearspeed", fs.getFs().getVesselspeed());
            context.set("starttime", fs.getFs().getStationstarttime());
            context.set("startlog", fs.getFs().getLogstart());
            context.set("stoptime", fs.getFs().getStationstoptime());
            context.set("distance", fs.getFs().getDistance());
            context.set("gearcondition", fs.getFs().getGearcondition());
            context.set("trawlquality", fs.getFs().getSamplequality());
            context.set("fishingdepthmax", fs.getFs().getFishingdepthmax());
            context.set("fishingdepthmin", fs.getFs().getFishingdepthmin());
            context.set("fishingdepthcount", fs.getFs().getFishingdepthcount());
            context.set("trawlopening", fs.getFs().getVerticaltrawlopening());
            context.set("trawlopeningSD", fs.getFs().getVerticaltrawlopeningsd());
            context.set("doorspread", fs.getFs().getTrawldoorspread());
            context.set("doorspreadSD", fs.getFs().getTrawldoorspreadsd());
            context.set("wirelength", fs.getFs().getWirelength());
            context.set("soaktime", fs.getFs().getSoaktime());
            /*context.set("flowcount", fs.getFs().getFlowCount());
            context.set("flowconst", fs.getFs().getFlowConst());*/
            context.set("comment", fs.getFs().getStationcomment());
            // Derived attributes
            context.set("serialno", fs.getFs().getSerialnumber());
            context.set("stationtype", fs.getFs().getStationtype());
            // period as integer: 20140101
            if (fs.getFs().getStationstartdate() != null) {
                context.set("year", IMRdate.getYear(fs.getFs().getStationstartdate()));
                Integer period = IMRdate.getYear(fs.getFs().getStationstartdate()) * 10000 + IMRdate.getMonth(fs.getFs().getStationstartdate()) * 100
                        + IMRdate.getDayOfMonth(fs.getFs().getStationstartdate());
                context.set("period", period);
            }
        } else if (o instanceof CatchSampleBO) {
            CatchSampleBO cs = (CatchSampleBO) o;
            addCatchFilter(context, cs);
            context.set("samplenumber", cs.getCs().getCatchpartnumber());
            context.set("sampletype", cs.getCs().getSampletype());
            context.set("group", cs.getCs().getGroup());
            context.set("conservation", cs.getCs().getConservation());
            context.set("measurement", cs.getCs().getCatchproducttype());
            context.set("weight", cs.getCs().getCatchweight());
            context.set("count", cs.getCs().getCatchcount());
            context.set("samplemeasurement", cs.getCs().getSampleproducttype());
            context.set("lengthmeasurement", cs.getCs().getLengthmeasurement());
            context.set("lengthsampleweight", cs.getCs().getLengthsampleweight());
            context.set("lengthsamplecount", cs.getCs().getLengthsamplecount());
            context.set("individualsamplecount", cs.getCs().getSpecimensamplecount());
            context.set("agesample", cs.getCs().getAgingstructure());
            context.set("parasite", cs.getCs().getParasite());
            context.set("stomach", cs.getCs().getStomach());
            context.set("genetics", cs.getCs().getTissuesample());
            context.set("nonbiological", cs.getCs().getForeignobject());
            context.set("comment", cs.getCs().getCatchcomment());
        } else if (o instanceof IndividualBO) {
            IndividualBO ii = (IndividualBO) o;
            if (ii.getCatchSample() != null) {
                CatchSampleBO cs = ii.getCatchSample();
                addCatchFilter(context, cs);
            }
            // grams and cm filter
            context.set("weight", ii.getIndividualweightG());
            context.set("length", ii.getLengthCM());

            // old field names support
            context.set("no", ii.getI().getSpecimenid());
            context.set("weightmethod", ii.getI().getIndividualproducttype());
            context.set("lengthunit", ii.getI().getLengthresolution());
            context.set("stage", ii.getI().getMaturationstage());
            context.set("digestdeg", ii.getI().getDigestion());
            context.set("vertebrae", ii.getI().getVertebraecount());
            context.set("comment", ii.getI().getIndividualcomment());

            // age determination filter on individual
            context.set("age", ii.getAge());
            context.set("spawningage", ii.getSpawningage());
            context.set("spawningzones", ii.getSpawningzones());
            context.set("readability", ii.getReadability());
            context.set("otolithtype", ii.getOtolithtype());
            context.set("otolithedge", ii.getOtolithedge());
            context.set("otolithcentre", ii.getOtolithcentre());
            context.set("calibration", ii.getCalibration());
            
            // Individual field names
            context.set("fat", ii.getI().getFat());
            context.set("sex", ii.getI().getSex());
            context.set("specialstage", ii.getI().getSpecialstage());
            context.set("stomachfillfield", ii.getI().getStomachfillfield());
            context.set("liver", ii.getI().getLiver());
            context.set("liverparasite", ii.getI().getLiverparasite());
            context.set("gillworms", ii.getI().getGillworms());
            context.set("swollengills", ii.getI().getSwollengills());
            context.set("fungusheart", ii.getI().getFungusheart());
            context.set("fungusspores", ii.getI().getFungusspores());
            context.set("fungusouter", ii.getI().getFungusouter());
            context.set("blackspot", ii.getI().getBlackspot());
            context.set("gonadweight", ii.getI().getGonadweight());
            context.set("liverweight", ii.getI().getLiverweight());
            context.set("stomachweight", ii.getI().getStomachweight());
        } else if (o instanceof DistanceBO) {
            DistanceBO d = (DistanceBO) o;
            if (d.getStart_time() != null) {
                Integer period = IMRdate.getYear(d.getStart_time(), true) * 10000 + IMRdate.getMonth(d.getStart_time(), true) * 100 + IMRdate.getDayOfMonth(d.getStart_time(), true);
                context.set("period", period);
            }
            context.set("log", d.getLog_start());
            context.set("log_start", d.getLog_start());
            context.set("bot_ch_thickness", d.getBot_ch_thickness());
            context.set("cruise", d.getCruise());
            context.set("integrator_dist", d.getIntegrator_dist());
            context.set("lat_start", d.getLat_start());
            context.set("lat_stop", d.getLat_stop());
            context.set("lon_start", d.getLon_start());
            context.set("lon_stop", d.getLon_stop());
            context.set("pel_ch_thickness", d.getPel_ch_thickness());
        } else if (o instanceof FrequencyBO) {
            FrequencyBO f = (FrequencyBO) o;
            context.set("frequency", f.getFreq());
            context.set("freq", f.getFreq());
            context.set("transceiver", f.getTranceiver());
            context.set("bubble_corr", f.getBubble_corr());
            context.set("lower_integrator_depth", f.getLower_integrator_depth());
            context.set("lower_interpret_depth", f.getLower_interpret_depth());
            context.set("max_bot_depth", f.getMax_bot_depth());
            context.set("min_bot_depth", f.getMin_bot_depth());
            context.set("num_bot_ch", f.getNum_bot_ch());
            context.set("num_pel_ch", f.getNum_pel_ch());
            context.set("quality", f.getQuality());
            context.set("threshold", f.getThreshold());
            context.set("upper_integrator_depth", f.getUpper_integrator_depth());
            context.set("upper_interpret_depth", f.getUpper_interpret_depth());
        } else if (o instanceof SABO) {
            SABO sa = (SABO) o;
            context.set("acocat", sa.getAcoustic_category());
            context.set("acoustic_Category", sa.getAcoustic_category());
            context.set("chtype", sa.getCh_type());
            context.set("ch_type", sa.getCh_type());
            context.set("ch", sa.getCh());
            context.set("sa", sa.getSa());
        } else if (o instanceof SluttSeddel) {
            SluttSeddel sl = (SluttSeddel) o;
            context.set("fangstaar", sl.getFangstAar());
            context.set("doktype", sl.getDokType());
            context.set("sltsnr", sl.getSltsNr());
            context.set("formulardato", sl.getFormularDato());
            context.set("salgslag", sl.getSalgslag());
            context.set("salgslagorgnr", sl.getSalgslagOrgnr());
            context.set("kjoporgnr", sl.getKjopOrgnr());
            context.set("kjopkundenr", sl.getKjopOrgnr());
            context.set("kjopland", sl.getKjopLand());
            context.set("fiskerkomm", sl.getFiskerKomm());
            context.set("fiskerland", sl.getFiskerLand());
            context.set("fiskermantall", sl.getFiskerManntall());
            context.set("fartregm", sl.getFartRegm());
            context.set("fartland", sl.getFartLand());
            context.set("farttype", sl.getFartType());
            context.set("antmann", sl.getAntMann());
            context.set("kvotetype", sl.getKvoteType());
            context.set("sistefangstdato", sl.getSisteFangstDato());
            context.set("fangstregion", sl.getFangstRegion());
            context.set("fangstkysthav", sl.getFangstKystHav());
            context.set("fangsthomr", sl.getFangstHomr());
            context.set("fangstlok", sl.getFangstLok());
            context.set("fangstsone", sl.getFangstSone());
            context.set("redskap", sl.getRedskap());
            context.set("kvoteland", sl.getKvoteLand());
            context.set("fiskedager", sl.getFiskedager());
            context.set("landingsdato", sl.getLandingsDato());
            // Helpers
            context.set("landingskvartal", IMRdate.getQuarter(sl.getLandingsDato()));
            context.set("landingsmnd", IMRdate.getMonth(sl.getLandingsDato(), true));
            context.set("landingsuke", IMRdate.getWeek(sl.getLandingsDato()));
            context.set("sistefangstkvartal", IMRdate.getQuarter(sl.getSisteFangstDato()));
            context.set("sistefangstmnd", IMRdate.getMonth(sl.getSisteFangstDato(), true));
            context.set("sistefangstuke", IMRdate.getWeek(sl.getSisteFangstDato()));

            context.set("landingsmottak", sl.getLandingsMottak());
            context.set("landingskomm", sl.getLandingsKomm());
            context.set("landingsland", sl.getLandingsLand());
        } else if (o instanceof FiskeLinje) {
            FiskeLinje fl = (FiskeLinje) o;
            context.set("id", fl.getFisk());
            context.set("fisk", fl.getFisk());
            /*
            0611 SILD    
            061101 NVG sild
            061104 Northsee sild
            615 BRISLING   
            1022 TORSK
            1027 HYSE
            1032 SEI    
            1038 KOLMULE
            2013 MAKRELL
             */
            context.set("konservering", fl.getKonservering());
            context.set("tilstand", fl.getTilstand());
            context.set("kvalitet", fl.getKvalitet());
            context.set("anvendelse", fl.getAnvendelse());
            context.set("prodvekt", fl.getProdVekt());
            context.set("rundvekt", fl.getRundVekt());
        }
    }

    /*public static List<Object> copyBOList(List<Object> list, Object parent) {
        List li = new ArrayList<>();
        for (Object o : list) {
            try {
                Object o2 = o.getClass().getConstructor().newInstance();
                li.add(o2);
                for (Method m : o.getClass().getMethods()) {
                    if (m.getName().startsWith("set") && m.getParameters().length == 1) {
                        Parameter p = m.getParameters()[0];
                        Method getter;
                        try {
                            getter = o.getClass().getMethod("get" + m.getName().substring(3));
                        } catch (NoSuchMethodException ex) {
                            continue;
                        }
                        Object v;
                        if (parent != null && p.getType().equals(parent.getClass())) {
                            v = parent;
                        } else {
                            v = getter.invoke(o);
                            if (p.getType().isAssignableFrom(List.class)) {
                                v = copyBOList((List)v, o2);
                            }
                        }
                        if (v != null) {
                            m.invoke(o2, v);
                        }
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | InstantiationException | NoSuchMethodException ex) {
                Logger.getLogger(FilterBiotic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return li;
    }*/
    private static void addCatchFilter(JexlContext context, CatchSampleBO cs) {
        if (cs == null) {
            return;
        }
        context.set("species", cs.getCs().getCatchcategory() != null ? cs.getCs().getCatchcategory().toLowerCase() : null);
        context.set("noname", cs.getCs().getCommonname() != null ? cs.getCs().getCommonname().toLowerCase() : null);
        context.set("aphia", cs.getCs().getAphia());
    }
}
