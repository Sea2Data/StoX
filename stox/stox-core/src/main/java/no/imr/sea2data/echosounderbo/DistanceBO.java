package no.imr.sea2data.echosounderbo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import no.imr.sea2data.imrbase.map.ILatLonEvent;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.jts.FeaturePojo;

/**
 * Business object containing information about acoustic distances. Usually
 * contained in as a list in EchosounderDatasetBO
 *
 * @author aasmunds
 */
@FeaturePojo(idField = "id", geometryField = "line", ignoreMethods = {"frequencies", "echosounderDatasetBO", "buildLine"})
public class DistanceBO implements Serializable, ILatLonEvent {

    private String id;
    private Double log_start;
    private Date start_time;
    private Date stop_time;
    private Double integrator_dist;
    private Double pel_ch_thickness;
    private Double bot_ch_thickness;
    private Integer include_estimate;
    private Double lat_start;
    private Double lon_start;
    private Double lat_stop = null;
    private Double lon_stop = null;
    private List<FrequencyBO> frequencies = new ArrayList<FrequencyBO>();
    private EchosounderDatasetBO echosounderDatasetBO;
    private String cruise;
    private String platform;
    private String nation;
    private LineString line = null;
    private Point point = null;
    String key = null;

    /**
     * Default constructor
     */
    public DistanceBO() {
    }

    /**
     * Copy constructor
     *
     * @param bo
     */
    public DistanceBO(DistanceBO bo) {
        id = bo.getId();
        log_start = bo.getLog_start();
        start_time = bo.getStart_time();
        stop_time = bo.getStop_time();
        integrator_dist = bo.getIntegrator_dist();
        pel_ch_thickness = bo.getPel_ch_thickness();
        bot_ch_thickness = bo.getBot_ch_thickness();
        include_estimate = bo.getInclude_estimate();
        lat_start = bo.getLat_start();
        lon_start = bo.getLon_start();
        lat_stop = bo.getLat_stop();
        lon_stop = bo.getLon_stop();
        cruise = bo.getCruise();
        platform = bo.getPlatform();
        nation = bo.getNation();
    }

    /**
     * @return the log_start
     */
    public Double getLog_start() {
        return log_start;
    }

    /**
     * @param log_start the log_start to set
     */
    public void setLog_start(Double log_start) {
        this.log_start = log_start;
    }

    /**
     * @return the start_time
     */
    public Date getStart_time() {
        return start_time;
    }

    /**
     * @param start_time the start_time to set
     */
    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    /**
     * @return the stop_time
     */
    public Date getStop_time() {
        return stop_time;
    }

    /**
     * @param stop_time the stop_time to set
     */
    public void setStop_time(Date stop_time) {
        this.stop_time = stop_time;
    }

    /**
     * @return the integrator_dist
     */
    public Double getIntegrator_dist() {
        return integrator_dist;
    }

    /**
     * @param integrator_dist the integrator_dist to set
     */
    public void setIntegrator_dist(Double integrator_dist) {
        this.integrator_dist = integrator_dist;
    }

    /**
     * @return the pel_ch_thickness
     */
    public Double getPel_ch_thickness() {
        return pel_ch_thickness;
    }

    /**
     * @param pel_ch_thickness the pel_ch_thickness to set
     */
    public void setPel_ch_thickness(Double pel_ch_thickness) {
        this.pel_ch_thickness = pel_ch_thickness;
    }

    /**
     * @return the bot_ch_thickness
     */
    public Double getBot_ch_thickness() {
        return bot_ch_thickness;
    }

    /**
     * @param bot_ch_thickness the bot_ch_thickness to set
     */
    public void setBot_ch_thickness(Double bot_ch_thickness) {
        this.bot_ch_thickness = bot_ch_thickness;
    }

    /**
     * @return the include_estimate
     */
    public Integer getInclude_estimate() {
        return include_estimate;
    }

    /**
     * @param include_estimate the include_estimate to set
     */
    public void setInclude_estimate(Integer include_estimate) {
        this.include_estimate = include_estimate;
    }

    /**
     * @return the lat_start
     */
    public Double getLat_start() {
        return lat_start;
    }

    /**
     * @param lat_start the lat_start to set
     */
    public void setLat_start(Double lat_start) {
        this.lat_start = lat_start;
    }

    /**
     * @return the lon_start
     */
    public Double getLon_start() {
        return lon_start;
    }

    /**
     * @param lon_start the lon_start to set
     */
    public void setLon_start(Double lon_start) {
        this.lon_start = lon_start;
    }

    /**
     * @return the lat_stop
     */
    public Double getLat_stop() {
        return lat_stop;
    }

    /**
     * @param lat_stop the lat_stop to set
     */
    public void setLat_stop(Double lat_stop) {
        this.lat_stop = lat_stop;
    }

    /**
     * @return the lon_stop
     */
    public Double getLon_stop() {
        return lon_stop;
    }

    /**
     * @param lon_stop the lon_stop to set
     */
    public void setLon_stop(Double lon_stop) {
        this.lon_stop = lon_stop;
    }

    /**
     * @return the frequencies
     */
    public List<FrequencyBO> getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(List<FrequencyBO> frequencies) {
        this.frequencies = frequencies;
    }

    public void setEchosounderDataSet(EchosounderDatasetBO echosounderDatasetBO) {
        this.echosounderDatasetBO = echosounderDatasetBO;
    }

    public EchosounderDatasetBO getEchosounderDatasetBO() {
        return echosounderDatasetBO;
    }

    /*@Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.log_start != null ? this.log_start.hashCode() : 0);
        hash = 79 * hash + (this.start_time != null ? this.start_time.hashCode() : 0);
        hash = 79 * hash + (this.echosounderDatasetBO != null ? this.echosounderDatasetBO.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DistanceBO other = (DistanceBO) obj;
        if (this.log_start != other.log_start && (this.log_start == null || !this.log_start.equals(other.log_start))) {
            return false;
        }
        if (this.start_time != other.start_time && (this.start_time == null || !this.start_time.equals(other.start_time))) {
            return false;
        }
        if (this.echosounderDatasetBO != other.echosounderDatasetBO && (this.echosounderDatasetBO == null || !this.echosounderDatasetBO.equals(other.echosounderDatasetBO))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final DistanceBO other) {
        DistanceBO otherFr = (DistanceBO) other;
        return new CompareToBuilder()
                .append(this.echosounderDatasetBO, otherFr.echosounderDatasetBO)
                .append(this.start_time, otherFr.start_time)
                .append(this.log_start, otherFr.log_start)
                .toComparison();
    }*/
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    public Point getPoint() {

        if (point == null) {
            // Not good mid point on this mile we try to construct the start point instead
            if (((!(lat_start == null)) && (!(lon_start == null))) && ((lat_start > -90.0 && lat_start < 90.0)
                    && (lon_start > -180.0 && lon_start < 180.0))) {
                GeometryFactory geomFac = new GeometryFactory();
                point = geomFac.createPoint(new Coordinate(lon_start, lat_start));
            }
            return point;
        } else {
            return point;
        }

    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public LineString getLine() {
        return line;
    }

    public void buildLine() {

        if (okLat(lat_start) && okLat(lat_stop) && okLon(lon_start) && okLon(lon_stop)) {
            if ((Math.abs(lat_stop - lat_start) + Math.abs(lon_stop - lon_start)) > 0.0001) {
                GeometryFactory geomFac = new GeometryFactory();
                Coordinate[] coordinates = new Coordinate[2];
                coordinates[0] = new Coordinate(lon_start, lat_start);
                coordinates[1] = new Coordinate(lon_stop, lat_stop);
                line = geomFac.createLineString(coordinates);
            } else {
                line = null;
            }

        } else {
            line = null;
        }

    }

    private Boolean okLat(Double lat) {
        if (lat == null) {
            return Boolean.FALSE;
        }
        if (lat > -90 && lat < 90) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private Boolean okLon(Double lon) {
        if (lon == null) {
            return Boolean.FALSE;
        }
        if (lon > -180 && lon < 180) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public void setLine(LineString line) {
        this.line = line;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        if (key != null) {
            return key;
        }
        String res = "";
        if (cruise != null) {
            res += cruise;
        } else if (echosounderDatasetBO != null && echosounderDatasetBO.getCruise() != null) {
            res += echosounderDatasetBO.getCruise();
        }
        String logKey = "";
        if (log_start != null) {
            if (log_start % 1 == 0) {
                logKey = Integer.toString(log_start.intValue());
            } else {
                logKey = log_start.toString();
            }
        }
        key = res + "/" + logKey + "/" + (start_time != null ? IMRdate.formatDate(start_time, "yyyy-MM-dd/HH:mm:ss") : "");
        return key;
    }

    @Override
    public String toString() {
        return getKey();
    }

    public String getCruise() {
        return cruise;
    }

    public void setCruise(String cruise) {
        this.cruise = cruise;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    @Override
    public Double getStartLat() {
        return getLat_start();
    }

    @Override
    public Double getStartLon() {
        return getLon_start();
    }

    @Override
    public Double getStopLat() {
        return getLat_stop();
    }

    @Override
    public Double getStopLon() {
        return getLon_stop();
    }

}
