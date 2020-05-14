package com.scrcu.ebank.ebap.batch.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.scrcu.ebank.ebap.batch.bean.request.GetOrderInfoRequest;
import com.scrcu.ebank.ebap.batch.common.utils.EncryptUtil;
import com.scrcu.ebank.ebap.batch.service.BillsDownloadService;
import org.apache.commons.collections4.Get;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.BillDownloadRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.batch.bean.response.BillDownloadResponse;
import com.scrcu.ebank.ebap.batch.service.BillDownloadService;
import com.scrcu.ebank.ebap.batch.service.CreditPayChkService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.dubbo.annotation.SOA;
import com.scrcu.ebank.ebap.log.LogLevel;
import com.scrcu.ebank.ebap.log.annotation.Explain;
import com.scrcu.ebank.ebap.valid.annotation.IfspValid;


/**
 * 名称：〈对账单下载及解析批量任务〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月21日 <br>
 * 作者：lijingbo <br>
 * 说明：用途: 控制层(controller)暴露对外服务,调用业务层(service)完成业务处理 <br>
 *      声明: 使用@org.springframework.stereotype.Controller声明该类为一个控制器 <br>
 */
@Controller
public class BillDownloadjobController {

    
    @Value("${unionBillDownIp}")
    private String unionBillDownIp;
    @Value("${unionBillDownport}")
    private String unionBillDownport;
    @Value("${unionUserName}")
    private String unionUserName;
    @Value("${unionPwd}")
    private String unionPwd;
    @Value("${unionRemoteUrl}")
    private String unionRemoteUrl;
    @Value("${unionRemoteFileName}")
    private String unionRemoteFileName;
    @Value("${unionLocalFileUrl}")
    private String unionLocalFileUrl;
    @Value("${unionLocalFileName}")
    private String unionLocalFileName;
    
    @Value("${unionFeeDownIp}")
    private String unionFeeDownIp;
    @Value("${unionFeeDownport}")
    private String unionFeeDownport;
    @Value("${unionFeeUserName}")
    private String unionFeeUserName;
    @Value("${unionFeePwd}")
    private String unionFeePassWord;
    @Value("${unionFeeRemoteUrl}")
    private String unionFeeRemoteUrl;
    @Value("${unionFeeRemoteFileName}")
    private String unionFeeRemoteFileName;
    @Value("${unionFeeLocalFileUrl}")
    private String unionFeeLocalFileUrl;
    @Value("${unionFeeLocalFileName}")
    private String unionFeeLocalFileName;
	
    @Resource
    private BillDownloadService billDownloadService;
    @Resource
    private CreditPayChkService creditPayChkService;

    /**
     * 本行文件下载
     */
    @Resource
    private BillsDownloadService iBankDownloadSerice;

    /**
     * 支付宝文件下载
     */
    @Resource
    private BillsDownloadService aliDownloadService;


    
    @SOA("001.WxBillDownload")
    @Explain(name = "微信通道对账单下载及解析", logLv = LogLevel.DEBUG)
    public BillDownloadResponse wxBillDownload(@IfspValid BillDownloadRequest request) throws Exception {
        return billDownloadService.wxBillDownload(request);
    }
    
    @SOA("002.AliBillDownload")
    @Explain(name = "支付宝通道对账单下载及解析", logLv = LogLevel.DEBUG)
    public CommonResponse aliBillDownload(@IfspValid GetOrderInfoRequest request)  {
        return aliDownloadService.billDownload(request);
    }
    
    @SOA("003.UnionBillDownload")
    @Explain(name = "银联通道对账单下载及解析", logLv = LogLevel.DEBUG)
    public BillDownloadResponse unionBillDownload(@IfspValid BillDownloadRequest request) throws Exception {
    	HashMap<Object, Object> hashMap = new HashMap<>();
    	hashMap.put("unionBillDownIp", unionBillDownIp);
    	hashMap.put("unionBillDownport", unionBillDownport);
    	hashMap.put("unionUserName", unionUserName);
    	hashMap.put("unionPwd", unionPwd);
    	hashMap.put("unionRemoteUrl", unionRemoteUrl);
    	hashMap.put("unionRemoteFileName", unionRemoteFileName);
    	hashMap.put("unionLocalFileUrl", unionLocalFileUrl);
    	hashMap.put("unionLocalFileName", unionLocalFileName);
        return billDownloadService.unionAllBillDownload(request, hashMap);
    }
    
    @SOA("004.DebitBillDownload")
    @Explain(name = "本行借记卡核心对账单下载及加载", logLv = LogLevel.DEBUG)
    public CommonResponse ibankBillDownload(@IfspValid GetOrderInfoRequest request) {
    	return iBankDownloadSerice.billDownload(request);
    }
    
//    @SOA("005.CreditBillDownload")
//    @Explain(name = "本行贷记卡核心对账单下载及加载", logLv = LogLevel.DEBUG)
//    public BillDownloadResponse creditBillDownload(@IfspValid BillDownloadRequest request) throws Exception {
//    	return billDownloadService.creditBillDownload(request);
//    }
//
//    @SOA("006.DebitTallyDownload")
    @Explain(name = "核心记账文件下载及加载", logLv = LogLevel.DEBUG)
    public BillDownloadResponse debitTallyDownload(@IfspValid BillDownloadRequest request) throws Exception {
    	return billDownloadService.debitTallyDownload(request);
    }

    @SOA("006.DebitTallyDownload")
    @Explain(name = "核心记账文件下载及加载", logLv = LogLevel.DEBUG)
    public BillDownloadResponse debitTallyDownloadNew(@IfspValid BillDownloadRequest request) throws Exception {
        return billDownloadService.coreBillFileDownload(request);
    }


//    @SOA("699.UnionBrandFeeDownload")
    @Explain(name = "银联品牌服务费文件下载及加载", logLv = LogLevel.DEBUG)
    public BillDownloadResponse brandFeeBillDownload(@IfspValid MerRegRequest request) throws Exception {
    	HashMap<Object, Object> hashMap = new HashMap<>();
    	hashMap.put("unionFeeDownIp", unionFeeDownIp);
    	hashMap.put("unionFeeDownport", unionFeeDownport);
    	hashMap.put("unionFeeUserName", unionFeeUserName);
    	hashMap.put("unionFeePassWord", unionFeePassWord);
    	hashMap.put("unionFeeRemoteUrl", unionFeeRemoteUrl);
    	hashMap.put("unionFeeRemoteFileName", unionFeeRemoteFileName);
    	hashMap.put("unionFeeLocalFileUrl", unionFeeLocalFileUrl);
    	hashMap.put("unionFeeLocalFileName", unionFeeLocalFileName);
    	return billDownloadService.brandFeeBillDownload(request, hashMap);
    }

    @SOA("699.UnionBrandFeeDownload")
    @Explain(name = "银联品牌服务费文件下载及加载", logLv = LogLevel.DEBUG)
    public BillDownloadResponse brandFeeBillDownloadNew(@IfspValid MerRegRequest request) throws Exception {
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("unionFeeDownIp", unionFeeDownIp);
        hashMap.put("unionFeeDownport", unionFeeDownport);
        hashMap.put("unionFeeUserName", unionFeeUserName);
        hashMap.put("unionFeePassWord", unionFeePassWord);
        hashMap.put("unionFeeRemoteUrl", unionFeeRemoteUrl);
        hashMap.put("unionFeeRemoteFileName", unionFeeRemoteFileName);
        hashMap.put("unionFeeLocalFileUrl", unionFeeLocalFileUrl);
        hashMap.put("unionFeeLocalFileName", unionFeeLocalFileName);
        return billDownloadService.brandFeeBillDownload(request, hashMap);
    }


//    @SOA("699.UpacpBillDownload")
    @Explain(name = "银联全渠道文件下载及加载", logLv = LogLevel.DEBUG)
    public BillDownloadResponse upacpBillDownload(@IfspValid BillDownloadRequest request) throws Exception {
        Map<Object, Object> hashMap = new HashMap<>();
        hashMap.put("unionBillDownIp", unionBillDownIp);
        hashMap.put("unionBillDownport", unionBillDownport);
        hashMap.put("unionUserName", unionUserName);
        hashMap.put("unionPwd", unionPwd);
        hashMap.put("unionRemoteUrl", unionRemoteUrl);
        hashMap.put("unionRemoteFileName", unionRemoteFileName);
        hashMap.put("unionLocalFileUrl", unionLocalFileUrl);
        hashMap.put("unionLocalFileName", unionLocalFileName);
        return billDownloadService.upacpBillDownload(request,hashMap);
    }
    
    
    @SOA("699.creditBillDownload")
    @Explain(name = "贷记卡支付对账单下载", logLv = LogLevel.DEBUG)
    public CommonResponse creditBillDownload(@IfspValid BatchRequest request) throws Exception {
        return creditPayChkService.creditPayChk(request);
    }
    
}
