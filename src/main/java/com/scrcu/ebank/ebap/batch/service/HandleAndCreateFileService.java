package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.dto.PagyMchtInfo;

import java.util.List;

public interface HandleAndCreateFileService {
    public void handle(String localRegFilePath, List<PagyMchtInfo> pagyMchtInfos, String curDate) throws Exception;

    }
