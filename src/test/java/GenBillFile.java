import com.opencsv.CSVWriter;
import com.ruim.ifsp.utils.constant.IfspConstants;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.scrcu.ebank.ebap.batch.bean.dto.AliBillLocal;
import com.scrcu.ebank.ebap.batch.bean.dto.UnionBillLocal;
import com.scrcu.ebank.ebap.batch.bean.dto.WxBillLocal;
import com.scrcu.ebank.ebap.batch.common.dict.RecoTxnTypeDict;
import com.scrcu.ebank.ebap.batch.common.utils.DateUtil;
import com.scrcu.ebank.ebap.batch.dao.AliBillLocalDao;
import com.scrcu.ebank.ebap.batch.dao.UnionBillLocalDao;
import com.scrcu.ebank.ebap.batch.dao.test.WxBillLocalDao;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 根据本地流水造三方文件
 */
@ContextConfiguration(locations = { "classpath:com/scrcu/ebank/ebap/config/spring-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class GenBillFile {


    @Resource
    private AliBillLocalDao aliBillLocalDao;

    @Resource
    private WxBillLocalDao wxBillLocalDao;

    @Resource
    private UnionBillLocalDao unionBillLocalDao;


    //构造支付宝账单
    @Test
    public void createAliBill() throws IOException {

        //交易日期,手动修改
        String txnDate = "20190828" ;
        /**
         * 构造文件内容
         */
        try(FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\20881117373712940156_"+txnDate+"_DETAILS.csv");
            OutputStreamWriter out = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
            CSVWriter csvWriter = new CSVWriter(out, CSVWriter.DEFAULT_SEPARATOR, '\0', '\0', CSVWriter.DEFAULT_LINE_END);){
            //写明细标题
            csvWriter.writeNext(new String[]{"#支付宝业务明细查询"});
            csvWriter.writeNext(new String[]{"#账号：[20881117373712940156]"});
            csvWriter.writeNext(new String[]{"#起始日期：[2019年05月09日 00:00:00]   终止日期：[2019年05月10日 00:00:00]"});
            csvWriter.writeNext(new String[]{"#-----------------------------------------业务明细列表----------------------------------------"});
            csvWriter.writeNext(getAliRecordHead());

            // 查询要生成的
            List<AliBillLocal> billLocals = getOuterList(IfspDateTime.plusTime(txnDate,IfspDateTime.YYYYMMDD,IfspTimeUnit.DAY,1));

            //写明细
            int i = 1;
            List<String[]> lines = new ArrayList<>();
            for (AliBillLocal billLocal : billLocals) {
                lines.add(getAliRecord(billLocal, i));
                i++;
            }

            csvWriter.writeAll(lines);
            //写汇总标题
            csvWriter.writeNext(new String[]{"#-----------------------------------------业务明细列表结束------------------------------------"});
            csvWriter.writeNext(new String[]{"#交易合计：23811笔，商家实收共7563180.81元，商家优惠共0.00元"});
            csvWriter.writeNext(new String[]{"#退款合计：3笔，商家实收退款共-15.01元，商家优惠退款共0.00元"});
            csvWriter.writeNext(new String[]{"#导出时间：[2019年05月10日 07:27:37]"});
            System.out.println("生成支付宝文件end");
        }

    }


    //构造微信账单
    @Test
    public void createWxBill() throws IOException {
        //交易日期,手动修改
        String fileDate = "2019-08-28" ;
        String txnDate = "20190828" ;

        try(FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\1457026702All"+fileDate+".csv");
            OutputStreamWriter out = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
            CSVWriter csvWriter = new CSVWriter(out, CSVWriter.DEFAULT_SEPARATOR, '\0', '\0', CSVWriter.DEFAULT_LINE_END);){
            /**
             * 构造文件内容
             */

            //写明细标题
            csvWriter.writeNext(getRecordHead());

            // 查询要生成的
            List<WxBillLocal> billLocals = getWxOuterList(IfspDateTime.plusTime(txnDate,IfspDateTime.YYYYMMDD,IfspTimeUnit.DAY,1));

            //写明细
            int i = 1;
            List<String[]> lines = new ArrayList<>();
            for (WxBillLocal billLocal : billLocals) {
                lines.add(getWxRecord(billLocal, i));
                i++;
            }

            csvWriter.writeAll(lines);
            //写汇总标题
            csvWriter.writeNext(getTotalHead());
            //写汇总信息
            csvWriter.writeNext(getTotal());
            System.out.println("生成微信文件end");
        }

    }

    private List<WxBillLocal> getWxOuterList(String recoDate) {
        Map<String, Object> map = new HashMap<>();
        map.put("recoDate",DateUtil.getDate(recoDate));
        return wxBillLocalDao.selectList("getWxInfo", map);
    }

    private String[] getRecordHead(){
        String[] head = new String[27];
        head[0] = "交易时间";
        head[1] = "公众账号ID";
        head[2] = "商户号";
        head[3] = "特约商户号";
        head[4] = "设备号";
        head[5] = "银联订单号";

        head[6] = "商户订单号";
        head[7] = "用户标识";
        head[8] = "交易类型";
        head[9] = "交易状态";
        head[10] = "付款银行";

        head[11] = "货币种类";
        head[12] = "应结订单金额";
        head[13] = "代金券金额";
        head[14] = "银联退款单号";
        head[15] = "商户退款单号";

        head[16] = "退款金额";
        head[17] = "充值券退款金额";
        head[18] = "退款类型";
        head[19] = "退款状态";
        head[20] = "商品名称";

        head[21] = "商户数据包";
        head[22] = "手续费";
        head[23] = "费率";
        head[24] = "订单金额";
        head[25] = "申请退款金额";
        head[26] = "费率备注";
        return head;
    }

    private String[] getWxRecord(WxBillLocal local , int i ){
        String[] record = new String[27];
        record[0] = new DateTime().toString("yyyy-MM-dd hh:mm:ss"); //交易时间
        record[1] = "`wx683a1b9bfd004839"; //公众账号ID
        record[2] = "`1457026702"; //商户号
        record[3] = "`merId_" + i; //特约商户号
        record[4] = "`WEB"; //设备号

        //支付还是退款
        if (RecoTxnTypeDict.PAY.getCode().equals(local.getTxnType())){
            record[6] = local.getTxnSsn();
            //交易状态
            record[9] = "`SUCCESS";
            //退款单号
            record[15] = "mrId" + i;
            //退款金额
            record[16] = "`0.00";
            //退款状态
            record[19] = "`";
            //订单金额
            record[24] = "`" + local.getTxnAmt().movePointLeft(2);
        }else{
            record[6] = "moId" + i;
            //交易状态
            record[9] = "`REFUND";
            //退款单号
            record[15] = "`" + local.getTxnSsn();
            //退款金额
            record[16] =  "`" +local.getTxnAmt().movePointLeft(2);
            //退款状态
            record[19] = "`SUCCESS";
            //订单金额
            record[24] = "`" + i;
        }

        record[5] = "ooId" + i; //银联订单号

        record[7] = "`userId_" + i;
        record[8] = "`JSAPI";

        record[10] = "`CFT";

        record[11] = "`CNY";
        record[12] = "`" + i;
        record[13] = "`" + i;
        record[14] = "orId" + i;

        record[17] = "`0.00";
        record[18] = "`";

        record[20] = "`微信(主扫)";

        record[21] = "`";
        record[22] = "`0.32";
        record[23] = "`0.20%"; //费率

        record[25] = "`0.00";
        record[26] = "`";
        return record;
    }

    private String[] getTotalHead(){
        String[] head = new String[27];
        head[0] = "总交易单数";
        head[1] = "应结订单总金额";
        head[2] = "退款总金额";
        head[3] = "充值券退款总金额";
        head[4] = "手续费总金额";
        head[5] = "订单总金额";
        head[6] = "申请退款总金额";
        return head;
    }

    private String[] getTotal(){
        String[] total = new String[27];
        total[0] = "`271";
        total[1] = "`102752.89";
        total[2] = "`0.00";
        total[3] = "`0.00";
        total[4] = "`205.56";
        total[5] = "`102752.89";
        total[6] = "`0.00";
        return total;
    }



    private List<AliBillLocal> getOuterList(String recoDate) {
        Map<String, Object> map = new HashMap<>();
        map.put("recoDate",DateUtil.getDate(recoDate));
        return aliBillLocalDao.selectList("getAliInfo", map);
    }


    public String[] getAliRecordHead(){
        String[] head = new String[29];
        head[0] = "银联交易号";
        head[1] = "商户订单号";
        head[2] = "业务类型";
        head[3] = "商品名称";
        head[4] = "创建时间";
        head[5] = "完成时间";
        head[6] = "门店编号";
        head[7] = "门店名称";
        head[8] = "操作员";
        head[9] = "终端号";
        head[10] = "对方账户";
        head[11] = "订单金额（元）";
        head[12] = "商家实收（元）";
        head[13] = "支付宝红包（元）";
        head[14] = "集分宝（元）";
        head[15] = "支付宝优惠（元）";
        head[16] = "商家优惠（元）";
        head[17] = "券核销金额（元）";
        head[18] = "券名称";
        head[19] = "商家红包消费金额（元）";
        head[20] = "卡消费金额（元）";
        head[21] = "退款批次号";
        head[22] = "服务费（元）";
        head[23] = "实收净额（元）";
        head[24] = "商户识别号";
        head[25] = "交易方式";
        head[26] = "备注";
        head[27] = "花呗分期手续费";
        head[28] = "花呗分期数";
        return head;
    }


    private String[] getAliRecord(AliBillLocal local , int i ){
        String[] record = new String[29];
        record[0] = "ali_order"+ i+"\t";

        if (RecoTxnTypeDict.PAY.getCode().equals(local.getTxnType())){
            record[1] = local.getTxnSsn()+"\t";
            record[2] = "交易";
            record[11] = local.getTxnAmt().movePointLeft(2).toString();
            record[21] = "\t";
        }else {
            record[1] = "pagy_ssn"+ i+"\t";
            record[2] = "退款";
            record[11] = "-"+local.getTxnAmt().movePointLeft(2).toString();
            record[21] = local.getTxnSsn()+"\t";
        }


        record[3] = "支付宝(主扫)";
        record[4] = new DateTime().toString("yyyy-MM-dd hh:mm:ss");
        record[5] = new DateTime().toString("yyyy-MM-dd hh:mm:ss");
        record[6] = "";
        record[7] = "";
        record[8] = "\t";
        record[9] = "";
        record[10] = "英(158****66)";

        record[12] = "0.00";
        record[13] = "0.00";
        record[14] = "0.00";
        record[15] = "0.00";
        record[16] = "0.00";
        record[17] = "0.00";
        record[18] = "";
        record[19] = "0.00";
        record[20] = "0.00";

        record[22] = "-"+local.getTxnAmt().multiply(new BigDecimal(0.002)).setScale(0, BigDecimal.ROUND_HALF_UP).movePointLeft(2);
        record[23] = "0.00";
        record[24] = "2088100191421638";
        record[25] = "无线支付";
        record[26] = "";
        record[27] = "0.00";
        record[28] = "0";
        return record;
    }



    //构造银联账单
    @Test
    public void createUnionBill() {
        //交易日期,手动修改
        String txnDate = "20190828" ;

        FileOutputStream f = null;
        OutputStreamWriter fileWriter = null ;

        try {
            File file = new File("C:\\Users\\Administrator\\Desktop\\IND"+txnDate.substring(2)+"01ACOMN");
            f = new FileOutputStream(file,false);
            fileWriter = new OutputStreamWriter(f, IfspConstants.GBK_ENCODING);

            // 查询要生成的
            List<UnionBillLocal> billLocals = getUnionOuterList(IfspDateTime.plusTime(txnDate,IfspDateTime.YYYYMMDD,IfspTimeUnit.DAY,1));
            for (UnionBillLocal billLocal : billLocals) {
                fileWriter.write(getUnionRecord(billLocal).toString());
                fileWriter.write("\r\n");
                fileWriter.flush();
            }


        }  catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null ){
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (f != null ){
                try {
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<UnionBillLocal> getUnionOuterList(String recoDate) {
        Map<String, Object> map = new HashMap<>();
        map.put("recoDate",DateUtil.getDate(recoDate));
        return unionBillLocalDao.selectList("getUnionInfo", map);

    }


    private StringBuffer getUnionRecord(UnionBillLocal local){
        StringBuffer sbf = new StringBuffer();
        String[] record = new String[39];
        String settleKey = local.getSettleKey();
        // [  a,  b)
        record[0] = String.format("%-12s", settleKey.substring(0,8));
        record[1] = String.format("%-12s", settleKey.substring(8,16));
        record[2] = String.format("%-7s", settleKey.substring(16,22));
        record[3] = String.format("%-11s", settleKey.substring(22,32));

        record[4] = String.format("%-20s", "6217853100006984603");
        // 订单金额
        record[5] = String.format("%-13s", String.format("%012d",local.getTxnAmt().longValue()));
        record[6] = String.format("%-13s", "000000000000");
        record[7] = String.format("%-13s", "00000000000");

        record[8] = String.format("%-5s", "0200");
        // 交易类型
        if (RecoTxnTypeDict.PAY.getCode().equals(local.getTxnType())){
            record[9] = String.format("%-7s", "000000");
        }else {
            record[9] = String.format("%-7s", "200000");
        }
        record[10] = String.format("%-5s", "6011");
        record[11] = String.format("%-9s", "20101715");
        // 三方商户号
        if ("607".equals(local.getPagyNo())){
            record[12] = String.format("%-16s", "952511041110001");
        }else {
            //全渠道商户号
            record[12] = String.format("%-16s", "839665100000001");
        }

        record[13] = String.format("%-13s", "000005306207");
        record[14] = String.format("%-3s", "00");
        record[15] = String.format("%-7s", "000000");
        record[16] = String.format("%-12s", "01030000");
        record[17] = String.format("%-7s", "000000");
        record[18] = String.format("%-3s", "00");
        // 扫码
        record[19] = String.format("%-4s", "042");
        record[20] = String.format("%-13s", "000000000000");
        record[21] = String.format("%-13s", "000000000000");
        record[22] = String.format("%-13s", "D00000000000");
        record[23] = String.format("%-2s", "1");
        record[24] = String.format("%-4s", "000");
        record[25] = String.format("%-2s", "6");
        record[26] = String.format("%-2s", "0");
        record[27] = String.format("%-11s", "0000000000");
        record[28] = String.format("%-12s", "01059999");
        record[29] = String.format("%-2s", "0");
        record[30] = String.format("%-3s", "03");
        record[31] = String.format("%-3s", "00");
        record[32] = String.format("%-13s", "00000000000");
        record[33] = String.format("%-15s", "03000111001");
        record[34] = String.format("%-20s", "");
        record[35] = String.format("%-3s", "");
        // 订单号
        record[36] = String.format("%-41s", settleKey);
        record[37] = String.format("%-4s", "0301");
        record[38] = String.format("%-132s", "");
        for (String s : record) {
            sbf.append(s);
        }
        return sbf;
    }



}

