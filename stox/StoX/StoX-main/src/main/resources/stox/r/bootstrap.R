#--------------------------------------------
# R Bootstrap example for StoX. Run from StoX
# Author: Gjert E. Dingsør, 2014, IMR, gjerted@imr.no
#--------------------------------------------

# include rstox.r - in future require a package rstox
#install.packages("rJava")
#install.packages("Hmisc")
#install.packages("log4r")

.jinit('rstox.jar')
source('rstox.r', chdir=TRUE)
fileName <- "./rprocess.txt"
rstox.runProcessFile(fileName) # Running baseline model
# BootstrapTrawl.out # View bootstrap output
projectName <- readRProcess()$projectName # Put project name in an R object for later reference
saveRImage() # Saves R image to file for later use



# Test logger
logger <- create.logger()
# Set the logger's file output.
logfile(logger) <- 'base.log'
# Set the current level of the logger.
level(logger) <- 'DEBUG'
debug(logger, 'A Debugging Message')
