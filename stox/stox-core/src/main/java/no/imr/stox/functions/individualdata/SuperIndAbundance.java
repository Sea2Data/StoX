package no.imr.stox.functions.individualdata;

import java.util.List;
import java.util.Map;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.stox.bo.AbundanceIndividualsMatrix;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.stox.bo.IndividualDataMatrix;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 * This function class is used to calculate a weight factor (abundance) for each
 * individual. A pre-requirement for this function is that for each length group
 * of the 'Number by length' data, there will exist at least one individual with
 * a length within each length group. The sum of all the calculated individual
 * weight factors in a stratum and estimation layer is equal to the numbers of
 * fish in all length groups in the 'Number by length' dataset for the same
 * stratum and estimation layer. In other words the calculated weight factors of
 * the individuals represent the abundance.
 *
 * @author aasmunds
 */
public class SuperIndAbundance extends AbstractFunction {

    /**
     *
     * @param input contains matrix INDIVIDUALDATA
     * @return Matrix object of type ABUNDANCE_MATRIX - see
     * DataTypeDescription.txt
     * @throws no.imr.stox.exception.UserErrorException
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        // result = Matrix[ROW~Individual / COL~IndVariable / VAR~Value]
        // indData = DataType=Matrix[GROUP~Species / ROW~Stratum / COL~EstLayer / CELL~LengthGroup / VAR~Individuals]
        IndividualDataMatrix indData = (IndividualDataMatrix) input.get(Functions.PM_SUPERINDABUNDANCE_INDIVIDUALDATA);
        // abundance = DataType=Matrix[GROUP~Species / ROW~SampleUnit / COL~EstLayer / CELL~LengthGroup / VAR~Abundance]
        AbundanceMatrix abnd = (AbundanceMatrix) input.get(Functions.PM_SUPERINDABUNDANCE_ABUNDANCE);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_SUPERINDABUNDANCE_PROCESSDATA);
        String abundSplitMethod = (String) input.get(Functions.PM_SUPERINDABUNDANCE_ABUNDWEIGHTMETHOD);
        Boolean splitByStationDens = abundSplitMethod != null && abundSplitMethod.equals(Functions.ABUNDWEIGHTMETHOD_STATIONDENSITY);
        LengthDistMatrix lenData = (LengthDistMatrix) input.get(Functions.PM_SUPERINDABUNDANCE_LENGTHDIST);

        if (indData == null || abnd == null) {
            // TODO: should we return null and let the program continue running, or should we tell the user that something went wrong?
            return null;
        }
        if (splitByStationDens) {
            if (lenData == null) {
                logger.error("Length distributaion parameter not set when splitting by station densities", null);
                return null;
            }
            String lenDistType = (String) lenData.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE);
            if (lenDistType == null || !lenDistType.contains(Functions.LENGTHDISTTYPE_STD_NORM)) {
                logger.error("Length distributaion not normalized to 1 nautical mile", null);
                return null;
            }
        }

        String sampleUnitType = (String) abnd.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);
        if (sampleUnitType == null || !sampleUnitType.equals(Functions.SAMPLEUNIT_STRATUM)) {
            logger.error("Abundance parameter sampleunit type must be set to stratum.", null);
        }
        AbundanceIndividualsMatrix result = new AbundanceIndividualsMatrix();
        MatrixBO resData = result.getData();
        // Calculate total length distribution in strata
        //MatrixBO totalLFQ = getTotalLFQ(abndByLength, indDataSel, lengthDist);
        Double lenIntv = (Double) abnd.getResolutionMatrix().getRowValue(Functions.RES_LENGTHINTERVAL);

        Integer idx = 0;
        for (String specCatKey : abnd.getData().getSortedKeys()) {
            MatrixBO specCat = (MatrixBO) abnd.getData().getValue(specCatKey);
            for (String stratumKey : specCat.getSortedKeys()) {
                MatrixBO stratum = (MatrixBO) specCat.getValue(stratumKey);
                for (String estLayerKey : stratum.getSortedKeys()) {
                    MatrixBO estLayer = (MatrixBO) stratum.getValue(estLayerKey);
                    for (String lenGrp : estLayer.getSortedKeys()) {
                        if (lenGrp == null) {
                            continue;
                        }
                        // for(String specis in speciesbyspeciesgroup: ...) Remember to add an extra for loop. For each species in speciesgroup
                        Double totAbundance = estLayer.getValueAsDouble(lenGrp);
                        if (totAbundance == null || totAbundance == 0d) {
                            // The abundance by length, the densities and the total lengthdist derives dummy lengths from stationlength dist.
                            // Remember to not create representation for those.
                            continue;
                        }
                        // Todo: Use SpeciesDef from resolution to lookup individual data.
                        List<IndividualBO> indList = (List<IndividualBO>) indData.getData().getGroupRowColCellValue(specCatKey, stratumKey, estLayerKey, lenGrp);
                        if (indList == null) {
                            logger.error("Superindividuals not found for length group " + lenGrp + " when abundance > 0", null);
                            // Ensure there is at least one representation of the abundance - in future open up for superindividuals without weight.
                            continue;
                        }
                        MatrixBO stDens = new MatrixBO();
                        Double dsum = null;
                        if (splitByStationDens) {
                            for (IndividualBO indBO : indList) {
                                String stationKey = indBO.getSample().getCatchBO().getStationBO().getKey();
                                Double m = stDens.getRowColValueAsDouble("m", stationKey);
                                m = m == null ? 1 : m + 1;
                                stDens.setRowColValue("m", stationKey, m);
                                if (lenData != null) {
                                    Double d = stDens.getRowColValueAsDouble("d", stationKey);
                                    if (d == null) {
                                        Double dens = lenData.getData().getGroupRowCellValueAsDouble(specCatKey, stationKey, lenGrp);
                                        if (dens != null) {
                                            stDens.setRowColValue("d", stationKey, dens);
                                        }
                                    }
                                }
                            }
                            MatrixBO mSum = stDens.getRowValueAsMatrix("d");
                            dsum = mSum != null ? mSum.getSum() : null;
                            if (dsum == null) {
                                logger.error("Splitting by station densities not possible because no densities found of at length group at any stations. Superindividuals should not be created for this lengthgroup", null);
                            }
                        }
                        // Distribute abundance on individuals represented
                        for (IndividualBO indBO : indList) {
                            // Make the abundance proportion representative to catch
                            // Default unscaled abundance proportion 
                            Double p = 1d / indList.size(); // equal split by default.
                            if (splitByStationDens) {
                                String stationKey = indBO.getSample().getCatchBO().getStationBO().getKey();                                // Split by station density, like for swept area.
                                Double m = stDens.getRowColValueAsDouble("m", stationKey);
                                Double d = stDens.getRowColValueAsDouble("d", stationKey);
                                if (d == null) {
                                    // Skip superindividuals with 0 proportion of abundance?
                                    // I.E when PgNapes data with missing lengthsampleweight and StationlenDist (not percent) will skip the same length.
                                    continue;
                                    //d = 0d; 
                                }
                                p = ImrMath.safeDivide(StoXMath.relativeToTotal(d, dsum), m);
                                if (p == null) {
                                    logger.error("Error when splitting abundance for length group " + lenGrp + " at individual " + indBO.getKey(), null);
                                }
                            }
                            Double abundance = totAbundance * p;
                            String indKey = (++idx).toString();
                            addSuperindividual(resData, indKey, specCatKey, stratumKey, estLayerKey, lenGrp, lenIntv, abundance);
                            /*if (indBO.getWeight() != null) {
                             resData.setRowColValue(indKey, Functions.COL_ABNDBYIND_BIOMASS, Calc.roundTo(StoXMath.safeMult(abundance, StoXMath.gramsToKg(indBO.getWeight())), 4));
                             }*/
                            if (indBO.getSample() == null) {
                                System.out.println("Error in individual " + indBO);
                            }
                            // Add Individual weighting factor
                            /*Double indWFac = 1.0d;//getIndividualWeightingFactor(species, stratum, estLayer, lenGrp, lengthDist,
                             //totalLFQ, indBO, indList.size());
                             if (indWFac != null) {
                             resData.setRowColValue(indKey, Functions.COL_ABNDBYIND_INDWFAC, Calc.roundTo(indWFac, 8));
                             }*/

                            for (String code : Functions.INDIVIDUALS) {
                                // Add biological individual pop categories here
                                resData.setRowColValue(indKey, code, BioticUtils.getIndVar(indBO, code));
                            }
                            String inc = pd != null ? (String) AbndEstProcessDataUtil.getStratumPolygons(pd).getRowColValue(stratumKey,
                                    Functions.COL_POLVAR_INCLUDEINTOTAL) : "true";
                            resData.setRowColValue(indKey, Functions.COL_ABNDBYIND_INCLUDEINTOTAL, inc);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Add super individual (abundance) representation for a given length group
     * in layer, strata for a given biological species.
     *
     * @param resData
     * @param indKey
     * @param specCat
     * @param stratum
     * @param estLayer
     * @param lenGrp
     * @param abundance
     */
    private void addSuperindividual(MatrixBO resData, String indKey, String specCat, String stratum, String estLayer, String lenGrp, Double lenIntv, Double abundance) {
        //resData.setRowColValue(indKey, Functions.COL_ABNDBYIND_SPECCAT, specCat); now added as individual variable
        resData.setRowColValue(indKey, Functions.COL_ABNDBYIND_STRATUM, stratum);
        resData.setRowColValue(indKey, Functions.COL_ABNDBYIND_ESTLAYER, estLayer);
        resData.setRowColValue(indKey, Functions.COL_ABNDBYIND_LENGRP, lenGrp);
        resData.setRowColValue(indKey, Functions.COL_ABNDBYIND_LENINTV, lenIntv);
        resData.setRowColValue(indKey, Functions.COL_ABNDBYIND_ABUNDANCE, Calc.roundTo(abundance, 4));
    }

    /**
     * Calculate strata LFQ by summing the relative LFQS for each station
     * involved. The station involved is given by the individual data selection
     *
     * @param abndByLength Matrix[GROUP~Species / ROW~SampleUnit / COL~EstLayer
     * / CELL~LengthGroup / VAR~Abundance]
     * @param indData Matrix[GROUP~Species / ROW~Stratum / COL~EstLayer /
     * CELL~LengthGroup / VAR~Individuals]
     * @param indDataSel Matrix[ROW~Stratum / COL~EstLayer / CELL~Station /
     * VAR~Included]
     * @param lengthDist Matrix[GROUP~Species / ROW~Station / CELL~LengthGroup /
     * VAR~WeightedCount]
     * @param lenInterval
     * @return
     */
    /*private MatrixBO getTotalLFQ(MatrixBO abndByLength, MatrixBO indDataSel, MatrixBO lengthDist) {
     if (lengthDist == null || indDataSel == null) {
     return null;
     }
     MatrixBO result = new MatrixBO("Matrix[GROUP~Species / ROW~Stratum / COL~EstLayer / CELL~LengthGroup / VAR~Count]");
     for (String species : abndByLength.getKeys()) {
     MatrixBO group = (MatrixBO) abndByLength.getValue(species);
     for (String stratum : group.getKeys()) {
     MatrixBO row = (MatrixBO) group.getValue(stratum);
     for (String estLayer : row.getKeys()) {
     MatrixBO cell = row.getValueAsMatrix(estLayer);
     MatrixBO stationsCell = indDataSel.getRowColValueAsMatrix(stratum, estLayer);
     if(stationsCell == null) {
     continue;
     }
     for (String station : stationsCell.getKeys()) {
     MatrixBO r = (MatrixBO) lengthDist.getGroupRowValue(species, station);
     if (r == null) {
     continue;
     }
     MatrixBO c = r.getDefaultValueAsMatrix();
     for (String lenGrp : cell.getKeys()) {
     Double value = c.getValueAsDouble(lenGrp);
     String newLGrp = lenGrp;
     result.addGroupRowColCellValue(species, stratum, estLayer, newLGrp, value);
     }
     }
     }
     }
     }
     return result;
     }*/

 /*private Double getIndividualWeightingFactor(String species, String stratum, String estLayer, String lenGrp, MatrixBO lengthDist,
     MatrixBO totalLFQ, IndividualBO indBO, Integer nIndividuals) {
     if (totalLFQ == null || lengthDist == null || indBO == null || indBO.getSample() == null) {
     return null;
     }
     String station = indBO.getSample().getCatchBO().getStationBO().getKey();
     MatrixBO r = (MatrixBO) lengthDist.getGroupRowValue(species, station);
     if (r == null) {
     return null;
     }
     MatrixBO c = r.getDefaultValueAsMatrix();
     // Formula Calculate the individual weighting factor (By Gjert Dingsør/Espen 04-2014)
     Double n1 = c.getValueAsDouble(lenGrp);
     Double nTot = totalLFQ.getGroupRowColCellValueAsDouble(species, stratum, estLayer, lenGrp);
     Double nRel = StoXMath.relativeToTotal(n1, nTot);
     return StoXMath.safeDivide(nRel, nIndividuals.doubleValue());
     }*/
}
