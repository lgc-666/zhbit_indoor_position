package zhbit.za102.bean;

public class SumData {  //当日总值统计
    /**
     * 当日室内客流人数
     */
    private Integer sum_in;
    /**
     * 当日室内总新增
     */
    private Integer sum_new;

    public Integer getSum_in() {
        return sum_in;
    }

    public void setSum_in(Integer sum_in) {
        this.sum_in = sum_in;
    }

    public Integer getSum_new() {
        return sum_new;
    }

    public void setSum_new(Integer sum_new) {
        this.sum_new = sum_new;
    }
}
