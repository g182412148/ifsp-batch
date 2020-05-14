package com.scrcu.ebank.ebap.batch.bean.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author ljy
 */
@Data
public class TpamUpacpTxnInfoVo extends TpamUpacpTxnInfo {

    private String txnReqSsn;

    private Date txnReqTm;

    private String origTxnReqSsn;

    private Date origTxnReqTm;

    private String prodId;

    private String txnType;


}