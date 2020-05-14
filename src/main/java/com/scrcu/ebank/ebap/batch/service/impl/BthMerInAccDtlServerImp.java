package com.scrcu.ebank.ebap.batch.service.impl;

import com.scrcu.ebank.ebap.batch.bean.request.InAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.response.InAcctResponse;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDtlDao;
import com.scrcu.ebank.ebap.batch.service.BthMerInAccDtlServer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("bthMerInAccDtlServer")
public class BthMerInAccDtlServerImp implements BthMerInAccDtlServer {

    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;

    @Override
    public InAcctResponse selectUpdateDate(InAcctRequest request) {
        InAcctResponse response =new InAcctResponse();
        String updateDate = bthMerInAccDtlDao.selectUpdateDate(request.getTxnSeqId());
        response.setUpdateDate(updateDate);
        return response;
    }
}
