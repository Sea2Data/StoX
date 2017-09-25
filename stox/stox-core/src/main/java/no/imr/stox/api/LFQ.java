package no.imr.stox.api;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: hva er dette?
 *
 * @author Åsmund
 */
public class LFQ implements ILFQ {

    String lengthDistType;
    Double lengthInterval;

    public LFQ(String lengthDistType, Double lengthInterval) {
        this.lengthDistType = lengthDistType;
        this.lengthInterval = lengthInterval;
    }

    private final Map<String, Double> lfq = new HashMap<>();

    @Override
    public String getLengthDistType() {
        return lengthDistType;
    }

    @Override
    public Double getLengthInterval() {
        return lengthInterval;
    }

    @Override
    public Map<String, Double> getLFQ() {
        return lfq;
    }

}
