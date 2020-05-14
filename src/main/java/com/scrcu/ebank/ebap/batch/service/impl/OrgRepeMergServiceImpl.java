package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.batch.bean.dto.IfsOrg;
import com.scrcu.ebank.ebap.batch.bean.dto.OrgRepeMergCfg;
import com.scrcu.ebank.ebap.batch.bean.dto.OrgRepeMergInfo;
import com.scrcu.ebank.ebap.batch.bean.request.OrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.bean.request.SelectOrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.bean.response.SelectOrgRepeMergResponse;
import com.scrcu.ebank.ebap.batch.common.dict.MergStusDict;
import com.scrcu.ebank.ebap.batch.common.dict.UpdateTypeDict;
import com.scrcu.ebank.ebap.batch.dao.IfsOrgDao;
import com.scrcu.ebank.ebap.batch.dao.OrgRepeMergDao;
import com.scrcu.ebank.ebap.batch.service.OrgRepeMergService;
import com.scrcu.ebank.ebap.batch.service.OrgRepeMergUpdateInterface;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrgRepeMergServiceImpl implements OrgRepeMergService {

    @Resource
    private OrgRepeMergDao orgRepeMergDao;
    @Resource
    private IfsOrgDao ifsOrgDao;
    @Value("${MergSettleUrl}")
    private String merSettleUrl;
    @Value("${MergSettleFile}")
    private String merSettleFile;
    @Value("${WaitInterval}")
    private int waitInterval;
    @Value("${WaitTotal}")
    private int waitTotal;


    @Override
    public CommonResponse orgRepeMerg(OrgRepeMergRequest req) {
        CommonResponse commonResponse = new CommonResponse();
        OrgRepeMergInfo orgRepeMergInfo = null;
        try {
            /* 查询机构撤并是否有记录了 */
            orgRepeMergInfo = orgRepeMergDao.selectOrgMergInfo(req.getRepeOrg(),req.getMergOrg(),IfspDateTime.parseDate(req.getMergDt(),"yyyyMMdd"));
            if (orgRepeMergInfo == null) {
            /* 插入执行机构撤并信息 */
                orgRepeMergInfo = new OrgRepeMergInfo();
                orgRepeMergInfo.setRepeMergId(IfspId.getUUID32());
                orgRepeMergInfo.setRepeOrg(req.getRepeOrg());
                orgRepeMergInfo.setMergOrg(req.getMergOrg());
                orgRepeMergInfo.setMergStus(MergStusDict.NOT_DONE.getCode());
                orgRepeMergInfo.setMergDt(IfspDateTime.parseDate(req.getMergDt(), IfspDateTime.YYYYMMDD));
                orgRepeMergInfo.setCraDt(new Date());
                orgRepeMergInfo.setUpdDt(new Date());
                orgRepeMergDao.insertOrgRepeMergInfo(orgRepeMergInfo);
            } else if (MergStusDict.NOT_DONE.getCode().equals(orgRepeMergInfo.getMergStus())
                    || MergStusDict.EXECUTING.getCode().equals(orgRepeMergInfo.getMergStus())) {
                commonResponse.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
                commonResponse.setRespMsg("系统繁忙，请稍后重试！");
                return commonResponse;
            } else if (MergStusDict.FAIL.getCode().equals(orgRepeMergInfo.getMergStus())) {
                OrgRepeMergInfo orgRepeMergInfo1 = new OrgRepeMergInfo();
                orgRepeMergInfo1.setRepeMergId(orgRepeMergInfo.getRepeMergId());
                orgRepeMergInfo1.setMergStus(MergStusDict.NOT_DONE.getCode());
                orgRepeMergInfo1.setUpdDt(new Date());
                orgRepeMergDao.updateOrgRepeMergInfo(orgRepeMergInfo1);
            } else {
                commonResponse.setRespCode(IfspRespCodeEnum.RESP_SUCCESS.getCode());
                commonResponse.setRespMsg("机构撤并已执行成功");
                return commonResponse;
            }

            /*
             * 业务动作
             */
            int tryCount = waitTotal/waitInterval;
            log.debug("检查ok文件最大重试次数: " + tryCount);
            String fileName = merSettleFile + req.getMergDt() + "_" + req.getRepeOrg() + "_" + req.getMergOrg() + ".dat.ok";
            log.info("ok文件名：" + fileName);
            do {
                File file = new File(merSettleUrl + fileName);
                if (file.exists()) {
                    log.info("获取ok文件成功");
                    orgRepeMergInfo.setMergStus(MergStusDict.EXECUTING.getCode());
                    orgRepeMergInfo.setUpdDt(new Date());
                    orgRepeMergDao.updateOrgRepeMergInfo(orgRepeMergInfo);
                    break;
                } else {
                    if (tryCount > 0) {
                        try {
                            log.info("等待ok文件, 次数:" + tryCount);
                            Thread.sleep(waitInterval);
                        } catch (InterruptedException e) {
                            log.error("等待ok文件异常中断:", e);
                        }
                    } else {
                        orgRepeMergInfo.setMergStus(MergStusDict.FAIL.getCode());
                        orgRepeMergInfo.setUpdDt(new Date());
                        orgRepeMergDao.updateOrgRepeMergInfo(orgRepeMergInfo);
                        throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "获取内部账户文件失败");
                    }
                    tryCount -- ;
                }
            } while (tryCount >= 0);

            /** 更新撤销机构状态为注销状态 */
            log.debug("===更新撤销机构状态为已注销(start)===");
            IfsOrg ifsOrg = new IfsOrg();
            ifsOrg.setBrId(req.getRepeOrg());
            ifsOrg.setBrState("02");//注销
            ifsOrgDao.update(ifsOrg);
            log.debug("===更新撤销机构状态为已注销(end)===");

            /** 更新机构 */
            /* 查询需要更新的机构 */
            List<OrgRepeMergCfg> orgRepeMergCfgList = orgRepeMergDao.selectAllOrg();
            for (OrgRepeMergCfg orgRepeMergCfg : orgRepeMergCfgList) {
                if (UpdateTypeDict.SQL.getCode().equals(orgRepeMergCfg.getUpdType())) {//sql拼接
                    long startTime = System.currentTimeMillis();
                    log.debug("===更新" +orgRepeMergCfg.getTableName()+ "表(start)===");
                    orgRepeMergDao.updateOrg(orgRepeMergCfg.getTableName(), orgRepeMergCfg.getParam(), req.getRepeOrg(), req.getMergOrg());
                    log.debug("===更新" +orgRepeMergCfg.getTableName()+ "表(end), 耗时: " + (startTime - System.currentTimeMillis())+  "===");
                } else {//java程序 或者 收单机构和内部账户更新
                    Object bean = IfspSpringContextUtils.getInstance().getBean(orgRepeMergCfg.getParam());
                    if(bean instanceof OrgRepeMergUpdateInterface){
                        OrgRepeMergUpdateInterface invoker = (OrgRepeMergUpdateInterface) bean;
                        long startTime = System.currentTimeMillis();
                        log.debug("===更新" +invoker.getDesc()+ "(start)===");
                        invoker.updateOrgMethod(req);
                        log.debug("===更新" +invoker.getDesc()+ "(end), 耗时: " + (startTime - System.currentTimeMillis())+  "===");
                    }else {
                        log.error("机构撤并实现类必须继承“OrgRepeMergUpdateInterface”接口");
                        throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "机构撤并实现类必须继承“OrgRepeMergUpdateInterface”接口");
                    }
                }
            }

            /* 更新执行机构撤并状态为撤并成功 */
            log.info("========== 机构撤并执行成功 ===========");
            orgRepeMergInfo.setMergStus(MergStusDict.SUCCESS.getCode());
            orgRepeMergInfo.setUpdDt(new Date());
            orgRepeMergDao.updateOrgRepeMergInfo(orgRepeMergInfo);

            commonResponse.setRespCode(IfspRespCodeEnum.RESP_SUCCESS.getCode());
            commonResponse.setRespMsg("机构撤并成功");
            return commonResponse;
        }  catch (Exception e) {
            /* 更新执行机构撤并状态为撤并失败 */
            log.info("机构撤并失败：" + e.getMessage());
            OrgRepeMergInfo orgRepeMergInfo1 = new OrgRepeMergInfo();
            orgRepeMergInfo1.setRepeMergId(orgRepeMergInfo.getRepeMergId());
            orgRepeMergInfo1.setMergStus(MergStusDict.FAIL.getCode());
            orgRepeMergInfo1.setUpdDt(new Date());
            orgRepeMergDao.updateOrgRepeMergInfo(orgRepeMergInfo1);
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public SelectOrgRepeMergResponse selectOrgRepeMerg(SelectOrgRepeMergRequest req) {
        String mergStus = orgRepeMergDao.selectOrgRepeMerg(req.getRepeOrg(),req.getMergOrg(),IfspDateTime.parseDate(req.getMergDt(),"yyyyMMdd"));
        if (StringUtils.isBlank(mergStus)) {
            mergStus = MergStusDict.NOT_DONE.getCode();
        }
        SelectOrgRepeMergResponse resp = new SelectOrgRepeMergResponse();
        resp.setMergStus(mergStus);
        resp.setRespCode(IfspRespCodeEnum.RESP_SUCCESS.getCode());
        resp.setRespMsg(IfspRespCodeEnum.RESP_SUCCESS.getDesc());
        return resp;
    }

}






