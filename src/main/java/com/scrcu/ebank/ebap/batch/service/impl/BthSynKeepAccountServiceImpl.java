package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.DateUtil;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.KeepAccVo;
import com.scrcu.ebank.ebap.batch.bean.dto.NewDayTimeKeepAcctVo;
import com.scrcu.ebank.ebap.batch.bean.request.NewDayTimeKeepAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OnceKeepAcctRequest;
import com.scrcu.ebank.ebap.batch.bean.response.BthKeepAccResponse;
import com.scrcu.ebank.ebap.batch.bean.response.KeepAccResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.KeepAccRespEnum;
import com.scrcu.ebank.ebap.batch.common.dict.ReRunFlagEnum;
import com.scrcu.ebank.ebap.batch.common.dict.RealTmFlagEnum;
import com.scrcu.ebank.ebap.batch.common.dict.RespEnum;
import com.scrcu.ebank.ebap.batch.dao.KeepAccInfoDao;
import com.scrcu.ebank.ebap.batch.service.BthSynKeepAccountService;
import com.scrcu.ebank.ebap.batch.service.DayTimeKeepAccoutService;
import com.scrcu.ebank.ebap.batch.service.FeeCalcService;
import com.scrcu.ebank.ebap.framework.utils.IfspSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class BthSynKeepAccountServiceImpl implements BthSynKeepAccountService {


    @Resource
    private FeeCalcService feeCalcService;

    @Resource
    private KeepAccInfoDao keepAccInfoDao;

    @Resource
    private DayTimeKeepAccoutService dayTimeKeepAccoutServiceImpl;


    @Override
    public BthKeepAccResponse bthSynKeepAccount(NewDayTimeKeepAcctRequest request) {

        log.info("====================>>批量同步记账表开始<<====================");
        // 数据库操作(原子)
        BthSynKeepAccountService bthSynKeepAccountService = (BthSynKeepAccountService)IfspSpringContextUtils.getInstance().getBean("bthSynKeepAccountServiceImpl");
        // 数据入库并返回记账列表
        List<KeepAccInfo> keepList = bthSynKeepAccountService.saveKeepAccInfo(request);

        // 声明返回信息中的记账列表
        List<KeepAccVo> respInfo = new ArrayList<>();
        // 超时标志
        boolean timeOutFlag = false;
        // 失败标志
        boolean errorFlag = false;
        for (KeepAccInfo keepAccInfo : keepList) {
            // 未发生记账失败 , 一律尝试去记账 ,单凡有失败则终止
            if(!timeOutFlag&&!errorFlag){
                // 调用单笔记账服务返回响应
                KeepAccResponse resp = getKeepAccResponse(keepAccInfo);
                log.info("单笔记账返回信息(respData):"+IfspFastJsonUtil.mapTOjson(resp.getRespData()));
                if (RespEnum.RESP_SUCCESS.getCode().equals(resp.getRespCode())) {
                    // 记账成功
                    keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_SUCCESS);
                    respInfo.add(new KeepAccVo(keepAccInfo,resp.getRespData()));
                }  else if (RespEnum.RESP_TIMEOUT.getCode().equals(resp.getRespCode())){
                    // 记账超时
                    keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_TIMEOUT);
                    respInfo.add(new KeepAccVo(keepAccInfo,resp.getRespData()));
                    timeOutFlag = true;
                }else {
                    // 记账失败
                    keepAccInfo.setState(Constans.KEEP_ACCOUNT_STAT_FAIL);
                    respInfo.add(new KeepAccVo(keepAccInfo,resp.getRespData()));
                    errorFlag = true;

                }
            }else {
                // 未发核心的记账流水也作为返回值返回,状态为未处理
                respInfo.add(new KeepAccVo(keepAccInfo,null));
            }
        }

        log.info("====================>>批量同步记账表结束<<====================");
        // 组装返回信息
        return buildRespInfo(respInfo , timeOutFlag , errorFlag);
    }


    /**
     * 获取返回值
     * @param keepAccInfo
     * @return
     */
    private KeepAccResponse getKeepAccResponse(KeepAccInfo keepAccInfo) {
        OnceKeepAcctRequest onceKeepAcctRequest = new OnceKeepAcctRequest();
        onceKeepAcctRequest.setKeepAccInfo(keepAccInfo);
        return dayTimeKeepAccoutServiceImpl.onceKeepAccount(onceKeepAcctRequest);
    }

    /**
     * 根据序号排序
     * @param keepList
     */
    private void sortKeepAccList(List<KeepAccInfo>  keepList) {
        Collections.sort(keepList,new Comparator<KeepAccInfo>() {
            @Override
            public int compare(KeepAccInfo o1, KeepAccInfo o2) {
                return Integer.valueOf(o1.getKeepAccSeq()) -Integer.valueOf(o2.getKeepAccSeq());
            }
        });
    }

    /**
     * 事务控制入库原子性
     * @param request
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public List<KeepAccInfo> saveKeepAccInfo(NewDayTimeKeepAcctRequest request) {
        List<KeepAccInfo> keepList = new ArrayList<>();
        for (NewDayTimeKeepAcctVo dayTimeKeepAcctVo : request.getKeepAcctList()) {
            KeepAccInfo info = initKeepInfo(dayTimeKeepAcctVo,Constans.KEEP_ACCOUNT_STAT_PRE,request.getOrderSsn(),request.getOrderTm(),request.getVerNo());
            keepAccInfoDao.insert(info);
            keepList.add(info);
        }


        // 按照序号排序入账   order by seq 升序
        sortKeepAccList(keepList);
        return keepList;
    }


    /**
     * 组装返回码
     * @param respInfo
     * @param timeOutFlag
     * @param errorFlag
     * @return
     */
    private BthKeepAccResponse buildRespInfo(List<KeepAccVo> respInfo , boolean timeOutFlag , boolean errorFlag) {

        BthKeepAccResponse response = new BthKeepAccResponse();
        response.setKeepAccData(respInfo);
        response.setErrorFlag(errorFlag?Constans.TRUE_FLAG:Constans.FALSE_FLAG);
        response.setTimeOutFlag(timeOutFlag?Constans.TRUE_FLAG:Constans.FALSE_FLAG);
        response.setRespCode(KeepAccRespEnum.RESP_SUCCESS.getCode());
        response.setRespMsg(KeepAccRespEnum.RESP_SUCCESS.getDesc());
        return response;
    }


    /**
     * 初始化记账
     * @param vo
     * @param orderSsn
     * @param orderTm
     * @return
     */
    public KeepAccInfo initKeepInfo(NewDayTimeKeepAcctVo vo, String state, String orderSsn, String orderTm, String verNo)  {
        KeepAccInfo keepAccInfo = new KeepAccInfo();
        // 订单批次号
        keepAccInfo.setVerNo(verNo);
        // 同步记账
        keepAccInfo.setIsSync(Constans.KEEP_ACCT_FLAG_SYNC);
        // 核心流水
        keepAccInfo.setCoreSsn(IfspId.getUUID(20));
        // 订单号
        keepAccInfo.setOrderSsn(orderSsn);
        // 订单时间
        keepAccInfo.setOrderTm(orderTm);
        // 借方账户
        keepAccInfo.setOutAccNo(vo.getOutAccNo());
        // 借方账户名
        keepAccInfo.setOutAccNoName(vo.getOutAccName());
        // 借方账户类型
        keepAccInfo.setOutAccType(vo.getOutAccType());
        // 贷方账户
        keepAccInfo.setInAccNo(vo.getInAccNo());
        // 贷方商户名
        keepAccInfo.setInAccNoName(vo.getInAccName());
        // 贷方账户类型
        keepAccInfo.setInAccType(vo.getInAccType());
        // 划账序号
        keepAccInfo.setKeepAccSeq(Short.valueOf(vo.getSeq()));
        // 记账时间
        keepAccInfo.setKeepAccTime(DateUtil.getYYYYMMDDHHMMSS());
        // 无意义,数据库需要
        keepAccInfo.setKeepAccType(Constans.KEEP_ACC_TYPE_OTHER);
        // 记账状态
        keepAccInfo.setState(state);
        // 交易描述
        keepAccInfo.setTxnDesc(vo.getTxnDesc());
        // 交易币种
        keepAccInfo.setTxnCcyType(vo.getTxnCcyType());
        // 商户所在机构
        keepAccInfo.setProxyOrg(vo.getProxyOrg());
        // 子订单号
        keepAccInfo.setSubOrderSsn(vo.getSubOrderSsn());
        // 同步记账不支持重跑
        keepAccInfo.setRerunFlag(ReRunFlagEnum.RE_RUN_FLAG_FALSE.getCode());
        // 是否实时计算手续费 默认非实时计算
        if (IfspDataVerifyUtil.isBlank(vo.getRealTmFlag())){
            keepAccInfo.setRealTmFlag(RealTmFlagEnum.REAL_TM_FALSE.getCode());
        }else {
            keepAccInfo.setRealTmFlag(vo.getRealTmFlag());
        }

        Map<String, Long> map = calcAmt(vo,orderSsn);


        // 先转换为BigDecimal金额 , 为了支持传的金额 带有 .0 后缀
        BigDecimal stlAmt = IfspDataVerifyUtil.isBlank(map.get("stlAmt")) ?  BigDecimal.ZERO :new BigDecimal(map.get("stlAmt"));
        BigDecimal merFee = IfspDataVerifyUtil.isBlank(map.get("merFee")) ?  BigDecimal.ZERO :new BigDecimal(map.get("merFee"));
        BigDecimal commissionFee = IfspDataVerifyUtil.isBlank(map.get("commissionFee")) ? BigDecimal.ZERO :new BigDecimal(map.get("commissionFee"));

        keepAccInfo.setTransAmt(stlAmt.longValue());
        keepAccInfo.setFeeAmt(merFee);
        keepAccInfo.setCommissionAmt(commissionFee);
        // 记账摘要
        keepAccInfo.setMemo(vo.getMemo());

        // 唯一索引
        keepAccInfo.setUniqueSsn(vo.getUniqueSsn());

        return keepAccInfo;
    }



    /**
     * 计算金额
     * @param vo
     * @param orderSsn
     * @return
     */
    private Map<String, Long> calcAmt(NewDayTimeKeepAcctVo vo, String orderSsn) {
        Map<String, Long> map = new HashMap<>();
        if (RealTmFlagEnum.REAL_TM_TREU.getCode().equals(vo.getRealTmFlag())){
            // 判断是线上还是线下 (子订单不为空说明是线上)
            if(IfspDataVerifyUtil.isNotBlank(vo.getSubOrderSsn())){
                map = feeCalcService.calcMerFee4SubOrder(vo.getSubOrderSsn());
            }else {
                map = feeCalcService.calcMerFee4Order(orderSsn);
            }

        }else {
            map.put("stlAmt",Long.valueOf(vo.getTransAmt()));
            map.put("merFee",0L);
            map.put("commissionFee",0L);
        }
        return map;
    }



}
