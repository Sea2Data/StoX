#*********************************************
# Test Acoustic rmodel without rprocess script. 
# projectName: The project name to be used.
#*********************************************
testModel <- function(projectName) {
  getParameters.out <- getParameters(projectName="IESNS2014", numIterations=50, seed=TRUE)
  getBaseline.out <- getBaseline(parameters=getParameters.out)
  runBaseline.out <- runBaseline(baseline=getBaseline.out)
  getPSUNASC.out <- getPSUNASC(baseline=getBaseline.out)
  getBioticAssignments.out <- getBioticAssignments(baseline=getBaseline.out)
  getNASCDistr.out <- getNASCDistr(baseline=getBaseline.out, psuNASC=getPSUNASC.out, NASCDistr="normal")
  getResampledNASCDistr.out <- getResampledNASCDistr(baseline=getBaseline.out, stratumNASC=getNASCDistr.out, parameters=getParameters.out)
  bootstrapBioticAcoustic.out <- bootstrapBioticAcoustic(baseline=getBaseline.out, assignments=getBioticAssignments.out, psuNASC=getPSUNASC.out, stratumNASC=getNASCDistr.out, resampledNASC=getResampledNASCDistr.out, startProcess="TotalLengthDist", endProcess="AbundanceByIndividuals", parameters=getParameters.out)
  saveRImage.out <- saveRImage(baseline=getBaseline.out)
  # Plot results

  reportEnv = loadEnv("./output/rmodel/rmodel.RData")
  plotNASCDistribution(reportEnv, "getPSUNASC.out", "getResampledNASCDistr.out", "getParameters.out")
  plotAbundanceByAge(reportEnv, "bootstrapBioticAcoustic.out", "getParameters.out", 1000000, distAbnd = TRUE)
}


#*********************************************
# Test Swept Area rmodel without rprocess script. 
# projectName: The project name to be used.
# Ikke ferdig med denne enda!!!
#*********************************************
testModel_SweptArea <- function(projectName) {
  parameters <- getParameters(projectName, as.integer(100), TRUE)
  baseline <- getBaseline(parameters)
  runBaseline(baseline)
  assignments <- getBioticAssignments(baseline) 
  startProcess = "TotalLengthDist";
  endProcess ="SuperIndAbundance"
  bootstrapBioticSweptArea.out <- bootstrapBioticSweptArea(baseline, assignments, startProcess, endProcess, parameters)
  saveRImage(baseline) 
  numberscale<-1000
  plotAbundanceByAge("./output/rmodel/rmodel.RData", "bootstrapBioticSweptArea.out", "getParameters.out", numberscale)
}