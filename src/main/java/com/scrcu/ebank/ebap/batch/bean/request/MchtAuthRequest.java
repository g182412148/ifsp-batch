package com.scrcu.ebank.ebap.batch.bean.request;

import com.scrcu.ebank.ebap.batch.common.dict.MchtAuthHandTypeDict;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.common.beans.CommonRequest;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.Data;

@Data
public class MchtAuthRequest extends CommonRequest {

	private static final long serialVersionUID = 12L;
    /**
     * 通过日期参数送过来
     */
	private String settleDate;

    private String authHandType;

    @Override
    public void valid() throws IfspValidException {
        authHandType = settleDate;
        MchtAuthHandTypeDict mchtAuthHandTypeDict = MchtAuthHandTypeDict.get(authHandType);
        if(mchtAuthHandTypeDict == null){
            throw new IfspValidException(RespConstans.RESP_FAIL.getCode(), "微信实名认证处理类型编码错误");
        }
    }
}
