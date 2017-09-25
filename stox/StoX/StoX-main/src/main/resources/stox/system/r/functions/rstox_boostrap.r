#*********************************************
#*********************************************
#' (Internal) Bootstrap biotic stations
#'
#' Resample (bootstrap) trawl stations based on survey (Cruise) number and station numbers (SerialNo) to estimate uncertainty in estimates.
#'
#' @param baseline		StoX Java baseline object
#' @param assignments	Trawl assignment from baseline
#' @param psuNASC		MeanNASC from baseline
#' @param stratumNASC	Strata NASC estimates from getNASCDistr(baseline)
#' @param resampledNASC	Resampled NASC distribution
#' @param parameters	Parameters set by user in Stox; 
#' \describe{
#'	\item{parameters$nboot}{Number of bootstrap replicates}
#'	\item{parameters$seed}{The seed for the random number generator (used for reproducibility)}
#' }
#' @param ...			Used for backwards compatibility.
#'
#' @return list with n = nboot data.frames containing results from baseline and bootstrap replicates
#'
#' @export
#' @rdname bootstrapBioticAcoustic
#'
bootstrapBioticAcoustic<-function(baseline, assignments, psuNASC, stratumNASC, resampledNASC, parameters, ...){
	bootstrapParallel(assignments=assignments, psuNASC=psuNASC, stratumNASC=stratumNASC, resampledNASC=resampledNASC, nboot=parameters$nboot, seed=parameters$seed, cores=1, baseline=baseline)
}


#*********************************************
#*********************************************
#' (Internal) Bootstrap biotic stations Swept Area version
#'
#' Resample (bootstrap) trawl stations based on survey (Cruise) number and station numbers (SerialNo) to estimate uncertainty in estimates
#'
#' @param baseline		StoX Java baseline object.
#' @param assignments	Trawl assignment from baseline.
#' @param parameters	R-object with parameters read from rprocess.txt.
#' \describe{
#'	\item{parameters$nboot}{Number of bootstrap replicates}
#'	\item{parameters$seed}{The seed for the random number generator (used for reproducibility)}
#' }
#' @param ...			Used for backwards compatibility.
#'
#' @return list with n = nboot data.frames containing results from baseline and bootstrap replicates
#'
#' @export
#' @rdname bootstrapBioticSweptArea
#'
bootstrapBioticSweptArea<-function(baseline, assignments, parameters, ...){
	bootstrapParallel(assignments=assignments, nboot=parameters$nboot, seed=parameters$seed, cores=1, baseline=baseline)
}	


#*********************************************
#*********************************************
#' (Internal) Run one bootstrap iteration of biotic stations and acoustic data 
#'
#' This function is used in bootstrapParallel().
#'
#' @param i				The boostrap iteration number.
#' @param projectName   The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param assignments	Trawl assignment from baseline.
#' @param strata		The strata of the survey.
#' @param psuNASC		MeanNASC from baseline.
#' @param stratumNASC	Strata NASC estimates from getNASCDistr(baseline).
#' @param resampledNASC	Resampled NASC distribution.
#' @param seedV			A vector of seeds. seedV[i] is used.
#'
#' @return list with (1) the abundance by length in the bootstrap run, (2) the abundance by super individuals in the bootstrap run
#'
#' @export
#' @rdname bootstrapOneIteration
#'
bootstrapOneIteration <- function(i, projectName, assignments, strata, psuNASC=NULL, stratumNASC=NULL, resampledNASC=NULL, seedV=NULL){
	
	# Define the start and end process between which to run the baseline. This is fixed here compared to used as parameters prior Rstox_1.6:
	startProcess="TotalLengthDist"
	endProcess="SuperIndAbundance"
	# Load Rstox if not already loaded:
	library(Rstox)
	# Get the baseline object (run if not already run), as this is needed to insert biostation weighting and meanNASC values into:
	baseline <- runBaseline(projectName=projectName, out="baseline", msg=FALSE, warningLevel=1)
	
	# Perform sampling drawing and replacement by stratum
	BootWeights <- data.frame()
	# Not effective if psuNASC has length 0:
	meanNASC <- psuNASC
	# Loop per strata:
	for(j in 1:length(strata)){
		# Get all stations with assignment to one or more PSUs in the current stratum:
		stations <- unique(getVar(assignments, "StID")[getVar(assignments, "Stratum")==strata[j]])
		# Change suggested for a problem with strata with no stations. The change was discarded, since there should be stations in all strata:
		if(length(stations)==0){
			warning(paste("No biotic stations in stratum", j))
			next
		}
		set.seed(seedV[i])
		# Resample BioStation:
		StID <- sample(stations, replace = TRUE)
		# Count weights from resample:
		count <- as.data.frame(table(StID))
		count$Stratum <- strata[j]
		BootWeights <- rbind(BootWeights,count)
		
		# Find NASC scaling factor. This is not directly related to the sampling of biotic stations above:
		if(length(psuNASC)>0){
			sm <- stratumNASC$NASC.by.strata$strata.mean[stratumNASC$NASC.by.strata$Stratum==strata[j]]
			# Scaling factor:
			meanNASC$NASC.scale.f[meanNASC$Stratum==strata[j]] <- ifelse(sm>0,resampledNASC[i,j]/sm,0)
		}
	}
	# Update biostation weighting
	asg2 <- merge(assignments,BootWeights,by=c("Stratum", "StID"), all.x=TRUE)
	asg2$StationWeight <- ifelse(!is.na(asg2$Freq), asg2$StationWeight*asg2$Freq, 0)
	# Update trawl assignment table in Stox Java object:
	setAssignments(projectName, assignments=asg2)

	# Scale and update NASC values
	if(length(psuNASC)>0){
		meanNASC$Value <- meanNASC$Value * meanNASC$NASC.scale.f
		# Update MeanNASC object in Java memory:
		setNASC(projectName, "MeanNASC", meanNASC)
	}
	# Run the sub baseline within Java
	#baseline <- runBaseline(projectName=baseline, save=FALSE, reset=TRUE)
	# Store the result
	#list(res.AbByLength <- getDataFrame1(baseline, 'AbundanceByLength'), res.AbByInd <- getDataFrame1(baseline, 'SuperIndAbundance'))
	#out <- getBaseline(baseline, fun=c('AbundanceByLength','SuperIndAbundance'), input=FALSE, msg=FALSE)
	
	# As of 2016-11-15 getBaseline() runs the baseline if reset=TRUE, so there is no need to run the baseline explicitely:
	#out <- getBaseline(projectName, fun=c("AbundanceByLength", "SuperIndAbundance"), input=FALSE, msg=FALSE, save=FALSE, reset=TRUE)
	# 2016-11-25 The function 'AbundanceByLength' changed name to 'Abundance':
	
	# Removed the output AbByLength (output from the process AbundanceByLength which has changed name to Abundance):
	#out <- getBaseline(projectName, fun=c("Abundance", "AbundanceByLength", "SuperIndAbundance"), input=FALSE, msg=FALSE, save=FALSE, reset=TRUE)
	getBaseline(projectName, startProcess=startProcess, endProcess=endProcess, fun="SuperIndAbundance", input=FALSE, msg=FALSE, save=FALSE, reset=TRUE, drop=FALSE, warningLevel=1)$outputData
}


#*********************************************
#*********************************************
#' (Internal) Bootstrap biotic stations and acoustic data
#'
#' Resample (bootstrap) trawl stations based on survey (Cruise) number and station numbers (SerialNo) to estimate uncertainty in estimates.
#'
#' @param projectName  	The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param assignments	Trawl assignment from baseline.
#' @param psuNASC		MeanNASC from baseline.
#' @param stratumNASC	Strata NASC estimates from getNASCDistr(baseline).
#' @param resampledNASC	Resampled NASC distribution.
#' @param nboot			Number of bootstrap replicates.
#' @param seed			The seed for the random number generator (used for reproducibility).
#' @param cores			An integer giving the number of cores to run the bootstrapping over.
#' @param baseline		Optional: a StoX baseline object returned from runBaseline().
#' @param msg			Logical: if TRUE print messages from runBaseline().
#' @param parameters	Parameters set by user in Stox (only kept for compatibility with older versions);.
#' \describe{
#'	\item{parameters$nboot}{Number of bootstrap replicates}
#'	\item{parameters$seed}{The seed for the random number generator (used for reproducibility)}
#' }
#'
#' @return list with (1) the abundance by length in the orginal model, (2) the abundance by length in the bootstrap run, (3) the abundance by super individuals in the orginal model, (4) the abundance by super individuals in the bootstrap run
#'
#' @importFrom parallel detectCores makeCluster stopCluster
#' @importFrom pbapply pblapply
#'
#' @export
#' @rdname bootstrapParallel
#'
bootstrapParallel <- function(projectName, assignments, psuNASC=NULL, stratumNASC=NULL, resampledNASC=NULL, nboot=5, seed=1, cores=1, baseline=NULL, msg=TRUE, parameters=list()){
	# Stop the funciton if both projectName and baseline are missing:
	if(length(baseline)==0 && missing(projectName)){
		stop("Either projectName or baseline must be given.")
	}
 
	# Allow for inputs given in 'nboot' and 'seed' to prepare for the higher level functions bootstrapAcoustic() and runBootstrap():
	if(length(parameters$nboot)){
		nboot <- parameters$nboot
	}
	if(length(parameters$seed)){
		seed = parameters$seed
	}

	# Filter assignments against NASC:
	if(length(psuNASC)){
		assignments <- droplevels(subset(assignments, getVar(assignments, "Stratum") %in% getVar(psuNASC, "Stratum")))
	}
	# Unique trawl station ID:
	assignments$StID <- getVar(assignments, "Station")
	
	set.seed(if(isTRUE(seed)) 1234 else if(is.numeric(seed)) seed else NULL) # seed==TRUE giving 1234 for compatibility with older versions
	# Makes seed vector for fixed seeds (for reproducibility):
	seedV <- sample(c(1:10000000), nboot, replace = FALSE)
	# Define strata, either by acoustic values (if psuNASC is given) or by the trawl assignments:
	strata <- unique(if(length(psuNASC)>0) getVar(psuNASC, "Stratum") else getVar(assignments, "Stratum"))
	
	# Store the SuperIndAbundance from the original model:
	base.SuperIndAbundance <- getBaseline(baseline, fun="SuperIndAbundance", input=FALSE, msg=msg, drop=FALSE)$outputData$SuperIndAbundance
	
	
		
		
	availableCores = detectCores()
	if(cores>availableCores){
		warning(paste0("Only ", availableCores, " cores available (", cores, " requested)"))
	}
	cores = min(cores, nboot, availableCores)
	# Generate the clusters of time steps:
	
	if(cores>1){
		cat(paste0("Running ", nboot, " bootstrap replicates (using ", cores, " cores in parallel):\n"))
		cl<-makeCluster(cores)
		# Bootstrap:
		out <- pblapply(seq_len(nboot), bootstrapOneIteration, projectName=projectName, assignments=assignments, strata=strata, psuNASC=psuNASC, stratumNASC=stratumNASC, resampledNASC=resampledNASC, seedV=seedV, cl=cl)
		# End the parallel bootstrapping:
		stopCluster(cl)
	}
	else{
		cat(paste0("Running ", nboot, " bootstrap replicates:\n"))
		out <- pblapply(seq_len(nboot), bootstrapOneIteration, projectName=projectName, assignments=assignments, strata=strata, psuNASC=psuNASC, stratumNASC=stratumNASC, resampledNASC=resampledNASC, seedV=seedV)
	}
	
	out <- unlist(out, recursive=FALSE)
	
	# Order the output from the bootstrapping:
	names(out) <- paste0(names(out), "_run", seq_along(out))
	
	bootstrapParameters <- list(
		seed = seed, 
		seedV = seedV, 
		nboot = nboot, 
		cores = cores
		)
	# Return the bootstrap data and parameters:
	list(base.SuperIndAbundance=base.SuperIndAbundance, SuperIndAbundance=out, bootstrapParameters=bootstrapParameters) 
}


#*********************************************
#*********************************************
#' Run a bootstrap in StoX
#'
#' Resample (bootstrap) trawl stations based on swept area data and possibly also acoustic data to estimate uncertainty in estimates.
#'
#' @param projectName   The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param acousticMethod,bioticMethod   Specification of the method to use for bootstrapping the acoustic and biotic data. These can be formulas or characters which can be converted to formulas, given as 'variable to bootstrap ~ level to bootstrap within'. Multiple bootstraps can be specified, such as bioticMethod=c(EDSU~Stratum, Sample~EDSU), instructing to bootstrap the EDSUs (stations) within each stratum, and also bootstrapping the individual catch samples within each EDSU. Only certain strings can be used in the formulas, as shown in the table below. The methods can also be given as functions of at least two arguments, 'projectName' and 'process', which makes modifications to the output from getBaseline(projectName, proc=process, input=NULL) and sends the modified data back to the baseline in Java memory and runs the baseline with the modified data. Using funcitons is not yet implemented. 
#' \tabular{ccc}{
#'   Level \tab Acoustic \tab Biotic\cr
#'   Survey \tab Survey \tab Survey\cr
#'   Stratum \tab Stratum \tab Stratum\cr
#'   Assignment \tab Not relevant \tab Assignment of biotic station groups to acoustic PSUs\cr
#'   PSU \tab Acoustic data averaged over e.g. one tansect \tab Biotic station group \cr
#'   EDSU \tab Acoustic data averaged over e.g. one nmi \tab Biotic station\cr
#'   Sample \tab Ping \tab Individal catch sample
#' }
#' @param nboot			Number of bootstrap replicates.
#' @param seed			The seed for the random number generator (used for reproducibility).
#' @param cores			An integer giving the number of cores to run the bootstrapping over.
#' @param msg			Logical: if TRUE print messages from runBaseline().
#' @param ...			Used for backwards compatibility.
#'
#' @return list with (1) the abundance by length in the orginal model, (2) the abundance by length in the bootstrap run, (3) the abundance by super individuals in the orginal model, (4) the abundance by super individuals in the bootstrap run
#'
#' @examples
#' \dontrun{
#' b <- runBootstrap("Test_Rstox", nboot=10, seed=1, cores=1)}
#'
#' @importFrom stats terms as.formula
#'
#' @export
#' @rdname runBootstrap
#'
runBootstrap <- function(projectName, acousticMethod=NULL, bioticMethod=NULL, nboot=5, seed=1, cores=1, msg=TRUE, ...){
	# Function used for extracting either a matrix of bootstrap variables and domains, or the function specified by the user:
	getBootstrapMethod <- function(x){
		isNULL <- any(length(x)==0, sum(nchar(x))==0, identical(x, FALSE))
		if(isNULL){
			return(NULL)
		}
		if(is.function(x)){
			warning("Method as a function not yet implemented")
			return(NULL)
		}
		if(!any(unlist(gregexpr("~", as.character(x), fixed=TRUE))>0)){
			warning("Invalid formula")
			return(NULL)
		}
		# The c(x) is added to assure that sapply works on each formula and not on the parts of one formula, if only one is given:
		if(is.character(x) || all(sapply(c(x), function(xx) class(xx)=="formula"))){
			return(sapply(c(x), function(xx) rownames(attributes(terms(as.formula(xx)))$fact)))
		}
		else{
			warning("Invalid input")
			return(NULL)
		}
	}
	
	lll <- list(...)
	### Backwards compatibility: ###
	# If the old numIterations is given, override the nboot by this:
	if(length(lll$numIterations)){
		nboot <- lll$numIterations
	}
	### End of backwards compatibility: ###
	
	# Run the different bootstrap types:
	matchOldAcoustic <- FALSE
	matchOldSweptArea <- FALSE
	acousticMethod <- getBootstrapMethod(acousticMethod)
	bioticMethod <- getBootstrapMethod(bioticMethod)
	
	if(	length(acousticMethod)==2 
		&& length(bioticMethod)==2 
		&& startsWith(tolower(acousticMethod[1,1]), "psu") 
		&& startsWith(tolower(acousticMethod[2,1]), "stratum") 
		&& startsWith(tolower(bioticMethod[2,1]), "stratum")  
		&& (startsWith(tolower(bioticMethod[1,1]), "edsu") || startsWith(tolower(bioticMethod[1,1]), "psu"))){
			matchOldAcoustic <- TRUE
	}
	else{
		matchOldAcoustic <- FALSE
	}
	
	if(	length(acousticMethod)==0 
		&& length(bioticMethod)==2 
		&& (startsWith(tolower(bioticMethod[1,1]), "edsu") || startsWith(tolower(bioticMethod[1,1]), "psu")) 
		&& startsWith(tolower(bioticMethod[2,1]), "stratum")){
			matchOldSweptArea <- TRUE
	}
	else{
		matchOldSweptArea <- FALSE
	}
	
	# Backwards compatibility for type="Acoustic", hidden in ... (used prior to Rstox 1.5):
	if(length(lll$type)){
		matchOldAcoustic <- matchOldAcoustic || lll$type=="Acoustic"
	}
	# Backwards compatibility for type="SweptArea", hidden in ... (used prior to Rstox 1.5):
	if(length(lll$type)){
		matchOldSweptArea <- matchOldSweptArea || lll$type=="SweptArea"
	}
	
	# Apply the original bootstrap methods in Rstox:
	if(matchOldAcoustic){
		# Baseline and biotic assignments:
		baseline <- runBaseline(projectName, out="baseline", msg=msg, reset=TRUE)
		assignments <- getBioticAssignments(baseline=baseline)
		# Acoustic data:
		# NOTE: The psuNASC is read here once, and used to scale the PSUs in the baseline at each bootstrap replicate. It is important to keept this, since the PSUs are changed in memory in each core, and we wish to scale relative to the original values each time. For the same reason, the PSUs are set back to the original value at the end of bootstrapParallel() when run on 1 core:
		psuNASC <- getPSUNASC(baseline=baseline)
		stratumNASC <- getNASCDistr(baseline=baseline, psuNASC=psuNASC, NASCDistr="observed")
		resampledNASC <- getResampledNASCDistr(baseline=baseline, psuNASC=psuNASC, stratumNASC=stratumNASC, parameters=list(nboot=nboot, seed=seed))
		# Assign varialbes to the project environment:
		setProjectData(projectName=projectName, var=psuNASC)
		setProjectData(projectName=projectName, var=stratumNASC)
		setProjectData(projectName=projectName, var=resampledNASC)
		
		# Run bootstrap:
		bootstrap <- bootstrapParallel(projectName=projectName, assignments=assignments, psuNASC=psuNASC, stratumNASC=stratumNASC, resampledNASC=resampledNASC, nboot=nboot, seed=seed, cores=cores, baseline=baseline, msg=msg)
		
		# Add the method specification:
		bootstrap$bootstrapParameters$acousticMethod <- acousticMethod
		bootstrap$bootstrapParameters$bioticMethod <- bioticMethod
		bootstrap$bootstrapParameters$description <- "Original Rstox default 'Acoustic' method up until Rstox 1.5, bootstrapping acousic PSUs within stratum, and scaleing the PSUs to have mean matching that of the bootstrap, and bootstrapping biotic stations within stratum, and assigning station weights equal to the frequency of occurrence of each station"
		bootstrap$bootstrapParameters$alias <- "Acoustic"
		
		# Assign the bootstrap to the project environment:
		setProjectData(projectName=projectName, var=bootstrap)
		# Rerun the baseline to ensure that all processes are run, and return the boostraped data:
		baseline <- runBaseline(projectName, reset=TRUE, msg=FALSE)
		invisible(bootstrap)
	}
	else if(matchOldSweptArea){
		# Baseline and biotic assignments:
		baseline <- runBaseline(projectName, out="baseline", msg=msg, reset=TRUE)
		assignments <- getBioticAssignments(baseline=baseline)
		# Run bootstrap:
		bootstrap <- bootstrapParallel(projectName=projectName, assignments=assignments, nboot=nboot, seed=seed, cores=cores, baseline=baseline, msg=msg)
		
		# Add the method specification:
		bootstrap$bootstrapParameters$acousticMethod <- acousticMethod
		bootstrap$bootstrapParameters$bioticMethod <- bioticMethod
		description <- "Original Rstox default 'SweptArea' method up until Rstox 1.5, bootstrapping biotic stations within stratum, and assigning station weights equal to the frequency of occurrence of each station"
		alias <- "SweptArea"
		
		# Assign varialbes to the global environment for use in plotting functions. This should be changed to a local Rstox environment in the future:
		setProjectData(projectName=projectName, var=bootstrap)
		# Rerun the baseline to ensure that all processes are run, and return the boostraped data:
		baseline <- runBaseline(projectName, reset=TRUE, msg=FALSE)
		invisible(bootstrap)
	}
	else{
		warning("Invalid bootstrap method...")
	}
}
