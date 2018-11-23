package no.imr.stox.functions.landing;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.XMLReader;
import no.imr.stox.bo.landing.FiskeLinje;
import no.imr.stox.bo.landing.SluttSeddel;

/**
 * Reader that extends XMLReader for reading biotic xml files coming from S2D
 * Editor
 *
 * @author aasmunds
 * @author sjurl
 */
public class LandingXMLReader extends XMLReader {

    /**
     * Fishstations read.
     */
    private Integer currentYear;
    private final List<SluttSeddel> landings;
    private SluttSeddel currentSluttSeddel;

    /**
     * Constructor that takes a list to populate with fishstations as input.
     *
     * @param stations List to populate.
     */
    public LandingXMLReader(final List<SluttSeddel> stations) {
        this.landings = stations;
    }

    @Override
    protected void onObjectValue(final Object object, final String key, final String value) {
        if (object instanceof SluttSeddel) {
            createSluttSeddel(object, key, value);
        } else if (object instanceof FiskeLinje) {
            createFiskelinje(object, key, value);
        }
    }

    @Override
    protected Object getObject(final Object current, final String elmName) {
        Object result = null;
        if (current == null && elmName.equalsIgnoreCase("data")) {
            currentYear = Conversion.safeStringtoIntegerNULL(getCurrentAttributeValue("fangstAar"));
            result = landings;
        } else if (current instanceof List && elmName.equalsIgnoreCase("sluttseddel")) {
            currentSluttSeddel = new SluttSeddel(currentYear, getCurrentAttributeValue("id"));
            landings.add(currentSluttSeddel);
            result = currentSluttSeddel;
        } else if (current instanceof SluttSeddel && elmName.equalsIgnoreCase("linje")) {
            SluttSeddel sluttSeddel = (SluttSeddel) current;
            FiskeLinje fl = new FiskeLinje(currentSluttSeddel, getCurrentAttributeValue("id"));
            sluttSeddel.getFiskelinjer().add(fl);
            result = fl;
        }
        return result;
    }

    /**
     * Sets an attribute on a fishstation object.
     *
     * @param object The fishstation object
     * @param key The attribute being set
     * @param value The value being set
     */
    static DateFormat dateFormat = IMRdate.getDateFormat("yyyy-MM-dd");

    Date getLandingDateTime(String landingDate) {
        try {
            return dateFormat.parse(landingDate);
        } catch (ParseException ex) {
            return null;
        }
    }

    private void createSluttSeddel(final Object object, final String key, final String value) {
        SluttSeddel bo = (SluttSeddel) object;
        if (key.equalsIgnoreCase("doktype")) {
            bo.setDokType(Conversion.safeStringtoInteger(value));
        } else if (key.equalsIgnoreCase("sltsnr")) {
            bo.setSltsNr(value);
        } else if (key.equalsIgnoreCase("formulardato")) {
            bo.setFormularDato(getLandingDateTime(value));
        } else if (key.equalsIgnoreCase("salgslag")) {
            bo.setSalgslag(value);
        } else if (key.equalsIgnoreCase("salgslagorgnr")) {
            bo.setSalgslagOrgnr(value);
        } else if (key.equalsIgnoreCase("kjoporgnr")) {
            bo.setKjopOrgnr(value);
        } else if (key.equalsIgnoreCase("kjopland")) {
            bo.setKjopLand(value);
        } else if (key.equalsIgnoreCase("fiskerkomm")) {
            bo.setFiskerKomm(value);
        } else if (key.equalsIgnoreCase("fiskerland")) {
            bo.setFiskerLand(value);
        } else if (key.equalsIgnoreCase("fiskermantall")) {
            bo.setFiskerManntall(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equalsIgnoreCase("fartregm")) {
            bo.setFartRegm(value);
        } else if (key.equalsIgnoreCase("fartland")) {
            bo.setFartLand(value);
        } else if (key.equalsIgnoreCase("farttype")) {
            bo.setFartType(value);
        } else if (key.equalsIgnoreCase("antmann")) {
            bo.setAntMann(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equalsIgnoreCase("kvotetype")) {
            bo.setKvoteType(value);
        } else if (key.equalsIgnoreCase("sistefangstdato")) {
            bo.setSisteFangstDato(getLandingDateTime(value));
        } else if (key.equalsIgnoreCase("fangstregion")) {
            bo.setFangstRegion(value);
        } else if (key.equalsIgnoreCase("fangstkysthav")) {
            bo.setFangstKystHav(value);
        } else if (key.equalsIgnoreCase("fangsthomr")) {
            bo.setFangstHomr(value);
        } else if (key.equalsIgnoreCase("fangstlok")) {
            bo.setFangstLok(value);
        } else if (key.equalsIgnoreCase("fangstsone")) {
            bo.setFangstSone(value);
        } else if (key.equalsIgnoreCase("redskap")) {
            bo.setRedskap(value);
        } else if (key.equalsIgnoreCase("kvoteland")) {
            bo.setKvoteLand(value);
        } else if (key.equalsIgnoreCase("fiskedager")) {
            bo.setFiskedager(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equalsIgnoreCase("landingsdato")) {
            bo.setLandingsDato(getLandingDateTime(value));
        } else if (key.equalsIgnoreCase("landingsmottak")) {
            bo.setLandingsMottak(value);
        } else if (key.equalsIgnoreCase("landingskomm")) {
            bo.setLandingsKomm(value);
        } else if (key.equalsIgnoreCase("landingsland")) {
            bo.setLandingsLand(value);
        }
    }

    /**
     * Sets an attribute on a sample object.
     *
     * @param object The sample object
     * @param key The attribute being set
     * @param value The value being set
     */
    private void createFiskelinje(final Object object, final String key, final String value) {
        FiskeLinje bo = (FiskeLinje) object;
        if (key.equalsIgnoreCase("fisk")) {
            bo.setFisk(value);
        } else if (key.equalsIgnoreCase("konservering")) {
            bo.setKonservering(value);
        } else if (key.equalsIgnoreCase("tilstand")) {
            bo.setTilstand(value);
        } else if (key.equalsIgnoreCase("kvalitet")) {
            bo.setKvalitet(value);
        } else if (key.equalsIgnoreCase("anvendelse")) {
            bo.setAnvendelse(value);
        } else if (key.equalsIgnoreCase("prodvekt")) {
            bo.setProdVekt(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equalsIgnoreCase("rundvekt")) {
            bo.setRundVekt(Conversion.safeStringtoDoubleNULL(value));
        }
    }
}
