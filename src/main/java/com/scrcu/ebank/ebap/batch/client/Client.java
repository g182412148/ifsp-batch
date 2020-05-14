package com.scrcu.ebank.ebap.batch.client;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import com.scrcu.ebank.ebap.dubbo.scan.SoaKey;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {
	
    public static final String wx = "605 ";//微信
    public static final String ali = "606 ";//支付宝
    public static final String union = "607";//银联
    public static final String ibank = "604 ";//本行
    public static final String unionChnl = "608"; // 银联全渠道

	public static void main(String[] args) {
		
		if (args.length < 1 || "".equals(args[0])) {
			log.error("PARAM ERROR,PARAM LENGTH IS 0");
			System.out.println("PARAM_IS_VALID");
			System.exit(1);
		}
		Map<String, Object> params = new HashMap<>();
		log.info("进入.............");
		log.info(">>>>>>>>>>>>>>>>>>参数" + Arrays.toString(args));
		log.info("当前执行:" + args[0]);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		String serviceName = args[0];//服务名
		
		if(args.length == 1){

            try {

                log.info(">>>>>>>>>>>开始执行:"+serviceName);
                Map resMap = DubboServiceUtil.invokeDubboService(params, new SoaKey(serviceName, null, null));
                log.info("resMap>>>>>>>>>>>" + resMap.toString());
                log.info("执行完成");
                if(!"0000".equals(resMap.get("respCode"))){
                    log.info("resMap>>>>>>>>>>>" + resMap.toString());
                    log.info("执行失败");
                    System.exit(1);
                }
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
		
		if (args.length > 1) { // 如果传入了日期参数
			try {
				log.info(serviceName+":开始执行>>>>>>>>>>>>>>>>>>>>>>>>");
				String today = args[1];//日期
				
				if("TODAY".equals(today)){
					today=sdf.format(new Date());
				}else if("YESTERDAY".equals(today)){
					Date date = new Date();
					long a=date.getTime()-24*60*60*1000;
					today=sdf.format(a);
				}
				log.info(">>>>>>>>>>>时间为today:"+today);

				//生成银联商户注册文件
                //获取银联商户注册反馈文件
                if("699.genMerRegFile".equals(serviceName)){
                    params.put("settleDate", today);
                    log.info(">>>>>>>>>>>开始执行:"+serviceName);
                }
                else if("699.getMerRegRtnFile".equals(serviceName)){
                    params.put("settleDate", today);
                    log.info(">>>>>>>>>>>开始执行:"+serviceName);
                }

				//依赖关系为1
                else if("002.GetWxAtTxnInfo".equals(serviceName)){
					params.put("settleDate", today);
					params.put("pagySysNo", wx);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("003.GetAliAtTxnInfo".equals(serviceName)){
					params.put("settleDate", today);
					params.put("pagySysNo", ali);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("004.GetUnionTxnInfo".equals(serviceName)){
					params.put("settleDate", today);
					params.put("pagySysNo", union);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("005.GetIbankTxnInfo".equals(serviceName)){
					params.put("settleDate", today);
					params.put("pagySysNo", ibank);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("006.GetTotalUnionTxn".equals(serviceName)){
                    params.put("settleDate", today);
                    params.put("pagySysNo", unionChnl);
                    log.info(">>>>>>>>>>>开始执行:"+serviceName);
                }

				
				//依赖关系为2
				else if("001.WxBillDownload".equals(serviceName)){
					params.put("pagySysNo", wx);
					params.put("settleDate", today);
					params.put("pagyNo", "605000000000001");//乱写以后可能会去掉
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("002.AliBillDownload".equals(serviceName)){
					params.put("pagySysNo", ali);
					params.put("settleDate", today);
					params.put("pagyNo", "606000000000001");//乱写以后可能会去掉
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("003.UnionBillDownload".equals(serviceName)){
					params.put("pagySysNo", union);
					params.put("settleDate", today);
					params.put("pagyNo", "607000000000001");//乱写以后可能会去掉
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("004.DebitBillDownload".equals(serviceName)){
					params.put("pagySysNo", ibank);
					params.put("settleDate", today);
					params.put("pagyNo", "604000000000001");//乱写以后可能会去掉
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("005.CreditBillDownload".equals(serviceName)){
					params.put("pagySysNo", ibank);
					params.put("settleDate", today);
					params.put("pagyNo", "123");//乱写以后可能会去掉
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("006.DebitTallyDownload".equals(serviceName)){
					params.put("pagySysNo", ibank);
					params.put("settleDate", today);
					params.put("pagyNo", "123");//乱写以后可能会去掉
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
                else if("699.UpacpBillDownload".equals(serviceName)){
                    params.put("pagySysNo", unionChnl);
                    params.put("settleDate", today);
                    params.put("pagyNo", "608000000000001");//乱写以后可能会去掉
                    log.info(">>>>>>>>>>>开始执行:"+serviceName);
                }


				//依赖关系为3
				else if("001.WxBillContrast".equals(serviceName)){
					params.put("pagySysNo", wx);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("002.AliBillContrast".equals(serviceName)){
					params.put("pagySysNo", ali);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("003.UnionBillContrast".equals(serviceName)){
					params.put("pagySysNo", union);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("004.DebitBillContrast".equals(serviceName)){
					params.put("pagySysNo", ibank);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("005.CreditBillContrast".equals(serviceName)){
					params.put("pagySysNo", ibank);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}

                else if("699.UnionAllChnlBillContrast".equals(serviceName)){
                    params.put("pagySysNo", unionChnl);
                    params.put("settleDate", today);
                    log.info(">>>>>>>>>>>开始执行:"+serviceName);
                }

                // 授信支付对账
                else if ("001.loanpayChk".equals(serviceName)){
                    params.put("settleDate", today);
                    log.info(">>>>>>>>>>>开始执行:"+serviceName);
                }
                // 纯积分支付对账
                else if ("001.pointPayChk".equals(serviceName)){
                    params.put("settleDate", today);
                    log.info(">>>>>>>>>>>开始执行:"+serviceName);
                }


                //依赖关系为4 对账结果与记账表对账
				else if("006.RsltKeepAccContrast".equals(serviceName)){
					params.put("pagySysNo", ibank);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				
				//依赖关系为5 记账表与核心对账
				else if("007.KeepAccCoreContrast".equals(serviceName)){
					params.put("pagySysNo", wx);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				
				//依赖关系为6 计算第三方通道手续费
				else if("001.WXCalculateServiceCharge".equals(serviceName)){
					params.put("pagySysNo", wx);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("002.ALICalculateServiceCharge".equals(serviceName)){
					params.put("pagySysNo", ali);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("003.YLCalculateServiceCharge".equals(serviceName)){
					params.put("pagySysNo", union);
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				
				//依赖关系6-1
				else if("T_RPS_accoutFailDataHandle6-1".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				
				//依赖关系为7 
				else if("001.preOrderClearing".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				
				//依赖关系为8 
				else if("001.coreOrderClearing".equals(serviceName)){
					params.put("pagySysNo", ibank);
					params.put("settleDate", today);
					params.put("pagyNo", "123");
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("001.wxOrderClearing".equals(serviceName)){
					params.put("pagySysNo", wx);
					params.put("settleDate", today);
					params.put("pagyNo", "123");
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("001.aliOrderClearing".equals(serviceName)){
					params.put("pagySysNo", ali);
					params.put("settleDate", today);
					params.put("pagyNo", "123");
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else if("001.unionpayOrderClearing".equals(serviceName)){
					params.put("pagySysNo", union);
					params.put("settleDate", today);
					params.put("pagyNo", "123");
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				
				//依赖关系为9 
				else if("001.CapitalSummarizeStep".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				
				//依赖关系为10 
				else if("002.GenerateStlFileGrp".equals(serviceName)){
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
                //授信支付放款文件
				else if("001.genSCLMChkFile".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
                //商户对账文件
				else if("001.genMerChkFile".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
                //商户结算文件
				else if("001.genMerStlFile".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
                //商户审核文件
				else if("001.genMerApprFile".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
                //计算结算日期
				else if("001.calcStlDate".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
                //同步结算状态
				else if("001.syncStlStatus".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
                //同步结算状态
				else if("699.creditBillDownload".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				//延长商户合同时间ExtensionContract
				else if("001.extensionContract".equals(serviceName)){
					params.put("settleDate", today);
					log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				else{
                    // 两个参数  默认是传时间
                    params.put("settleDate", today);
                    log.info(">>>>>>>>>>>开始执行:"+serviceName);
				}
				
				Map resMap = DubboServiceUtil.invokeDubboService(params, new SoaKey(serviceName, null, null));
				if(!"0000".equals(resMap.get("respCode"))){
					log.info("执行失败");
					log.info("resMap>>>>>>>>>>>" + resMap.toString());
					System.exit(1);
				}
                System.exit(0);
				log.info("resMap>>>>>>>>>>>" + resMap.toString());
				log.info("执行完成");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		} 

	}
}
