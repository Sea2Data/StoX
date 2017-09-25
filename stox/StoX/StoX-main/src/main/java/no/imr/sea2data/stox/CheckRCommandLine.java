/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;

/**
 *
 * @author aasmunds
 */
//@ServiceProvider(service = OptionProcessor.class)
public class CheckRCommandLine extends OptionProcessor {

    //Here we specify "runAction" as the new key in the command,
    //but it could be any other string you like, of course:
    private static final Option rCheck = Option.withoutArgument(Option.NO_SHORT_NAME, "rcheck");

    @Override
    public Set<org.netbeans.spi.sendopts.Option> getOptions() {
        HashSet set = new HashSet();
        //      set.add(always);
        set.add(rCheck);
        //      set.add(xDebug);
        return set;
    }

    @Override
    protected void process(Env env, Map<Option, String[]> values) {
  /*      File f = RUtils.getRBinFolder(true);
        if (f == null) {
            JOptionPane.showMessageDialog(null, "R is not installed or registered properly in the system path. R models will not be available", "Warning", JOptionPane.WARNING_MESSAGE);
        }*/
    }

}
