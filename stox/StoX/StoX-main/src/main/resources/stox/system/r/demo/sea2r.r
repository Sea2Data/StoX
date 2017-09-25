
library('RCurl')

s2r.get.nmdbiotic.bymission <- function(url_in,xmlfile_out,cruisecode){

# Generate search string
str<-paste(url_in,'/apis/nmdapi/v1/api/nmdbiotic/bymission?cruisecode=',cruisecode,'&format=xml',sep="")
# Get data
nmdbiotic_xml = getURLContent(str)
# Write XML file
fileConn<-file(xmlfile_out)
writeLines(nmdbiotic_xml,fileConn)
close(fileConn)

}

#s2r.get.nmdbiotic.query <- function(url,vararg){
#
#
#
#?query&format=xml'
#
#
## Parse xml file
#
#statements
#return(object)
#}
#
#s2r.get.nmdecho.bymission <- function(arg1, arg2, ... ){
#statements
#return(object)
#}
#
#
#s2r.get.nmdecho.query <- function(arg1, arg2, ... ){
#statements
#return(object)
#}
#
#
#s2r.get.nmdhub.bymission <- function(arg1, arg2, ... ){
#statements
#return(object)
#}
#
#
#s2r.get.nmdhub.query <- function(arg1, arg2, ... ){
#statements
#return(object)
#}
#
#s2r.get.nmdmission.bymission <- function(arg1, arg2, ... ){
#statements
#return(object)
#}
#
#
#s2r.get.nmdmission.query <- function(arg1, arg2, ... ){
#statements
#return(object)
#}
#
#