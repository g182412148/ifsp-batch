package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.batch.bean.vo.TimeQuanTumVO;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.util.List;

public class TimeQuanTumResponse extends CommonResponse {

    private static final long serialVersionUID = 1L;

    private List<TimeQuanTumVO> timeQuanTums;

    public List<TimeQuanTumVO> getTimeQuanTums() {
        return timeQuanTums;
    }

    public void setTimeQuanTums(List<TimeQuanTumVO> timeQuanTums) {
        this.timeQuanTums = timeQuanTums;
    }

}
