#Plan:
# Build search strings functions sea2r master funksjon som lagar ei xml fil







######################
setwd('C:/repositories/StoX_r')

# NB you need to install RCurl
#install.packages('RCurl')

# Source Sea2R package
source('Sea2R.r')

#Help file: http://crius.nodc.no/wiki/doku.php?id=api:nmdapi 

#
# Biotic examples
#

#
# Extract xml files from NMD topic databases
#

# Get mission data


# Get biotic data by mission (tokt)
xmlfile_out<-'C:/repositories/StoX_r/test.xml'
cruisecode<-2014203
url_in<-'http://tomcat7.imr.no:8080'
sea2r.get.nmdbiotic.bymission(url_in,xmlfile_out,cruisecode)

# Get biotic data by query
sea2r('NMDbiotic',parameters)

str<-paste(url_in,'/apis/nmdapi/v1/api/nmdbiotic/query?query=year=2014&format=xml',sep="")

# Get data
nmdbiotic_xml = getURLContent(str)

str
get('station',[1:10],'whatever','sunshine')



#
# 
#