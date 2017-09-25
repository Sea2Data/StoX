#*********************************************
#*********************************************
#' Polygon area
#'
#' Calculate area of wkt polygon with az.eq.are projection / adapted origo.
#' 
#' @param wkt	String like "MULTIPOLYGON(((4.9 57.1, 5.00 57.23, 4.0 58.9)))
#' 
#' @return Area in nmi squared
#'
#' @examples
#' poly <- cbind(c(4.9, 5.00, 4.0, 4.9), c(57.1, 57.23, 58.9, 57.1))
#' plot(NULL, xlim=range(poly[,1]), ylim=range(poly[,2]), xlab="Longitude", ylab="Latitude")
#' polygon(poly, col="green")
#' polyArea(poly)
#'
#' @export
#' @import rgdal
#' @importFrom sp CRS spTransform proj4string
#' @importFrom rgeos readWKT gArea
#' 
polyArea <- function(wkt) {
	# We need rgdal when AreaMethod=Acurate in StratumArea!!!!
	
	if(is.numeric(wkt)){
		wkt <- paste0("MULTIPOLYGON(((", paste(apply(wkt, 1, paste, collapse=" "), collapse=", "), ")))")
	}
	p <- rgeos::readWKT(wkt)
	# Define projection for the wkt
	sp::proj4string(p) <- sp::CRS("+proj=longlat +ellps=WGS84")	
	# define the proj4 definition of Lambert Azimuthal Equal Area (laea) CRS with origo in wkt center:
	# Units: international nautical miles:
	laea.CRS<-CRS(paste0("+proj=laea +lat_0=",p@polygons[[1]]@labpt[2]," +lon_0=",p@polygons[[1]]@labpt[1],
		" +x_0=0 +y_0=0 +ellps=WGS84 +datum=WGS84 +units=kmi +no_defs"))
	# project data points from longlat to given laea
	p1 <- sp::spTransform(p, laea.CRS)
	sum(rgeos::gArea(p1, byid=T)) # Returns area
	# The result is very near compared known online geodesic planimeters (+- 0.001 naut.m)
}


