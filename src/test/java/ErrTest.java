import com.scrcu.ebank.ebap.batch.service.ErrInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @Description:
 * @CopyRightInformation : 数云
 * @Prject: 数云PMS
 * @author: sun_b
 * @date: 2020/5/11
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
//@ContextConfiguration(locations ={ "classpath*:/evnconfigs/dev/MchtChkTest-context.xml" } )
@Slf4j
@ContextConfiguration(locations ={ "classpath*:com/scrcu/ebank/ebap/config/spring-context.xml" } )
public class ErrTest {
    @Resource
    private ErrInfoService errInfoService;



    @Test
    public void insertBillRecoErrInfo(){
        errInfoService.getYestodayErrInfo();
//        errInfoService.test();
    }

    @Test
    public void writeErrFile(){
        //update BILL_RECO_ERR_INFO
        //set err_state = '00',err_acc_state = '02',CORE_ACC_SSN = 'HLP1234567',
        //CORE_ACC_TM = sysdate,CORE_ACC_AMT = PAY_AMT
        errInfoService.pushErrFile();
    }
}
