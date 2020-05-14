package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.MchtAuthRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

/**
 * 生成商户结算文件
 * @author ydl
 *
 */
public interface MchtAuthService
{


	CommonResponse auth(MchtAuthRequest request);
}
