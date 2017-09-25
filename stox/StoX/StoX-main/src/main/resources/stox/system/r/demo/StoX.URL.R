#' Create an NMD-API URL
#'
#' Returns a search string which can be used to download data from the NMD (The Norwegian Marine Data Centre).
#' @param type Do you love cats? Defaults to TRUE.
#' @keywords cats
#' @export
#' @examples
#' StoX.URL()
#' @details text describing parameter inputs in more detail.

StoX.URL <- function(type="bio", query=list(), includeprey=FALSE, includeAgeDetermination=FALSE, version=1, ...){
	
	############ AUTHOR(S): ############
	# Arne Johannes Holmin
	############### LOG: ###############
	# Start: 2015-02-26 - Clean version.
	########### DESCRIPTION: ###########
	# Returns a search string which can be used to download data from the NMD (The Norwegian Marine Data Centre).
	############ VARIABLES: ############
	# ---type--- a string representing the type of data to download. Has the following possible values (abbreviations accepted):
	#				'biotoc':				Biotic data (fish samples, planction, etc)
	#				'echo':					Acoustic data
	#				'hub':					Vessel and environmental dat
	#				'mission':				Mission information
	#				'refmissionType':		List of available values of the query 'missionType'
	#				'refplatform':			List of available values of the query 'platform'
	#				'refnation':			List of available values of the query 'nation'
	#				'refstationType':		List of available values of the query 'stationType'
	#				'reftrawlQuality':	List of available values of the query 'trawlQuality'
	#				'refgearCondition':	List of available values of the query 'gearCondition'
	#				'refequipmentCode':	List of available values of the query 'equipmentCode'
	#				'refshipcodeSpd':		List of available values of the query 'shipcodeSpd'
	# 
	# ---query--- is a list of queries, given as list(query1 = VALUE, query2 = ANOTHER VALUE, ...). A set of pre-defined query  variables are defined: 
	#				cruiseCode		The cruise code, given as a 7 digit numeric of the format yyyynnn, where yyyy is the start year of the cruise and nnn is the code of the cruise for the given year. List of cruise codes will be available later.
	#				missionType	Mission type, such as 2 = Referanseflåten-Hav. List possible values with  StoX.URL("refm").
	#				platform		Platform code, such as 4174 = G.O.Sars. List possible values with  StoX.URL("refp") (takes several seconds).
	#				nation			Nation code, such as 58 = Norway. List possible values with StoX.URL("refn").
	#				stationType	Station type code, such as E = Ringnot/kolmule. List possible values with StoX.URL("refst").
	#				trawlQuality	Trawl quality code, such as 2 = Satt ut på akustisk registrering. List possible values with StoX.URL("reft").
	#				gearCondition	Gear condition code, such as 2 = Små skader, ikke vesentlig. List possible values with StoX.URL("refg").
	#				equipmentCode	Equipment code, such as 2501 = Van Veen grabb. List possible values with StoX.URL("refe").
	#				equipmentNo	???.
	#				equpmentCount	Number of tools in the equipment.
	#				system			???.
	#				area			???.
	#				shipcodeSpd	Ship code, such as 3 = Kommersielt eller leid fartøy. List possible values with StoX.URL("refsh").
	#				departurePort	The departure port, such as "Tromsø".
	#				arrivalPort	The arrival port, such as "Bodø".

	#
	# ---includeprey--- is TRUE to include prey in the biotic data, defaulted to FALSE
	# ---includeAgeDetermination--- is TRUE to include age determination in the biotic data, defaulted to FALSE
	# ---version--- is the version of the database, defaulted to 1.
	# ---...--- arguments passed on to StoX_getDescription when 'type' starts with "ref" (description of possible values of query). specifically 'output' (default FALSE), specifying whether a matrix of the description of possible parameter values should be returnerd; and 'print' (default TRUE), specifying whether to print the description on the console.
	
	
	##################################################
	##################################################
	########## Preparation ##########
	# Construct the root of the URL:
	server = "http://tomcat7.imr.no:8080/apis/nmdapi"
	version = paste("v", version,sep="")
	URL = file.path(server, version, "api")
	
	# Define URL characters:
	URL_eq = "%3D"
	URL_ge = "%3E%3D"
	URL_se = "%3C%3D"
	URL_g = "%3E"
	URL_s = "%3C"
	URL_sp = "%20"
	URL_q = "%27"
	and = "%20and%20"
	
	# Define the base variable
	baseVariable = c("fishStation.", "echosounderDataset.", "track.", "")
	
	# Accept types starting with "NMD":
	if(tolower(substr(type[1],1,3))=="nmd"){
		type[1] = substring(type[1], 4)
		}
	validtypes = c("biotic", "echo", "hub", "mission", "refmissionType", "refplatform", "refnation", "refstationType", "reftrawlQuality", "refgearCondition", "refequipmentCode", "refshipcodeSpd", "refeventtype")
	validSearch = c("nmdbiotic", "nmdecho", "nmdhub", "nmdmission", "nmdreference/missiontype", "nmdreference/platform", "nmdreference/nation", "nmdreference/Fishstationtype", "nmdreference/Samplequality", "nmdreference/Gearcondition", "nmdreference/equipment", "nmdreference/SKIPSKODE_SPD", "nmdreference/eventtype")
	
	# Match the requested type with the valid types:
	typematch = which(tolower(substr(validtypes, 1, nchar(type[1]))) == tolower(type[1]))
	# Error if non-unique types are given:
	if(length(typematch)>1){
		stop(paste("Non-unique type given. Interpreted as ",paste(validtypes[typematch],collapse=", "),sep=""))
		}
	typestring = validSearch[typematch]
	
	# If reference data are requested, return here:
	if(typematch>=5){
		return(StoX_getDescription(file.path(URL, typestring), ...))
		}
	
	# Warning if no query is given (this should return a list of available queries):
	if(length(query)==0){
		#warning("No queries specified. List of pre-defined queries and possible vaules returned")
		warning("No queries specified. Returning NULL")
		return()
		}
	
	# Define valid pre-defined queries and corresponding query codes and types:
	# For the fixed value queries:
	queryMatrix = matrix(c(
						"cruiseCode",		"mission.cruiseMission.cruiseCode", 	"",					URL_q,	"1234",
						"missionType",		"mission.missiontype.code", 			"",					URL_q,	"1234",
						"platform",			"mission.platform.platform", 			"",					URL_q,	"1234",
						"nation",			"nation.nation", 						"",					URL_q,	"1",
						"stationType",		"stationType.name", 					"",					URL_q,	"1",
						"trawlQuality",		"trawlQuality.name", 					"",					URL_q,	"1",
						"gearCondition",	"gearcondition.name", 					"",					URL_q,	"1",
						"equipmentCode",	"equipment.code", 						"",					URL_q,	"1",
						"equipmentCount",	"equipmentCount", 						"",					"",		"1",
						"system",			"system", 								"",					"",		"1",
						"area",				"area", 								"",					"",		"1",
						"shipcodeSpd",		"shipcodeSpd.name", 					"",					URL_q,	"1",
						#"departurePort",	"mission.cruiseMission.departurePort",	"",					URL_q,	"1234",
						#"arrivalPort",		"mission.cruiseMission.arrivalPort",	"",					URL_q,	"1234",
						"eventtype",		"eventtype.code",						"",					URL_q,	"3",
						"year",				"year",									"",					"",		"1",
						"long",				"longitudeStart",						"longitudeEnd",		"",		"1",
						"lat",				"latitudeStart",						"latitudeEnd",		"",		"1",
						"log",				"logStart",								"logStop",			"",		"1",
						"bottomDepth",		"bottomDepthStart",						"bottomDepthStop",	"",		"1",
						"fishingDepth",		"fishingDepthMin",						"fishingDepthMax",	"",		"1",
						"directionGps",		"directionGps",							"directionGps",		URL_q,		"1",
						"speedEquipment",	"speedEquipment",						"speedEquipment",	"",		"1",
						"distance",			"distance",								"distance",			"",		"1",
						"trawlOpening",		"trawlOpening",							"trawlOpening",		"",		"1",
						"trawlDoorSpread",	"trawlDoorSpread",						"trawlDoorSpread",	"",		"1",
						"date",				"startDate",							"stopDate",			URL_q,	"1",
						"date",				"mission.startTime",					"mission.stopTime",	URL_q,	"234"
						), ncol=5, byrow=TRUE)
						
	# Funciton to convert from a query to date:
	query2date <- function(x){
		# Defaults:
		defaults = c("2014", "01", "01", "00", "00", "00")
		
		# Convert to character if POSIX is used:
		x = as.character(x)
		x = unlist(strsplit(x, "-", fixed=TRUE))
		if(length(x)==0){
			return("")
			}
		x = unlist(strsplit(x, ":", fixed=TRUE))
		if(length(x)==0){
			return("")
			}
		x = unlist(strsplit(x, " ", fixed=TRUE))
		if(length(x)==0){
			return("")
			}
		
		# Merge with defaults if less than 6 elements are given in 'x':
		lx = length(x)
		if(lx<6){
			x = c(x, tail(defaults, 6-lx))
			}
		# PAste together the ISO 8601 string:
		paste(x[1], "-",
			x[2], "-",
			x[3], "T",
			x[4], "%3A",
			x[5], "%3A",
			x[6], "Z", 
			sep="")
		}
			
	
	########## Execution ##########
	# Move through the query elements and match to the pre-defined queries:
	querynames = names(query)
	Nqueries = length(querynames)
	querymatch = double(Nqueries)
	querylist = vector("list", Nqueries)
	
	for(i in seq_len(Nqueries)){
		# Identify matches
		thismatch = which(
			# Match the query name with the first column of 'queryMatrix':
			tolower(substr(queryMatrix[,1], 1, nchar(querynames[i]))) == tolower(querynames[i])
			# Match the type with the fifth column of 'queryMatrix':
			& regexpr(typematch, queryMatrix[,5])>0
			)
		
		# Error if non-unique query is given:
		if(length(thismatch)>1){
			stop(paste("Non-unique query given. Interpreted as ",paste(queryMatrix[thismatch,1],collapse=", "),sep=""))
			}
		else if(length(thismatch)==1){
			# Save the match:
			querymatch[i] = thismatch
			
			### 1. Treat fixed value queries: ###
			if(nchar(queryMatrix[thismatch,3])==0){
				# Paste together the each string of the current query:
				querylist[[i]] = paste(
					baseVariable[typematch], 
					queryMatrix[thismatch,2], 
					URL_eq, 
					# Add quote if string:
					queryMatrix[thismatch,4], 
					query[[i]][1], 
					# Add quote if string:
					queryMatrix[thismatch,4], 
					sep="")
				}
			
			### 2. Treat interval value queries: ###
			else if(length(thismatch)>0){
				
				# Print a warning if date is requested for other types than biotic:
				if(thismatch>=nrow(queryMatrix)-1){
					# Convert to ISO 8601:
					query[[i]][1] = query2date(query[[i]][1])
					query[[i]][2] = query2date(query[[i]][2])
					if(typematch!=1){
						warning("Searching for date considers start and stop time of entire missions when 'type' is one of \"echo\", \"hub\", and \"mission\"")
						}
					}
				
				# Define the search string for the first query value of the interval type query (using "greater than"):
				querylist[[i]] = paste(
					baseVariable[typematch], 
					queryMatrix[thismatch,2], 
					URL_ge, 
					# Add quote if string:
					queryMatrix[thismatch,4], 
					query[[i]][1], 
					# Add quote if string:
					queryMatrix[thismatch,4], 
					sep="")
				# Add the second query value of the interval type query (using "less than"):
				if(length(query[[i]])==2){
					querylist[[i]] = paste(
						querylist[[i]], 
						and, 
						baseVariable[typematch], 
						queryMatrix[thismatch,3], 
						URL_se, 
						# Add quote if string:
						queryMatrix[thismatch,4], 
						query[[i]][2], 
						# Add quote if string:
						queryMatrix[thismatch,4], 
						sep="")
					}
				else{
					warning(paste("Only one value given for the interval variable ", queryMatrix[thismatch,1], " . Larger/equal asumed: ", queryMatrix[thismatch,1], " >= ", query[[i]][1], sep=""))
					}
				}
			}
		}
	
	# Check wheter any queries were not detected amongst the pre-defined queries:
	notPredefined = which(querymatch==0)
	NnotPredefined = length(notPredefined)
	if(NnotPredefined>0){
		warning(paste(
			ngettext(NnotPredefined, "The query:\n\"", "The queries:\n\""),
			paste(querynames[notPredefined], collapse="\"\n\""),
			"\"\ndid not match the pre-defined queries for the given type '", validtypes[typematch], "', and ",
			ngettext(NnotPredefined, "was ", "were "),
			"considererd to be exact. See ?StoX.URL for a table available combinations of pre-defined queries and types. Only the pre-defined queries should be used unless one knows exactly what to search for.",
			sep=""))
		querylist[notPredefined] = paste(querynames[notPredefined], "=", unlist(lapply(query[notPredefined],head,1)), sep="")
		}
	
	# Merge all query strings:
	querylist = paste("query?query=", paste(querylist, collapse=and), "&format=xml", sep="")
	
	
	########## Output ##########
	file.path(URL, typestring, querylist)
	##################################################
	##################################################
	}
