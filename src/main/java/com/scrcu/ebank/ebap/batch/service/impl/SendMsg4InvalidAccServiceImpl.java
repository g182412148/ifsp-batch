package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.dubbo.bean.SoaParams;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAcc;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtStaffInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.NormalSmsSendReq;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.BthMerInAccDao;
import com.scrcu.ebank.ebap.batch.dao.MchtBaseInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtStaffInfoDao;
import com.scrcu.ebank.ebap.batch.dao.MchtStaffInfoTempDao;
import com.scrcu.ebank.ebap.batch.message.SmsService;
import com.scrcu.ebank.ebap.batch.service.SendMsg4InvalidAccService;
import com.scrcu.ebank.ebap.batch.soaclient.SmsSoaService;
import com.scrcu.ebank.ebap.cache.redis.operator.factory.IfspRedisCacheOperation;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SendMsg4InvalidAccServiceImpl implements SendMsg4InvalidAccService
{
    private String batchDate;

    @Autowired
    private BthMerInAccDao bthMerInAccDao;

    @Autowired
    private MchtBaseInfoDao mchtBaseInfoDao;

    @Autowired
    private MchtStaffInfoDao mchtStaffInfoDao;

    @Value("${sendMsgUseCache}")
    private String useCache;     //是否使用缓存

    @Autowired
    private SmsSoaService smsSoaService;

    @Resource
    private IfspRedisCacheOperation invalidAccCacheRedis;

    @Value("${redisKeyExpire}")
    private Long expireIn;

    @Override
    public CommonResponse sendMsg4InvalidAcc(BatchRequest request) throws Exception {
        ///账户状态错:已销户INACC=11820152621082400009

        //this.sendMsg(null);

        String batchDate = request.getSettleDate();
        if(IfspDataVerifyUtil.isBlank(batchDate))
        {
            batchDate = DateUtil.format(new Date(), "yyyyMMdd");
        }

        this.setBatchDate(batchDate);
        //汇总超时处理：
        // ((SqlSessionDaoSupport)mchtStaffInfoDao).getSqlSession().getConnection().setAutoCommit(false);

        //获取需要发送短信的商户信息
        List<BthMerInAcc> noticeList = this.getDataList();

        Iterator<BthMerInAcc> iterator = noticeList.iterator();
        while (iterator.hasNext())
        {
            BthMerInAcc merInAcc = iterator.next();

            MchtStaffInfo staffInfo = this.getContanctInfo(merInAcc.getChlMerId());
            if (staffInfo == null) continue;
            String payOrg = merInAcc.getChlMerId().substring(0,4);

            if("Y".equalsIgnoreCase(useCache))
            {
                //判断缓存中是否存在，不存在才需发送短信
                String currCacheKey = Constans.REDIS_KEY_BATCH_SENDMSG+merInAcc.getChlMerId()+"-"+staffInfo.getStaffPhone();
                if(invalidAccCacheRedis.opsForValue().get(currCacheKey) != null)
                {
                    log.info(">>>>>>>>>>>>>>已经通知过商户，两周内不再重复通知！");
                    continue;
                }
                sendMsg(staffInfo,merInAcc);
                //加入缓存
                //Jedis jedisClient = new Jedis();
                //jedisClient.set(Constans.REDIS_KEY_BATCH_SENDMSG+staffInfo.getStaffPhone(),"ALREADY-SENT");
                //jedisClient.expire(Constans.REDIS_KEY_BATCH_SENDMSG+staffInfo.getStaffPhone(),expireIn);

                invalidAccCacheRedis.opsForValue().set(currCacheKey,"ALREADY-SENT");
                invalidAccCacheRedis.opsForValue().getOperations().expire(currCacheKey,expireIn, TimeUnit.SECONDS);

            }
            else
            {
                sendMsg(staffInfo,merInAcc);
            }

        }

        CommonResponse response = new CommonResponse();
        response.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        response.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());
        return response;
    }

    /**
     * 根据商户号查找联系人信息
     * @param mchtId
     * @return
     */
    public MchtStaffInfo getContanctInfo(String mchtId)
    {
        if(!StringUtils.hasText(mchtId)) return null;
        //发送给小微商户负责人，普通商户联系人
        //1)确认商户性质
        MchtBaseInfo mchtInfo = mchtBaseInfoDao.queryById(mchtId);
        String staffRole;
        if(Constans.MCHT_NAT_TINY.equals(mchtInfo.getMchtNat()))
        {
            staffRole = Constans.STAFF_ROLE_ONCHARGE;
        }
        else
        {
            staffRole = Constans.STAFF_ROLE_CONTACT;
        }

        //2)查询商户员工联系电话（小微商户查负责人，普通商户查联系人）
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mchtId",mchtId);
        params.put("staffRole",staffRole);
        params.put("staffState",Constans.STAFF_STATE_NORMAL);

        List<MchtStaffInfo> staffInfos = mchtStaffInfoDao.selectList("selectStaffInfoByMerAndRole",params);
        if(staffInfos != null && staffInfos.size() > 0)
        {
            return  staffInfos.get(0);
        }

        return  null;
    }

    /**
     * 发送短信
     * 短信模板：
     * 【四川农信】尊敬的xxx（联系人姓名），您的商户xxxxxx（商户名称）因结算账户状态异常清算失败，
     *  请联系客户经理修改结算账户，修改后未入账交易将会清算至新账户。
     * @param staffInfo
     * @param payOrg : 短信付费机构
     */
    public void sendMsg(MchtStaffInfo staffInfo,BthMerInAcc merInAcc)
    {
        //调用短信发送接口发送短信
        /*NormalSmsSendReq smsSendReq = new NormalSmsSendReq();
        smsSendReq.setPhone("17381804009");
        smsSendReq.setBrNo("8396");
        smsSendReq.setTempCode("2801");            //短信模板
        smsSendReq.setLegalNm("狗哥");         //法人姓名
        smsSendReq.setMchtNm("挂羊头买狗肉");  //商户名称
        smsSendReq.setPwd("666666");

        smsService.sendNormalOTP(smsSendReq);*/

        String payOrg = merInAcc.getChlMerId().substring(0,4);

        /*//参数设置参考模板smsSendAccStsInvalid.ftl
        Map<String,Object> msgParams = new HashMap<String,Object>();
        msgParams.put("phone",staffInfo.getStaffPhone());
        msgParams.put("brNo",payOrg);
        msgParams.put("tempCode",Constans.MSG_TEMPLDATE_CODE_ACCSTSINVALID);
        msgParams.put("legalNm",staffInfo.getStaffName());
        msgParams.put("mchtNm",merInAcc.getChlMerName());

        smsService.sendMsg(msgParams,Constans.MSG_TEMPLDATE_FILE_NAME_ACCSTSINVALID);*/

        //使用通道服务发送短信
        SoaParams soaParams = new SoaParams();
        soaParams.put("tempCode", Constans.MSG_TEMPLDATE_CODE_ACCSTSINVALID);    //短信模板编号
        soaParams.put("brNo",payOrg);

        Map<String,Object> tempData = new HashMap<String,Object>();
        tempData.put("phone", staffInfo.getStaffPhone());
        tempData.put("legalNm", staffInfo.getStaffName());
        tempData.put("mchtNm", merInAcc.getChlMerName());

        soaParams.put("tempData", tempData);
        smsSoaService.sendMsg(soaParams);

        System.out.println("test>>>>>>>>>>>>>>>>>>>>>>>>>>");

    }

    public List<BthMerInAcc> getDataList()
    {
        Map<String,Object> params = new HashMap<String,Object>();

        params.put("inAcctStat", Constans.IN_ACC_STAT_FAIL);
        params.put("dateStlm",batchDate);
        params.put("coreRespCode",Constans.CORE_RESP_CODE_ACCT_STATE_INVALID);

        List<BthMerInAcc> noticeList = bthMerInAccDao.selectList("selectNoticeList4InvalidAcc",params);

        return noticeList;
    }

    public String getBatchDate() {
        return batchDate;
    }

    public void setBatchDate(String batchDate) {
        this.batchDate = batchDate;
    }
}