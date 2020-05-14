package com.scrcu.ebank.ebap.batch.bean.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.scrcu.ebank.ebap.common.beans.CommonDTO;

public class BthPagyChkErrInfo extends CommonDTO {
    private String chkErrSsn;

    private String pagyPayTxnSsn;

    private Date pagyPayTxnTm;

    private String pagyTxnSsn;

    private Date pagyTxnTm;

    private String pagySysNo;

    private String pagySysSoaNo;

    private String pagySysSoaVersion;

    private String pagyNo;

    private String pagyMchtNo;

    private Long txnAmt;

    private String tpamTxnSsn;

    private String tpamTxnTm;

    private String tpamTxnTypeNo;

    private Long tpamTxnAmt;

    private Long tpamTxnFeeAmt;

    private Long tpamSetAmt;

    private Date chkDataDt;

    private Date chkErrDt;

    private String errTp;

    private String errDesc;

    private String procSt;

    private String procDesc;

    private Date crtTm;

    private Date procTm;

    private String procTlrNo;

    private Date lstUpdTm;
    private String txnType;
    private String acctInFlag;
    private String fileInFlag;

    public String getFileInFlag() {
        return fileInFlag;
    }

    public void setFileInFlag(String fileInFlag) {
        this.fileInFlag = fileInFlag;
    }

    public String getAcctInFlag() {
        return acctInFlag;
    }

    public void setAcctInFlag(String acctInFlag) {
        this.acctInFlag = acctInFlag;
    }

    public BthPagyChkErrInfo(BthWxFileDet r) throws Exception {
    	super();
        this.setPagyTxnTm(IfspDateTime.strToDate(r.getTxnTm(), IfspDateTime.YYYY_MM_DD));
        this.setPagySysNo(r.getPagySysNo());
        this.setPagySysSoaVersion("1.0.0");
        this.setTxnAmt(r.getOrderAmt().longValue());
        this.setTpamTxnSsn(r.getWxOrderNo());
        this.setTpamTxnAmt(r.getOrderAmt().longValue());
//        this.setTpamTxnFeeAmt(r.getFeeAmt().longValue());
        this.setPagyPayTxnSsn(r.getOrderNo());
	}

	public BthPagyChkErrInfo(BthPagyLocalInfo r) {
		super();
        this.setPagyTxnTm(r.getPagyPayTxnTm());
        this.setPagySysNo(r.getPagySysNo());
        this.setPagySysSoaVersion("1.0.0");
        this.setTxnAmt(r.getTxnAmt());
        this.setTpamTxnSsn(r.getTpamTxnSsn());
        this.setTpamTxnAmt(r.getTpamTxnAmt());
        this.setPagyPayTxnSsn(r.getPagyPayTxnSsn());
	}

	public BthPagyChkErrInfo(BthAliFileDet r) throws Exception {
		super();
        this.setPagyTxnTm(IfspDateTime.strToDate(r.getEndTm(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
        this.setPagySysNo(r.getPagySysNo());
        this.setPagySysSoaVersion("1.0.0");
        this.setTxnAmt(r.getOrderAmt().longValue());
        this.setTpamTxnSsn(r.getAliOrderNo());
        this.setTpamTxnAmt(r.getOrderAmt().longValue());
        this.setTpamTxnFeeAmt(r.getFeeAmt().longValue());
        this.setPagyPayTxnSsn(r.getOrderNo());
	}

	public BthPagyChkErrInfo() {
		super();
	}

	public BthPagyChkErrInfo(BthUnionFileDet r) throws Exception {
		super();
		Date date =new Date();
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        this.setPagyTxnTm(IfspDateTime.strToDate(form.format(date).substring(0,4)+r.getTransDate(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
        this.setPagySysNo(r.getPagySysNo());
        this.setPagySysSoaVersion("1.0.0");
        this.setTxnAmt(r.getTransAmt().longValue());
        this.setTpamTxnSsn(r.getOrderId());
        this.setTpamTxnAmt(r.getTransAmt().longValue());
        this.setPagyPayTxnSsn(r.getProxyInsCode().trim()+r.getSendInsCode().trim()+r.getTraceNum().trim()+r.getTransDate().trim());
	}

	public BthPagyChkErrInfo(DebitTranInfo r) throws Exception {
		super();
        this.setPagyTxnTm(IfspDateTime.strToDate(r.getChannelDate(), IfspDateTime.YYYY_MM_DD));
        this.setPagySysNo(r.getPagySysNo());
        this.setPagySysSoaVersion("1.0.0");
        this.setTxnAmt(r.getTxnAmount().longValue());
        this.setTpamTxnSsn(r.getChannelSeq());
        this.setTpamTxnAmt(r.getTxnAmount().longValue());
        this.setPagyPayTxnSsn(r.getChannelSeq());
	}

	public String getChkErrSsn() {
        return chkErrSsn;
    }

    public void setChkErrSsn(String chkErrSsn) {
        this.chkErrSsn = chkErrSsn == null ? null : chkErrSsn.trim();
    }

    public String getPagyPayTxnSsn() {
        return pagyPayTxnSsn;
    }

    public void setPagyPayTxnSsn(String pagyPayTxnSsn) {
        this.pagyPayTxnSsn = pagyPayTxnSsn == null ? null : pagyPayTxnSsn.trim();
    }

    public Date getPagyPayTxnTm() {
        return pagyPayTxnTm;
    }

    public void setPagyPayTxnTm(Date pagyPayTxnTm) {
        this.pagyPayTxnTm = pagyPayTxnTm;
    }

    public String getPagyTxnSsn() {
        return pagyTxnSsn;
    }

    public void setPagyTxnSsn(String pagyTxnSsn) {
        this.pagyTxnSsn = pagyTxnSsn == null ? null : pagyTxnSsn.trim();
    }

    public Date getPagyTxnTm() {
        return pagyTxnTm;
    }

    public void setPagyTxnTm(Date pagyTxnTm) {
        this.pagyTxnTm = pagyTxnTm;
    }

    public String getPagySysNo() {
        return pagySysNo;
    }

    public void setPagySysNo(String pagySysNo) {
        this.pagySysNo = pagySysNo == null ? null : pagySysNo.trim();
    }

    public String getPagySysSoaNo() {
        return pagySysSoaNo;
    }

    public void setPagySysSoaNo(String pagySysSoaNo) {
        this.pagySysSoaNo = pagySysSoaNo == null ? null : pagySysSoaNo.trim();
    }

    public String getPagySysSoaVersion() {
        return pagySysSoaVersion;
    }

    public void setPagySysSoaVersion(String pagySysSoaVersion) {
        this.pagySysSoaVersion = pagySysSoaVersion == null ? null : pagySysSoaVersion.trim();
    }

    public String getPagyNo() {
        return pagyNo;
    }

    public void setPagyNo(String pagyNo) {
        this.pagyNo = pagyNo == null ? null : pagyNo.trim();
    }

    public String getPagyMchtNo() {
        return pagyMchtNo;
    }

    public void setPagyMchtNo(String pagyMchtNo) {
        this.pagyMchtNo = pagyMchtNo == null ? null : pagyMchtNo.trim();
    }

    public Long getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(Long txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getTpamTxnSsn() {
        return tpamTxnSsn;
    }

    public void setTpamTxnSsn(String tpamTxnSsn) {
        this.tpamTxnSsn = tpamTxnSsn == null ? null : tpamTxnSsn.trim();
    }

    public String getTpamTxnTm() {
        return tpamTxnTm;
    }

    public void setTpamTxnTm(String tpamTxnTm) {
        this.tpamTxnTm = tpamTxnTm == null ? null : tpamTxnTm.trim();
    }

    public String getTpamTxnTypeNo() {
        return tpamTxnTypeNo;
    }

    public void setTpamTxnTypeNo(String tpamTxnTypeNo) {
        this.tpamTxnTypeNo = tpamTxnTypeNo == null ? null : tpamTxnTypeNo.trim();
    }

    public Long getTpamTxnAmt() {
        return tpamTxnAmt;
    }

    public void setTpamTxnAmt(Long tpamTxnAmt) {
        this.tpamTxnAmt = tpamTxnAmt;
    }

    public Long getTpamTxnFeeAmt() {
        return tpamTxnFeeAmt;
    }

    public void setTpamTxnFeeAmt(Long tpamTxnFeeAmt) {
        this.tpamTxnFeeAmt = tpamTxnFeeAmt;
    }

    public Long getTpamSetAmt() {
        return tpamSetAmt;
    }

    public void setTpamSetAmt(Long tpamSetAmt) {
        this.tpamSetAmt = tpamSetAmt;
    }

    public Date getChkDataDt() {
        return chkDataDt;
    }

    public void setChkDataDt(Date chkDataDt) {
        this.chkDataDt = chkDataDt;
    }

    public Date getChkErrDt() {
        return chkErrDt;
    }

    public void setChkErrDt(Date chkErrDt) {
        this.chkErrDt = chkErrDt;
    }

    public String getErrTp() {
        return errTp;
    }

    public void setErrTp(String errTp) {
        this.errTp = errTp == null ? null : errTp.trim();
    }

    public String getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc == null ? null : errDesc.trim();
    }

    public String getProcSt() {
        return procSt;
    }

    public void setProcSt(String procSt) {
        this.procSt = procSt == null ? null : procSt.trim();
    }

    public String getProcDesc() {
        return procDesc;
    }

    public void setProcDesc(String procDesc) {
        this.procDesc = procDesc == null ? null : procDesc.trim();
    }

    public Date getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public Date getProcTm() {
        return procTm;
    }

    public void setProcTm(Date procTm) {
        this.procTm = procTm;
    }

    public String getProcTlrNo() {
        return procTlrNo;
    }

    public void setProcTlrNo(String procTlrNo) {
        this.procTlrNo = procTlrNo == null ? null : procTlrNo.trim();
    }

    public Date getLstUpdTm() {
        return lstUpdTm;
    }

    public void setLstUpdTm(Date lstUpdTm) {
        this.lstUpdTm = lstUpdTm;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }
}