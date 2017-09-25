/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore
public class ReadVintertoktStrata {

    @Test
    public void read() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("F:\\tokt\\vintertokt\\Vinterstrata96.csv"))) {
            String line = br.readLine();
            line = br.readLine(); // read header
            Map<Integer, List<String>> strm = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] s = line.split(",");
                int stratum = Integer.valueOf(s[0]);
                List<String> l = strm.get(stratum);
                if (l == null) {
                    l = new ArrayList<>();
                    strm.put(stratum, l);
                }
                Double lat = Double.valueOf(s[1]);
                Double lon = Double.valueOf(s[2]);
                l.add(lon.toString() + " " + lat.toString());
            }
            List<Integer> keys = new ArrayList<>(strm.keySet());
            Collections.sort(keys);
            for (Integer i : keys) {
                String res = i + "\tMULTIPOLYGON(((";
                List<String> l = strm.get(i);
                for (String li : l) {
                    res += li + ", ";
                }
                res += l.get(0) + ")))";
                System.out.println(res);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadVintertoktStrata.class.getName()).log(Level.SEVERE, null, ex);
        }
//1	MULTIPOLYGON (((4.4897 62, -3.0384 62, -0.7956 63.6773, -0.4907 63.8871, -0.4887 64.4328, -0.468656139 64.444533694, -0.443692722 64.45887375, -0.418524361 64.473151167, -0.393151639 64.487365361, -0.370120278 64.500115667, -0.112238833 64.641148694, 0 64.7017, 0 70, 17.9377 70, 17.772876139 69.918748444, 17.769885278 69.917214528, 17.76315275 69.913377139, 17.590087333 69.806102028, 17.340443389 69.649109056, 17.298823917 69.640649667, 16.85615275 69.549641083, 16.41547325 69.476439222, 15.956259889 69.39843025, 15.947232944 69.396771194, 15.936332417 69.394459917, 15.92590175 69.391894333, 15.849707778 69.371732417, 15.445304917 69.263523417, 14.604834722 69.126178167, 14.281788111 68.989100111, 13.962742972 68.851418222, 13.961901917 68.851051139, 13.954134444 68.847555139, 13.946633528 68.843984361, 13.939404611 68.840341417, 13.932452889 68.836629028, 13.925783306 68.832849917, 13.919400722 68.829006889, 13.913309611 68.82510275, 13.907514361 68.821140361, 13.902019056 68.817122694, 13.896827583 68.813052694, 13.891943583 68.808933306, 13.8036805 68.731315528, 13.5810985 68.595208889, 13.3324015 68.440806167, 13.277511111 68.426054389, 12.886055861 68.319765806, 12.882747278 68.318850556, 12.873175139 68.316100444, 12.863809556 68.31325525, 12.854657389 68.310317083, 12.845725361 68.307288111, 12.83702 68.304170583, 12.828547611 68.300966833, 12.820314417 68.297679167, 12.812326389 68.294310083, 12.804589306 68.290862028, 12.7134585 68.249005389, 12.709227722 68.247026639, 12.70192175 68.24345625, 12.694880472 68.239813694, 12.688108972 68.236101667, 12.681612139 68.232322889, 12.675394611 68.228480167, 12.669460806 68.224576306, 12.663815 68.220614194, 12.602675917 68.176164361, 12.385018194 68.016327528, 12.383589778 68.015260778, 12.378409389 68.011220222, 12.373523667 68.007128611, 12.368936083 68.002988889, 12.364649861 67.998804139, 12.360667917 67.994577444, 12.356993083 67.990311889, 12.343892417 67.974358444, 12.204296833 67.802927806, 12.124243444 67.779693222, 11.750310639 67.670026889, 11.747205611 67.66909975, 11.738068444 67.666268, 11.729137778 67.663342972, 11.687803278 67.6494145, 11.680764944 67.646987528, 11.566350667 67.606547139, 11.562366222 67.605116972, 11.553968806 67.601980111, 11.545798722 67.598757528, 11.537861861 67.595451611, 11.530164056 67.592064778, 11.522710861 67.588599556, 11.515507667 67.585058472, 11.508559722 67.581444194, 11.501871972 67.577759333, 11.49544925 67.574006639, 11.489296194 67.570188889, 11.483417167 67.566308861, 11.477816389 67.562369417, 11.472497833 67.558373472, 11.467465278 67.554324, 11.46272225 67.550223917, 11.458272139 67.54607625, 11.454118 67.541884083, 11.45026275 67.537650444, 11.446709056 67.5333785, 11.443459361 67.529071306, 11.404899639 67.475110139, 11.403486972 67.473077861, 11.40070125 67.468724028, 11.398223889 67.464342861, 11.39808125 67.464073056, 11.396147556 67.460175861, 11.394255833 67.455752, 11.392676639 67.451310111, 11.391410889 67.4468535, 11.390459417 67.442385361, 11.389822667 67.437909, 11.389500972 67.433427639, 11.389494361 67.428944611, 11.389802694 67.424463111, 11.390425528 67.419986472, 11.391362278 67.415517889, 11.392612028 67.411060639, 11.39417375 67.406618, 11.396046111 67.402193139, 11.398227583 67.397789333, 11.400716389 67.393409722, 11.403510583 67.3890575, 11.406607972 67.384735833, 11.428419889 67.355788528, 11.428528472 67.355644444, 11.431928611 67.351357194, 11.4356265 67.347106778, 11.439619278 67.342896306, 11.470254639 67.311845722, 11.634246583 67.144147417, 11.795974028 66.976279167, 11.955484056 66.808244528, 11.716802139 66.701851556, 11.714336222 66.700735694, 11.707035417 66.697308778, 11.699972917 66.693804722, 11.693153778 66.690226167, 11.686582972 66.686575722, 11.68026525 66.682856056, 11.674205083 66.679069944, 11.668406889 66.675220194, 11.662874778 66.671309583, 11.657612694 66.667341028, 11.652624389 66.663317444, 11.647913389 66.659241778, 11.643483028 66.655117028, 11.504526444 66.519924361, 11.335943083 66.353747028, 11.169573528 66.187386667, 11.168004694 66.185782556, 11.164121083 66.181582611, 11.160520583 66.17734175, 11.157205667 66.173063056, 11.154178639 66.168749667, 11.151441556 66.16440475, 11.078360167 66.040843972, 10.976397583 65.866372028, 10.875806556 65.691829583, 10.811862306 65.625331556, 10.6529695 65.458489639, 10.497850833 65.309389222, 10.328674722 65.144648167, 10.161581028 64.979715694, 10.160884583 64.979019194, 10.156910444 64.9748625, 10.153203889 64.970661806, 10.149767528 64.966420194, 10.086016194 64.883847056, 10.084151167 64.881357222, 10.081166556 64.877055861, 10.078457139 64.87272175, 10.076024722 64.868358083, 10.073870944 64.863968056, 10.071997278 64.859554889, 10.070404944 64.855121778, 10.070299611 64.854799389, 9.822462028 64.737269028, 9.543756083 64.603370111, 9.267779583 64.468949056, 8.994499917 64.334014028, 8.836916333 64.275703778, 8.524396917 64.158825111, 8.214500778 64.041281833, 8.2074925 64.038536722, 8.200043361 64.035462139, 8.192787889 64.032300167, 8.185731417 64.029053139, 8.176331722 64.024607528, 8.1701835 64.021627083, 8.1635205 64.018224972, 8.157070889 64.014744889, 8.150839389 64.011189444, 8.004217722 63.924799972, 7.7621235 63.780657167, 7.522487361 63.636110556, 7.521636667 63.635592, 7.515819528 63.631927833, 7.510228417 63.628194944, 7.504867361 63.624396083, 7.420104722 63.56232275, 7.361604444 63.526649556, 7.125626278 63.381596889, 6.892017278 63.236156306, 6.673020972 63.163027528, 6.355483833 63.055731361, 6.040280111 62.94772825, 6.038276361 62.947031889, 6.030549667 62.944250417, 6.022992139 62.941374528, 6.015609278 62.938406361, 6.008406472 62.935348139, 6.001389028 62.932202056, 5.998791167 62.93100525, 5.723090139 62.803160944, 5.718985167 62.801213472, 5.533501528 62.711366417, 5.266438861 62.580603972, 5.001714278 62.449335083, 4.999199972 62.44806725, 4.992783583 62.444707222, 4.98656675 62.441267889, 4.980553972 62.437751806, 4.974749667 62.434161528, 4.969157944 62.43049975, 4.963782917 62.426769111, 4.755925306 62.277278667, 4.754833056 62.276483417, 4.749777028 62.272665472, 4.744945278 62.268785167, 4.740341306 62.264845333, 4.735968389 62.260848889, 4.731829667 62.25679875, 4.576823694 62.098749667, 4.57546775 62.097341778, 4.57168925 62.093219833, 4.490415278 62.000833444, 4.490176944 62.000560972, 4.4897 62)))
    }

}
