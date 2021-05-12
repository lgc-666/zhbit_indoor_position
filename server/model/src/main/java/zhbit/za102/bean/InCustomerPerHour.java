package zhbit.za102.bean;

public class InCustomerPerHour {
    /**
     * hour_in_customer_number 每小时的进店量
     */
    private Integer hour_in_class_number;
    /**
     * hours 每个小时的小时数
     */
    private Integer hours;

    public Integer getHour_in_class_number() {
        return hour_in_class_number;
    }

    public void setHour_in_class_number(Integer hour_in_class_number) {
        this.hour_in_class_number = hour_in_class_number;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }
}
