package com.scrcu.ebank.ebap.batch.bean.request;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;

@Data
public class QueryMchtPayStatisticsRequset extends CommonRequest {
    //商户号
    private String mchtNo;
    //查询时间
    private String time;

    @Override
    public void valid() throws IfspValidException {
        if(IfspDataVerifyUtil.isBlank(this.getMchtNo())){
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "商户号为空！");
        }
        if(IfspDataVerifyUtil.isBlank(this.getTime())){
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "时间为空！");
        }
    }
}
