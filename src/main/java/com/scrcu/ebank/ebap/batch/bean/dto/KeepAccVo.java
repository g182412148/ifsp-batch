package com.scrcu.ebank.ebap.batch.bean.dto;

import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.common.beans.CommonDTO;

import java.math.BigDecimal;
import java.util.Map;

public class KeepAccVo extends CommonDTO {
    /**
     * 订单号
     */
    private String orderSsn;
    /**
     * 借方账户
     */
    private String outAccNo;
    /**
     * 贷方账户
     */
    private String inAccNo;
    /**
     * 记账金额
     */
    private Long transAmt;
    /**
     * 记账状态
     */
    private String state;

    /**
     * 子订单号
     */
    private String subOrderSsn;
    /**
     * 实时标志
     */
    private String realTmFlag;

    /**
     * 手续费
     */
    private BigDecimal feeAmt;

    /**
     * 返佣
     */
    private BigDecimal commissionAmt;

    /**
     * 核心柜员流水号
     */
    private String HDTellrSeqNum;



    public KeepAccVo(){

    }



    public KeepAccVo(KeepAccInfo keepAccInfo , Map<String, Object> results){
        this.orderSsn = keepAccInfo.getOrderSsn();
        this.outAccNo=keepAccInfo.getOutAccNo();
        this.inAccNo=keepAccInfo.getInAccNo();
        this.transAmt=keepAccInfo.getTransAmt();
        this.state=keepAccInfo.getState();
        this.subOrderSsn=keepAccInfo.getSubOrderSsn();
        this.realTmFlag=keepAccInfo.getRealTmFlag();
        this.feeAmt=keepAccInfo.getFeeAmt();
        this.commissionAmt=keepAccInfo.getCommissionAmt();
        if (IfspDataVerifyUtil.isNotBlank(results)&&IfspDataVerifyUtil.isNotBlank(results.get("hDTellrSeqNum"))){
            this.HDTellrSeqNum=String.valueOf(results.get("hDTellrSeqNum"));
        }
    }




    public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn;
    }

    public String getOutAccNo() {
        return outAccNo;
    }

    public void setOutAccNo(String outAccNo) {
        this.outAccNo = outAccNo;
    }

    public String getInAccNo() {
        return inAccNo;
    }

    public void setInAccNo(String inAccNo) {
        this.inAccNo = inAccNo;
    }

    public String getSubOrderSsn() {
        return subOrderSsn;
    }

    public void setSubOrderSsn(String subOrderSsn) {
        this.subOrderSsn = subOrderSsn;
    }

    public String getRealTmFlag() {
        return realTmFlag;
    }

    public void setRealTmFlag(String realTmFlag) {
        this.realTmFlag = realTmFlag;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public BigDecimal getCommissionAmt() {
        return commissionAmt;
    }

    public void setCommissionAmt(BigDecimal commissionAmt) {
        this.commissionAmt = commissionAmt;
    }

    public String getHDTellrSeqNum() {
        return HDTellrSeqNum;
    }

    public void setHDTellrSeqNum(String HDTellrSeqNum) {
        this.HDTellrSeqNum = HDTellrSeqNum;
    }

    public Long getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(Long transAmt) {
        this.transAmt = transAmt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    /** ----------------------------------------------转换------------------------------------------ */
    /**
     * 转换json
     *
     * @return
     */
    public String toJson() {
        return IfspFastJsonUtil.tojson(this);
    }

    /**
     * json转换bean
     *
     * @return
     */
    public static KeepAccVo convertjson(String json) {
        return IfspFastJsonUtil.jsonToobject(json, KeepAccVo.class);
    }

    /**
     * bean转换 map
     */
    public Map<String, Object> toMap() {
        return IfspFastJsonUtil.objectTomap(this);
    }

    /**
     * map转换bean
     */
    public static KeepAccVo convertMap(Map<String, Object> map) {
        return IfspFastJsonUtil.mapTobean(map, KeepAccVo.class);
    }

}
