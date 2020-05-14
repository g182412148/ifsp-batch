package util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.service.GetAccBkFileService;
import com.scrcu.ebank.ebap.batch.service.impl.GetAccBkFileServiceImpl;

/**
 * 名称：〈〉<br>
 * 功能：〈功能详细描述〉<br>
 * 方法：〈方法简述 - 方法描述〉<br>
 * 版本：1.0 <br>
 * 日期：2018年06月11日 <br>
 * 作者：qzhang <br>
 * 说明：<br>
 */
public class SubMerStlTest {
//    private String url = "http://10.16.1.90:8090/ifsp-mcht/SOA/";
	 private String url = "http://localhost:8080/batch/SOA/";
    private boolean base64Flag = false;


    /**
     * 订单清分数据抽取功能测试
     */
    @Test
    public void transfer(){
    	BatchRequest request=new BatchRequest();
        List<String> list=new ArrayList<>();
        list.add("0001");
        
        request.setSettleDate("20180920");

        HttpClientUtils.send(request,"001.transfer4IndirectStl",url,base64Flag);
    }
    
    @Test
    public void transfer2(){
    	
    	GetAccBkFileServiceImpl getAccBkFileService = new GetAccBkFileServiceImpl();
    	
    	getAccBkFileService.getDataList(0, 1, null);
    }
    
    
    @Test
    public void indirectStl(){
    	BatchRequest request=new BatchRequest();
        List<String> list=new ArrayList<>();
        list.add("0001");
        
        request.setSettleDate("20180920");
        request.setPagyNo("604");               //604-本行 605-微信

        //001.coreOrderClearing
        HttpClientUtils.send(request,"001.indirectStl",url,base64Flag);   
    }
    
}
