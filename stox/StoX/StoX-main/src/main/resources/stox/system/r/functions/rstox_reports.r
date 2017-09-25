#*********************************************
#*********************************************
#' Impute unknown individual biological parameters from known values
#'
#' This function fills in holes in individual fish samples (also called imputation).
#' In cases where individuals are not aged, missing biological variables (e.g "weight","age","sex", and "specialstage") are sampled from 
#' fish in the same length group at the lowest imputation level possible.
#'	impLevel = 0: no imputation, biological information exists
#'	impLevel = 1: imputation at station level; biological information is selected at random from fish within station
#'	impLevel = 2: imputation at strata level; no information available at station level, random selection within stratum
#'	impLevel = 3: imputation at survey level; no information available at lower levels, random selection within suvey
#'	impLevel = 99: no imputation, no biological information exists for this length group
#'
#' @param i		The index in the list of bootstrap iterations.
#' @param abnd	Abundance matrix with individual data
#' @param seedV	The seed vector for the random number generator, where element 'i' is picked out (this may seem strange, but is a consequence of the parallelability of the function, where 'i' is the primary parameter).
#'
#' @return Abundance matrix with imputed biological information 
#'
#' @export
#' 
#distributeAbundance <- function(i=NULL, abnd, seedV=NULL, dotfile=NULL) {
distributeAbundance <- function(i=NULL, abnd, seedV=NULL) {
	# Function for replacing NAs by data given missing and replacement indices. This function treats individual columns to keep data type:
	imputeFromInd <- function(x, indMissing, indReplacement){
		if(length(indMissing)){
			for(i in unique(indMissing[,2])){
				indMissingInCurrentCol <- indMissing[indMissing[, 2] == i, 1]
				indReplacementInCurrentCol <- indReplacement[indReplacement[, 2] == i, 1]
				x[indMissingInCurrentCol, i] <- x[indReplacementInCurrentCol, i]
			}
		}
		x
	}
	
	if(length(i)==1 && !"Row" %in% names(abnd)){
		abnd = abnd[[i]]
	}
	N <- nrow(abnd)
	
	# Get the indices of known (with includeintotal==TRUE) and unknown ages:
	#atKnownAge <- which(getVar(abnd, "age") != "-" & getVar(abnd, "includeintotal")=="true")
	#atUnknownAge <- which(getVar(abnd, "age") == "-")
	
	#knownAge_old <- getVar(abnd, "age") != "-" & getVar(abnd, "includeintotal")=="true"
	knownAge <- !is.na(getVar(abnd, "age")) & getVar(abnd, "includeintotal") %in% TRUE
	#unknownAge_old <- getVar(abnd, "age") == "-"
	unknownAge <- is.na(getVar(abnd, "age"))
	#atKnownAge <- which(knownAge_old | knownAge)
	#atUnknownAge <- which(unknownAge_old | unknownAge)
	atKnownAge <- which(knownAge)
	atUnknownAge <- which(unknownAge)
	
	NatKnownAge <- length(atKnownAge)
	NatUnknownAge <- length(atUnknownAge)
	msg <- double(6)
	msg[1] <- NatKnownAge
	msg[2] <- NatUnknownAge
	
	# Stop if no known ages and return if no unknown:
	if(NatKnownAge == 0){
		stop("No known ages")
	}
	if(NatUnknownAge == 0){
	warning("No unknown ages")
		abnd$impLevel <- 0
		return(list(data=abnd, msg=msg, indMissing=NULL, indReplacement=NULL, seedM=NULL))
	}

	# Set the seed matrix:
	if(isTRUE(seedV[i])){
		seedM <- matrix(c(1231234, 1234, 1234), nrow=NatUnknownAge, ncol=3, byrow=TRUE)
	}
	else{
		set.seed(seedV[i])
		# Create a seed matrix with 3 columns representing the replacement by station, stratum and survey:
		seedM <- matrix(sample(seq_len(10000000), 3*NatUnknownAge, replace = FALSE), ncol=3)
	}
	
	# Run through the unknown rows and get indices for rows at which the missing data should be extracetd:
	#imputeRows <- rep("-", N)
	imputeRows <- rep(NA, N)
	imputeLevels <- integer(N)
	for(atUnkn in seq_along(atUnknownAge)){
		indUnkn <- atUnknownAge[atUnkn]
		# Get indices for which of the rows with known ages that have the same station, stratum and survey as the current unknown individual:
		matchStratum <- getVar(abnd, "Stratum")[indUnkn] == getVar(abnd, "Stratum")[atKnownAge]
		matchcruise <- getVar(abnd, "cruise")[indUnkn] == getVar(abnd, "cruise")[atKnownAge]
		matchserialno <- getVar(abnd, "serialno")[indUnkn] == getVar(abnd, "serialno")[atKnownAge]
		matchLenGrp <- getVar(abnd, "LenGrp")[indUnkn] == getVar(abnd, "LenGrp")[atKnownAge]
		id.known.sta <- atKnownAge[ which(matchStratum & matchcruise & matchserialno & matchLenGrp) ]
		id.known.stratum <- atKnownAge[ which(matchStratum & matchLenGrp) ]
		id.known.survey <- atKnownAge[ which(matchLenGrp) ]
		#Nid.known.stratum <- length(id.known.stratum)
		#Nid.known.sta <- length(id.known.sta)
		#Nid.known.survey <- length(id.known.survey)
												
		## Replace by station:
	 	if(any(id.known.sta)){
			set.seed(seedM[atUnkn,1])
			imputeRows[indUnkn] <- sample(id.known.sta, 1L, FALSE, NULL)
			#imputeRows[indUnkn] <- id.known.sta[.Internal(sample(Nid.known.sta, 1L, FALSE, NULL))]
			imputeLevels[indUnkn] <- 1L
		}
		## Replace by stratum:
		else if(any(id.known.stratum)){
			set.seed(seedM[atUnkn,2])
			imputeRows[indUnkn] <- sample(id.known.stratum, 1L, FALSE, NULL)
			#imputeRows[indUnkn] <- id.known.stratum[.Internal(sample(Nid.known.stratum, 1L, FALSE, NULL))]
			imputeLevels[indUnkn] <- 2L
		}
		## Replace by survey:
		else if(any(id.known.survey)) {
			set.seed(seedM[atUnkn,3])
			imputeRows[indUnkn] <- sample(id.known.survey, 1L, FALSE, NULL)
			#imputeRows[indUnkn] <- id.known.survey[.Internal(sample(Nid.known.survey, 1L, FALSE, NULL))]
			imputeLevels[indUnkn] <- 3L
		}
		else{
			imputeLevels[indUnkn] <- 99L
		}
	}
	abnd$impLevel <- imputeLevels
	abnd$impRow <- imputeRows
	# Store process info:
	msg[3] <- sum(abnd$impLevel[atUnknownAge]==1)
	msg[4] <- sum(abnd$impLevel[atUnknownAge]==2)
	msg[5] <- sum(abnd$impLevel[atUnknownAge]==3)
	msg[6] <- sum(abnd$impLevel[atUnknownAge]==99)
	
	# Create the following two data frames: 1) the rows of abnd which contain missing age and where there is age available in other rows, and 2) the rows with age available for imputing:
	missing <- abnd[atUnknownAge, , drop=FALSE]
	available <- abnd[imputeRows[atUnknownAge], , drop=FALSE]
	# Get the indices of missing data in 'missing' which are present in 'available':
	#ind <- which(missing == "-" & available != "-", arr.ind=TRUE)
	ind <- which(is.na(missing) & !is.na(available), arr.ind=TRUE)
	indMissing <- cbind(missing$Row[ind[,1]], ind[,2])
	indReplacement <- cbind(available$Row[ind[,1]], ind[,2])
	
	# Apply the replacement. This may be moved to the funciton imputeByAge() in the future to allow for using previously generated indices:
	abnd <- imputeFromInd(abnd, indMissing, indReplacement)
	#if(length(indMissing)){
	#	abnd[indMissing] <- abnd[indReplacement]
	#}
	
	#if(length(dotfile)){
	#	cat(".", file=dotfile, append=TRUE)
	#}
	#list(data=abnd, msg=msg, indMissing=indMissing, indReplacement=indReplacement, atUnknownAge=atUnknownAge, imputeRows=imputeRows, seedM=seedM)
	list(data=abnd, msg=msg, indMissing=indMissing, indReplacement=indReplacement, seedM=seedM)
}


#*********************************************
#*********************************************
#' Impute	missing individual data by age
#' 
#' Impute missing data within the bootstrap data object
#' 
#' @param projectName   The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param seed			The seed for the random number generator (used for reproducibility)
#' @param cores			An integer giving the number of cores to run the bootstrapping over.
#' @param saveInd		Logical: if TRUE save the imputing indices.
#' 
#' @return Updated list with imputed bootstrap results 
#'
#' @examples
#' # Create the test project:
#' createProject("Test_Rstox", files=system.file("extdata", "Test_Rstox", package="Rstox"), ow=TRUE)
#' projectName <- "Test_Rstox"
#' bootstrap <- runBootstrap(projectName, nboot=10, acousticMethod=PSU~Stratum, bioticMethod=EDSU~Stratum)
#' # imputeByAge() fills in empty cells:
#' system.time(bootstrap_Acoustic_imputed <- imputeByAge(projectName))
#'
#' @importFrom parallel detectCores makeCluster parLapplyLB stopCluster
#'
#' @export
#' 
imputeByAge <- function(projectName, seed=1, cores=1, saveInd=TRUE){
	
	# Write a dot at each iteration to a textfile:
	#dotfile <- file.path(getProjectPaths(projectName)$RReportDir, "imputeProgress.txt")
	#write("", dotfile)
	
	# Read the saved data from the R model. In older versions the user loaded the file "rmodel.RData" separately, but in the current code the environment "RstoxEnv" is declared on load of Rstox, and all relevant outputs are assigned to this environment:
	imputeVariable <- getProjectData(projectName=projectName, var="bootstrap")
	
	nboot <- length(imputeVariable$SuperIndAbundance) ## The length of the data collection corresponds to the number of bootstrap iterations
	msg.out <- vector("list", nboot)
	indMissing.out <- vector("list", nboot)
	indReplacement.out <- vector("list", nboot)
	seedM.out <- vector("list", nboot)
	
	# Set the seed of the runs, either as a vector of 1234s, to comply with old code, where the seeed was allways 1234 (from before 2016), or as a vector of seeds sampled with the given seed, or as NULL, in which case the seed matrix 'seedM' of distributeAbundance() is set by sampling seq_len(10000000) without seed:
	if(isTRUE(seed)){
		seedV = rep(TRUE, nboot+1) # seed==TRUE giving 1234 for compatibility with older versions
	}
	else if(is.numeric(seed)){
		set.seed(seed)
		seedV = sample(seq_len(10000000), nboot+1, replace = FALSE)
	}
	else{
		seedV = NULL
	}
	
	# Store the bootstrap iteration names:
	namesOfIterations <- names(imputeVariable$SuperIndAbundance)
 
	# Impute biological information
	imputeVariable$base.SuperIndAbundance <- distributeAbundance(i=1, abnd=imputeVariable$base.SuperIndAbundance, seedV=tail(seedV,1))$data
	
	# Check available cores:	
	availableCores = detectCores()
	if(cores>availableCores){
		warning(paste0("Only ", availableCores, " cores available (", cores, " requested)"))
	}
	cores = min(cores, nboot, availableCores)
	# Generate the clusters of time steps:
	
	if(cores>1){
		cat(paste0("Imputing missing data (", nboot, " replicates, using ", cores, " cores in parallel):\n"))
		cl<-makeCluster(cores)
		# Bootstrap:
		out <- pblapply(seq_len(nboot), distributeAbundance, abnd=imputeVariable$SuperIndAbundance, seedV=seedV, cl=cl)
		# End the parallel bootstrapping:
		stopCluster(cl)
	}
	else{
		cat(paste0("Imputing missing data (", nboot, " replicates):\n"))
		out <- pblapply(seq_len(nboot), distributeAbundance, abnd=imputeVariable$SuperIndAbundance, seedV=seedV)
	}
		
	imputeVariable$SuperIndAbundance <- lapply(out, "[[", "data")
	# Add names ot the iterations:
	names(imputeVariable$SuperIndAbundance) <- namesOfIterations
	msg.out <- lapply(out, "[[", "msg")
	indMissing.out <- lapply(out, "[[", "indMissing")
	indReplacement.out <- lapply(out, "[[", "indReplacement")
	seedM.out <- lapply(out, "[[", "seedM")
	
	
	names(imputeVariable$SuperIndAbundance) <- namesOfIterations
	msg.out <- lapply(out, "[[", "msg")
	indMissing.out <- lapply(out, "[[", "indMissing")
	indReplacement.out <- lapply(out, "[[", "indReplacement")
	seedM.out <- lapply(out, "[[", "seedM")	
		
		
		
	
	
	
	 
#	if(cores==1){
#		for(i in seq_len(nboot)){ # Loop to all bootstrap results
#			#thisiteration <- distributeAbundance(i, abnd=imputeVariable$SuperIndAbundance, seedV=seedV, dotfile=dotfile)
#			thisiteration <- distributeAbundance(i, abnd=imputeVariable$SuperIndAbundance, seedV=seedV)
#			
#			# Store the data from the current iteration:
#			imputeVariable$SuperIndAbundance[[i]] <- thisiteration$data
#			msg.out[[i]] <- thisiteration$msg
#			indMissing.out[[i]] <- thisiteration$indMissing
#			indReplacement.out[[i]] <- thisiteration$indReplacement
#			seedM.out[[i]] <- thisiteration$seedM
#			#cat(paste0("This is iteration ", i, "/", nboot, "\n"))
#		}
#	}
#	else{
#		# Do not run on more cores than physically available:
#		availableCores = detectCores()
#		if(cores>availableCores){
#			warning(paste0("Only ", availableCores, " cores available (", cores, " requested)"))
#			cores = availableCores
#		}
#		#memoryOneCore <- J("java.lang.Runtime")$getRuntime()$totalMemory()*2^-20
#		cat("Parallel imputing on", cores, "cores:\n")
#		# Generate the cluster of time steps:
#		cl <- makeCluster(cores)
#		#out <- parLapplyLB(cl, seq_len(nboot), distributeAbundance, abnd=imputeVariable$SuperIndAbundance, seedV=seedV, dotfile=dotfile)
#		out <- parLapplyLB(cl, seq_len(nboot), distributeAbundance, abnd=imputeVariable$SuperIndAbundance, seedV=seedV)
#		
#		# End the parallel bootstrapping:
#		stopCluster(cl)
#		imputeVariable$SuperIndAbundance <- lapply(out, "[[", "data")
#		# Add names ot the iterations:
#		names(imputeVariable$SuperIndAbundance) <- namesOfIterations
#		msg.out <- lapply(out, "[[", "msg")
#		indMissing.out <- lapply(out, "[[", "indMissing")
#		indReplacement.out <- lapply(out, "[[", "indReplacement")
#		seedM.out <- lapply(out, "[[", "seedM")
#	}



	msg.out <- t(as.data.frame(msg.out))
	colnames(msg.out) <- c("Aged", "NotAged", "ImputedAtStation", "ImputedAtStrata", "ImputedAtSurvey", "NotImputed")
	rownames(msg.out) <- paste0("Iter", seq_len(nboot))
	
	# Store the output messages, the missing and replace indices, the seeds and other parameters of the imputing:
	imputeVariable$imputeParameters$msg <- msg.out
	imputeVariable$imputeParameters$seed <- seed
	imputeVariable$imputeParameters$seedV <- seedV
	imputeVariable$imputeParameters$seedM <- seedM.out
	imputeVariable$imputeParameters$nboot <- nboot
	imputeVariable$imputeParameters$cores <- cores
	if(saveInd){
		imputeVariable$imputeParameters$indMissing <- indMissing.out
		imputeVariable$imputeParameters$indReplacement <- indReplacement.out
	}
	
	# Assign the data to the environment of the project:
	setProjectData(projectName=projectName, var=imputeVariable, name="bootstrapImpute")
	
	return(msg.out)
}


#*********************************************
#*********************************************
#' Interprets a unit key stings as a scaleing factor
#'
#' Given a key string such as "milliseconds" (possibly abbreviated) or the abbreviation "ms" (identical matching), the unit (here milliseconds) and scaling factor (here 1e-3) is returned.
#'
#' @param unit			A unit key string indicating the unit, or alternatively a numeric value giving the scaling factor (run getPlottingUnit() to see available values).
#' @param var			A key string indicating the variable to plot (see getPlottingUnit()$defaults$Rstox_var for available values).
#' @param baseunit		The unit used in the data.
#' @param implemented	An integer vector giving the inplemented variables, which are the first two ("Abundance", "Weight") in the current version of Rstox.
#' @param def.out		Logical: if TRUE return also the defaults and definitions.
#' 
#' @return a list of the following four elements: 1. the scaling factor, 2. the unit string, 3. a matrix of the default values, and 4. a matrix of the defintions.
#'
#' @examples
#' getPlottingUnit(unit="milli", var="abund", baseunit="stox", def.out=FALSE)
#' getPlottingUnit(unit="milli", var="weight", baseunit="stox", def.out=FALSE)
#' getPlottingUnit(unit="hecto", var="weight", baseunit="stox")
#'
#' @export
#' 
getPlottingUnit <- function(unit=NULL, var="Abundance", baseunit=NULL, implemented=c(1,2), def.out=TRUE){
	# Function used to get the index of the match of unit against the default units:
	getUnitInd <- function(unit, var){
		# Check abbreviations first:
		ind <- if(is.numeric(unit)) which(as.numeric(abbrev[[var]]) == unit) else which(abbrev[[var]] == unit)
		# Then do abbreviated matching:
		if(length(ind)==0){
			ind <- which(abbrMatch(unit, units[[var]], ignore.case=TRUE)$hit)
		}
		# If still no match, use the default:
		if(length(ind)==0){
			unit <- Rstox_unit[[var]]
			warning(paste0("No match for the specified unit. Default used (", unit, ")"))
			ind <- which(abbrMatch(unit, units[[var]], ignore.case=TRUE)$hit)
		}
		ind
	}
	# Define variable, unit and base unit default vectors:
	Rstox_var <- c("Abundance", "Weight", "Length", "Time")[implemented]
	Rstox_unit <- c("millions", "tonnes", "meters", "seconds")[implemented]
	names(Rstox_unit) <- Rstox_var
	Rstox_baseunit <- c("1", "grams", "centimeters", "seconds")[implemented]
	names(Rstox_baseunit) <- Rstox_var
	defaults <- data.frame(Rstox_var, Rstox_unit, Rstox_baseunit)
	# Define lists of allowed unit definitions, abbreviations and scaling factors to be matched with the inputs:
	units <- list(
		c( "ones", "tens", "hundreds", "thousands", "millions", "billions", "trillions" ),
		c( "micrograms", "milligrams", "grams", "hectograms", "kilograms", "tonnes" ),
		c( "micrometers", "millimeters", "centimeters", "decimeters", "meters", "kilometers" ),
		c( "microseconds", "milliseconds", "seconds", "minutes", "hours", "days" ) )[implemented]
	names(units) <- Rstox_var
	abbrev <- list(
		c( "1", "10", "100", "1000", "1e6", "1e9", "1e12" ),
		c( "mcg", "mg", "g", "hg", "kg", "t" ),
		c( "mcm", "mm", "cm", "dm", "m", "km" ),
		c( "mcs", "ms", "s", "m", "h", "d" ) )[implemented]
	names(abbrev) <- Rstox_var
	scale <- list(
		as.numeric(abbrev$Abundance),
		c( 1e-9, 1e-6, 1e-3, 1e-1, 1, 1e3 ),
		c( 1e-6, 1e-3, 1e-2, 1e-1, 1, 1e3 ),
		c( 1e-6, 1e-3, 1, 60, 60*60, 24*60*60 ) )[implemented]
	names(scale) <- Rstox_var
	definitions <- data.frame(unlist(units), unlist(abbrev), unlist(scale))
	
	# Get the variable by abbreviated matching:
	var <- abbrMatch(var[1], Rstox_var, ignore.case=TRUE)$string
	# Defalut var if missing:
	if(length(var)==0){
		warning(paste0("'var' not matched with any of the available values (", paste0(getPlottingUnit()$defaults$Rstox_var, collapse=", "), "). Default selected (", getPlottingUnit()$defaults$Rstox_var[1],")"))
		var <- getPlottingUnit()$defaults$Rstox_var[1]
	}
	# Defalut unit if missing:
	if(length(unit)==0){
		unit <- Rstox_unit[[var]]
	}
	if(length(baseunit)==0){
		baseunit <- Rstox_baseunit[[var]]
	}
	
	# Get matches:
	ind <- getUnitInd(unit, var)
	baseind <- getUnitInd(baseunit, var)
	unit.out <- units[[var]][ind]
	# Get the scaling factor between the base unit and requested unit:
	scale.out <- scale[[var]][ind] / scale[[var]][baseind]
	
	out <- list(scale=scale.out, unit=unit.out, baseunit=baseunit, var=var)
	if(def.out){
		out <- c(out, list(defaults=defaults, definitions=definitions))
	}
	return(out)
}


#*********************************************
#*********************************************
#' Plot NASC distribution to file
#'
#' Plots both original and resampled NASC distributions;
#' histogram of NASC transect means together with distribution of resampled NASC values (line).
#' Probability densities, component density, are plotted (so that the histogram has a total area of one).
#' Plot is exported to a tif- or png-file
#'
#' @param projectName   The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param format		The file format of the saved plot (one of "png" and "tiff").
#' @param ...			Parameters passed on from other functions.
#' 
#' @return Plot saved to file 
#'
#' @examples
#' # Create the test project:
#' createProject("Test_Rstox", files=system.file("extdata", "Test_Rstox", package="Rstox"), ow=TRUE)
#' projectName <- "Test_Rstox"
#' # Run bootstrap before plotting:
#' bootstrap <- runBootstrap(projectName, nboot=10, seed=1, acousticMethod=PSU~Stratum, bioticMethod=EDSU~Stratum)
#' plotNASCDistribution(projectName)
#'
#' @export
#' 
plotNASCDistribution<-function(projectName, format="png", ...){
	# Read the saved data from the R model. In older versions the user loaded the file "rmodel.RData" separately, but in the current code the environment "RstoxEnv" is declared on load of Rstox, and all relevant outputs are assigned to this environment:
	var <- c("psuNASC", "resampledNASC")
	projectEnv <- loadProjectData(projectName=projectName, var=var)
	
	# If any of the psuNASC and resampledNASCDistr are missing in the report environment, issue a warning:
	if(length(projectEnv)==0){
		return(NULL)
	}
	else if(length(projectEnv) && !all(c("psuNASC", "resampledNASC") %in% ls(projectEnv))){
		warning(paste0("At least one of the objects psuNASC and getResampledNASCDistr required to plot by the function plotNASCDistribution() are missing. These are generated by runBootstrap(, acousticMethod=PSU~Stratum, bioticMethod=EDSU~Stratum)"))
		return(NULL)
	}
	# Aggregate the NACS values:
	if(getVar(projectEnv$psuNASC, "LayerType")[1]!="WaterColumn"){
		agg <- aggPSUNASC(projectEnv$psuNASC)
	}
	else{
		agg <- projectEnv$psuNASC
	}
	
	# Define the file name and initiate the plot file:
	filenamebase <- getProjectPaths(projectName)$RReportDir
	if(startsWith(tolower(format), "tif")){
		filename <- file.path(filenamebase, "NASC_Distribution.tif")
		tiff(filename, res=600, compression = "lzw", height=5, width=5, units="in")
	}
	else if(startsWith(tolower(format), "png")){
		filename <- file.path(filenamebase, "NASC_Distribution.png")
		png(filename, width=800, height=600)
	}
	else{
		filename <- NA
		warning("Invalid format")
	}
	moveToTrash(filename)
	
	# Run the plot
	out <- list()
	tryCatch({
		out <- hist(getVar(agg, "Value"), breaks=20, freq=FALSE, xlab="NASC transect means", ylab="Relative frequency", main=projectName)
		d <- density(projectEnv$resampledNASC)
		lines(d)
		}, finally = {
		# safe closure of image resource inside finally block
		dev.off()
	})
	
	out$filename <- filename
	out <- c(out, d)
	out
}


#*********************************************
#*********************************************
#' Plot abundance results to file (generic)
#' 
#' Plots boxplot of bootstrap results together with Coefficient of Variation (CV).
#' Prints summary table and plot is exported to a tif- or png-file.
#' 
#' 
#' @param projectName   The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param var			A key string indicating the variable to plot (see getPlottingUnit()$defaults$Rstox_var for available values).
#' @param unit			A unit key string indicating the unit, or alternatively a numeric value giving the scaling factor (run getPlottingUnit() to see available values).
#' @param baseunit		The unit used in the data.
#' @param grp1			Variable used to group results, e.g. "age", "LenGrp", "sex"
#' @param grp2			An optional second grouping variable
#' @param xlabtxt		The label to user for the x axis, with default depending on data plotted,
#' @param ylabtxt		The label to user for the y axis, with default depending on data plotted.
#' @param maintitle		Main title for plot (text)
#' @param numberscale	Kept for compability with older versions. Use 'unit' instead. (Scale results with e.g. 1000 or 1000000).
#' @param format		The file format of the saved plot (one of "png" and "tiff").
#' @param maxcv			The maximum cv in the plot. Use Inf to indicate the maximum cv of the data.
#' @param ...			Parameters passed on from other functions.
#' 
#' @return Plot saved to file and abundance table printed
#'
#' @examples
#' # Create the test project:
#' createProject("Test_Rstox", files=system.file("extdata", "Test_Rstox", package="Rstox"), ow=TRUE)
#' projectName <- "Test_Rstox"
#' plotAbundance(projectName, grp1="age")
#' plotAbundance(projectName, grp1="age", unit=1)
#' 
#' @export
#' @rdname plotAbundance
#' 
plotAbundance <- function(projectName, var="Abundance", unit=NULL, baseunit=NULL, grp1="age", grp2=NULL, xlabtxt=NULL, ylabtxt=NULL, maintitle="", numberscale=NULL, format="png", maxcv=1, ...){
	# numberscale is kept for backwards compatibility:
	if(length(numberscale)){
		unit <- numberscale
	}
	plottingUnit <- getPlottingUnit(unit=unit, var=var, baseunit=baseunit, def.out=FALSE)
	
	# Process the boostrap runs:
	temp <- reportAbundance(projectName, grp1=grp1, grp2=grp2, numberscale=plottingUnit$scale, plotOutput=TRUE)
	outList <- list(filename=NULL, data=NULL)
	
	for(i in seq_along(temp)){
		level <- names(temp)[i]
		out <- temp[[i]]$abnd
		grp1.unknown <- temp[[i]]$grp1.unknown
		tmp1 <- temp[[i]]$tmp1
	
		# Set the missing values to low value (assuming only postive values are used for age and stratum and other variables):
		cat("Abundance by age for ", level, "\n", se0="")
		print(out)
		xForMissing <- min(tmp1[[grp1]], na.rm=TRUE)-1
		if(length(grp1)){
			suppressWarnings(tmp1[[grp1]][is.na(tmp1[[grp1]])] <- xForMissing)
			suppressWarnings(out[[grp1]][is.na(out[[grp1]])] <- xForMissing)
			unique_grp1 <- unique(tmp1[[grp1]])
		}
		if(length(grp2)){
			suppressWarnings(tmp1[[grp2]][is.na(tmp1[[grp2]])] <- xForMissing)
			suppressWarnings(out[[grp2]][is.na(out[[grp2]])] <- xForMissing)
			unique_grp2 <- unique(tmp1[[grp2]])
		}
	
		# Get ylab and xlab text:
		if(length(ylabtxt)==0){
			ylabtxt <- paste0(plottingUnit$var, " (", plottingUnit$unit, ")")
		}
		if(is.null(xlabtxt) & !is.null(grp2)){
			xlabtxt <- paste(grp1,"by", grp2)
		}
		if(is.null(xlabtxt)){
			xlabtxt <- paste(grp1)
		}
	
		# Get file name:
		filenamebase <- file.path(getProjectPaths(projectName)$RReportDir, paste0(c(level, plottingUnit$var, grp1, grp2), collapse="_"))
		if(startsWith(tolower(format), "tif")){
			filename <- paste0(filenamebase, ".tif")
			x11()
		}
		else if(startsWith(tolower(format), "png")){
			filename <- paste0(filenamebase, ".png")
			#png(filename, width=800, height=600, units = 'in', res = 300)
			png(filename, width=800, height=600)
		}
		else{
			filename <- NA
			warning("Invalid format")
		}
		moveToTrash(filename)
	
		maxcv <- min(maxcv, max(out$Ab.Sum.cv, na.rm=TRUE))
		if(maxcv==0){
			maxcv <- 1
		}
		cvLabels <- pretty(c(0, maxcv)) 
		xlim <- range(unique_grp1)
		ylim <- c(0, max(tmp1$Ab.Sum, na.rm=TRUE))
		cvScalingFactor <- max(ylim) / maxcv
	
		tryCatch({
			par(mfrow=c(1,1), oma=c(2,2,2,2), mar=c(4,4,2,4))
			form <- as.formula(paste0("Ab.Sum~", grp1))
			plot(0, type="n", xlim=range(unique_grp1, na.rm=TRUE), ylim=c(0, max(tmp1$Ab.Sum, na.rm=TRUE)), xlab=xlabtxt, ylab=ylabtxt, axes=FALSE)
	
			if(is.null(grp2)){
				atx <- sort(unique_grp1)
				xTickLabels <- atx
				xTickLabels[xTickLabels==xForMissing] <- "-"
				boxplot(form, at=atx,	data=tmp1, xlim=xlim, ylim=ylim, xlab=xlabtxt, ylab=ylabtxt, axes=FALSE, outline=FALSE, show.names=FALSE)
				axis(1, at=atx, labels=xTickLabels)
				axis(2)
			
				cv <- out$Ab.Sum.cv * cvScalingFactor
				lines(atx, cv, type='b')
			}	
			else{
				addToAtx <- 0.6 / length(unique_grp2)
			
				for(j in seq_along(unique_grp2)){
					thisdata <- tmp1[tmp1[,grp2] == unique_grp2[j],]
					thisout <- out[out[,grp2] == unique_grp2[j],]
					thisout <- thisout[!is.na(thisout$Ab.Sum.cv), , drop=FALSE]
					# Update unique_grp1 to the current grp2:
					unique_grp1 <- unique(thisdata[[grp1]])
					atx <- sort(unique_grp1) + addToAtx*(j-1)
					xTickLabels <- atx
					xTickLabels[xTickLabels==xForMissing] <- "-"
					boxplot(form,	at=atx, data=thisdata, xlim=xlim, ylim=ylim, xlab=xlabtxt, ylab=ylabtxt, axes=FALSE, add=j>1, boxcol=j, boxwex=addToAtx, outline=FALSE, show.names=FALSE)
					if(i==1 && j==1){
						axis(1, at=atx, labels=xTickLabels)
						axis(2)
					}
					# Scale the cvs to fit the ylim of the current plot:
					cv <- thisout$Ab.Sum.cv * cvScalingFactor
					lines(atx, cv, type='b', col=j)
				}
				legend(x="top", lty=1, legend=paste0(grp2, ": ", unique_grp2), col=seq_along(unique_grp2), xpd=TRUE, inset=c(3.1,0), horiz=TRUE, bty="n")
			}
			mtext("CV", side=4, line=3, cex=1)
			axis(4, at=cvLabels * cvScalingFactor, labels=cvLabels)
			title(maintitle)
	
			if(startsWith(tolower(format), "tif")){
				dev.copy(tiff, filename=filename, res=600, compression="lzw", height=10, width=15, units="in")
			}
			}, finally = {
			# safe closure of image resource inside finally block
			dev.off()
		})
		#system(paste0("open '" ,filename, "'"))
		outList$filename[[level]] <- filename
		outList$data[[level]] <- temp[[i]]
	}
}


#*********************************************
#*********************************************
#' Calculate a summary of the bootstrap iterations (possibly after imputing unknown ages).
#' 
#' This function is used in the plotting function plotAbundance().
#' 
#' @param projectName   The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param var			A key string indicating the variable to plot (see getPlottingUnit()$defaults$Rstox_var for available values).
#' @param unit			A unit key string indicating the unit, or alternatively a numeric value giving the scaling factor (run getPlottingUnit() to see available values).
#' @param baseunit		The unit used in the data.
#' @param level			A string naming the process level (see getRstoxEnv()$processLevels for available levels).
#' @param grp1			Variable used to group results, e.g. "age", "LenGrp", "sex"
#' @param grp2			An optional second grouping variable
#' @param numberscale	Kept for compability with older versions. Use 'unit' instead. (Scale results with e.g. 1000 or 1000000).
#' @param plotOutput	Logical: if TRUE return a list of the recuired data in the function plotAbundance(). Otherwise, only the abundance data frame is returned.
#' @param write			Logical: if TRUE write the data to a tab-separated file.
#' 
#' @return A data frame of the abundance in sumary per grp2 and grp1 if plotOutput=FALSE, and a list holding this object (keeping "-" for missing values and not ordering) and other objects needed by plotAbundance().
#'
#' @examples
#' # Create the test project:
#' createProject("Test_Rstox", files=system.file("extdata", "Test_Rstox", package="Rstox"), ow=TRUE)
#' projectName <- "Test_Rstox"
#' reportAbundance(projectName, grp1=NULL, grp2=NULL)
#' reportAbundance(projectName, grp1="age", grp2=NULL)
#' reportAbundance(projectName, var="weight", grp1="age", grp2=NULL)
#' reportAbundance(projectName, grp1="age", grp2="Stratum")
#' reportAbundance(projectName, grp1="age", grp2="Stratum")
#'
#' @export
#' @rdname reportAbundance
#'
#' @import data.table
#' 
reportAbundance <- function(projectName, var="Abundance", unit=NULL, baseunit=NULL, grp1="age", grp2=NULL, numberscale=1e6, plotOutput=FALSE, write=FALSE){
	out <- list()
	for(level in getRstoxEnv()$processLevels){
		out[[level]] <- reportAbundanceAtLevel(projectName, var=var, unit=unit, baseunit=baseunit, level=level, grp1=grp1, grp2=grp2, numberscale=numberscale, plotOutput=plotOutput, write=write)
	}
	out[sapply(out, length)>0]
}
#'
#' @export
#' @rdname reportAbundance
#' 
reportAbundanceAtLevel <- function(projectName, var="Abundance", unit=NULL, baseunit=NULL, level="bootstrapImpute", grp1="age", grp2=NULL, numberscale=1e6, plotOutput=FALSE, write=FALSE){
	# Read the saved data from the R model. In older versions the user loaded the file "rmodel.RData" separately, but in the current code the environment "RstoxEnv" is declared on load of Rstox, and all relevant outputs are assigned to this environment:
	projectEnv <- loadProjectData(projectName=projectName, var=level)
	
	# Combine all the bootstrap runs in one data table:
	DT <- rbindlist(projectEnv[[level]]$SuperIndAbundance, idcol=TRUE)
	if(sum(unlist(lapply(DT, length)))==0){
		return(NULL)
	}

	# If grp1 is missing, replace it with all zeros:
	if(length(grp1)==0 || length(DT[[grp1]])==0){
		grp1 <- "temp"
		DT[[grp1]] <- integer(nrow(DT))
	}

	## Is any data in the grp1 input unkown?
	grp1.unknown <- TRUE
	
	#if(!any((DT[[grp1]]) == "-")){
	if(!any(is.na( DT[[grp1]] ))){
		#DT[[grp1]] <- as.numeric(DT[[grp1]]) 
		grp1.unknown <- FALSE 
	}
	if(!is.null(grp2)){
		setkeyv(DT, cols=c(".id","Stratum", grp1, grp2))
	}
	else{
		setkeyv(DT, cols=c(".id","Stratum", grp1))
	}
	byGrp <- c(grp1, grp2, ".id")

	# Filter and sum by stratum:
	base <- projectEnv[[level]]$base.SuperIndAbundance
	strata <- unique(base$Stratum[base$includeintotal %in% TRUE])
	# Get the scaling factor for the plotting and the unit requested and the unit used in the data as strings:
	plottingUnit <- getPlottingUnit(unit=unit, var=var, baseunit=baseunit, def.out=FALSE)
	plottingUnit$nboot <- length(projectEnv[[level]]$SuperIndAbundance)
	plottingUnit$seed <- projectEnv[[level]]$bootstrapParameters$seed
	var <- plottingUnit$var
	
	# Sum the abundance or the product of abundance and weight (and possibly others in the future):
	varInd <- abbrMatch(var[1], c("Abundance", "weight"), ignore.case=TRUE)
	# Declare the variables used in the DT[] expression below:
	. <- NULL
	Ab.Sum <- NULL
	Abundance <- NULL
	Stratum <- NULL
	weight <- NULL
	if(varInd$ind==1){
		tmp <- DT[Stratum %in% strata, .(Ab.Sum=sum(Abundance, na.rm=TRUE) / plottingUnit$scale), by=byGrp]
	}
	else if(varInd$ind==2){
		tmp <- DT[Stratum %in% strata, .(Ab.Sum=sum(Abundance * weight, na.rm=TRUE) / plottingUnit$scale), by=byGrp]
	}
	else{
		warning(paste0("'var' does not match the available values (", getPlottingUnit()$defaults$Rstox_var, ")"))
	}
	
	tmp1 <- as.data.frame(tmp)
	unique_grp1 <- unique(tmp1[,grp1])
	unique_grp2 <- unique(tmp1[,grp2])

	setkeyv(tmp, cols=c(".id", grp1))
	tmp <- tmp[CJ(unique(tmp$.id), unique_grp1), allow.cartesian=TRUE]

	if(!is.null(grp2)){
		setkeyv(tmp, cols=c(".id", grp1, grp2))
		tmp <- tmp[CJ(unique(tmp$.id), unique_grp1, unique_grp2), allow.cartesian=TRUE]
	} 
	
	tmp$Ab.Sum[is.na(tmp$Ab.Sum)] <- 0
	out <- tmp[, .("Ab.Sum.5%" = quantile(Ab.Sum, probs=0.05, na.rm=TRUE),
					"Ab.Sum.50%" = quantile(Ab.Sum, probs=0.50, na.rm=TRUE),
					"Ab.Sum.95%" = quantile(Ab.Sum, probs=0.95, na.rm=TRUE),
					Ab.Sum.mean = mean(Ab.Sum, na.rm=TRUE),
					Ab.Sum.sd = sd(Ab.Sum, na.rm=TRUE),
					Ab.Sum.cv = sd(Ab.Sum, na.rm=TRUE) / mean(Ab.Sum, na.rm=TRUE)) 
					, by = c(grp1, grp2)]
							
	# Before, as.numeric() was added in as.data.frame() for some reason, but as.data.frame() should convert numerics to numeric properly:
	#out <- as.data.frame(lapply(out, as.numeric))
	out <- as.data.frame(out)
	orderFact1 <- if(length(grp1) && length(out[[grp1]])) out[[grp1]] else integer(nrow(out))
	orderFact2 <- if(length(grp2) && length(out[[grp2]])) out[[grp2]] else integer(nrow(out))
	out <- out[order(orderFact2, orderFact1, na.last=FALSE),]
	rownames(out) <- NULL
	if(nrow(out)==1){
		out <- out[,-1]
		rownames(out) <- c("TSN", "TSB")[varInd$ind]
	}
	
	# Write the data to a tab-separated file:
	if(write){
		filename <- paste0(file.path(getProjectPaths(projectName)$RReportDir, paste0(c(level, plottingUnit$var, grp1, grp2), collapse="_")), ".txt")
		moveToTrash(filename)
		writeLines(paste(names(plottingUnit), unlist(plottingUnit), sep=": "), con=filename)
		suppressWarnings(write.table(out, file=filename, append=TRUE, sep="\t", dec=".", row.names=FALSE))
	}
	else{
		filename <- NULL
	}
	
	outlist <- c(list(abnd=out, filename=filename), plottingUnit)
	
	if(plotOutput){
		outlist <- c(outlist, list(grp1.unknown=grp1.unknown, tmp1=tmp1, unique_grp1=unique_grp1, unique_grp2=unique_grp2, Ab.Sum=tmp$Ab.Sum, tmp=tmp))
	}
	outlist
}


#*********************************************
#*********************************************
#' Get various plots and reports in Rstox
#'
#' \code{getPlots} calls all or a subset of the plotting functions in Rstox (name starting with plot). \cr \cr
#' \code{getReports} calls all or a subset of the report functions in Rstox (name starting with report). \cr \cr
#' \code{runFunsRstox} is the underlying function that calls the requested functions. \cr \cr
#'
#' @param projectName  	The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param out			A string vector giving the plot or report functions to run. See getRstoxEnv()$keywords for available keywords.
#' @param options		A string vector holding the parameters passed on to the plotting functions. These parameters overrides identically named parameters in '...'. The parameters must be formatted as R expressions (as typed into an R console), and separated by semicolons (";"). See examples below:
#' \describe{
#'	\item{"Single string"}{"string = 'a string'"}
#'	\item{"String vector"}{"stringvec = c('red', 'blue', 'yellow2')"}
#'	\item{"Numeric"}{"num = 1.55"}
#'	\item{"Numeric vector"}{"numvec = c(1.4e-6, 16/3, runif(3))"}
#'	\item{"Logical"}{"ok = TRUE"}
#'	\item{"Logical vector"}{"okvec = c( TRUE, FALSE, T, F, {set.seed(0); runif(3)>0.3} )"}
#'	\item{"Array"}{"arr = array(runif(12), dim=3:4)"}
#'	\item{"Function"}{"fun1 = function(x) sin(rev(x))"}
#' }
#' All the examples in one string:
#' options = "string = 'a string'; stringvec = c('red', 'blue', 'yellow2'); num = 1.55; numvec = c(1.4e-6, 16/3, runif(3)); ok = TRUE; okvec = c( TRUE, FALSE, T, F, runif(3)>0.3 ); arr = array(runif(12), dim=3:4); fun1 = function(x) sin(rev(x)); fun3 = runif"
#' @param ...			Parameters passed on to the plotting functions.
#' @param string		A string giving the first characters of the functions to run, such as "plot" or "report".
#' @param all.out		Logical: if TRUE return all data from the functions, and otherwise only return file names.
#' 
#' @return A vector of file names of the plots or reports.
#'
#' @examples
#' # Create the test project:
#' createProject("Test_Rstox", files=system.file("extdata", "Test_Rstox", package="Rstox"), ow=TRUE)
#' projectName <- "Test_Rstox"
#' # Get all plots:
#' getPlots(projectName)
#' # Get all reports:
#' getReports(projectName)
#' # Get a specific plot:
#' getPlots(projectName, out="plotNASCDistribution")
#'
#' @export
#' @rdname getPlots
#' 
getPlots <- function(projectName, out="all", options="", ...){
	runFunsRstox(projectName, string="plot", out=out, options=options, ...)
}
#'
#' @export
#' @rdname getPlots
#' 
getReports <- function(projectName, out="all", options="", ...){
	runFunsRstox(projectName, string="report", out=out, options=options, write=TRUE, all.out=TRUE, ...)
}
#'
#' @export
#' @rdname getPlots
#' 
runFunsRstox <- function(projectName, string, out="all", options="", all.out=FALSE, ...){
	# Get the parameters
	dotlist <- list(...)
	if(nchar(options)>0){
		# Merge with '...', where 'options' overrides '...':
		dotlist <- c(getOptionsText(options), dotlist)
	}
	
	# Remove duplicates:
	dotlist <- dotlist[!duplicated(names(dotlist))]
	
	# Get available plotting functions:
	funs <- ls("package:Rstox")
	funs <- funs[tolower(substr(funs, 1, nchar(string)))==string]
	
	# Apply keywords (none other than 'all' implemented yet)!!!!!!!!!!!!!!!!!!:
	if(identical("all", out)){
		out <- funs
	}
	# Keywords are defined in .onload():
	else if(length(getRstoxEnv()$keywords)){
		keywordMatch <- startsWith(names(getRstoxEnv()$keywords), out)
		out <- unlist(getRstoxEnv()$keywords[keywordMatch])
	}
	# Intersect the requested and available functions:
	funs <- intersect(out, funs)
	
	# Run functions and return outputs:
	if(all.out){
		#out <- lapply(funs, function(xx) {cat("... running", xx, "...\n"); do.call(xx, c(list(projectName=projectName), dotlist))})
		out <- lapply(funs, function(xx) do.call(xx, c(list(projectName=projectName), dotlist)))
	}
	else{
		#out <- lapply(funs, function(xx) {cat("... running", xx, "...\n"); do.call(xx, c(list(projectName=projectName), dotlist))$filename})
		out <- lapply(funs, function(xx) do.call(xx, c(list(projectName=projectName), dotlist))$filename)
	}
	return(out[unlist(lapply(out, length))>0])
}
#'
#' @export
#' @rdname getPlots
#' 
getOptionsText <- function(options){
	# Split into single parameter definitions:
	options <- strsplit(options, ";", fixed=TRUE)[[1]]
	# Get parameter names:
	optionsNames <- gsub("=.*", "", options)
	optionsNames <- gsub("[[:blank:]]", "", optionsNames)
	# Evaluate parameter expressions:
	options <- lapply(options, function(x) eval(parse(text=x)))
	names(options) <- optionsNames
	options
}
