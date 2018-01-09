package no.imr.stox.functions.acoustic;

import java.util.List;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.PurposeBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.XMLReader;
import java.util.UUID;
import no.imr.sea2data.imrbase.math.Calc;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class EchoXMLReader extends XMLReader {

    private String currentChType = null;
    private String currentAcousticCategory = null;
    private String currentCruise;
    private String currentPlatform;
    private String currentNation;
    private final List<DistanceBO> distances;

    public EchoXMLReader(final List<DistanceBO> distances) {
        this.distances = distances;
    }

    /**
     * event called when a tag value is read
     *
     * @param object
     * @param key
     * @param value
     */
    @Override
    protected void onObjectValue(Object object, String key, String value) {
        if (key.equals("cruise")) {
            currentCruise = value;
        } else if (key.equals("platform")) {
            currentPlatform = value;
        } else if (key.equals("nation")) {
            currentNation = value;
        } else if (object instanceof DistanceBO) {
            DistanceBO d = (DistanceBO) object;
            d.setCruise(currentCruise);
            d.setNation(currentNation);
            d.setPlatform(currentPlatform);
            if (key.equals("log_start")) {
                Double val = Conversion.safeStringtoDouble(value);
                d.setLog_start(Calc.roundTo(val, 1));
            } else if (key.equals("start_time")) {
                // set seconds and miliseconds to value 0
                d.setStart_time(IMRdate.strToDateTime(value));
            } else if (key.equals("stop_time")) {
                d.setStop_time(IMRdate.strToDateTime(value));
            } else if (key.equals("integrator_dist")) {
                d.setIntegrator_dist(Calc.roundTo(Conversion.safeStringtoDoubleNULL(value), 8));
            } else if (key.equals("pel_ch_thickness")) {
                d.setPel_ch_thickness(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("bot_ch_thickness")) {
                d.setBot_ch_thickness(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("include_estimate")) {
                d.setInclude_estimate(Conversion.safeStringtoIntegerNULL(value));
            } else if (key.equals("lat_start")) {
                d.setLat_start(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("lat_stop")) {
                d.setLat_stop(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("lon_start")) {
                d.setLon_start(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("lon_stop")) {
                d.setLon_stop(Conversion.safeStringtoDoubleNULL(value));
            }
        } else if (object instanceof FrequencyBO) {
            FrequencyBO f = (FrequencyBO) object;
            if (key.equals("freq")) {
                f.setFreq(Conversion.safeStringtoInteger(value));
            } else if (key.equals("tranceiver") || key.equals("transceiver")) { // tackle former typo erratum 'tranceiver'
                f.setTranceiver(Conversion.safeStringtoInteger(value));
            } else if (key.equals("threshold")) {
                f.setThreshold(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("num_pel_ch")) {
                f.setNum_pel_ch(Conversion.safeStringtoIntegerNULL(value));
            } else if (key.equals("num_bot_ch")) {
                f.setNum_bot_ch(Conversion.safeStringtoIntegerNULL(value));
            } else if (key.equals("min_bot_depth")) {
                f.setMin_bot_depth(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("max_bot_depth")) {
                f.setMax_bot_depth(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("upper_interpret_depth")) {
                f.setUpper_interpret_depth(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("lower_interpret_depth")) {
                f.setLower_interpret_depth(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("upper_integrator_depth")) {
                f.setUpper_integrator_depth(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("lower_integrator_depth")) {
                f.setLower_integrator_depth(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("quality")) {
                f.setQuality(Conversion.safeStringtoIntegerNULL(value));
            } else if (key.equals("bubble_corr")) {
                f.setBubble_corr(Conversion.safeStringtoDoubleNULL(value));
            } else if (key.equals("type")) {
                currentChType = value;
            } else if (key.equals("acocat")) {
                currentAcousticCategory = value;
            }
        } else if (object instanceof SABO) {
            SABO sa = (SABO) object;
            if (key.equals("sa")) {
                // Ensure that . and , is supported when nasc is read
                sa.setSa(Conversion.safeStringtoDoubleNULLEnglishFormat(value));
            } else if (key.equals("ch")) {
                sa.setCh(Conversion.safeStringtoIntegerNULL(value));
                // Set the keys found at frequency level
                sa.setCh_type(currentChType);
                sa.setAcoustic_category(currentAcousticCategory);
            }
        } else if (object instanceof PurposeBO) {
            PurposeBO purpose = (PurposeBO) object;
            if (key.equals("acocat")) {
                purpose.setAcoustic_category(value);
            } else if (key.equals("purpose")) {
                purpose.setPurpose(Conversion.safeStringtoIntegerNULL(value));
            }
        }
    }

    /**
     * event called when an element is read
     *
     * @param current the current element read (parent)
     * @param elmName the name of the element
     * @return the new element read
     */
    @Override
    protected Object getObject(Object current, String elmName) {
        if (elmName.equals("distance")) {
            DistanceBO distance = new DistanceBO();
            // Set id
            distance.setId(UUID.randomUUID().toString().replace("-", "").toString());
            distances.add(distance);
            return distance;
        } else if (current instanceof DistanceBO && elmName.equals("frequency")) {
            DistanceBO e = (DistanceBO) current;
            FrequencyBO frequency = new FrequencyBO();
            frequency.setDistance(e);
            e.getFrequencies().add(frequency);
            return frequency;
        } else if (current instanceof FrequencyBO) {
            if (elmName.equals("ch_type") || elmName.equals("sa_by_acocat")) {
                return current;
            } else if (elmName.equals("sa")) {
                FrequencyBO e = (FrequencyBO) current;
                SABO sa = new SABO();
                sa.setFrequency(e);
                e.getSa().add(sa);
                return sa;
            }
        }
        return null;
    }
}
