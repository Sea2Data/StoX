#*********************************************
#*********************************************
#' (Internal) Initialize rJava
#' 
#' This funcion initializes the connection to Java.
#' 
#' @import grDevices
#' @import graphics
#' @import stats
#' @import utils
#'
#' @importFrom rJava .jpackage
#' @export
#' 
.Rstox.init <- function() {
	# Package initialization of rJava. Note that the documentatino of this functions also contains importing of the four packages grDevices, graphics, stats and utils. This is a bit of cheating, but avoids using :: for such common functions.
	pkgname <- "Rstox";
	loc = dirname(path.package(pkgname))
	# rJava - load jar files in package java directory 
	.jpackage(pkgname, lib.loc=loc)
}


#*********************************************
#*********************************************
#' (Internal) Get available StoX functions
#' 
#' This funcion gets available StoX functions.
#' 
#' @importFrom rJava .jnew
#' @export
#' 
getAvailableFunctions <- function(){
	projectName <- .jnew("no/imr/stox/model/Project")
	functions <- projectName$getLibrary()$getMetaFunctions()$toString()
	JavaString2vector(functions)
}


#*********************************************
#*********************************************
#' (Internal) Convert a Java string to R vector
#' 
#' When reading some data from the StoX Java memory using toString(), the resulting string is converted to a string vector by this function.
#' 
#' @param x	A Java string imported from the StoX Java library.
#' 
#' @export
#' 
JavaString2vector <- function(x){
	x <- gsub("[", "", x, fixed=TRUE)
	x <- gsub("]", "", x, fixed=TRUE)
	strsplit(x, ", ")[[1]]
}


#*********************************************
#*********************************************
#' (Internal) Wrap basic R objects into Java objects
#' 
#' Wraps a value \code{i} into a Java integer, double, og Boolean for use in Java functions accessable by the package Rstox.
#' 
#' @param i	A single integer, double, or Boolean
#'
#' @return A Java integer, double, or Boolean
#'
#' @importFrom rJava .jnew
#' @export
#' @rdname .jInt
#' 
.jInt <- function(i) {
	.jnew("java/lang/Integer", as.integer(i))
}
#' 
#' @importFrom rJava .jnew
#' @export
#' @rdname .jInt
#' 
.jDouble <- function(i) {
	.jnew("java/lang/Double", as.double(i))
}
#' 
#' @importFrom rJava .jnew
#' @export
#' @rdname .jInt
#' 
.jBoolean <- function(i) {
	.jnew("java/lang/Boolean", i)
}


#*********************************************
#*********************************************
#' (Internal) Convert a storage object into a dataframe representation
#' 
#' \code{getDataFrame} converts a StoX storage object into a dataframe, and is used by the more user friendly \code{getDataFrame1} to convert a baseline object to dataframe. \cr \cr
#' \code{getProcessDataTableAsDataFrame} gets a joined table with meanNASC, psu, stratum, and area. Reads transect data, strata and area information from baseline Java object and merges them into one data frame.
#' 
#' @param baseline		A StoX baseline object
#' @param processName	The name of the process to extract data by, such as "ReadBioticXML"
#' @param functionName	The name of the function to extract data by, such as "ReadBioticXML"
#' @param tableName		Supported processdata tables: TRAWLASSIGNMENT, DISTANCEASSIGNMENT, PSUASSIGNMENT, DISTANCEPSU, PSUSTRATUM, STRATUMPOLYGON
#' @param storage		The StoX storage object
#' @param data			The StoX data
#' @param level			The level of tables for some datatypes like FishStation, which contains different levels, i.e., 1 for fishstation, 2 for sample and 3 for individuals. \code{getDataFrame1} selects the first level, and \code{getDataFrame} must be used to select a different level
#' @param drop			Logical: if TRUE (defalut) drop the list if only one data frame is requested.
#'
#' @return A dataframe
#'
#' @export
#' @rdname getDataFrame
#' 
getDataFrame <- function(baseline, processName=NULL, functionName=NULL, level=NULL, drop=TRUE){
	if(is.null(baseline)){
		warning("Empty baseline object")
		return(NULL)
	}
	# Get the process by process name or function name:
	if(length(processName)){
		pr <- baseline$findProcess(processName)
		if(is.null(pr)){
			warning(paste0("Process \"", processName, "\" not found"))
			return(NULL)
		} 
	}
	else if(length(functionName)){
		pr <- baseline$findProcessByFunction(functionName)
		if(is.null(pr)){
			warning(paste0("Function \"", functionName, "\" not found"))
			return(NULL)
		} 
	}
	else{
		warning("One of 'processName' or 'functionName' must be given")
		return(NULL)
	}
	
	# Get the data storage object:
	storage <- pr$getDataStorage()
	if(is.null(storage)){
		warning(paste0("Data from ", if(length(processName)) paste0("process \"", processName, "\"") else paste0("function \"", functionName, "\""), " not found"))
		return(NULL)
	}
	
	# Get the stored data:
	data <- pr$getOutput()
	if(is.null(data)){
		warning(paste0("Output from ", if(length(processName)) paste0("process \"", processName, "\"") else paste0("function \"", functionName, "\""), " not found"))
		return(NULL)
	}
	
	# Get the requested levels:
	if(length(level)==0){
		level <- seq_len(storage$getNumDataStorageFiles())
	}
	else{
		level <- level[level<=storage$getNumDataStorageFiles()]
	}
	
	# Output a list of the data of each requested level:
	out <- lapply(level, getDataFrameAtLevel, storage=storage, data=data)
	names(out) <- sapply(seq_along(out), function(xx) basename(storage$getStorageFileName(.jInt(xx))))
	if(drop && length(out)==1){
		out[[1]]
	}
	else{
		out
	}
}
#' 
#' @export
#' @rdname getDataFrame
#' 
getDataFrame1 <- function(baseline, processName=NULL, functionName=NULL, level=NULL, drop=TRUE){
	warning("getDataFrame1() is deprecated. Use getbaseline() instead (or the dependent funciton getDataFrame())")
	getDataFrame(baseline, processName=processName, functionName=functionName, level=level, drop=drop)
}
#' 
#' @export
#' @rdname getDataFrame
#' 
getDataFrameAtLevel <- function(level, storage, data) {
	if(is.null(storage) || is.null(data)){
		return(NULL)
	} 
	s <- storage$asTable(data, .jInt(level))
	#out <- read.csv(textConnection(s), sep='\t', stringsAsFactors=F)
	out <- read.csv(textConnection(s), sep='\t', stringsAsFactors=F, na.strings="-", encoding="UTF-8")
	# Interpret true/false as TRUE/FALSE (move along the columns of 'out'):
	for(i in seq_along(out)){
		if(length(out[[i]])>0 && head(out[[i]], 1) %in% c("true", "false")){
		 	out[[i]] <- as.logical(out[[i]])
		}
	}
	out
	#apply(out, 2, function(xx) if(head(xx, 1) %in% c("true", "false")) as.logical(xx) else xx)
}
#' 
#' @export
#' @rdname getDataFrame
#' 
getProcessDataTableAsDataFrame <- function(baseline, tableName) {
	s <- baseline$getProject()$getProcessData()$asTable(tableName)
	if(nchar(s)>0){
	out <- read.csv(textConnection(s), sep='\t', row.names=NULL, stringsAsFactors=F, na.strings="-", encoding="UTF-8")
		# Interpret true/false as TRUE/FALSE:
		for(i in seq_along(out)){
			if(length(out[[i]])>0 && head(out[[i]], 1) %in% c("true", "false")){
			 	out[[i]] <- as.logical(out[[i]])
			}
		}
		return(out)
	}
	else{
		warning(paste0("Table \"", tableName, "\" missing in the project.xml file"))
		return(NULL)
	}
}


#*********************************************
#*********************************************
#' (Internal) Set NASC data and assignments to memory
#' 
#' \code{setAssignments} Sets assignments to the assignment table in the process data (in memory).
#' \code{setMeanNASCValues} Sets NASC or meanNASC values to the baseline data (in memory).
#' 
#' @param projectName  	The name or full path of the project, a baseline object (as returned from getBaseline() or runBaseline()), og a project object (as returned from open).
#' @param assignments	The modified trawl assignments (StationWeight for each combination of AssignmentID and Station).
#' @param process		The process to set NASC values to.
#' @param data			The modified NASC data (Value for each combination of AcoCat, SampleUnit and Layer).
#'
#' @return An assignment
#'
#' @importFrom rJava J .jarray
#' @export
#' @rdname setAssignments
#'
setAssignments <- function(projectName, assignments){
	# Get the baseline object:
	baseline <- runBaseline(projectName=projectName, out="baseline", msg=FALSE)
	# Define the Java-object to modify:
	JavaPath <- baseline$getProject()$getProcessData()$getMatrix("bioticassignment")
	# The functions J and .jnew and other functions in the rJava library needs initialization:
	.Rstox.init()
	# Modify with the 'assignments':
	J("no.imr.stox.functions.utils.AbndEstProcessDataUtil")$setAssignments(JavaPath, .jarray(as.character(assignments$AssignmentID)), .jarray(as.character(assignments$Station)), .jarray(as.double(assignments$StationWeight)))
}
#'
#' @importFrom rJava J .jarray
#' @export
#' @rdname setAssignments
#'
setNASC <- function(projectName, process="MeanNASC", data){
	# Get the baseline object:
	baseline <- runBaseline(projectName=projectName, out="baseline", msg=FALSE)
	# Define the Java-object to modify:
	JavaPath <- baseline$findProcessByFunction(process)$getOutput()$getData()
	# The functions J and .jnew and other functions in the rJava library needs initialization:
	.Rstox.init()
	# Modify with the 'data':
	J("no.imr.stox.bo.MatrixUtil")$setGroupRowColValues(JavaPath, .jarray(as.character(data$AcoCat)), .jarray(as.character(data$SampleUnit)), .jarray(as.character(data$Layer)), .jarray(as.double(data$Value)))
}



#*********************************************
#*********************************************
#' (Internal) Set and remove assignments
#' 
#' \code{setAssignments_old} Sets assignments to the assignment table in the process data (in memory).
#' \code{setMeanNASCValues_old} Sets NASC or meanNASC values to the baseline data (in memory).
#' 
#' @param ta_table		Table 'TRAWLASSIGNMENT' in ProcessData
#' @param assignments	Modified trawl assignments
#' @param mtrx			The mean NASC matrix
#' @param tbl			The table dataframe
#'
#' @importFrom rJava J .jarray
#' @export
#' @rdname setAssignments_old
#'
setAssignments_old <- function(ta_table, assignments){
	# The functions J and .jnew and other functions in the rJava library needs initialization:
	.Rstox.init()
	J("no.imr.stox.functions.utils.AbndEstProcessDataUtil")$setAssignments(ta_table, .jarray(as.character(assignments$AssignmentID)), .jarray(as.character(assignments$Station)), .jarray(as.double(assignments$StationWeight)))
}
#'
#' @importFrom rJava J .jarray
#' @export
#' @rdname setAssignments_old
#'
setMeanNASCValues_old <- function(mtrx, tbl){
	# The functions J and .jnew and other functions in the rJava library needs initialization:
	.Rstox.init()
	J("no.imr.stox.bo.MatrixUtil")$setGroupRowColValues(mtrx, .jarray(as.character(tbl$AcoCat)), .jarray(as.character(tbl$SampleUnit)), .jarray(as.character(tbl$Layer)), .jarray(as.double(tbl$Value)))
}
