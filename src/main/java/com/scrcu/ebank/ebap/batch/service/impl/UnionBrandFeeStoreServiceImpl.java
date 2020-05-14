package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthUnionBrandFeeInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.BthUnionFileDet;
import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.batch.dao.BthUnionBrandFeeInfoDao;
import com.scrcu.ebank.ebap.batch.dao.BthUnionFileDetDao;
import com.scrcu.ebank.ebap.batch.service.UnionBrandFeeStoreService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: ljy
 * @create: 2018-11-26 15:46
 */
@Slf4j
@Service
public class UnionBrandFeeStoreServiceImpl implements UnionBrandFeeStoreService {
    /**
     * 品牌服务文件
     */
    @Resource
    private BthUnionBrandFeeInfoDao bthUnionBrandFeeInfoDao;

    /**
     * 银联文件
     */
    @Resource
    private BthUnionFileDetDao bthUnionFileDetDao;



    @Override
    public CommonResponse unionBrandFeeStore(MerRegRequest request) {

        CommonResponse response = new CommonResponse();
        Map<String ,Object> map = new HashMap<>(1);
        map.put("settleDate",request.getSettleDate());
        List<BthUnionFileDet> bthUnionFileDets = bthUnionFileDetDao.selectList("selectByStlmDateFileDet", map);
        log.info("================================>> 清算日[{}]共有[{}]条银联交易数据",request.getSettleDate() , bthUnionFileDets.size());
        List<BthUnionBrandFeeInfo> bthUnionBrandFeeInfos = bthUnionBrandFeeInfoDao.selectByStlmDate(request.getSettleDate());
        log.info("================================>> 清算日[{}]共有[{}]条银联品牌服务数据",request.getSettleDate() , bthUnionBrandFeeInfos.size());

        List<BthUnionFileDet> fileList = new ArrayList<>();
        for (BthUnionFileDet bthUnionFileDet : bthUnionFileDets) {
            // 清算主键
            String stlmKeyFileDet = bthUnionFileDet.getProxyInsCode() + bthUnionFileDet.getSendInsCode() + bthUnionFileDet.getTraceNum() + bthUnionFileDet.getTransDate();
            // 匹配到的设置品牌服务费
            for (BthUnionBrandFeeInfo bthUnionBrandFeeInfo : bthUnionBrandFeeInfos) {
                // 清算主键
                String stlmKeyBrandFee = bthUnionBrandFeeInfo.getAcceptInsCode()+bthUnionBrandFeeInfo.getSendInsCode()+bthUnionBrandFeeInfo.getSysTrackNum()+bthUnionBrandFeeInfo.getTransDate();
                if (!stlmKeyFileDet.equals(stlmKeyBrandFee)){
                    continue;
                }

                BigDecimal brandFee = BigDecimal.ZERO;
                if (IfspDataVerifyUtil.isNotBlank(bthUnionBrandFeeInfo.getBrandFee())){

                    String DBrandFee = bthUnionBrandFeeInfo.getBrandFee();
                    // 去掉第一位字母 D
                    brandFee = new BigDecimal(DBrandFee.substring(1));
                }
                bthUnionFileDet.setBrandFee(brandFee);
                fileList.add(bthUnionFileDet);
            }
        }

        // 入库操作
        log.info("================================>> 匹配上的银联品牌服务费共有[{}]笔 ",fileList.size());
        updateForBrandFee(fileList , bthUnionFileDets);

        return response;
    }

    /**
     * 入库操作
     * @param fileList
     * @param bthUnionFileDets
     */
    private void updateForBrandFee(List<BthUnionFileDet> fileList ,List<BthUnionFileDet> bthUnionFileDets) {
        for (BthUnionFileDet bthUnionFileDet : bthUnionFileDets) {
            String stlmKeyFileDet = bthUnionFileDet.getProxyInsCode() +"|"+ bthUnionFileDet.getSendInsCode()+"|"+ bthUnionFileDet.getTraceNum() +"|"+bthUnionFileDet.getTransDate();
            log.info("================================>> 还原银联文件表品牌服务费 ,银联清算主键为[{}]",stlmKeyFileDet);
            BthUnionFileDet record = new BthUnionFileDet();
            record.setProxyInsCode(bthUnionFileDet.getProxyInsCode());
            record.setSendInsCode(bthUnionFileDet.getSendInsCode());
            record.setTraceNum(bthUnionFileDet.getTraceNum());
            record.setTransDate(bthUnionFileDet.getTransDate());
            record.setBrandFee(BigDecimal.ZERO);
            bthUnionFileDetDao.updateByPrimaryKeySelective(record);
        }

        for (BthUnionFileDet bthUnionFileDet : fileList) {
            String stlmKeyFileDet = bthUnionFileDet.getProxyInsCode() +"|"+ bthUnionFileDet.getSendInsCode()+"|"+ bthUnionFileDet.getTraceNum() +"|"+bthUnionFileDet.getTransDate();
            log.info("================================>> 录入银联文件表品牌服务费 ,银联清算主键为[{}]",stlmKeyFileDet);
            BthUnionFileDet record = new BthUnionFileDet();
            record.setProxyInsCode(bthUnionFileDet.getProxyInsCode());
            record.setSendInsCode(bthUnionFileDet.getSendInsCode());
            record.setTraceNum(bthUnionFileDet.getTraceNum());
            record.setTransDate(bthUnionFileDet.getTransDate());
            record.setBrandFee(bthUnionFileDet.getBrandFee());
            bthUnionFileDetDao.updateByPrimaryKeySelective(record);
        }
    }

}
