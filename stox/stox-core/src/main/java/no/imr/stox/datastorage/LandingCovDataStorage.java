/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.List;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.LandingCovDataMatrix;
import no.imr.stox.bo.landing.FiskeLinje;
import no.imr.stox.bo.landing.SluttSeddel;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.CovariateUtils;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class LandingCovDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        LandingCovDataMatrix flData = (LandingCovDataMatrix) data;
        String temporalHdr = ExportUtil.tabbed("Temporal");//seasonal ? ExportUtil.tabbed("Year", "Season") : ExportUtil.tabbed("Temporal");
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(temporalHdr, "GearFactor", "Spatial", /*"PlatformFactor", */"fangstaar", "doktype", "sltsnr", "formulardato", "salgslag", "salgslagorgnr", "kjoporgnr",
                "kjopkundenr", "kjopland", "fiskerkomm", "fiskerland", "fiskermantall", "fartregm", "fartland", "farttype", "antmann", "kvotetype", "sistefangstdato", "fangstregion",
                "fangstkysthav", "fangsthomr", "fangstlok", "fangstsone", "redskap", "kvoteland", "fiskedager", "landingsdato", "landingsmottak", "landingskomm", "landingsland",
                "fisk", "konservering", "tilstand", "kvalitet", "anvendelse", "prodvekt", "rundvekt")));
        for (String cov : flData.getData().getSortedRowKeys()) {
            String[] keys = cov.split("/");
            String context = ExportUtil.tabbed(keys[0], keys[1], keys[2]/*, keys[3]*/);
            List<FiskeLinje> flList = (List<FiskeLinje>) flData.getData().getRowValue(cov);
            if (flList == null) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(context));
            } else {
                for (FiskeLinje fl : flList) {
                    SluttSeddel sl = fl.getSluttSeddel();
                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(context, sl.getFangstAar(), sl.getDokType(), sl.getSltsNr(), IMRdate.formatDate(sl.getFormularDato()), sl.getSalgslag(), sl.getSalgslagOrgnr(),
                            sl.getKjopOrgnr(), sl.getKjopOrgnr(), sl.getKjopLand(), sl.getFiskerKomm(), sl.getFiskerLand(), sl.getFiskerManntall(),
                            sl.getFartRegm(), sl.getFartLand(), sl.getFartType(), sl.getAntMann(), sl.getKvoteType(), IMRdate.formatDate(sl.getSisteFangstDato()),
                            sl.getFangstRegion(), sl.getFangstKystHav(), sl.getFangstHomr(), sl.getFangstLok(), sl.getFangstSone(), sl.getRedskap(), sl.getKvoteLand(), sl.getFiskedager(),
                            IMRdate.formatDate(sl.getLandingsDato()), sl.getLandingsMottak(), sl.getLandingsKomm(), sl.getLandingsLand(),
                            // Fiskelinje:
                            fl.getFisk(), fl.getKonservering(), fl.getTilstand(), fl.getKvalitet(), fl.getAnvendelse(), fl.getProdVekt(), fl.getRundVekt())));
                }
            }
        }
    }
}
