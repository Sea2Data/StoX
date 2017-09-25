require(rJava,  quietly=T)
.jinit('./../rstox.jar', force.init=T)
source('./../rstox.r')

# Work with StoX template factory and Functions
Factory = J("no.imr.stox.factory.Factory");
Functions = J("no.imr.stox.functions.utils.Functions");

# Create ad-hoc template project for length distribution
p = Factory$getTemplateProject(Factory$TEMPLATE_STATIONLENGTHDIST);

# Modify parameters
# Set filename
fileName <- "C:/Users/aasmunds/workspace/stox/project/Tobis-2013842-test/input/biotic/4-2013-3317-1.xml";
readFile = p$getBaseline()$getProcessByFunctionName(Functions$FN_READBIOTICXML);
readFile$setParameterValue(Functions$PM_READBIOTICXML_FILENAME1, fileName);

# Set species
filterBiotic = p$getBaseline()$getProcessByFunctionName(Functions$FN_FILTERBIOTIC);
# Supported species: HAVSIL, LODDE, SILD, SILDG03, MAKRELL, KOLMULE, TORSK, HYSE, SEI
# For other species use TSN code.
# Filter syntax for multiple species: species =~ ['TORSK', 'HYSE']
# Filter syntax for single species: species == 'TORSK'
catchExpr = "species == 'HAVSIL'";
filterBiotic$setParameterValue(Functions$PM_FILTERBIOTIC_CATCHEXPR, catchExpr);

# Set lengthdist type
stationLengthDist = p$getBaseline()$getProcessByFunctionName(Functions$FN_STATIONLENGTHDIST);
stationLengthDist$setParameterValue(Functions$PM_STATIONLENGTHDIST_LENGTHDISTTYPE, Functions$LENGTHDISTTYPE_NORMLENGHTDISTR);


# Set length group
regroupLengthDist = p$getBaseline()$getProcessByFunctionName(Functions$FN_REGROUPLENGTHDIST);
regroupLengthDist$setParameterValue(Functions$PM_REGROUPLENGTHDIST_LENGTHINTERVAL, jDouble(1.0));

p$getBaseline()$run(jInt(1), jInt(p$getBaseline()$getProcessList()$size()), jBoolean(FALSE));
fl = readFile$getOutput();
lfq <- rStoX.getDataFrame1(p$getBaseline(), regroupLengthDist$getName());
System.out.println(regroupLengthDist$getOutput());

System.out.println("Fishstation count: " + fl.size());
