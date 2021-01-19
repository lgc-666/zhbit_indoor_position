package zhbit.za102.bean;

import java.io.Serializable;

public class Logrecord implements Serializable {
    private Integer logid;

    private String id;

    private String cardid;

    private String changevalue;

    private String gentime;

    public Integer getLogid() {
        return logid;
    }

    public void setLogid(Integer logid) {
        this.logid = logid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid == null ? null : cardid.trim();
    }

    public String getChangevalue() {
        return changevalue;
    }

    public void setChangevalue(String changevalue) {
        this.changevalue = changevalue == null ? null : changevalue.trim();
    }

    public String getGentime() {
        return gentime;
    }

    public void setGentime(String gentime) {
        this.gentime = gentime == null ? null : gentime.trim();
    }
}