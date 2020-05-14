package com.scrcu.ebank.ebap.batch.bean.dto;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import lombok.Data;

import java.util.List;

@Data
public class DayTimeKeepAcctVo extends CommonDTO {

    /****************  调本行通道所需字段 ***********/
    private String orderSsn; // 订单号
    private String orderTm;  // 订单时间
    private String outAccNo; // 借方账户
    private String outAccType; // 借方账户类型
    /** 借方账户名,有就送 */
    private String outAccName;
    private String inAccNo;  // 贷方账户
    private String inAccType; // 贷方账户类型
    /** 贷方账户名,有就送 */
    private String inAccName;
    private String txnDesc; // 交易描述
    private String transAmt; // 交易金额
    private String txnCcyType; // 币种
    // 记账序号
    private String seq;
    /** 商户所在机构,有就送*/
    private String proxyOrg;

    /*************** 记账表额外需要字段 ************/
    private String pagySysNo; // 通道系统编号


    /**************  线上新增   *******************/
    /**
     * 子订单号
     */
    private String subOrderSsn;
    /**
     * 是否实时计算手续费标志  00 实时计算  01 非实时计算
     */
    private String realTmFlag;
    /**
     * 是否支持重跑标志  00 支持重跑  01 不支持重跑
     */
    private String rerunFlag;

    /**
     * 记账摘要
     */
    private String memo;


    /**
     * 唯一流水号
     */
    private String uniqueSsn;


    @Override
    public String toString() {
        return "DayTimeKeepAcctVo{" +
                "orderSsn='" + orderSsn + '\'' +
                ", orderTm='" + orderTm + '\'' +
                ", outAccNo='" + outAccNo + '\'' +
                ", outAccType='" + outAccType + '\'' +
                ", outAccName='" + outAccName + '\'' +
                ", inAccNo='" + inAccNo + '\'' +
                ", inAccType='" + inAccType + '\'' +
                ", inAccName='" + inAccName + '\'' +
                ", txnDesc='" + txnDesc + '\'' +
                ", transAmt='" + transAmt + '\'' +
                ", txnCcyType='" + txnCcyType + '\'' +
                ", seq='" + seq + '\'' +
                ", proxyOrg='" + proxyOrg + '\'' +
                ", pagySysNo='" + pagySysNo + '\'' +
                ", subOrderSsn='" + subOrderSsn + '\'' +
                ", realTmFlag='" + realTmFlag + '\'' +
                ", rerunFlag='" + rerunFlag + '\'' +
                ", memo='" + memo + '\'' +
                ", uniqueSsn='" + uniqueSsn + '\'' +
                '}';
    }
}
