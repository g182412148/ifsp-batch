package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public interface MerRegService {
    /**
     * 生成商户注册文件任务
     * @param request
     * @return
     */
    CommonResponse genMerRegFile(MerRegRequest request) throws IOException, ParseException;

    /**
     * 获取商户注册反馈文件任务
     * @param request
     * @return
     */
    CommonResponse getMerRegRtnFile(MerRegRequest request) throws IOException;
}
