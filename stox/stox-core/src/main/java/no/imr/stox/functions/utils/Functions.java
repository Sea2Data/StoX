package no.imr.stox.functions.utils;

import java.util.Arrays;
import java.util.List;

/**
 * TODO what does this class do?
 *
 * @author aasmunds
 */
public final class Functions {

    //public static Boolean XMLDATA = false; // Use this swithc to turn on and off XMLDATA
    // Variables used as matrix dimensions or content
    // Assignments are specified per sampling unit (normally transect) and groups of channel layers (normally PB)
    public static final String DIM_ASSIGNMENT = "AssignmentID";
    // Channel layer is either single channels or layers (P or B) where P = PB - B. (!!PB is denoted P in the backend data). 
    public static final String DIM_LAYER = "Layer";
    // Acocat is the acoustic species dimension in NASC
    public static final String DIM_ACOCAT = "AcoCat";
    // EDSU is the elementary data sampling unit = 1 acoustic distance given by cruisenumber, date and time.
    public static final String DIM_EDSU = "EDSU";
    // Estimation layer is the vertical aggregation unit to sum the densities by. The NASC values may be aggregated to est.layer by choice.
    public static final String DIM_ESTLAYER = "EstLayer";
    // Length group is used in length distributions of different length intervals
    public static final String DIM_LENGRP = "LenGrp";

    // PSU is the primary sampling unit
    public static final String DIM_PSU = "PSU";
    // Sample unit is either PSU or EDSU, a generic term in horizontal aggregation
    public static final String DIM_SAMPLEUNIT = "SampleUnit";
    // Species is the name of the catchsample taxa.
    public static final String DIM_SPECCAT = "SpecCat";
    // Station is keys of year and serial number
    public static final String DIM_STATION = "Station";
    // Stratum is the main area with psus
    public static final String DIM_STRATUM = "Stratum";
    // Dimension 1 && 2
    //public static final String PM_VAR1 = "Var1";
    //public static final String PM_VAR2 = "Var2";

    // Variables used as matrix content
    // Densities are used in length distributions grouped by taxa, channelleyer and sample unit (edsu or psu).
    public static final String VAR_DENSITY = "Density";
    // Est layer expression is defined per estimation layer
    public static final String VAR_ESTLAYEREXPR = "EstLayerExpr";
    // NASC is grouped by channel layers and edsu
    public static final String VAR_NASC = "NASC";
    // Station weight in an asssignment 
    public static final String VAR_STATIONWEIGHT = "StationWeight";
    // Weighted counts are used in length distributions grouped by taxa/station or taxa/assignment
    public static final String VAR_WEIGHTEDCOUNT = "WeightedCount";
    // Weighted counts are used in length distributions grouped by taxa/station or taxa/assignment
    public static final String VAR_ABUNDANCE = "Abundance";

    // Data types
    public static final String DT_STRING = "String";
    public static final String DT_INTEGER = "Integer";
    public static final String DT_DOUBLE = "Double";
    public static final String DT_BOOLEAN = "Boolean";
    public static final String DT_BIOTICDATA = "BioticData";
    public static final String DT_ACOUSTICDATA = "AcousticData";
    public static final String DT_NASC = "NASC";
    public static final String DT_POLYGONAREA = "PolygonArea";
    public static final String DT_SAMPLEUNITS = "SampleUnits";
    public static final String DT_PROCESSDATA = "ProcessData";
    public static final String DT_MATRIX = "Matrix";

    // Process data matrix
    public static final String MM_BIOTICASSIGNMENT_MATRIX = "Matrix[ROW~AssignmentID / COL~Station / VAR~StationWeight]";
    public static final String MM_SUASSIGNMENT_MATRIX = "Matrix[ROW~SampleUnit / COL~EstLayer / VAR~AssignmentID]";
    public static final String MM_EDSUPSU_MATRIX = "Matrix[ROW~EDSU / VAR~PSU]";
    public static final String MM_PSUSTRATUM_MATRIX = "Matrix[ROW~PSU / VAR~Stratum]";
    public static final String MM_POLYGON_MATRIX = "Matrix[ROW~PolygonKey / COL~PolygonVariable / VAR~Value]";
    public static final String MM_COVARIATE_MATRIX = "Matrix[ROW~CovariateSourceType / COL~Covariate / VAR~Value]";
    public static final String MM_AGEERROR_MATRIX = "Matrix[ROW~ReadAge / COL~RealAge / VAR~Probability]";
    public static final String MM_COVARIATEPARAMETER_MATRIX = "Matrix[ROW~CovariateTable / COL~Parameter / VAR~Value]";

    // Baseline matrix
    public static final String MM_NASC_MATRIX = "Matrix[GROUP~AcoCat / ROW~SampleUnit / COL~Layer / VAR~NASC]";
    public static final String MM_LENGTHDIST_MATRIX = "Matrix[GROUP~SpecCat / ROW~Observation / CELL~LengthGroup / VAR~WeightedCount]";
    public static final String MM_DENSITY_MATRIX = "Matrix[GROUP~SpecCat / ROW~SampleUnit / COL~Layer / CELL~LengthGroup / VAR~Density]";
    public static final String MM_POLYGONAREA_MATRIX = "Matrix[ROW~PolygonKey / VAR~Area]";
    public static final String MM_ABUNDANCE_MATRIX = "Matrix[GROUP~SpecCat / ROW~SampleUnit / COL~EstLayer / CELL~LengthGroup / VAR~Abundance]";
    public static final String MM_SAMPLEUNIT_MATRIX = "Matrix[ROW~SampleUnit / VAR~Value]";
    public static final String MM_SPECCATSAMPLEUNITLAYER_MATRIX = "Matrix[GROUP~SpecCat / ROW~SampleUnit / COL~Layer / VAR~Value]";
    public static final String MM_VARIABLE_MATRIX = "Matrix[ROW~Variable / VAR~Value]";
    public static final String MM_DEFINEESTLAYER_MATRIX = "Matrix[ROW~EstLayer / VAR~EstLayerExpr]";
    public static final String MM_INDIVIDUALDATASTATIONS_MATRIX = "Matrix[ROW~SampleUnit / COL~EstLayer / CELL~Observation / VAR~Included]";
    public static final String MM_INDIVIDUALDATA_MATRIX = "Matrix[GROUP~SpecCat / ROW~SampleUnit / COL~EstLayer / CELL~LengthGroup / VAR~Individuals]";
    public static final String MM_SUPERINDABUNDANCE_MATRIX = "Matrix[ROW~Individual / COL~IndVariable / VAR~Value]";
    public static final String MM_SAMPLEUNITVARIABLE_MATRIX = "Matrix[ROW~SampleUnit / COL~Variable / VAR~Value]";
    public static final String MM_SAMPLEUNITS_MATRIX = "Matrix[ROW~Stratum / COL~PSU / VAR~EDSU]";
    
    // Report matrices
    public static final String MM_XCATBYLENGTH_MATRIX = "Matrix[GROUP~SPEC_ESTL_STRATA / ROW~LengthGroup / COL~XCat / CELL~Variable / VAR~Value]";
    public static final String MM_TOTALABUNDANCEREPORT_MATRIX = "Matrix[GROUP~SpecCat / ROW~EstLayer / COL~Stratum / CELL~Variable / VAR~Value]";

    public static final String MM_BIOTICCOVDATA_MATRIX = "Matrix[ROW~CovariateKeys / VAR~Individuals]";
    public static final String MM_LANDINGCOVDATA_MATRIX = "Matrix[ROW~CovariateKeys / VAR~Landings]";
    public static final String MM_LANDINGWEIGHTCOVDATA_MATRIX = "Matrix[GROUP~Temporal / ROW~GearFactor / COL~Spatial / VAR~Weight]";
    // Data resolution types
    public static final String RES_SAMPLEUNITTYPE = "SampleUnitType";
    public static final String RES_LAYERTYPE = "LayerType";
    public static final String RES_OBSERVATIONTYPE = "ObservationType";
    public static final String RES_LENGTHINTERVAL = "LengthInterval";
    public static final String RES_LENGTHDISTTYPE = "LengthDistType";

    // Data storage
    public static final String DS_FILE = "FILE";
    public static final String DS_MEM = "MEM";
    public static final String DS_BIOTIC = "BIOTIC";
    public static final String DS_ECHO = "ECHO";
    public static final String DS_ABUNDANCEBYINDIVIDUAL = "ABUNDANCEBYINDIVIDUAL";
    public static final String DS_ABUNDANCEBYPOPCATEGORY = "ABUNDANCEBYPOPCATEGORY";

    // Implicit parameter to processes
    public static final String PM_RFOLDER = "RFOLDER";
    public static final String PM_LOGGER = "LOGGER";
    public static final String PM_PROJECTFOLDER = "PROJECTFOLDER";
    public static final String PM_MODEL = "MODEL";

    // Functions and parameters
    public static final String FN_READPROCESSDATA = "ReadProcessData";
    public static final String PM_READPROCESSDATA_USEPROCESSDATA = "UseProcessData";

    public static final String FN_WRITEPROCESSDATA = "WriteProcessData";

    public static final String FN_READBIOTICXML = "ReadBioticXML";
    public static final String PM_READBIOTICXML_FILENAME = "FileName";

    public static final String FN_FILTERBIOTIC = "FilterBiotic";
    public static final String PM_FILTERBIOTIC_BIOTICDATA = "BioticData";
    public static final String PM_FILTERBIOTIC_FISHSTATIONEXPR = "FishStationExpr";
    public static final String PM_FILTERBIOTIC_CATCHEXPR = "CatchExpr";
    public static final String PM_FILTERBIOTIC_SAMPLEEXPR = "SampleExpr";
    public static final String PM_FILTERBIOTIC_INDEXPR = "IndExpr";

    public static final String FN_READLANDINGXML = "ReadLandingXML";
    public static final String PM_READLANDINGXML_FILENAME1 = "FileName1";
    public static final String PM_READLANDINGXML_FILENAME2 = "FileName2";
    public static final String PM_READLANDINGXML_FILENAME3 = "FileName3";

    public static final String FN_FILTERLANDING = "FilterLanding";
    public static final String PM_FILTERLANDING_LANDINGDATA = "LandingData";
    public static final String PM_FILTERLANDING_SLUTTSEDDELEXPR = "SluttSeddelExpr";
    public static final String PM_FILTERLANDING_FISKELINJEEXPR = "FiskeLinjeExpr";

    public static final String FN_APPLYPOSTODATA = "ApplyPosToData";
    public static final String PM_APPLYPOSTODATA_SOURCETYPE = "SourceType";
    public static final String PM_APPLYPOSTODATA_AREACODING = "AreaCoding";
    public static final String PM_APPLYPOSTODATA_LANDINGDATA = "LandingData";
    public static final String PM_APPLYPOSTODATA_BIOTICDATA = "BioticData";
    public static final String PM_APPLYPOSTODATA_FILENAME = "FileName";

    public static final String FN_ASSIGNDATATOSTRATUM = "ApplyDataToStratum";
    public static final String PM_ASSIGNDATATOSTRATUM_PROCESSDATA = "ProcessData";
    public static final String PM_ASSIGNDATATOSTRATUM_SOURCETYPE = "SourceType";
    public static final String PM_ASSIGNDATATOSTRATUM_LANDINGDATA = "LandingData";
    public static final String PM_ASSIGNDATATOSTRATUM_BIOTICDATA = "BioticData";

    public static final String FN_READACOUSTICXML = "ReadAcousticXML";
    public static final String PM_READACOUSTICXML_FILENAME = "FileName";

    public static final String FN_READACOUSTICLUF5 = "ReadAcousticLUF5";
    public static final String PM_READACOUSTICLUF5_ACOUSTICDATA = "AcousticData";
    public static final String PM_READACOUSTICLUF5_FILENAME = "FileName";

    public static final String FN_APPENDSPECCAT = "AppendSpecCat";
    public static final String PM_APPENDSPECCAT_BIOTICDATA = "BioticData";
    public static final String PM_APPENDSPECCAT_SPECCAT = "SpecCat";
    public static final String PM_APPENDSPECCAT_SPECCATMETHOD = "SpecCatMethod";
    public static final String PM_APPENDSPECCAT_FILENAME = "FileName";
    public static final String PM_APPENDSPECCAT_SPECVARSTOX = "SpecVarStoX";
    public static final String PM_APPENDSPECCAT_SPECVARREF = "SpecVarRef";
    public static final String PM_APPENDSPECCAT_SPECCATREF = "SpecCatRef";

    public static final String FN_FILTERACOUSTIC = "FilterAcoustic";
    public static final String PM_FILTERACOUSTIC_ACOUSTICDATA = "AcousticData";
    public static final String PM_FILTERACOUSTIC_DISTANCEEXPR = "DistanceExpr";
    public static final String PM_FILTERACOUSTIC_NASCEXPR = "NASCExpr";
    public static final String PM_FILTERACOUSTIC_FREQEXPR = "FreqExpr";

    public static final String FN_NASC = "NASC";
    public static final String PM_NASC_ACOUSTICDATA = "AcousticData";
    public static final String PM_NASC_LAYERTYPE = "LayerType";

    public static final String FN_MEANNASC = "MeanNASC";
    public static final String PM_MEANNASC_NASC = "NASC";
    public static final String PM_MEANNASC_PROCESSDATA = "ProcessData";
    public static final String PM_MEANNASC_SAMPLEUNITTYPE = "SampleUnitType";
    //public static final String PM_MEANNASC_AGGVERTICAL = "AggregateVertical";

    public static final String FN_SPLITNASC = "SplitNASC";
    public static final String PM_SPLITNASC_NASC = "NASC";
    public static final String PM_SPLITNASC_PROCESSDATA = "ProcessData";
    public static final String PM_SPLITNASC_MIXACOCAT = "MixAcoCat";
    public static final String PM_SPLITNASC_SPECIESTS = "SpeciesTS";
    public static final String PM_SPLITNASC_LENGTHDIST = "LengthDist";
    public static final String PM_SPLITNASC_ACOUSTICDATA = "AcousticData";

    public static final String FN_COMBINENASC = "CombineNASC";
    public static final String PM_COMBINENASC_TARGETACOCAT = "TargetAcoCat";
    public static final String PM_COMBINENASC_NASC1 = "NASC1";
    public static final String PM_COMBINENASC_NASC2 = "NASC2";
    public static final String PM_COMBINENASC_NASC3 = "NASC3";
    public static final String PM_COMBINENASC_NASC4 = "NASC4";
    public static final String PM_COMBINENASC_NASC5 = "NASC5";

    public static final String FN_NASCTOACOUSTICDATA = "NASCToAcousticData";
    public static final String PM_NASCTOACOUSTICDATA_ACOUSTICDATA = "AcousticData";
    public static final String PM_NASCTOACOUSTICDATA_NASC = "NASC";

    public static final String FN_WRITEACOUSTICDATATOXML = "WriteAcousticDataToXML";
    public static final String PM_WRITEACOUSTICDATATOXML_ACOUSTICDATA = "AcousticData";
    public static final String PM_WRITEACOUSTICDATATOXML_FILENAME = "FileName";

    public static final String FN_STATIONLENGTHDIST = "StationLengthDist";
    public static final String PM_STATIONLENGTHDIST_BIOTICDATA = "BioticData";
    //public static final String PM_STATIONLENGTHDIST_LENGTHINTERVAL = "LengthInterval";
    public static final String PM_STATIONLENGTHDIST_LENGTHDISTTYPE = "LengthDistType";
//    public static final String PM_STATIONLENGTHDIST_INPERCENT = "InPercent";
//    public static final String PM_STATIONLENGTHDIST_NORMTODIST = "NormToDist";

    public static final String FN_REGROUPLENGTHDIST = "RegroupLengthDist";
    public static final String PM_REGROUPLENGTHDIST_LENGTHDIST = "LengthDist";
    public static final String PM_REGROUPLENGTHDIST_LENGTHINTERVAL = "LengthInterval";

    public static final String FN_RELLENGTHDIST = "RelLengthDist";
    public static final String PM_RELLENGTHDIST_LENGTHDIST = "LengthDist";

    public static final String FN_CATCHABILITY = "Catchability";
    public static final String PM_CATCHABILITY_CATCHABILITYMETHOD = "CatchabilityMethod";
    public static final String PM_CATCHABILITY_LENGTHDIST = "LengthDist";
    public static final String PM_CATCHABILITY_BIOTICDATA = "BioticData";
    public static final String PM_CATCHABILITY_PARLENGTHDEPENDENTSWEEPWIDTH = "ParLengthDependentSweepWidth";
    public static final String PM_CATCHABILITY_PARLENGTHDEPENDENTSELECTIVITY = "ParLengthDependentSelectivity";

    public static final String FN_DEFINEACOUSTICPSU = "DefineAcousticPSU";
    public static final String PM_DEFINEACOUSTICPSU_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINEACOUSTICPSU_ACOUSTICDATA = "AcousticData";
    public static final String PM_DEFINEACOUSTICPSU_DEFINITIONMETHOD = "DefinitionMethod";
    public static final String PM_DEFINEACOUSTICPSU_USEPROCESSDATA = "UseProcessData";

    public static final String FN_DEFINESWEPTAREAPSU = "DefineSweptAreaPSU";
    public static final String PM_DEFINESWEPTAREAPSU_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINESWEPTAREAPSU_BIOTICDATA = "BioticData";
    public static final String PM_DEFINESWEPTAREAPSU_METHOD = "Method";

    public static final String FN_DEFINERECTANGLE = "DefineRectangle";
    public static final String PM_DEFINERECTANGLE_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINERECTANGLE_ACOUSTICDATA = "AcousticData";
    public static final String PM_DEFINERECTANGLE_WIDTH = "Width";
    public static final String PM_DEFINERECTANGLE_HEIGHT = "Height";

    public static final String FN_BIOSTATIONASSIGNMENT = "BioStationAssignment";
    public static final String PM_BIOSTATIONASSIGNMENT_PROCESSDATA = "ProcessData";
    public static final String PM_BIOSTATIONASSIGNMENT_BIOTICDATA = "BioticData";
    //public static final String PM_BIOSTATIONASSIGNMENT_NASC = "NASC";
    public static final String PM_BIOSTATIONASSIGNMENT_ASSIGNMENTMETHOD = "AssignmentMethod";
    public static final String PM_BIOSTATIONASSIGNMENT_RADIUS = "Radius";
    public static final String PM_BIOSTATIONASSIGNMENT_ACOUSTICDATA = "AcousticData";
    public static final String PM_BIOSTATIONASSIGNMENT_ESTLAYERS = "EstLayers";
    public static final String PM_BIOSTATIONASSIGNMENT_MINNUMSTATIONS = "MinNumStations";
    public static final String PM_BIOSTATIONASSIGNMENT_REFGCDISTANCE = "RefGCDistance";
    public static final String PM_BIOSTATIONASSIGNMENT_REFTIME = "RefTime";
    public static final String PM_BIOSTATIONASSIGNMENT_REFBOTDEPTH = "RefBotDepth";
    public static final String PM_BIOSTATIONASSIGNMENT_REFLATITUDE = "RefLatitude";
    public static final String PM_BIOSTATIONASSIGNMENT_REFLONGITUDE = "RefLongitude";

    public static final String FN_SPLITNASCASSIGNMENT = "SplitNASCAssignment";
    public static final String PM_SPLITNASCASSIGNMENT_ACOUSTICDATA = "AcousticData";
    public static final String PM_SPLITNASCASSIGNMENT_BIOTICDATA = "BioticData";
    public static final String PM_SPLITNASCASSIGNMENT_RADIUS = "Radius";

    public static final String FN_BIOSTATIONWEIGHTING = "BioStationWeighting";
    public static final String PM_BIOSTATIONWEIGHTING_PROCESSDATA = "ProcessData";
    public static final String PM_BIOSTATIONWEIGHTING_BIOTICDATA = "BioticData";
    public static final String PM_BIOSTATIONWEIGHTING_ACOUSTICDATA = "AcousticData";
    public static final String PM_BIOSTATIONWEIGHTING_NASC = "NASC";
    public static final String PM_BIOSTATIONWEIGHTING_LENGTHDIST = "LengthDist";
    public static final String PM_BIOSTATIONWEIGHTING_WEIGHTINGMETHOD = "WeightingMethod";
    public static final String PM_BIOSTATIONWEIGHTING_RADIUS = "Radius";
    public static final String PM_BIOSTATIONWEIGHTING_M = "m";
    public static final String PM_BIOSTATIONWEIGHTING_A = "a";
    public static final String PM_BIOSTATIONWEIGHTING_MAXNUMLENGTHSAMPLES = "MaxNumLengthSamples";

    public static final String FN_RECTANGLEASSIGNMENT = "RectangleAssignment";
    public static final String PM_RECTANGLEASSIGNMENT_PROCESSDATA = "ProcessData";
    public static final String PM_RECTANGLEASSIGNMENT_BIOTICDATA = "BioticData";
    public static final String PM_RECTANGLEASSIGNMENT_NASC = "NASC";
    public static final String PM_RECTANGLEASSIGNMENT_USEPROCESSDATA = "UseProcessData";
    public static final String PM_RECTANGLEASSIGNMENT_ESTLAYERS = "EstLayers";

    public static final String FN_TOTALLENGTHDIST = "TotalLengthDist";
    public static final String PM_TOTALLENGTHDIST_LENGTHDIST = "LengthDist";
    public static final String PM_TOTALLENGTHDIST_PROCESSDATA = "ProcessData";
//    public static final String PM_TOTALLENGTHDIST_AGGREGATION = "Aggregation";

    public static final String FN_ACOUSTICDENSITY = "AcousticDensity";
    public static final String PM_ACOUSTICDENSITY_LENGTHDIST = "LengthDist";
    public static final String PM_ACOUSTICDENSITY_PROCESSDATA = "ProcessData";
    public static final String PM_ACOUSTICDENSITY_NASC = "NASC";
    public static final String PM_ACOUSTICDENSITY_M = "m";
    public static final String PM_ACOUSTICDENSITY_A = "a";
    public static final String PM_ACOUSTICDENSITY_D = "d";

    public static final String FN_SWEPTAREADENSITY = "SweptAreaDensity";
    public static final String PM_SWEPTAREADENSITY_PROCESSDATA = "ProcessData";
    public static final String PM_SWEPTAREADENSITY_SWEPTAREAMETHOD = "SweptAreaMethod";
    public static final String PM_SWEPTAREADENSITY_BIOTICDATA = "BioticData";
    public static final String PM_SWEPTAREADENSITY_CATCHVARIABLE = "CatchVariable";
    public static final String PM_SWEPTAREADENSITY_LENGTHDIST = "LengthDist";
    public static final String PM_SWEPTAREADENSITY_DISTANCEMETHOD = "DistanceMethod";
    public static final String PM_SWEPTAREADENSITY_SWEEPWIDTHMETHOD = "SweepWidthMethod";
    public static final String PM_SWEPTAREADENSITY_SWEEPWIDTH = "SweepWidth";
    public static final String PM_SWEPTAREADENSITY_ALPHA = "Alpha";
    public static final String PM_SWEPTAREADENSITY_BETA = "Beta";
    public static final String PM_SWEPTAREADENSITY_LMIN = "LMin";
    public static final String PM_SWEPTAREADENSITY_LMAX = "LMax";
    public static final String PM_SWEPTAREADENSITY_SWEEPWIDTHEXPR = "SweepWidthExpr";

    public static final String FN_LENGTHWEIGHTRELATIONSHIP = "LengthWeightRelationship";
    public static final String PM_LENGTHWEIGHTRELATIONSHIP_PROCESSDATA = "ProcessData";
    public static final String PM_LENGTHWEIGHTRELATIONSHIP_BIOTICDATA = "BioticData";

    public static final String FN_LARVAEDENSITY = "LarvaeDensity";
    public static final String PM_LARVAEDENSITY_PROCESSDATA = "ProcessData";
    public static final String PM_LARVAEDENSITY_LENGTHDIST = "LengthDist";
    public static final String PM_LARVAEDENSITY_BIOTICDATA = "BioticData";
    public static final String PM_LARVAEDENSITY_GEAROPENINGAREA = "GearOpeningArea";

    public static final String FN_MEANDENSITY = "MeanDensity";
    public static final String PM_MEANDENSITY_PROCESSDATA = "ProcessData";
    public static final String PM_MEANDENSITY_DENSITY = "Density";
    public static final String PM_MEANDENSITY_SAMPLEUNITTYPE = "SampleUnitType";

    public static final String FN_SUMDENSITY = "SumDensity";
    public static final String PM_SUMDENSITY_DENSITY = "Density";

    public static final String FN_SUMABUNDANCE = "SumAbundance";
    public static final String PM_SUMABUNDANCE_PROCESSDATA = "ProcessData";
    public static final String PM_SUMABUNDANCE_ABUNDANCE = "Abundance";

    public static final String FN_DEFINESTRATA = "DefineStrata";
    public static final String PM_DEFINESTRATA_FILENAME = "FileName";
    public static final String PM_DEFINESTRATA_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINESTRATA_USEPROCESSDATA = "UseProcessData";

    public static final String FN_STRATUMAREA = "StratumArea";
    public static final String PM_STRATUMAREA_PROCESSDATA = "ProcessData";
    public static final String PM_STRATUMAREA_AREAMETHOD = "AreaMethod";
    public static final String AREAMETHOD_SIMPLE = "Simple";
    public static final String AREAMETHOD_ACCURATE = "Accurate";

    public static final String FN_RECTANGLEAREA = "RectangleArea";
    public static final String PM_RECTANGLEAREA_PROCESSDATA = "ProcessData";

    public static final String FN_ABUNDANCE = "Abundance";
    public static final String PM_ABUNDANCE_DENSITY = "Density";
    public static final String PM_ABUNDANCE_POLYGONAREA = "PolygonArea";

    public static final String FN_REGROUPABUNDANCE = "RegroupAbundance";
    public static final String PM_REGROUPABUNDANCE_ABUNDANCE = "Abundance";
    public static final String PM_REGROUPABUNDANCE_LENGTHINTERVAL = "LengthInterval";

    public static final String FN_INDIVIDUALDATASTATIONS = "IndividualDataStations";
    public static final String PM_INDIVIDUALDATASTATIONS_ABUNDANCE = "Abundance";
    public static final String PM_INDIVIDUALDATASTATIONS_PROCESSDATA = "ProcessData";

    public static final String FN_INDIVIDUALDATA = "IndividualData";
    public static final String PM_INDIVIDUALDATA_BIOTICDATA = "BioticData";
    public static final String PM_INDIVIDUALDATA_INDIVIDUALDATASTATIONS = "IndividualDataStations";

    public static final String FN_CORRECTFORINNSUFFICIENTSAMPLING = "CorrectForInnsufficientSampling";
    public static final String PM_CORRECTFORINNSUFFICIENTSAMPLING_INDIVIDUALDATA = "IndividualData";
    public static final String PM_CORRECTFORINNSUFFICIENTSAMPLING_ABUNDANCE = "Abundance";

    public static final String FN_SUPERINDABUNDANCE = "SuperIndAbundance";
    public static final String PM_SUPERINDABUNDANCE_PROCESSDATA = "ProcessData";
    public static final String PM_SUPERINDABUNDANCE_INDIVIDUALDATA = "IndividualData";
    public static final String PM_SUPERINDABUNDANCE_ABUNDANCE = "Abundance";
    public static final String PM_SUPERINDABUNDANCE_ABUNDWEIGHTMETHOD = "AbundWeightMethod";
    public static final String PM_SUPERINDABUNDANCE_LENGTHDIST = "LengthDist";

    public static final String FN_FILLMISSINGDATA = "FillMissingData";
    public static final String PM_FILLMISSINGDATA_SUPERINDIVIDUALS = "SuperIndividuals";
    public static final String PM_FILLMISSINGDATA_FILLVARIABLES = "FillVariables";
    public static final String PM_FILLMISSINGDATA_FILLWEIGHT = "FillWeight";
    public static final String PM_FILLMISSINGDATA_FILENAME = "FileName";
    public static final String PM_FILLMISSINGDATA_A = "a";
    public static final String PM_FILLMISSINGDATA_B = "b";
    public static final String PM_FILLMISSINGDATA_SEED = "Seed";
    
    public static final String FN_FILLMISSINGDATA_2_4 = "FillMissingData_2_4";
    public static final String PM_FILLMISSINGDATA_SUPERINDIVIDUALS_2_4 = "SuperIndividuals";
    public static final String PM_FILLMISSINGDATA_FILLVARIABLES_2_4 = "FillVariables";
    public static final String PM_FILLMISSINGDATA_FILLWEIGHT_2_4 = "FillWeight";
    public static final String PM_FILLMISSINGDATA_FILENAME_2_4 = "FileName";
    public static final String PM_FILLMISSINGDATA_A_2_4 = "a";
    public static final String PM_FILLMISSINGDATA_B_2_4 = "b";
    public static final String PM_FILLMISSINGDATA_SEED_2_4 = "Seed";

    public static final String FN_ESTIMATEBYPOPULATIONCATEGORY = "EstimateByPopulationCategory";
    public static final String PM_ESTIMATEBYPOPCATEGORY_SUPERINDIVIDUALS = "SuperIndividuals";
    public static final String PM_ESTIMATEBYPOPCATEGORY_LENGTHINTERVAL = "LengthInterval";
    public static final String PM_ESTIMATEBYPOPCATEGORY_SCALE = "Scale";
    public static final String PM_ESTIMATEBYPOPCATEGORY_DIM1 = "Dim1";
    public static final String PM_ESTIMATEBYPOPCATEGORY_DIM2 = "Dim2";
    public static final String PM_ESTIMATEBYPOPCATEGORY_DIM3 = "Dim3";
    public static final String PM_ESTIMATEBYPOPCATEGORY_DIM4 = "Dim4";
    public static final String PM_ESTIMATEBYPOPCATEGORY_DIM5 = "Dim5";

    public static final String FN_TOTALABUNDANCE = "TotalAbundance";
    public static final String PM_TOTALABUNDANCE_WEIGHTABUNDANCE = "WeightAbundance";
    public static final String PM_TOTALABUNDANCE_COUNTABUNDANCE = "CountAbundance";
    public static final String PM_TOTALABUNDANCE_SCALE = "Scale";

    public static final String FN_DATRAS = "DATRASConvert";
    public static final String PM_DATRAS_BIOTICDATA = "BioticData";

    public static final String FN_DEFINETEMPORAL = "DefineTemporal";
    public static final String PM_DEFINETEMPORAL_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINETEMPORAL_SOURCETYPE = "SourceType";
    public static final String PM_DEFINETEMPORAL_COVARIATETYPE = "CovariateType";
    public static final String PM_DEFINETEMPORAL_DEFINITIONMETHOD = "DefinitionMethod";
    public static final String PM_DEFINETEMPORAL_LANDINGDATA = "LandingData";
    public static final String PM_DEFINETEMPORAL_BIOTICDATA = "BioticData";
    //public static final String PM_DEFINETEMPORAL_FILENAME = "FileName";
    public static final String PM_DEFINETEMPORAL_TIMEINTERVAL = "TimeInterval";
    public static final String PM_DEFINETEMPORAL_SEASONAL = "Seasonal";

    public static final String FN_DEFINEGEARFACTOR = "DefineGearFactor";
    public static final String PM_DEFINEGEARFACTOR_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINEGEARFACTOR_SOURCETYPE = "SourceType";
    public static final String PM_DEFINEGEARFACTOR_COVARIATETYPE = "CovariateType";
    public static final String PM_DEFINEGEARFACTOR_DEFINITIONMETHOD = "DefinitionMethod";
    public static final String PM_DEFINEGEARFACTOR_LANDINGDATA = "LandingData";
    public static final String PM_DEFINEGEARFACTOR_BIOTICDATA = "BioticData";
    public static final String PM_DEFINEGEARFACTOR_FILENAME = "FileName";

    public static final String FN_DEFINESPATIAL = "DefineSpatial";
    public static final String PM_DEFINESPATIAL_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINESPATIAL_SOURCETYPE = "SourceType";
    public static final String PM_DEFINESPATIAL_COVARIATETYPE = "CovariateType";
    public static final String PM_DEFINESPATIAL_USESTRATUMNEIGHBOUR = "UseStratumNeighbour";
    public static final String PM_DEFINESPATIAL_DEFINITIONMETHOD = "DefinitionMethod";
    public static final String PM_DEFINESPATIAL_LANDINGDATA = "LandingData";
    public static final String PM_DEFINESPATIAL_BIOTICDATA = "BioticData";
    public static final String PM_DEFINESPATIAL_FILENAME = "FileName";
//    public static final String PM_DEFINESPATIAL_VAR1 = PM_VAR1;
//    public static final String PM_DEFINESPATIAL_VAR2 = PM_VAR2;

    public static final String FN_DEFINEPLATFORM = "DefinePlatform";
    public static final String PM_DEFINEPLATFORM_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINEPLATFORM_SOURCETYPE = "SourceType";
    public static final String PM_DEFINEPLATFORM_COVARIATETYPE = "CovariateType";
    public static final String PM_DEFINEPLATFORM_DEFINITIONMETHOD = "DefinitionMethod";
    public static final String PM_DEFINEPLATFORM_BIOTICDATA = "BioticData";

    public static final String FN_BIOTICCOVDATA = "BioticCovData";
    public static final String PM_BIOTICCOVDATA_PROCESSDATA = "ProcessData";
    public static final String PM_BIOTICCOVDATA_BIOTICDATA = "BioticData";

    public static final String FN_LANDINGCOVDATA = "LandingCovData";
    public static final String PM_LANDINGCOVDATA_PROCESSDATA = "ProcessData";
    public static final String PM_LANDINGCOVDATA_LANDINGDATA = "LandingData";

    public static final String FN_LANDINGWEIGHTCOVDATA = "LandingWeightCovData";
    public static final String PM_LANDINGWEIGHTCOVDATA_LANDINGCOVDATA = "LandingCovData";

    public static final String FN_DEFINEAGEERRORMATRIX = "DefineAgeErrorMatrix";
    public static final String PM_DEFINEAGEERRORMATRIX_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINEAGEERRORMATRIX_DEFINITIONMETHOD = "DefinitionMethod";
    public static final String PM_DEFINEAGEERRORMATRIX_FILENAME = "FileName";

    public static final String FN_CONVERTLENGTHANDWEIGHT = "ConvertLengthAndWeight";
    public static final String PM_CONVERTLENGTHANDWEIGHT_BIOTICDATA = "BioticData";
    public static final String PM_CONVERTLENGTHANDWEIGHT_HEADCUTFACA = "HeadCutFacA";
    public static final String PM_CONVERTLENGTHANDWEIGHT_HEADCUTFACB = "HeadCutFacB";
    public static final String PM_CONVERTLENGTHANDWEIGHT_WGUTHEADOFF = "WGutHeadOff";
    public static final String PM_CONVERTLENGTHANDWEIGHT_WGUTHEADON = "WGutHeadOn";

    public static final String FN_DEFINESTRATUMNEIGHBOUR = "DefineStratumNeighbour";
    public static final String PM_DEFINESTRATUMNEIGHBOUR_PROCESSDATA = "ProcessData";
    public static final String PM_DEFINESTRATUMNEIGHBOUR_DEFINITIONMETHOD = "DefinitionMethod";
    public static final String PM_DEFINESTRATUMNEIGHBOUR_FILENAME = "FileName";

    // R model functions:
    public static final String FN_SAVEPROJECTDATA = "saveProjectData";

    /*public static final String FN_LOADENV = "loadEnv";
    public static final String PM_LOADENV_FILEBASENAME = "fileBaseName";
    public static final String PM_LOADENV_OUTPUTFOLDER = "outputFolder";
     */
    public static final String FN_RUNBOOTSTRAP = "runBootstrap";
    public static final String PM_RUNBOOTSTRAP_BOOTSTRAPMETHOD = "bootstrapMethod";
    public static final String PM_RUNBOOTSTRAP_ACOUSTICMETHOD = "acousticMethod";
    public static final String PM_RUNBOOTSTRAP_BIOTICMETHOD = "bioticMethod";
    public static final String PM_RUNBOOTSTRAP_STARTPROCESS = "startProcess";
    public static final String PM_RUNBOOTSTRAP_ENDPROCESS = "endProcess";
    public static final String PM_RUNBOOTSTRAP_NBOOT = "nboot";
    public static final String PM_RUNBOOTSTRAP_SEED = "seed";
    public static final String PM_RUNBOOTSTRAP_CORES = "cores";

    public static final String FN_RUNBOOTSTRAP_1_6 = "runBootstrap_1.6";
    public static final String PM_RUNBOOTSTRAP_1_6_ACOUSTICMETHOD = "acousticMethod";
    public static final String PM_RUNBOOTSTRAP_1_6_BIOTICMETHOD = "bioticMethod";
    public static final String PM_RUNBOOTSTRAP_1_6_NBOOT = "nboot";
    public static final String PM_RUNBOOTSTRAP_1_6_SEED = "seed";
    public static final String PM_RUNBOOTSTRAP_1_6_CORES = "cores";

    public static final String FN_BASELINE2ECA = "baseline2eca";

    public static final String FN_ECA = "eca";
    public static final String PM_ECA_DATA = "data";

    public static final String FN_PLOTNASCDISTRIBUTION = "plotNASCDistribution";

    public static final String FN_PLOTABUNDANCE = "plotAbundance";
    public static final String PM_PLOTABUNDANCE_GRP1 = "grp1";
    public static final String PM_PLOTABUNDANCE_GRP2 = "grp2";
    public static final String PM_PLOTABUNDANCE_NUMBERSCALE = "numberscale";

    public static final String FN_IMPUTEBYAGE = "imputeByAge";
    public static final String PM_IMPUTEBYAGE_SEED = "seed";
    public static final String PM_IMPUTEBYAGE_CORES = "cores";

    public static final String FN_GETPLOTS = "getPlots";
    public static final String PM_GETPLOTS_OUT = "out";
    public static final String PM_GETPLOTS_OPTIONS = "options";

    public static final String FN_GETREPORTS = "getReports";
    public static final String PM_GETREPORTS_OUT = "out";
    public static final String PM_GETREPORTS_OPTIONS = "options";

    public static final String FN_SAMPLEUNIT = "SampleUnit";
    public static final String PM_SAMPLEUNIT_PROCESSDATA = "ProcessData";

    public static final String COL_POLVAR_INCLUDEINTOTAL = "includeintotal";
    public static final String COL_POLVAR_POLYGON = "polygon";

    public static final String COL_ABNDBYIND_SPECCAT = DIM_SPECCAT;
    public static final String COL_ABNDBYIND_STRATUM = DIM_STRATUM;
    public static final String COL_ABNDBYIND_ESTLAYER = DIM_ESTLAYER;
    public static final String COL_ABNDBYIND_LENGRP = DIM_LENGRP;
    public static final String COL_ABNDBYIND_LENINTV = "LenIntv";
    public static final String COL_ABNDBYIND_ABUNDANCE = "Abundance";
    public static final String COL_ABNDBYIND_BIOMASS = "Biomass";
    public static final String COL_ABNDBYIND_INCLUDEINTOTAL = COL_POLVAR_INCLUDEINTOTAL;

    public static final String COL_IND_CRUISE = "cruise";
    public static final String COL_IND_PLATFORM = "platform";
    public static final String COL_IND_STARTDATE = "startdate";
    public static final String COL_IND_STARTTIME = "starttime";
    public static final String COL_IND_SERIALNO = "serialno";
    public static final String COL_IND_FISHSTATIONTYPE = "fishstationtype";
    public static final String COL_IND_LATITUDESTART = "latitudestart";
    public static final String COL_IND_LONGITUDESTART = "longitudestart";
    public static final String COL_IND_SYSTEM = "system";
    public static final String COL_IND_AREA = "area";
    public static final String COL_IND_LOCATION = "location";
    public static final String COL_IND_GEAR = "gear";
    public static final String COL_IND_SPECCAT = COL_ABNDBYIND_SPECCAT;
    public static final String COL_IND_SPECIES = "species";
    public static final String COL_IND_NONAME = "noname";
    public static final String COL_IND_APHIA = "aphia";
    public static final String COL_IND_CATCHWEIGHT = "catchweight";
    public static final String COL_IND_CATCHCOUNT = "catchcount";
    public static final String COL_IND_SAMPLENUMBER = "samplenumber";
    public static final String COL_IND_LENGTHSAMPLEWEIGHT = "lengthsampleweight";
    public static final String COL_IND_LENGTHSAMPLECOUNT = "lengthsamplecount";
    public static final String COL_IND_FREQUENCY = "frequency";
    public static final String COL_IND_NO = "no";
    public static final String COL_IND_TRAWLQUALITY = "trawlquality";
    public static final String COL_IND_GROUP = "group";
    public static final String COL_IND_SAMPLETYPE = "sampletype";

    public static final String COL_IND_WEIGHT = "weight";
    public static final String COL_IND_LENGTH = "length";
    public static final String COL_IND_AGE = "age";
    public static final String COL_IND_SEX = "sex";
    public static final String COL_IND_DEVELOPMENTALSTAGE = "developmentalstage";
    public static final String COL_IND_STAGE = "stage";
    public static final String COL_IND_SPECIALSTAGE = "specialstage";
    public static final String COL_IND_DIGESTDEG = "digestdeg";
    public static final String COL_IND_FAT = "fat";
    public static final String COL_IND_LIVER = "liver";
    public static final String COL_IND_LIVERWEIGHT = "liverweight";
    public static final String COL_IND_GONADWEIGHT = "gonadweight";
    public static final String COL_IND_STOMACHWEIGHT = "stomachweight";
    public static final String COL_IND_VERTEBRAE = "vertebrae";
    public static final String COL_IND_LENGTHUNIT = "lengthunit";
    public static final String COL_IND_WEIGHTMETHOD = "weightmethod";
    public static final String COL_IND_STOMACHFILLFIELD = "stomachfillfield";
    public static final String COL_IND_LIVERPARASITE = "liverparasite";
    public static final String COL_IND_SPAWNINGAGE = "spawningage";
    public static final String COL_IND_SPAWNINGZONES = "spawningzones";
    public static final String COL_IND_READABILITY = "readability";
    public static final String COL_IND_OTOLITHTYPE = "otolithtype";
    public static final String COL_IND_OTOLITHEDGE = "otolithedge";
    public static final String COL_IND_OTOLITHCENTRE = "otolithcentre";
    public static final String COL_IND_CALIBRATION = "calibration";

    public static List<String> INDIVIDUALS = Arrays.asList(COL_IND_CRUISE,
            COL_IND_SERIALNO,
            COL_IND_PLATFORM,
            COL_IND_SPECCAT,
            COL_IND_SPECIES,
            COL_IND_NONAME,
            COL_IND_APHIA,
            COL_IND_SAMPLENUMBER,
            COL_IND_NO,
            COL_IND_WEIGHT,
            COL_IND_LENGTH,
            COL_IND_AGE,
            COL_IND_SEX,
            COL_IND_DEVELOPMENTALSTAGE,
            COL_IND_STAGE,
            COL_IND_SPECIALSTAGE,
            COL_IND_DIGESTDEG,
            COL_IND_FAT,
            COL_IND_LIVER,
            COL_IND_LIVERWEIGHT,
            COL_IND_GONADWEIGHT,
            COL_IND_STOMACHWEIGHT,
            COL_IND_VERTEBRAE,
            COL_IND_LENGTHUNIT,
            COL_IND_WEIGHTMETHOD,
            COL_IND_STOMACHFILLFIELD,
            COL_IND_LIVERPARASITE,
            COL_IND_SPAWNINGAGE,
            COL_IND_SPAWNINGZONES,
            COL_IND_READABILITY,
            COL_IND_OTOLITHTYPE,
            COL_IND_OTOLITHEDGE,
            COL_IND_OTOLITHCENTRE,
            COL_IND_CALIBRATION);

    public static List<String> INDIVIDUALS_FULL = Arrays.asList(COL_IND_CRUISE,
            COL_IND_SERIALNO,
            COL_IND_PLATFORM,
            COL_IND_STARTDATE,
            COL_IND_STARTTIME,
            COL_IND_FISHSTATIONTYPE,
            COL_IND_LATITUDESTART,
            COL_IND_LONGITUDESTART,
            COL_IND_SYSTEM,
            COL_IND_AREA,
            COL_IND_LOCATION,
            COL_IND_GEAR,
            COL_IND_SPECCAT,
            COL_IND_SPECIES,
            COL_IND_NONAME,
            COL_IND_APHIA,
            COL_IND_CATCHWEIGHT,
            COL_IND_CATCHCOUNT,
            COL_IND_SAMPLENUMBER,
            COL_IND_LENGTHSAMPLEWEIGHT,
            COL_IND_LENGTHSAMPLECOUNT,
            COL_IND_FREQUENCY,
            COL_IND_NO,
            COL_IND_WEIGHT,
            COL_IND_LENGTH,
            COL_IND_AGE,
            COL_IND_SEX,
            COL_IND_DEVELOPMENTALSTAGE,
            COL_IND_STAGE,
            COL_IND_SPECIALSTAGE,
            COL_IND_DIGESTDEG,
            COL_IND_FAT,
            COL_IND_LIVER,
            COL_IND_LIVERWEIGHT,
            COL_IND_GONADWEIGHT,
            COL_IND_STOMACHWEIGHT,
            COL_IND_VERTEBRAE,
            COL_IND_LENGTHUNIT,
            COL_IND_WEIGHTMETHOD,
            COL_IND_STOMACHFILLFIELD,
            COL_IND_LIVERPARASITE,
            COL_IND_SPAWNINGAGE,
            COL_IND_SPAWNINGZONES,
            COL_IND_READABILITY,
            COL_IND_OTOLITHTYPE,
            COL_IND_OTOLITHEDGE,
            COL_IND_OTOLITHCENTRE,
            COL_IND_CALIBRATION);
    // REPORTS
    public static final String REPORT_XCATBYLENGTH = "AgeByLength";
    public static final String REPORT_TOTALABUNDANCE = "TotalAbundance";
    // Horizontal Sample units
    public static final String SAMPLEUNIT_EDSU = DIM_EDSU;
    public static final String SAMPLEUNIT_PSU = DIM_PSU;
    public static final String SAMPLEUNIT_STRATUM = DIM_STRATUM;
    // Biostation assignment method
    public static final String ASSIGNMENTMETHOD_STRATUM = SAMPLEUNIT_STRATUM;
    public static final String ASSIGNMENTMETHOD_RADIUS = "Radius";
    public static final String ASSIGNMENTMETHOD_ELLIPSOIDALDISTANCE = "EllipsoidalDistance";
    public static final String ASSIGNMENTMETHOD_USEPROCESSDATA = "UseProcessData";

    // Define swept area psu method
    public static final String SWEPTAREAPSUMETHOD_STATION = "Station";
    public static final String SWEPTAREAPSUMETHOD_USEPROCESSDATA = "UseProcessData";

    // Biostation assignment weighting method
    public static final String WEIGHTINGMETHOD_EQUAL = "Equal";
    public static final String WEIGHTINGMETHOD_NASC = "NASC";
    public static final String WEIGHTINGMETHOD_NUMBEROFLENGTHSAMPLES = "NumberOfLengthSamples";
    public static final String WEIGHTINGMETHOD_NORMTOTALWEIGHT = "NormTotalWeight";
    public static final String WEIGHTINGMETHOD_NORMTOTALCOUNT = "NormTotalCount";
    public static final String WEIGHTINGMETHOD_SUMWEIGHTEDCOUNT = "SumWeightedCount";
    public static final String WEIGHTINGMETHOD_INVSUMWEIGHTEDCOUNT = "InvSumWeightedCount";
    // Observation type
    public static final String OBSERVATIONTYPE_STATION = DIM_STATION;
    public static final String OBSERVATIONTYPE_ASSIGNMENT = DIM_ASSIGNMENT;

    public static final String LENGTHDISTTYPE_STD_NORM = "Norm";
    public static final String LENGTHDISTTYPE_STD_SWEEPW = "SweepW";
    public static final String LENGTHDISTTYPE_STD_PERCENT = "Percent";

    public static final String LENGTHDISTTYPE_LENGHTDIST = "LengthDist";
    public static final String LENGTHDISTTYPE_NORMLENGHTDIST = LENGTHDISTTYPE_STD_NORM + LENGTHDISTTYPE_LENGHTDIST;
    public static final String LENGTHDISTTYPE_PERCENTLENGHTDIST = LENGTHDISTTYPE_STD_PERCENT + LENGTHDISTTYPE_LENGHTDIST;
    public static final String LENGTHDISTTYPE_SWEEPWLENGHTDIST = LENGTHDISTTYPE_STD_SWEEPW + LENGTHDISTTYPE_LENGHTDIST;
    public static final String LENGTHDISTTYPE_SWEEPWNORMLENGHTDIST = LENGTHDISTTYPE_STD_SWEEPW + LENGTHDISTTYPE_NORMLENGHTDIST;

    public static final String CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH = "LengthDependentSweepWidth";
    public static final String CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY = "LengthDependentSelectivity";
    // Layer Types (4 types of layers with different capabilities)
    public static final String LAYERTYPE_PCHANNEL = "PChannel";
    public static final String LAYERTYPE_DEPTHLAYER = "DepthLayer";
    public static final String LAYERTYPE_WATERCOLUMN = "WaterColumn";
    //public static final String LAYERTYPE_ESTLAYER = "EstLayer";

    // Depth layer constants
    public static final String DEPTHLAYER_PEL = "PEL";
    public static final String DEPTHLAYER_BOT = "BOT";

    // Water column constants
    public static final String WATERCOLUMN_PELBOT = "PELBOT";

    // Aggregation types
    public static final String AGGREGATION_AVERAGE = "Average";
    public static final String AGGREGATION_SUM = "Sum";

    // Swept area method
    public static final String SWEPTAREAMETHOD_LENGTHDEPENDENT = "LengthDependent";
    public static final String SWEPTAREAMETHOD_TOTALCATCH = "TotalCatch";

    // Swept area method
    public static final String CATCHVARIABLE_WEIGHT = "Weight";
    public static final String CATCHVARIABLE_COUNT = "Count";

    // Swept area sweep distance method
    public static final String SWEEPDISTANCE_DISTANCE = "Distance";
    public static final String SWEEPDISTANCE_SPEEDMULTTIME = "SpeedMultTime";

    // Swept area effective fishing width method
    public static final String SWEEPWIDTH_CONSTANT = "Constant";
    public static final String SWEEPWIDTH_LENGTHDEPENDENT = "LengthDependent";
    public static final String SWEEPWIDTH_CRUISEDEPENDENT = "CruiseDependent";
    public static final String SWEEPWIDTH_PREDETERMINED = "Predetermined";

    // Fill variables method for missing variables at superindividuals
    public static final String FILLVARIABLES_IMPUTEBYAGE = "ImputeByAge";
    public static final String FILLVARIABLES_NONE = "None";

// Fill variables method for missing weights at superindividuals
    public static final String FILLWEIGHT_MEAN = "Mean";
    public static final String FILLWEIGHT_REGRESSION = "Regression";
    public static final String FILLWEIGHT_MANUALLY = "Manually";
    public static final String FILLWEIGHT_FROMFILE = "FromFile";
    public static final String FILLWEIGHT_NONE = "None";

    // Super individual abundance weighting method
    public static final String ABUNDWEIGHTMETHOD_EQUAL = "Equal";
    public static final String ABUNDWEIGHTMETHOD_STATIONDENSITY = "StationDensity";

    // Covariate source type
    public static final String SOURCETYPE_LANDING = "Landing";
    public static final String SOURCETYPE_BIOTIC = "Biotic";
    // Covariate type
    public static final String COVARIATETYPE_FIXED = "Fixed";
    public static final String COVARIATETYPE_RANDOM = "Random";
    public static final String COVARIATETYPE_DEPENDENCY = "Dependency";
    // Acoustic PSU definition method
    public static final String ACOUSTICPSU_DEFINITIONMETHOD_NONE = "None";
    public static final String ACOUSTICPSU_DEFINITIONMETHOD_USEPROCESSDATA = "UseProcessData";
    public static final String ACOUSTICPSU_DEFINITIONMETHOD_EDSUTOPSU = "EDSUToPSU";
    // Covariate definition method
    public static final String DEFINITIONMETHOD_COPYFROMLANDING = "CopyFromLanding";
    public static final String DEFINITIONMETHOD_USEDATA = "UseData";
    public static final String DEFINITIONMETHOD_RESOURCEFILE = "ResourceFile";
    public static final String DEFINITIONMETHOD_USEPROCESSDATA = "UseProcessData";
    // Covariate temporal time interval
    public static final String COVARIATETIMEINTERVAL_YEAR = "Year";
    public static final String COVARIATETIMEINTERVAL_QUARTER = "Quarter";
    public static final String COVARIATETIMEINTERVAL_MONTH = "Month";
    public static final String COVARIATETIMEINTERVAL_WEEK = "Week";
    public static final String COVARIATETIMEINTERVAL_PERIOD = "Period";
    // Spatial covariate dimension
    /*public static final String SPATIALVARIABLE_NONE = "None";
    public static final String SPATIALVARIABLE_STRATUM = "Stratum";
    public static final String SPATIALVARIABLE_MAINAREA = "MainArea";
    public static final String SPATIALVARIABLE_LOCATION = "Location";
    public static final String SPATIALVARIABLE_LANDINGSITE = "LandingSite";*/
    // Area coding
    public static final String AREACODING_MAINAREA = "MainArea";
    public static final String AREACODING_MAINAREAANDLOCATION = "MainAreaAndLocation";
    // Transient process data table
    public static final String TABLE_ESTLAYERDEF = "ESTLAYERDEF";
    //public static final String TABLE_SPATIALVAR = "SPATIALDIM";
    // Transient process data table
    public static final String REPORT_FETCH_LEVEL_IMPUTE = "impute";
    public static final String REPORT_FETCH_LEVEL_BOOTSTRAP = "bootstrap";

    public static final String BOOTSTRAPMETHOD_ACOUSTICTRAWL = "AcousticTrawl";
    public static final String BOOTSTRAPMETHOD_SWEPTAREALENGTH = "SweptAreaLength";
    public static final String BOOTSTRAPMETHOD_SWEPTAREATOTAL = "SweptAreaTotal";

    public static final int WARNINGLEVEL_STRICT = 0;
    public static final int WARNINGLEVEL_MEDIUM = 1;
    public static final int WARNINGLEVEL_LOW = 2;

    public static final String DISTANCEMETHOD_FULLDISTANCE = "FullDistance";
    public static final String DISTANCEMETHOD_BYDEPTH = "ByDepth";

    public static final String LENGTHWEIGHT_COEFF_A = "a";
    public static final String LENGTHWEIGHT_COEFF_B = "b";
    public static final String LENGTHWEIGHT_COEFF_R2 = "r2";
}
