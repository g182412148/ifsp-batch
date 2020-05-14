package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetFileNameRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MchtAuthRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OfflineCreatMerChkFileRequest;
import com.scrcu.ebank.ebap.batch.bean.response.GetFileNameResponse;
import com.scrcu.ebank.ebap.batch.service.*;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * 商户文件生成Controller
 * @author ydl
 *1、生成商户对账文件
 *2、生成商户结算文件
 *3、生成商户审核结果文件
 */
@Controller
public class MchtAuthController
{
	
	@Resource
	private MchtAuthService mchtAuthService;
	

	
	@Resource
	private PointFileDispatchService pointFileDispatchService;

    @Resource
    private GenEptFileService genEptFileService;

    @SOA("001.mchtAuth")
    @Explain(name = "微信商户实名认证", logLv = LogLevel.DEBUG)
    public CommonResponse mchtAuth(@IfspValid MchtAuthRequest request) throws Exception
    {

        return mchtAuthService.auth(request);
    }
    

}
