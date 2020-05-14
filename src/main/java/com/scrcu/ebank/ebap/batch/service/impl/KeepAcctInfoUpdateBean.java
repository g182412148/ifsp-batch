package com.scrcu.ebank.ebap.batch.service.impl;

import com.scrcu.ebank.ebap.batch.bean.request.OrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.dao.OrgRepeMergDao;
import com.scrcu.ebank.ebap.batch.service.OrgRepeMergUpdateInterface;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2019/9/5.
 */
@Component("keepAcctInfoUpdateBean")
public class KeepAcctInfoUpdateBean implements OrgRepeMergUpdateInterface {
    @Resource
    private OrgRepeMergDao orgRepeMergDao;

    @Override
    public void updateOrgMethod(OrgRepeMergRequest req) {
        List<String> mchtIdList = orgRepeMergDao.selectMchtId(req.getRepeOrg());
        for (String mchtId : mchtIdList) {
            orgRepeMergDao.updateKeepAcctInfo(mchtId, req.getMergOrg());
        }
    }

    @Override
    public String getDesc() {
        return "记账表";
    }
}
