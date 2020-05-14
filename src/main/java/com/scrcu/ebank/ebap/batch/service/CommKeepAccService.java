package com.scrcu.ebank.ebap.batch.service;


import java.util.Map;

public interface CommKeepAccService {

    /**
     * 查询单笔记账状态函数
     * @param pagyPayTxnSsn
     * @return
     */
    Map<String ,String> qrcKeepAccRst(String pagyPayTxnSsn);

}
