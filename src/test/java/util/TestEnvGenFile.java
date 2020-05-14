package util;

import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: ljy
 * @create: 2018-11-06 20:40
 */
public class TestEnvGenFile {


    @Test
    public void testEnvGenWxFile(){
        Map<String,Object> params = new HashMap<>();
        params.put("settleDate","20181106");
        DubboServiceUtil.invokeDubboService(params,"699.testEnvGenWxFile");
    }
    @Test
    public void testEnvGenAliFile(){
        Map<String,Object> params = new HashMap<>();
        params.put("settleDate","20181106");
        DubboServiceUtil.invokeDubboService(params,"699.testEnvGenAliFile");
    }
    @Test
    public void testEnvGenUnionQrcFile(){
        Map<String,Object> params = new HashMap<>();
        params.put("settleDate","20181106");
        DubboServiceUtil.invokeDubboService(params,"699.testEnvGenUnionQrcFile");
    }
    @Test
    public void testEnvGenUnionAllChnlFile(){
        Map<String,Object> params = new HashMap<>();
        params.put("settleDate","20181106");
        DubboServiceUtil.invokeDubboService(params,"699.testEnvGenUnionAllChnlFile");
    }
    @Test
    public void testGenOffLineMerChkFile(){
        Map<String,Object> params = new HashMap<>();
        params.put("settleDate","20190605");
        params.put("merNo","139765100270699");
        DubboServiceUtil.invokeDubboService(params,"001.genOffLineMerChkFile");
    }


}
