package util;

import com.scrcu.ebank.ebap.batch.bean.dto.PagnParam;

import java.util.HashMap;
import java.util.Map;

public class PmpQueryInAcct {
    private static String url = "http://localhost:8080/batch/SOA/";
    private static boolean base64Flag = false;

    public static void main(String[] args) {
        Map<String , Object> map = new HashMap<>();
        PagnParam p = new PagnParam();
        p.setPageNo(4);
        p.setPageSize(2);
        map.put("pagnParams",p);
//        map.put("stlmDate",20180818);
        map.put("merId","10021101070000");
        String rs = HttpClientUtils.send(map, "003.QueryMerInAcc", url, base64Flag);
        System.out.println(rs);
    }
}
