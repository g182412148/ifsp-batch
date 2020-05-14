package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthBatchAccountFile;
import com.scrcu.ebank.ebap.batch.bean.request.CoreBkFileChkRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.dao.BthBatchAccountFileDao;
import com.scrcu.ebank.ebap.batch.service.CoreAccBkFileChkService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 任务名称：核心记账反馈文件检查
 *
 * 检查是否存在上次跑批未返回的核心记账反馈文件
 *
 * @author: ljy
 * @create: 2018-08-24 16:30
 */
@Slf4j
@Service
public class CoreAccBkFileChkServiceImpl implements CoreAccBkFileChkService {


    /**
     * 文件记录表
     */
    @Resource
    private BthBatchAccountFileDao bthBatchAccountFileDao;

    @Override
    public CommonResponse coreAccBkFileChk(CoreBkFileChkRequest request) {
        log.info("~~~~~~~~~~~~~~~~~~~~~~~~核心记账反馈文件检查开始~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        CommonResponse response = new CommonResponse();
        /**
         * 查询出02-处理成功的记录 (上次跑批调核心返回受理成功, 但还没到终态 ,即反馈文件未返回)  ,如果这次跑批反馈地址下还不存在此文件, 抛出异常
         */
        Map<String , Object> map = new HashMap<>(2);
        map.put("dealStatus",Constans.FILE_STATUS_02);
        map.put("fileType", Constans.FILE_IN_ACC);
        List<BthBatchAccountFile> list = bthBatchAccountFileDao.selectList("accF_selectByState", map);
        if (list.size() != 0){
            Iterator<BthBatchAccountFile> iterator = list.iterator();
            while (iterator.hasNext()){
                BthBatchAccountFile checkFile = iterator.next();
                String fileName = checkFile.getBkFileName();
                if (IfspDataVerifyUtil.isNotBlank(fileName)){
                    File f = new File(fileName);
                    if (!f.exists()){
                        log.error(">>>>>>>>>>>>>>>>Not found Core Check file! FileName : "+fileName+"<<<<<<<<<<<<<<<<<");
                        throw new IfspBizException("9999","核心仍无入账文件返回!!");
                    }
                }
            }
        }

        log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~核心记账反馈文件检查步结束~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        return response;
    }
}

