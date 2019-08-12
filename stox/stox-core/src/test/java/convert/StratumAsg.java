/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import no.imr.stox.util.base.Conversion;

/**
 *
 * @author aasmunds
 */
public class StratumAsg {

    String stratumKey;
    List<Integer> serialNo;

    public StratumAsg(Integer mission, String stratum, List<String> serialNo) {
        this.stratumKey = Util.getMissionStratumKey(mission, stratum);
        try {
            this.serialNo = serialNo.stream()
                    .map(Conversion::safeStringtoIntegerNULL)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    public String getStratumKey() {
        return stratumKey;
    }

    public List<Integer> getSerialNo() {
        return serialNo;
    }

}
