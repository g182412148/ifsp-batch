package com.scrcu.ebank.ebap.batch.bean.dto;

import com.ruim.ifsp.utils.datetime.DateUtil;
import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.ReRunFlagEnum;
import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;

public class KeepAccInfo extends CommonDTO {
    private String coreSsn;

    private String orderSsn;

    private String keepAccTime;

    private String state;

    private String keepAccType;

    private String inAccNo;

    private String inAccNoName;

    private String inAccType;

    private String outAccNo;

    private String outAccNoName;

    private String outAccType;

    private Long transAmt;

    private String chkSt;

    private String chkRst;

    private String orderTm;

    private String txnDesc;

    private String proxyOrg;

    private String txnCcyType;

    private Short keepAccSeq;

    private String pagyRespCode;

    private String pagyRespMsg;

    private String registerIp;

    private String reserved1;

    private String reserved2;

    private String dubiousFlag;

    private String isSync;

    private String asyncLoc;

    private String noticeStat;

    private Short batchNum;

    private String subOrderSsn;

    private String rerunFlag;

    private String realTmFlag;

    private BigDecimal feeAmt;

    private BigDecimal commissionAmt;

    private String memo;

    private String uniqueSsn;

    private String origCoreSsn;

    private String origKeepAccTime;

    private Short retryCount;

    private String verNo;

    private String isReaccount;

    public KeepAccInfo(){}


    /**
     * 根据原记账流水构造反向记账流水
     * @param revVo
     */
    public KeepAccInfo(KeepAccInfo revVo){
        // 核心流水
        this.coreSsn = IfspId.getUUID(20);
        // 订单号
        this.orderSsn = revVo.getOrderSsn();
        // 订单时间
        this.orderTm = revVo.getOrderTm();
        // 借方账户(反向)
        this.outAccNo = revVo.getInAccNo();
        // 借方账户名 (反向)
        this.outAccNoName = revVo.getInAccNoName();
        // 借方账户类型(反向)
        this.outAccType = revVo.getInAccType();
        // 贷方账户(反向)
        this.inAccNo = revVo.getOutAccNo();
        // 贷方账户名(反向)
        this.inAccNoName =revVo.getOutAccNoName();
        // 贷方账户类型(反向)
        this.inAccType = revVo.getOutAccType();
        // 记账时间
        this.keepAccTime = DateUtil.getYYYYMMDDHHMMSS();
        // 交易类型
        this.keepAccType = Constans.KEEP_ACC_TYPE_REVERSE;
        // 记账状态
        this.state = Constans.KEEP_ACCOUNT_STAT_PRE;
        // 交易描述
        this.txnDesc = "记账冲正";
        // 交易币种
        this.txnCcyType = revVo.getTxnCcyType();
        // 商户所在机构
        this.proxyOrg = revVo.getProxyOrg();
        // 子订单号
        this.subOrderSsn = revVo.getSubOrderSsn();
        // 不支持重跑
        this.rerunFlag = ReRunFlagEnum.RE_RUN_FLAG_FALSE.getCode();
        // 金额
        this.transAmt = revVo.getTransAmt();
        // 记账摘要
        this.memo = "扫码记账冲正";
        // 唯一索引
        this.uniqueSsn = IfspId.getUUID32();
        // 原核心记账交易流水
        this.origCoreSsn = revVo.getCoreSsn();
        // 原记账时间
        this.origKeepAccTime = revVo.getKeepAccTime();
        // 冲正尝试次数
        this.retryCount = 0;
        // 批次号
        this.verNo = revVo.getVerNo();
        // 保存记账的序号 , 冲正的时候以倒序冲
        this.keepAccSeq = revVo.getKeepAccSeq();

    }






    public String getCoreSsn() {
        return coreSsn;
    }

    public void setCoreSsn(String coreSsn) {
        this.coreSsn = coreSsn == null ? null : coreSsn.trim();
    }

    public String getOrderSsn() {
        return orderSsn;
    }

    public void setOrderSsn(String orderSsn) {
        this.orderSsn = orderSsn == null ? null : orderSsn.trim();
    }

    public String getKeepAccTime() {
        return keepAccTime;
    }

    public void setKeepAccTime(String keepAccTime) {
        this.keepAccTime = keepAccTime == null ? null : keepAccTime.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getKeepAccType() {
        return keepAccType;
    }

    public void setKeepAccType(String keepAccType) {
        this.keepAccType = keepAccType == null ? null : keepAccType.trim();
    }

    public String getInAccNo() {
        return inAccNo;
    }

    public void setInAccNo(String inAccNo) {
        this.inAccNo = inAccNo == null ? null : inAccNo.trim();
    }

    public String getInAccNoName() {
        return inAccNoName;
    }

    public void setInAccNoName(String inAccNoName) {
        this.inAccNoName = inAccNoName == null ? null : inAccNoName.trim();
    }

    public String getInAccType() {
        return inAccType;
    }

    public void setInAccType(String inAccType) {
        this.inAccType = inAccType == null ? null : inAccType.trim();
    }

    public String getOutAccNo() {
        return outAccNo;
    }

    public void setOutAccNo(String outAccNo) {
        this.outAccNo = outAccNo == null ? null : outAccNo.trim();
    }

    public String getOutAccNoName() {
        return outAccNoName;
    }

    public void setOutAccNoName(String outAccNoName) {
        this.outAccNoName = outAccNoName == null ? null : outAccNoName.trim();
    }

    public String getOutAccType() {
        return outAccType;
    }

    public void setOutAccType(String outAccType) {
        this.outAccType = outAccType == null ? null : outAccType.trim();
    }

    public Long getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(Long transAmt) {
        this.transAmt = transAmt;
    }

    public String getChkSt() {
        return chkSt;
    }

    public void setChkSt(String chkSt) {
        this.chkSt = chkSt == null ? null : chkSt.trim();
    }

    public String getChkRst() {
        return chkRst;
    }

    public void setChkRst(String chkRst) {
        this.chkRst = chkRst == null ? null : chkRst.trim();
    }

    public String getOrderTm() {
        return orderTm;
    }

    public void setOrderTm(String orderTm) {
        this.orderTm = orderTm == null ? null : orderTm.trim();
    }

    public String getTxnDesc() {
        return txnDesc;
    }

    public void setTxnDesc(String txnDesc) {
        this.txnDesc = txnDesc == null ? null : txnDesc.trim();
    }

    public String getProxyOrg() {
        return proxyOrg;
    }

    public void setProxyOrg(String proxyOrg) {
        this.proxyOrg = proxyOrg == null ? null : proxyOrg.trim();
    }

    public String getTxnCcyType() {
        return txnCcyType;
    }

    public void setTxnCcyType(String txnCcyType) {
        this.txnCcyType = txnCcyType == null ? null : txnCcyType.trim();
    }

    public Short getKeepAccSeq() {
        return keepAccSeq;
    }

    public void setKeepAccSeq(Short keepAccSeq) {
        this.keepAccSeq = keepAccSeq;
    }

    public String getPagyRespCode() {
        return pagyRespCode;
    }

    public void setPagyRespCode(String pagyRespCode) {
        this.pagyRespCode = pagyRespCode == null ? null : pagyRespCode.trim();
    }

    public String getPagyRespMsg() {
        return pagyRespMsg;
    }

    public void setPagyRespMsg(String pagyRespMsg) {
        this.pagyRespMsg = pagyRespMsg == null ? null : pagyRespMsg.trim();
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp == null ? null : registerIp.trim();
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1 == null ? null : reserved1.trim();
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2 == null ? null : reserved2.trim();
    }

    public String getDubiousFlag() {
        return dubiousFlag;
    }

    public void setDubiousFlag(String dubiousFlag) {
        this.dubiousFlag = dubiousFlag == null ? null : dubiousFlag.trim();
    }

    public String getIsSync() {
        return isSync;
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync == null ? null : isSync.trim();
    }

    public String getAsyncLoc() {
        return asyncLoc;
    }

    public void setAsyncLoc(String asyncLoc) {
        this.asyncLoc = asyncLoc == null ? null : asyncLoc.trim();
    }

    public String getNoticeStat() {
        return noticeStat;
    }

    public void setNoticeStat(String noticeStat) {
        this.noticeStat = noticeStat == null ? null : noticeStat.trim();
    }

    public Short getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Short batchNum) {
        this.batchNum = batchNum;
    }

    public String getSubOrderSsn() {
        return subOrderSsn;
    }

    public void setSubOrderSsn(String subOrderSsn) {
        this.subOrderSsn = subOrderSsn == null ? null : subOrderSsn.trim();
    }

    public String getRerunFlag() {
        return rerunFlag;
    }

    public void setRerunFlag(String rerunFlag) {
        this.rerunFlag = rerunFlag == null ? null : rerunFlag.trim();
    }

    public String getRealTmFlag() {
        return realTmFlag;
    }

    public void setRealTmFlag(String realTmFlag) {
        this.realTmFlag = realTmFlag == null ? null : realTmFlag.trim();
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    public String getUniqueSsn() {
        return uniqueSsn;
    }

    public void setUniqueSsn(String uniqueSsn) {
        this.uniqueSsn = uniqueSsn == null ? null : uniqueSsn.trim();
    }

    public String getOrigCoreSsn() {
        return origCoreSsn;
    }

    public void setOrigCoreSsn(String origCoreSsn) {
        this.origCoreSsn = origCoreSsn == null ? null : origCoreSsn.trim();
    }

    public String getOrigKeepAccTime() {
        return origKeepAccTime;
    }

    public void setOrigKeepAccTime(String origKeepAccTime) {
        this.origKeepAccTime = origKeepAccTime == null ? null : origKeepAccTime.trim();
    }

    public Short getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Short retryCount) {
        this.retryCount = retryCount;
    }

    public String getVerNo() {
        return verNo;
    }

    public void setVerNo(String verNo) {
        this.verNo = verNo == null ? null : verNo.trim();
    }

    public String getIsReaccount() {
        return isReaccount;
    }

    public void setIsReaccount(String isReaccount) {
        this.isReaccount = isReaccount;
    }
}