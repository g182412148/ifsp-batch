package com.scrcu.ebank.ebap.batch.service;

import com.scrcu.ebank.ebap.batch.bean.vo.ecif.FileResultVo;

/**
 * 描述 </br>
 *
 * @author M.chen
 * 2019/6/13 18:29
 */
public interface EcifFileService {
    FileResultVo createMiniUpdateFile(String fileCreate);
    FileResultVo createNormalUpdateFile(String settleDate);
}
