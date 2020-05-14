import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 测试对账
 */
public class TestBillReco {



    @Test
    public void preData() {
        Map<String, Object> params = new HashMap<String, Object>();
        DubboServiceUtil.invokeDubboService(params, "preData");
    }

    //数据抽取
    @Test
    public void dataExtract() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190220");
        params.put("pagySysNo", "abc");
//        DubboServiceUtil.invokeDubboService(params, "002.GetWxAtTxnInfo");
//        DubboServiceUtil.invokeDubboService(params, "003.GetAliAtTxnInfo");
//        DubboServiceUtil.invokeDubboService(params, "004.GetUnionTxnInfo");
//        DubboServiceUtil.invokeDubboService(params, "005.GetIbankTxnInfo");
//        DubboServiceUtil.invokeDubboService(params, "006.GetTotalUnionTxn");
//        DubboServiceUtil.invokeDubboService(params, "006.GetKeepAccTxn");


    }


    //账单解析
    @Test
    public void billDownload() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pagyNo", "605000000000001");
        params.put("pagySysNo", "abc");
        params.put("settleDate", "20190220");
        //sh /home/app/tws/opac_runbatch.sh 001.WxBillDownload 20190515
//        DubboServiceUtil.invokeDubboService(params, "001.WxBillDownload");
//        DubboServiceUtil.invokeDubboService(params, "002.AliBillDownload");
//        DubboServiceUtil.invokeDubboService(params, "003.UnionBillDownload");
        DubboServiceUtil.invokeDubboService(params, "004.DebitBillDownload");
    }

    //账单解析
    @Test
    public void createBill() throws IOException {
        /**
         * 构造文件内容
         */
        try(FileOutputStream fos = new FileOutputStream("D:\\test.csv");
            OutputStreamWriter out = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
            CSVWriter csvWriter = new CSVWriter(out, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);){
            //写明细标题
            csvWriter.writeNext(getRecordHead());
            //写明细
            List<String[]> lines = new ArrayList<String[]>();
            int count = 10;
            for(int i = 0; i<count; i++){
                lines.add(getRecord(i));
            }
            csvWriter.writeAll(lines);
            //写汇总标题
            csvWriter.writeNext(getTotalHead());
            //写汇总信息
            csvWriter.writeNext(getTotal());
            csvWriter.close();
        }

    }

    //微信对账
    @Test
    public void wxBillContrast() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190220");   //传交易日
        DubboServiceUtil.invokeDubboService(params, "001.WxBillContrast");
    }

    //支付宝对账
    @Test
    public void aliBillContrast() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190220");
        params.put("pagySysNo","abc");      //这个变量在批量中并没有真正用到
        DubboServiceUtil.invokeDubboService(params, "002.AliBillContrast");
    }

    //银联二维码通道对账
    @Test
    public void unionBillContrast() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190220");
        params.put("pagySysNo","abc");      //这个变量在批量中并没有真正用到
        DubboServiceUtil.invokeDubboService(params, "003.UnionBillContrast");
    }

    //本行借记卡核心对账
    @Test
    public void debitBillContrast() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190220");   //传交易日
        params.put("pagySysNo","604");
        DubboServiceUtil.invokeDubboService(params, "004.DebitBillContrast");
    }

    //记账表与核心对账
    @Test
    public void keepAccCoreContrast() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190220");   //传交易日
        params.put("pagySysNo","604");          //这个变量在批量中并没有真正用到
        DubboServiceUtil.invokeDubboService(params, "007.KeepAccCoreContrast");
    }

    //银联全渠道对账
    @Test
    public void unionAllChnlBillContrast() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190220");   //传交易日
        params.put("pagySysNo","608");
        DubboServiceUtil.invokeDubboService(params, "699.UnionAllChnlBillContrast");
    }

    //补记订单记账流水
    @Test
    public void rsltKeepAccContrast() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190220");   //传交易日
        params.put("pagySysNo","abc");
        DubboServiceUtil.invokeDubboService(params, "006.RsltKeepAccContrast");
    }

    /**
     * 清分数据抽取
     */
    @Test
    public void preOrderClearing() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190221");
        DubboServiceUtil.invokeDubboService(params, "001.preOrderClearing");
    }

    /**
     * 计算结算日期
     */
    @Test
    public void calcStlDate() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190220");
        DubboServiceUtil.invokeDubboService(params, "001.calcStlDate");
    }

    /**
     * 订单清分
     */
    @Test
    public void wxOrderClearing() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190221");
        DubboServiceUtil.invokeDubboService(params, "001.wxOrderClearing");
//        DubboServiceUtil.invokeDubboService(params, "001.coreOrderClearing");
//        DubboServiceUtil.invokeDubboService(params, "001.unionpayOrderClearing");
    }

    @Test
    public void aliOrderClearing() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190221");
        DubboServiceUtil.invokeDubboService(params, "001.aliOrderClearing");
    }

    @Test
    public void coreOrderClearing() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190221");
        DubboServiceUtil.invokeDubboService(params, "001.coreOrderClearing");
    }

    @Test
    public void unionpayOrderClearing() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20190221");
        DubboServiceUtil.invokeDubboService(params, "001.unionpayOrderClearing");
    }

    //清分汇总
    @Test
    public void capitalSummarizeStep()
    {
//        001.CapitalSummarizeStep
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20191115");
        DubboServiceUtil.invokeDubboService(params, "001.CapitalSummarizeStep");
    }

    //上送反欺诈
    @Test
    public void merInAccAntiFraud()
    {
//        001.CapitalSummarizeStep
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("settleDate", "20191112");
        DubboServiceUtil.invokeDubboService(params, "699.MerInAccAntiFraud");
    }

    //生成入账文件
    @Test
    public void generateStlFileGrp()
    {
//        001.CapitalSummarizeStep
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("settleDate", "20191112");
        DubboServiceUtil.invokeDubboService(params, "002.GenerateStlFileGrp");
    }

    //调核心接口入账
    @Test
    public void callCH1730()
    {
//        001.CapitalSummarizeStep
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("settleDate", "20191112");
        DubboServiceUtil.invokeDubboService(params, "699.callCH1730");
    }

    //更新入账状态
    @Test
    public void updateStat() {
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("settleDate", "20190517");
        DubboServiceUtil.invokeDubboService(params, "Test699.updateStat");
    }

    @Test
    public void genEptDataService() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("settleDate", "20191014");
        DubboServiceUtil.invokeDubboService(params, "001.genEptDataService");
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

    private String[] getRecord(int i){
        String[] record = new String[27];
        record[0] = new DateTime().toString("yyyy-MM-dd hh:mm:ss"); //交易时间
        record[1] = "`wx683a1b9bfd004839"; //公众账号ID
        record[2] = "`1457026702"; //商户号
        record[3] = "`merId_" + i; //特约商户号
        record[4] = "`WEB"; //设备号
        record[5] = "ooId" + i; //银联订单号

        record[6] = "moId" + i;
        record[7] = "`userId_" + i;
        record[8] = "`JSAPI";
        record[9] = "`SUCCESS";
        record[10] = "`CFT";

        record[11] = "`CNY";
        record[12] = "`" + i;
        record[13] = "`" + i;
        record[14] = "orId" + i;
        record[15] = "mrId" + i;

        record[16] = "`0.00";
        record[17] = "`0.00";
        record[18] = "`";
        record[19] = "`";
        record[20] = "`微信(主扫)";

        record[21] = "`";
        record[22] = "`0.32";
        record[23] = "`0.20%"; //费率
        record[24] = "`" + i; //订单金额
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


    //构造支付宝账单
    @Test
    public void createAliBill() throws IOException {
        /**
         * 构造文件内容
         */
        try(FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\20881117373712940156_20190101_DETAILS.csv");
            OutputStreamWriter out = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
            CSVWriter csvWriter = new CSVWriter(out, CSVWriter.DEFAULT_SEPARATOR, '\0', '\0', CSVWriter.DEFAULT_LINE_END);
            ){
            //写明细标题
            csvWriter.writeNext(new String[]{"#支付宝业务明细查询"});
            csvWriter.writeNext(new String[]{"#账号：[20881117373712940156]"});
            csvWriter.writeNext(new String[]{"#起始日期：[2019年05月09日 00:00:00]   终止日期：[2019年05月10日 00:00:00]"});
            csvWriter.writeNext(new String[]{"#-----------------------------------------业务明细列表----------------------------------------"});
            csvWriter.writeNext(getAliRecordHead());
//        //写明细
            List<String[]> lines = new ArrayList<String[]>();
            int count = 1000000;
            for(int i = 0; i<count; i++){
                lines.add(getAliRecord(i));
            }
            csvWriter.writeAll(lines);
            //写汇总标题
            csvWriter.writeNext(new String[]{"#-----------------------------------------业务明细列表结束------------------------------------"});
            csvWriter.writeNext(new String[]{"#交易合计：23811笔，商家实收共7563180.81元，商家优惠共0.00元"});
            csvWriter.writeNext(new String[]{"#退款合计：3笔，商家实收退款共-15.01元，商家优惠退款共0.00元"});
            csvWriter.writeNext(new String[]{"#导出时间：[2019年05月10日 07:27:37]"});
            csvWriter.close();
            System.out.println("生成文件end");
        }

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


    private String[] getAliRecord(int i){
        String[] record = new String[29];
        record[0] = "ali_order"+ i+"\t";
        record[1] = "pagy_ssn"+ i+"\t";
        record[2] = "交易";
        record[3] = "支付宝(主扫)";
        record[4] = new DateTime().toString("yyyy-MM-dd hh:mm:ss");
        record[5] = new DateTime().toString("yyyy-MM-dd hh:mm:ss");
        record[6] = "";
        record[7] = "";
        record[8] = "\t";
        record[9] = "";
        record[10] = "英(158****66)";
        record[11] = "295.00";
        record[12] = "295.00";
        record[13] = "0.00";
        record[14] = "0.00";
        record[15] = "0.00";
        record[16] = "0.00";
        record[17] = "0.00";
        record[18] = "";
        record[19] = "0.00";
        record[20] = "0.00";
        record[21] = "\t";
        record[22] = "-0.59";
        record[23] = "294.41";
        record[24] = "2088100191421638";
        record[25] = "无线支付";
        record[26] = "";
        record[27] = "0.00";
        record[28] = "0";
        return record;
    }

    /**
     * 生成核心反馈文件
     */
    @Test
    public void genCoreFile() throws IOException
    {
        String fileName="RPS052F20191115171738450";
        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("/Users/yangli/send/"+fileName)));
        PrintWriter pw=new PrintWriter(new FileOutputStream("/Users/yangli/recv/"+fileName+".dow"));
        String line=null;
        int rowNum=1;
        while((line=br.readLine())!=null)
        {
            line=line.replaceAll("##","#").concat("0000|#| ");
//            line=rowNum+"|#|"+line;
            pw.println(line);

            if(rowNum%10000==0)
            {
                pw.flush();
            }
            rowNum++;
        }
        File file=new File("/Users/yangli/recv/"+fileName+".dow.ok");
        file.createNewFile();
        pw.close();
        br.close();
    }

    @Test
    public void test()
    {
        String settleDate = "20191114";// 交易日期
        String recoDate = IfspDateTime.plusTime(settleDate, "yyyyMMdd", IfspTimeUnit.DAY, 1);
        Date date=IfspDateTime.parseDate(recoDate,"yyyyMMdd");
        System.out.println(date);
    }
}
