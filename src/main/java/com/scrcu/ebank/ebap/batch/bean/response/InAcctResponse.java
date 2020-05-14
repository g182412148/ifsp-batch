package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;

public class InAcctResponse  extends CommonResponse {
    public String updateDate;

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
