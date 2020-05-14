package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BthMerInAccSumInfo extends CommonDTO {
    // 清算时间
    private String stlmDate;
    // 商户号
    private String merId;
    // 商户简称
    private String merSimpleName;
    // 结算账户
    private String setlAcctNo;
    // 订单金额
    private BigDecimal txnAmt;
    // 清算笔数
    private String txnCount;
    // 入账金额
    private BigDecimal inAcctAmt;
    // 入账状态
    private String inAcctStat;
    // 失败原因
    private String statMark;
    // 批次号
    private String batchNo;
    // 流水号
    private String txnSsn;
    private String otherSetlFee;

}
