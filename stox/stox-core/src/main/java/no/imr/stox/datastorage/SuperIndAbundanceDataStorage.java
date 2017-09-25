/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.imr.stox.bo.AbundanceIndividualsMatrix;
import no.imr.stox.functions.utils.Functions;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class SuperIndAbundanceDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        List<String> cols = new ArrayList<>(Arrays.asList(
                Functions.COL_ABNDBYIND_STRATUM,
                Functions.COL_ABNDBYIND_ESTLAYER,
                Functions.COL_ABNDBYIND_LENGRP,
                Functions.COL_ABNDBYIND_LENINTV,
                Functions.COL_ABNDBYIND_ABUNDANCE
                ));
        cols.addAll(Functions.INDIVIDUALS);
        cols.add(Functions.COL_ABNDBYIND_INCLUDEINTOTAL);
        ((AbundanceIndividualsMatrix) data).getData().asPage(cols, wr);
    }

}
