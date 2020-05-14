package util;

import com.alibaba.fastjson.JSON;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.BthMerInAccMchtsDtl;
import com.scrcu.ebank.ebap.batch.bean.dto.PagnParam;
import com.scrcu.ebank.ebap.batch.common.constant.MchtChnlRequestConstants;
import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import com.scrcu.ebank.ebap.dubbo.scan.SoaKey;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SumInAccTest {
    /**
     * 清分汇总
     */
    @Test
    public void sumClear(){
        Map<String , Object>  map = new HashMap<>();
        map.put("settleDate",IfspDateTime.getYYYYMMDD());
        DubboServiceUtil.invokeDubboService(map,"001.CapitalSummarizeStep");
    }

    /**
     * 生成入账文件
     */
    @Test
    public void generateFile(){
        Map<String , Object>  map = new HashMap<>();
        DubboServiceUtil.invokeDubboService(map,"002.GenerateStlFileGrp");
    }

    /**
     * 检查核心文件反馈文件
     */
    @Test
    public void coreAccBkFileChk(){
        Map<String , Object>  map = new HashMap<>();
        DubboServiceUtil.invokeDubboService(map,"005.CoreAccBkFileChk");
    }


    /**
     * 门户入账查询
     */
    @Test
    public void queryMchts(){
        Map<String , Object>  map = new HashMap<>();
        map.put("inAcctDate","20180825");
        map.put("merId","11825101080000556");
        PagnParam p = new PagnParam();
        p.setPageNo(1);
        p.setPageSize(2);
        map.put("pagnParams",JSON.toJSON(p));
        Map map1 = DubboServiceUtil.invokeDubboService(map, "006.QueryMerInAccPositive");

        BthInAcc bthss = IfspFastJsonUtil.mapTobean(map1, BthInAcc.class);

        List<BthMerInAccMchtsDtl> dtlList = bthss.getDtlList();

        String orderTm = dtlList.get(0).getOrderTm();
        System.out.println(orderTm);
    }

    /**
     * 他行
     */
    @Test
    public void otherBank(){
        Map<String , Object>  map = new HashMap<>();
        map.put("payPathCd","1002" );//支付汇路   1001-大额 1002-小额 1003-网银互联 1010-农信银 1110-四川支付 9001-行内支付 2001-智能汇路
        map.put("pltfBizTyp", "A100");//业务类型  A100：普通贷记
        map.put("pltfBizKind", "02102");//业务种类  02102：普通贷记
        map.put("debtCrdtInd", "1");//借贷标识  1-贷 2-借
        map.put("totlCnt", 1);//明细总笔数
        map.put("totlAmt", 0.04);//明细总金额
        map.put("Bat_Doc_Nm", "S05220180827RPS180827183307821.txt");//文件名 S(1位) + 机构号 + 交易日期(8位) + 渠道号 + 渠道流水号 + .txt   机构号9996  渠道号052  渠道流水号20位随机
        Map resMap = DubboServiceUtil.invokeDubboService(map, new SoaKey("6040980004", "1.0.0", "604"));
        if (!MchtChnlRequestConstants.CHL_APPLY_SUCCESS.equals((String) resMap.get("respCode"))) {
            System.out.println(resMap.get("respCode"));
            System.out.println(resMap.get("respMsg"));
        }

    }

}

