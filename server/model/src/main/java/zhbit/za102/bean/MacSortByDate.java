package zhbit.za102.bean;

import java.io.Serializable;
import java.util.Date;

public class MacSortByDate implements Serializable {
    private Integer rssi;
    private Date time;
    private String machineId;
    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}
