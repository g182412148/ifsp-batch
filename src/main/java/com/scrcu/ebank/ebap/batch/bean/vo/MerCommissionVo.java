package com.scrcu.ebank.ebap.batch.bean.vo;

import java.math.BigDecimal;

/**
 * @author ljy
 * @date 2018-12-24 18:10
 */
public class MerCommissionVo {

    // 入账日期
    private String inAcctDate ;

    // 返佣金额
    private BigDecimal commissionAmt;

    public String getInAcctDate() {
        return inAcctDate;
    }

    public void setInAcctDate(String inAcctDate) {
        this.inAcctDate = inAcctDate;
    }

    public BigDecimal getCommissionAmt() {
        return commissionAmt;
    }

    public void setCommissionAmt(BigDecimal commissionAmt) {
        this.commissionAmt = commissionAmt;
    }

}
