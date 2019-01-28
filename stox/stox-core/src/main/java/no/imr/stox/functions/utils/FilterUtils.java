package no.imr.stox.functions.utils;

import LandingsTypes.v2.SeddellinjeType;
import java.lang.reflect.Field;
import java.util.List;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.bo.landing.SeddellinjeBO;
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
            context.set("missiontype", fs.getMission().bo().getMissiontype());
            context.set("cruise", fs.getMission().bo().getCruise());

            context.set("fs", fs);

            context.set("nation", fs.bo().getNation());
            context.set("station", fs.bo().getStation());
            context.set("latitudestart", fs.bo().getLatitudestart());
            context.set("longitudestart", fs.bo().getLongitudestart());
            context.set("system", fs.bo().getSystem());
            context.set("area", fs.bo().getArea());
            context.set("location", fs.bo().getLocation());
            context.set("bottomdepthstart", fs.bo().getBottomdepthstart());
            context.set("bottomdepthstop", fs.bo().getBottomdepthstop());
            context.set("gearno", fs.bo().getGearno());
            context.set("gear", fs.bo().getGear());
            context.set("gearcount", fs.bo().getGearcount());
            context.set("distance", fs.bo().getDistance());
            context.set("gearcondition", fs.bo().getGearcondition());
            context.set("fishingdepthmax", fs.bo().getFishingdepthmax());
            context.set("fishingdepthmin", fs.bo().getFishingdepthmin());
            context.set("fishingdepthcount", fs.bo().getFishingdepthcount());
            context.set("wirelength", fs.bo().getWirelength());
            context.set("soaktime", fs.bo().getSoaktime());
            context.set("stationtype", fs.bo().getStationtype());
            // old 
            context.set("platform", fs.bo().getCatchplatform());
            context.set("startdate", fs.bo().getStationstartdate());
            context.set("serialno", fs.bo().getSerialnumber());
            context.set("fishstationtype", fs.bo().getStationtype());
            context.set("equipmentnumber", fs.bo().getGearno());
            context.set("directiongps", fs.bo().getDirection());
            context.set("gearspeed", fs.bo().getGearflow());
            context.set("starttime", fs.bo().getStationstarttime());
            context.set("startlog", fs.bo().getLogstart());
            context.set("stoptime", fs.bo().getStationstoptime());
            context.set("trawlquality", fs.bo().getSamplequality());
            context.set("trawlopening", fs.bo().getVerticaltrawlopening());
            context.set("trawlopeningSD", fs.bo().getVerticaltrawlopeningsd());
            context.set("doorspread", fs.bo().getTrawldoorspread());
            context.set("doorspreadSD", fs.bo().getTrawldoorspreadsd());
            context.set("comment", fs.bo().getStationcomment());
            // period as integer: 20140101
            if (fs.bo().getStationstartdate() != null) {
                context.set("year", IMRdate.getYear(fs.bo().getStationstartdate()));
                Integer period = IMRdate.getYear(fs.bo().getStationstartdate()) * 10000 + IMRdate.getMonth(fs.bo().getStationstartdate()) * 100
                        + IMRdate.getDayOfMonth(fs.bo().getStationstartdate());
                context.set("period", period);
            }
        } else if (o instanceof CatchSampleBO) {
            CatchSampleBO cs = (CatchSampleBO) o;
            addCatchFilter(context, cs);
            context.set("sampletype", cs.bo().getSampletype());
            context.set("conservation", cs.bo().getConservation());
            context.set("group", cs.bo().getGroup());
            context.set("lengthmeasurement", cs.bo().getLengthmeasurement());
            context.set("lengthsampleweight", cs.bo().getLengthsampleweight());
            context.set("lengthsamplecount", cs.bo().getLengthsamplecount());
            context.set("parasite", cs.bo().getParasite());
            context.set("stomach", cs.bo().getStomach());

            context.set("samplenumber", cs.bo().getCatchpartnumber());
            context.set("measurement", cs.bo().getCatchproducttype());
            context.set("weight", cs.bo().getCatchweight());
            context.set("count", cs.bo().getCatchcount());
            context.set("samplemeasurement", cs.bo().getSampleproducttype());
            context.set("individualsamplecount", cs.bo().getSpecimensamplecount());
            context.set("agesample", cs.bo().getAgingstructure());
            context.set("genetics", cs.bo().getTissuesample());
            context.set("nonbiological", cs.bo().getForeignobject());
            context.set("comment", cs.bo().getCatchcomment());
        } else if (o instanceof IndividualBO) {
            IndividualBO ii = (IndividualBO) o;
            if (ii.getCatchSample() != null) {
                CatchSampleBO cs = ii.getCatchSample();
                addCatchFilter(context, cs);
            }
            // Individual field names
            context.set("fat", ii.bo().getFat());
            context.set("sex", ii.bo().getSex());
            context.set("specialstage", ii.bo().getSpecialstage());
            context.set("stomachfillfield", ii.bo().getStomachfillfield());
            context.set("liver", ii.bo().getLiver());
            context.set("liverparasite", ii.bo().getLiverparasite());
            context.set("gillworms", ii.bo().getGillworms());
            context.set("swollengills", ii.bo().getSwollengills());
            context.set("fungusheart", ii.bo().getFungusheart());
            context.set("fungusspores", ii.bo().getFungusspores());
            context.set("fungusouter", ii.bo().getFungusouter());
            context.set("blackspot", ii.bo().getBlackspot());
            context.set("gonadweight", ii.bo().getGonadweight());
            context.set("liverweight", ii.bo().getLiverweight());
            context.set("stomachweight", ii.bo().getStomachweight());

            // grams and cm filter
            context.set("weight", ii.bo().getIndividualweight());
            context.set("length", ii.bo().getLength());

            // old field names support
            context.set("no", ii.bo().getSpecimenid());
            context.set("weightmethod", ii.bo().getIndividualproducttype());
            context.set("lengthunit", ii.bo().getLengthresolution());
            context.set("stage", ii.bo().getMaturationstage());
            context.set("digestdeg", ii.bo().getDigestion());
            context.set("vertebrae", ii.bo().getVertebraecount());
            context.set("comment", ii.bo().getIndividualcomment());

            // age determination filter on individual
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
        } else if (o instanceof SeddellinjeBO) {
            SeddellinjeBO sl = (SeddellinjeBO) o;
            List<Field> fields = ReflectionUtil.getCompoundFields(SeddellinjeType.class);
            fields.forEach(f -> {
                String varName = f.getName().toLowerCase().replace("æ", "ae").replace("ø", "oe").replace("å", "aa").replace("Æ", "AE").replace("Ø", "OE").replace("Å", "AA");
                context.set(varName, ReflectionUtil.invoke(f, sl.bo(), true));
            });
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
        context.set("speccat", cs.getSpecCat());
        context.set("species", cs.bo().getCatchcategory() != null ? cs.bo().getCatchcategory().toLowerCase() : null);
        context.set("noname", cs.bo().getCommonname() != null ? cs.bo().getCommonname().toLowerCase() : null);
        context.set("aphia", cs.bo().getAphia());
    }
}
