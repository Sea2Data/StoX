# When building Rstox:
# 1. Check R version (look for updates)
# 2. Update all dependent packages
buildRstox <- function(dir, version="1.3.2", Rversion="3.3.1", ex=FALSE, official=FALSE, data="gytetokt2016", updateSVN=FALSE, clearBuild=TRUE){
	
	if(updateSVN){
		system2("cd", dir)
		system2("svn", "update")
	}
 	
	# Define directories and files:
	buildDir <- file.path(dir, "build", "RstoxBuildDir")
	exportDir <- file.path(dir, "export")
	dataDir <- file.path(dir, "data")
	sourceDir <- file.path(dir, "functions")
	examplesFile <- file.path(dir, "build", "example", "Examples.R")
	READMEfile <- file.path(dir, "README")
	buildUtils <- file.path(dir, "build", "Build.R")
	
	# Source functions used for building, generating examples and writing README file:
	source(buildUtils)
	
	# Build the package:
	build.rstox.package(sourceDir, buildDir=buildDir, exportDir=exportDir, dataDir=dataDir, version=version, Rversion=Rversion, data=data)
	
	# Create example script:
	if(ex){
		ex = getExamples(dir, examplesFile)
		source(examplesFile)
		# Work out any errors and warnings.
	}

	### Generate the README file: ###
	betaAlpha <- length(gregexpr(".", version, fixed=TRUE)[[1]]) + 1
	betaAlphaString <- c("", "beta", "alpha")[betaAlpha]
	
	# Read the NAMESPACE file and get the package dependencies:
	writeRstoxREADME(READMEfile, version, Rversion, betaAlpha, betaAlphaString, imports=getRstoxImports(buildDir), official=official)
	
	if(clearBuild){
		unlink(buildDir, recursive=TRUE)
	}
	
	library(Rstox)
}

# Define the directory of the working copy:
#arnejh
dir <- "/Users/arnejh/Documents/Produktivt/Prosjekt/Acoustics/sea2R/rstox_svn/r"
# aasmunds
#dir <- "C:/Projects/Sea2Data/NewBeam/trunk/beam/StoX/StoX-Main/src/main/resources/stox/system/r"





# Build 1.4.3:
buildRstox(dir, version="1.4.3", Rversion="3.3.2", ex=FALSE, official=FALSE, data="Example_acoustic_InternationaEcosystemSurveyNordic Sea_herring_2016", updateSVN=FALSE)

# Build 1.5:
buildRstox(dir, version="1.5", Rversion="3.3.2", ex=FALSE, official=TRUE, data="Example_acoustic_InternationaEcosystemSurveyNordic Sea_herring_2016", updateSVN=FALSE)

# Build 1.5:
buildRstox(dir, version="1.5", Rversion="3.3.2", ex=FALSE, official=TRUE, data="Example_acoustic_InternationaEcosystemSurveyNordic Sea_herring_2016", updateSVN=FALSE)

# Build 1.5.1:
buildRstox(dir, version="1.5.1", Rversion="3.3.2", ex=FALSE, official=FALSE, data="Example_acoustic_InternationaEcosystemSurveyNordic Sea_herring_2016", updateSVN=FALSE)


# Build 1.5.2:
buildRstox(dir, version="1.5.2", Rversion="3.3.2", ex=FALSE, official=FALSE, data="Example_acoustic_InternationaEcosystemSurveyNordic Sea_herring_2016", updateSVN=FALSE)

# In terminal:
R CMD check Rstox_1.5.2.tar.gz


# Build 1.6:
#buildRstox(dir, version="1.6", Rversion="3.3.2", ex=FALSE, official=TRUE, data="Example_acoustic_InternationaEcosystemSurveyNordic Sea_herring_2016", updateSVN=FALSE)



