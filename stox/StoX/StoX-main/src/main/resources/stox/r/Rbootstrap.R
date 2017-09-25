#--------------------------------------------
# R Bootstrap example for StoX, run from R.
# Author: Gjert E. Dingsør, 2014, IMR, gjerted@imr.no
#--------------------------------------------

# include rstox.r - in future require a package rstox
source('rstox.r', chdir=TRUE)

fileName <- "./rprocess.txt"
.jinit("./rstox.jar")
#  # Read projectName from rprocess.txt and acquire model
projectName <- readRProcess()$projectName
set.boot.param.out <- readRProcess()
set.boot.param.out$nB # Number of bootstrap replicates
model <- rStoX.acquireModel(set.boot.param.out)
rStoX.runModel(model)# Run StoX baseline model

# Load data from baseline model in StoX
msa <- rstox.processdata.getmeanSA(model) # Retrieve sA data from baseline model
asg <- rstox.processdata.getassignments(model)   # Retrieve trawl assigment from baseline model
ssa <- calc.sA.dist(model, msa, sA.distr="normal") # Estimate global mean sA, based on Jolly & Hampton (1990)
rsa <- resample.sA(model, ssa=ssa, set.boot.param.out) # Resample global mean sA, for use in Bootstrap routine
# Run bootstrap and save to file
BootstrapTrawl.out <- BootstrapTrawl(model, asg, msa, ssa, rsa, start.process = "TotalLengthDist", end.process ="AbundanceByPopulationCategory", set.boot.param.out)
#system.time(BootstrapTrawl(model, asg, msa, ssa, rsa, start.process = "TotalLengthDist", end.process ="AbundanceByPopulationCategory", set.boot.param.out))
str(BootstrapTrawl.out)

#BootstrapTrawl.out
saveRImage(model) # Save R image to file for later use

# Produce reports and figures
plotSA(model, msa, rsa, set.boot.param.out)
plot.boot.abundance(model, BootstrapTrawl.out, set.boot.param.out, scale=1000000)
# to be continued...
