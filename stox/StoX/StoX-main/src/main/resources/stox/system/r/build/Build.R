##### Create and install the package stox in R: #####


# install lazy loading packages
#install.packages(c("rJava"), repos='http://cran.uib.no/', quiet=T, verbose=F);
#install.packages(c("HMisc"), repos='http://cran.uib.no/', quiet=T, verbose=F);
#install.packages(c("rgdal"), repos='http://cran.uib.no/', quiet=T, verbose=F);
#install.packages(c("sp"), repos='http://cran.uib.no/', quiet=T, verbose=F);
#install.packages(c("rgeos"), repos='http://cran.uib.no/', quiet=T, verbose=F);
#install.packages(c("data.table"), repos='http://cran.uib.no/', quiet=T, verbose=F);
#install.packages(c("gdata"), repos='http://cran.uib.no/', quiet=T, verbose=F);

# install devtools
#install.packages(c("devtools"), repos='http://cran.uib.no/', quiet=T, verbose=F);
#install.packages(c("roxygen2"), repos='http://cran.uib.no/', quiet=T, verbose=F);

##### Load devtools: #####
library("devtools")
##########

# sourceDir - Define the directory in which the R- and jar-files used to build the package are located. This is preferably a local copy of 
#						 https://code.imr.no/repos/Sea2Data/NewBeam/trunk/beam/base/src/main/resources/stox/system/r":"			 
#						 Example: "C:/Projects/Sea2Data/NewBeam/trunk/beam/base/src/main/resources/stox/system/r"
# buildDir -	Define the directory of the R-package build files (this could be anywhere on your computer):
#						 Example: "F:/aasmunds/rstox/Rstox"
# promise:		Use library(Rstox) to use Rstox
build.rstox.package <- function(sourceDir, buildDir, exportDir, dataDir, pkgName="Rstox", version="1.0", Rversion="3.3.1", data="gytetokt2016") {
	
	try(lapply(.libPaths(), function(xx) remove.packages(pkgName, xx)), silent=TRUE)
	
	##### Define sub directories of stoxdir (r, java and data directories): #####
	stoxdir = file.path(buildDir, pkgName)
	rdir = file.path(stoxdir, "R")
	javadir = file.path(stoxdir, "inst", "java")
	extdatadir = file.path(stoxdir, "inst", "extdata")
	#acousticdir = file.path(stoxdir, "inst", "extdata", "acoustic")
	#bioticdir = file.path(stoxdir, "inst", "extdata", "biotic")
	##########
	
	##### Create the package skeleton: #####
	unlink(stoxdir, recursive=TRUE, force=TRUE)
	suppressWarnings(dir.create(stoxdir, recursive=TRUE))
	suppressWarnings(dir.create(extdatadir, recursive=TRUE))
	suppressWarnings(dir.create(rdir, recursive=TRUE))
	suppressWarnings(dir.create(javadir, recursive=TRUE))
	#if(!file.exists(acousticdir)) dir.create(acousticdir, recursive=T)
	#if(!file.exists(bioticdir)) dir.create(bioticdir, recursive=T)
	##########
	
	##### Add R function files to the "R" directory in the package #####
	Rfiles = list.files(sourceDir, "^.*[:.:](r|R)$")
	Javafiles = list.files(sourceDir, "^.*[:.:](jar)$")
	
	# Copy R files. Only files with file extension "r" or "R" which are located in the directory sourceDir (not in subfolders) are copied:
	if(length(Rfiles)){
		Rfiles_copy = file.path(sourceDir, Rfiles)
		file.copy(Rfiles_copy, rdir, overwrite=TRUE)
	}

	##### Create the directory "inst/java" in the root directory of the package (alongside the "R" directory), and put the file "rstox.jar" in that directory: #####
	if(length(Javafiles)){
		Javafiles_copy = file.path(sourceDir, Javafiles)
		file.copy(Javafiles_copy, javadir, overwrite=TRUE)
	}
	##########
	
	##### Save the following content to the onLoad.R file in the "R" directory: #####
	# JAVA_HOME is unset to be able to load rJava.dll in R CMD BATCH
	# jPackage is moved to rstox.init for dynamic import of rJava
	# The local Rstox environment is created here, in which all useful outputs from functions are placed, and saved at the end of any code:
	onLoadFile = file.path(stoxdir, "R", "onLoad.R")
	onLoadText = paste(
		".onLoad <- function(libname, pkgname){",
		"	",
		"	Sys.setenv(JAVA_HOME=\"\")",
		"	options(java.parameters=\"-Xmx2g\")",
		"# Create a Rstox environment in which the baseline objects of the various projects are placed. This allows for a check for previously run baseline models and avoids memory leakage:", 
		"	assign(\"RstoxEnv\", new.env(), envir=.GlobalEnv)",
		"	assign(\"StoXFolders\", c(\"input\", \"output\", \"process\"), envir=get(\"RstoxEnv\"))",
		"	assign(\"NMD_data_types\", c(\"echosounder\", \"biotic\", \"landing\"), envir=get(\"RstoxEnv\"))",
		"	assign(\"StoX_data_types\", c(\"acoustic\", \"biotic\", \"landing\"), envir=get(\"RstoxEnv\"))",
		"	assign(\"StoX_data_type_keys\", c(acoustic=\"echosounder_dataset\", biotic=\"missions xmlns\", landing=\"Sluttseddel\"), envir=get(\"RstoxEnv\"))",
		"	assign(\"bootstrapTypes\", c(\"Acoustic\", \"SweptArea\"), envir=get(\"RstoxEnv\"))",
		"	assign(\"processLevels\", c(\"bootstrap\", \"bootstrapImpute\"), envir=get(\"RstoxEnv\"))",
	"}", sep="\n")
	write(onLoadText, onLoadFile)
	##########
	
	##### Save a Java memory message to the onAttach.R file in the "R" directory: #####
	onAttachFile = file.path(stoxdir, "R", "onAttach.R")
	onAttachText = paste(
		".onAttach <- function(libname, pkgname){",
		paste0("	packageStartupMessage(\"", pkgName, "_", version, "\n**********\nIf problems with Java Memory such as java.lang.OutOfMemoryError occurs, try increasing the Java memory by running options(java.parameters=\\\"-Xmx4g\\\"), and possibly using an even higher value than 4g\n**********\n\", appendLF=FALSE)"),
	"}", sep="\n")
	write(onAttachText, onAttachFile)
	##########
	
	##### Add required fields to the DESCRIPTION file (below is the full content of the DESCRIPTION file): #####
	# Depends is replaced by @import specified by functions"
	DESCRIPTIONfile = file.path(stoxdir, "DESCRIPTION")
	DESCRIPTIONtext = paste(
		paste0("Package: ", pkgName),
		#"Encoding: UTF8",
		"Title: Running stox functionality independently in R",
		paste0("Version: ", version),
		"Authors@R: c(",
		"  person(\"Arne Johannes\", \"Holmin\", role = c(\"aut\",\"cre\"), email = \"arnejh@imr.no\"),",
		"  person(\"Gjert Endre\", \"Dingsoer\", role = \"ctb\"),",
		"  person(\"Aasmund\", \"Skaalevik\", role = \"ctb\"),",
		"  person(\"Espen\", \"Johnsen\", role = \"ctb\"))",
		# OLD: "Authors@R: c(",
		# OLD: "person(\"Arne Johannes\", \"Holmin\", role = c(\"aut\",\"cre\"), email = \"arnejh@imr.no\"),",
		# OLD: "person(\"Gjert Endre\", \"Dingsoer\", role = c(\"aut\",\"cre\"), email = \"gjert.endre.dingsoer@imr.no\"),",
		# OLD: "person(\"Aasmund\", \"Skaalevik\", role = c(\"aut\",\"cre\"), email = \"aasmund.skaalevik@imr.no\"),",
		# OLD: "person(\"Espen\", \"Johnsen\", role = \"aut\", email = \"espen.johnsen@imr.no\"))",
		"Author: Arne Johannes Holmin [aut, cre],",
		"  Gjert Endre Dingsoer [ctr],",
		"  Aasmund Skaalevik [ctr],",
		"  Espen Johnsen [ctr]",
		"Maintainer: Arne Johannes Holmin <arnejh@imr.no>",
		paste0("Depends: R (>= ", Rversion, ")"), 
		"Description: This package contains most of the functionality of the StoX software, which is used for assessment of fish and other marine resources based on biotic and acoustic survey and landings data, among other uses. Rstox is intended for further analyses of such data, facilitating iterations over an arbitrary number of parameter values and data sets.",
		#paste("Imports:", paste("RCurl", "XML", "data.table", "rJava", "rgdal", "rgeos", "sp", "gdata", sep=", ")), 
		"BugReports: https://github.com/Rstox/Rstox/issues", 
		"License: GPL-2",
		"LazyData: true", sep="\n")
	write(DESCRIPTIONtext, DESCRIPTIONfile)
	##########
	# Comment on depends - static linking/loading should be rarely used according to oxygen documentation
	# using import or NAMESPACE instead
	
	##### Add example data: #####
	Test_Rstoxdir = file.path(extdatadir, "Test_Rstox")
	exampleData <- file.path(dataDir, data, "input")
	filePaths <- list.files(exampleData, full.name=TRUE, recursive=TRUE)
	newFilePaths <- file.path(Test_Rstoxdir, "input", list.files(exampleData, recursive=TRUE))
	for(i in seq_along(filePaths)){
		suppressWarnings(dir.create(dirname(newFilePaths[i]), recursive=TRUE))
		file.copy(filePaths[i], newFilePaths[i])
	}
	# Add also the project.xml files:
	exampleProjectfile <- file.path(dataDir, data, "process", "project.xml")
	if(file.exists(exampleProjectfile) && !file.info(exampleProjectfile)$isdir){
		newexampleProjectfile <- file.path(Test_Rstoxdir, "process", "project.xml")
		suppressWarnings(dir.create(dirname(newexampleProjectfile), recursive=TRUE))
		file.copy(exampleProjectfile, newexampleProjectfile)
	}
	##########
	
	##### Create documentation: #####
	document(stoxdir)
	# Alter the DESCRIPTION file to contain the imports listed in the NAMESPACE file:
	imports <- getRstoxImports(stoxdir)
	DESCRIPTIONtext <- readLines(DESCRIPTIONfile)
	if(length(imports)){
		cat("Imports:\n		", file=DESCRIPTIONfile, append=TRUE)
		cat(paste(imports, collapse=",\n		"), file=DESCRIPTIONfile, append=TRUE)
	}
	##########
	
	##### Create platform independent bundle of source package *****
	pkgFileVer <- build(stoxdir, path=exportDir)
	#pkgFile <- paste0(sourceDir, "/", pkgName, "_", version, ".tar.gz");
	#pkgFileNoVersion <- paste0(sourceDir, "/", pkgName, ".tar.gz");
	#file.rename(pkgFileVer, pkgFile)
	#file.copy(pkgFile, pkgFileNoVersion)
	##########
	
	##### Unload the package: #####
	unload(stoxdir)
	##########
	
	##### Install local source package by utils (independent of dev-tools): #####
	#pkgDir <- paste0(buildDir, "/", pkgName, "*.gz")
	install.packages(pkgFileVer, repos=NULL, type="source", lib=.libPaths()[1])
	##########
}

getExample <- function(x){
	l <- readLines(x)
	atex <- which(substr(l, 1, 12) == "#' @examples")
	atblank <- which(substr(l, 1, 2) == "#'" & nchar(l)<4)
	atblank <- sapply(atex, function(xx) min(atblank[atblank>xx]))
	ex <- vector("list", length(atex))
	for(i in seq_along(ex)){
		#ex[[i]] <- paste0(l[seq(atex[i], atblank[i]-1)], collapse="\n")
		ex[[i]] <- l[seq(atex[i]+1, atblank[i]-1)]
		atdontrun = grep("dontrun", ex[[i]])
		if(length(atdontrun)){
			ex[[i]] = ex[[i]][-atdontrun]
			last = ex[[i]][length(ex[[i]])]
			if(substring(last, nchar(last)) == "}"){
				last = substr(last, 1, nchar(last)-1)
				ex[[i]][length(ex[[i]])] = last
			}
		}
		ex[[i]] = substring(ex[[i]], 4)
	}
	if(length(ex)){
		names(ex) <- paste("Line", atex, "to", atblank, sep="_")	
	}
	ex
}

getExamples <- function(x, file){
	Rfiles = list.files(x, full.names=TRUE, "^.*[:.:](r|R)$")
	out = lapply(Rfiles, getExample)
	names(out) <- basename(Rfiles)
	write("library(Rstox)", file=file)
	for(i in seq_along(out)){
		if(length(out[[i]])){
			write(paste0("##### File: ", names(out)[i], ": #####"), file=file, append=TRUE)
			append = TRUE
			for(j in seq_along(out[[i]])){
				write(paste0("# ", names(out[[i]])[j], ":"), file=file, append=TRUE)
				write(out[[i]][[j]], file=file, append=TRUE)
				write("", file=file, append=TRUE)
			}
			write("", file=file, append=TRUE)
		}
	}
}

# Function used for writing the README file automatically, including package dependencies, R and Rstox version and release notes:
writeRstoxREADME <- function(READMEfile, version, Rversion, betaAlpha, betaAlphaString, imports, official=FALSE){
	# Write Rstox and R version in the first two lines. THIS SHOULD NEVER BE CHANGED, SINCE STOX READS THESE TWO LINES TO CHECK VERSIONS:
	write(paste0("# Rstox version: ", version, " (latest ", betaAlphaString, ", ", format(Sys.time(), "%Y-%m-%d"), ")"), READMEfile)
	write(paste0("# R version: ", Rversion), READMEfile, append=TRUE)
	write("", READMEfile, append=TRUE)
	# Package description and installation code:
	write("# The package Rstox contains most of the functionality of the stock assesment utility StoX, which is an open source approach to acoustic and swept area survey calculations. Download Rstox from ftp://ftp.imr.no/StoX/Download/Rstox or install by running the following commands in R:", READMEfile, append=TRUE)
	write("", READMEfile, append=TRUE)
	write("# Install the packages that Rstox depends on. Note that this updates all the specified packages to the latest (binary) version:", READMEfile, append=TRUE)
	write(paste0("dep.pck <- c(\"", paste0(imports, collapse="\", \""), "\")"), READMEfile, append=TRUE)
	# WARNING: IT IS CRUSIAL TO ENCLUDE THE repos IN THIS CALL, FOR STOX TO SOURCE THE README FILE PROPERLY (RESULTS IN AN ERROR IF ABSENT) IT SEEMS "R CMD BATCH source(TheReadMeFile)" RETURNS AN ERROR WHEN repos IS NOT SET (2016-12-16):
	write("install.packages(dep.pck, repos=\"http://cran.us.r-project.org\", type=\"binary\")", READMEfile, append=TRUE)
	#write("install.packages(dep.pck, type=\"binary\")", READMEfile, append=TRUE)
	write("", READMEfile, append=TRUE)
	write("# Install Rstox:", READMEfile, append=TRUE)
	
	# Get the version string, the name of the Rstox tar file, the ftp root and, finally, the ftp directory and full path to the Rstox tar file:
	versionString <- paste0("Rstox_", version)
	tarName <- paste0(versionString, ".tar.gz")
	ftpRoot <- "ftp://ftp.imr.no/StoX/Download/Rstox"
	if(betaAlpha==3){
		ftpDir <- file.path(ftpRoot, "Versions", "Alpha", versionString)
	}
	else{
		if(official){
			ftpDir <- ftpRoot
		}
		else{
			ftpDir <- file.path(ftpRoot, "Versions", versionString)
		}
	}
	tarFile <- file.path(ftpDir, tarName)
	
	# Write the Rstox install command:
	write(paste0("install.packages(\"", tarFile, "\", repos=NULL)"), READMEfile, append=TRUE)
	
	
	write("", READMEfile, append=TRUE)
	write("# Note that 64 bit Java is required to run Rstox", READMEfile, append=TRUE)
	write("# On Windows, install Java from this webpage: https://www.java.com/en/download/windows-64bit.jsp, or follow the instructions found on ftp://ftp.imr.no/StoX/Tutorials/", READMEfile, append=TRUE)
	write("# On Mac, getting Java and Rstox to communicate can be challenging. If you run into problems such as \"Unsupported major.minor version ...\", try the following:", READMEfile, append=TRUE)
	write("# Update java, on", READMEfile, append=TRUE)
	write("# http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html", READMEfile, append=TRUE)
	write("# If this does not work install first the JDK and then the JRE:", READMEfile, append=TRUE)
	write("# http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html", READMEfile, append=TRUE)
	write("# http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html", READMEfile, append=TRUE)
	write("# You may want to check that the downloaded version is first in the list by running the following in the Terminal:", READMEfile, append=TRUE)
	write("# /usr/libexec/java_home -V", READMEfile, append=TRUE)
	write("# java -version", READMEfile, append=TRUE)
	write("# Then run this in the Terminal:", READMEfile, append=TRUE)
	write("# R CMD javareconf", READMEfile, append=TRUE)
	write("# Open R (close and then open if already open) and install rJava:", READMEfile, append=TRUE)
	write("# install.packages('rJava', type='source')", READMEfile, append=TRUE)
	write("# Then the installed Rstox should work.", READMEfile, append=TRUE)
	
	
	# Write release notes:
	write("", READMEfile, append=TRUE)
	write("", READMEfile, append=TRUE)
	write("# Release notes:", READMEfile, append=TRUE)
	
	# Read the changes:
	l <- readLines("~/Documents/Produktivt/Prosjekt/Acoustics/sea2R/rstox_svn/r/CHANGES")
	# Split into vesions:
	atversion <- which(substr(l, 1, 1) == "#")
	versionStringInChanges <- substr(l[atversion], 3, regexpr(" ", substring(l[atversion], 3)) + 2 - 1)
	l <- split(l, findInterval(seq_along(l), c(atversion, length(l)+1)))
	names(l) <- versionStringInChanges
	l <- lapply(l, function(xx) xx[substr(xx, 1, 1) != "#"])
	thisl <- l[[version]]
	hasText <- which(nchar(thisl)>1)
	thisl[hasText] <- paste0("# ", seq_along(hasText), ". ", thisl[hasText])
	write(thisl, READMEfile, append=TRUE)
}

getRstoxImports <- function(buildDir){
	# Read the NAMESPACE file and get the package dependencies:
	buildDirList <- list.files(buildDir, recursive=TRUE, full.names=TRUE)
	NAMESPACE <- readLines(buildDirList[basename(buildDirList) == "NAMESPACE"])
	atImports <- grep("import", NAMESPACE)
	imports <- NAMESPACE[atImports]
	imports <- sapply(strsplit(imports, "(", fixed=TRUE), "[", 2)
	imports <- sapply(strsplit(imports, ")", fixed=TRUE), "[", 1)
	imports <- unique(sapply(strsplit(imports, ",", fixed=TRUE), "[", 1))
	
	
	importsPresent <- intersect(imports, installed.packages()[,"Package"])
	imports <- setdiff(imports, importsPresent[installed.packages()[importsPresent,"Priority"] %in% "base"])
	imports <- sort(imports)
	
	#imports <- sort(imports[!(installed.packages()[imports,"Priority"]) %in% "base"])
	imports
}
