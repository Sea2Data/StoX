package adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author kjetilf
 */
public class DoubleAdapter extends XmlAdapter<String, Double> {

    @Override
    public Double unmarshal(String val) throws Exception {
        try {
            return new Double(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String marshal(Double val) throws Exception {
        if (val != null) {
            return val.toString();
        } else {
            return null;
        }
    }

}
