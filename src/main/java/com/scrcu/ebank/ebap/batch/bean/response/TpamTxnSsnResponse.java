package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public class TpamTxnSsnResponse extends CommonResponse {
    private String  pagyTxnSsn;
    private String tpamTxnSsn;

    public String getPagyTxnSsn() {
        return pagyTxnSsn;
    }

    public void setPagyTxnSsn(String pagyTxnSsn) {
        this.pagyTxnSsn = pagyTxnSsn;
    }

    public String getTpamTxnSsn() {
        return tpamTxnSsn;
    }

    public void setTpamTxnSsn(String tpamTxnSsn) {
        this.tpamTxnSsn = tpamTxnSsn;
    }
}
