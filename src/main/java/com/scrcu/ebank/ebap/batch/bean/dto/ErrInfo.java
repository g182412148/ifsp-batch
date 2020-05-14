package com.scrcu.ebank.ebap.batch.bean.dto;

import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.common.beans.CommonDTO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @CopyRightInformation : 数云
 * @Prject: 数云PMS
 * @author: sun_b
 * @date: 2020/5/9
 */
public class ErrInfo extends CommonDTO {
    private String id = IfspId.getUUID32();
    private String errAcctNo;//交易账号（差错文件）
    private String orderSsn;
    private String subOrderSsn;
    private String txnSsn;
    private Date orderTm;
    private String errChlnNo;//差错中心交易渠道
    private String errTranType;//差错中心交易类型
    private String mchtOrgId;//收单机构号
    private String opOrgId;//运营机构号
    private String coreAccSsn;//核心记账流水
    private Date coreAccTm;//核心记账时间
    private BigDecimal coreAccAmt;//核心记账金额
    private String chkRst;//对账结果 1本方少账  2本方多账
    private String errType;//差错类别（是否结算，退款以原订单为准） 1延时（否） 空-实时（是）
    private String feeredFlag;//手续费垫付标志

    private String liqAcctNo;//商户待清算账户
    private String settlAcctNo;//商户结算账户
    private String settlAcctType;//结算账户类型 0本行  1他行
    private BigDecimal orderAmt;//订单金额
    private BigDecimal payAmt;//支付金额
    private BigDecimal bankCouponAmt;//银行营销金额
    private BigDecimal inFeeAmt;//手续费
    private BigDecimal settlAmt;//结算金额
    private BigDecimal pagyFeeAmt;//渠道手续费

    private String errReturnRst;//差错中心返回结果  00成功  01失败
    private String erratchMFlag;//err文件勾连标志  0未勾连 1勾连  空-初始化
    private String orderTranType;//订单交易类型 00消费  10退款
    private String pagyType;//订单支付类型  00银联 10微信 20支付宝 99银联err文件
    private String errState = "00";//差错状态  00未处理  01已处理  02已推送
    private String errAccState = "00";//差错记账状态   00 待记账 01.记账中 02.记账成功 03.记账失败
    private String localTxnState;// 00本地成功
    private String outerTxnState;// 00三方成功

    /**退款 数据初始化*/
    public void refundInit(){
        this.errChlnNo =  "10";
        this.errTranType =  "63";
        this.chkRst = "1";
        this.feeredFlag = "3";
        this.errType = "";
    }
    /**消费 数据初始化*/
    public void payInit(){
        this.errChlnNo =  "10";
        this.errTranType =  "61";
        boolean a = this.localTxnState == "00";
        boolean b = this.outerTxnState != "00";
        if("00".equals(this.localTxnState) && !"00".equals(this.outerTxnState)){//本地单边
            this.chkRst = "2";
        }else if(!"00".equals(this.localTxnState) && "00".equals(this.outerTxnState)){ //三方单边
            this.chkRst = "1";
        }
        this.feeredFlag = "4";
    }

    /**待清算账户 结算账户*/
    public void initCont(MchtContInfo cont){
        this.liqAcctNo = cont.getLiqAcctNo();
        this.settlAcctNo = cont.getSettlAcctNo();
        if(this.errType == "1"){
            this.errAcctNo = this.liqAcctNo;
            this.settlAcctType = "0";
        }else {
            this.errAcctNo = this.settlAcctNo;
            this.settlAcctType = cont.getSettlAcctType();
        }
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getOrderSsn() {
        return orderSsn;
    }
    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn;
    }
    public String getSubOrderSsn() {
        return subOrderSsn;
    }
    public void setSubOrderSsn(String subOrderSsn) {
        this.subOrderSsn = subOrderSsn;
    }
    public String getTxnSsn() {
        return txnSsn;
    }
    public void setTxnSsn(String txnSsn) {
        this.txnSsn = txnSsn;
    }
    public Date getOrderTm() {
        return orderTm;
    }
    public void setOrderTm(Date orderTm) {
        this.orderTm = orderTm;
    }
    public String getLiqAcctNo() {
        return liqAcctNo;
    }
    public void setLiqAcctNo(String liqAcctNo) {
        this.liqAcctNo = liqAcctNo;
    }
    public String getSettlAcctNo() {
        return settlAcctNo;
    }
    public void setSettlAcctNo(String settlAcctNo) {
        this.settlAcctNo = settlAcctNo;
    }
    public BigDecimal getOrderAmt() {
        return orderAmt;
    }
    public void setOrderAmt(BigDecimal orderAmt) {
        this.orderAmt = orderAmt;
    }
    public BigDecimal getPayAmt() {
        return payAmt;
    }
    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }
    public BigDecimal getBankCouponAmt() {
        return bankCouponAmt;
    }
    public void setBankCouponAmt(BigDecimal bankCouponAmt) {
        this.bankCouponAmt = bankCouponAmt;
    }
    public BigDecimal getInFeeAmt() {
        return inFeeAmt;
    }
    public void setInFeeAmt(BigDecimal inFeeAmt) {
        this.inFeeAmt = inFeeAmt;
    }
    public BigDecimal getSettlAmt() {
        return settlAmt;
    }

    public void setSettlAmt(BigDecimal settlAmt) {
        this.settlAmt = settlAmt;
    }

    public String getErrChlnNo() {
        return errChlnNo;
    }

    public void setErrChlnNo(String errChlnNo) {
        this.errChlnNo = errChlnNo;
    }

    public String getErrTranType() {
        return errTranType;
    }

    public void setErrTranType(String errTranType) {
        this.errTranType = errTranType;
    }

    public String getMchtOrgId() {
        return mchtOrgId;
    }

    public void setMchtOrgId(String mchtOrgId) {
        this.mchtOrgId = mchtOrgId;
    }

    public String getOpOrgId() {
        return opOrgId;
    }

    public void setOpOrgId(String opOrgId) {
        this.opOrgId = opOrgId;
    }

    public String getCoreAccSsn() {
        return coreAccSsn;
    }

    public void setCoreAccSsn(String coreAccSsn) {
        this.coreAccSsn = coreAccSsn;
    }

    public String getChkRst() {
        return chkRst;
    }

    public void setChkRst(String chkRst) {
        this.chkRst = chkRst;
    }

    public String getErrType() {
        return errType == null?"":errType;
    }

    public void setErrType(String errType) {
        this.errType = errType;
    }

    public String getFeeredFlag() {
        return feeredFlag;
    }

    public void setFeeredFlag(String feeredFlag) {
        this.feeredFlag = feeredFlag;
    }

    public String getErrReturnRst() {
        return errReturnRst;
    }

    public void setErrReturnRst(String errReturnRst) {
        this.errReturnRst = errReturnRst;
    }

    public String getErratchMFlag() {
        return erratchMFlag;
    }

    public void setErratchMFlag(String erratchMFlag) {
        this.erratchMFlag = erratchMFlag;
    }

    public String getOrderTranType() {
        return orderTranType;
    }

    public void setOrderTranType(String orderTranType) {
        this.orderTranType = orderTranType;
    }

    public String getPagyType() {
        return pagyType;
    }

    public void setPagyType(String pagyType) {
        this.pagyType = pagyType;
    }

    public String getErrState() {
        return errState;
    }

    public void setErrState(String errState) {
        this.errState = errState;
    }

    public String getErrAccState() {
        return errAccState;
    }

    public void setErrAccState(String errAccState) {
        this.errAccState = errAccState;
    }

    public String getLocalTxnState() {
        return localTxnState;
    }

    public void setLocalTxnState(String localTxnState) {
        this.localTxnState = localTxnState;
    }

    public String getOuterTxnState() {
        return outerTxnState;
    }

    public void setOuterTxnState(String outerTxnState) {
        this.outerTxnState = outerTxnState;
    }

    public String getErrAcctNo() {
        return errAcctNo;
    }

    public void setErrAcctNo(String errAcctNo) {
        this.errAcctNo = errAcctNo;
    }

    public Date getCoreAccTm() {
        return coreAccTm;
    }

    public void setCoreAccTm(Date coreAccTm) {
        this.coreAccTm = coreAccTm;
    }

    public BigDecimal getCoreAccAmt() {
        return coreAccAmt;
    }

    public void setCoreAccAmt(BigDecimal coreAccAmt) {
        this.coreAccAmt = coreAccAmt;
    }

    public BigDecimal getPagyFeeAmt() {
        return pagyFeeAmt;
    }

    public void setPagyFeeAmt(BigDecimal pagyFeeAmt) {
        this.pagyFeeAmt = pagyFeeAmt;
    }

    public String getSettlAcctType() {
        return settlAcctType;
    }

    public void setSettlAcctType(String settlAcctType) {
        this.settlAcctType = settlAcctType;
    }
}
