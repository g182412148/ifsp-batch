package com.scrcu.ebank.ebap.batch.service.impl;/**
 * Created by Administrator on 2019-04-28.
 */

import com.ruim.ifsp.utils.enums.IfspRespCodeEnum;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.ParternBaseInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.RespConstans;
import com.scrcu.ebank.ebap.batch.bean.request.GetFileNameRequest;
import com.scrcu.ebank.ebap.batch.bean.request.GetSerApprFileNameRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OfflineCreatMerChkFileRequest;
import com.scrcu.ebank.ebap.batch.bean.response.GetFileNameResponse;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.common.utils.FileUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.GenOfflineMerChkFileService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * 名称：〈名称〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2019-04-28 <br>
 * 作者：yangqi <br>
 * 说明：<br>
 */
@Service
@Slf4j
public class GenOfflineMerChkFileServiceImpl  implements GenOfflineMerChkFileService {
    private String batchDate;
    private String txnDate;
    private String merNo;

    @Resource
    private BthMerInAccDtlDao bthMerInAccDtlDao;

    @Resource
    private MchtBaseInfoDao mchtBaseInfoDao;           //商户基本信息

    @Resource
    private MchtContInfoDao mchtContInfoDao;           //商户合同信息
    @Resource
    private PaySubOrderInfoDao paySubOrderInfoDao;     //子订单表
    @Resource
    private MchtOrgRelDao mchtOrgRelDao;               //商户组织关联表
    @Resource
    private ParternBaseInfoDao parternBaseInfoDao;     //合作方信息
    @Value("${merChkFilePath}")
    private String merChkFilePath;

    @Value("${dmzFtpHost}")
    private String dmzFtpHost;

    @Value("${dmzFtpPort}")
    private String dmzFtpPort;

    @Value("${dmzFtpUserName}")
    private String dmzFtpUserName;

    @Value("${dmzFtpPwd}")
    private String dmzFtpPassword;

    @Value("${dmzFtpPath}")
    private String dmzFtpPath;

    @Value("${jsFilePath}")
    private String jsFilePath;

    @Override
    public CommonResponse genOffLineMerChkFile(OfflineCreatMerChkFileRequest request) {
        String batchDate = request.getSettleDate();
        if (IfspDataVerifyUtil.isBlank(batchDate)) {
            batchDate = DateUtil.format(new Date(), "yyyyMMdd");
            txnDate = DateUtil.format(DateUtil.getDiffStringDate(new Date(), -1), "yyyyMMdd");
        }


        this.setBatchDate(batchDate);
        this.setTxnDate(DateUtil.format(DateUtil.getDiffStringDate(DateUtil.parse(batchDate, "yyyyMMdd"), -1), "yyyyMMdd"));

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>genMerChkFile service of " + this.batchDate + " executing ");

        //1)生成平台商户对账文件
        List<String> platMerList = this.getPlatMerList(request.getMerNo());
        for (String platMerNo : platMerList) {
            List<BthMerInAccDtl> chkOrderList = this.getDataList(platMerNo, Constans.MER_LEVLE_00);
            if (chkOrderList.size() == 0) {
                log.info(">>>>>>>>>>>>>>>>>>>>plat mer : " + platMerNo + " has no data @" + this.getBatchDate());
                continue;
            }

            log.info(">>>>>>>>>>>>>>>>>>>>gen check file for plat mer : " + platMerNo);

            //对账文件存放路径
            String remoteFileName = "";
            String checkFile = this.getFileName(merChkFilePath, txnDate, platMerNo);
            remoteFileName = platMerNo + "-" + txnDate + "checkfile_all.txt";

            //统计首行信息
            BthMerInAccDtl merInAcc = this.getFirstLineData(platMerNo, Constans.MER_LEVLE_00);
            int totalRow = Integer.parseInt(merInAcc.getRowCount());
            String totalAmount = merInAcc.getTxnAmt();
            String firstLine = this.genFirstLine(totalRow, totalAmount);

            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>begin to generate check file:" + checkFile);
            this.writeFileFirstLine(checkFile, firstLine);

            //写文件
            FileUtil.write(checkFile, chkOrderList, true, null, null, true);
        }

        //2)生成商城商户对账文件
        log.info(">>>>>>>>>>>>>>>>>>>>gen check file for normal merchants>>>>>>>>>>>>>");
        List<String> merList = null;
        merList = this.getMerList(request.getMerNo());
        for (String merNo : merList) {
            List<BthMerInAccDtl> chkOrderList = this.getDataList(merNo, Constans.MER_LEVLE_01);

            if (chkOrderList.size() == 0) {
                log.info(">>>>>>>>>>>>>>>>>>>>normal mer : " + merNo + " has no data @" + this.getBatchDate());
                continue;
            }
            log.info(">>>>>>>>>>>>>>>>>>>>gen check file for normal mer : " + merNo);

            //对账文件存放路径
            String remoteFileName = "";
            String checkFile = this.getFileName(merChkFilePath, txnDate, merNo);
            remoteFileName = merNo + "-" + txnDate + "checkfile_all.txt";
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>begin to generate check file:" + checkFile);

            //统计首行信息
            BthMerInAccDtl merInAcc = this.getFirstLineData(merNo, Constans.MER_LEVLE_01);
            int totalRow = Integer.parseInt(merInAcc.getRowCount());
            String totalAmount = merInAcc.getTxnAmt();
            String firstLine = this.genFirstLine(totalRow, totalAmount);

            this.writeFileFirstLine(checkFile, firstLine);

            //写文件
            FileUtil.write(checkFile, chkOrderList, true, null, null, true);

            String fullName = checkFile;
            File f = new File(fullName);    //方便测试..
            String localDir = f.getParent();
            String shortName = f.getName();
        }
        //应答
        CommonResponse commonResponse = new CommonResponse();

        commonResponse.setRespCode(RespConstans.RESP_SUCCESS.getCode());
        commonResponse.setRespMsg(RespConstans.RESP_SUCCESS.getDesc());

        return commonResponse;

    }

    @Override
    public GetFileNameResponse qryFileName(GetFileNameRequest request) {
        request.valid();
        String txnDate = request.getSettleDate().trim();
        GetFileNameResponse response = new GetFileNameResponse();
        MchtBaseInfo mchtBaseInfo = mchtBaseInfoDao.selectByPrimaryKey(request.getMchtNo());
        if (mchtBaseInfo == null){
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "商户不存在，请核实商户号");
        }
        log.info(">>>>>>>>>>商户配置的clientId:[{}],查询传送的clientId:[{}]<<<<<<<<<<",mchtBaseInfo.getOpenClientId(),request.getClientId());
        if (!IfspDataVerifyUtil.equals(request.getClientId(), mchtBaseInfo.getOpenClientId())) {
            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "clientId不匹配，请到全网收单后台管理系统->商户管理->商户信息管理查看开放平台ID是否正确。");
        }

        String checkFile = this.getFileName(merChkFilePath, txnDate, request.getMchtNo());

        log.info(">>>>>>>>>>查询文件路径:[{}]<<<<<<<<<<",checkFile);

        File file = new File(checkFile);
        String filePath = this.getFileName("", txnDate, request.getMchtNo());
        if (file.exists()) {
            response.setDownloadType("share");// downloadType:0:share,1:ftp
            response.setFileName(filePath);// fileName
            response.setRespCode(IfspRespCodeEnum.RESP_SUCCESS.getCode());
            response.setRespMsg(IfspRespCodeEnum.RESP_SUCCESS.getDesc());
        }else{
            response.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
            response.setRespMsg("交易失败，对账文件不存在。");
        }
        return response;
    }


    //查询所有一级商户
    public List<String> getMerList(String merNo) {
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> merList = null;
        params.put("orgType", Constans.ORG_TYPE_PLAT);
        if (IfspDataVerifyUtil.isNotBlank(merNo)) {
            params.put("merNo", merNo);

        }
        merList = mchtOrgRelDao.selectMchtNoList("selectNonPlatMerNoListMerNo", params);

        return merList;
    }

    //查询所有平台商户
    public List<String> getPlatMerList(String merNo) {
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> platMerList = null;
        params.put("orgType", Constans.ORG_TYPE_PLAT);
        if(IfspDataVerifyUtil.isNotBlank(merNo)){
            params.put("merNo", merNo);

        }
        platMerList = mchtOrgRelDao.selectMchtNoList("selectPlatMerNoList1", params);
        return platMerList;
    }


    public int getTotalResult(String merNo, String merType) {
        int count = 0;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("stlResult", Constans.SETTLE_STATUS_SUCCESS_CLEARING);
        params.put("updateDate", batchDate);
        count = bthMerInAccDtlDao.count("countStlSuccOrderOfCurrDate", params);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>order-count-num  update @" + this.getBatchDate() + " is : " + count);
        return count;
    }

    /**
     * 根据商户号查询商户订单信息
     *
     * @param merNo   ： 商户号
     * @param merType ：商户类型
     * @return
     */
    public List<BthMerInAccDtl> getDataList(String merNo, String merType) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("createDate", this.getBatchDate());
        List<BthMerInAccDtl> inAccDtlList = null;
        if (Constans.MER_LEVLE_00.equals(merType)) {
            //查询平台商户订单数据
            params.put("platMerNo", merNo);
            inAccDtlList = bthMerInAccDtlDao.selectList("selectChkDataByPlatMerNoAll", params);
        } else {
            //查询一级商户订单数据
            params.put("merNo", merNo);
            inAccDtlList = bthMerInAccDtlDao.selectList("selectChkDataByMerNoAll", params);
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>inAccDtlList size of merchant :" + merNo + " @" + this.batchDate + " is " + inAccDtlList.size());

        for (BthMerInAccDtl inAccDtl : inAccDtlList) {
            fundChannelMap(inAccDtl);  //新老渠道映射
        }
        return inAccDtlList;
    }

    public BthMerInAccDtl getFirstLineData(String merNo, String merType) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("createDate", this.getBatchDate());
        BthMerInAccDtl inAccDtl = null;
        if (Constans.MER_LEVLE_00.equals(merType)) {
            //查询平台商户订单数据
            params.put("platMerNo", merNo);
            inAccDtl = bthMerInAccDtlDao.selectOne("selectChkFileCountDataByPlatMerNoAll", params);
        } else {
            //查询一级商户订单数据
            params.put("merNo", merNo);
            inAccDtl = bthMerInAccDtlDao.selectOne("selectChkFileCountDataByMerNoAll", params);
        }
        return inAccDtl;
    }

    public void updateObject() {
        //更新二级商户结算状态
        log.info(">>>>>>>>>>>>>>>>>>>syncStlStatus @" + this.getBatchDate() + "completed..");
    }

    /**
     * 将首行内容写入文件
     *
     * @param fileName ： 文件名
     * @param content  ：文件首行内容
     */
    private void writeFileFirstLine(String fileName, String content) {
        File file = new File(fileName);

        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileOutputStream fos = new FileOutputStream(fileName, false);
             OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
             BufferedWriter bw = new BufferedWriter(osw)){

            bw.write(content);
            bw.write(System.getProperty("line.separator"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拼接首行内容
     *
     * @param rowCount ： 总行数
     * @param totalAmt ：总金额
     * @return ： 首行内容
     */
    private String genFirstLine(int rowCount, String totalAmt) {
        String firstLineContent = "";
        StringBuffer rowCountStr = new StringBuffer("" + rowCount);
        while (rowCountStr.length() < 12) {
            rowCountStr.insert(0, "0");
        }

        StringBuffer iAmtStr = new StringBuffer("" + totalAmt);
        while (iAmtStr.length() < 12) {
            iAmtStr.insert(0, "0");
        }

        firstLineContent = rowCountStr.append("|").append(iAmtStr).append("|").toString();

        return firstLineContent;
    }

    /**
     * 新老账号映射
     *
     * @param inAccDtl
     */
    private void fundChannelMap(BthMerInAccDtl inAccDtl) {
        if (IfspDataVerifyUtil.equals(inAccDtl.getTxnType(), Constans.TXN_TYPE_ONLINE_PAY) || IfspDataVerifyUtil.equals(inAccDtl.getTxnType(), Constans.TXN_TYPE_ONLINE_REFUND)) {
            if (Constans.GAINS_CHANNEL_WX.equals(inAccDtl.getFundChannel())) {
                //微信支付
                inAccDtl.setFundChannel(Constans.OLD_CHANNEL_WX);
            } else if (Constans.GAINS_CHANNEL_ALI.equals(inAccDtl.getFundChannel())) {
                //支付宝
                inAccDtl.setFundChannel(Constans.OLD_CHANNEL_ALI);
            } else if (Constans.GAINS_CHANNEL_HZF.equals(inAccDtl.getFundChannel())) {
                //惠支付设置成本行
                inAccDtl.setFundChannel(Constans.OLD_CHANNEL_SXC);
            } else if (Constans.GAINS_CHANNEL_SXK.equals(inAccDtl.getFundChannel())) {
                //蜀信卡
                inAccDtl.setFundChannel(Constans.OLD_CHANNEL_SXC);
            } else if (Constans.GAINS_CHANNEL_LOAN.equals(inAccDtl.getFundChannel())) {
                //授信支付设置成蜀信卡
                inAccDtl.setFundChannel(Constans.OLD_CHANNEL_SXC);
            } else if (Constans.GAINS_CHANNEL_SXE.equals(inAccDtl.getFundChannel())) {
                //蜀信e
                inAccDtl.setFundChannel(Constans.OLD_CHANNEL_SXE);
            } else if (Constans.GAINS_CHANNEL_UNIONPAY.equals(inAccDtl.getFundChannel())) {
                //银联
                inAccDtl.setFundChannel(Constans.OLD_CHANNEL_UNIONPAY);
            } else if (Constans.GAINS_CHANNEL_POINT.equals(inAccDtl.getFundChannel())) {
                //积分
                inAccDtl.setFundChannel(Constans.OLD_CHANNEL_POINT);
            }
        } else {
            if (Constans.GAINS_CHANNEL_WX.equals(inAccDtl.getFundChannel())) {
                //线下微信
                inAccDtl.setFundChannel(Constans.GAINS_CHANNEL_OFFLIN_WX);
            }else if (Constans.GAINS_CHANNEL_ALI.equals(inAccDtl.getFundChannel())){
                //线下支付宝
                inAccDtl.setFundChannel(Constans.GAINS_CHANNE_OFFLINL_ALI);
            }else if (Constans.GAINS_CHANNEL_SXE.equals(inAccDtl.getFundChannel())){
                //线下蜀信e
                inAccDtl.setFundChannel(Constans.GAINS_CHANNEL_OFFLIN_SXE);
            }else if (Constans.GAINS_CHANNEL_UNIONPAY.equals(inAccDtl.getFundChannel())){
                //线下银联
                inAccDtl.setFundChannel(Constans.GAINS_CHANNEL_OFFLIN_UNIONPAY);
            }
        }
    }

    public String getFileName(String filePath, String settleDate, String merId){
        StringBuffer checkFile = new StringBuffer(filePath);
        checkFile.append(settleDate).append("/").append(merId).append("/");
        checkFile.append(merId).append("-").append(settleDate).append("checkfile_all.txt");
        return checkFile.toString();
    }
    public String getSerApprFileName(String filePath, String settleDate, String serviceId){
        StringBuffer checkFile = new StringBuffer(filePath);
        checkFile.append(settleDate).append("/").append(serviceId).append("/");
        checkFile.append(serviceId).append("_").append("SER_APPRROVERESULT_").append(settleDate).append(".txt");
        return checkFile.toString();
    }
    public String getBatchDate() {
        return batchDate;
    }

    public void setBatchDate(String batchDate) {
        this.batchDate = batchDate;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }


    @Override
    public GetFileNameResponse qrySerMerApprFileName(GetSerApprFileNameRequest request) {

        request.valid();
        String txnDate = request.getQueryDate().trim();
        GetFileNameResponse response = new GetFileNameResponse();
//        MchtBaseInfo mchtBaseInfo = mchtBaseInfoDao.selectByPrimaryKey(request.getClientId());
//        if (mchtBaseInfo == null){
//            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "商户不存在，请核实商户号");
//        }
//        log.info(">>>>>>>>>>商户配置的clientId:[{}],查询传送的clientId:[{}]<<<<<<<<<<",mchtBaseInfo.getOpenClientId(),request.getClientId());
//        if (!IfspDataVerifyUtil.equals(request.getClientId(), mchtBaseInfo.getOpenClientId())) {
//            throw new IfspValidException(IfspRespCodeEnum.RESP_ERROR.getCode(), "clientId不匹配，请到全网收单后台管理系统->商户管理->商户信息管理查看开放平台ID是否正确。");
//        }
        /* to do 添加服务商校验代码
                */
        if(!IfspDataVerifyUtil.equals("01",request.getReqChnl())){
            throw new IfspBizException(IfspRespCodeEnum.RESP_ERROR.getCode(), "渠道编号不正确");
        }
        ParternBaseInfo parternBaseInfo = parternBaseInfoDao.selectParternBaseInfo(request.getServiceId());
        if(parternBaseInfo==null  || !IfspDataVerifyUtil.equals("1",parternBaseInfo.getParternStatus())){
            response.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
            response.setRespMsg("服务商不存在或状态不正常[" + request.getServiceId() + "]");
            return response;
        }

        if(!IfspDataVerifyUtil.equals(parternBaseInfo.getOpenClientId(),request.getClientId())){
            response.setRespCode(IfspRespCodeEnum.RESP_ERROR.getCode());
            response.setRespMsg( "服务商openClientId与client不匹配");
            return response;
        }
        String checkFile = this.getSerApprFileName(merChkFilePath, txnDate, request.getServiceId());
        log.info(">>>>>>>>>>查询文件路径:[{}]<<<<<<<<<<",checkFile);
        File file = new File(checkFile);
        String filePath = this.getSerApprFileName("", txnDate, request.getServiceId());
        if (file.exists()) {
            response.setDownloadType("share");// downloadType:0:share,1:ftp
            response.setFileName(filePath);// fileName
            response.setRespCode(IfspRespCodeEnum.RESP_SUCCESS.getCode());
            response.setRespMsg(IfspRespCodeEnum.RESP_SUCCESS.getDesc());
        }else{
            response.setRespCode(IfspRespCodeEnum.RESP_0060.getCode());
            response.setRespMsg("交易失败，下载文件不存在。");
        }
        return response;
    }
}
