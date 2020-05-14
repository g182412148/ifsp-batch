package com.scrcu.ebank.ebap.batch.bean.request;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;

public class TimeQuanTumRequest extends CommonRequest {

    private static final long serialVersionUID = 1L;

    /** 商户号 */
    private String mchtId;

    /** 开始时间 */
    private String startDate;

    /** 结束时间 */
    private String endDate;

    /** 时间段 */
    private String timeQuanTum;

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTimeQuanTum() {
        return timeQuanTum;
    }

    public void setTimeQuanTum(String timeQuanTum) {
        this.timeQuanTum = timeQuanTum;
    }

    @Override
    public void valid() throws IfspValidException {
        if (IfspDataVerifyUtil.isBlank(mchtId)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ": [ mchtId ]");
        }
        if (IfspDataVerifyUtil.isBlank(startDate)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ": [ startDate ]");
        }
        if (IfspDataVerifyUtil.isBlank(endDate)) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_1006.getCode(), IfspRespCodeEnum.RESP_1006.getDesc() + ": [ endDate ]");
        }
    }
}
