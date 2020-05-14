package com.scrcu.ebank.ebap.batch.bean.response;

import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: ljy
 * @create: 2018-11-26 22:27
 */
@Data
public class KeepAccResponse extends CommonResponse {

    /**
     * 附加信息
     */
    private List<Map<String ,Object>> respInfo;

    private Map<String ,Object> respData;

    public KeepAccResponse(){}


    public KeepAccResponse(String respCode ,String respMsg) {
        super.respCode = respCode;
        super.respMsg = respMsg;
    }
}
