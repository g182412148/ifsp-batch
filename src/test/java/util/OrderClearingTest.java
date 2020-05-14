package util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.request.ClearRequest;

/**
 * 名称：〈〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月11日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public class OrderClearingTest {
//    private String url = "http://10.16.1.90:8090/ifsp-mcht/SOA/";
	 private String url = "http://localhost:8080/batch/SOA/";
    private boolean base64Flag = false;

    /*@Test
    public void count(){
        CommRegiRequest request=new CommRegiRequest();
        request.setReqSsn(IfspId.getUUID32());
        request.setReqTm(IfspDateTime.getYYYYMMDDHHMMSS());
        request.setReqChnl("00");
        request.setUserNo("0001");
        HttpClientUtils.send(request,"001.MchtCountCreate",url,base64Flag);
    }*/

    /**
     * 订单清分数据抽取功能测试
     */
    @Test
    public void preData(){
    	BatchRequest request=new BatchRequest();
        List<String> list=new ArrayList<>();
        list.add("0001");
        
        request.setSettleDate("20180926");
        request.setPagyNo("604");

        HttpClientUtils.send(request,"001.preOrderClearing",url,base64Flag);
    }
    
    
    @Test
    public void coreClearing(){
    	BatchRequest request=new BatchRequest();
        List<String> list=new ArrayList<>();
        list.add("0001");
        
        request.setSettleDate("20180926");
        request.setPagyNo("604");               //604-本行 605-微信

        //001.coreOrderClearing
        HttpClientUtils.send(request,"001.coreOrderClearing",url,base64Flag);   
    }
    
    @Test
    public void wxClearing(){
    	BatchRequest request=new BatchRequest();
        List<String> list=new ArrayList<>();
        list.add("0001");
        
        request.setSettleDate("20180822");
        request.setPagyNo("605");               //604-本行 605-微信

        //001.wxOrderClearing
        //001.aliOrderClearing
         //001.unionpayOrderClearing
        HttpClientUtils.send(request,"001.wxOrderClearing",url,base64Flag);   
    }
    
    
    @Test
    public void aliClearing(){
    	BatchRequest request=new BatchRequest();
        List<String> list=new ArrayList<>();
        list.add("0001");
        
        request.setSettleDate("20180816");
        request.setPagyNo("606");               //604-本行 605-微信

        //001.coreOrderClearing
        //001.wxOrderClearing
        //001.aliOrderClearing
         //001.unionpayOrderClearing
        HttpClientUtils.send(request,"001.aliOrderClearing",url,base64Flag);   
    }
    
    @Test
    public void unionpayClearing(){
    	BatchRequest request=new BatchRequest();
        List<String> list=new ArrayList<>();
        list.add("0001");
        
        request.setSettleDate("20180816");
        request.setPagyNo("607");               //604-本行 605-微信

        //001.coreOrderClearing
        //001.wxOrderClearing
        //001.aliOrderClearing
         //001.unionpayOrderClearing
        HttpClientUtils.send(request,"001.unionpayOrderClearing",url,base64Flag);   
    }
    
    
    @Test
    public void accountFailDataClearing(){
        BatchRequest request=new BatchRequest();
        List<String> list=new ArrayList<>();
        list.add("0001");
        
        request.setSettleDate("20180816");

        HttpClientUtils.send(request,"001.accoutFailDataHandle",url,base64Flag);   
    }

    
}
