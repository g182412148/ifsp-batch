package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccVo;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.util.List;

public class BthKeepAccResponse extends CommonResponse {

    private String timeOutFlag;

    private String errorFlag;

    private List<KeepAccVo> keepAccData;

    public String getTimeOutFlag() {
        return timeOutFlag;
    }

    public void setTimeOutFlag(String timeOutFlag) {
        this.timeOutFlag = timeOutFlag;
    }

    public String getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(String errorFlag) {
        this.errorFlag = errorFlag;
    }

    public List<KeepAccVo> getKeepAccData() {
        return keepAccData;
    }

    public void setKeepAccData(List<KeepAccVo> keepAccData) {
        this.keepAccData = keepAccData;
    }

    public BthKeepAccResponse(){}

    public BthKeepAccResponse(String respCode ,String respMsg ){
        super.respCode = respCode;
        super.respMsg =  respMsg;

    }
}
