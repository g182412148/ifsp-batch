package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.MchtBlackGreyListRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.io.IOException;
import java.text.ParseException;

public interface MchtBlackGreyService {
    /**
     * 生成商户注册文件任务
     * @param request
     * @return
     */
    CommonResponse localChk(MchtBlackGreyListRequest request) throws IOException, ParseException;

    /**
     * 获取商户注册反馈文件任务
     * @param request
     * @return
     */
    CommonResponse getMerRegRtnFile(MerRegRequest request) throws IOException;

    /**
     * 上传商户文件任务
     * @param request
     * @return
     */
    CommonResponse uploadMchtFile(MchtBlackGreyListRequest request) throws IOException, ParseException;
    /**
     * 下载商户文件任务
     * @param request
     * @return
     */
    CommonResponse downloadMchtFile(MchtBlackGreyListRequest request) throws IOException, ParseException;
}
