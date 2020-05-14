package com.scrcu.ebank.ebap.batch.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ljy
 */
@Data
public class BthMerInAccSumInfoExtend extends BthMerInAccSumInfo{
    /**
     * 结算账户户名
     */
    private String setlAcctName;
    /**
     * 清算手续费
     */
    private BigDecimal feeAmt;
    /**
     * 结算账户类型
     */
    private String stlmAcctType;
    /**
     * 营销金额(总)
     */
    private BigDecimal sumCouponAmt;
    /**
     * 营销优惠金额(银行)
     */
    private BigDecimal sumBankCouponAmt;
    /**
     * 营销补贴金额(商户)
     */
    private BigDecimal sumMchtCouponAmt;

}
