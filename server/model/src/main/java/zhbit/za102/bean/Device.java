package zhbit.za102.bean;

import java.io.Serializable;

public class Device implements Serializable {
    private Integer deviceid;

    private String devicename;

    private String id;

    private String devicetype;

    private String devicevalue;

    private String location;

    private String lasttime;

    private String gentime;

    private String owner;

    private String ip;

    private Integer port;

    private String indoorname;

    public Integer getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(Integer deviceid) {
        this.deviceid = deviceid;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename == null ? null : devicename.trim();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype == null ? null : devicetype.trim();
    }

    public String getDevicevalue() {
        return devicevalue;
    }

    public void setDevicevalue(String devicevalue) {
        this.devicevalue = devicevalue == null ? null : devicevalue.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime == null ? null : lasttime.trim();
    }

    public String getGentime() {
        return gentime;
    }

    public void setGentime(String gentime) {
        this.gentime = gentime == null ? null : gentime.trim();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIndoorname() {
        return indoorname;
    }

    public void setIndoorname(String indoorname) {
        this.indoorname = indoorname == null ? null : indoorname.trim();
    }
}