package com.scrcu.ebank.ebap.batch.bean.vo;

/**
 * 数据区间
 */
public class DataInterval {

    private Integer min;
    private Integer max;

    public DataInterval (Integer min, Integer max) {
        this.min = min;
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("数据区间:");
        return buffer.append(min).append("-").append(max).toString();
    }
}
