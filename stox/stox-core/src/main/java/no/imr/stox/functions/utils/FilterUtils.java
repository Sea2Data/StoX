package no.imr.stox.functions.utils;

import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
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
            context.set("missiontype", fs.getMissiontype());
            context.set("cruise", fs.getCruise());
            context.set("platform", fs.getCatchplatform());
            context.set("nation", fs.getNation());
            context.set("platform", fs.getCatchplatform());
            context.set("startdate", fs.getStationstartdate());
            context.set("station", fs.getStation());
            context.set("serialno", fs.getSerialnumber());
            context.set("fishstationtype", fs.getStationtype());
            context.set("latitudestart", fs.getLatitudestart());
            context.set("longitudestart", fs.getLongitudestart());
            context.set("system", fs.getSystem());
            context.set("area", fs.getArea());
            context.set("location", fs.getLocation());
            context.set("bottomdepthstart", fs.getBottomdepthstart());
            context.set("bottomdepthstop", fs.getBottomdepthstop());
            context.set("equipmentnumber", fs.getGearno());
            context.set("gearno", fs.getGearno());
            context.set("gear", fs.getGear());
            context.set("gearcount", fs.getGearcount());
            context.set("directiongps", fs.getDirection());
            context.set("gearspeed", fs.getVesselspeed());
            context.set("starttime", fs.getStationstarttime());
            context.set("startlog", fs.getLogstart());
            context.set("stoptime", fs.getStationstoptime());
            context.set("distance", fs.getDistance());
            context.set("gearcondition", fs.getGearcondition());
            context.set("trawlquality", fs.getSamplequality());
            context.set("fishingdepthmax", fs.getFishingdepthmax());
            context.set("fishingdepthmin", fs.getFishingdepthmin());
            context.set("fishingdepthcount", fs.getFishingdepthcount());
            context.set("trawlopening", fs.getVerticaltrawlopening());
            context.set("trawlopeningSD", fs.getVerticaltrawlopeningsd());
            context.set("doorspread", fs.getTrawldoorspread());
            context.set("doorspreadSD", fs.getTrawldoorspreadsd());
            context.set("wirelength", fs.getwirelength());
            context.set("soaktime", fs.getSoaktime());
            /*context.set("flowcount", fs.getFlowCount());
            context.set("flowconst", fs.getFlowConst());*/
            context.set("comment", fs.getComment());
            // Derived attributes
            context.set("serialno", fs.getSerialnumber());
            context.set("stationtype", fs.getStationtype());
            // period as integer: 20140101
            if (fs.getStationstartdate() != null) {
                context.set("year", IMRdate.getYear(fs.getStationstartdate(), true));
                Integer period = IMRdate.getYear(fs.getStationstartdate(), true) * 10000 + IMRdate.getMonth(fs.getStationstartdate(), true) * 100
                        + IMRdate.getDayOfMonth(fs.getStationstartdate(), true);
                context.set("period", period);
            }
        } else if (o instanceof CatchBO) {
            CatchBO cs = (CatchBO) o;
            addCatchFilter(context, cs);
        } else if (o instanceof SampleBO) {
            SampleBO cs = (SampleBO) o;
            CatchBO css = cs.getCatchBO();
            addCatchFilter(context, css);
            context.set("samplenumber", cs.getCatchpartnumber());
            context.set("sampletype", cs.getSampletype());
            context.set("group", cs.getGroup());
            context.set("conservation", cs.getConservation());
            context.set("measurement", cs.getCatchproducttype());
            context.set("weight", cs.getCatchweight());
            context.set("count", cs.getCatchcount());
            context.set("samplemeasurement", cs.getSampleproducttype());
            context.set("lengthmeasurement", cs.getLengthmeasurement());
            context.set("lengthsampleweight", cs.getlengthsampleweight());
            context.set("lengthsamplecount", cs.getLengthsamplecount());
            context.set("individualsamplecount", cs.getSpecimentsamplecount());
            context.set("agesample", cs.getAgingstructure());
            context.set("parasite", cs.getParasite());
            context.set("stomach", cs.getStomach());
            context.set("genetics", cs.getGenetics());
            context.set("nonbiological", cs.getForeignobject());
            context.set("comment", cs.getCatchcomment());
        } else if (o instanceof IndividualBO) {
            IndividualBO ii = (IndividualBO) o;
            if (ii.getSample() != null) {
                CatchBO cs = ii.getSample().getCatchBO();
                addCatchFilter(context, cs);
            }
            context.set("weight", ii.getWeight());
            context.set("length", ii.getLength());
            context.set("fat", ii.getFat());
            context.set("sex", ii.getSex());

            context.set("no", ii.getSpecimenid());
            context.set("weightmethod", ii.getIndividualproducttype());
            context.set("lengthunit", ii.getLengthresolution());
            //context.set("developmentalstage", ii.getDevelopmentalStage());
            context.set("stage", ii.getMaturationstage());
            context.set("specialstage", ii.getSpecialstage());
            context.set("stomachfillfield", ii.getStomachfillfield());
            context.set("digestdeg", ii.getDigestion());
            context.set("liver", ii.getLiver());
            context.set("liverparasite", ii.getLiverparasite());
            context.set("gillworms", ii.getGillworms());
            context.set("swollengills", ii.getSwollengills());
            context.set("fungusheart", ii.getFungusheart());
            context.set("fungusspores", ii.getFungusspores());
            context.set("fungusouter", ii.getFungusouter());
            context.set("blackspot", ii.getBlackspot());
            context.set("vertebrae", ii.getvertebraecount());
            context.set("gonadweight", ii.getGonadweight());
            context.set("liverweight", ii.getLiverweight());
            context.set("stomachweight", ii.getStomachweight());
            context.set("comment", ii.getIndividualcomment());
            context.set("age", ii.getAge());
            context.set("spawningage", ii.getSpawningage());
            context.set("spawningzones", ii.getSpawningzones());
            context.set("readability", ii.getReadability());
            context.set("otolithtype", ii.getOtolithtype());
            context.set("otolithedge", ii.getOtolithedge());
            context.set("otolithcentre", ii.getOtolithcentre());
            context.set("calibration", ii.getCalibration());
        } else if (o instanceof DistanceBO) {
            DistanceBO d = (DistanceBO) o;
            if (d.getStart_time() != null) {
                Integer period = IMRdate.getYear(d.getStart_time(), true) * 10000 + IMRdate.getMonth(d.getStart_time(), true) * 100 + IMRdate.getDayOfMonth(d.getStart_time(), true);
                context.set("period", period);
            }
            context.set("log", d.getLog_start().doubleValue());
            context.set("log_start", d.getLog_start().doubleValue());
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
    private static void addCatchFilter(JexlContext context, CatchBO cs) {
        if (cs == null) {
            return;
        }
        context.set("species", cs.getCatchcategory()!= null ? cs.getCatchcategory().toLowerCase() : null);
        context.set("noname", cs.getCommonname() != null ? cs.getCommonname().toLowerCase() : null);
        context.set("aphia", cs.getAphia());
    }
}
