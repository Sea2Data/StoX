#*********************************************
#*********************************************
#' Run demo of Rstox
#'
#' This function runs a demo of the workflow of a StoX project run from R with the package Rstox. All code run in the function can be found in the examples.
#'
#' @examples
#' # Create a StoX project in the StoX "Workspace" directory containing the example files in the Rstox:
#' proj <- createProject("Test_Rstox", acoustic=system.file("extdata", "Test_Rstox", package="Rstox"), ow=FALSE)
#' # Open the project (producing the same output as from \code{createProject}):
#' proj <- openProject("Test_Rstox")
#' proj
#'
#' @export
#' 
Rstox.demo <- function(){
  projectName <- "Test_Rstox"
  # Create a StoX project in the StoX "Workspace" directory containing the example files in the Rstox:
  proj <- createProject("Test_Rstox", acoustic=system.file("extdata", "Test_Rstox", package="Rstox"))
  # Run the baseline model of the project:
  proj <- runBaseline(projectName)
  # Display the mean nautical area scattering (NASC) values per sampling unit:
  psuNASC <- getDataFrame1(proj, 'MeanNASC')
  # Bootstrap the baseline model:
  bootstrap_Acoustic <- runBootstrap(projectName, type="Acoustic", startProcess="TotalLengthDist", endProcess="SuperIndAbundance", numIterations=50, seed=1, cores=1, NASCDistr="observed")
  # Save the bootstrap data:
  saveRImage(projectName)
  
  # Plot the NASC distribution:
  plotNASCDistribution(projectName)
  # In the project "Test_Rstox" there are missing cells, which are imputed here (time demanding process, but faster when setting cores>1):
  system.time(bootstrap_Acoustic_imputed <- imputeByAge(projectName, type="Acoustic", cores=1))
  # Save also the imputed data sets:
  saveRImage(projectName, fileName="ImputeByAge.RData", outputFolder="report")
  # Plot the abundance:
  plotAbundance(projectName, type="Acoustic", grp1="age", numberscale=1000000)
}
