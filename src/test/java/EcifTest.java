import com.ruim.ifsp.utils.client.http.IfspHttpClientUtil;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.OfflineCreatMerChkFileRequest;
import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述 </br>
 *
 * @author M.chen
 * 2019/6/17 9:57
 */
public class EcifTest {
    private String uat = "http://10.16.1.212:8090/crm/api/";

    @Test
    public void testEcifUpdateMchtInfo(){
        BatchRequest request = new BatchRequest();
        request.setSettleDate("20190606");

        //发送请求
        IfspHttpClientUtil ifspHttpClientUtil = new IfspHttpClientUtil();
        //ifspHttpClientUtil.setHttpMethod("POST");
        ifspHttpClientUtil.setSysName("ecifUpdateMchtInfo");
        ifspHttpClientUtil.setConnectionTimeout(10000);
        ifspHttpClientUtil.setReadTimeOut(20000);
        ifspHttpClientUtil.setContentType("http");
        ifspHttpClientUtil.setEncoding("utf-8");
        ifspHttpClientUtil.setUrl(uat+"ecifUpdateMchtInfo");
        //创建链接
        ifspHttpClientUtil.createConnection();
        //发送报文
        int code = ifspHttpClientUtil.send(IfspFastJsonUtil.tojson(request));
        System.out.println("code:" + code);
        String respData = ifspHttpClientUtil.getRespData();
        System.out.println("返回报文:" + respData);
    }

    @Test
    public void testEcifUpdateMchtInfoSoa() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190605");
        DubboServiceUtil.invokeDubboService(params, "ecifUpdateMchtInfo");
    }
}
