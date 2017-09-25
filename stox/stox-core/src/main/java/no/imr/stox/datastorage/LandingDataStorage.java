/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.List;
import no.imr.stox.bo.landing.FiskeLinje;
import no.imr.stox.bo.landing.SluttSeddel;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class LandingDataStorage extends FileDataStorage {

    public LandingDataStorage() {
    }

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((List<SluttSeddel>) data, level, wr);
    }

    @Override
    public Integer getNumDataStorageFiles() {
        return 2;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        switch (idxFile) {
            case 1:
                return "SluttSeddel";
            case 2:
                return "FiskeLinje";
        }
        return "";
    }

    public static void asTable(List<SluttSeddel> list, Integer level, Writer wr) {
        switch (level) {
            case 1:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("fangstaar", "doktype", "sltsnr", "formulardato", "salgslag", "salgslagorgnr", "kjoporgnr",
                        "kjopkundenr", "kjopland", "fiskerkomm", "fiskerland", "fiskermantall", "fartregm", "fartland", "farttype", "antmann", "kvotetype", "sistefangstdato", "fangstregion",
                        "fangstkysthav", "fangsthomr", "fangstlok", "latitude", "longitude", "stratum", "fangstsone", "redskap", "kvoteland", "fiskedager", "landingsdato", "landingsmottak", "landingskomm", "landingsland")));
                for (SluttSeddel sl : list) {
                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(sl.getFangstAar(), sl.getDokType(), sl.getSltsNr(), IMRdate.formatDate(sl.getFormularDato()), sl.getSalgslag(), sl.getSalgslagOrgnr(),
                            sl.getKjopOrgnr(), sl.getKjopOrgnr(), sl.getKjopLand(), sl.getFiskerKomm(), sl.getFiskerLand(), sl.getFiskerManntall(),
                            sl.getFartRegm(), sl.getFartLand(), sl.getFartType(), sl.getAntMann(), sl.getKvoteType(), IMRdate.formatDate(sl.getSisteFangstDato()),
                            sl.getFangstRegion(), sl.getFangstKystHav(), sl.getFangstHomr(), sl.getFangstLok(), sl.getLatitude(), sl.getLongitude(), sl.getStratum(), sl.getFangstSone(), sl.getRedskap(), sl.getKvoteLand(), sl.getFiskedager(),
                            IMRdate.formatDate(sl.getLandingsDato()), sl.getLandingsMottak(), sl.getLandingsKomm(), sl.getLandingsLand())));
                }
                break;

            case 2:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("fangstaar", "sltsnr", "id", "fisk", "konservering", "tilstand", "kvalitet", "anvendelse", "prodvekt", "rundvekt")));
                for (SluttSeddel sl : list) {
                    for (FiskeLinje fl : sl.getFiskelinjer()) {
                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(sl.getFangstAar(), sl.getSltsNr(),
                                fl.getId(), fl.getFisk(), fl.getKonservering(), fl.getTilstand(), fl.getKvalitet(), fl.getAnvendelse(), fl.getProdVekt(), fl.getRundVekt())));
                    }
                }
                break;
        }
    }
}
