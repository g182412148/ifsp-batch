package com.scrcu.ebank.ebap.batch.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class BthVatInfo extends CommonDTO {
    private String id;
    
    private String orderId;

    private String tranDate;

    private String txnChnlFlag;

    private String subjectCode;
    private String subjectName;

    private String merAccount;

    private String merOrgId;
    private String merOrgNm;
    private BigDecimal merFee;

    private String openOrgId;
    private String openOrgNm;
    private BigDecimal openOrgFee;
    
    private String opOrgId;
    private String opOrgNm;
    private BigDecimal opOrgFee;

    private String overseasFlag;              //境内外标识:0-境内;1-境外;默认0
    private String cur;
    private String drCrFlag;

    private Date createDate;
    private Date updateDate;

    private String reserved1;
    private String reserved2;
    private String reserved3;
    private String reserved4;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}
	public String getTxnChnlFlag() {
		return txnChnlFlag;
	}
	public void setTxnChnlFlag(String txnChnlFlag) {
		this.txnChnlFlag = txnChnlFlag;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getMerAccount() {
		return merAccount;
	}
	public void setMerAccount(String merAccount) {
		this.merAccount = merAccount;
	}
	public String getMerOrgId() {
		return merOrgId;
	}
	public void setMerOrgId(String merOrgId) {
		this.merOrgId = merOrgId;
	}
	public String getMerOrgName() {
		return merOrgNm;
	}
	public void setMerOrgName(String merOrgName) {
		this.merOrgNm = merOrgName;
	}
	public BigDecimal getMerFee() {
		return merFee;
	}
	public void setMerFee(BigDecimal merFee) {
		this.merFee = merFee;
	}
	public String getOpenOrgId() {
		return openOrgId;
	}
	public void setOpenOrgId(String openOrgId) {
		this.openOrgId = openOrgId;
	}
	public String getOpenOrgNm() {
		return openOrgNm;
	}
	public void setOpenOrgNm(String openOrgNm) {
		this.openOrgNm = openOrgNm;
	}
	public BigDecimal getOpenOrgFee() {
		return openOrgFee;
	}
	public void setOpenOrgFee(BigDecimal openOrgFee) {
		this.openOrgFee = openOrgFee;
	}
	public String getOpOrgId() {
		return opOrgId;
	}
	public void setOpOrgId(String opOrgId) {
		this.opOrgId = opOrgId;
	}
	public String getOpOrgNm() {
		return opOrgNm;
	}
	public void setOpOrgNm(String opOrgNm) {
		this.opOrgNm = opOrgNm;
	}
	public BigDecimal getOpOrgFee() {
		return opOrgFee;
	}
	public void setOpOrgFee(BigDecimal opOrgFee) {
		this.opOrgFee = opOrgFee;
	}
	public String getOverseasFlag() {
		return overseasFlag;
	}
	public void setOverseasFlag(String overseasFlag) {
		this.overseasFlag = overseasFlag;
	}
	public String getCur() {
		return cur;
	}
	public void setCur(String cur) {
		this.cur = cur;
	}
	public String getDrCrFlag() {
		return drCrFlag;
	}
	public void setDrCrFlag(String drCrFlag) {
		this.drCrFlag = drCrFlag;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getReserved1() {
		return reserved1;
	}
	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}
	public String getReserved2() {
		return reserved2;
	}
	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}
	public String getReserved3() {
		return reserved3;
	}
	public void setReserved3(String reserved3) {
		this.reserved3 = reserved3;
	}
	public String getReserved4() {
		return reserved4;
	}
	public void setReserved4(String reserved4) {
		this.reserved4 = reserved4;
	}
    
}