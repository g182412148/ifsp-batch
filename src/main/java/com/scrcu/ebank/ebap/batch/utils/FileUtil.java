package com.scrcu.ebank.ebap.batch.utils;

import com.scrcu.ebank.ebap.batch.bean.dto.MchtBaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class FileUtil {

    private static String jyType = "2";//商户来源（1-POS；2-扫码）--默认为扫码商户
    private static String userFilePath = "D:/home/shanghu/upload/YYYYMMDD/";//商户文件存放地址
//    private static String userFilePath = "/data/home/app/deploy/mchtchk/fileLoad/YYYYMMDD/";//商户文件存放地址

    
    private static String partten = "|#|";//商户文件字段分隔符
    private static String fileFormt = "UTF-8";//文件编码格式

    private static Logger log = LoggerFactory.getLogger(FileUtil.class);
    /**
     * 
     * @Description: 组装商户文件名
     * @param batchDay
     */
    public static String makeUpUserFileName(String batchDay){
        return "mcht_upload_"+jyType+"_"+batchDay.trim()+".txt";
    }
    /**
     *
     * @Description: 组装商户文件名
     * @param batchDay
     */
    public static String makeDownUserFileName(String batchDay){
        return "mcht_download_"+jyType+"_"+batchDay.trim()+".txt";
    }
    /**
     * 
     * @Description: 生成文件
     * @param megs 文件内容
     * @param locpath 文件路径
     * @param fileName 文件名
     */
    public static void createFile(List<String> megs,String locpath,String fileName,String charset){
        
        FileOutputStream fos = null;
        OutputStreamWriter os = null;
        BufferedWriter bw = null;
        try {
            File path = new File(locpath);
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(locpath + fileName);
            fos = new FileOutputStream(file);  //覆盖已有的文件内容
            os = new OutputStreamWriter(fos, charset);
            bw = new BufferedWriter(os);
            for (String data : megs) {
                bw.write(data);
                bw.newLine();
            }
            bw.flush();
            log.info("文件已生成！");
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (os != null)
                    os.close();
                if (fos != null)
                    fos.close();
            } catch (Exception e2) {
                log.error(e2.toString());;
            }
        }
        
    }
    
    /**
     * 
     * @Description: 对文件的值加引号
     * @param str
     * @param ready 备用字符串
     * @return

     */
    public static String addMark(String str,String ready){
        if(str==null || "".equals(str) || "null".equals(str))
            str = ready;
        return "\""+str+"\"";
    }
    
    /**
     * 
     * @Description: 对文件的值加引号
     * @param str
     * @param
     * @return

     */
    public static String addMark(String str){
        if(str!=null && !"".equals(str) && !"null".equals(str))
            return str;
        return "";
    }
    
    public static String addMark2(String str){
        if(str!=null && !"".equals(str) && !"null".equals(str))
            return str;
        return "";
    }
    
    /**
     * 
     * @Description: 生成文件批次号 
     * @param i
     * @param batchNo 
     * @return

     */
    public static String getFileBatchNo(int i,String batchNo){
        i = i +Integer.valueOf(batchNo);
        if(1<=i&&i<10)
            return "000"+i;
        if(10<=i && i<100)
            return "00"+i;
        if(100<=i&&i<1000)
            return "0"+i;
        
        return i+"";
    }
    
    /**
     * 
     * @Description: 根据当前批次号生成最新文件批次号 
     * @param batchNo 
     * @return

     */
    public static String getFileBatchNo(String batchNo){
        int i = Integer.valueOf(batchNo)+1;
        if(1<=i&&i<10)
            return "000"+i;
        if(10<=i && i<100)
            return "00"+i;
        if(100<=i&&i<1000)
            return "0"+i;
        return i+"";
    }
    
    /**
     * 
     * @Description: 组装商户文件记录编号
     * @param i
     * @return

     */
    public static String getNo(int i){
        if(1<=i&&i<10)
            return "00000"+i;
        if(10<=i && i<100)
            return "0000"+i;
        if(100<=i&&i<1000)
            return "000"+i;
        if(1000==i)
            return "00"+i;
        if(1000<i )
            return getNo(i%1000);
        return "000001";
    }
    
    /**
     * 
     * @Description:生成账务文件名 
     * @param reqTime yyyyMMddHHmmss
     * @return AGGREGATE_PAY_ACCOUNT_yyyyMMddHHmmss.txt

     */
    public static String makeUpAccountFileName(String reqTime){
        return "AGGREGATE_PAY_ACCOUNT_"+reqTime+".txt";
    }
    /**
     * 
     * @Description: 通过sftp上传文件至文件中转服务器
     * @param fileName
     * @param fileType 1-商户文件/ACK文件  2-账务文件
     * @param filePath
     * @time : 2017年12月25日 下午2:59:21
     */
//    public static void uploadingFile(String fileName,int fileType,String filePath){
//
//        log.info("正在上传文件至文件中转服务器……");
//        String yyyymmddTime = DateUtils.getCurrentDate();
//        String targetAddress = "";
//        filePath = filePath+fileName;
//        switch (fileType) {
//        case 1:
//            targetAddress = DateUtils.changeDateMark(FILE_USER_FILE_PATH, yyyymmddTime)+fileName;
//            break;
//        case 2:
//            targetAddress = DateUtils.changeDateMark(FILE_ACCOUNT_FILE_PATH, yyyymmddTime)+fileName;
//            break;
//        default:
//            break;
//        }
//        try {
//            SFTPUtil.uploading(FILE_SERVICE_IP, FILE_SERVICE_PORT, FILE_SERVICE_USER_NAME, FILE_SERVICE_PASSWORD, filePath, targetAddress);
//            log.info("上传文件："+fileName+",成功！");
//        } catch (JSchException e) {
//            log.info("连接"+FILE_SERVICE_IP+":"+FILE_SERVICE_PORT+"异常!");
//            throw new CashierException("连接"+FILE_SERVICE_IP+":"+FILE_SERVICE_PORT+"异常!");
//        } catch (SftpException e) {
//            log.info("上传文件："+fileName+",失败！");
//            throw new CashierException("上传文件："+fileName+",失败!");
//        } catch (Exception e) {
//            log.info("上传文件："+fileName+"时,关闭通讯渠道失败!");
//            throw new CashierException("上传文件："+fileName+"时,关闭通讯渠道失败!");
//        }
//    }
    
    /**
     * 
     * @Description: 通过sftp下载文件至本地
     * @param fileName 商户回盘文件名
     * @param fileDate 回盘日期
     * @return 

     */
//    public static boolean downloadingFile(String fileName ,String fileDate){
//        log.info("正在下载商户回盘文件至本地……");
//        String targetAddress = DateUtils.changeDateMark(FILE_RESPONSE_FILE_PATH, fileDate)+fileName;
//        String filePath = responseFilePath+fileDate+"/"+fileName;
//        boolean flag = false;
//        try {
//            SFTPUtil.download(FILE_SERVICE_IP, FILE_SERVICE_PORT, FILE_SERVICE_USER_NAME, FILE_SERVICE_PASSWORD, filePath, targetAddress);
//            flag = true;
//        } catch (FileNotFoundException e) {
//            log.info("下载文件："+fileName+",失败;本地存放路径错误……");
//            LogUtil.logToString(e);
//        } catch (JSchException e) {
//            log.info("连接"+FILE_SERVICE_IP+":"+FILE_SERVICE_PORT+"异常");
//            LogUtil.logToString(e);
//        } catch (SftpException e) {
//            log.info("下载文件："+fileName+",失败;文件不存在……");
//        } catch (Exception e) {
//            log.info("下载文件："+fileName+"时,关闭通讯渠道失败");
//            LogUtil.logToString(e);
//        }
//        return flag;
//    }
    
    /**
     * 
     * @Description: 当文件存在时删除文件
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File fs = new File(filePath);
        if(fs.exists())
            fs.delete();
    }
    
    /**
     * 
     * @Description: 组装本地商户回盘文件路径
     * @param batchDate
     */
//    public static String makeupResponseFilePath(String fileName,String batchDate){
//        return responseFilePath+File.separator+batchDate+File.separator+fileName;
//    }
    
    /**
     * 
     * @Description: 
     * @param userList 待上传posp商户信息
     * @param uploadDate 上传日期
     * @time : 2019年09月17日 下午17:13:11   miyajun
     */
    public static String makeUpUserFile(List<MchtBaseInfo> userList ,String localFilePath,String charset, String uploadDate){
        List<String> megs = new ArrayList<String>();
//        Map<String,String> map = new HashMap<String, String>();
//        Map<String,String> magsMap = new HashMap<String, String>();
        StringBuffer head = new StringBuffer();
        for(MchtBaseInfo bi :userList ){
            String storeId = bi.getMchtId();//商户编号
            String blNo = bi.getBlNo();//商户编号
            String ownerName = bi.getOwnerName();//法人/负责人姓名
            String ownerCertNo = bi.getOwnerCertNo();//商户法人证件号

            head.append(addMark(storeId));//商户编号
            head.append(partten);
            head.append(addMark(jyType));//商户来源（1-POS；2-扫码）
            head.append(partten);
            head.append(addMark(blNo));//商户营业执照号
            head.append(partten);
            head.append(addMark(ownerCertNo));//法人/负责人身份证号
            head.append(partten);
            head.append(addMark(ownerName));//法人/负责人姓名
            head.append("|#|");//每条数据结束分隔符
            head.append("\r\n");

//            magsMap.put(storeId, head.toString());
        }
        megs.add(head.toString());

        //将路径中的YYYYMMDD改成实际日期
        String userFilePath2 = DateUtils.changeDateMark(localFilePath,uploadDate.substring(0,8));
        String fileName ="";

    
        fileName = makeUpUserFileName(uploadDate);
        createFile(megs,userFilePath2 ,fileName,charset );

        
        return userFilePath2 + fileName;
    }
    
    /**
     * 
     * @Description: 通过校验卡bin
     * @param cardNum 
     */
    public static String  verifyPyeCbFlg(String cardNum){
        if(cardNum!=null &&!cardNum.trim().startsWith("62")){
            return "1";
        }
        return "0";
    }
    
    
    
    
    public static void main(String[] args) {
//        List<String> list = new ArrayList<String>();
//        list.add("dfafafda");
//        list.add("dafaagwwr");
//        createFile(list,"d:/home/","xxx.txt");
//        System.out.println(changeDateMark("D:/home/YYMMDD/", "20170811"));
//        System.out.println(231631/1000);
//        downloadingFile("mer_7300_20171227_09_0005.txt", "20171227");


        List<MchtBaseInfo> userList = new ArrayList<MchtBaseInfo>();
        MchtBaseInfo mbi = new MchtBaseInfo();
        mbi.setMchtId("001");
        mbi.setBlNo("0001");
        mbi.setOwnerCertNo("00001");
        mbi.setOwnerName("测试");
        userList.add(mbi);
        MchtBaseInfo mbio = new MchtBaseInfo();
        mbio.setMchtId("002");
        mbio.setBlNo("0002");
        mbio.setOwnerCertNo("00002");
        mbio.setOwnerName("测试");
        userList.add(mbio);
        MchtBaseInfo mbioo = new MchtBaseInfo();
        mbioo.setMchtId("003");
        mbioo.setBlNo("0003");
        mbioo.setOwnerCertNo("00003");
        mbioo.setOwnerName("测试");
        userList.add(mbioo);
        String  uploadDate = DateUtils.getCurrentDate17();
        String str = makeUpUserFile(userList,"","utf-8",uploadDate);
        System.out.println(str);
        log.info("返回文件保存路径：" + str);

    }
}
