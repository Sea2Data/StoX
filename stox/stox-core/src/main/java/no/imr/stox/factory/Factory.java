/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.factory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.IProject;
import no.imr.stox.model.Project;

/**
 *
 * @author aasmunds
 */
public class Factory {

    public static final String TEMPLATE_ACOUSTICABUNDANCETRANSECT = "AcousticAbundanceTransect";
    public static final String TEMPLATE_ACOUSTICABUNDANCERECTANGLE = "AcousticAbundanceRectangle";
    public static final String TEMPLATE_STATIONLENGTHDIST = "StationLengthDistTemplate";
    public static final String TEMPLATE_DATRAS = "DATRASTemplate";
    public static final String TEMPLATE_SWEPTAREA = "SweptAreaTemplate";
    public static final String TEMPLATE_LENGTHWEIGHTRELATIONSHIP = "LengthWeightRelationShip";
    public static final String TEMPLATE_SWEPTAREA_TOTAL = "SweptAreaTotalTemplate";
    public static final String TEMPLATE_LARVAESWEPTAREA = "LarvaeSweptAreaTemplate";
    public static final String TEMPLATE_SPLITNASC = "SplitNASC";
    public static final String TEMPLATE_ECA = "ECA";
    public static final String TEMPLATE_USERDEFINED = "UserDefined";

    public static List<String> getAvailableTemplates() {
        return Arrays.asList(
                TEMPLATE_ACOUSTICABUNDANCETRANSECT,
                //TEMPLATE_ACOUSTICABUNDANCERECTANGLE,
                TEMPLATE_SWEPTAREA,
                TEMPLATE_SWEPTAREA_TOTAL,
                TEMPLATE_SPLITNASC,
                TEMPLATE_LENGTHWEIGHTRELATIONSHIP,
                TEMPLATE_STATIONLENGTHDIST,
                TEMPLATE_DATRAS,
                TEMPLATE_LARVAESWEPTAREA,
                TEMPLATE_USERDEFINED/*,
                TEMPLATE_ECA*/ // not finished
        );
    }

    public static String getTemplateDescription(String template) {
        switch (template) {
            case TEMPLATE_ACOUSTICABUNDANCETRANSECT:
                return "Acoustic abundance by transect and r-model with uncertainty";
            case TEMPLATE_ACOUSTICABUNDANCERECTANGLE:
                return "Acoustic abundance by rectangles";
            case TEMPLATE_STATIONLENGTHDIST:
                return "Station length distribution";
            case TEMPLATE_DATRAS:
                return "DATRAS conversion";
            case TEMPLATE_SWEPTAREA:
                return "Swept area (length dependent)";
            case TEMPLATE_SWEPTAREA_TOTAL:
                return "Swept area (total catch)";
            case TEMPLATE_LARVAESWEPTAREA:
                return "Larvae swept area";
            case TEMPLATE_SPLITNASC:
                return "Split NASC";
            case TEMPLATE_ECA:
                return "ECA - Estimate catch at age";
            case TEMPLATE_USERDEFINED:
                return "User defined (empty models)";
            case TEMPLATE_LENGTHWEIGHTRELATIONSHIP:
                return "Length Weight relationship";
        }
        return "";
    }

    public static void applyProjectTemplate(IProject p, String template) {
        p.getBaseline().getProcessList().clear();
        p.getRModel().getProcessList().clear();
        switch (template) {
            case TEMPLATE_ACOUSTICABUNDANCETRANSECT:
                createAcousticAbundanceTransectProject(p.getBaseline());
                createAbundanceReport(p.getBaselineReport(), true, 1.0, 1000, Functions.COL_IND_AGE);
                createRWithUncertainty(p.getRModel(), true, Functions.BOOTSTRAPMETHOD_ACOUSTICTRAWL);
                createRReport(p.getRModelReport(), null);
                break;
            case TEMPLATE_ACOUSTICABUNDANCERECTANGLE:
                createAcousticAbundanceRectangleProject(p.getBaseline());
                createAbundanceReport(p.getBaselineReport(), true, 1.0, 1000, Functions.COL_IND_AGE);
                break;
            case TEMPLATE_STATIONLENGTHDIST:
                createStationLengthDistTemplateProject(p.getBaseline());
                break;
            case TEMPLATE_DATRAS:
                createDATRASTemplateProject(p.getBaseline());
                break;
            case TEMPLATE_SWEPTAREA_TOTAL:
                createSweptAreaTotalCatchTemplateProject(p.getBaseline());
                createSweptAreaTotalCatchReport(p.getBaselineReport());
                createRWithUncertainty(p.getRModel(), false, Functions.BOOTSTRAPMETHOD_SWEPTAREATOTAL);
                createRReport(p.getRModelReport(), "bootstrapMethod='SweptAreaTotal'");
                break;
            case TEMPLATE_SWEPTAREA:
                createSweptAreaTemplateProject(p.getBaseline(), template, Functions.LENGTHDISTTYPE_NORMLENGHTDIST, 1.0);
                createAbundanceReport(p.getBaselineReport(), true, 1.0, 1000, Functions.COL_IND_AGE);
                createRWithUncertainty(p.getRModel(), true, Functions.BOOTSTRAPMETHOD_SWEPTAREALENGTH);
                createRReport(p.getRModelReport(), null);
                break;
            case TEMPLATE_LARVAESWEPTAREA:
                createSweptAreaTemplateProject(p.getBaseline(), template, Functions.LENGTHDISTTYPE_LENGHTDIST, 0.1);
                createAbundanceReport(p.getBaselineReport(), false, 0.1, 1000000, Functions.COL_IND_DEVELOPMENTALSTAGE);
                createRWithUncertainty(p.getRModel(), false, Functions.BOOTSTRAPMETHOD_SWEPTAREALENGTH);
                createRReport(p.getRModelReport(), null);
                break;
            case TEMPLATE_SPLITNASC:
                createSplitNASCProject(p.getBaseline());
                break;
            case TEMPLATE_ECA:
                createECAProject(p.getBaseline());
                createECARModel(p.getRModel());
                break;
            case TEMPLATE_LENGTHWEIGHTRELATIONSHIP:
                createLengthWeightRelationshipTemplateProject(p.getBaseline());
                break;
        }
        // Set template description by using basline - should have another project file.
        p.setTemplate(template);
        //p.getBaseline().setDescription(Factory.getTemplateDescription(template));
    }

    public static IProject getTemplateProject(String template) {
        IProject p = new Project();
        applyProjectTemplate(p, template);
        return p;
    }

    /**
     * Template project for running length distr. from biotic api data
     *
     * @param m
     */
    public static void createStationLengthDistTemplateProject(IModel m) {
        m.addProcess(Functions.FN_READBIOTICXML, Functions.FN_READBIOTICXML);
        m.addProcess(Functions.FN_FILTERBIOTIC, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_FILTERBIOTIC_BIOTICDATA, Functions.FN_READBIOTICXML);
        m.addProcess(Functions.FN_STATIONLENGTHDIST, Functions.FN_STATIONLENGTHDIST).
                setParameterProcessValue(Functions.PM_STATIONLENGTHDIST_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                setParameterValue(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE, Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST);
        m.addProcess(Functions.FN_REGROUPLENGTHDIST, Functions.FN_REGROUPLENGTHDIST).
                setParameterProcessValue(Functions.PM_REGROUPLENGTHDIST_LENGTHDIST, Functions.FN_STATIONLENGTHDIST).
                setParameterValue(Functions.PM_REGROUPLENGTHDIST_LENGTHINTERVAL, 1.0);
    }

    /**
     * Template project for acoustic abundance estimation by transects
     *
     * @param m
     */
    public static void createAcousticAbundanceTransectProject(IModel m) {
        m.setDescription("Abundance estimation by transects");

        // Read process data
        m.addProcess(Functions.FN_READPROCESSDATA, Functions.FN_READPROCESSDATA);
        // Acoustic related:
        createAbndAcoustic(m);

        // Biotic related:
        createAbndBiotic(m, Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST, 1.0);

        // Process data related:
        m.addProcess(Functions.FN_DEFINESTRATA, Functions.FN_DEFINESTRATA).
                setParameterProcessValue(Functions.PM_DEFINESTRATA_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterValue(Functions.PM_DEFINESTRATA_USEPROCESSDATA, false).
                setRespondInGUI(true).setBreakInGUI(true);
        m.addProcess(Functions.FN_STRATUMAREA, Functions.FN_STRATUMAREA).
                setParameterProcessValue(Functions.PM_DEFINESTRATA_PROCESSDATA, Functions.FN_READPROCESSDATA);
        m.addProcess(Functions.FN_DEFINEACOUSTICPSU, Functions.FN_DEFINEACOUSTICPSU).
                setRespondInGUI(true).setBreakInGUI(true).
                setParameterProcessValue(Functions.PM_DEFINEACOUSTICPSU_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_DEFINEACOUSTICPSU_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC);
        m.addProcess(Functions.FN_MEANNASC, Functions.FN_MEANNASC).
                setParameterProcessValue(Functions.PM_MEANNASC_NASC, Functions.FN_NASC).
                setParameterProcessValue(Functions.PM_MEANNASC_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterValue(Functions.PM_MEANNASC_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_PSU);
        m.addProcess(Functions.FN_BIOSTATIONASSIGNMENT, Functions.FN_BIOSTATIONASSIGNMENT).
                setRespondInGUI(true).setRespondInGUI(true).setBreakInGUI(true).
                setParameterProcessValue(Functions.PM_BIOSTATIONASSIGNMENT_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_BIOSTATIONASSIGNMENT_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_BIOSTATIONASSIGNMENT_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterValue(Functions.PM_BIOSTATIONASSIGNMENT_ASSIGNMENTMETHOD, Functions.ASSIGNMENTMETHOD_STRATUM).
                setParameterValue(Functions.PM_BIOSTATIONASSIGNMENT_ESTLAYERS, "1" + "~" + Functions.WATERCOLUMN_PELBOT);
        createBioStationWeighting(m);
        m.addProcess(Functions.FN_TOTALLENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_LENGTHDIST, Functions.FN_REGROUPLENGTHDIST).
                setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_PROCESSDATA, Functions.FN_READPROCESSDATA);/*.
         setParameterValue(Functions.PM_TOTALLENGTHDIST_AGGREGATION, Functions.AGGREGATION_AVERAGE);*/

        // Density related:
        m.addProcess(Functions.FN_ACOUSTICDENSITY, Functions.FN_ACOUSTICDENSITY).
                setParameterProcessValue(Functions.PM_ACOUSTICDENSITY_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterProcessValue(Functions.PM_ACOUSTICDENSITY_LENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                setParameterProcessValue(Functions.PM_ACOUSTICDENSITY_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_ACOUSTICDENSITY_NASC, Functions.FN_MEANNASC).
                setParameterValue(Functions.PM_ACOUSTICDENSITY_M, 20);
        m.addProcess("MeanDensity_Stratum", Functions.FN_MEANDENSITY).
                setParameterProcessValue(Functions.PM_MEANDENSITY_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_MEANDENSITY_DENSITY, Functions.FN_ACOUSTICDENSITY).
                setParameterValue(Functions.PM_MEANDENSITY_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_STRATUM);
        m.addProcess("SumDensity_Stratum", Functions.FN_SUMDENSITY).
                setParameterProcessValue(Functions.PM_SUMDENSITY_DENSITY, "MeanDensity_Stratum");

        m.addProcess(Functions.FN_ABUNDANCE, Functions.FN_ABUNDANCE).
                setParameterProcessValue(Functions.PM_ABUNDANCE_DENSITY, "SumDensity_Stratum").
                setParameterProcessValue(Functions.PM_ABUNDANCE_POLYGONAREA, Functions.FN_STRATUMAREA);

        // Individual data related:
        createAbndIndividualData(m, Functions.FN_ABUNDANCE, Functions.ABUNDWEIGHTMETHOD_EQUAL);
        // Write process data
        m.addProcess(Functions.FN_WRITEPROCESSDATA, Functions.FN_WRITEPROCESSDATA).
                setRespondInGUI(Boolean.TRUE);
    }

    /**
     * Template project for acoustic abundance by rectangles
     *
     * @param m
     */
    public static void createAcousticAbundanceRectangleProject(IModel m) {
        m.setDescription("Abundance estimation by rectangles");

        // Read process data
        m.addProcess(Functions.FN_READPROCESSDATA, Functions.FN_READPROCESSDATA);

        // Acoustic related:
        createAbndAcoustic(m);

        // Biotic related:
        createAbndBiotic(m, Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST, 1.0);

        // Process data related:
        m.addProcess(Functions.FN_DEFINESTRATA, Functions.FN_DEFINESTRATA).
                setParameterProcessValue(Functions.PM_DEFINESTRATA_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setRespondInGUI(true).setBreakInGUI(true);
        m.addProcess(Functions.FN_DEFINERECTANGLE, Functions.FN_DEFINERECTANGLE).
                setRespondInGUI(Boolean.TRUE).
                setParameterProcessValue(Functions.PM_DEFINERECTANGLE_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_DEFINERECTANGLE_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterValue(Functions.PM_DEFINERECTANGLE_WIDTH, 2.0).
                setParameterValue(Functions.PM_DEFINERECTANGLE_HEIGHT, 1.0);
        m.addProcess(Functions.FN_MEANNASC, Functions.FN_MEANNASC).
                setParameterProcessValue(Functions.PM_MEANNASC_NASC, Functions.FN_NASC).
                setParameterProcessValue(Functions.PM_MEANNASC_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterValue(Functions.PM_MEANNASC_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_PSU);
        m.addProcess(Functions.FN_RECTANGLEAREA, Functions.FN_RECTANGLEAREA).
                setParameterProcessValue(Functions.PM_RECTANGLEAREA_PROCESSDATA, Functions.FN_READPROCESSDATA);
        m.addProcess(Functions.FN_RECTANGLEASSIGNMENT, Functions.FN_RECTANGLEASSIGNMENT).
                setRespondInGUI(true).
                setBreakInGUI(true).
                setParameterProcessValue(Functions.PM_RECTANGLEASSIGNMENT_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_RECTANGLEASSIGNMENT_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_RECTANGLEASSIGNMENT_NASC, Functions.FN_MEANNASC).
                setParameterValue(Functions.PM_RECTANGLEASSIGNMENT_ESTLAYERS, "1" + "~" + Functions.WATERCOLUMN_PELBOT);
        m.addProcess(Functions.FN_TOTALLENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_LENGTHDIST, Functions.FN_REGROUPLENGTHDIST).
                setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_PROCESSDATA, Functions.FN_READPROCESSDATA);
        // Density related:
        m.addProcess(Functions.FN_ACOUSTICDENSITY, Functions.FN_ACOUSTICDENSITY).
                setParameterProcessValue(Functions.PM_ACOUSTICDENSITY_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterProcessValue(Functions.PM_ACOUSTICDENSITY_LENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                setParameterProcessValue(Functions.PM_ACOUSTICDENSITY_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_ACOUSTICDENSITY_NASC, Functions.FN_MEANNASC).
                setParameterValue(Functions.PM_ACOUSTICDENSITY_M, 20);
        m.addProcess("SumDensity_PSU", Functions.FN_SUMDENSITY).
                setParameterProcessValue(Functions.PM_SUMDENSITY_DENSITY, Functions.FN_ACOUSTICDENSITY);
        // Abundance related:
        m.addProcess(Functions.FN_ABUNDANCE, Functions.FN_ABUNDANCE).
                setParameterProcessValue(Functions.PM_ABUNDANCE_DENSITY, "SumDensity_PSU").
                setParameterProcessValue(Functions.PM_ABUNDANCE_POLYGONAREA, Functions.FN_RECTANGLEAREA);
        m.addProcess(Functions.FN_SUMABUNDANCE, Functions.FN_SUMABUNDANCE).
                setParameterProcessValue(Functions.PM_SUMABUNDANCE_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_SUMABUNDANCE_ABUNDANCE, Functions.FN_ABUNDANCE);
        m.addProcess(Functions.FN_REGROUPABUNDANCE, Functions.FN_REGROUPABUNDANCE).
                setParameterProcessValue(Functions.PM_REGROUPABUNDANCE_ABUNDANCE, Functions.FN_SUMABUNDANCE).
                setParameterValue(Functions.PM_REGROUPABUNDANCE_LENGTHINTERVAL, 1.0);
        // Individual data
        createAbndIndividualData(m, Functions.FN_REGROUPABUNDANCE, Functions.ABUNDWEIGHTMETHOD_EQUAL);
        // Write process data
        m.addProcess(Functions.FN_WRITEPROCESSDATA, Functions.FN_WRITEPROCESSDATA).
                setRespondInGUI(Boolean.TRUE);
    }

    /**
     * Create Abundance acoustic related functions
     *
     * @param m
     */
    public static void createAbndAcoustic(IModel m) {
        m.addProcess(Functions.FN_READACOUSTICXML, Functions.FN_READACOUSTICXML).
                setFileOutput(false);

        m.addProcess(Functions.FN_FILTERACOUSTIC, Functions.FN_FILTERACOUSTIC).
                setParameterProcessValue(Functions.PM_FILTERACOUSTIC_ACOUSTICDATA, Functions.FN_READACOUSTICXML).
                setParameterValue(Functions.PM_FILTERACOUSTIC_FREQEXPR, "frequency == 38000 and transceiver == 2").
                setParameterValue(Functions.PM_FILTERACOUSTIC_NASCEXPR, "acocat == 12 and chtype == 'P'").
                setRespondInGUI(true);

        m.addProcess(Functions.FN_NASC, Functions.FN_NASC).
                setParameterProcessValue(Functions.PM_NASC_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterValue(Functions.PM_NASC_LAYERTYPE, Functions.LAYERTYPE_WATERCOLUMN);

    }

    /**
     * Create sub model Abundance biotic related functions
     *
     * @param m
     */
    public static void createAbndBiotic(IModel m, String lengthDistType, Double lenInterval) {
        m.addProcess(Functions.FN_READBIOTICXML, Functions.FN_READBIOTICXML).
                setFileOutput(false);

        m.addProcess(Functions.FN_FILTERBIOTIC, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_FILTERBIOTIC_BIOTICDATA, Functions.FN_READBIOTICXML).
                setParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR, "noname == 'SILDG03'").
                setRespondInGUI(true);

        m.addProcess(Functions.FN_STATIONLENGTHDIST, Functions.FN_STATIONLENGTHDIST).
                setParameterProcessValue(Functions.PM_STATIONLENGTHDIST_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                setParameterValue(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE, lengthDistType);

        m.addProcess(Functions.FN_REGROUPLENGTHDIST, Functions.FN_REGROUPLENGTHDIST).
                setParameterProcessValue(Functions.PM_REGROUPLENGTHDIST_LENGTHDIST, Functions.FN_STATIONLENGTHDIST).
                setParameterValue(Functions.PM_REGROUPLENGTHDIST_LENGTHINTERVAL, lenInterval);

    }

    /**
     * Create sub model Individual data related functions
     *
     * @param m
     * @param abundanceSource - abundance by length grouped to stratum
     */
    public static void createAbndIndividualData(IModel m, String abundanceSource, String abundSplitMethod) {
        m.addProcess(Functions.FN_INDIVIDUALDATASTATIONS, Functions.FN_INDIVIDUALDATASTATIONS).
                setParameterProcessValue(Functions.PM_INDIVIDUALDATASTATIONS_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_INDIVIDUALDATASTATIONS_ABUNDANCE, abundanceSource);

        m.addProcess(Functions.FN_INDIVIDUALDATA, Functions.FN_INDIVIDUALDATA).
                setParameterProcessValue(Functions.PM_INDIVIDUALDATA_INDIVIDUALDATASTATIONS, Functions.FN_INDIVIDUALDATASTATIONS).
                setParameterProcessValue(Functions.PM_INDIVIDUALDATA_BIOTICDATA, Functions.FN_FILTERBIOTIC);

        /*        m.addProcess(Functions.FN_CORRECTFORINNSUFFICIENTSAMPLING, Functions.FN_CORRECTFORINNSUFFICIENTSAMPLING).
         setParameterProcessValue(Functions.PM_CORRECTFORINNSUFFICIENTSAMPLING_ABUNDANCE, abundanceSource).
         setParameterProcessValue(Functions.PM_CORRECTFORINNSUFFICIENTSAMPLING_INDIVIDUALDATA, Functions.FN_INDIVIDUALDATA);
         */
        m.addProcess(Functions.FN_SUPERINDABUNDANCE, Functions.FN_SUPERINDABUNDANCE).
                setParameterProcessValue(Functions.PM_SUPERINDABUNDANCE_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_SUPERINDABUNDANCE_ABUNDANCE, abundanceSource).
                setParameterProcessValue(Functions.PM_SUPERINDABUNDANCE_INDIVIDUALDATA, Functions.FN_INDIVIDUALDATA).
                setParameterProcessValue(Functions.PM_SUPERINDABUNDANCE_LENGTHDIST, Functions.FN_REGROUPLENGTHDIST).
                setParameterValue(Functions.PM_SUPERINDABUNDANCE_ABUNDWEIGHTMETHOD, abundSplitMethod);
    }

    public static void createAbundanceReport(IModel m, Boolean fillMissing, Double lenInterval, Integer scale, String dim2) {
        if (fillMissing) {
            m.addProcess(Functions.FN_FILLMISSINGDATA, Functions.FN_FILLMISSINGDATA).
                    setParameterProcessValue(Functions.PM_FILLMISSINGDATA_SUPERINDIVIDUALS, Functions.FN_SUPERINDABUNDANCE).
                    setParameterValue(Functions.PM_FILLMISSINGDATA_FILLVARIABLES, Functions.FILLVARIABLES_IMPUTEBYAGE).
                    setParameterValue(Functions.PM_FILLMISSINGDATA_FILLWEIGHT, Functions.FILLWEIGHT_MEAN);
        }
        m.addProcess(Functions.FN_ESTIMATEBYPOPULATIONCATEGORY, Functions.FN_ESTIMATEBYPOPULATIONCATEGORY).
                setParameterProcessValue(Functions.PM_ESTIMATEBYPOPCATEGORY_SUPERINDIVIDUALS, fillMissing ? Functions.FN_FILLMISSINGDATA : Functions.FN_SUPERINDABUNDANCE).
                setParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_LENGTHINTERVAL, lenInterval).
                setParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_SCALE, scale).
                setParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM1, Functions.COL_ABNDBYIND_LENGRP).
                setParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM2, dim2).
                setParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM3, Functions.COL_ABNDBYIND_SPECCAT).
                setParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM4, "none").
                setParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM5, "none");
    }

    public static void createBioStationWeighting(IModel m) {
        m.addProcess(Functions.FN_BIOSTATIONWEIGHTING, Functions.FN_BIOSTATIONWEIGHTING).
                setParameterProcessValue(Functions.PM_BIOSTATIONWEIGHTING_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_BIOSTATIONWEIGHTING_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_BIOSTATIONWEIGHTING_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterProcessValue(Functions.PM_BIOSTATIONWEIGHTING_LENGTHDIST, Functions.FN_REGROUPLENGTHDIST).
                setParameterProcessValue(Functions.PM_BIOSTATIONWEIGHTING_NASC, Functions.FN_NASC).
                setParameterValue(Functions.PM_BIOSTATIONWEIGHTING_WEIGHTINGMETHOD, Functions.WEIGHTINGMETHOD_EQUAL).
                setParameterValue(Functions.PM_BIOSTATIONWEIGHTING_M, 20).
                setParameterValue(Functions.PM_BIOSTATIONWEIGHTING_MAXNUMLENGTHSAMPLES, 100);
    }

    private static void createDATRASTemplateProject(IModel m) {
        m.addProcess(Functions.FN_READBIOTICXML, Functions.FN_READBIOTICXML);
        m.addProcess(Functions.FN_FILTERBIOTIC, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_FILTERBIOTIC_BIOTICDATA, Functions.FN_READBIOTICXML);
        m.addProcess(Functions.FN_DATRAS, Functions.FN_DATRAS).
                setParameterProcessValue(Functions.PM_DATRAS_BIOTICDATA, Functions.FN_FILTERBIOTIC);

    }

    private static void createSweptAreaTemplateProject(IModel m, String template, String lenDistType, Double lenInterval) {
        m.setDescription("Swept area (length dependent)");

        // Read process data
        m.addProcess(Functions.FN_READPROCESSDATA, Functions.FN_READPROCESSDATA);

        // Biotic related:
        createAbndBiotic(m, lenDistType, lenInterval);

        // Process data related:
        m.addProcess(Functions.FN_DEFINESTRATA, Functions.FN_DEFINESTRATA).
                setParameterProcessValue(Functions.PM_DEFINESTRATA_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterValue(Functions.PM_DEFINESTRATA_USEPROCESSDATA, false).
                setRespondInGUI(true).setBreakInGUI(true);
        m.addProcess(Functions.FN_STRATUMAREA, Functions.FN_STRATUMAREA).
                setParameterProcessValue(Functions.PM_DEFINESTRATA_PROCESSDATA, Functions.FN_READPROCESSDATA);

        m.addProcess(Functions.FN_DEFINESWEPTAREAPSU, Functions.FN_DEFINESWEPTAREAPSU).
                setParameterProcessValue(Functions.PM_DEFINESWEPTAREAPSU_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_DEFINESWEPTAREAPSU_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                setParameterValue(Functions.PM_DEFINESWEPTAREAPSU_METHOD, Functions.SWEPTAREAPSUMETHOD_STATION);

        m.addProcess(Functions.FN_TOTALLENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_LENGTHDIST, Functions.FN_REGROUPLENGTHDIST).
                setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_PROCESSDATA, Functions.FN_READPROCESSDATA);
        // Density related:
        switch (template) {
            case TEMPLATE_SWEPTAREA:
                m.addProcess(Functions.FN_SWEPTAREADENSITY, Functions.FN_SWEPTAREADENSITY).
                        setParameterProcessValue(Functions.PM_SWEPTAREADENSITY_PROCESSDATA, Functions.FN_READPROCESSDATA).
                        setParameterProcessValue(Functions.PM_SWEPTAREADENSITY_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                        setParameterProcessValue(Functions.PM_SWEPTAREADENSITY_LENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                        setParameterValue(Functions.PM_SWEPTAREADENSITY_SWEEPWIDTHMETHOD, 25.0);
                break;
            case TEMPLATE_LARVAESWEPTAREA:
                m.addProcess(Functions.FN_LARVAEDENSITY, Functions.FN_LARVAEDENSITY).
                        setParameterProcessValue(Functions.PM_LARVAEDENSITY_PROCESSDATA, Functions.FN_READPROCESSDATA).
                        setParameterProcessValue(Functions.PM_LARVAEDENSITY_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                        setParameterProcessValue(Functions.PM_LARVAEDENSITY_LENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                        setParameterValue(Functions.PM_LARVAEDENSITY_GEAROPENINGAREA, "2113:0.25,2114:0.5,2124:0.5,2133:0.25,2331:0.02835");
                break;
        }
        m.addProcess("MeanDensity_Stratum", Functions.FN_MEANDENSITY).
                setParameterProcessValue(Functions.PM_MEANDENSITY_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_MEANDENSITY_DENSITY,
                        template.equals(TEMPLATE_SWEPTAREA) ? Functions.FN_SWEPTAREADENSITY : Functions.FN_LARVAEDENSITY).
                setParameterValue(Functions.PM_MEANDENSITY_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_STRATUM);

        m.addProcess(Functions.FN_ABUNDANCE, Functions.FN_ABUNDANCE).
                setParameterProcessValue(Functions.PM_ABUNDANCE_DENSITY, "MeanDensity_Stratum").
                setParameterProcessValue(Functions.PM_ABUNDANCE_POLYGONAREA, Functions.FN_STRATUMAREA);

        // Individual data related:
        createAbndIndividualData(m, Functions.FN_ABUNDANCE, Functions.ABUNDWEIGHTMETHOD_STATIONDENSITY);
        // Write process data
        m.addProcess(Functions.FN_WRITEPROCESSDATA, Functions.FN_WRITEPROCESSDATA).
                setRespondInGUI(Boolean.TRUE);
    }

    private static void createSweptAreaTotalCatchTemplateProject(IModel m) {
        m.setDescription("Swept area (total catch)");

        // Read process data
        m.addProcess(Functions.FN_READPROCESSDATA, Functions.FN_READPROCESSDATA);

        // Biotic related:
        m.addProcess(Functions.FN_READBIOTICXML, Functions.FN_READBIOTICXML).
                setFileOutput(false);

        m.addProcess(Functions.FN_FILTERBIOTIC, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_FILTERBIOTIC_BIOTICDATA, Functions.FN_READBIOTICXML).
                setParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR, "noname == 'SILDG03'").
                setRespondInGUI(true);

        // Process data related:
        m.addProcess(Functions.FN_DEFINESTRATA, Functions.FN_DEFINESTRATA).
                setParameterProcessValue(Functions.PM_DEFINESTRATA_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterValue(Functions.PM_DEFINESTRATA_USEPROCESSDATA, false).
                setRespondInGUI(true).setBreakInGUI(true);
        m.addProcess(Functions.FN_STRATUMAREA, Functions.FN_STRATUMAREA).
                setParameterProcessValue(Functions.PM_DEFINESTRATA_PROCESSDATA, Functions.FN_READPROCESSDATA);

        m.addProcess(Functions.FN_DEFINESWEPTAREAPSU, Functions.FN_DEFINESWEPTAREAPSU).
                setParameterProcessValue(Functions.PM_DEFINESWEPTAREAPSU_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_DEFINESWEPTAREAPSU_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                setParameterValue(Functions.PM_DEFINESWEPTAREAPSU_METHOD, Functions.SWEPTAREAPSUMETHOD_STATION);

        Stream.of(Functions.CATCHVARIABLE_COUNT, Functions.CATCHVARIABLE_WEIGHT).forEach(var -> {
            String densProc = "SweptArea" + var + "Density";
            m.addProcess(densProc, Functions.FN_SWEPTAREADENSITY).
                    setParameterProcessValue(Functions.PM_SWEPTAREADENSITY_PROCESSDATA, Functions.FN_READPROCESSDATA).
                    setParameterProcessValue(Functions.PM_SWEPTAREADENSITY_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                    setParameterValue(Functions.PM_SWEPTAREADENSITY_SWEPTAREAMETHOD, Functions.SWEPTAREAMETHOD_TOTALCATCH).
                    setParameterValue(Functions.PM_SWEPTAREADENSITY_CATCHVARIABLE, var).
                    setParameterValue(Functions.PM_SWEPTAREADENSITY_SWEEPWIDTHMETHOD, 25.0);

            String meanProc = "Mean" + var + "Density_Stratum";
            m.addProcess(meanProc, Functions.FN_MEANDENSITY).
                    setParameterProcessValue(Functions.PM_MEANDENSITY_PROCESSDATA, Functions.FN_READPROCESSDATA).
                    setParameterProcessValue(Functions.PM_MEANDENSITY_DENSITY, densProc).
                    setParameterValue(Functions.PM_MEANDENSITY_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_STRATUM);

            m.addProcess("Abundance" + var + "_Stratum", Functions.FN_ABUNDANCE).
                    setParameterProcessValue(Functions.PM_ABUNDANCE_DENSITY, meanProc).
                    setParameterProcessValue(Functions.PM_ABUNDANCE_POLYGONAREA, Functions.FN_STRATUMAREA);
        });
        // Write process data
        m.addProcess(Functions.FN_WRITEPROCESSDATA, Functions.FN_WRITEPROCESSDATA).
                setRespondInGUI(Boolean.TRUE);
    }

    private static void createLengthWeightRelationshipTemplateProject(IModel m) {
        m.setDescription("Length weight relationship");

        // Read process data
        m.addProcess(Functions.FN_READPROCESSDATA, Functions.FN_READPROCESSDATA);

        // Biotic related:
        m.addProcess(Functions.FN_READBIOTICXML, Functions.FN_READBIOTICXML).
                setFileOutput(false);

        m.addProcess(Functions.FN_FILTERBIOTIC, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_FILTERBIOTIC_BIOTICDATA, Functions.FN_READBIOTICXML).
                setParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR, "noname == 'SILDG03'").
                setRespondInGUI(true);

        // Process data related:
        m.addProcess(Functions.FN_DEFINESTRATA, Functions.FN_DEFINESTRATA).
                setParameterProcessValue(Functions.PM_DEFINESTRATA_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterValue(Functions.PM_DEFINESTRATA_USEPROCESSDATA, false).
                setRespondInGUI(true);

        m.addProcess(Functions.FN_DEFINESWEPTAREAPSU, Functions.FN_DEFINESWEPTAREAPSU).
                setParameterProcessValue(Functions.PM_DEFINESWEPTAREAPSU_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_DEFINESWEPTAREAPSU_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                setParameterValue(Functions.PM_DEFINESWEPTAREAPSU_METHOD, Functions.SWEPTAREAPSUMETHOD_STATION);

        m.addProcess(Functions.FN_LENGTHWEIGHTRELATIONSHIP, Functions.FN_LENGTHWEIGHTRELATIONSHIP).
                setParameterProcessValue(Functions.PM_SWEPTAREADENSITY_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_SWEPTAREADENSITY_BIOTICDATA, Functions.FN_FILTERBIOTIC);
        // Write process data
        m.addProcess(Functions.FN_WRITEPROCESSDATA, Functions.FN_WRITEPROCESSDATA).
                setRespondInGUI(Boolean.TRUE);
    }

    private static void createSweptAreaTotalCatchReport(IModel m) {
        m.addProcess(Functions.FN_TOTALABUNDANCE, Functions.FN_TOTALABUNDANCE).
                setParameterProcessValue(Functions.PM_TOTALABUNDANCE_COUNTABUNDANCE, "AbundanceCount_Stratum").
                setParameterProcessValue(Functions.PM_TOTALABUNDANCE_WEIGHTABUNDANCE, "AbundanceWeight_Stratum").
                setParameterValue(Functions.PM_TOTALABUNDANCE_SCALE, 1000);
    }

    private static void createSplitNASCProject(IModel m) {
        m.setDescription("Swept area");
        // Biotic related:
        m.addProcess(Functions.FN_READACOUSTICXML, Functions.FN_READACOUSTICXML).
                setFileOutput(false);

        m.addProcess(Functions.FN_FILTERACOUSTIC, Functions.FN_FILTERACOUSTIC).
                setParameterProcessValue(Functions.PM_FILTERACOUSTIC_ACOUSTICDATA, Functions.FN_READACOUSTICXML).
                setParameterValue(Functions.PM_FILTERACOUSTIC_FREQEXPR, "frequency == 38000 and transceiver == 2");

        m.addProcess(Functions.FN_NASC, Functions.FN_NASC).
                setParameterProcessValue(Functions.PM_NASC_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterValue(Functions.PM_NASC_LAYERTYPE, Functions.LAYERTYPE_PCHANNEL);

        m.addProcess(Functions.FN_READBIOTICXML, Functions.FN_READBIOTICXML).
                setFileOutput(false);

        m.addProcess(Functions.FN_FILTERBIOTIC, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_FILTERBIOTIC_BIOTICDATA, Functions.FN_READBIOTICXML).
                setParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR, "noname =~ ['SEI','HVITTING','TORSK','HYSE','ØYEPÅL']").
                setRespondInGUI(true);

        m.addProcess(Functions.FN_STATIONLENGTHDIST, Functions.FN_STATIONLENGTHDIST).
                setParameterProcessValue(Functions.PM_STATIONLENGTHDIST_BIOTICDATA, Functions.FN_FILTERBIOTIC).
                setParameterValue(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE, Functions.LENGTHDISTTYPE_NORMLENGHTDIST);

        m.addProcess(Functions.FN_REGROUPLENGTHDIST, Functions.FN_REGROUPLENGTHDIST).
                setParameterProcessValue(Functions.PM_REGROUPLENGTHDIST_LENGTHDIST, Functions.FN_STATIONLENGTHDIST).
                setParameterValue(Functions.PM_REGROUPLENGTHDIST_LENGTHINTERVAL, 1.0);

        m.addProcess(Functions.FN_SPLITNASCASSIGNMENT, Functions.FN_SPLITNASCASSIGNMENT).
                setParameterProcessValue(Functions.PM_SPLITNASCASSIGNMENT_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterValue(Functions.PM_SPLITNASCASSIGNMENT_RADIUS, 50.0).
                setParameterProcessValue(Functions.PM_SPLITNASCASSIGNMENT_BIOTICDATA, Functions.FN_FILTERBIOTIC);

        m.addProcess(Functions.FN_TOTALLENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_LENGTHDIST, Functions.FN_REGROUPLENGTHDIST).
                setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_PROCESSDATA, Functions.FN_SPLITNASCASSIGNMENT);

        m.addProcess(Functions.FN_SPLITNASC + "_BUNN", Functions.FN_SPLITNASC).
                setParameterProcessValue(Functions.PM_SPLITNASC_PROCESSDATA, Functions.FN_SPLITNASCASSIGNMENT).
                setParameterProcessValue(Functions.PM_SPLITNASC_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterProcessValue(Functions.PM_SPLITNASC_LENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                setParameterProcessValue(Functions.PM_SPLITNASC_NASC, Functions.FN_NASC).
                setParameterValue(Functions.PM_SPLITNASC_SPECIESTS, "2;22;SEI;20.0;-67.0;0.0/2;18;HVITTING;20.0;-67.0;0.0/2;31;TORSK;20.0;-66.0;0.0/2;30;HYSE;20.0;-65.0;0.0/2;28;ØYEPÅL;20.0;-67.0;0.0/2;24;KOLMULE;20.0;-67.0;0.0");

        m.addProcess(Functions.FN_SPLITNASC + "_BUNN2", Functions.FN_SPLITNASC).
                setParameterProcessValue(Functions.PM_SPLITNASC_PROCESSDATA, Functions.FN_SPLITNASCASSIGNMENT).
                setParameterProcessValue(Functions.PM_SPLITNASC_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterProcessValue(Functions.PM_SPLITNASC_LENGTHDIST, Functions.FN_TOTALLENGTHDIST).
                setParameterProcessValue(Functions.PM_SPLITNASC_NASC, Functions.FN_NASC).
                setParameterValue(Functions.PM_SPLITNASC_SPECIESTS, "52;22;SEI;20.0;-67.0;0.0/52;18;HVITTING;20.0;-67.0;0.0/52;31;TORSK;20.0;-66.0;0.0/52;30;HYSE;20.0;-65.0;0.0/52;28;ØYEPÅL;20.0;-67.0;0.0");

        m.addProcess(Functions.FN_COMBINENASC + "_SEI", Functions.FN_COMBINENASC).
                setParameterProcessValue(Functions.PM_COMBINENASC_NASC1, Functions.FN_SPLITNASC + "_BUNN").
                setParameterProcessValue(Functions.PM_COMBINENASC_NASC2, Functions.FN_SPLITNASC + "_BUNN2").
                setParameterProcessValue(Functions.PM_COMBINENASC_NASC3, Functions.FN_NASC).
                setParameterValue(Functions.PM_COMBINENASC_TARGETACOCAT, 22);

        m.addProcess(Functions.FN_COMBINENASC + "_TORSK", Functions.FN_COMBINENASC).
                setParameterProcessValue(Functions.PM_COMBINENASC_NASC1, Functions.FN_SPLITNASC + "_BUNN").
                setParameterProcessValue(Functions.PM_COMBINENASC_NASC2, Functions.FN_SPLITNASC + "_BUNN2").
                setParameterProcessValue(Functions.PM_COMBINENASC_NASC3, Functions.FN_NASC).
                setParameterValue(Functions.PM_COMBINENASC_TARGETACOCAT, 31);

        m.addProcess(Functions.FN_COMBINENASC, Functions.FN_COMBINENASC).
                setParameterProcessValue(Functions.PM_COMBINENASC_NASC1, Functions.FN_COMBINENASC + "_SEI").
                setParameterProcessValue(Functions.PM_COMBINENASC_NASC2, Functions.FN_COMBINENASC + "_TORSK");

        m.addProcess(Functions.FN_NASCTOACOUSTICDATA, Functions.FN_NASCTOACOUSTICDATA).
                setParameterProcessValue(Functions.PM_NASCTOACOUSTICDATA_ACOUSTICDATA, Functions.FN_FILTERACOUSTIC).
                setParameterProcessValue(Functions.PM_NASCTOACOUSTICDATA_NASC, Functions.FN_COMBINENASC);

        m.addProcess(Functions.FN_WRITEACOUSTICDATATOXML, Functions.FN_WRITEACOUSTICDATATOXML).
                setParameterProcessValue(Functions.PM_WRITEACOUSTICDATATOXML_ACOUSTICDATA, Functions.FN_NASCTOACOUSTICDATA).
                setParameterValue(Functions.PM_WRITEACOUSTICDATATOXML_FILENAME, "output/baseline/acoustic.xml");
    }

    private static void createECAProject(IModel m) {
        // Read process data
        m.addProcess(Functions.FN_READPROCESSDATA, Functions.FN_READPROCESSDATA);

        m.addProcess(Functions.FN_READLANDINGXML, Functions.FN_READLANDINGXML).
                setFileOutput(false);
        m.addProcess(Functions.FN_FILTERLANDING, Functions.FN_FILTERLANDING).
                setParameterProcessValue(Functions.PM_FILTERLANDING_LANDINGDATA, Functions.FN_READLANDINGXML).
                setParameterValue(Functions.PM_FILTERLANDING_SLUTTSEDDELEXPR, "LandingsKvartal =~ [1,2]").
                setParameterValue(Functions.PM_FILTERLANDING_FISKELINJEEXPR, "Fisk =~ ['1022','2524']").
                setRespondInGUI(true);

        m.addProcess(Functions.FN_APPLYPOSTODATA, Functions.FN_APPLYPOSTODATA).
                setParameterValue(Functions.PM_APPLYPOSTODATA_SOURCETYPE, Functions.SOURCETYPE_LANDING).
                setParameterProcessValue(Functions.PM_APPLYPOSTODATA_LANDINGDATA, Functions.FN_FILTERLANDING);
        m.addProcess(Functions.FN_APPLYPOSTODATA, Functions.FN_APPLYPOSTODATA).
                setParameterValue(Functions.PM_APPLYPOSTODATA_SOURCETYPE, Functions.SOURCETYPE_BIOTIC).
                setParameterProcessValue(Functions.PM_APPLYPOSTODATA_BIOTICDATA, Functions.FN_FILTERBIOTIC);
        m.addProcess(Functions.FN_ASSIGNDATATOSTRATUM, Functions.FN_ASSIGNDATATOSTRATUM).
                setParameterValue(Functions.PM_ASSIGNDATATOSTRATUM_SOURCETYPE, Functions.SOURCETYPE_LANDING).
                setParameterProcessValue(Functions.PM_ASSIGNDATATOSTRATUM_LANDINGDATA, Functions.FN_FILTERLANDING);
        m.addProcess(Functions.FN_ASSIGNDATATOSTRATUM, Functions.FN_ASSIGNDATATOSTRATUM).
                setParameterValue(Functions.PM_ASSIGNDATATOSTRATUM_SOURCETYPE, Functions.SOURCETYPE_BIOTIC).
                setParameterProcessValue(Functions.PM_ASSIGNDATATOSTRATUM_LANDINGDATA, Functions.FN_FILTERBIOTIC);
        m.addProcess(Functions.FN_READBIOTICXML, Functions.FN_READBIOTICXML).
                setFileOutput(false);

        m.addProcess(Functions.FN_FILTERBIOTIC, Functions.FN_FILTERBIOTIC).
                setParameterProcessValue(Functions.PM_FILTERBIOTIC_BIOTICDATA, Functions.FN_READBIOTICXML).
                setParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR, "species == '161722'").
                setRespondInGUI(true);

        // Process data related:
        m.addProcess(Functions.FN_DEFINESTRATA, Functions.FN_DEFINESTRATA).
                setParameterProcessValue(Functions.PM_DEFINESTRATA_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterValue(Functions.PM_DEFINESTRATA_USEPROCESSDATA, false).
                setRespondInGUI(true).setBreakInGUI(true);
        for (String sourceType : new String[]{Functions.SOURCETYPE_LANDING, Functions.SOURCETYPE_BIOTIC}) {
            m.addProcess(Functions.FN_DEFINETEMPORAL + sourceType, Functions.FN_DEFINETEMPORAL).
                    setParameterProcessValue(Functions.PM_DEFINETEMPORAL_PROCESSDATA, Functions.FN_READPROCESSDATA).
                    setParameterValue(Functions.PM_DEFINETEMPORAL_COVARIATETYPE, Functions.COVARIATETYPE_FIXED).
                    setParameterValue(Functions.PM_DEFINETEMPORAL_SOURCETYPE, sourceType).
                    setParameterProcessValue(sourceType.equals(Functions.SOURCETYPE_LANDING)
                            ? Functions.PM_DEFINETEMPORAL_LANDINGDATA : Functions.PM_DEFINETEMPORAL_BIOTICDATA,
                            sourceType.equals(Functions.SOURCETYPE_LANDING) ? Functions.FN_FILTERLANDING : Functions.FN_FILTERBIOTIC).
                    setParameterValue(Functions.PM_DEFINETEMPORAL_DEFINITIONMETHOD, sourceType.equals(Functions.SOURCETYPE_LANDING)
                            ? Functions.DEFINITIONMETHOD_USEDATA : Functions.DEFINITIONMETHOD_INHERIT).
                    setRespondInGUI(true).setBreakInGUI(true);
        }
        for (String covariateSourceType : new String[]{Functions.SOURCETYPE_LANDING, Functions.SOURCETYPE_BIOTIC}) {
            m.addProcess(Functions.FN_DEFINEGEARFACTOR + covariateSourceType, Functions.FN_DEFINEGEARFACTOR).
                    setParameterProcessValue(Functions.PM_DEFINEGEARFACTOR_PROCESSDATA, Functions.FN_READPROCESSDATA).
                    setParameterValue(Functions.PM_DEFINEGEARFACTOR_COVARIATETYPE, Functions.COVARIATETYPE_FIXED).
                    setParameterValue(Functions.PM_DEFINEGEARFACTOR_SOURCETYPE, covariateSourceType).
                    setParameterValue(Functions.PM_DEFINEGEARFACTOR_DEFINITIONMETHOD, covariateSourceType.equals(Functions.SOURCETYPE_LANDING)
                            ? Functions.DEFINITIONMETHOD_USEPROCESSDATA : Functions.DEFINITIONMETHOD_INHERITCOVARID).
                    setRespondInGUI(true).setBreakInGUI(true);
        }
        for (String covariateSourceType : new String[]{Functions.SOURCETYPE_LANDING, Functions.SOURCETYPE_BIOTIC}) {
            m.addProcess(Functions.FN_DEFINESPATIAL + covariateSourceType, Functions.FN_DEFINESPATIAL).
                    setParameterProcessValue(Functions.PM_DEFINETEMPORAL_PROCESSDATA, Functions.FN_READPROCESSDATA).
                    setParameterValue(Functions.PM_DEFINESPATIAL_COVARIATETYPE, Functions.COVARIATETYPE_FIXED).
                    setParameterValue(Functions.PM_DEFINESPATIAL_SOURCETYPE, covariateSourceType).
                    setParameterValue(Functions.PM_DEFINESPATIAL_DEFINITIONMETHOD, covariateSourceType.equals(Functions.SOURCETYPE_LANDING)
                            ? Functions.DEFINITIONMETHOD_USEDATA : Functions.DEFINITIONMETHOD_INHERIT).
                    setRespondInGUI(true).setBreakInGUI(true);
        }
        m.addProcess(Functions.FN_BIOTICCOVDATA, Functions.FN_BIOTICCOVDATA).
                setParameterProcessValue(Functions.PM_BIOTICCOVDATA_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_BIOTICCOVDATA_BIOTICDATA, Functions.FN_FILTERBIOTIC);
        m.addProcess(Functions.FN_LANDINGCOVDATA, Functions.FN_LANDINGCOVDATA).
                setParameterProcessValue(Functions.PM_LANDINGCOVDATA_PROCESSDATA, Functions.FN_READPROCESSDATA).
                setParameterProcessValue(Functions.PM_LANDINGCOVDATA_LANDINGDATA, Functions.FN_FILTERLANDING);
        m.addProcess(Functions.FN_LANDINGWEIGHTCOVDATA, Functions.FN_LANDINGWEIGHTCOVDATA).
                setParameterProcessValue(Functions.PM_LANDINGWEIGHTCOVDATA_LANDINGCOVDATA, Functions.FN_LANDINGCOVDATA);
        m.addProcess(Functions.FN_WRITEPROCESSDATA, Functions.FN_WRITEPROCESSDATA).
                setRespondInGUI(Boolean.TRUE);
    }

    private static void createECARModel(IModel m) {
        m.addProcess(Functions.FN_BASELINE2ECA, Functions.FN_BASELINE2ECA);
        m.addProcess(Functions.FN_ECA, Functions.FN_ECA).
                setParameterProcessValue(Functions.PM_ECA_DATA, Functions.FN_BASELINE2ECA);
    }

    /**
     * Create a R model for uncertainty. The functions resides in a rstox
     * library
     *
     * @param m
     */
    public static void createRWithUncertainty(IModel m, boolean imputeByAge, String bootstrapType) {
        boolean acousticTrawl = bootstrapType.equals(Functions.BOOTSTRAPMETHOD_ACOUSTICTRAWL);
        boolean sweptAreaTotal = bootstrapType.equals(Functions.BOOTSTRAPMETHOD_SWEPTAREATOTAL);
        IProcess p = m.addProcess(Functions.FN_RUNBOOTSTRAP, Functions.FN_RUNBOOTSTRAP).
                setParameterValue(Functions.PM_RUNBOOTSTRAP_BOOTSTRAPMETHOD, bootstrapType).
                setParameterValue(Functions.PM_RUNBOOTSTRAP_ACOUSTICMETHOD, acousticTrawl ? "PSU~Stratum" : "").
                setParameterValue(Functions.PM_RUNBOOTSTRAP_BIOTICMETHOD, "PSU~Stratum").
                setParameterProcessValue(Functions.PM_RUNBOOTSTRAP_STARTPROCESS, sweptAreaTotal ? "SweptAreaCountDensity" : Functions.FN_TOTALLENGTHDIST).
                setParameterProcessValue(Functions.PM_RUNBOOTSTRAP_ENDPROCESS, sweptAreaTotal ? "SweptAreaCountDensity" : Functions.FN_SUPERINDABUNDANCE).
                setParameterValue(Functions.PM_RUNBOOTSTRAP_NBOOT, 5).
                setParameterValue(Functions.PM_RUNBOOTSTRAP_SEED, 1).
                setParameterValue(Functions.PM_RUNBOOTSTRAP_CORES, 1);
        if (imputeByAge) {
            m.addProcess(Functions.FN_IMPUTEBYAGE, Functions.FN_IMPUTEBYAGE).
                    setParameterValue(Functions.PM_IMPUTEBYAGE_SEED, 1).
                    setParameterValue(Functions.PM_IMPUTEBYAGE_CORES, 1);
        }
        m.addProcess(Functions.FN_SAVEPROJECTDATA, Functions.FN_SAVEPROJECTDATA);
    }

    public static void createRReport(IModel m, String options) {
        m.addProcess(Functions.FN_GETREPORTS, Functions.FN_GETREPORTS).
                setParameterValue(Functions.PM_GETREPORTS_OPTIONS, options);
        
        m.addProcess(Functions.FN_GETPLOTS, Functions.FN_GETPLOTS).
                setParameterValue(Functions.PM_GETPLOTS_OPTIONS, options);
    }
}
