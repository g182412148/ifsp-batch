package com.scrcu.ebank.ebap.batch.controller;

import com.scrcu.ebank.ebap.batch.bean.request.MchtBlackGreyListRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.batch.service.MchtBlackGreyService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;

/**
 * <p>名称 :  商户黑名单，灰名单处理 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : zhangb </p>
 * <p>日期 : 2019-08-20  16:16 </p>
 */
@Controller
public class MchtBlackGreyController {
    @Resource
    MchtBlackGreyService mchtBlackGreyService;

    /**
     * 生成商户注册文件上传查询黑名单任务
     * @param request
     * @return
     */
    @SOA("699.uploadMchtFile")
    @Explain(name = "生成商户文件，并上传到文件服务器,并调用接口传送文件id", logLv = LogLevel.DEBUG)
    public CommonResponse uploadMchtFile(@IfspValid MchtBlackGreyListRequest request) throws IOException, ParseException {
        return mchtBlackGreyService.uploadMchtFile(request);
    }

    /**
     * 下载商户文件更新黑名单数据
     * @param request
     * @return
     */
    @SOA("699.downloadMchtFile")
    @Explain(name = "并调用接口获得文件id，下载商户文件", logLv = LogLevel.DEBUG)
    public CommonResponse downloadMchtFile(@IfspValid MchtBlackGreyListRequest request) throws IOException, ParseException {
        return mchtBlackGreyService.downloadMchtFile(request);
    }


    /**
     * 本地商户黑灰名单处理任务
     * @param request
     * @return
     */
    @SOA("699.mchtBlackGrey")
    @Explain(name = "本地商户黑灰名单处理", logLv = LogLevel.DEBUG)
    public CommonResponse localMchtChk(@IfspValid MchtBlackGreyListRequest request) throws IOException, ParseException {
        return mchtBlackGreyService.localChk(request);
    }
}
