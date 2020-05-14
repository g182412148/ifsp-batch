package com.scrcu.ebank.ebap.batch.bean.request;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: ljy
 * @create: 2018-09-06 12:05
 */
@Data
public class TxnSectionRequest extends CommonRequest {
    /**
     * 商户号
     */
    private String chlMerId;
    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 金额段
     */
    private List<Map<String ,String>> amtList;

    @Override
    public void valid() throws IfspValidException {
        if(IfspDataVerifyUtil.isBlank(this.getChlMerId())){
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "商户号为空！");
        }
        if(IfspDataVerifyUtil.isBlank(this.getStartTime())){
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "开始时间为空！");
        }
        if(IfspDataVerifyUtil.isBlank(this.getEndTime())){
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "结束时间为空！");
        }

        if(IfspDataVerifyUtil.isEmptyList(this.amtList)){
            throw new IfspSystemException(SystemConfig.getSysErrorCode(), "金额分段信息为空！");
        }


    }



}
