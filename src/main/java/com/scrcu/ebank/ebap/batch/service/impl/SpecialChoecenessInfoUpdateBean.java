package com.scrcu.ebank.ebap.batch.service.impl;

import com.scrcu.ebank.ebap.batch.bean.dto.IfsOrg;
import com.scrcu.ebank.ebap.batch.bean.dto.SpecialChoecenessInfo;
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
@Component("specialChoecenessInfoUpdateBean")
public class SpecialChoecenessInfoUpdateBean implements OrgRepeMergUpdateInterface {
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
            List<SpecialChoecenessInfo> specialChoecenessInfoList = orgRepeMergDao.selectSpecialChoecenessInfo(req.getRepeOrg());
            for (SpecialChoecenessInfo specialChoecenessInfo : specialChoecenessInfoList) {
                if (specialChoecenessInfo.getSpecialParCpt().contains(req.getMergOrg())) {//如果同一个专栏可选法人机构有并入后的机构 去掉撤消机构的机构号
                    if (specialChoecenessInfo.getSpecialParCpt().contains(req.getRepeOrg() + ",")) {
                        specialChoecenessInfo.setSpecialParCpt(specialChoecenessInfo.getSpecialParCpt().replace(req.getRepeOrg() + ",", ""));
                    } else {
                        specialChoecenessInfo.setSpecialParCpt(specialChoecenessInfo.getSpecialParCpt().replace("," + req.getRepeOrg(), ""));
                    }
                } else {//如果同一个专栏可选法人机构没有并入后的机构 替换撤销机构号为并入机构号
                    specialChoecenessInfo.setSpecialParCpt(specialChoecenessInfo.getSpecialPicId().replace(req.getRepeOrg(),req.getMergOrg()));
                }
                orgRepeMergDao.updateSpecialChoecenessInfo(specialChoecenessInfo);
            }
        }
    }

    @Override
    public String getDesc() {
        return "专栏2";
    }
}
