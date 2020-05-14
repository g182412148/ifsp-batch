package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.batch.bean.dto.IfsOrg;
import com.scrcu.ebank.ebap.batch.bean.request.OrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.dao.IfsOrgDao;
import com.scrcu.ebank.ebap.batch.dao.OrgRepeMergDao;
import com.scrcu.ebank.ebap.batch.service.OrgRepeMergUpdateInterface;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@Component("orgRepeMergUapdateServiceImpl")
@Slf4j
public class OrgRepeMergUapdateServiceImpl implements OrgRepeMergUpdateInterface {

    @Autowired
    private OrgRepeMergDao orgRepeMergDao;

    @Autowired
    private IfsOrgDao ifsOrgDao;

    @Value("${MergSettleUrl}")
    private String merSettleUrl;

    @Value("${MergSettleFile}")
    private String merSettleFile;


    @Transactional(propagation = Propagation.REQUIRED)
    public void updateOrgMethod(OrgRepeMergRequest req){
        /**========================== 一、机构更新 ===========================*/
        log.info("========== 组织机构更新开始 ===========");
        IfsOrg ifsOrg = ifsOrgDao.selectByPrimaryKey(req.getMergOrg());
        orgRepeMergDao.updateMchtOrgRel(req.getRepeOrg(), req.getMergOrg(),ifsOrg.getBrName());
        orgRepeMergDao.updateMchtOrgRelTemp(req.getRepeOrg(), req.getMergOrg(),ifsOrg.getBrName());
        log.info("========== 组织机构更新完成 ===========");
        /**========================== 二、内部账户更新 ===========================*/
        log.info("========== 内部账户更新开始 ===========");
        String FilePath = merSettleUrl + merSettleFile + req.getMergDt() + "_" + req.getRepeOrg() + "_" + req.getMergOrg() + ".dat";
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(FilePath), Charset.forName("GBK")))){
            String str = null;
            while ((str = br.readLine()) != null) {
                String[] strarr = str.split("\\|#\\|",-1);
                orgRepeMergDao.updateMchtContInfoDep(strarr[0],strarr[1]);//更新保证金正式表
                orgRepeMergDao.updateMchtContInfoLiq(strarr[0],strarr[1]);//更新待清算正式表
                orgRepeMergDao.updateParternBaseInfo(strarr[0],strarr[1]);

                orgRepeMergDao.updateMchtContInfoTempDep(strarr[0],strarr[1]);//更新保证金临时表
                orgRepeMergDao.updateMchtContInfoTempLiq(strarr[0],strarr[1]);//更新待清算临时表
            }
        }catch (Exception e) {
            log.error("机构&内部账户更新错误:", e);
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(),  "机构&内部账户更新错误");
        }
        log.info("========== 内部账户更新完成 ===========");
    }

    @Override
    public String getDesc() {
        return "商户收单机构和内部账户";
    }
}






