package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.ChoCptMap;
import com.scrcu.ebank.ebap.batch.bean.dto.IfsOrg;
import com.scrcu.ebank.ebap.batch.bean.request.OrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.common.dict.CorpFlagDict;
import com.scrcu.ebank.ebap.batch.dao.IfsOrgDao;
import com.scrcu.ebank.ebap.batch.dao.OrgRepeMergDao;
import com.scrcu.ebank.ebap.batch.service.OrgRepeMergUpdateInterface;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2019/9/5.
 */
@Component("choCptMapUpdateBean")
public class ChoCptMapUpdateBean implements OrgRepeMergUpdateInterface {
    @Resource
    private OrgRepeMergDao orgRepeMergDao;
    @Resource
    private IfsOrgDao ifsOrgDao;

    @Override
    public void updateOrgMethod(OrgRepeMergRequest req) {
        IfsOrg repeIfsOrg = ifsOrgDao.selectByPrimaryKey(req.getRepeOrg());
        IfsOrg mergIfsOrg = ifsOrgDao.selectByPrimaryKey(req.getMergOrg());
        if (StringUtils.equals(CorpFlagDict.YES.getCode(),repeIfsOrg.getCorpFlag())
                && StringUtils.equals(CorpFlagDict.YES.getCode(),mergIfsOrg.getCorpFlag())) {
            List<ChoCptMap> repeChoCptMapList = orgRepeMergDao.selectChoCptMap(req.getRepeOrg());
            for (ChoCptMap repeChoCptMap : repeChoCptMapList) {
                ChoCptMap choCptMap = orgRepeMergDao.selectChoCptMap(req.getMergOrg(),repeChoCptMap.getChoId());
                if (IfspDataVerifyUtil.isNotEmpty(choCptMap)) {//如果同一个专栏可选法人机构有并入后的机构删除撤销的
                    orgRepeMergDao.deleteChoCptMap(repeChoCptMap.getCptId(), repeChoCptMap.getChoId());
                } else {//如果同一个专栏可选法人机构没有并入后的机构更新
                    orgRepeMergDao.updateChoCptMap(req.getRepeOrg(), req.getMergOrg());
                }
            }
        }
    }

    @Override
    public String getDesc() {
        return "专栏";
    }
}
