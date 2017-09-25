#*********************************************
#*********************************************
#' Prepare data from a project baseline run to the ECA model
#' 
#' This function reads data from a baseline run and converts to a list of data used the ECA model in the Reca package.
#' 
#' @param projectName  	The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param biotic		The process from which the biotic data are extracted, conventionally the BioticCovData process.
#' @param landing		The process from which the landing data are extracted, conventionally the LandingCovData process.
#' @param temporal		Optional definition of the temporal covariate (not yet implemented).
#' @param gearfactor	Optional definition of the gearfactor covariate (not yet implemented).
#' @param spatial		Optional definition of the spatial covariate (not yet implemented).
#'
#' @return A reference to the StoX Java baseline object
#'
#' @examples
#' # Create the test project:
#' createProject("Test_Rstox", files=system.file("extdata", "Test_Rstox", package="Rstox"), ow=TRUE)
#' system.time(eca <- baseline2eca("Test_Rstox"))
#' # Show the covariate definitions:
#' eca$resources
#' # Show the eca object:
#' str(eca)
#'
#' @export
#' @rdname baseline2eca
#'
baseline2eca <- function(projectName, biotic="BioticCovData", landing="LandingCovData", temporal=NULL, gearfactor=NULL, spatial=NULL){
	# Function that retreives year, month, day, yearday:
	addYearday <- function(x, datecar="startdate", tz="UTC", format="%d/%m/%Y"){
		x[[datecar]] <- as.POSIXlt(x[[datecar]], tz=tz, format=format)
		if(length(x$year)==0){
			x$year <- x[[datecar]]$year + 1900
		}
		x$month <- x[[datecar]]$mon + 1
		x$monthday <- x[[datecar]]$mday
		x$yearday <- x[[datecar]]$yday + 1
		x
	}
	# Function used for extracting covariate definitions:
	getCovDef <- function(x){
		if(length(grep(",", x[[3]]))){
		x[[3]] = strsplit(x[[3]], ",")
		x[[3]] = lapply(x[[3]], as.numeric)
		}
			#biotic <- as.data.frame(sapply(x, "[", x$CovariateSourceType=="Biotic", drop=FALSE))
		biotic <- x[x$CovariateSourceType=="Biotic", , drop=FALSE]
			#landing <- as.data.frame(lapply(x, "[", x$CovariateSourceType=="Landing", drop=FALSE))
		landing <- x[x$CovariateSourceType=="Landing", , drop=FALSE]
		list(biotic=biotic, landing=landing)
	}
	# Function for aggregating landing data in each covariate cell:
	aggregateLanding <- function(x, names, covariateDefinition){
		# Replace missing values by "-" to include these levels in the aggregation, and change back to NA at the end. This is because by() does not like NAs in the indices. We should find a better way later...:
		x[names][is.na(x[names])] <- "-"
		# Convert to tonnes from the hg definition in the landing data:
		weight_hektogram = by(x$rundvekt, x[,names], sum, na.rm=TRUE)
			weight_tonnes <- weight_hektogram/10000
			covariates <- attributes(weight_tonnes)$dimnames
			suppressWarnings(covariates <- lapply(covariates, as.numeric))
			covariates <- expand.grid(covariates)
			#covariates = expand.grid(attributes(weight_tonnes)$dimnames)
			covariatesFactor <- lapply(seq_along(names), function(xx) covariateDefinition[[xx]]$biotic[covariates[[xx]], "Covariate"])
			names(covariatesFactor) <- names(covariateDefinition)
			# Aggregate by the covariates:
		out = data.frame(covariates, covariatesFactor, weight_tonnes=c(weight_tonnes))
		# Remove empty cells and order:
		out = out[!is.na(out$weight_tonnes),,drop=FALSE]
		out = out[do.call(order, c(out[names], list(na.last=FALSE))),,drop=FALSE]
		out[out=="-"] <- NA
		rownames(out) <- seq_len(nrow(out))
			out
	}
	
	# Define covariate processes and returned process data:
									covariateProcessesData <- c("temporal", "season", "gearfactor", "spatial")
	# Get the baseline output:
	baselineOutput <- getBaseline(projectName, input=c("par", "proc"), fun=c(biotic, landing))
	
	# Run if both biotic and landing data are present:
	if(all(c(biotic[1], landing[1]) %in% names(baselineOutput$out))){
	
		#####################################
		##### (1) Get raw landing data: #####
		#####################################
		# (1a) Get the data and convert variable names to lower case:
		landing <- baselineOutput$out[[landing[1]]]
		names(landing) <- tolower(names(landing))
		landing <- addYearday(landing, datecar="formulardato", tz="UTC", format="%d/%m/%Y")
		#####################################
	
		############################################################
		##### (2) Get raw biotic data with some modifications: #####
		############################################################
		# (2a) Get the data and convert variable names to lower case:
		biotic <- baselineOutput$out[[biotic[1]]]
		names(biotic) <- tolower(names(biotic))
	
		# Detect whether temporal is defined with seasons, and add year and season and remove temporal in the process data:
		if(any(baselineOutput$out$LandingCovData$Season %in% TRUE)){
			baselineOutput$proc$season <- baselineOutput$proc$temporal
			years <- range(biotic$year, landing$year)
			years <- seq(years[1], years[2])
			baselineOutput$proc$year <- data.frame(CovariateSourceType=rep(c("Biotic","Landing"), each=length(years)), Covariate=rep(years, ,2), Value=rep(years, ,2))
			baselineOutput$proc$temporal <- NULL
		}
	
		# (2b) Define the present covariate names, which are some but not all of the following:
		implementedCovariateNames <- c("year", "season", "gearfactor", "spatial")
		implementedCovariateDescriptions <- c("The year covariate, used in conjunction with 'season'", "The season covariate defining seasons throughout a year", "The gear covariate given as groups of gear codes", "The spatial covariate giving polygons or locations")
		implementedCovariateProcesses <- c("DefineTemporalLanding", "DefineTemporalLanding", "DefineGearLanding", "DefineSpatialLanding")
	
		present <- which(implementedCovariateNames %in% names(biotic))
		covariateNames <- implementedCovariateNames[present]
		covariateDescriptions <- implementedCovariateDescriptions[present]
		covariateProcesses <- implementedCovariateProcesses[present]
		
		# (2c) Add yearday, year and month:
		biotic <- addYearday(biotic, datecar="startdate", tz="UTC", format="%d/%m/%Y")
	 
		# (2d) Hard code the lengthunits (from the sampling handbook). This must be changed in the future, so that lengthunitmeters is present in the biotic file:
		lengthcode <- 1:7
		lengthmeters <- c(1, 5, 0, 30, 50, 0.5, 0.1)/1000
		biotic$lengthunitmeters <- lengthmeters[match(biotic$lengthunit, lengthcode)]
	
		# (2e) Aggregate by lines without weight, but with equal length:
		duplines = duplicated(biotic[,c("serialno","length","weight")]) & is.na(biotic$age)
		if(any(duplines)){
			frequency = by(biotic$frequency, paste(biotic$serialno, biotic$length), sum)
			biotic = biotic[!duplines,]
			biotic$frequency = frequency
		}
		############################################################
	
		#######################################################
		### (3) Get covariate definitions and change names: ###
		#######################################################
		covariateDefinition <- lapply(baselineOutput$proc[covariateNames], getCovDef)
		# Add year covariate definitions if present:
		if("year" %in% covariateNames){
			year <- unique(c(landing$year, biotic$year))
			yearBiotic = data.frame(CovariateSourceType="Biotic", Covariate=year, Value=year)
			yearLanding = data.frame(CovariateSourceType="Biotic", Covariate=year, Value=year)
			covariateDefinition$year <- list(biotic=yearBiotic, landing=yearLanding)
		}
	 
		# Extract the covariates from biotic and landing in two separate matrices, and convert to integers using match():
		allLevels <- lapply(covariateNames, function(x) sort(unique(c(biotic[[x]], landing[[x]]))))
		Nlevels <- sapply(allLevels, length)
		covariateMatrixBiotic <- sapply(seq_along(covariateNames), function(i) match(biotic[[covariateNames[i]]], allLevels[[i]]))
		covariateMatrixLanding <- sapply(seq_along(covariateNames), function(i) match(landing[[covariateNames[i]]], allLevels[[i]]))
		colnames(covariateMatrixBiotic) <- covariateNames
		colnames(covariateMatrixLanding) <- covariateNames
		
		covariateLink <- lapply(seq_along(allLevels), function(i) match(allLevels[[i]], covariateDefinition[[i]]$biotic[,2]))
		covariateLink <- lapply(seq_along(allLevels), function(i) data.frame(Numeric=seq_along(allLevels[[i]]), Covariate=covariateDefinition[[i]]$biotic[covariateLink[[i]], 2]))
		names(covariateLink) <- names(covariateDefinition)
		
		
		#############################################
		##### (6) Get aggreagated landing data: #####
		#############################################
		#landingAggOrig <- aggregateLanding(landing, covariateNames, covariateDefinition=covariateDefinition)
		#landingAgg <- aggregateLanding(landing, covariateNames, covariateDefinition=covariateDefinition)
		#############################################
	
	
		# Extract the hierarchy matrix from StoX (not implemented on 2017-02-23):
		
	
	
		###########################################
		##### (7) Covariate meta information: #####
		###########################################
		# Add a data frame with meta information about the covariates:
		covType <- unlist(lapply(covariateProcesses, function(xx) baselineOutput$parameters[[xx]]$CovariateType))
		CAR <- rep(NA, length(covType))
		# This process assigns TRUE to CAR only if the parameter 'ConditionalAutoRegression' exists and is equal to the string "true". All other values except empty values (NULL) implies FALSE. If the parameter 'ConditionalAutoRegression' is not present, NA is used:
		temp <- lapply(covariateProcesses, function(xx) baselineOutput$parameters[[xx]]$ConditionalAutoRegression %in% TRUE)
		CAR[unlist(lapply(temp, length))>0] <- unlist(temp)
		 	# Make sure that CAR is a logical when covType is Random:
		CAR[is.na(CAR) & covType=="Random"] <- FALSE
		covariateInfo <- data.frame(
			Nlevels = Nlevels, 
			covType = covType, 
			CAR = CAR, 
			name = covariateNames, 
			description = covariateDescriptions
		)
		###########################################
	
		################################################
		##### (8) Fish age vs length-error matrix: #####
		################################################
		ageErrorData <- baselineOutput$proc$ageerror
		# Expand the AgeLength data to a sparse matrix:
		maxAge <- max(ageErrorData[,1:2])+1
		ageErrorMatrix <- matrix(0, ncol=maxAge, nrow=maxAge)
		ageErrorMatrix[as.matrix(ageErrorData[,1:2])+1] <- ageErrorData[,3]
		rownames(ageErrorMatrix) <- seq_len(maxAge)-1
		colnames(ageErrorMatrix) <- rownames(ageErrorMatrix)
		################################################
	
		############################################
		##### (9) Adjacent strata definitions: #####
		############################################
		stratumNeighbour <- baselineOutput$proc$stratumneighbour
		stratumNeighbourList <- as.list(stratumNeighbour[,2])
		names(stratumNeighbourList) <- stratumNeighbour[,1]
		stratumNeighbourList <- lapply(stratumNeighbourList, function(xx) as.numeric(unlist(strsplit(xx, ","))))
		#############################################
		
		# Return all data in a list
		list(biotic=biotic, landing=landing, covariateMatrixBiotic=covariateMatrixBiotic, covariateMatrixLanding=covariateMatrixLanding, resources=list(covariateInfo=covariateInfo, covariateDefinition=covariateDefinition, covariateLink=covariateLink, ageError=ageErrorMatrix, stratumNeighbour=stratumNeighbourList))
	}
	else{
		warning(paste0("The processes ", paste(c(biotic[1], landing[1]), collapse=" and "), "does not exist in the baseline model"))
		invisible(NULL)
	}
}
