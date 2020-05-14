package com.scrcu.ebank.ebap.batch.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import javax.annotation.Resource;

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.dao.test.BillRecoErrDao;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.request.AcctContrastRequest;
import com.scrcu.ebank.ebap.batch.bean.response.AcctContrastResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.ConstantUtil;
import com.scrcu.ebank.ebap.batch.common.utils.ReflectionUtil;
import com.scrcu.ebank.ebap.batch.service.UnionAcctContrastService;
import com.scrcu.ebank.ebap.exception.IfspBizException;

import lombok.extern.slf4j.Slf4j;
/**
 * 名称：〈通道对账〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年08月03日 <br>
 * 作者：lijingbo <br>
 * 说明：<br>
 */
@Service
@Slf4j
public class UnionAcctContrastServiceImpl implements UnionAcctContrastService {

	@Resource
	private BthUnionFileDetDao bthUnionFileDetDao;

	@Resource
	private BthChkRsltInfoDao bthChkRsltInfoDao;
	
	@Resource
	private BthPagyChkErrInfoDao bthPagyChkErrInfoDao;
	
	@Resource
	private BthPagyLocalInfoDao bthPagyLocalInfoDao;
	
	@Resource
	private PayOrderInfoDao payOrderInfoDao;
	
	@Resource
	private PagyTxnInfoDao pagyTxnInfoDao;

    @Resource
    private BthChkUnequalInfoDao bthChkUnequalInfoDao;

    @Resource
    private UnionBillLocalDao unionBillLocalDao;

    @Resource
    private UnionBillOuterDao unionBillOuterDao;

    @Resource
    private BillRecoErrDao billRecoErrDao;

    @Resource
    private UnionQrcBillResultDao resultDao;

    /**
     * 处理线程数量
     */
    @Value("${unionReco.threadCount}")
    private Integer unionThreadCount;
    /**
     * 每个线程处理数据量
     */
    @Value("${unionReco.threadCapacity}")
    private Integer unionThreadCapacity;
    /**
     * 线程池
     */
    ExecutorService executor;

    /**
     * 银联二维码对账
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public AcctContrastResponse unionBillContrastNew(AcctContrastRequest request) throws Exception {
        log.info("---------------------银联通道系统vs银联二维码对账开始--------------------");
        long start=System.currentTimeMillis();
        //获取对账日期
        Date recoDate = getRecoDate(request.getSettleDate());
        log.info("获取对账日期--->"+recoDate);
        //数据清理
        log.info("银联对账数据清理-begin");
        clear(recoDate, Constans.UNION_SYS_NO);
        log.info("银联对账数据清理-end");
        //初始化线程池
//        log.info("银联对账线程池初始化");
//        initPool();
        //对账
        log.info("银联对账-begin");
        reco(recoDate, Constans.UNION_SYS_NO);
//        try{
//            reco(recoDate, request.getPagySysNo());
//        }finally {
//            destoryPool();
//        }

        log.info("银联二维码对账结束，对账日期【{}】，总耗时【{}】", DateUtil.toDateString(recoDate), System.currentTimeMillis()-start);
        return new AcctContrastResponse();
    }

    /**
     * 数据清理
     * @param recoDate
     */
    private void clear(Date recoDate, String pagyNo) {
        long start=System.currentTimeMillis();
        //恢复本地当天对账明细和前一天可疑 recoveryDubious
        int recCount = unionBillLocalDao.recovery(recoDate, pagyNo);
        log.info("恢复本地当天对账明细, 数量【{}】，耗时【{}】",recCount,System.currentTimeMillis()-start);
        int recCountDub = unionBillLocalDao.recoveryDubious(recoDate, pagyNo);
        log.info("恢复本地前一天可疑明细, 数量【{}】，耗时【{}】",recCountDub,System.currentTimeMillis()-start);
        //恢复银联当天对账明细和前一天可疑
        int recOutCount = unionBillOuterDao.recovery(recoDate, pagyNo);
        log.info("恢复银联当天对账明细, 数量【{}】，耗时【{}】", recOutCount,System.currentTimeMillis()-start);
        int recOutCountDub = unionBillOuterDao.recoveryDubious(recoDate, pagyNo);
        log.info("恢复银联前一天可疑明细, 数量【{}】，耗时【{}】", recOutCountDub,System.currentTimeMillis()-start);
        //清空平账结果
        log.info("清空当天平账结果表，耗时【{}】",System.currentTimeMillis()-start);
        bthChkRsltInfoDao.deleteAliByStlmDateAndPagySysNo(IfspDateTime.getYYYYMMDD(recoDate), pagyNo+" ");
        //清空差错结果
        log.info("清空当天差错结果表，耗时【{}】",System.currentTimeMillis()-start);
        billRecoErrDao.clear(recoDate, pagyNo);
        //清空银联二维码对账结果表
        int resultCount = resultDao.clear();
        log.info("清空银联对账结果, 数量【{}】，耗时【{}】" , resultCount,System.currentTimeMillis()-start);

    }

    /**
     * 对账
     * @param recoDate
     */
    private void reco(Date recoDate, String pagyNo){
        long start=System.currentTimeMillis();
        /*
         * 查询本地流水表的数据总数
         */
        int count = unionBillLocalDao.count(recoDate, pagyNo);
        log.info("本地需对账数据, 数量【{}】，耗时【{}】",count,System.currentTimeMillis()-start);
        /** 自本行起为本次银联二维码对账改造代码 start */
        //将本地流水表(UNION_BILL_LOCAL)与三方流水表(UNION_BILL_OUTER)进行比对，比对结果插入至银联二维码对账结果表(UNION_QRC_BILL_RESULT)
        int unionQrcChkResultCount = resultDao.insertUnionQrcResult(recoDate);
        log.info("本次银联二维码对账共勾兑流水, 数量【{}】，耗时【{}】",unionQrcChkResultCount,System.currentTimeMillis()-start);
        //统计银联二维码对账结果表中本地可疑和本地单边流水数量
        int localDebiousCount = resultDao.countLocal();
        log.info("本次银联二维码对账本地可疑和本地单边流水, 数量【{}】，耗时【{}】",localDebiousCount,System.currentTimeMillis()-start);
        //统计银联二维码对账结果表中三方可疑和三方单边流水数量
        int outerDebiousCount = resultDao.countOuter();
        log.info("本次银联二维码对账三方可疑和三方单边流水, 数量【{}】，耗时【{}】",outerDebiousCount,System.currentTimeMillis()-start);
        //将差错记录插入到本地差错记录表
        int errCount = billRecoErrDao.insertUnionQrcErrResult(recoDate);
        log.info("本次银联二维码对账差错记录, 数量【{}】，耗时【{}】",errCount,System.currentTimeMillis()-start);
        //将银联二维码对账结果插入到对账结果表
        int chkResultCount = bthChkRsltInfoDao.insertUnionQrcChkResult(recoDate);
        log.info("本次银联二维码对账已对账记录, 数量【{}】，耗时【{}】",chkResultCount,System.currentTimeMillis()-start);
        //更新本地流水表
        int localCount = unionBillLocalDao.updateByUnionQrcResult(recoDate);
        log.info("本次银联二维码对账共勾兑本地流水, 数量【{}】，耗时【{}】",localCount,System.currentTimeMillis()-start);
        //更新三方流水表
        int outerCount = unionBillOuterDao.updateByUnionQrcResult(recoDate);
        log.info("本次银联二维码对账共勾兑三方流水, 数量【{}】，耗时【{}】",outerCount,System.currentTimeMillis()-start);
        /** end */
        //分组数量
//        int groupCount = (int) Math.ceil((double) count / unionThreadCapacity);
//        log.info("总分组数量[{}]页", groupCount);
        /*
         * 已本地为准 勾兑 第三方
         */
        //处理结果
//        List<Future> futureList = new ArrayList<>();
//        for (int groupIndex = 1; groupIndex <= groupCount; groupIndex++) {
//            int minIndex = (groupIndex - 1) * unionThreadCapacity + 1;
//            int maxIndex = groupIndex * unionThreadCapacity;
//            log.info("处理第[{}]组数据", groupIndex);
//            Future future = executor.submit(new Handler(recoDate, new DataInterval(minIndex, maxIndex), pagyNo));
//            futureList.add(future);
//        }
//        /*
//         * 获取处理结果
//         */
//        log.info("获取处理结果。。。。。。");
//        for (Future future : futureList) {
//            try {
//                future.get(10, TimeUnit.MINUTES);
//            } catch (Exception e) {
//                log.error("对账线程处理异常: ", e);
//                //取消其他任务
//                executor.shutdownNow();
//                log.warn("其他子任务已取消.");
//                //返回结果
//                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常");
//            }
//        }
//        /*
//         * 处理三方未对账数据
//         *      1) 可疑交易, 更改对账状态为可疑
//         *      2) 单边交易, 更新对账状态为已对账; 记入对账差错表;
//         */
//        log.info("处理三方未对账数据-begin");
//        List<UnionBillOuter> outerList = unionBillOuterDao.queryNotReco(recoDate, pagyNo);
//        if(outerList == null || outerList.isEmpty()){
//            log.info("三方无单边或可疑明细");
//        }else {
//            for(UnionBillOuter outer : outerList){
//                //可疑交易
//                if(isDubious(recoDate, outer.getRecoDate())){
//                    unionBillOuterDao.updateById(new UnionBillOuter(outer.getTxnSsn(), RecoStatusDict.FINISH.getCode(), outer.getRecoDate(), recoDate, DubisFlagDict.TRUE.getCode()));
//                }else {
//                    billRecoErrDao.insert(getRecoErr(recoDate,null, outer, pagyNo, RecoResultDict.OUTER_UA));
//                    unionBillOuterDao.updateById(new UnionBillOuter(outer.getTxnSsn(), RecoStatusDict.FINISH.getCode(), outer.getRecoDate(), recoDate));
//                }
//            }
//        }
    }

	@Override
	public AcctContrastResponse unionBillContrast(AcctContrastRequest request) throws Exception {
		log.info("---------------------银联通道系统vs银联对账开始--------------------");
        /*****获取请求参数值********/
		 String pagySysNo = request.getPagySysNo();//通道系统编号
		 String settleDate = request.getSettleDate();// 清算日期
		 
		 /*********初始化响应对象***********/
		 AcctContrastResponse acctContrastResponse = new AcctContrastResponse();
        log.info("date:"+settleDate);
        String doubtDate=IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, -1);
        log.info("doubtDate:"+doubtDate);
        // 对账成功日期
        String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
        log.info("chkSuccDt:"+chkSuccDt);
        //1.	根据清算日期和通道系统编号删除对账结果表【清算中心】和对账差错表【清算中心】
        log.info("---------------------删除通道对账结果表和通道对账差错表里清算日期为当前系统日期T-1的流水------------------");
        bthChkRsltInfoDao.deleteAliByStlmDateAndPagySysNo(chkSuccDt,pagySysNo);
        bthPagyChkErrInfoDao.deleteByStlmDateAndPagySysNo(settleDate,pagySysNo);

        // ------------------      modify by ljy   date: 20190423      start  ------------------------------------------------
        log.info("-------------------删除对账不平表清算日[{}]跑进去的银联二维码数据------------------",settleDate);
        // 这里不用 request.getPagySysNo() (因为传的值带空格, eg: "607 " ) ,对账不平表是后面建的 ,类型为 CHAR3 ,所以这里不用空格
        bthChkUnequalInfoDao.deleteByChkUeqDtAndPagySysNo(chkSuccDt,"607");
        // ------------------      modify by ljy   date: 20190423      end      -----------------------------------------------



        //2.	根据清算日期和通道系统编号查询银联对账文件明细表【清算中心】和本地通道交易明细对账表【清算中心】，更新状态为未对账,对账结果还原
        List<BthPagyLocalInfo> bthPagyLocalInfos=bthPagyLocalInfoDao.selectAliByDateAndPagyno(settleDate,pagySysNo);
        log.info("---------------------更新本地通道交易明细对账表"+bthPagyLocalInfos.size()+"条---------------------");
        if(bthPagyLocalInfos!=null){
            for(BthPagyLocalInfo bthPagyLocalInfo:bthPagyLocalInfos){
           	 bthPagyLocalInfo.setChkSt(Constans.CHK_STATE_00);
           	 bthPagyLocalInfo.setChkRst("");
             bthPagyLocalInfo.setDubiousFlag("");
           	 bthPagyLocalInfoDao.updateByPrimaryKeySelective(bthPagyLocalInfo);
            }
        }
        Map<String,Object> params = new HashMap<String,Object>();
		params.put("settleDate", settleDate);
		params.put("pagySysNo",pagySysNo);
        List<BthUnionFileDet> bthUnionFileDets=bthUnionFileDetDao.selectList("selectUnionFileByDateAndPagySysNo", params);
        log.info("---------------------更新银联对账文件明细表"+bthUnionFileDets.size()+"条---------------------");
        if(bthUnionFileDets!=null){
            for(BthUnionFileDet bthUnionFileDet:bthUnionFileDets){
           	 bthUnionFileDet.setChkAcctSt(Constans.CHK_STATE_00);
           	 bthUnionFileDet.setChkRst("");
           	 bthUnionFileDet.setDubiousFlag("");
           	 bthUnionFileDetDao.updateByPrimaryKeySelective(bthUnionFileDet);
            }
        }
        //TODO 前天可疑三方/本地流水信息表状态（ChkAcctSt，ChkRst）还原
        Map<String,Object> msg = new HashMap<String,Object>();
        msg.put("settleDate", doubtDate);
        msg.put("pagySysNo", pagySysNo);
        msg.put("dubiousFlag", Constans.DUBIOUS_FLAG_01);
        List<BthPagyLocalInfo> bthPagyLocalDoubtInfos=bthPagyLocalInfoDao.selectList("queryThreeLocalByFlagAndDate",msg);
        log.info("---------------------更新可疑本地通道交易明细对账表"+bthPagyLocalDoubtInfos.size()+"条---------------------");
        if(bthPagyLocalDoubtInfos!=null){
            for(BthPagyLocalInfo bthPagyLocalDoubtInfo:bthPagyLocalDoubtInfos){
           	 bthPagyLocalDoubtInfo.setChkSt(Constans.CHK_STATE_00);
           	 bthPagyLocalDoubtInfo.setChkRst(Constans.CHK_RST_02);
           	 bthPagyLocalInfoDao.updateByPrimaryKeySelective(bthPagyLocalDoubtInfo);
            }
        }
        Map<String,Object> msgs = new HashMap<String,Object>();
        msgs.put("settleDate", doubtDate);
        msgs.put("pagySysNo", pagySysNo);
        msgs.put("dubiousFlag", Constans.DUBIOUS_FLAG_01);
        List<BthUnionFileDet> bthUnionDoubtFileDets=bthUnionFileDetDao.selectList("queryUnionFileByFlagAndDateAndPagySysNo",msgs);
        log.info("---------------------更新可疑银联对账文件明细表"+bthUnionDoubtFileDets.size()+"条---------------------");
        if(bthUnionDoubtFileDets!=null){
            for(BthUnionFileDet bthUnionDoubtFileDet:bthUnionDoubtFileDets){
            	bthUnionDoubtFileDet.setChkAcctSt(Constans.CHK_STATE_00);
            	bthUnionDoubtFileDet.setChkRst(Constans.CHK_RST_02);
           	 	bthUnionFileDetDao.updateByPrimaryKeySelective(bthUnionDoubtFileDet);
            }
        }
        log.info("---------------------银联可疑流水对账开始---------------------");
        unionDoubt(settleDate,doubtDate,pagySysNo);
        log.info("---------------------通道系统可疑流水对账开始---------------------");
        systemDoubt(settleDate,doubtDate,pagySysNo);
        log.info("---------------------未对账流水对账开始---------------------");
        unionVsSystem(settleDate,pagySysNo);
        log.info("---------------------剩余通道对账表未匹配流水开始---------------------");
        systemVsUnion(settleDate,pagySysNo);
		return acctContrastResponse;
	}




    /**
	 * 银联可疑流水对账
	 * @param settleDate
	 * @param doubtDate
	 * @param pagySysNo
	 * @throws Exception
	 */
	private void unionDoubt(String settleDate, String doubtDate, String pagySysNo) throws Exception {
		log.info("---------------------银联可疑流水对账开始---------------------");
        // 对账成功日期
        String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
        log.info("chkSuccDt:"+chkSuccDt);
		SimpleDateFormat format = new SimpleDateFormat(IfspDateTime.YYYYMMDD);
//        List<BthChkRsltInfo> resultList=bthChkRsltInfoDao.selectDateAndStat(doubtDate,Constans.THIRD_PARTY_SUSP);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("chkDate", doubtDate);
        params.put("pagySysNo", pagySysNo);
		params.put("chkRst", Constans.CHK_RST_02);
        List<BthUnionFileDet> unionList=bthUnionFileDetDao.selectList("selectUnionFileByDateAndChkRstAndPagySysNo", params);
        log.info("---------------------银联可疑流水"+unionList.size()+"条---------------------");
        List<BthPagyLocalInfo> localList=bthPagyLocalInfoDao.selectAliByDateAndStat(settleDate,Constans.CHK_STATE_00,pagySysNo);
        log.info("---------------------本地待对账流水"+localList.size()+"条---------------------");
        for(BthUnionFileDet unionDoubtRecord:unionList){
            boolean exist_flag = false;
            for(BthPagyLocalInfo localUnionRecord:localList){
            	String merOrderId=unionDoubtRecord.getProxyInsCode()+unionDoubtRecord.getSendInsCode()+unionDoubtRecord.getTraceNum()+unionDoubtRecord.getTransDate();
                /*
                 * 匹配交易流水号看是否存在 :
                 * 1.正常两边都成功 , 可以用清算主键来进行匹配 (不能用通道流水匹配对账文件!);
                 * 2.如果银联成功,本地是处理中 (只可能是用户银联被扫交易 , 用户主扫通知过来一定是终态 ; 用户银联被扫过我们系统 , 可以用通道流水匹配对账文件), 则清算主键值为null ,无法用清算主键来进行匹配 ,
                 * 此时尝试通过通道流水来匹配 ;
                 *
                 */

                if(merOrderId.equals(localUnionRecord.getTpamTxnSsn()) ||  unionDoubtRecord.getOrderId().equals(localUnionRecord.getPagyPayTxnSsn()) ){
                    exist_flag=true;
                    boolean checkResult = true; 
                    boolean amt=false;
                    boolean state=false;
                    boolean type=false;
                    BigDecimal unionAmt=null;
                    String tradeSt="00";
                    unionAmt=unionDoubtRecord.getTransAmt();
                    /**比交易类型*/
                    if(!unionDoubtRecord.getTransCode().equals(localUnionRecord.getTxnChlAction())){
                    	checkResult=false;
                    	type=true;
                    }
                  /**比状态*/
                  if(!tradeSt.equals(localUnionRecord.getTradeSt())){
                      checkResult=false;
                      state=true;
                  }
                  /**比金额*/
                  if(!unionAmt.toString().equals(localUnionRecord.getTxnAmt().toString())){
                	  checkResult=false;                     
                	  amt=true;
                  }
                  
                  if(checkResult){
                      /** 对账成功 */
                	  localUnionRecord.setChkSt(Constans.CHK_STATE_01);
                      localUnionRecord.setChkRst(Constans.CHK_RST_00);
//                      localWechatRecord.setChkDataDt(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDD(), IfspDateTime.YYYYMMDD));
                      bthPagyLocalInfoDao.updateByPrimaryKeySelective(localUnionRecord);
                      unionDoubtRecord.setChkAcctSt(Constans.CHK_STATE_01);
                      unionDoubtRecord.setChkRst(Constans.CHK_RST_00);
//                      wechatDoubtRecord.setChkDataDt(IfspDateTime.getYYYYMMDD());
                      bthUnionFileDetDao.updateByPrimaryKeySelective(unionDoubtRecord);
                      //插入对账结果结果表
                      BthChkRsltInfo record=new BthChkRsltInfo();
                      ReflectionUtil.copyProperties(localUnionRecord,record);
                      record.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                      record.setChkSt(Constans.CHK_STATE_00);
                      record.setChkRst("");
                      Map<String, Object> param = new HashMap<String, Object>();
              		param.put("pagyTxnSsn", localUnionRecord.getPagyTxnSsn());
              		PagyTxnInfo pagyTxnInfo = pagyTxnInfoDao.selectOne("selectPagyTxnInfoByPagyTxnSsn", param);
              		if(pagyTxnInfo==null){
              			throw new IfspBizException("9999", "根据通道内部订单号查询通道核心信息失败！");
              		}
              		String orderSsn =pagyTxnInfo.getTxnReqSsn();
            		if(pagyTxnInfo.getTxnReqSsn().length()!=20){
            			PayOrderInfo payOrderInfo = payOrderInfoDao.selectByPagyTxnSsn(localUnionRecord.getPagyTxnSsn());
    					if(payOrderInfo==null){
    						throw new IfspBizException("9999", "根据通道交易流水号<"+localUnionRecord.getPagyTxnSsn()+">查询订单信息失败！");
    					}
    					orderSsn = payOrderInfo.getOrderSsn();
            		}
                      record.setOrderSsn(orderSsn);
                      record.setStlmSt(Constans.STLM_ST_00);
                      record.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                      record.setTpamTxnFeeAmt(Long.parseLong(unionDoubtRecord.getCustomerFee()));
                      BigDecimal brandFee = BigDecimal.ZERO;
                      if (IfspDataVerifyUtil.isNotBlank(unionDoubtRecord.getBrandFee())){
                          brandFee = unionDoubtRecord.getBrandFee();
                      }
                      record.setUnionBrandFee(brandFee);
                      bthChkRsltInfoDao.insert(record); 
                  }else{



                      // -------- 对账不平, 发现状态不一致进对账不平表 modify by ljy    modify_date : 20190422       start  -------
                      if (state){
                          initChkUnequalInfo(unionDoubtRecord, localUnionRecord, unionAmt, tradeSt, chkSuccDt);
                      }else {
                          // --------       modify end   --------------------------------------------------------------------------
                          BthPagyChkErrInfo bthBalErrRecord=new BthPagyChkErrInfo(unionDoubtRecord);
                          //添加差错标志
                          if(type==true&&state==false&&amt==false){
                              bthBalErrRecord.setErrTp(Constans.ERR_TP_100);
                              bthBalErrRecord.setErrDesc("交易类型不一致");
                          }

                          if(type==false&&state==false&&amt==true){
                              bthBalErrRecord.setErrTp(Constans.ERR_TP_001);
                              bthBalErrRecord.setErrDesc("金额不一致");
                          }

                          if(type==true&&state==false&&amt==true){
                              bthBalErrRecord.setErrTp(Constans.ERR_TP_101);
                              bthBalErrRecord.setErrDesc("交易类型金额均不一致");
                          }
                          bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                          bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                          bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                          bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                          bthPagyChkErrInfoDao.insert(bthBalErrRecord);
                      }
                      //设置本地对账流水表对账状态为已对账
                      localUnionRecord.setChkSt(Constans.CHK_STATE_01);
                      localUnionRecord.setChkRst(Constans.CHK_RST_01);
                      bthPagyLocalInfoDao.updateByPrimaryKeySelective(localUnionRecord);
                      unionDoubtRecord.setChkAcctSt(Constans.CHK_STATE_01);
                      unionDoubtRecord.setChkRst(Constans.CHK_RST_01);
                      bthUnionFileDetDao.updateByPrimaryKeySelective(unionDoubtRecord);
                  } 
                  break;
              }  
            }
            //连续两天都没有匹配到
            if(exist_flag == false){
                BthPagyChkErrInfo bthBalErrRecord=new BthPagyChkErrInfo(unionDoubtRecord);
                bthBalErrRecord.setErrDesc("连续两天可疑");
                bthBalErrRecord.setErrTp(Constans.ERR_TP_02);
                bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                bthPagyChkErrInfoDao.insert(bthBalErrRecord);
                unionDoubtRecord.setChkAcctSt(Constans.CHK_STATE_01);
                unionDoubtRecord.setChkRst(Constans.CHK_RST_03);
                bthUnionFileDetDao.updateByPrimaryKeySelective(unionDoubtRecord);
            }                
        }
        
    
	}


    /**
	 * 通道系统可疑流水对账
	 * @param settleDate
	 * @param doubtDate
	 * @param pagySysNo
	 * @throws Exception
	 */
	private void systemDoubt(String settleDate, String doubtDate, String pagySysNo) throws Exception {
		log.info("---------------------通道系统可疑流水对账开始---------------------");
        // 对账成功日期
        String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
        log.info("chkSuccDt:"+chkSuccDt);
		SimpleDateFormat format = new SimpleDateFormat(IfspDateTime.YYYYMMDD);
        List<BthPagyLocalInfo> resultList=new ArrayList<BthPagyLocalInfo>();
        //银联对账流水
        List<BthUnionFileDet> unionList=new ArrayList<BthUnionFileDet>();
        Map<String,Object> msg = new HashMap<String,Object>();
        msg.put("settleDate", doubtDate);
        msg.put("chkRst", Constans.CHK_RST_02);
        msg.put("pagySysNo", pagySysNo);
		resultList=bthPagyLocalInfoDao.selectList("selectPagyDateAndChkstat",msg);
        log.info("---------------------通道可疑流水"+resultList.size()+"条---------------------");
        Map<String,Object> params = new HashMap<String,Object>();
		params.put("chkDate", settleDate);
		params.put("chkSt", Constans.CHK_STATE_00);
        params.put("pagySysNo", pagySysNo);
		unionList=bthUnionFileDetDao.selectList("selectUnionFileByDateAndChkstatAndPagySysNo",params);
        log.info("---------------------银联对账流水"+unionList.size()+"条---------------------");
       
        for(BthPagyLocalInfo systemDoubtRecord:resultList){
            boolean exist_flag = false;
            for(BthUnionFileDet unionRecord:unionList){
            	String merOrderId=unionRecord.getProxyInsCode()+unionRecord.getSendInsCode()+unionRecord.getTraceNum()+unionRecord.getTransDate();
                /*
                 * 匹配交易流水号看是否存在 :
                 * 正常两边都成功 , 可以用清算主键来进行匹配 (不能用通道流水匹配对账文件!);
                 * 如果银联成功,本地是处理中 (只可能是用户银联被扫交易 , 用户主扫通知过来一定是终态 ; 用户银联被扫过我们系统 , 可以用通道流水匹配对账文件), 则清算主键值为null ,无法用清算主键来进行匹配 ,
                 * 此时尝试通过通道流水来匹配 ;
                 *
                 */

                if(merOrderId.equals(systemDoubtRecord.getTpamTxnSsn()) || unionRecord.getOrderId().equals(systemDoubtRecord.getPagyPayTxnSsn())){
                    exist_flag=true;
                    boolean checkResult = true;                        
                    boolean amt=false;
                    boolean type=false;
                    boolean state=false;
                    
                    BigDecimal unionAmt=null;
                    String tradeSt="00";
                    unionAmt=unionRecord.getTransAmt();
                    
                    /**比交易类型*/
                    if(!unionRecord.getTransCode().equals(systemDoubtRecord.getTxnChlAction())){
                    	checkResult=false;
                    	type=true;
                    }              
                    /**比状态*/
                    if(!tradeSt.equals(systemDoubtRecord.getTradeSt())){
                        checkResult=false;
                        state=true;
                    }
                    /**比金额*/
                    if(!unionAmt.toString().equals(systemDoubtRecord.getTxnAmt().toString())){
                    	checkResult=false;
                    	amt=true;
                    }
                    
                    if(checkResult){
                    	/** 对账成功 */ 
                    	systemDoubtRecord.setChkSt(Constans.CHK_STATE_01);
                    	systemDoubtRecord.setChkRst(Constans.CHK_RST_00);
//                    	systemDoubtRecord.setChkDataDt(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDD(), IfspDateTime.YYYYMMDD));
                         bthPagyLocalInfoDao.updateByPrimaryKeySelective(systemDoubtRecord);
                         unionRecord.setChkAcctSt(Constans.CHK_STATE_01);
                         unionRecord.setChkRst(Constans.CHK_RST_00);
//                         wechatRecord.setChkDataDt(IfspDateTime.getYYYYMMDD());
                         bthUnionFileDetDao.updateByPrimaryKeySelective(unionRecord);
                         //插入对账结果结果表
                         BthChkRsltInfo record=new BthChkRsltInfo();
                         ReflectionUtil.copyProperties(systemDoubtRecord,record);
                         record.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                         record.setChkSt(Constans.CHK_STATE_00);
                         record.setChkRst("");
                         Map<String, Object> param = new HashMap<String, Object>();
                 		param.put("pagyTxnSsn", systemDoubtRecord.getPagyTxnSsn());
                 		PagyTxnInfo pagyTxnInfo = pagyTxnInfoDao.selectOne("selectPagyTxnInfoByPagyTxnSsn", param);
                 		if(pagyTxnInfo==null){
                 			throw new IfspBizException("9999", "根据通道内部订单号查询通道核心信息失败！");
                 		}
                 		String orderSsn =pagyTxnInfo.getTxnReqSsn();
                		if(pagyTxnInfo.getTxnReqSsn().length()!=20){
                			PayOrderInfo payOrderInfo = payOrderInfoDao.selectByPagyTxnSsn(systemDoubtRecord.getPagyTxnSsn());
        					if(payOrderInfo==null){
        						throw new IfspBizException("9999", "根据通道交易流水号<"+systemDoubtRecord.getPagyTxnSsn()+">查询订单信息失败！");
        					}
        					orderSsn = payOrderInfo.getOrderSsn();
                		}
                         record.setOrderSsn(orderSsn);
                         record.setStlmSt(Constans.STLM_ST_00);
                         record.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                         //解决重跑未删除对成功的对账结果表
                         record.setTpamTxnTm(unionRecord.getChkDataDt());
                         record.setTpamTxnFeeAmt(Long.parseLong(unionRecord.getCustomerFee()));
                        BigDecimal brandFee = BigDecimal.ZERO;
                        if (IfspDataVerifyUtil.isNotBlank(unionRecord.getBrandFee())){
                            brandFee = unionRecord.getBrandFee();
                        }
                        record.setUnionBrandFee(brandFee);
                         bthChkRsltInfoDao.insert(record); 
                  
                    }else{

                        // -------- 对账不平, 发现状态不一致进对账不平表 modify by ljy    modify_date : 20190425       start  -------
                        if (state){
                            initChkUnequalInfo(unionRecord, systemDoubtRecord, unionAmt, tradeSt, chkSuccDt);
                        }else {
                            // --------       modify end   --------------------------------------------------------------------------
                            //标识原因且清算日期改为T-1插入通道对账差错表，
                            //删除通道对账结果该流水，
                            //并更新第三方对账表流水为对账失败
                            BthPagyChkErrInfo bthBalErrRecord=new BthPagyChkErrInfo(systemDoubtRecord);
                            //添加差错标志
                            if(type==true&&state==false&&amt==false){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_100);
                                bthBalErrRecord.setErrDesc("交易类型不一致");
                            }

                            if(type==false&&state==false&&amt==true){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_001);
                                bthBalErrRecord.setErrDesc("金额不一致");
                            }

                            if(type==true&&state==false&&amt==true){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_101);
                                bthBalErrRecord.setErrDesc("交易类型金额均不一致");
                            }
                            bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                            bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                            bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                            bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                            bthPagyChkErrInfoDao.insert(bthBalErrRecord);
                        }

                      //设置本地对账流水表对账状态为已对账
                        systemDoubtRecord.setChkSt(Constans.CHK_STATE_01);
                        systemDoubtRecord.setChkRst(Constans.CHK_RST_01);
                        bthPagyLocalInfoDao.updateByPrimaryKeySelective(systemDoubtRecord);
                        unionRecord.setChkAcctSt(Constans.CHK_STATE_01);
                        unionRecord.setChkRst(Constans.CHK_RST_01);
                        bthUnionFileDetDao.updateByPrimaryKeySelective(unionRecord);
                    }
                    break;
                }  
            }
            //连续两天都没有匹配到
            if(exist_flag == false){
                BthPagyChkErrInfo bthBalErrRecord=new BthPagyChkErrInfo(systemDoubtRecord);
                bthBalErrRecord.setErrDesc("连续两天可疑");
                bthBalErrRecord.setErrTp(Constans.ERR_TP_01);
                bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                bthPagyChkErrInfoDao.insert(bthBalErrRecord);
                systemDoubtRecord.setChkSt(Constans.CHK_STATE_01);
                systemDoubtRecord.setChkRst(Constans.CHK_RST_03);
                bthPagyLocalInfoDao.updateByPrimaryKeySelective(systemDoubtRecord);
            }
        }
        
    
	}
	
	/**
	 * 未对账流水对账
	 * @param settleDate
	 * @param pagySysNo
	 * @throws Exception
	 */
	private void unionVsSystem(String settleDate, String pagySysNo) throws Exception {
		log.info("---------------------未对账流水对账开始---------------------");
        // 对账成功日期
        String chkSuccDt = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
        log.info("chkSuccDt:"+chkSuccDt);
		SimpleDateFormat format = new SimpleDateFormat(IfspDateTime.YYYYMMDD);
        //本地待清算流水
        List<BthPagyLocalInfo> localList=new ArrayList<BthPagyLocalInfo>();
        //3.	根据清算日期，通道系统编号及状态（未对账）查询银联对账文件明细表和本地通道交易明细对账表
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("pagySysNo", pagySysNo);
        params.put("chkDate", settleDate);
        params.put("chkSt", Constans.CHK_STATE_00);
		List<BthUnionFileDet> unionList=bthUnionFileDetDao.selectList("selectUnionFileByDateAndChkstatAndPagySysNo",params);
        log.info("---------------------银联待对账流水"+unionList.size()+"条---------------------");
        localList=bthPagyLocalInfoDao.selectAliByDateAndStat(settleDate,Constans.CHK_STATE_00,pagySysNo);
        log.info("---------------------本地待对账流水"+localList.size()+"条---------------------");
        for(BthUnionFileDet unionRecord:unionList){
            boolean exist_flag = false;
            for(BthPagyLocalInfo localUnionRecord:localList){
                String merOrderId=unionRecord.getProxyInsCode()+unionRecord.getSendInsCode()+unionRecord.getTraceNum()+unionRecord.getTransDate();
                /*
                 * 匹配交易流水号看是否存在 :
                 * 正常两边都成功 , 可以用清算主键来进行匹配 (不能用通道流水匹配对账文件!);
                 * 如果银联成功,本地是处理中 (只可能是用户银联被扫交易 , 用户主扫通知过来一定是终态 ; 用户银联被扫过我们系统 , 可以用通道流水匹配对账文件), 则清算主键值为null ,无法用清算主键来进行匹配 ,
                 * 此时尝试通过通道流水来匹配 ;
                 *
                 */

                if(merOrderId.equals(localUnionRecord.getTpamTxnSsn()) || unionRecord.getOrderId().equals(localUnionRecord.getPagyPayTxnSsn())){
                    exist_flag=true;
                    boolean checkResult = true;                       
                    boolean amt=false;
                    boolean type=false;
                    boolean state=false;
                    BigDecimal unionAmt=null;
                    String tradeSt="00";
                    unionAmt=unionRecord.getTransAmt();
                    
                    /**比交易类型*/
                    if(!unionRecord.getTransCode().equals(localUnionRecord.getTxnChlAction())){
                    	// 插入对账差错表,更新两表状态已对账
                    	checkResult=false;
                    	type=true;
                    }              
                    /**比状态*/
                    
                    if(!tradeSt.equals(localUnionRecord.getTradeSt())){
                        checkResult=false;
                        state=true;
                    }
                    /**比金额*/
                    if(!unionAmt.toString().equals(localUnionRecord.getTxnAmt().toString())){
                    	checkResult=false;
                    	amt=true;
                    }
                    
                    if(checkResult){
                        /** 对账成功 */                        
                        //设置状态为00成功和对账描述为成功并插入通道对账结果表
                        //更新通道对账表和第三方对账表状态为对账成功                        
                        localUnionRecord.setChkSt(Constans.CHK_STATE_01);
                        localUnionRecord.setChkRst(Constans.CHK_RST_00);
//                        localWechatRecord.setChkDataDt(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDD(), IfspDateTime.YYYYMMDD));
                        bthPagyLocalInfoDao.updateByPrimaryKeySelective(localUnionRecord);
                        unionRecord.setChkAcctSt(Constans.CHK_STATE_01);
                        unionRecord.setChkRst(Constans.CHK_RST_00);
//                        wechatRecord.setChkDataDt(IfspDateTime.getYYYYMMDD());
                        bthUnionFileDetDao.updateByPrimaryKeySelective(unionRecord);
                        //插入对账结果结果表
                        BthChkRsltInfo record=new BthChkRsltInfo();
                        ReflectionUtil.copyProperties(localUnionRecord,record);
                        record.setChkSuccDt(IfspDateTime.strToDate(chkSuccDt, IfspDateTime.YYYY_MM_DD));
                        record.setChkSt(Constans.CHK_STATE_00);
                        record.setChkRst("");
                        Map<String, Object> param = new HashMap<String, Object>();
                		param.put("pagyTxnSsn", localUnionRecord.getPagyTxnSsn());
                		PagyTxnInfo pagyTxnInfo = pagyTxnInfoDao.selectOne("selectPagyTxnInfoByPagyTxnSsn", param);
                		if(pagyTxnInfo==null){
                			throw new IfspBizException("9999", "根据通道内部订单号查询通道核心信息失败！");
                		}
                		String orderSsn =pagyTxnInfo.getTxnReqSsn();
                		if(pagyTxnInfo.getTxnReqSsn().length()!=20){
                			PayOrderInfo payOrderInfo = payOrderInfoDao.selectByPagyTxnSsn(localUnionRecord.getPagyTxnSsn());
        					if(payOrderInfo==null){
        						throw new IfspBizException("9999", "根据通道交易流水号<"+localUnionRecord.getPagyTxnSsn()+">查询订单信息失败！");
        					}
        					orderSsn = payOrderInfo.getOrderSsn();
                		}
                        record.setOrderSsn(orderSsn);
                        record.setStlmSt(Constans.STLM_ST_00);
                        record.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                        record.setTpamTxnFeeAmt(Long.parseLong(unionRecord.getCustomerFee()));
                        BigDecimal brandFee = BigDecimal.ZERO;
                        if (IfspDataVerifyUtil.isNotBlank(unionRecord.getBrandFee())){
                            brandFee = unionRecord.getBrandFee();
                        }
                        record.setUnionBrandFee(brandFee);
                        bthChkRsltInfoDao.insert(record);                        
                    }else{

                        // -------- 对账不平, 发现状态不一致进对账不平表 modify by ljy    modify_date : 20190425       start  -------
                        if (state){
                            initChkUnequalInfo(unionRecord, localUnionRecord, unionAmt, tradeSt, chkSuccDt);
                        }else {
                            // --------       modify end   --------------------------------------------------------------------------

                            //标识差错原因插入通道对账差错表，
                            //并更新通道流水表和第三方流水表对账状态为对账失败
                            BthPagyChkErrInfo bthBalErrRecord=new BthPagyChkErrInfo(localUnionRecord);
                            if(type==true&&state==false&&amt==false){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_100);
                                bthBalErrRecord.setErrDesc("交易类型不一致");
                            }
                            if(type==false&&state==false&&amt==true){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_001);
                                bthBalErrRecord.setErrDesc("金额不一致");
                            }
                            if(type==true&&state==false&&amt==true){
                                bthBalErrRecord.setErrTp(Constans.ERR_TP_101);
                                bthBalErrRecord.setErrDesc("交易类型金额均不一致");
                            }
                            bthBalErrRecord.setProcSt(Constans.PROC_ST_01);
                            bthBalErrRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
                            bthBalErrRecord.setChkErrDt(format.parse(settleDate));
                            bthBalErrRecord.setChkErrSsn(ConstantUtil.getRandomNum(32));
                            bthPagyChkErrInfoDao.insert(bthBalErrRecord);
                        }
                        //设置本地对账流水表对账状态为已对账
                        localUnionRecord.setChkSt(Constans.CHK_STATE_01);
                        localUnionRecord.setChkRst(Constans.CHK_RST_01);
                        bthPagyLocalInfoDao.updateByPrimaryKeySelective(localUnionRecord);
                        unionRecord.setChkAcctSt(Constans.CHK_STATE_01);
                        unionRecord.setChkRst(Constans.CHK_RST_01);
                        bthUnionFileDetDao.updateByPrimaryKeySelective(unionRecord);
                    }
                    break;                    
                }                                                             
            } 
            //没有匹配到,设为可疑
            if(exist_flag == false){
                //银联可疑
            	unionRecord.setChkAcctSt(Constans.CHK_STATE_01);
                unionRecord.setChkRst(Constans.CHK_RST_02);
                unionRecord.setDubiousFlag(Constans.DUBIOUS_FLAG_01);
                bthUnionFileDetDao.updateByPrimaryKeySelective(unionRecord);
            }                
        }
        
    
	}
	
	/**
	 * 剩余通道对账表未匹配流水
	 * @param settleDate
	 * @param pagySysNo
	 */
	private void systemVsUnion(String settleDate, String pagySysNo) {
		log.info("---------------------剩余通道对账表未匹配流水开始---------------------");
		List<BthPagyLocalInfo> bthLocalTraces=bthPagyLocalInfoDao.selectAliByDateAndStat(settleDate,Constans.CHK_STATE_00,pagySysNo);
		log.info("---------------------剩余通道对账表未匹配流水"+bthLocalTraces.size()+"条---------------------");
        for(BthPagyLocalInfo bthLocalTrace:bthLocalTraces){
            bthLocalTrace.setChkSt(Constans.CHK_STATE_01);
            bthLocalTrace.setChkRst(Constans.CHK_RST_02);
            bthLocalTrace.setDubiousFlag(Constans.DUBIOUS_FLAG_01);
            bthPagyLocalInfoDao.updateByPrimaryKeySelective(bthLocalTrace);
        }
	}
	


    /**
     * 初始化通道对账不平记录表
     * @param unionRecord
     * @param localUnionRecord
     * @param unionAmt
     * @param tradeSt
     * @param chkUeqDate
     * @throws ParseException
     */
    private void initChkUnequalInfo(BthUnionFileDet unionRecord, BthPagyLocalInfo localUnionRecord, BigDecimal unionAmt, String tradeSt, String chkUeqDate) throws ParseException {
        BthChkUnequalInfo unEqRecord = new BthChkUnequalInfo();
        unEqRecord.setPagyPayTxnSsn(localUnionRecord.getPagyPayTxnSsn());
        unEqRecord.setPagyPayTxnTm(localUnionRecord.getPagyPayTxnTm());
        unEqRecord.setPagySysNo(localUnionRecord.getPagySysNo());
        unEqRecord.setTxnAmt(localUnionRecord.getTxnAmt());
        unEqRecord.setTxnType(localUnionRecord.getTxnChlAction());
        unEqRecord.setTxnStat(localUnionRecord.getTradeSt());
        unEqRecord.setTpamTxnSsn(unionRecord.getProxyInsCode()+unionRecord.getSendInsCode()+unionRecord.getTraceNum()+unionRecord.getTransDate());
        // transDate 月日时分秒
        unEqRecord.setTpamTxnTm(unionRecord.getTransDate());
        unEqRecord.setTpamTxnAmt(Long.parseLong(unionAmt.toString()));
        // 对账文件入库的时候已经做了转换  TransCode 映射成了  TxnChlAction
        unEqRecord.setTpamTxnType(unionRecord.getTransCode());
        unEqRecord.setTpamTxnStat(tradeSt);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pagyTxnSsn", localUnionRecord.getPagyTxnSsn());
        PagyTxnInfo pagyTxnInfo = pagyTxnInfoDao.selectOne("selectPagyTxnInfoByPagyTxnSsn", param);
        if(IfspDataVerifyUtil.isBlank(pagyTxnInfo)||IfspDataVerifyUtil.isBlank(pagyTxnInfo.getTxnReqSsn())){
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "根据通道内部订单号查询通道核心信息失败！");
        }
        unEqRecord.setOrderSsn(pagyTxnInfo.getTxnReqSsn());

        unEqRecord.setProcSt(Constans.PROC_ST_01);
        unEqRecord.setProcDesc("");
        unEqRecord.setChkUeqDate(IfspDateTime.strToDate(chkUeqDate,IfspDateTime.YYYY_MM_DD));
        unEqRecord.setCrtTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));
        unEqRecord.setLstUpdTm(IfspDateTime.strToDate(IfspDateTime.getYYYYMMDDHHMMSS(), IfspDateTime.YYYY_MM_DD_HH_MM_SS));

        bthChkUnequalInfoDao.insert(unEqRecord);
    }
    /**
     * 获取对账日期
     * @param dateStr
     * @return
     */
    private Date getRecoDate(String dateStr){
        if(StringUtils.isBlank(dateStr)){
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期为空");
        }
        try{
            return IfspDateTime.getDateTime(dateStr, "yyyyMMdd").plusDays(1).toDate(); //todo 改成常数
        }catch (Exception e){
            log.error("对账日期格式错误: ", e);
            throw new IfspValidException(IfspValidException.getErrorCode(), "对账日期格式错误");
        }
    }



    /**
     * 工作线程
     *
     * @param <T>
     */
//    class Handler<T> implements Callable<T> {
//
//        //对账日期
//        private Date recoDate;
//        //最小行数
//        private DataInterval dataInterval;
//
//        private String pagyNo;
//
//        public Handler(Date recoDate, DataInterval dataInterval, String pagyNo) {
//            this.recoDate = recoDate;
//            this.dataInterval = dataInterval;
//            this.pagyNo = pagyNo;
//        }
//
//        @Override
//        public T call() throws Exception {
//            try {
//                log.info("====处理{}数据(start)====", dataInterval);
//                /*
//                 * 结果集合
//                 */
//                //对平
//                List<BthChkRsltInfo> identicalList = new ArrayList<>();
//                //不平: 包括本地单边 和 不平
//                List<BillRecoErr> errorList = new ArrayList<>();
//                /*
//                 * 对账
//                 */
//                //分页查询出本地流水
//                List<UnionBillLocal> localRecords = unionBillLocalDao.queryByRange(recoDate, dataInterval.getMin(), dataInterval.getMax(), pagyNo);
//                if (localRecords == null || localRecords.isEmpty()) {
//                    log.warn("{}数据为null", dataInterval);
//                } else {
//                    log.debug("{},数据容量:{}", dataInterval, localRecords.size());
//                    /*
//                     * 以本地流水为准, 匹配三方流水:
//                     *      1)匹配不上, 本地单边;
//                     *      2)匹配上了, 对比流水内容;
//                     */
//                    for (UnionBillLocal localRecord : localRecords) {
//                        //根据本地流水查询银联流水（区分二维码和全渠道，二维码用清算主键全渠道用流水号）
//                        UnionBillOuter outerRecord = null;
//                        if(IfspDataVerifyUtil.equals(Constans.ALL_CHNL_UNION_SYS_NO, pagyNo)){
//                            outerRecord = unionBillOuterDao.selectByPrimaryKeyDate(localRecord.getTxnSsn(), recoDate);
//                        }else{
//                            if(IfspDataVerifyUtil.isBlank(localRecord.getSettleKey())){
//                                /**
//                                 * 清算主键为空交易处于中间状态，应该使用通道流水号去三方账单查询
//                                 */
//                                outerRecord = unionBillOuterDao.selectByOrderId(localRecord.getTxnSsn(), recoDate);
//                            }else{
//                                outerRecord = unionBillOuterDao.selectByPrimaryKeyDate(localRecord.getSettleKey(), recoDate);
//                            }
//
//                        }
//                        /*
//                         * 本地有, 银联无时:
//                         *      1) 本地流水对账日期 == 当前对账日期, 本地流水进入可疑
//                         *      2) 本地流水对账日期 + 1 == 当前对账日期, 表示两个对账周期都没有找到匹配的流水, 本地流水进入单边;
//                         */
//                        if (outerRecord == null) {
//                            //判断是否可疑
//                            if(isDubious(recoDate, localRecord.getRecoDate())){
//                                //更新对账状态为: 可疑
//                                unionBillLocalDao.updateById(new UnionBillLocal(localRecord.getTxnSsn(), localRecord.getSettleKey(), RecoStatusDict.FINISH.getCode(), localRecord.getRecoDate(), recoDate, DubisFlagDict.TRUE.getCode()));
//                            }else {
//                                //加入不平列表
//                                errorList.add(getRecoErr(recoDate, localRecord, null, pagyNo, RecoResultDict.LOCAL_UA));
//                                //更新对账状态为: 已完成对账
//                                unionBillLocalDao.updateById(new UnionBillLocal(localRecord.getTxnSsn(), localRecord.getSettleKey(), RecoStatusDict.FINISH.getCode(), localRecord.getRecoDate(), recoDate));
//                            }
//                        } else {
//                            //比较流水内容
//                            RecoResultDict recoResult = compare(localRecord, outerRecord);
//                            //对平
//                            if (recoResult == RecoResultDict.IDENTICAL) {
//                                //加入对平列表
//                                identicalList.add(local2Idel(localRecord, outerRecord, recoDate));
//                            }
//                            //不平
//                            else {
//                                errorList.add(getRecoErr(recoDate, localRecord, outerRecord, pagyNo, recoResult));
//                            }
//                            //更新对账状态
//                            unionBillLocalDao.updateById(new UnionBillLocal(localRecord.getTxnSsn(), localRecord.getSettleKey(), RecoStatusDict.FINISH.getCode(), localRecord.getRecoDate(), recoDate));
//                            unionBillOuterDao.updateById(new UnionBillOuter(outerRecord.getTxnSsn(), RecoStatusDict.FINISH.getCode(), outerRecord.getRecoDate(), recoDate));
//                        }
//                    }
//                }
//                /*
//                 * 结果处理
//                 */
//                //记录平账流水
//                bthChkRsltInfoDao.insertBatch(identicalList);
//                //记录不平流水
//                billRecoErrDao.insertBatch(errorList);
//                log.debug("====处理{}数据(end)====", dataInterval);
//            } catch (Exception e) {
//                log.error("对账未知异常:", e);
//                log.debug("====处理{}数据(error end)====", dataInterval);
//                throw e;
//            }
//            return null;
//        }
//    }
//
//    /**
//     * 本地流水 转换为 平账流水
//     *
//     * @return
//     */
//    private BthChkRsltInfo local2Idel(UnionBillLocal localRecord, UnionBillOuter outer, Date recoDate) {
//        BthChkRsltInfo result = new BthChkRsltInfo();
//        result.setPagyPayTxnSsn(localRecord.getTxnSsn());
//        result.setPagyPayTxnTm(localRecord.getTxnTime());
//        result.setPagyTxnTm(localRecord.getOrderDate()); //没用, 但是数据库需要
//        result.setPagySysSoaNo("0");  //没用, 但是数据库需要
//        result.setPagySysSoaVersion("0");  //没用, 但是数据库需要
//        result.setChkSuccDt(recoDate);
//        result.setChkSt(RecoStatusDict.FINISH.getCode());
//        result.setChkRst(null);
//        result.setOrderSsn(localRecord.getOrderId());
//        result.setPagySysNo(localRecord.getPagyNo());
//
//        result.setTxnAmt(localRecord.getTxnAmt() == null ? null : localRecord.getTxnAmt().longValue());
//        result.setTpamTxnFeeAmt(outer.getCustomerFee() == null ? null : Long.parseLong(outer.getCustomerFee())); //渠道手续费
//        result.setStlmSt(Constans.STLM_ST_00); //未清算
//        return result;
//    }
//
//    /**
//     * 本地流水 转换为 不平账流水
//     *
//     * @return
//     */
//    private BillRecoErr getRecoErr(Date recoDate, UnionBillLocal localRecord, UnionBillOuter outerRecord, String chnlId, RecoResultDict resultDict) {
//        if (localRecord == null && outerRecord == null) {
//            throw new IllegalArgumentException("localRecord and outerRecord is null");
//        }
//        if (StringUtils.isBlank(chnlId)) {
//            throw new IllegalArgumentException("chnlId is blank");
//        }
//        if (resultDict == null) {
//            throw new IllegalArgumentException("resultDict is null");
//        }
//        BillRecoErr result = new BillRecoErr();
//        result.setChnlNo(chnlId);
//        result.setRecoDate(recoDate);
//        /*
//         * 记录本地流水信息
//         */
//        if (localRecord != null) {
//            result.setTxnSsn(localRecord.getTxnSsn());
//            result.setTxnTime(localRecord.getTxnTime());
//            result.setLocalTxnType(localRecord.getTxnType());
//            result.setLocalTxnState(localRecord.getTxnState());
//            result.setLocalTxnAmt(localRecord.getTxnAmt());
//            result.setOrderId(localRecord.getOrderId());
//            result.setOrderDate(localRecord.getOrderDate());
//        }
//        /*
//         * 记录三方流水信息
//         */
//        if (outerRecord != null) {
//            result.setTxnSsn(outerRecord.getTxnSsn());
//            result.setTxnTime(outerRecord.getTxnTime());
//            result.setOuterTxnType(outerRecord.getTxnType());
//            result.setOuterTxnState(outerRecord.getTxnState());
//            result.setOuterTxnAmt(outerRecord.getTransAmt());
//        }
//        result.setRecoRest(resultDict.getCode());
//        result.setProcState(ErroProcStateDict.INIT.getCode()); //状态为: 未处理
//        result.setProcDesc(resultDict.getDesc());
//        result.setUpdDate(new Date());
//
//        return result;
//    }
//
//    /**
//     * 比较流水内容
//     *
//     * @param localRecord
//     * @param outerRecord
//     * @return
//     */
//    private RecoResultDict compare(UnionBillLocal localRecord, UnionBillOuter outerRecord) {
//        //交易状态
//        if (!StringUtils.equals(localRecord.getTxnState(), outerRecord.getTxnState())) {
//            return RecoResultDict.STATE_UI;
//        }
//        //交易类型
//        if (!StringUtils.equals(localRecord.getTxnType(), outerRecord.getTxnType())) {
//            return RecoResultDict.TYPE_UI;
//        }
//        //交易金额
//        if (!equalsAmt(localRecord.getTxnAmt(), outerRecord.getTransAmt())) {
//            return RecoResultDict.AMT_UI;
//        }
//        return RecoResultDict.IDENTICAL;
//    }
//
//    /**
//     * 比较金额
//     *
//     * @param localAmt
//     * @param outerAmt
//     * @return
//     */
//    private boolean equalsAmt(BigDecimal localAmt, BigDecimal outerAmt) {
//        if (localAmt == null) {
//            return false;
//        }
//        if (outerAmt == null) {
//            return false;
//        }
//        return localAmt.compareTo(outerAmt) == 0;
//    }
//
//    private void initPool() {
//        destoryPool();
//        /*
//         * 构建
//         */
//        log.info("====初始化线程池(start)====");
//        executor = Executors.newFixedThreadPool(unionThreadCount, new ThreadFactory() {
//            AtomicInteger atomic = new AtomicInteger();
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "unionRecoHander_" + this.atomic.getAndIncrement());
//            }
//        });
//        log.info("====初始化线程池(end)====");
//    }
//
//
//    private void destoryPool() {
//        log.info("====销毁线程池(start)====");
//        /*
//         * 初始化线程池
//         */
//        if (executor != null) {
//            log.info("线程池为null, 无需清理");
//            /*
//             * 关闭线程池
//             */
//            try {
//                executor.shutdown();
//                if(!executor.awaitTermination(10, TimeUnit.SECONDS)){
//                    executor.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                System.out.println("awaitTermination interrupted: " + e);
//                executor.shutdownNow();
//            }
//        }
//        log.info("====销毁线程池(end)====");
//    }
//
//    /**
//     * 判断是否单边
//     * @param recoDate 当前对账日期
//     * @param recordRecoDate  流水对账日期
//     * @return
//     */
//    private boolean isDubious(Date recoDate, Date recordRecoDate){
//        //本地流水对账日期 == 当前对账日期
//        if (recordRecoDate.compareTo(recoDate) == 0) {
//            return true;
//        }else {
//            return false;
//        }
//        //本地流水对账日期 + 1 == 当前对账日期
//        else if ((new DateTime(recordRecoDate)).plusDays(1).toDate().compareTo(recoDate) == 0) {
//            return true;
//        }else {
//            return null;
//        }
//    }


	
}
