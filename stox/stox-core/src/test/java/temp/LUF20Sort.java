package temp;

import java.util.List;
import java.util.stream.Collectors;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.functions.acoustic.ReadAcousticXML;
import no.imr.stox.functions.utils.EchosounderUtils;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author aasmunds
 */
public class LUF20Sort {

    @Test
    public void readwriteLuf20() {
        String f1 = "C:\\Users\\aasmunds\\workspace\\stox\\project\\Varanger Stad Northeast Arctic saithe acoustic index in autumn 2009\\input\\acoustic\\echosounder_cruiseNumber_2009703_Jan+Mayen-dx.xml";
        String f2 = "C:\\Users\\aasmunds\\workspace\\stox\\project\\Varanger Stad Northeast Arctic cod acoustic index in autumn 2009\\input\\acoustic\\echosounder_cruiseNumber_2009703_Jan+Mayen.xml";
        System.out.println("File 1 (dx) = " + f1);
        System.out.println("File 2 (edited) = " + f2);
        List<DistanceBO> dl1 = ReadAcousticXML.perform(f1);
        dl1.stream()
                .sorted((d1, d2) -> d1.getStart_time().compareTo(d2.getStart_time()))
                .collect(Collectors.toList());
        List<DistanceBO> dl2 = ReadAcousticXML.perform(f2);
        dl2.stream()
                .sorted((d1, d2) -> d1.getStart_time().compareTo(d2.getStart_time()))
                .collect(Collectors.toList());
        dl1.stream()
                .filter(d -> EchosounderUtils.findDistance(dl2, d.getKey()) == null)
                .forEach(d->evaluateByTime(d, dl2, "1", "2", true));
        dl2.stream()
                .filter(d -> EchosounderUtils.findDistance(dl1, d.getKey()) == null)
                .forEach(d->evaluateByTime(d, dl1, "2", "1", false));
    }

    void evaluateByTime(DistanceBO dist, List<DistanceBO> dists, String searchFile1, String searchFile2, Boolean testLogChange) {
        DistanceBO d1 = dists.stream().filter(d -> d.getStart_time().equals(dist.getStart_time())).findFirst().orElse(null);
        if (d1 == null) {
            System.out.println("Timestamp " + IMRdate.formatDate(dist.getStart_time(), "yyyy-MM-dd/HH:mm:ss") + " in file " + searchFile1 + " not found in file " + searchFile2);
        } else if (testLogChange && !d1.getLog_start().equals(dist.getLog_start())) {
            System.out.println("Timestamp " + IMRdate.formatDate(d1.getStart_time(), "yyyy-MM-dd/HH:mm:ss") + " with log " + dist.getLog_start() + " in file "
                    + searchFile1 + " changed to log " + d1.getLog_start() + " in file " + searchFile2);
        }
    }
}
