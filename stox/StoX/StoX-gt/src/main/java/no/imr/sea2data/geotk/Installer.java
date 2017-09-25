package no.imr.sea2data.geotk;

import java.util.Iterator;
import java.util.Set;
import javax.imageio.ImageIO;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.io.plugin.GeoTiffImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.referencing.CRS;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

/**
 * Manages a module's life cycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        String derbyLog = System.getProperty("java.io.tmpdir") + "derby.log";
        System.setProperty("derby.stream.error.file", derbyLog);

        Setup.initialize(null);

        //force netbeans platform classloader to load image readers.
        Registry.setDefaultCodecPreferences();
        WorldFileImageReader.Spi.registerDefaults(null);
        WorldFileImageWriter.Spi.registerDefaults(null);
        GeoTiffImageReader.Spi.registerDefaults(null);
        ImageIO.scanForPlugins();

        //allow aproximative reprojection when bursa-wolf parameters are missing
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);

        //load derby jdbc driver used by epsg database
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }

        //force loading/creating epsg database.
        try {
            // BOTTLENECK 14 % of startup
            CRS.decode("EPSG:3395");
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        //force loading featurestore factories in this modules classloader
        final Set<FeatureStoreFactory> sdsf = FeatureStoreFinder.getAvailableFactories(null);
        final Iterator<FeatureStoreFactory> ite = sdsf.iterator();
        while (ite.hasNext()) {
            ite.next();
        }

    }

}
