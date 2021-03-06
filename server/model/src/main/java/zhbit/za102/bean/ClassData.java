package zhbit.za102.bean;

import java.io.Serializable;
import java.util.Date;

public class ClassData implements Serializable {
    private Integer id;

    private String adress;

    private Integer newStudent;

    private Integer inClassNumber;

    private Integer jumpOut;

    private Integer classNowNumber;

    private Integer hourClassNumber;

    private Integer hourInClassNumber;

    private Date updatetime;

    private Integer hours;

    private String indoorname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress == null ? null : adress.trim();
    }

    public Integer getNewStudent() {
        return newStudent;
    }

    public void setNewStudent(Integer newStudent) {
        this.newStudent = newStudent;
    }

    public Integer getInClassNumber() {
        return inClassNumber;
    }

    public void setInClassNumber(Integer inClassNumber) {
        this.inClassNumber = inClassNumber;
    }

    public Integer getJumpOut() {
        return jumpOut;
    }

    public void setJumpOut(Integer jumpOut) {
        this.jumpOut = jumpOut;
    }

    public Integer getClassNowNumber() {
        return classNowNumber;
    }

    public void setClassNowNumber(Integer classNowNumber) {
        this.classNowNumber = classNowNumber;
    }

    public Integer getHourClassNumber() {
        return hourClassNumber;
    }

    public void setHourClassNumber(Integer hourClassNumber) {
        this.hourClassNumber = hourClassNumber;
    }

    public Integer getHourInClassNumber() {
        return hourInClassNumber;
    }

    public void setHourInClassNumber(Integer hourInClassNumber) {
        this.hourInClassNumber = hourInClassNumber;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public String getIndoorname() {
        return indoorname;
    }

    public void setIndoorname(String indoorname) {
        this.indoorname = indoorname == null ? null : indoorname.trim();
    }

    @Override
    public String toString() {
        return "ClassData{" +
                "id=" + id +
                ", adress='" + adress + '\'' +
                ", newStudent=" + newStudent +
                ", inClassNumber=" + inClassNumber +
                ", jumpOut=" + jumpOut +
                ", classNowNumber=" + classNowNumber +
                ", hourClassNumber=" + hourClassNumber +
                ", hourInClassNumber=" + hourInClassNumber +
                ", updatetime=" + updatetime +
                ", hours=" + hours +
                ", indoorname='" + indoorname + '\'' +
                '}';
    }
}