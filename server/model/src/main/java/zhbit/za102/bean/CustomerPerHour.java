package zhbit.za102.bean;

public class CustomerPerHour {  //存小时人流数据的实体（不用缓存不必序列化）
    /**
     * hour_customer_number 每个小时的人流量
     */
    private Integer hour_class_number;
    /**
     * hours 每个小时的小时数
     */
    private Integer hours;

    public Integer getHour_class_number() {
        return hour_class_number;
    }

    public void setHour_class_number(Integer hour_class_number) {
        this.hour_class_number = hour_class_number;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }
}
