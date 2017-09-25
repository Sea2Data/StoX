#----------------------------------------------------------#
#---------- This document contains functions in -----------#
#------------ Rstox that have been archived as ------------#
#--------------- of 2016-09-07 classified as --------------#
#------------- unused or by some other reason -------------#
#------------ removed. Also functions that are ------------#
#---------- renamed are moved to this document: -----------#
#----------------------------------------------------------#


#----------------------------------------------------------#
#----------------------- 2016-09-07 -----------------------#
#----------------------------------------------------------#

#*********************************************
#*********************************************
#' (Internal) Get parameters object
#' 
#' Set parameters (project name, Number of bootstrap replicates, and seed = TRUE/FALSE) used in the R Model.
#' 
#' @param projectName The name of the StoX project.
#' @param numIterations Number of bootstrap replicates.
#' @param seed The seed for the random number generator (used for reproducibility).
#'
#' @return List with user set parameters
#'
#' @examples
#' # No longer used
#'
#' @export
#'
getParameters <- function(projectName, numIterations, seed){
  out <- list(projectName = projectName, numIterations = numIterations, seed = seed)
}


#----------------------------------------------------------#
#----------------------- 2016-09-07 -----------------------#
#----------------------------------------------------------#

#*********************************************
#*********************************************
#' (Internal) Read and process a StoX R process file
#'
#' This function calls each process in a StoX R process file, and translates parameters to actual arguments in a local environment. The old function is included for reference.
#'
#' @param rootFolder the root folder of the project
#' @param projectName the project name
#' @param modelName model to parse
#' @param fileName the file name of the R process file
#'
#' @importFrom rJava .jnew
#' @export
#' @rdname runRProcessFile
#' 
rstox.runRProcessFile <- function(rootFolder, projectName, modelName) {
   # J() and reference to "no/imr/stox/model/Project" requires  Rstox.init():
   Rstox.init()
   prj <- .jnew("no/imr/stox/model/Project", rootFolder, projectName, modelName)
   prj$openProject()
   m <- prj$getModel(modelName)
   for(p in as.list(m$getProcessList())) {
     processName <- p$getProcessName()
     fnc <- p$getMetaFunction()
     if(is.null(fnc)){
       next
     }
     functionName <- fnc$getName()
     functionCall <- paste0(functionName, "(")
     subSequentArguments <- FALSE
     for(pm in as.list(fnc$getMetaParameters())) {
       paramName <- pm$getName()
       paramValue <- p$getValue(pm)
       if(is.null(paramValue)){
         next
       }
       # Primitive actualization
       actualValue <- paramValue 
       if(grepl("Process\\(", paramValue)){
         processValue <- sub("^Process\\((.*)\\)$", "\\1", paramValue)
         # Reference actualization
         actualValue <- paste0(processValue, ".out") #get(envir=globalenv(), paste0(processValue, ".out"))
       }
       else{
         actualValue <- paramValue 
         if(is.na(suppressWarnings(as.numeric(actualValue)))){
           # Delimit character constants by quoting:
           actualValue <- paste0("\"", actualValue, "\"");
         }
         else if(!is.na(suppressWarnings(as.logical(actualValue)))){
           actualValue = toupper(actualValue) #convert true to TRUE and false to FALSE
         }
       }
       if(subSequentArguments) {
         # Separate arguments with comma
         functionCall <- paste0(functionCall, ", ");
       }
       functionCall <- paste0(functionCall, paramName);
       functionCall <- paste0(functionCall, "=");
       functionCall <- paste0(functionCall, actualValue);
       subSequentArguments <- TRUE
     }
     # Store parameter dynamically in environment after function call
     functionCall=paste0(functionCall, ")")
     resultName <- paste0(processName, ".out")
     #print(paste0(paste0(processName, ".out"), " <- ", functionCall))
     # Write a hint to file - to be listened on from external system for process progress status'
     #write(processName, file=statusFileName, append=FALSE)     
     # evaluate function call in global environment
     envCall(env=globalenv(), envVar=resultName, functionCall)
     subSequentArguments <- FALSE
   }   
}  


#----------------------------------------------------------#
#----------------------- 2016-09-12 -----------------------#
#----------------------------------------------------------#

#*********************************************
#*********************************************
#' (Internal) Distribute unknown individual biological parameters from known values
#'
#' This function fills in holes in individual fish samples (also called imputation).
#' In cases where individuals are not aged, missing biological variables (e.g "weight","age","sex", and "specialstage") are sampled from 
#' fish in the same length group at the lowest imputation level possible.
#'    impLevel = 0: no imputation, biological information exists
#'    impLevel = 1: imputation at station level; biological information is selected at random from fish within station
#'    impLevel = 2: imputation at strata level; no information available at station level, random selection within stratum
#'    impLevel = 3: imputation at survey level; no information available at lower levels, random selection within suvey
#'    impLevel = 99: no imputation, no biological information exists for this length group
#'
#' @param abnd Abundance matrix with individual data
#' @param seedV The seed vector for the random number generator, where element 'i' is picked out (this may seem strange, but is a consequence of the parallelability of the function, where 'i' is the primary parameter).
#'
#' @return Abundance matrix with imputed biological information 
#'
#' @export
#' 
distributeAbundance <- function(i=NULL, abnd, seedV=NULL) {
  if(length(i)==1){
    abnd = abnd[[i]]
  }
  
  msg <- double(6)
  abnd.distr.known <- abnd[abnd$age != "-" & abnd$includeintotal=="true",]
  #cat(paste0("Data have ", length(abnd.distr.known$age)," aged individuals.\n"))
  msg[1] <- length(abnd.distr.known$age)
  abnd.distr.unknown <- abnd[abnd$age == "-", ]
  #cat(paste0("Data have ", length(abnd.distr.unknown$age)," length sampled individuals without known age.\n"))
  msg[2] <- length(abnd.distr.unknown$age)
  
  if(length(abnd.distr.known$age) == 0){
    stop("No known ages")
  }
  abnd.distr.known$impLevel <- 0
  if(length(abnd.distr.unknown$age) == 0){
    warning("No unknown ages")
    return(abnd)
  }
  abnd.distr.unknown$impLevel <- 99
  ## Go through all rows in abundance matrix
  if(isTRUE(seedV[i])){
  	seedM <- matrix(c(1231234, 1234, 1234), ncol=nrow(abnd.distr.unknown), ncol=3, byrow=TRUE)
  }
  else{
    set.seed(seedV[i])
    # Create a seed matrix with 3 columns representing the replacement by station, stratum and survey:
    seedM <- matrix(sample(seq_len(10000000), 3*nrow(abnd.distr.unknown), replace = FALSE), ncol=3)
  }
  #set.seed(if(isTRUE(seed)) 1234 else if(is.numeric(seed)) seed else NULL) # seed==TRUE giving 1234 for compatibility with older versions
  #set.seed(seedV[i])
  #seedV <- sample(c(1:10000000), nrow(abnd.distr.unknown), replace = FALSE) # Makes seed vector for fixed seeds (for reproducibility).
  # Create a seed matrix with 3 columns representing the replacement by station, stratum and survey:
  #seedM <- matrix(sample(seq_len(10000000), 3*nrow(abnd.distr.unknown), replace = FALSE), ncol=3) # Makes seed matrix.
  for(i in 1: nrow(abnd.distr.unknown)){
	  id.known.sta <- id.known.stratum <- id.known.survey <- NULL

    ## Old comment, before changing station to serialno: Replace by station
    ## Replace by serialno
      id.known.sta <- abnd.distr.unknown$Stratum[i] == abnd.distr.known$Stratum &
      abnd.distr.unknown$cruise[i] == abnd.distr.known$cruise &
      #abnd.distr.unknown$serialno[i] == abnd.distr.known$serialno &
      # The variable station was changed to serialno (2016-08-31):
	    abnd.distr.unknown$serialno[i] == abnd.distr.known$serialno &
      abnd.distr.unknown$LenGrp[i] == abnd.distr.known$LenGrp
		if(any(id.known.sta)){
      known.data <- abnd.distr.known[id.known.sta,]
			set.seed(seedM[i,1]) # For reproducibility
      id.select.sta <- sample(1:length(known.data$age), size=1, replace=T)
      crep <- colnames(abnd.distr.unknown)[abnd.distr.unknown[i,] %in% c("-")]
      #if(any(is.na(crep))){
      #}
      abnd.distr.unknown[i,crep] <- known.data[id.select.sta,crep]
      abnd.distr.unknown$impLevel[i] <- 1
      next
    }
    ## Replace by stratum
    if(!any(id.known.sta)){
      id.known.stratum <- abnd.distr.unknown$Stratum[i] == abnd.distr.known$Stratum &
        abnd.distr.unknown$LenGrp[i] == abnd.distr.known$LenGrp
      if(any(id.known.stratum)){
        known.data.stratum <- abnd.distr.known[id.known.stratum,]
        set.seed(seedM[i,2]) # For reproducibility
        id.select.stratum <- sample(1:length(known.data.stratum$age), size=1, replace=T)
        crep <- colnames(abnd.distr.unknown)[abnd.distr.unknown[i,] %in% c("-")]
        abnd.distr.unknown[i,crep] <- known.data.stratum[id.select.stratum,crep]
        abnd.distr.unknown$impLevel[i] <- 2
        next
      }
    }
    ## Replace by survey
    if(!any(id.known.stratum)) {
      id.known.survey <- abnd.distr.unknown$LenGrp[i] == abnd.distr.known$LenGrp
      if(any(id.known.survey)){
        known.data.survey <- abnd.distr.known[id.known.survey,]
        set.seed(seedM[i,3]) # For reproducibility
        id.select.survey <- sample(1:length(known.data.survey$age), size=1, replace=T)
        crep <- colnames(abnd.distr.unknown)[abnd.distr.unknown[i,] %in% c("-")]
        abnd.distr.unknown[i,crep] <- known.data.survey[id.select.survey,crep]
        abnd.distr.unknown$impLevel[i] <- 3
        next
      }
    }
  }
  #cat(paste0(length(abnd.distr.unknown$impLevel[abnd.distr.unknown$impLevel==1])," individual ages were imputed at station level. \n"))
  #cat(paste0(length(abnd.distr.unknown$impLevel[abnd.distr.unknown$impLevel==2])," individual ages were imputed at strata level. \n"))
  #cat(paste0(length(abnd.distr.unknown$impLevel[abnd.distr.unknown$impLevel==3])," individual ages were imputed at survey level. \n"))
  #cat(paste0(length(abnd.distr.unknown$impLevel[abnd.distr.unknown$impLevel==99])," individual ages were not possible to impute. \n"))
  #msg[3] <- length(abnd.distr.unknown$impLevel[abnd.distr.unknown$impLevel==1])
  #msg[4] <- length(abnd.distr.unknown$impLevel[abnd.distr.unknown$impLevel==2])
  #msg[5] <- length(abnd.distr.unknown$impLevel[abnd.distr.unknown$impLevel==3])
  #msg[6] <- length(abnd.distr.unknown$impLevel[abnd.distr.unknown$impLevel==99])
  msg[3] <- sum(abnd.distr.unknown$impLevel==1)
  msg[4] <- sum(abnd.distr.unknown$impLevel==2)
  msg[5] <- sum(abnd.distr.unknown$impLevel==3)
  msg[6] <- sum(abnd.distr.unknown$impLevel==99)
  
  out <- rbind(abnd.distr.known,abnd.distr.unknown)
  out <- out[order(out$Row),]
  list(data=out, msg=msg)
}


#----------------------------------------------------------#
#----------------------- 2016-09-12 -----------------------#
#----------------------------------------------------------#

#*********************************************
#*********************************************
#' (Internal) Convert a storage object into a dataframe representation
#' 
#' \code{getDataFrame} converts a StoX storage object into a dataframe, and is used by the more user friendly \code{getDataFrame1} to convert a baseline object to dataframe. \cr \cr
#' \code{getProcessDataTableAsDataFrame} gets a joined table with meanNASC, psu, stratum, and area. Reads transect data, strata and area information from baseline Java object and merges them into one data frame.
#' 
#' @param baseline a StoX baseline object
#' @param functionName the function to extract data by, such as "AbundanceByLength"
#' @param tableName Supported processdata tables: TRAWLASSIGNMENT, DISTANCEASSIGNMENT, PSUASSIGNMENT, DISTANCEPSU, PSUSTRATUM, STRATUMPOLYGON
#' @param storage the StoX storage object
#' @param data the StoX data
#' @param level the level of tables for some datatypes like FishStation, which contains different levels, i.e., 1 for fishstation, 2 for sample and 3 for individuals. \code{getDataFrame1} selects the first level, and \code{getDataFrame} must be used to select a different level
#'
#' @return A dataframe
#'
#' @export
#' @rdname getDataFrame
#' 
getDataFrame1 <- function(baseline, functionName) {
  if(is.null(baseline)){
    return(NULL)
  }
  pr <- baseline$findProcessByFunction(functionName)
  if(is.null(pr)){
    return(NULL)
  } 
  st <- pr$getDataStorage()
  if(is.null(st)){
    return(NULL)
  }
  getDataFrame(st, pr$getOutput())
}
#' 
#' @export
#' @rdname getDataFrame
#' 
getDataFrame <- function(storage, data, level=1) {
  if(is.null(storage) || is.null(data)){
    return(NULL)
  } 
  s <- storage$asTable(data, jInt(level))
  read.csv(textConnection(s), sep='\t', stringsAsFactors=F)
}


#----------------------------------------------------------#
#----------------------- 2016-09-12 -----------------------#
#----------------------------------------------------------#

#*********************************************
#*********************************************
#' Get joined table of trawl assignments, psu and stratum
#'
#' Get trawl assignments from baseline in StoX Java memory.
#'
#' @param baseline StoX Java baseline object.
#'
#' @return Dataframe with  trawl assignments merged with psu and stratum
#'
#' @examples
#' \dontrun{
#' baseline <- getBaseline("Test_Rstox")  
#' assignments <- getBioticAssignments(baseline)}
#'
#' @export
#' 
getBioticAssignments <- function(baseline) {
  ta <- getProcessDataTableAsDataFrame(baseline, 'bioticassignment')
  pa <- getProcessDataTableAsDataFrame(baseline, 'suassignment')
  ps <- getProcessDataTableAsDataFrame(baseline, 'psustratum')
  out <- merge(x=merge(x=ps, y=pa, by.x='PSU', by.y='SampleUnit'), y=ta, by='AssignmentID')
}


#----------------------------------------------------------#
#----------------------- 2016-09-12 -----------------------#
#----------------------------------------------------------#

#*********************************************
#*********************************************
#' Run (or get an already run) a baseline model created by StoX
#' 
#' \code{runBaseline} runs a baseline model created by StoX, and includes possibilities to specify start and end process and to override parameters in the baseline model. It is also possible to return a list of the existing processes with parameter values.
#' \code{getBaseline} returns the name a reference to a baseline model that may have been run in JAVA memory already.
#' 
#' @param projectName the name of an existing StoX project.
#' @param startProcess the name or number of the start process in the list of processes in the model (use info=TRUE to return a list of the processes). The use of startProcess and endProcess requres that either no processes in the given range of processes depends on processes outside of the range, or that a baseline object is given in the input.
#' @param endProcess the name or number of the end process in the list of processes in the model (use info=TRUE to return a list of the processes).
#' @param info logical; if TRUE, a list of the existing processes with parameter values is returned.
#' @param rewrite logical; if TRUE the existing file named "project.xml", which conatins all specifications of the project (including parameter values and strata definitions), will be overwitten by with the possible new settings (see description of "...").
#' @param baseline a StoX baseline object returned from runBaseline() or getBaseline().
#' @param ... parameter values overriding existing parameter values. These are specified as processName = list(parameter = value), for example AcousticDensity = list(a = -70, m = 10), BioStationWeighting = list(WeightingMethod = "NASC", a = -70, m = 10). Numeric parameters must be given as numeric, string parameters as string, and logical parameters (given as strings "true"/"false" in StoX) can be given as logical TRUE/FALSE.
#' @param prNames the names of the processes from which to return data. If set to NULL, all processes are requested.
#' @param prData the names of the processes data variables (information stored in the process.xml file) from which to return data. If set to NULL, all processes data are requested.
#' @param check.names logical; if TRUE (default), check whether the process names 'prNames' or process data variable names 'prData' exist (time consuming).
#'
#' @return A reference to the StoX Java baseline object
#'
#' @examples
#' \dontrun{
#' system.time(baseline <- runBaseline("Test_Rstox"))
#' system.time(baselineInfo <- runBaseline("Test_Rstox", info=TRUE))
#' baselineInfo
#' system.time(baseline2 <- runBaseline("Test_Rstox", AcousticDensity = list(a = -70, m = 10), BioStationWeighting = list(WeightingMethod = "NASC", Radius=100, a = -70, m = 10)))
#' # When a baseline model has alreaddy been run:
#' system.time(baseline3 <- runBaseline("Test_Rstox"))}
#'
#' @export
#' @rdname runBaseline
#'
runBaseline <- function(projectName, startProcess=1, endProcess=Inf, info=FALSE, rewrite=FALSE, baseline=NULL, ...){
  # rewrite==NULL impies to run all processes, but rewrite==TRUE is intended to assure that the model is written to the project.xml file. However the need for this is not verified, and the time it takes for checking which processes are present prevents removal of writing processes so that, currently, rewrite==NULL and rewrite==TRUE have identical effects:
  if(length(rewrite)==0){
    rewrite=TRUE
  }
  l = list(...)
  # Allow for baseline as input for campatibility with older versions:
  if(!missing(projectName) && any(is(projectName)=="jobjRef")){
    baseline <- projectName
  }
  
  if(length(baseline)==0){
    # Open the project
    project <- openProject(projectName)
    baseline <- project$getBaseline()
    baseline$setBreakable(jBoolean(FALSE))
    baseline$setExportCSV(jBoolean(FALSE))
  }
  # Get process names. This is done if at least one of the following occurs:
  # (1) info is given as TRUE, impying that the user requests a list of the processes with parameter values
  # (2) The used has specified parameter values to override existing parameter values in the baseline model. Note that this currently demands a lot of processing time because rJava uses reflection when interfacing with Java
  # (3) The process that writes the model to the process.xml file whould be omitted, which is set as defaut:
  if(info || length(l)>0 || !rewrite){
    numProcesses = baseline$getProcessList()$size()
    ##processName = baseline$getProcessList()$toString()
    ##processName = strsplit(substr(processName, 2, nchar(processName)-1), ", ")
    processName = sapply(seq(0,numProcesses-1), function(i) baseline$getProcessList()$get(as.integer(i))$getName())
    getProcessFunction <- function(i, baseline){
      temp <- baseline$getProcessList()$get(as.integer(i))$getMetaFunction()
      if(length(names(temp))){
        temp$getName()
      }
      else{
        NA
      }
    }
  # processFunction = sapply(seq(0,numProcesses-1), function(i) baseline$getProcessList()$get(as.integer(i))$getMetaFunction()$getName())
  processFunction = sapply(seq(0,numProcesses-1), getProcessFunction, baseline=baseline)
  }
  # Return a list of processes with paramers:
  if(info){
    #processes = paste("Process nr. ", seq_len(numProcesses), ": ", processName, " (fun: ", processFunction, ")", sep="")
    parameters <- lapply(seq(0,numProcesses-1), getParametersOfProcess, baseline=baseline)
    # Add process number, function name and parameters in a list:
    #parameters <- lapply(seq_along(parameters), function(i) rbind(parameters[[i]], c("functionName", processFunction[i])))
    parameters <- lapply(seq_along(parameters), function(i) c(parameters[[i]], functionName=processFunction[i]))
    #names(parameters) = processes
    names(parameters) = processName
    return(parameters)
  }
  
  # Set start and end for the baseline run:
  if(!is.numeric(startProcess)){
    startProcess <- baseline$getProcessList()$indexOf(baseline$findProcessByFunction(startProcess)) + 1
  }
  if(!is.numeric(endProcess)){
    endProcess <- baseline$getProcessList()$indexOf(baseline$findProcessByFunction(endProcess)) + 1
  }
  startProcess = max(startProcess, 1)
  endProcess = min(endProcess, baseline$getProcessList()$size())
  
  # Test if the baseline has already been run. THIS DID NOT WORK. openProject RESETS THE getRunningProcessIdx():
  if(baseline$getRunningProcessIdx()>=endProcess && length(l)==0){
    return(baseline)
  }
  
  # Remove processes that saves the project.xml file: 
 if(!rewrite){
    saveProcesses = which(processFunction %in% c("SaveProject", "WriteProcessData"))
    if(length(saveProcesses)>0){
    # Remove save processes:
    for(i in rev(saveProcesses)){
      baseline$getProcessList()$remove(as.integer(i-1))
    }
    # Update the end process index number:
    endProcess = endProcess-length(saveProcesses)
    }
  }
 
  # Override parameters in the baseline:
  if(length(l)>0){
    changeProcesses <- names(l)
    numParameters <- unlist(lapply(l, length))
    changeProcesses <- rep(changeProcesses, numParameters)
    changeProcessesIdx <- match(changeProcesses, processName)-1
    changeParameters <- unlist(lapply(l, names))
    changeValues <- unlist(l, recursive=FALSE)
	# Set numeric values to jDouble:
	#numericValues <- !is.na(sapply(changeValues, as.numeric))
    numericValues <- sapply(changeValues, is.numeric)
	changeValues[numericValues] <- lapply(changeValues[numericValues], jDouble)
	# Set logical values to "true"/"false":
    logicalValues <- sapply(changeValues, is.logical)
	changeValues[logicalValues] <- lapply(changeValues[logicalValues], function(xx) if(xx) "true" else "false")
    for(i in seq_along(changeProcessesIdx)){
      baseline$getProcessList()$get(as.integer(changeProcessesIdx[i]))$setParameterValue(changeParameters[i], changeValues[[i]])
    }
  }

  # Run the baseline:
  baseline$run(jInt(startProcess), jInt(endProcess), jBoolean(FALSE))
  # Output the baseline model:
  baseline
}
#'
#' @export
#' @rdname runBaseline
#' 
getBaseline <- function(projectName) {
  project <- openProject(projectName)
  project$setRFolder(R.home("bin"))
  baseline <- project$getBaseline()
  baseline$setBreakable(jBoolean(FALSE))
  baseline$setExportCSV(jBoolean(FALSE))
  baseline
}
#'
#' @export
#' @rdname runBaseline
#' 
getBaselineOutput <- function(projectName, prNames="", prData="", check.names=TRUE) {
  if(is.character(projectName)){
    baseline <- runBaseline(projectName)
  }
  else{
    baseline <- projectName
  }
  # get process list:
  if(check.names){
    # Get names of the processes:
    processNames <- names(runBaseline(baseline, info=TRUE))
    if(length(prNames)>0){
      prNames <- intersect(processNames, prNames)
    }
    else{
      prNames <- processNames
    }
  }
  # Read psocess.xml file and process data names:
  if(check.names){
    # Get names of the process data:
    processdataNames <- baseline$getProject()$getProcessData()$getOutputOrder()$toArray()
    if(length(prData)>0){
      prData <- intersect(processdataNames, prData)
    }
    else{
      prData <- processdataNames
    }
  }
  out <- c(lapply(prNames, function(xx) getDataFrame1(baseline, xx)), lapply(prData, function(xx) getProcessDataTableAsDataFrame(baseline, xx)))
  names(out) <- c(prNames, prData)
  out
}


#----------------------------------------------------------#
#----------------------- 2016-09-12 -----------------------#
#----------------------------------------------------------#

#*********************************************
#*********************************************
#' (Internal) Retrieving parameters and default values of a StoX-process
#' 
#' This function retrievs the parameters and default values of a StoX-process. Used intenally in runBaseline()
#' 
#' @param processNr the index number of the process in the model.
#' @param baseline a StoX baseline object returned from runBaseline().
#'
#' @return A column matrix of parameter names and values
#'
#' @examples
#' # See runBaseline
#'
#' @export
#' @rdname getParametersOfProcess
#'
# 
getParametersOfProcess <- function(processNr, baseline){
  processNr = as.integer(processNr)
  # Number of parameters:
  #L <- baseline$getProcessList()$get(as.integer(processNr))$getMetaFunction()$getMetaParameters()$size()
  #if(L==0){
  #  return()
  #}
  temp <- baseline$getProcessList()$get(as.integer(processNr))$getMetaFunction()
  if(length(temp)==0){
    return()
  }
  else{
    L <- temp$getMetaParameters()$size()
    if(L==0){
      return()
    }
  }
  parameterNames <- unlist(lapply(seq(0,L-1), function(j) baseline$getProcessList()$get(as.integer(processNr))$getMetaFunction()$getMetaParameters()$get(as.integer(j))$getName()))
  parameterValues <- lapply(seq(1,L), function(j) baseline$getProcessList()$get(as.integer(processNr))$getParameterValue(parameterNames[j]))
  empty <- sapply(parameterValues, length)==0
  if(sum(empty)){
    parameterValues[empty] <- rep(list(NA),sum(empty))
  }
  names(parameterValues) <- parameterNames
  parameterValues
  #cbind(parameterName=parameterNames, value=unlist(parameterValues))
}


#----------------------------------------------------------#
#----------------------- 2016-09-13 -----------------------#
#----------------------------------------------------------#

# Replaced by the function getBaselineParameters():

#*********************************************
#*********************************************
#' (Internal) Retrieving parameters and default values of a StoX-process
#' 
#' This function retrievs the parameters and default values of a StoX-process. Used intenally in runBaseline()
#' 
#' @param processNr the index number of the process in the model.
#' @param baseline a StoX baseline object returned from runBaseline().
#'
#' @return A column matrix of parameter names and values
#'
#' @examples
#' # See runBaseline
#'
#' @export
#' @rdname getParametersOfProcess
#'
# 
getParametersOfProcess <- function(process, baseline){
	if(is.character(process)){
		thisProcess <- baseline$getProcessFromName(process)
	}
  else{
  	thisProcess <- baseline$getProcessList()$get(as.integer(process))
  }
  
	# Number of parameters:
	L <- thisProcess$getMetaFunction()
  if(length(L)==0){
    return()
  }
  else{
    L <- L$getMetaParameters()$size()
    if(L==0){
      return()
    }
  }
  
	parameterNames <- unlist( lapply( seq(0,L-1), function(j) thisProcess$getMetaFunction()$getMetaParameters()$get(as.integer(j))$getName() ) )
  parameterValues <- lapply( seq(1,L), function(j) thisProcess$getParameterValue(parameterNames[j]) )
  empty <- sapply(parameterValues, length)==0
  if(sum(empty)){
    parameterValues[empty] <- rep(list(NA),sum(empty))
  }
  names(parameterValues) <- parameterNames
  parameterValues
  #cbind(parameterName=parameterNames, value=unlist(parameterValues))
}
#' 
#' @export
#' @rdname getParametersOfProcess
#' 
getProcesses <- function(baseline){
	# Function used to extracting the function of a process:
	getProcessFunction <- function(i, baseline){
	  temp <- baseline$getProcessList()$get(as.integer(i))$getMetaFunction()
	  if(length(names(temp))){
	    temp$getName()
	  }
	  else{
	    NA
	  }
	}
	# Get the number of processes, the process names and functions:
	numProcesses <- baseline$getProcessList()$size()
	processName <- sapply(seq(0,numProcesses-1), function(i) baseline$getProcessList()$get(as.integer(i))$getName())
	processFunction <- sapply(seq(0,numProcesses-1), getProcessFunction, baseline=baseline)
	data.frame(processName=processName, processFunction=processFunction)
}



#----------------------------------------------------------#
#----------------------- 2016-09-13 -----------------------#
#----------------------------------------------------------#

# Changed to the new parameters input (replacing par and proc), fun and proc, where fun and proc represents the function and process names for which output is requested:

getBaseline <- function(projectName, par=NULL, data=NULL, proc=NULL, drop=TRUE){
  # Locate the baseline object:
	if(is.character(projectName)){
		projectName_baseline <- paste0(projectName, "_baseline")
	  if(projectName_baseline %in% ls(get(getProjectEnvName()))){
	    baseline <- get(projectName_baseline, envir=get(getProjectEnvName()))
	  }
		else{
      baseline <- runBaseline(projectName)
		}
  }
  else{
    baseline <- projectName
  }

	######################################################
	##### (1) Get a list of processes with paramers: #####
	processes <- getBaselineParameters(baseline, discardSave=TRUE)
	# par = FALSE suppresses returning parameters of the baseline:
	if(!identical(par, FALSE)){
    # par = TRUE or NULL returns the parameters of all processes:
		if(length(par)==0 || isTRUE(par)){
			par.out <- processes
		}
		# If par is numeric, discard too low and too high indices:
		else if(is.numeric(par)){
			par.out <- processes[par[par>=0 & par<length(processes)]]
		}
		# If not numeric and if check.names = TRUE, intersect with the available process names:
		else{
			par.out <- processes[intersect(par, names(processes))]
		}
	}
	else{
		par.out = NULL
	}
	######################################################
	
	###########################################
	##### (2) Get names of the functions used by the processes (not the process names, since these can be defined arbitrary by the user in StoX and in Rstox): #####
	functions <- sapply(processes, "[[", functionName)
	if(length(data)){
    data <- intersect(names(processes), data)
  }
  else if(isTRUE(data) || length(data)==0){
    data <- names(processes)
  }
	data.out <- lapply(data, function(xx) getDataFrame(baseline, xx))
	names(data.out) <- data
 	###########################################
	
	###########################################
	##### (3) Get a list of process data: #####
	# Read process.xml file and process data names:
  processdataNames <- baseline$getProject()$getProcessData()$getOutputOrder()$toArray()
  if(length(proc)>0){
    proc <- intersect(processdataNames, proc)
  }
  else if(isTRUE(proc) || length(proc)==0){
    proc <- processdataNames
  }
	proc.out <- lapply(proc, function(xx) getProcessDataTableAsDataFrame(baseline, xx))
	names(proc.out) <- proc
 ###########################################
	
	# Return the data:
	out <- list(par=par.out, data=data.out, proc=proc.out)
	if(drop){
		out <- out[sapply(out, length)>0]
		while(is.list(out) && length(out)==1){
		  out <- out[[1]]
		}
	}
	out
}


#' 
#' @export
#' @rdname saveRImage
#' 
saveRList <- function(Rlist, fileName){
  save(list=Rlist, file = paste0("./output/rmodel/", fileName, ".rda"))
  fileName
}


#' 
#' @export
#' @rdname loadEnv
#' 
envCall <- function(env, envVar, functionCall) {
  result <- eval(parse(text=functionCall), envir=env)
  if(!is.null(result)) {
    assign(envVar, result, envir=env)
  }
  NULL
}


#----------------------------------------------------------#
#----------------------- 2016-09-14 -----------------------#
#----------------------------------------------------------#

# This function was modified to accept an environment, a project name of the file name, so that a single line can be run at the beginning of all report and plotting functions:

loadEnv<-function(fileName, fileBaseName="rmodel.RData", outputFolder="rmodel"){
  # If 'fileName' is not the path to a file, assume that the project name is specified:
  if(!identical(file.info(fileName)$isdir, FALSE)){
    fileName <- getRDataFileName(fileName, fileBaseName=fileBaseName, outputFolder=outputFolder)
  }
  # Try sourcing first, and use load() if source() fails:
  res <- new.env()
  ok <- try(source(file=fileName, local=res), silent=TRUE)
  if(length(ok)==0 || !is.numeric(ok)){
    load(file=fileName, env=res)
  }
  #load(file = fileName, env=res)
  res
}

# Not used:

removeAssignment <- function(asgTable, assignment, station){
   asgRow <- asgTable$getRowValue(paste(assignment))
   if(is.null(asgRow)){
    return(NULL)
  }
  asgRow$removeValue(station)
}


#----------------------------------------------------------#
#----------------------- 2016-09-14 -----------------------#
#----------------------------------------------------------#

# Before applying getVar() for introducing rigor with respect to changes in variable names in the StoX Java library:

#*********************************************
#*********************************************
#' (Internal) Distribute unknown individual biological parameters from known values
#'
#' This function fills in holes in individual fish samples (also called imputation).
#' In cases where individuals are not aged, missing biological variables (e.g "weight","age","sex", and "specialstage") are sampled from 
#' fish in the same length group at the lowest imputation level possible.
#'    impLevel = 0: no imputation, biological information exists
#'    impLevel = 1: imputation at station level; biological information is selected at random from fish within station
#'    impLevel = 2: imputation at strata level; no information available at station level, random selection within stratum
#'    impLevel = 3: imputation at survey level; no information available at lower levels, random selection within suvey
#'    impLevel = 99: no imputation, no biological information exists for this length group
#'
#' @param abnd Abundance matrix with individual data
#' @param seedV The seed vector for the random number generator, where element 'i' is picked out (this may seem strange, but is a consequence of the parallelability of the function, where 'i' is the primary parameter).
#'
#' @return Abundance matrix with imputed biological information 
#'
#' @export
#' 
distributeAbundance <- function(i=NULL, abnd, seedV=NULL) {
  if(length(i)==1 && !"Row" %in% names(abnd)){
    abnd = abnd[[i]]
  }
	N <- nrow(abnd)
	
	# Get the indices of known (with includeintotal==TRUE) and unknown ages:
	atKnownAge <- which(abnd$age != "-" & abnd$includeintotal=="true")
	atUnknownAge <- which(abnd$age == "-")
	NatKnownAge <- length(atKnownAge)
	NatUnknownAge <- length(atUnknownAge)
	
 # Stop if no known ages and return if no unknown:
	if(NatKnownAge == 0){
    stop("No known ages")
  }
  if(NatUnknownAge == 0){
    warning("No unknown ages")
	  abnd$impLevel <- 0
    return(abnd)
  }

  # Set the seed matrix:
  if(isTRUE(seedV[i])){
  	seedM <- matrix(c(1231234, 1234, 1234), ncol=NatUnknownAge, ncol=3, byrow=TRUE)
  }
  else{
    set.seed(seedV[i])
    # Create a seed matrix with 3 columns representing the replacement by station, stratum and survey:
    seedM <- matrix(sample(seq_len(10000000), 3*NatUnknownAge, replace = FALSE), ncol=3)
  }
	
	# Run through the unknown rows and get indices for rows at which the missing data should be extracetd:
	imputeRows <- rep("-", N)
  imputeLevels <- integer(N)
	for(atUnkn in seq_along(atUnknownAge)){
		indUnkn <- atUnknownAge[atUnkn]
		# Get indice for which of the rows with known ages that have the same station, stratum and survey as the current unknown individual:
		matchStratum <- abnd$Stratum[indUnkn] == abnd$Stratum[atKnownAge]
		matchcruise <- abnd$cruise[indUnkn] == abnd$cruise[atKnownAge]
		matchserialno <- abnd$serialno[indUnkn] == abnd$serialno[atKnownAge]
		matchLenGrp <- abnd$LenGrp[indUnkn] == abnd$LenGrp[atKnownAge]
		id.known.sta <- atKnownAge[ which(matchStratum & matchcruise & matchserialno & matchLenGrp) ]
		id.known.stratum <- atKnownAge[ which(matchStratum & matchLenGrp) ]
		id.known.survey <- atKnownAge[ which(matchLenGrp) ]
		Nid.known.stratum <- length(id.known.stratum)
		Nid.known.sta <- length(id.known.sta)
		Nid.known.survey <- length(id.known.survey)
												
    ## Replace by station:
   	if(any(id.known.sta)){
      set.seed(seedM[atUnkn,1])
			imputeRows[indUnkn] <- id.known.sta[.Internal(sample(Nid.known.sta, 1L, FALSE, NULL))]
      imputeLevels[indUnkn] <- 1L
    }
    ## Replace by stratum:
    else if(any(id.known.stratum)){
      set.seed(seedM[atUnkn,2])
      imputeRows[indUnkn] <- id.known.stratum[.Internal(sample(Nid.known.stratum, 1L, FALSE, NULL))]
      imputeLevels[indUnkn] <- 2L
    }
    ## Replace by survey:
    else if(any(id.known.survey)) {
      set.seed(seedM[atUnkn,3])
      imputeRows[indUnkn] <- id.known.survey[.Internal(sample(Nid.known.survey, 1L, FALSE, NULL))]
      imputeLevels[indUnkn] <- 3L
    }
		else{
			imputeLevels[indUnkn] <- 99L
		}
  }
	abnd$impLevel <- imputeLevels
	abnd$impRow <- imputeRows
  # Store process info:
	msg <- double(6)
	msg[1] <- NatKnownAge
	msg[2] <- NatUnknownAge
	msg[3] <- sum(abnd$impLevel[atUnknownAge]==1)
  msg[4] <- sum(abnd$impLevel[atUnknownAge]==2)
  msg[5] <- sum(abnd$impLevel[atUnknownAge]==3)
  msg[6] <- sum(abnd$impLevel[atUnknownAge]==99)
  
  # Create the following two data frames: 1) the rows of abnd which contain missing age and where there is age available in other rows, and 2) the rows with age available for imputing:
	missing <- abnd[atUnknownAge, , drop=FALSE]
	available <- abnd[imputeRows[atUnknownAge], , drop=FALSE]
	# Get the indices of missing data in 'missing' which are present in 'available':
	ind <- which(missing == "-" & available != "-", arr.ind=TRUE)
	indMissing <- cbind(missing$Row[ind[,1]], ind[,2])
	indReplacement <- cbind(available$Row[ind[,1]], ind[,2])
	
	# Apply the replacement. This may be moved to the funciton imputeByAge() in the future to allow for using previously generated indices:
	abnd[indMissing] <- abnd[indReplacement]
	
  #list(data=abnd, msg=msg, indMissing=indMissing, indReplacement=indReplacement, atUnknownAge=atUnknownAge, imputeRows=imputeRows, seedM=seedM)
  list(data=abnd, msg=msg, indMissing=indMissing, indReplacement=indReplacement, seedM=seedM)
}