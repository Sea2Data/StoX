package adapters;

import java.math.BigDecimal;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author kjetilf
 */
public class BigDecimalAdapter extends XmlAdapter<String, BigDecimal> {

    @Override
    public BigDecimal unmarshal(String val) throws Exception {
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String marshal(BigDecimal val) throws Exception {
        if (val != null) {
            return val.toString();
        } else {
            return null;
        }
    }

}
