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
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.LandingData;
import no.imr.stox.bo.landing.LandingsdataBO;
import no.imr.stox.bo.landing.SeddellinjeBO;
import no.imr.stox.functions.utils.ReflectionUtil;

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
        asTable((LandingData)data, level, wr);
    }

    @Override
    public Integer getNumDataStorageFiles() {
        return 2;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        switch (idxFile) {
            case 1:
                return "LandingData";
        }
        return "";
    }

    public static void asTable(LandingData list, Integer level, Writer wr) {
        switch (level) {
            case 1:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(BaseBO.csvHdr(SeddellinjeType.class, true))));
                for (LandingsdataBO fl : list) {
                    for (SeddellinjeBO sl : fl.getSeddellinjeBOs()) {
                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(sl.csv(true))));
                    }
                }
                break;
        }
    }
}
