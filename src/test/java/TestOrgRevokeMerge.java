import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.scrcu.ebank.ebap.batch.bean.request.OrgRepeMergRequest;
import com.scrcu.ebank.ebap.batch.common.dict.OpFlagDict;
import com.scrcu.ebank.ebap.dubbo.scan.SoaKey;
import org.junit.Test;
import util.TestDubboServiceUtil;

import java.util.Map;


/**
 * 机构撤并
 */
public class TestOrgRevokeMerge {

    /**
     * 执行机构撤并
     */
    @Test
    public void handle() {
        /*
         * 1. 报文
         */
        OrgRepeMergRequest req = new OrgRepeMergRequest();
        req.setOpFlag(OpFlagDict.REPE_MERG.getCode()); //操作类型: 执行撤并
        req.setMergDt("20191011"); //撤并日期
        req.setRepeOrg("3793"); //撤销机构
        req.setMergOrg("3792"); //合并机构___
        /*
         * 2. 调用
         */
        Map serRetMap = TestDubboServiceUtil.invokeDubboServiceBean(req, new SoaKey("009.orgRepeMerg", null, null));
        System.out.println("**************************************");
        System.out.println(IfspFastJsonUtil.tojson(serRetMap));
    }

    /**
     * 查询机构撤并结果
     */
    @Test
    public void query() {
        /*
         * 1. 报文
         */
        Map serRetMap = TestDubboServiceUtil.invokeDubboService(null, new SoaKey("014.mirAppHeartBeat", null, null));
        System.out.println("**************************************");
        System.out.println(IfspFastJsonUtil.tojson(serRetMap));
    }


}

