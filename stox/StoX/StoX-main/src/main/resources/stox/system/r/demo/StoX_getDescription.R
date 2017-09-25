#' Get MND API reference information
#'
#' Converts, prints and optionally returns MND reference information given a search string to the reference information. Used in StoX.URL().
#' @param x character string giving the search string of an NMD API reference.
#' @param output a logical value indicating whether the description of possible parameter values should be returned as a matrix.
#' @param silent a logical value indicating whether the printing of the description of possible parameter values should be  avoided.
#' @keywords MND-API
#' @export
#' @examples
#' system.time(StoX_getDescription("http://tomcat7.imr.no:8080/apis/nmdapi/v1/api/nmdreference/missiontype"))
#' system.time(StoX_getDescription("http://tomcat7.imr.no:8080/apis/nmdapi/v1/api/nmdreference/platform"))

StoX_getDescription <- function(x, output=FALSE, silent=FALSE){
	
	############ AUTHOR(S): ############
	# Arne Johannes Holmin
	############### LOG: ###############
	# Start: 2015-02-26 - Clean version.
	########### DESCRIPTION: ###########
	# Converts, prints and optionally returns MND reference information given a search string to the reference information. Used in StoX.URL().
	############ VARIABLES: ############
	# ---x--- is a character string giving the search string of an NMD API reference
	# ---output---  is TRUE to return a matrix of the description of possible parameter values (default FALSE)
	# ---print--- is TRUE to print the description of possible parameter values on the console (default TRUE)
	
	
	##################################################
	##################################################
	########## Preparation ##########
	# A small funciton ordering a mixture of numeric and string:
	orderGeneral <-function(x){
		# Split 'x' into a character part and a numeric part, and order first the numeric part and then the character part:
		toCharacter = as.character(x)
		toNumeric = suppressWarnings(as.numeric(toCharacter))
		arenumeric = which(!is.na(toNumeric))
		arecharacter = which(is.na(toNumeric))
		c(arenumeric[order(toNumeric[arenumeric])], arecharacter[order(toCharacter[arecharacter])])
		}
	
	# Read the reference ref:
	ref = getURLContent(x, .encoding="latin1")
	# Split by line space:
	s = strsplit(ref, "\n", fixed=TRUE)[[1]]
	
	# Possible sets of key strings:
	keylist = list(
		c("\"platform\" : ", "\"platformCode\" : \"", "\"validFrom\" : ", "\"sysName\" : \"Ship Name\""),
		c("\"code\" : ", "\"description\" : "), 
		c("\"code\" : ", "\"name\" : ", "\"description\" : "), 
		c("\"nation\" : ", "\"nationioc\" : ", "\"nationname\" : "), 
		c("\"name\" : ", "\"shortname\" : ", "\"description\" : ")
		)
	
	# Identify which type of information is present:
	type = NA
	heads = head(s,100)
	for(i in seq_along(keylist)){
		hits = double(length(keylist[[i]]))
		for(j in seq_along(keylist[[i]])){
			# Store the number of hits for the differnt key strings:
			hits[j] = length(grep(keylist[[i]][j], heads))
			}
		if(all(hits>0) && !any(hits<max(hits)/2)){
			type = i
			}
		}
	
	
	########## Execution ##########
	# Move through the key strings in 'keylist' and identify the positions of the key strings:
	thiskey = keylist[[type]]
	thiskeyclean = unlist(lapply(strsplit(keylist[[type]],"\"",fixed=TRUE),"[",2))
	ncharthiskey = nchar(thiskey)
	allat = vector("list", length(thiskey))
	ref = vector("list", length(thiskey))
	names(ref) = thiskey
	
	for(i in seq_along(thiskey)){
		# First the lines holding the key:
		at = grep(thiskey[i], s)
		# Then the detailed positions:
		atpos = unlist(gregexpr(thiskey[i], s[at])) + ncharthiskey[i]
		# Then the lines:
		# Save 'at' for use if type==1:
		if(type == 1){
			allat[[i]] = at
			}
		# Extract and clean the ref:
		thisdata = substring(s[at], atpos)
		# Remove comma at the end:
		if(substring(thisdata[1], nchar(thisdata[1])) == ","){
			thisdata = substr(thisdata, 1, nchar(thisdata)-1)
			}
		# Remove quotes:
		thisdata = gsub("\"", "", thisdata, fixed=TRUE)
		# Insert into 'ref':
		ref[[i]] = thisdata
		}
	
	
	# Special care of type = 1. Pick out the values of 'platformCode' and 'validFrom' just prior to the indices of the 'sysName'
	if(type == 1){
		# First select only those 'platformCode'-indices just prior to 'platform':
		inplatform = findInterval(allat[[2]], allat[[4]])+1
		# Remove duplicated from the end:
		revdup = rev(duplicated(rev(inplatform)))
		inplatform = inplatform[!revdup]
		allat[[2]] = allat[[2]][!revdup]
		allat[[3]] = allat[[3]][!revdup]
		ref[[2]] = ref[[2]][!revdup]
		ref[[3]] = ref[[3]][!revdup]
		
		# Then find the intervals of the 'platformCode' and 'validFrom' in 'platform':
		inplatform = findInterval(allat[[2]], allat[[1]])+1
		
		# Remove empty platforms:
		nonemptyPlatforms = which(seq_along(allat[[1]]) %in% inplatform)
		allat[[1]] = allat[[1]][nonemptyPlatforms]
		ref[[1]] = ref[[1]][nonemptyPlatforms]
		inplatform = findInterval(allat[[2]], allat[[1]])+1
		
		# Add start year to the platform codes:
		ref[[2]] = paste(ref[[2]]," (",substr(ref[[3]],1,4),")",sep="")
		
		# Merge duplicated ships on platforms:
		ref[[2]] = unlist(by(ref[[2]], inplatform, paste, collapse=", "))
		
		# Remove the last two elements of the list (only keep code and name):
		ref = ref[1:2]
		thiskeyclean = thiskeyclean[1:2]
		}
	
	# Crop to the shortest:
	shortest = min(unlist(lapply(ref,length)))
	ref = lapply(ref, "[", seq_len(shortest))
	# Convert to ref frame and order:
	out = matrix(unlist(ref), ncol=length(ref))
	colnames(out) <- thiskeyclean
	out = out[orderGeneral(out[,1]),]
	rownames(out) <- NULL
	
	
	########## Output ##########
	outpasted = apply(rbind(thiskeyclean, out),1, paste, collapse=" = ")
	# Print the pasted information:
	if(!silent){
		cat(c(x,outpasted), sep="\n")
		}
	# Return either pasted ref or ref frame:
	if(output){
		out
		}
	else{
		invisible()
		}
	##################################################
	##################################################
	}
