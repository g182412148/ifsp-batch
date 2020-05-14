package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

public class PagyTxnSsnRequest extends CommonRequest {

    private String  pagyTxnSsn;
    private String acctTypeId;
    private String unionType;

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public String getPagyTxnSsn() {
        return pagyTxnSsn;
    }

    public void setPagyTxnSsn(String pagyTxnSsn) {
        this.pagyTxnSsn = pagyTxnSsn;
    }

    public String getAcctTypeId() {
        return acctTypeId;
    }

    public void setAcctTypeId(String acctTypeId) {
        this.acctTypeId = acctTypeId;
    }

    @Override
    public void valid() throws IfspValidException {
    }
}
