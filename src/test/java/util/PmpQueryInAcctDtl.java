package util;

import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.PagnParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PmpQueryInAcctDtl {
    private static String url = "http://localhost:8080/batch/SOA/";
    private static boolean base64Flag = false;

    public static void main(String[] args) {
        Map<String , Object> map = new HashMap<>();
        map.put("batchNo","20180820144859588289307913612646");
        PagnParam p = new PagnParam();
        p.setPageNo(1);
        p.setPageSize(1);
        map.put("pagnParams",p);
        String rs = HttpClientUtils.send(map, "004.QueryMerInAccDtl", url, base64Flag);
        System.out.println(rs);
        Map<?, ?> map1 = IfspFastJsonUtil.jsonTOmap(rs);
        List<Object> dtlList = (List<Object>) map1.get("dtlList");
        System.out.println(dtlList.size());
    }
}

