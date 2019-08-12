/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import LandingsTypes.v2.ArtType;
import LandingsTypes.v2.DellandingType;
import LandingsTypes.v2.FangstdataType;
import LandingsTypes.v2.FartøyType;
import LandingsTypes.v2.FiskerType;
import LandingsTypes.v2.KvoteType;
import LandingsTypes.v2.LandingOgProduksjonType;
import LandingsTypes.v2.LandingsdataType;
import LandingsTypes.v2.MottakendeFartøyType;
import LandingsTypes.v2.MottakerType;
import LandingsTypes.v2.ProduktType;
import LandingsTypes.v2.RedskapType;
import LandingsTypes.v2.SalgslagdataType;
import LandingsTypes.v2.SeddellinjeType;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.imr.sea2data.biotic.bo.BaseBO;
import no.imr.stox.util.base.ExportUtil;
import no.imr.stox.util.base.IMRdate;
import no.imr.stox.util.base.ImrIO;
import no.imr.stox.bo.LandingCovDataMatrix;
import no.imr.stox.bo.landing.LandingsdataBO;
import no.imr.stox.bo.landing.SeddellinjeBO;
import no.imr.stox.functions.utils.ReflectionUtil;

/**
 *
 * @author aasmunds
 */
public class LandingCovDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        LandingCovDataMatrix flData = (LandingCovDataMatrix) data;
        String temporalHdr = ExportUtil.tabbed("Temporal", "GearFactor", "Spatial");//seasonal ? ExportUtil.tabbed("Year", "Season") : ExportUtil.tabbed("Temporal");
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(temporalHdr, BaseBO.csvHdr(SeddellinjeType.class, true, null))));
        for (String cov : flData.getData().getSortedRowKeys()) {
            String[] keys = cov.split("/");
            String context = ExportUtil.tabbed(keys[0], keys[1], keys[2]/*, keys[3]*/);
            List<SeddellinjeBO> flList = (List<SeddellinjeBO>) flData.getData().getRowValue(cov);
            if (flList == null) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(context));
            } else {
                for (SeddellinjeBO sl : flList) {
                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(context, sl.csv(true, null))));
                }
            }
        }
    }
}
