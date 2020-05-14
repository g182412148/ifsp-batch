package com.scrcu.ebank.ebap.batch.bean.dto;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.common.beans.CommonDTO;
import java.math.BigDecimal;

public class BthPagyFileSum extends CommonDTO {
    private String pagyNo;

    private String chkDataDt;

    private String pagySysNo;

    private BigDecimal txnTotalCnt;

    private BigDecimal txnTotalAmt;

    private BigDecimal refTotalCnt;

    private BigDecimal refTotalAmt;

    private BigDecimal feeTotalAmt;

    private BigDecimal recptAmt;

    private String sumRmk;

    private String crtTm;

    public BthPagyFileSum(){

    }

    /**
     * 支付宝汇总
     * @param settledate
     * @param line
     */
    public BthPagyFileSum(String settledate ,String[] line) {

        this.pagyNo = "606000000000001";
        this.chkDataDt = settledate;
        this.pagySysNo = "606";

        this.txnTotalCnt = new BigDecimal(line[2].trim());
        this.txnTotalAmt = new BigDecimal(line[4].trim()).movePointRight(2);
        this.refTotalCnt = new BigDecimal(line[3].trim());
        this.feeTotalAmt = new BigDecimal(line[9].trim()).movePointRight(2).abs();
        this.recptAmt = new BigDecimal(line[5].trim()).movePointRight(2);
        this.sumRmk = "汇总信息";
        this.crtTm = IfspDateTime.getYYYYMMDDHHMMSS();

    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getChkDataDt() {
        return chkDataDt;
    }

    public void setChkDataDt(String chkDataDt) {
        this.chkDataDt = chkDataDt == null ? null : chkDataDt.trim();
    }

    public String getPagySysNo() {
        return pagySysNo;
    }

    public void setPagySysNo(String pagySysNo) {
        this.pagySysNo = pagySysNo == null ? null : pagySysNo.trim();
    }

    public BigDecimal getTxnTotalCnt() {
        return txnTotalCnt;
    }

    public void setTxnTotalCnt(BigDecimal txnTotalCnt) {
        this.txnTotalCnt = txnTotalCnt;
    }

    public BigDecimal getTxnTotalAmt() {
        return txnTotalAmt;
    }

    public void setTxnTotalAmt(BigDecimal txnTotalAmt) {
        this.txnTotalAmt = txnTotalAmt;
    }

    public BigDecimal getRefTotalCnt() {
        return refTotalCnt;
    }

    public void setRefTotalCnt(BigDecimal refTotalCnt) {
        this.refTotalCnt = refTotalCnt;
    }

    public BigDecimal getRefTotalAmt() {
        return refTotalAmt;
    }

    public void setRefTotalAmt(BigDecimal refTotalAmt) {
        this.refTotalAmt = refTotalAmt;
    }

    public BigDecimal getFeeTotalAmt() {
        return feeTotalAmt;
    }

    public void setFeeTotalAmt(BigDecimal feeTotalAmt) {
        this.feeTotalAmt = feeTotalAmt;
    }

    public BigDecimal getRecptAmt() {
        return recptAmt;
    }

    public void setRecptAmt(BigDecimal recptAmt) {
        this.recptAmt = recptAmt;
    }

    public String getSumRmk() {
        return sumRmk;
    }

    public void setSumRmk(String sumRmk) {
        this.sumRmk = sumRmk == null ? null : sumRmk.trim();
    }

    public String getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(String crtTm) {
        this.crtTm = crtTm == null ? null : crtTm.trim();
    }
}