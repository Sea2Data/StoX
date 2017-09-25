require(utils)
require(rJava)
 
# include rstox.r - in future require a package rstox
source('~/r/rstox.r', chdir=TRUE)

# For SVN project
# Connect to rstox jar:
# Note: ~ refers to Sys.getenv("HOME")
# Can also use ~/../workspace/stox/r/rstox.jar
jar <- .jinit(classpath="~/r/rstox.jar", force.init=TRUE) 

# acquire model
model <- rStoX.acquireModel("Tobis-2013842-test")

# Run the model
model$run(jInt(1), jInt(model$getProcessList()$size()), jBoolean(FALSE))

# check the result
abnd <- rStoX.getDataFrame1(model, 'AbundanceByLength')
head(abnd)

# trawlassigments as dataframe
ta <- rstox.getProcessDataTableAsDataFrame(model, 'TRAWLASSIGNMENT')
head(ta)

# Get assignments for psu and stratum
asg <- rstox.processdata.getassignments(model)
ta_table <- model$findProcess("ProcessData")$getOutput()$getData()$getMatrix("TRAWLASSIGNMENT")

#Remove assignment
removeAssignment(ta_table, asg[1,1], asg[1,5], asg[1,6])
setAssignment(ta_table, asg[1,1], asg[1,5], asg[1,6], asg[1,7])

#Replace assignment by vector
setAssignments(ta_table, asg)


# Run the model from BioStationWeighting to AbundanceByIndividuals
idx.start <- model$getProcessList()$indexOf(model$findProcess("TotalLengthDist")) + 1
idx.stop <- model$getProcessList()$indexOf(model$findProcess("AbundanceByIndividuals")) + 1

# Loop the runs
for(i in 1 : 100) { 
  # Perform sampling drawing and replacement...
  # Run the sub model
  model$run(jInt(idx.start), jInt(idx.stop), jBoolean(FALSE)) 
  # Store the result
}

# mean sa table
meanSA_table <- rStoX.getDataFrame1(model, 'MeanSAPSU')
meanSA_table$Value = meanSA_table$Value * 0.75
head(meanSA_table)
meansamatrix <- model$findProcess("MeanSAPSU")$getOutput()$getData()

# Replace mean sa values in the matrix by using dataframe
setMeanSAValues(meansamatrix, meanSA_table)


