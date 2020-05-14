package com.scrcu.ebank.ebap.batch.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.AreaInfo;
import com.scrcu.ebank.ebap.batch.bean.vo.ecif.ECIFMchtResultVo;
import com.scrcu.ebank.ebap.batch.bean.vo.ecif.FileResultVo;
import com.scrcu.ebank.ebap.batch.common.constant.ECIFConstants;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.dao.AreaInfoDao;
import com.scrcu.ebank.ebap.batch.dao.IfsOrgDao;
import com.scrcu.ebank.ebap.batch.dao.MchtStaffInfoDao;
import com.scrcu.ebank.ebap.batch.service.EcifFileService;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>名称 : ECIF文件上传下载处理任务 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/7/21 </p>
 */
@Service
@Slf4j
public class EcifFileServiceImpl implements EcifFileService {
    // 拼接字符
    private static final String UNION = ECIFConstants.UNION;
    // 换行符
    private static final String NEW_LINE = ECIFConstants.NEW_LINE;


    @Resource
    private IfsOrgDao ifsOrgDao;
    @Resource
    private AreaInfoDao areaInfoDao;
    @Resource
    private MchtStaffInfoDao mchtStaffInfoDao;

    /**
     * 组装小微商户ECIF上传文件
     */
    public FileResultVo createMiniUpdateFile(String settleDate) {
        // 响应报文
        FileResultVo resultVo = new FileResultVo();
        // 信息计数
        int count = 0;
        // 商户信息
        StringBuilder msg = new StringBuilder();
        /* ========================== 准备数据 ========================== */
        // 查询
        List<ECIFMchtResultVo> miniList = mchtStaffInfoDao.createMiniUpdateInfo(settleDate);
        /* =============================== 把信息写入文件 =============================== */
        if (IfspDataVerifyUtil.isEmptyList(miniList)) {
            log.info("没有新增/修改的小微商户");
            resultVo.setCount(0);
            resultVo.setMsg("");

            return resultVo;
        }
        // 遍历写入文件信息
        for (ECIFMchtResultVo vo :
                miniList) {
            // 获取文件信息
            msg.append( this.modifyOneMiniMcht(vo));
            // 计数
            count++;
        }

        // 映射响应
        resultVo.setCount(count);
        resultVo.setMsg(msg.toString());

        return resultVo;
    }

    /**
     * 组装普通商户ECIF上传文件
     */
    public FileResultVo createNormalUpdateFile(String settleDate) {
        // 响应报文
        FileResultVo resultVo = new FileResultVo();
        // 信息计数
        int count = 0;
        // 商户信息
        StringBuilder msg = new StringBuilder();
        /* ========================== 准备数据 ========================== */
        // 查询
        List<ECIFMchtResultVo> miniList = mchtStaffInfoDao.createNormalUpdateInfo(settleDate);
        log.info("===============商户查询结果===============" + JSON.toJSONString(miniList));
        /* =============================== 把信息写入文件 =============================== */
        if (IfspDataVerifyUtil.isEmptyList(miniList)) {
            log.info("没有新增/修改的普通商户");
//            throw new IfspBizException(MchtRespCodeEnum.RESP_QUERY_MCHT_FAIL.getCode(), "没有新增/修改的普通商户");
            resultVo.setCount(0);
            resultVo.setMsg("");

            return resultVo;
        }
        // 遍历写入文件信息
        for (ECIFMchtResultVo vo :
                miniList) {
            // 获取文件信息
            msg.append(this.modifyOneNormalMcht(vo));
            // 计数
            count++;
        }
        // 映射响应
        resultVo.setCount(count);
        resultVo.setMsg(msg.toString());

        return resultVo;
    }

    /**
     * 组装小微商户信息
     *
     * @param vo 商户查询vo
     * @return 商户信息字符串
     */
    private StringBuilder modifyOneMiniMcht(ECIFMchtResultVo vo) {
        // 新建字段接受stringBuffer
        StringBuilder bf = new StringBuilder();
        /*  小微商户ECIF字段拼接顺序,拼接间隔字段 "|@|"
            客户号 ECIF_CUSTOMER_NO 0
            证件类型	OWNER_CERT_TYPE 1
            证件号码	OWNER_CERT_NO 2
            商户名称	MCHT_NAME 3
            商户简称	MCHT_SIM_NAME 4
            证件有效期 OWNER_CERT_EXP_DATE 5
            负责人手机号 OWNER_PHONE 6
            所在省市	AREA_NO 7
            详细地址	MCHT_ADDR 8
            机构编号	ORG_NO 9
         */
        // 获取商户所在省市
        String areaNm = this.getAreaName(vo.getAreaNo().trim());
        // 获取机构编号
        String legalIfsOrg = this.getLegalIfsOrg(vo.getAreaNo().trim());
        // 拼接
        bf.append(vo.getEcifCustomerNo().trim()).append(UNION)
                .append(vo.getLegalCertType().trim()).append(UNION)
                .append(vo.getLegalCertNo().trim()).append(UNION)
                .append(vo.getMchtName().trim()).append(UNION)
                .append(vo.getMchtSimName().trim()).append(UNION)
                .append(IfspDateTime.getYYYYMMDD(vo.getLegalCertExpDate())).append(UNION)
                .append(vo.getLegalPhone().trim()).append(UNION)
                .append(areaNm).append(UNION)
                .append(vo.getMchtAddr().trim()).append(UNION)
                .append(legalIfsOrg).append(NEW_LINE);

        return bf;
    }

    /**
     * 组装普通商户信息
     *
     * @param vo 商户查询vo
     * @return 商户信息字符串
     */
    private StringBuilder modifyOneNormalMcht(ECIFMchtResultVo vo) {
        // 新建字段接受stringBuffer
        StringBuilder bf = new StringBuilder();
        /*  小微商户ECIF字段拼接顺序,拼接间隔字段 "|@|"
            客户号 PARTY_ID 0
            证件类型	BL_EXP_TYPE 1
            证件号码	BL_NO 2
            商户名称	MCHT_NAME 3
            商户简称	MCHT_SIM_NAME 4
            法人名称	LEGAL_CUST_NAME 5
            法人证件类型 LEGAL_CERT_TYPE 6
            法人证件号 LEGAL_CERT_NO 7
            法人证件有效期	CERT_DUE_DATA 8
            法人手机号 LEG_TEL_NO 9
            所在省市	AREA_NO 10
            详细地址	MCHT_ADDR 11
            联系人姓名 CON_TEL 12
            联系人手机号 CONN_TEL_NO 13
            营业执照有效期	 BL_EXP_DATE 14
            机构编号	INSTN_NO 15
         */
        // 获取机构编号
        String legalIfsOrg = this.getLegalIfsOrg(vo.getAreaNo().trim());
        // 获取商户所在省市
        String areaNm = this.getAreaName(vo.getAreaNo());
        // 拼接
        bf.append(vo.getEcifCustomerNo().trim()).append(UNION)
                .append(vo.getBlExpType().trim()).append(UNION)
                .append(vo.getBlNo().trim()).append(UNION)
                .append(vo.getMchtName().trim()).append(UNION)
                .append(vo.getMchtSimName().trim()).append(UNION)
                .append(vo.getLegalName().trim()).append(UNION)
                .append(vo.getLegalCertType().trim()).append(UNION)
                .append(vo.getLegalCertNo().trim()).append(UNION)
                .append(IfspDateTime.getYYYYMMDD(vo.getLegalCertExpDate())).append(UNION)
                .append(vo.getLegalPhone().trim()).append(UNION)
                .append(areaNm).append(UNION)
                .append(vo.getMchtAddr().trim()).append(UNION)
                .append(vo.getNorName().trim()).append(UNION)
                .append(vo.getNorPhone().trim()).append(UNION)
                .append(vo.getBlExpDate()).append(UNION)
                .append(legalIfsOrg).append(NEW_LINE);

        return bf;
    }

    /**
     * 通过地区码查询所在市区
     *
     * @param areaNo 地区
     * @return 市区名字
     */
    private String getAreaName(String areaNo) {
        // 通过地区码查询地区信息
        AreaInfo areaInfo = areaInfoDao.selectByPrimaryKey(areaNo);
        if (IfspDataVerifyUtil.isBlank(areaInfo)){
            throw new IfspBizException(RespConstans.RESP_AREA_NONE.getCode(),RespConstans.RESP_AREA_NONE.getDesc());
        }
        // 定义省市区名字
        String areaNm = areaInfo.getAreaName().trim();
        // 递归查询上级地区获取省市区名称
        return this.getParAreaName(areaInfo, areaNm);
    }

    /**
     * 递归获取省市区名称
     *
     * @param areaInfo
     * @param areaNm
     * @return
     */
    private String getParAreaName(AreaInfo areaInfo, String areaNm) {
        // 判断地区是否有上级地区
        if (IfspDataVerifyUtil.equals("1", areaInfo.getParAreaCode())) {
            return areaNm;
        }
        // 有上级地区
        AreaInfo par = areaInfoDao.selectByPrimaryKey(areaInfo.getParAreaCode());
        areaNm = par.getAreaName().trim() + areaNm;
        return getParAreaName(par, areaNm);
    }


    /**
     * 通过地区码查询法人机构
     *
     * @param areaNo 地区码
     * @return 法人机构
     */
    private String getLegalIfsOrg(String areaNo) {
        AreaInfo areaInfo = areaInfoDao.selectByPrimaryKey(areaNo);
        if (IfspDataVerifyUtil.equals("510100",areaNo) || IfspDataVerifyUtil.equals("510100",areaInfo.getParAreaCode()))
            areaNo = "512081";
        List<String> brNos = ifsOrgDao.selectBrAreaCode(areaNo);
        return IfspDataVerifyUtil.isEmptyList(brNos)?"":brNos.get(0);
    }
}
