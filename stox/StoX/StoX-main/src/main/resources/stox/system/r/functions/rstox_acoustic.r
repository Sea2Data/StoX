#*********************************************
#*********************************************
#' Get and aggregate PSUNASC
#' 
#' \code{getPSUNASC} gets a joined table with meanNASC, psu, stratum, and area. I.e., reads transect data, strata and area information from baseline Java object and merges them into one data frame. \cr \cr
#' \code{aggPSUNASC} aggregates psuNASC Layer to PELBOT. Used within functions when resampling and rescaling NASC values if Layer!=PELBOT.
#' 
#' @param baseline	A StoX baseline object.
#' @param psuNASC	Data frame from getPSUNASC().
#'
#' @return A Java integer, double, or Boolean
#' \code{getPSUNASC} returns psuNASC Data frame with mean NASC (Value) per transect (PSU) and Layer together with strata area \cr 
#' \code{aggPSUNASC} returns psuNASC_agg Data frame with mean NASC (Value) per transect (PSU)
#'
#' @examples
#' # Create the test project:
#' createProject("Test_Rstox", files=system.file("extdata", "Test_Rstox", package="Rstox"), ow=TRUE)
#' psuNASC <- getPSUNASC("Test_Rstox")
#' psuNASC_agg <- aggPSUNASC(psuNASC=psuNASC)
#'
#' @export
#' @rdname getPSUNASC
#'
getPSUNASC <- function(baseline){

	#psuNASC <- getDataFrame1(baseline, 'MeanNASC')
	psuNASC <- getBaseline(baseline, fun="MeanNASC", input=FALSE, msg=FALSE)
	# Test the presence of acoustic data:
	if(length(psuNASC)==0){
		warning(paste0("Process with function MeanNASC missing in project \"", getProjectPaths(baseline)$projectName, "\""))
		return(NULL)
	}
	
	#psuStratum <- getProcessDataTableAsDataFrame(baseline, 'proc')
	psuStratum <- getBaseline(baseline, input="psustratum", proc=FALSE, msg=FALSE)
	
	# Filter psu/stratum by includeintotal flag in table stratumpolygon.
	#stratumPolygon <- getProcessDataTableAsDataFrame(baseline, 'stratumpolygon')
	stratumPolygon <- getBaseline(baseline, input="stratumpolygon", proc=FALSE, msg=FALSE)
	inclStrata <- stratumPolygon[stratumPolygon$IncludeInTotal %in% TRUE, c("Stratum")]
	psuStratum <- psuStratum[psuStratum$Stratum %in% inclStrata,]
	
	#stratumArea <- getDataFrame1(baseline, 'StratumArea')
	stratumArea <- getBaseline(baseline, fun="StratumArea", input=FALSE, msg=FALSE)
	# Added a warning if SampleUnitType is not set to PSU, but rahter EDSU:
	if(!any(tolower(psuNASC$SampleUnitType) == "psu")){
		warning("getPSUNASC() requires SampleUnit to be PSU in the baseline.")
	}
	psuNASC <- merge(x=merge(x=psuNASC, y=psuStratum, by.x='SampleUnit', by.y='PSU'), y=stratumArea, by.x='Stratum', by.y='PolygonKey')
	
	# PSUSTRATUM may contain strata not covered by NASC EDSUs-> therefore drop empty stratum levels in factor.
	psuNASC <- droplevels(psuNASC)	

	#change headers by using names
	names(psuNASC)[match(c("NASC"), names(psuNASC))] <- "Value"
	names(psuNASC)[match(c("Distance"), names(psuNASC))] <- "dist"
	psuNASC
}
#'
#' @export
#' @rdname getPSUNASC
#'
aggPSUNASC <- function(psuNASC){
	# The functions J and .jnew and other functions in the rJava library needs initialization:
	.Rstox.init()
	Functions <- J("no.imr.stox.functions.utils.Functions")
	# psuNASC contains the column PSU (AJ 2016-08-31):
	if(length(psuNASC$PSU)){
		names(psuNASC)[names(psuNASC)=="PSU"] <- "SampleUnitType"
	}
	a <- aggregate(Value~Stratum + SampleUnitType + SampleUnit + SampleSize + dist + Area, psuNASC, sum)
	data.frame( append(a, list(LayerType=Functions$LAYERTYPE_WATERCOLUMN, Layer=Functions$WATERCOLUMN_PELBOT), after=grep("^dist$", colnames(psuNASC))) )
}


#*********************************************
#*********************************************
#' Weighted mean NASC by stratum
#'
#' Calculates mean by stratum (Jolly & Hampton 1990 Eq(2)) and variance by stratum (Eq3b).
#' 
#' @param tr.value	Transect NASC values.
#' @param tr.dist	Transect distance values.
#'
#' @return list with mean and variance by stratum
#'
#' @export
#' 
wtd.strata.est <- function(tr.value, tr.dist){
	mean.strata <- sum(tr.value * tr.dist) / sum(tr.dist)
	#var.strata <- (sum((tr.dist^2) * ((tr.value - mean.strata)^2))) /
	#	(((mean(tr.dist))^2) * length(tr.value) * (length(tr.value) -1) )
	var.strata1 <- sum(tr.dist^2 * (tr.value - mean.strata)^2)
	var.strata2 <- mean(tr.dist)^2 * length(tr.value) * (length(tr.value) - 1)
	var.strata <- var.strata1 / var.strata2
	out <- list(strata.mean=mean.strata, strata.var=var.strata)
}


#*********************************************
#*********************************************
#' Estimate mean NASC distribution
#'
#' Calculates mean and variance of NASC, based on Jolly & Hampton 1990 http://www.nrcresearchpress.com/doi/pdf/10.1139/f90-147
#' 
#' @param baseline	StoX Java baseline object.
#' @param psuNASC	MeanNASC table from baseline.
#' @param NASCDistr	Assumed distribution of mean NASC values, "normal", "lognormal", "gamma", or "weibull" ("normal" is default).
#' 
#' @return list with mean(NASC) and var(NASC) per strata, global (stratified) mean, SE and RSE (Relative standard error) of NASC
#'
#' @examples
#' # Create the test project:
#' createProject("Test_Rstox", files=system.file("extdata", "Test_Rstox", package="Rstox"), ow=TRUE)
#' projectName <- "Test_Rstox"
#' psuNASC <- getPSUNASC(projectName)
#' stratumNASC <- getNASCDistr(projectName, psuNASC=psuNASC, NASCDistr="normal")
#'
#' @importFrom MASS fitdistr
#'
#' @export
#' 
getNASCDistr <- function(baseline, psuNASC, NASCDistr="observed"){
	# Test the presence of acoustic data:
	if(length(psuNASC)==0){
		warning(paste0("Process with function MeanNASC missing in project \"", getProjectPaths(baseline)$projectName, "\""))
		return(NULL)
	}
	
	#stratumArea <- getDataFrame1(baseline, 'StratumArea')
	stratumArea <- getBaseline(baseline, fun="StratumArea", input=FALSE, msg=FALSE)
	names(stratumArea)[1] <- "Stratum"
	tmp <- psuNASC
	if(psuNASC$LayerType[1]!="WaterColumn") {
		tmp <- aggPSUNASC(psuNASC)
	}
	# Estimates strata mean and variance:
	tmp2 <- split(tmp,list(tmp$Stratum))
	strata.mean <- sapply(tmp2,function(yy) {wtd.strata.est(yy$Value,yy$dist)$strata.mean})
	strata.mean[is.na(strata.mean)] <- 0 # If no NASC-values, mean is 0
	strata.var <- sapply(tmp2,function(yy) {wtd.strata.est(yy$Value,yy$dist)$strata.var})
	strata.var[is.na(strata.var)] <- 0 # If no NASC-values, var is 0
	n.by.strata <- sapply(tmp2,function(yy) {length(yy$Value)})
	
	## Put in function that tests data before this step!
	switch(NASCDistr,
		 observed = distr.fit <- sapply(tmp2,function(yy) {c('mean' = NA, 'sd' = NA)}),
		 normal = distr.fit <- sapply(tmp2,function(yy) {coef(fitdistr(yy$Value, 'normal'))}),
		 lognormal = distr.fit <-	sapply(tmp2,function(yy) {coef(fitdistr(yy$Value+1, 'lognormal'))}),
		 gamma = distr.fit <-	sapply(tmp2,function(yy) {if(length(yy$Value>0)>5) coef(fitdistr(yy$Value+1, 'gamma')) else c(shape=0,rate=1)}),
		 weibull = distr.fit <-	sapply(tmp2,function(yy) {if(length(yy$Value>0)>5) coef(fitdistr(yy$Value+1, 'weibull',lower=c(0.001,0.001)))	else c(shape=0,scale=1)})
	)
	tmp3 <- as.data.frame(cbind(strata.mean, strata.var, n.by.strata, distr.fit[1,], distr.fit[2,]))
	names(tmp3)[4:5]<-row.names(distr.fit)
	tmp3$Stratum <- row.names(tmp3)
	tmp3 <- merge(tmp3, stratumArea, by="Stratum")
	tmp3$w <- tmp3$Area/sum(tmp3$Area)
	
	# Estimates stratified mean and standard error:
	# Global mean 
	mean.tot.NASC <- sum(tmp3$strata.mean * tmp3$w) 
	
	#Global SE
	SE.tot.NASC <- sqrt(sum(tmp3$w^2 * tmp3$strata.var))
	
	#Global RSE
	RSE.tot.NASC <- round(100 * SE.tot.NASC / mean.tot.NASC,2)
	print(paste("Global RSE of NASC is",RSE.tot.NASC, "%" ))
	
	out <- list(NASC.by.strata=tmp3,	NASC.tot.mean=mean.tot.NASC, NASC.tot.SE=SE.tot.NASC, NASC.tot.RSE=RSE.tot.NASC, NASCDistr=NASCDistr)
}


#*********************************************
#*********************************************
#' Resample NASC distribution
#'
#' Generates resampled NASC distribution.
#'
#' @param baseline		StoX Java baseline object.
#' @param psuNASC		MeanNASC table from baseline.
#' @param stratumNASC	Strata NASC estimates from getNASCDistr(baseline).
#' @param parameters	Parameters set by user in Stox;
#'			parameters$nboot: Number of bootstrap replicates
#'			parameters$seed: The seed for the random number generator (used for reproducibility)
#'
#' @return Matrix of resampled strata NASC means 
#'
#' @examples
#' # Create the test project:
#' createProject("Test_Rstox", files=system.file("extdata", "Test_Rstox", package="Rstox"), ow=TRUE)
#' projectName <- "Test_Rstox"
#' psuNASC <- getPSUNASC(projectName)
#' stratumNASC <- getNASCDistr(projectName, psuNASC=psuNASC, NASCDistr="normal")
#' resampledNASC <- getResampledNASCDistr(projectName, psuNASC=psuNASC, stratumNASC=stratumNASC, parameters=list(seed=1, nboot=5))
#'
#' @export
#' 
getResampledNASCDistr <- function(baseline, psuNASC, stratumNASC, parameters){
	# Test the presence of acoustic data:
	if(length(psuNASC)==0){
		warning(paste0("Process with function MeanNASC missing in project \"", getProjectPaths(baseline)$projectName, "\""))
		return(NULL)
	}
	
	if(stratumNASC$NASCDistr=="observed"){
		tmpNASC <- psuNASC
		if(psuNASC$LayerType[1]!="WaterColumn"){
			tmpNASC <- aggPSUNASC(psuNASC)
		}
		set.seed(if(isTRUE(parameters$seed)) 1234 else if(is.numeric(parameters$seed)) parameters$seed else NULL) # seed==TRUE giving 1234 for compatibility with older versions
		SeedV <- sample(c(1:10000000), parameters$nboot, replace = FALSE) # Makes seed vector for fixed seeds (for reproducibility).
		tmp2NASC <- split(tmpNASC,list(tmpNASC$Stratum))
		res.NASC.dist <- matrix(NA,nrow=parameters$nboot,ncol=length(unique(tmpNASC$Stratum)))
		for(i in 1:parameters$nboot){
			# Function used for sampling the PSUs:
			reest <- function(yy){
				set.seed(SeedV[i])
				# Change made by Holmin 2016-08-26: Initiated after warnings from Are Salthaug that the variance in the boostrap estimates were unexpectedly low. An error was found in this function, where the variable formerly named "PSU" has been renamed to "SampleUnit" in the Java code, but this has not been updated here. 
				#if(length(yy$PSU)){
					#	tID <- sample(yy$PSU, length(yy$PSU), replace = TRUE) # Resample NASC
					#	yy2 <- yy[match(tID,yy$PSU), ]
					#}
					#else{
				tID <- sample(yy$SampleUnit, length(yy$SampleUnit), replace = TRUE) # Resample NASC
				yy2 <- yy[match(tID,yy$SampleUnit), ]
				#}
				resmean <- wtd.strata.est(yy2$Value,yy2$dist)$strata.mean
			}
		
			# Estimates strata mean and variance:
			strata.mean <- sapply(tmp2NASC, reest)
			strata.mean[is.na(strata.mean)] <- 0 # If no NASC-values, mean is 0
			res.NASC.dist[i,] <- strata.mean
			if(i==1){
				colnames(res.NASC.dist) <- names(strata.mean)
			}
		}
	}
	
	tmp <- split(stratumNASC$NASC.by.strata,list(stratumNASC$NASC.by.strata$Stratum))
	set.seed(if(isTRUE(parameters$seed)) 1234 else if(is.numeric(parameters$seed)) parameters$seed else NULL) # seed==TRUE giving 1234 for compatibility with older versions
	switch(stratumNASC$NASCDistr,
		 normal = res.NASC.dist <- sapply(tmp, function(yy) {rnorm(parameters$nboot, mean=yy$strata.mean, sd=sqrt(yy$strata.var))}),
		 lognormal = res.NASC.dist <- sapply(tmp, function(yy) {rlnorm(parameters$nboot, meanlog=yy$meanlog, sdlog=yy$sdlog)-1}),
		 gamma = res.NASC.dist <- sapply(tmp, function(yy) {rgamma(parameters$nboot, shape=yy$shape, rate=yy$rate)-1}),
		 weibull = res.NASC.dist <- sapply(tmp, function(yy) {rweibull(parameters$nboot, shape=yy$shape, scale=yy$scale)-1})
	)
	res.NASC.dist <- ifelse(res.NASC.dist<0, 0, res.NASC.dist) # Note this assumption! Negative values are set to zero
	out <- res.NASC.dist
}
