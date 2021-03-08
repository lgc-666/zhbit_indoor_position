package zhbit.za102.bean;

import java.io.Serializable;

public class map_mamage implements Serializable {
    private Integer id;

    private String fmapid;

    private String indoorname;

    private String longitude;

    private String latitude;

    private String charge;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFmapid() {
        return fmapid;
    }

    public void setFmapid(String fmapid) {
        this.fmapid = fmapid == null ? null : fmapid.trim();
    }

    public String getIndoorname() {
        return indoorname;
    }

    public void setIndoorname(String indoorname) {
        this.indoorname = indoorname == null ? null : indoorname.trim();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? null : longitude.trim();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? null : latitude.trim();
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge == null ? null : charge.trim();
    }
}