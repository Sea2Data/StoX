package no.imr.sea2data.stox;

import no.imr.stox.util.base.VersionUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.openide.modules.ModuleInstall;

/**
 * work done when module is installed and ready to use
 *
 * @author trondwe
 */
public class Installer extends ModuleInstall {

    public static final String APPVERSION = "2.7.9";

    @Override
    public void restored() {
        VersionUtil.setAppVersion(APPVERSION);
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
    }

    @Override
    public boolean closing() {
        return DirtyChecker.canContinueIfDirty();
    }
}
