#*********************************************
#*********************************************
#' Get NMD API data and reference information
#'
#' \code{getNMDinfo} converts, prints and optionally returns NMD reference information given a search string to the reference information. Used in StoX.URL(). \cr 
#' \code{getNMDdata} downloads data from specific cruises, cruise series ot survey time series from NMD. \cr 
#' \code{downloadXML} downloads xml data from an API, parses the xml data, and converts to a list (the latter is time consuming).
#'
#' @param type					A character string naming the type of information to return for the specifications given in 'spec'. Possible values are shown in the list below. Any reference data can be requested, and the names of the possible reference data are returned by running getNMDinfo():
#' \describe{
#'	\item{NULL}{List of available reference data}
#'	\item{"c"}{List of cruises (Will be implemented in version 2)}
#'	\item{"cs"}{List of cruise series. If given as a vector of length 2, where the first element is "cs" and the second is the exact case sensitive name of a cruise series, information only about that cruise sereis is returned.}
#'	\item{"sts"}{List of survey time series. Can be given as a two element vector as for "cs".}
#'	\item{"v"}{List of vessels, where the first line of the platform information is extracted and presented in a matrix with vessels in the rows. Use "platform" to preserve all details about the platforms/vessels}
#'	}
#' @param ver					The version of the API. As of 2015-05 only version 1 is available. Version 2 will include the possibility to return a list of all cruises.
#' @param API					The path to the API.
#' @param recursive				Logical, special for type \%in\% c("cs","sts"); if FALSE only the list of cruise series or survey time series is returned.
#' @param msg					Logical: if TRUE a message is printed to the consolle stating the estimated time left for the funciton.
#' @param simplify				Logical: if TRUE simplify the data into matrices instead of complicated lists in some cases like taxa.
#' @param cruise				Either the code of a cruise, such as "2015116", or the full or short name of a cruise series or survey time series. In the current version, if given as a cruise code, the parameter 'shipname' must be given as well, based on which the path to the cruise is searched for by functionallity provided by NMD. For cruises prior to the year 1995 several vessels can be linked to the same cruise code, and as of version 2 the user will by default be asked to specify which vessel(s) to specify the vessels when this occurs, instead of having to specify the cruise initially.
#' @param year					Used in conjunction with 'shipname' to get all cruises from one or more years from a specific ship.
#' @param shipname				Specifies the ship name (see 'cruise' and 'year').
#' @param serialno				The serial number range within which to download data.
#' @param tsn					The species code for downloading a specific species. See the examples for how to get the \code{tsn} of a species.
#' @param datatype				The type of data requested. Currently implemented are "echosunder" and "biotic", while "landing" and "ctd" are in the pipeline. datatype=NULL (default) returns all possible data.
#' @param dir					The path to the directory in which to place the StoX project holding the downloaded data.
#' @param group					Specifies how to gruop the data: (1) If given as "year", the data are split into years, and one StoX project is saved for each year; (2) if given as "cruise", one Stox project is generated for each cruise, and (3) group is NULL, all data are saved in one StoX project. The default "default" groups by years if several cruises are requested and by cruise otherwise.
#' @param subset				An integer vector giving the subset of the cruises to download in a cruise series (1 meaning the first cruise and c(2,5) cruise nr 2 and 5).
#' @param filebase				The prefix to use in the names of the StoX projects to which the data are downloaded.
#' @param cleanup				Logical: if FALSE, zip files containing cruise series or survey time series are not deleted.
#' @param model					The model to use, either given as a string specifying a template, or a vector of process names or list of processes given as lists of parameter specifications (not yet implemented). Show available templates with createProject().
#' @param ow   					Specifies whether to ovewrite existing project: If TRUE, overwrite; if FALSE, do not overwrite; if NULL (default), aks the user to confitm overwriting.
#' @param return.URL			Logical: If TRUE, return the URL of the files to download.
#' @param ...   			Same as parlist, but can be specified separately (not in a list but as separate inputs).
#' @param URL					The URL(s) to the xml data.
#' @param list.out				Logical: If TRUE, convert the XML data to a list (time consuming).
#' @param file					The path to a the file to which the data are saved.
#' @param quiet					Used in download.file().
#' @param cruisenrANDshipname	A vector of two elements, the first being the cruise number and the second the ship name.
#'
#' @details
#' If non-standard characters are not shown as expected, it might be an issue of locale encoding. 
#' It may help to run Sys.setlocale(category = "LC_ALL", locale = ""), at least to display nordic characters on a nordic system.
#'
#' @keywords NMD-API
#'
#' @examples
#' # A list of available reference data:
#' g1 <- getNMDinfo()
#' # List of cruise series:
#' g2 <- getNMDinfo("cs")
#' # List of survey time series:
#' g3 <- getNMDinfo("sts")
#' # List of vessels (the first vessel per plarform) and the slightly more complicated list of platforms (where there can be several vessels per platform). These requestes may take tens of seconds:
#' g4 <- getNMDinfo("v")
#' g5 <- getNMDinfo("platform")
#' # Get other types of information:
#' g6 <- getNMDinfo("gearcondition")
#' g7 <- getNMDinfo("missiontype")
#' g8 <- getNMDinfo("person")
#' g9 <- getNMDinfo("taxa")
#' # Get the tsn code of torsk:
#' g9[tolower(g9$synonym.name) == "torsk", ]
#' # And all names containing "torsk":
#' g9[grep("torsk", g9$synonym.name, ignore.case=TRUE),]
#' 
#' # For examples of downloading data from Norwegian Marine Data Centre (NMD in norwegian), go to ftp://ftp.imr.no/StoX/Download/Rstox/Examples/Rstox-example_1.6.R.
#' 
#' @export
#' @rdname getNMDinfo
#' 
getNMDinfo <- function(type=NULL, ver=1, API="http://tomcat7.imr.no:8080/apis/nmdapi", recursive=TRUE, msg=FALSE, simplify=TRUE){
	###############################
	##### Internal functions>>> #####
	# Function used for extracting a data frame of the cruises used in a cruise series:
	getCruiseSeriesCruises <- function(x, URLbase, name="Cruises"){
		this <- downloadXML(paste(URLbase, x, sep="/"), msg=msg)$Sample
		# Get years and repeat by the number of cruises for each year
		years <- unname(sapply(this, "[[", ".attrs"))
		nCruisesPerYear <- sapply(this, function(xx) length(xx$Cruises))
		years <- rep(years, nCruisesPerYear)
		CruiseShipName <- as.matrix_full(unlist(lapply(this, "[[", name), use.names=FALSE, recursive=FALSE))
		cbind(year=years, Cruise=CruiseShipName[,1], ShipName=CruiseShipName[,2])
	}
	# Function used for extracting a data frame of the StoX projects used in a survey time series:
	getSurveyTimeSeriesProjects <- function(x, URLbase){
		this <- downloadXML(paste(URLbase, x, sep="/"), msg=msg)$Sample
		as.matrix_full(this)
	}
	# Function for extracting the platform information from the NMD platform data structure:
	platformExtract <- function(x){
		# Small function for extracting the platform code from an NMD platform:
		getPlatformCode <- function(xx){
			out <- xx["platformCode"]
			names(out) <- gsub(" ", "_", xx["sysName"])
			out
		}
		# Small function for extracting the from and to date from a NMD platform:
		getvalidFromTo <- function(xx){
			c(xx["validFrom"], xx["validTo"])
		}
		# Extract the relevant data from the platform reference data:
		platformExtractOne <- function(x){
			# Group the platformCodes according to dates:
			codes <- unlist(x[names(x)=="platformCodes"], recursive=FALSE, use.names=FALSE)
			dates <- NULL
			if(length(codes)){
				dates <- t(sapply(codes, getvalidFromTo))
				codes <- lapply(codes, getPlatformCode)
				codes <- split(codes, apply(dates, 1, paste, collapse=""))
				codes <- lapply(codes, unlist)
				codes <- as.matrix_full(codes)
				dates <- unique(dates)
			}
			out <- cbind(nation=unname(x$nation$.attrs), platformType=unname(x$platformType$.attrs), codes, dates)
		}
		# Drop elements with length 1, indicating time stamps or similar:
		x <- x[sapply(x, length)>1]
		lapply(x, platformExtractOne)
	}
	# Function used for simplifiying taxa data into a matrix:
	gettaxaMatrix <- function(x, name=".attrs"){
		# Get the number of elements per taxa for the specified name:
		Ind <- sapply(x, function(x){ temp <- x[[name]]; if(is.list(temp)) length(temp) else 1 }) # Vector
		Ind <- rep(seq_along(Ind), Ind) # Vector
	
		# If the data are given as a list for each taxa, get the indices for the rows at which to insert the data in the matrix 'allMatrix':
		numFull <- unlist(lapply(x, function(x){ temp <- x[[name]]; if(is.list(temp)) unlist(lapply(temp, length)) else length(temp) })) # Vector
		# Get the indices to which taxa each element in all belong:
		rowInd <- rep(seq_along(numFull), numFull) # Vector
	
		# Get a vector of the data for each taxa, in a list:
		allList <- lapply(x, function(x) unlist(x[[name]])) # List
		# Flatten the data in one vector:
		all <- unlist(allList) # Vector
		# Get all names:
		allNames <- unlist(lapply(x, function(x){ temp <- x[[name]]; if(is.list(temp)) names(unlist(temp)) else names(temp) })) # Vector
		# Get the unique names 
		allUniqueNames <- unique(allNames)
		# Create a matrix of NAs to be filled with the data using the indices 'rowInd' and names 'allNames':
		allMatrix <- as.data.frame(array(NA, dim=c(max(rowInd), length(allUniqueNames))))
		names(allMatrix) <- allUniqueNames
		allMatrix[cbind(rowInd, match(allNames, allUniqueNames))] <- all
		cbind(Ind=Ind, allMatrix)
	}
	##### <<<Internal functions #####
	###############################
	
	####################################################
	# Get the list of reference data types:
	if(length(type)==0){
		URLbase <- paste(API, "reference", paste0("v", ver), sep="/")
		# Get the list of cruise series:
		data <- downloadXML(URLbase, msg=msg)
		data <- unname(sapply(data[names(data)=="element"], "[[", "text"))
		return(data)
	}
	##### Treat the requested type of information>>> #####
	# A string requested to the API consists of the following parts:
	# 1. The API specific string: "http://tomcat7.imr.no:8080/apis/nmdapi"
	# 2. The datatype: "cruise", "reference", "biotic", "echosounder", "stox", "cruiseseries", "surveytimeseries", where the latter two may be moved to the reference data
	# 3. Version: Such as 1 (current as of 2016-05) or 2
	type[1] <- tolower(type[1])
	vesseltype <- FALSE
	if(length(intersect(type, c("v", "vessel")))){
		type <- "platform"
		vesseltype <- TRUE
	}
	#if(length(intersect(type, c("c", "cruises")))){
	#	datatype <- "echosounder"
	#	type <- NA
	#}
	#else if(length(intersect(type, c("v", "vessel")))){
	#	datatype <- "reference"
	#	type <- "platform"
	#	vesseltype <- TRUE
	#}
	#else{
	#	datatype <- "reference"
	#}
	##### <<<Treat the requested type of information #####
	####################################################
	
	################################
	##### Get the information>>> #####
	# Get the list of cruises:
	#else 
	if(type[1] %in% c("c", "cruises")){
		warning("Returning a list of cruises will be implemented in version 2")
	}
	# Get the list of cruises series with cruises for each series:
	else if(type[1] %in% c("cs", "cruiseseries")){
		URLbase <- paste(API, "cruiseseries", paste0("v", ver), sep="/")
		# Get the list of cruise series. The cruise series name can be given exactly as the second element of 'type':
		if(length(type)==2){
			data <- type[2]
			recursive <- TRUE
		}
		else{
			data <- downloadXML(URLbase, msg=msg)
			data <- unlist(data[names(data)=="element"], use.names=FALSE)
		}
		if(recursive){
			namesdata <- data
			data <- lapply(data, getCruiseSeriesCruises, URLbase=URLbase)
			names(data) <- namesdata
		}
	}
	# Get the list of survey time series with StoX projets for each series:
	else if(type[1] %in% c("sts", "sureveytimeseries")){
		URLbase <- paste(API, "surveytimeseries", paste0("v", ver), sep="/")
		# Get the list of survey time series. The survey time series name can be given exactly as the second element of 'type':
		if(length(type)==2){
			data <- type[2]
			recursive <- TRUE
		}
		else{
			data <- downloadXML(URLbase, msg=msg)
			data <- unlist(data[names(data)=="element"], use.names=FALSE)
		}
		if(recursive){
			namesdata <- data
			data <- lapply(data, getSurveyTimeSeriesProjects, URLbase=URLbase)
			names(data) <- namesdata
			## Unlist if only one element is returned (occurs when only one survey time series exists or is requested by the used through a second string in 'type'):
			#if(length(data)==1){
			#	data <- data[[1]]
			#}
		}
	}
	else{
		# Get the available reference data types:
		URLbase <- paste(API, "reference", paste0("v", ver), sep="/")
		# Get the list of cruise series:
		ref <- downloadXML(URLbase, msg=msg)
		ref <- unname(sapply(ref[names(ref)=="element"], "[[", "text"))
		if(length(type)==0){
			return(ref)
		}
		
		# Match the 'type' with the reference data available:
		type <- ref[tolower(ref) == type[1]]
		
		# Download the reference data:
		URLbase <- paste(API, "reference", paste0("v", ver), type[1], sep="/")
		data <- downloadXML(URLbase, msg=msg)
		
		# Simplify the data:
		if(simplify){
			if(length(data)){
				# Remove elements with length 1, indicating time stamps and the like:
				data <- data[sapply(data, length)>1]
				if("element" %in% names(data)){
					if("text" %in% names(data[[1]])){
						data <- sapply(data[names(data)=="element"], "[[", "text")
					}
					else{
						data <- as.matrix_full(data[names(data)=="element"])
					}
				}
				# Special case for platform:
				else if("platform" %in% names(data)){
					data <- platformExtract(data)
					if(vesseltype){
						data <- as.matrix_full(lapply(data, head, 1))
					}
				}
				# Special case for taxa:
				else if("taxa" %in% names(data)){
					attrs <- gettaxaMatrix(data, name=".attrs")
					synonyms <- gettaxaMatrix(data, name="taxaSynonyms")
					data <- merge(attrs, synonyms)
				}
				# Else do a basic simplification:
				else if(is.list(data[[1]])){
					data <- t(Reduce(cbind, data))
				}
			}
		}
	}
	##### <<<Get the information #####
	################################
	data
}
#'
#' @export
#' @rdname getNMDinfo
#' 
getNMDdata <- function(cruise=NULL, year=NULL, shipname=NULL, serialno=NULL, tsn=NULL, datatype=NULL, dir=NULL, group="default", subset=NULL, filebase="NMD", ver=1, API="http://tomcat7.imr.no:8080/apis/nmdapi", cleanup=TRUE, model="StationLengthDistTemplate", msg=TRUE, ow=NULL, return.URL=FALSE, ...){
	
	##### Internal functions: #####
	# Function for converting a vector of serial numbers, which can be fully or partly sequenced (incriment of 1 between consecutive elements):
	getSerialnoRanges <- function(x){
		d <- diff(x)
		starts <- c(1, which(d != 1)+1)
		ends <- c(which(d != 1), length(x))
		cbind(x[starts], x[ends])
	}
	# Function for getting the URL for serial number searches:
	getURLBySerialno <- function(serialno, year, tsn=NULL, API="http://tomcat7.imr.no:8080/apis/nmdapi", ver=1){
		paste(API, "biotic", paste0("v", ver), year[1], serialno[1], serialno[2], tsn[1], "serial", sep="/")
	}
	# Function for downloading a stox project in a surveytimeseries:
	getSurveyTimeSeriesStoXProjects <- function(sts, dir, cleanup=TRUE, downloadtype="?format=zip"){
		stsMatrix <- sts[[1]]
		sts <- names(sts)
		status = nrow(stsMatrix)
		projectNames <- rep(NA, length(status))
		for(i in seq_len(status)){
			URL <- URLencode(paste(API, "surveytimeseries", paste0("v", ver), sts, "samples", stsMatrix[i,"sampleTime"], sep="/"))
			# Add download type:
			URL = paste0(URL, downloadtype)
			projectNames[i] <- file.path(dir, paste0(sts, "_", stsMatrix[i,"sampleTime"]))
			zipPath <- paste0(projectNames[i], ".zip")
			# Added mode="wb" to make the zip openable on Windows:
			status[i] <- download.file(URL, zipPath, mode="wb")
			
			# Overwriting or not?:
			if(file.exists(projectNames[i])){
				if(length(ow)==0){
					ans <- readline(paste0("Project \"", projectNames[i], "\" already exists. Overwrite? (y/n)\n"))
					if(ans!="y"){
						cat("Not overwriting:", projectNames[i], "\n")
						return()
					}
				}
				else if(!ow){
					cat("Not overwriting:", projectNames[i], "\n")
					return()
				}
			}
			
			# Delete the existing project if the conditional expression above did not exit the function:
			unzip(zipPath, exdir=dirname(zipPath))
			if(cleanup){
				unlink(zipPath)
			}
		}
		invisible(basename(projectNames))
	}
	# Function for extracting URLs to a list of cruises. The conver from the names given by 'datatype' to the names given by 'StoX_data_types' is needed to save the data in the StoX directory structure:
	getCruiseStrings <- function(cs, datatype, StoX_data_types, ver){
		# Use the search function in version 1:
		if(ver==1){
			# Pick out the first element of 'cs', since a list is always returned from getNMDinfo():
			#cruiseURL <- apply(cs[[1]][,c("Cruise", "ShipName"), drop=FALSE], 1, searchNMDCruise, datatype=datatype[1])
			cruiseURL <- t(apply(cs[[1]][,c("Cruise", "ShipName"), drop=FALSE], 1, searchNMDCruise, datatype=datatype))
			#cruiseURL <- sapply(datatype, function(xx) sub(datatype[1], xx, cruiseURL))
			#if(length(dim(cruiseURL))==0){
			#	dim(cruiseURL) <- c(1, length(cruiseURL))
			#}
			colnames(cruiseURL) <- StoX_data_types
			cbind(cs[[1]], URL=cruiseURL)
		}
		else{
			warning("Version 1 is currently the latest verison")
		}
	}
	
	# Set the download directory to the default StoX directory if 'dir' in not set:
	dir <- getProjectPaths(projectName="", dir=dir)$projectRoot
	
	# Define the valid types:
	NMD_data_types <- getRstoxEnv()$NMD_data_types
	StoX_data_types <- getRstoxEnv()$StoX_data_types
	if(length(datatype)==0){
		datatype <- NMD_data_types
	}
	else{
		StoX_data_types <- StoX_data_types[NMD_data_types %in% datatype]
	}
	# Define the number of columns in the cruise matrix which are not ************
	firstcols <- 1:3
	firstcols2 <- 1:2
	##########
	
	##### 1. Get URLs to the cruises: #####
	# Get the available cruise series and survey time series (StoX) in order to recognize the inputs:
	cs <- getNMDinfo("cs", recursive=FALSE)
	sts <- getNMDinfo("sts", recursive=FALSE)
	# Match identically against 'cs' and 'sts':
	if(length(cruise)==1 && cruise %in% sts){
		sts <- intersect(sts, cruise)
		sts <- getNMDinfo(c("sts", sts))
		# Download and unzip all StoX projects of the survey time series:
		status <- getSurveyTimeSeriesStoXProjects(sts=sts, dir=dir, cleanup=cleanup, downloadtype="?format=zip")
		###lapply(stsMatrix[,"sampleTime"], getSurveyTimeSeriesStoXProject, sts=sts, dir=dir, cleanup=cleanup, downloadtype="?format=zip")
		return(status)
	}
	
	# The matrix 'cruiseMatrix' holds the information needed to make the call to the NMD API. The columns are (1) year, (2) Cruise (cruise number), (3) ShipName, and (4, 4 + length(StoX_data_types)-1) the StoX_data_types:
	if(length(cruise)==1 && cruise %in% cs){
		cs <- intersect(cs, cruise)
		filebase <- cs
		cs <- getNMDinfo(c("cs", cs))
		cruiseMatrix <- getCruiseStrings(cs, datatype, StoX_data_types, ver)
	}
	else{
		# Special case if the user has requested data by serial number, in which case generating 1 StoX project for all the data and dowloading is completed and returned here:
		if(length(serialno)){
			if(length(year)==0){
				warning("'year' must be given when serial number is requested")
				return(NULL)
			}
			serialno <- getSerialnoRanges(serialno)
			serialnoRanges <- apply(serialno, 1, paste, collapse="-")
			serialnoStrings <- paste0("serialno", "_", serialnoRanges)
			serialnoString <- paste("serialno", serialnoRanges, sep="_", collapse="_")
			tsnString <- paste("tsn", tsn, sep="_")
			
			projectName <- paste(filebase, serialnoString, tsnString, "year", year[1], sep="_")
			projectName <- gsub("__", "_", projectName)
			projectPath <- createProject(projectName, dir=dir, model=model, ow=ow, ...)
			
			xmlfiles <- rep(NA, nrow(serialno))
			for(i in seq_along(xmlfiles)){
				URL <- getURLBySerialno(serialno=serialno[i,], year=year[1], tsn=tsn, API=API, ver=ver)
				xmlfiles[i] <- file.path(projectPath, "input", "biotic", paste0(serialnoStrings[i], ".xml"))
				downloadXML(URL, msg=msg, list.out=FALSE, file=xmlfiles[i])
			}
			updateProject(projectName)
			return(projectName)
		}
		# Get data by cruise:
		else{
			if(length(shipname)==0){
				warning("Under the current version (version 1) 'shipname' must be specified alongside 'cruise'")
				cruiseMatrix <- NULL
			}
			else{
				cruiseMatrix <- getCruiseStrings(list(cbind(Cruise=cruise, ShipName=shipname)), datatype, StoX_data_types, ver)
				yearbase <- cruiseMatrix[,-firstcols2]
				yearbase <- yearbase[!is.na(yearbase)]
				if(length(yearbase)==0){
					warning("No data downloaded")
					return(cruiseMatrix)
				}
				cruiseMatrix <- cbind(year=NMDdecode(yearbase[1])$year, cruiseMatrix)
			}
		}
	}
	
	if(length(cruiseMatrix)==0){
		warning("No data found for the specific cruises. No data downloaded.")
		return(NULL)
	}
	##########
	
	##### 2. Group the cruises by year or cruise or one large group for all cruises: #####
	if(length(group)==0){
		cruiseMatrixSplit <- list(cruiseMatrix)
		names(cruiseMatrixSplit) <- paste(range(cruiseMatrix[,"year"]), collapse="-")
	}
	else{
		if(tolower(substr(group, 1, 1))=="d"){
			if(nrow(cruiseMatrix)==1){
				group = "c"
			}
			else{
				group = "y"
			}
		}
		if(tolower(substr(group, 1, 1))=="y"){
			splitvec <- paste0("year_", cruiseMatrix[,"year"])
		}
		else{
			splitvec <- paste0("cruiseNumber_", cruiseMatrix[,"Cruise"])
		}
		cruiseMatrixSplit <- lapply( split(cruiseMatrix, splitvec), matrix, ncol=ncol(cruiseMatrix), dimnames=list(NULL,colnames(cruiseMatrix)))
		# Discard cells with no data:
		cruiseMatrixSplit <- cruiseMatrixSplit[sapply(cruiseMatrixSplit, function(xx) any(!is.na(xx[, -firstcols])))]
		# Function for adding ship name(s) to the project name
		shipNames <- function(x){
			u <- unique(x[,"ShipName"])
			if(length(u)==1){
				paste0("_shipName_", u[1])
			}
			else if(length(u)==2){
				paste0("_shipNames_", paste(u, collapse="_"))
			}
			else{
				paste0("_shipNames_", paste(range(u), collapse="_till_"))
			}
		}
		shipNamesAdd <- sapply(cruiseMatrixSplit, shipNames)
		names(cruiseMatrixSplit) <- paste0(names(cruiseMatrixSplit), shipNamesAdd)
	}
	
	# Select all or some of the projects:
	nprojects = length(cruiseMatrixSplit)
	if(length(subset)==0){
		subset = seq_len(nprojects)
	}
	else{
		subset = subset[subset>=1 & subset<=nprojects]
	}
	cruiseMatrixSplit <- cruiseMatrixSplit[subset]
	if(msg){
		print(cruiseMatrixSplit)
	}
	if(return.URL){
		return(cruiseMatrixSplit)
	}
	
	##### 3. Create StoX projects: #####
	projectNames <- paste(filebase, names(cruiseMatrixSplit), sep="_")
	projectPaths <- lapply(projectNames, createProject, dir=dir, model=model, ow=ow, ...)
	#projectPaths <- lapply(projectPaths, getProjectDir)
	##########
	
	##### 4. Download to the projects: #####
	# Feed data into the projects:
	
	# Plot a time bar showing the progress of the reading and plotting:
	if(msg){
		infostring <- "Downloading files from NMD:"
		cat(infostring,"\n",sep="")
		#totalsteps <- length(cruiseMatrixSplit)
		totalsteps <- sum(sapply(cruiseMatrixSplit, nrow) * length(StoX_data_types))
		stepfact <- nchar(infostring)/totalsteps
		oldvalue <- 0
		index <- 0
	}
	for(i in seq_along(cruiseMatrixSplit)){
		xmlfiles <- matrix(NA, nrow=nrow(cruiseMatrixSplit[[i]]), ncol=length(StoX_data_types))
		for(j in seq_along(StoX_data_types)){
			for(k in seq_len(nrow(cruiseMatrixSplit[[i]]))){
				# Print a dot if the floor of the new value exceeds the old value:
				if(msg){
					index <- index + 1
					thisvalue = floor(index*stepfact)
					if(thisvalue > oldvalue){
						cat(rep(".",thisvalue-oldvalue),if(index == totalsteps) "\n", sep="")
						oldvalue = thisvalue
						}
					}
				# Get the current URL:
				URL <- cruiseMatrixSplit[[i]][k, StoX_data_types[j]]
				# Use the naming conver that NMD uses, which is 'datatype'_cruiseNumber_'cruiseNumber'_'ShipName'
				cruise_shipname <- paste(NMD_data_types[j], "cruiseNumber", cruiseMatrixSplit[[i]][k,"Cruise"], cruiseMatrixSplit[[i]][k,"ShipName"], sep="_")
				xmlfiles[k,j] <- file.path(projectPaths[i], "input", StoX_data_types[j], paste0(cruise_shipname, ".xml"))
				suppressWarnings(downloadXML(URL, msg=FALSE, list.out=FALSE, file=xmlfiles[k,j]))
			}
		cruiseMatrixSplit[[i]] <- cbind(cruiseMatrixSplit[[i]], xmlfiles)
		}
	}
	
	# Point to the downloaded files in each project:
	#lapply(projectNames, pointToStoXFiles) before 2016-11-04
	lapply(projectNames, updateProject)
	#getURIAsynchronous
	projectNames
}
#' 
#' @importFrom XML xmlParse xmlToList
#' @export
#' @rdname getNMDinfo
#' 
downloadXML <- function(URL, msg=FALSE, list.out=TRUE, file=NULL, quiet=TRUE){
	URL <- URLencode(URL)
	#cat(URL, "\n")
	failed <- FALSE
	if(msg){
		used <- proc.time()[3]
	}
	# Using rCurl there are recurring encoding problems, where the xml file is interpreted as some other than the UTF-8 encoding specified in the first line of the file (such as latin-1). Thus we test out downloading the file directly using download.file():
	# Download to the temporary file if 'file' is missing:
	if(length(file)==0){
		file <- tempfile()
	}
	tryCatch(download.file(URL, destfile=file, quiet=quiet), error=function(...) failed<<-TRUE)
	if(failed){
		warning(paste("Non-existent URL", URLdecode(URL)))
		return(NULL)
	}
	# Convert to a list and output if requested:
	if(list.out){
		if(msg){
			used <- proc.time()[3] - used
			cat("Time left (rough estimate at ", toString(Sys.time()), "): ", signif(8*used, 2), " seconds\n", sep="")
		}
		# Read the file:
		x <- readChar(file, file.info(file)$size)
		# Parse the file as XML:
		x <- tryCatch(xmlParse(x, asText=TRUE), error=function(...) failed<<-TRUE)
		if(failed){
			warning(paste("URL" ,URLdecode(URL) ,"does not contain valid XML data (error in xmlParse())"))
			return(NULL)
		}
		else{
			# Convert to list:
			x <- xmlToList(x)
			# New line added on 2016-08-12 after an issue with nordic characters being interpreted as latin1 by R on Windows. The problem is that xmlAttrs() has no parameter for encoding, and, in contrast with the rest of xmlToList(), fails to interpret the data as UTF-8. The solution is to convert all the data afterwards:
			rapply(x, function(xx) iconv(xx, "UTF-8", "UTF-8"), how="replace")
		}
		x
	}
	else{
		file
	}
}
#'
#' @export
#' @rdname getNMDinfo
#' 
searchNMDCruise <- function(cruisenrANDshipname, datatype, ver=1, API="http://tomcat7.imr.no:8080/apis/nmdapi"){
	searchURL <- paste(API, datatype, paste0("v", ver), paste0("find?cruisenr=", cruisenrANDshipname[1], "&shipname=", cruisenrANDshipname[2]), sep="/")
	out <- rep(NA, length(searchURL))
	for(i in seq_along(out)){
		temp <- suppressWarnings(downloadXML(URLencode(searchURL[i]), msg=FALSE)$element$text)
		if(length(temp)){
			out[i] <- temp
		}
	}
	out
}


#*********************************************
#*********************************************
#' Encodes and decodes NMD API strings.
#'
#' \code{getNMDinfo} converts, prints and optionally returns NMD reference information given a search string to the reference information. Used in StoX.URL(). \cr 
#' \code{getNMDdata} downloads data from specific cruises, cruise series ot survey time series from NMD. \cr 
#' \code{downloadXML} downloads xml data from an API, parses the xml data, and converts to a list (the latter is time consuming).
#'
#' @param URL	An URL.
#' 
#' @export
#' @rdname NMDencode
#' 
NMDdecode <- function(URL){
	URL <- URLdecode(URL)
	URL <- strsplit(URL, "/")[[1]]
	# Remove empty strings past the first (the http://):
	URL <- URL[!(URL=="" & duplicated(URL))]
	# Extract the API and data type:
	API <- paste(URL[1:5], collapse="/")
	datatype <- URL[6]
	ver <- URL[7]
	year=NA
	vessel=NA
	cruise=NA
	cs=NA
	sts=NA
	type=NA
	if(datatype %in% c("echosounder","biotic", "cruise")){
	missiontype <- URL[8]
	year <- URL[9]
	vessel <- URL[10]
	cruise <- URL[11]
	}
	else if(datatype=="cruiseseries"){
	if(length(URL)==8){
		cs <- URL[8]
	}
	}
	else if(datatype=="surveytimeseries"){
	if(length(URL)==8){
		sts <- URL[8]
	}
	}
	else if(datatype=="reference"){
	if(length(URL)==8){
		type <- URL[8]
	}
	}
	list(API=API, datatype=datatype, ver=ver, year=year, vessel=vessel, cruise=cruise, cs=cs, sts=sts, type=type)
}
#NMDencode <- function(URLbase=NULL, datatype="echosounder", ver=1, API="http://tomcat7.imr.no:8080/apis/nmdapi", missiontype="Forskningsfartøy", year="2015", vessel="G O Sars-LMEL", cruise="2015106", cs=NULL, sts=NULL, type=NULL){
#	if(length(URLbase)==0){
#	URLbase <- paste(API, datatype, paste0("v", ver), sep="/")
#	}
#	if(datatype %in% c("echosounder","biotic", "cruise")){
#	out <- paste(URLbase, missiontype, year, vessel, cruise, sep="/")
#	}
#	else if(datatype=="cruiseseries"){
#	out <- paste(URLbase, cs, "samples", year, sep="/")
#	}
#	else if(datatype=="surveytimeseries"){
#	out <- paste(URLbase, sts, "samples", year, sep="/")
#	}
#	else if(datatype=="reference"){
#	out <- paste(URLbase, type, sep="/")
#	}
#	URLencode(out)
#}
#############################################
########## After meeting with NMD: ##########
#############################################
#http://tomcat7.imr.no:8080/DatasetExplorer/v1/html/main.html (Dataset Explorer gir en oversikt over dataene som er i API’et og de toktseriene og survey tidsseriene som finnes der).

#http://tomcat7.imr.no:8080/apis/nmdapi/cruise/v1/ (API for tokt informasjon,				 http://tomcat7.imr.no:8080/apis/nmdapi/cruise/v1/Forskningsfart%C3%B8y/2015/G%20O%20Sars-LMEL/2015106 eksempel på et gitt tokt)
#http://tomcat7.imr.no:8080/apis/nmdapi/biotic/v1/ (API for biologisk informasjon (fiskedata) http://tomcat7.imr.no:8080/apis/nmdapi/biotic/v1/Forskningsfart%C3%B8y/2015/G%20O%20Sars-LMEL/2015106 eksempel på et gitt tokt)
#http://tomcat7.imr.no:8080/apis/nmdapi/echosounder/v1/ (API for akustiske data http://tomcat7.imr.no:8080/apis/nmdapi/echosounder/v1/Forskningsfart%C3%B8y/2015/G%20O%20Sars-LMEL/2015106 eksempel på et gitt tokt)
#http://tomcat7.imr.no:8080/apis/nmdapi/cruiseseries/v1 (toktserie som er definert, legg til verdien i en av <result> taggene for å få ut innholdet til en toktserie)
#http://tomcat7.imr.no:8080/apis/nmdapi/surveytimeseries/v1 (survey timeserie, brukes på samme måte som cruiseseries)
#http://tomcat7.imr.no:8080/apis/nmdapi/stox/v1 (stox prosjekt filer)
#http://tomcat7.imr.no:8080/apis/nmdapi/reference/v1 (referanse data)

#For å søke mot API’et etter et tokt kan du bruke find komandoen på denne måten:
#http://tomcat7.imr.no:8080/apis/nmdapi/echosounder/v1/find?cruisenr=2015106&shipname=G%20O%20Sars
