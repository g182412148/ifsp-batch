package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.datetime.IfspTimeUnit;
import com.ruim.ifsp.utils.id.IfspId;
import com.ruim.ifsp.utils.message.IfspFastJsonUtil;
import com.ruim.ifsp.utils.verify.IfspDataVerifyUtil;
import com.scrcu.ebank.ebap.batch.bean.dto.*;
import com.scrcu.ebank.ebap.batch.bean.request.MchtBlackGreyListRequest;
import com.scrcu.ebank.ebap.batch.bean.request.MerRegRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.DataInterval;
import com.scrcu.ebank.ebap.batch.common.constant.Constans;
import com.scrcu.ebank.ebap.batch.common.dict.BlackGreyReasonDict;
import com.scrcu.ebank.ebap.batch.common.dict.MchtListTypeDict;
import com.scrcu.ebank.ebap.batch.common.utils.DubboServiceUtil;
import com.scrcu.ebank.ebap.batch.dao.*;
import com.scrcu.ebank.ebap.batch.service.MchtBlackGreyService;
import com.scrcu.ebank.ebap.batch.service.MchtBlackGreyInfoDao;
import com.scrcu.ebank.ebap.batch.utils.DateUtils;
import com.scrcu.ebank.ebap.batch.utils.FileLoadUtils;
import com.scrcu.ebank.ebap.batch.utils.FileUtil;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.config.SystemConfig;
import com.scrcu.ebank.ebap.dubbo.scan.SoaKey;
import com.scrcu.ebank.ebap.exception.IfspSystemException;
import com.scrcu.ebank.ebap.exception.IfspValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>名称 :  </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : zhangb </p>
 * <p>日期 : 2019-08-20  16:25 </p>
 */
@Slf4j
@Service
public class MchtBlackGreyServiceImpl  implements MchtBlackGreyService {
    @Resource
    BthMchtListTempInfoDao bthMchtListTempInfoDao;
    @Resource
    MchtBaseInfoDao mchtBaseInfoDao;
    @Resource
    MchtContInfoDao mchtContInfoDao;
    @Resource
    PayOrderInfoDao payOrderInfoDao;
    @Resource
    private MchtBlackGreyInfoDao mchtBlackGreyInfoDao;
    @Resource
    private IfsParamDao ifsParamDao;


    /**
     * 处理线程数量
     */
    @Value("${mchtBlackGreyChk.threadCount}")
    private Integer mchtBlackGreyChkThreadCount;
    /**
     * 每个线程处理数据量
     */
    @Value("${mchtBlackGreyChk.threadCapacity}")
    private Integer mchtBlackGreyChkThreadCapacity;

    @Value("${UPLOAD_FILE_URL}")
    private String UPLOAD_FILE_URL;

    @Value("${DOWNLOAD_FILE_URL}")
    private String DOWNLOAD_FILE_URL;

    @Value("${USER_FILE_PATH}")
    private String USER_FILE_PATH;
    /***
     * 银联黑名单查询同步全量商户的日期
     */
    private final static String QUERY_ALL_MCHT_DATE= "0122";

    private final static String CHARSET="UTF-8";

    /**
     * 线程池
     */
    ExecutorService executor;

    @Override
    public CommonResponse localChk(MchtBlackGreyListRequest request) throws IOException, ParseException {

        //获取处理日期
        Date chkDate = getRecoDate(request.getSettleDate());

        log.info("商户黑白名单处理开始，处理日期[{}]", chkDate);

        //数据清理
        log.info("银联全渠道对账数据清理-begin");
        clear(chkDate);

        log.info("银联全渠道对账数据清理-end");
        try {
            chkHandle(chkDate);
        } finally {
            destoryPool();
        }
        log.info("商户黑白名单处理结束，处理日期[{}]", chkDate);
        return new CommonResponse();
    }

    /**
     * 数据清理
     *
     * @param chkDate
     */
    private void clear(Date chkDate) {
        //查询待处理表中数据当前日期的数据记录数，若为0，表示未开始处理，先清理表数据，若不为0表式已开始处理，继续处理
        int count = bthMchtListTempInfoDao.countByChkDate(IfspDateTime.getYYYYMMDD(chkDate));
        log.info("查询待处理表中目前日期待处理记录数[{}]", count);
        if (count == 0) {
            //清空处理表
            int clearCount = bthMchtListTempInfoDao.truncateTable();
            log.info("清理待处理表中记录数[{}]", clearCount);
            //抽取商户信息到待处理表中
            int allCount = bthMchtListTempInfoDao.pullMchtInfo(IfspDateTime.getYYYYMMDD(chkDate));
            log.info("抽取商户信息到待处理表记录数[{}]", allCount);
        }
        //初始化线程池
        initPool();
    }

    /**
     * 处理
     *
     * @param chkDate
     */
    private void chkHandle(Date chkDate) {
        /*
         * 查询本地流水表的数据总数
         */
        int count = bthMchtListTempInfoDao.countByChkDate(null);
        log.info("本次需处理数据[{}]条", count);
        //分组数量
        int groupCount = (int) Math.ceil((double) count / mchtBlackGreyChkThreadCapacity);
        log.info("总分组数量[{}]页", groupCount);

        List<Future> futureList = new ArrayList<>();
        for (int groupIndex = 1; groupIndex <= groupCount; groupIndex++) {
            int minIndex = (groupIndex - 1) * mchtBlackGreyChkThreadCapacity + 1;
            int maxIndex = groupIndex * mchtBlackGreyChkThreadCapacity;
            log.info("处理第[{}]组数据", groupIndex);
            Future future = executor.submit(new Handler( new DataInterval(minIndex, maxIndex)));
            futureList.add(future);
        }
        /*
         * 获取处理结果
         */
        log.info("获取处理结果。。。。。。");
        for (Future future : futureList) {
            try {
                future.get(10, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("对账线程处理异常: ", e);
                //取消其他任务
                executor.shutdownNow();
                log.warn("其他子任务已取消.");
                //返回结果
                throw new IfspSystemException(SystemConfig.getSysErrorCode(), "子线程处理异常");
            }
        }
    }

    /**
     *
     *
     * @param dateStr
     * @return
     */
    private Date getRecoDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            throw new IfspValidException(IfspValidException.getErrorCode(), "处理日期为空");
        }
        try {
            return IfspDateTime.getDateTime(dateStr, "yyyyMMdd").toDate(); //todo 改成常数
        } catch (Exception e) {
            log.error("处理日期格式错误: ", e);
            throw new IfspValidException(IfspValidException.getErrorCode(), "处理日期格式错误");
        }
    }

    @Override
    public CommonResponse getMerRegRtnFile(MerRegRequest request) throws IOException {
        return null;
    }

    /**
     * 文件上传并调用传送文件id接口
     * @param request
     * @return
     * @throws IOException
     * miyajun  20190918
     */
    @Override
    public CommonResponse uploadMchtFile(MchtBlackGreyListRequest request) throws IOException {
        CommonResponse cr = new CommonResponse();
        try{
            /**全量sql
             * select mcht_id||'|#|'||'2'||'|#|'||bl_no||'|#|'||owner_cert_no||'|#|'||owner_name||'|#|' from mcht_base_info where rege_date
             */
            //IfsParam ifsParam = ifsParamDao.selectByParamKey(QUERY_ALL_MCHT_DATE);
            Map<String, Object> params = new HashMap<String, Object>();

            //取执行日期的前一天
            String yesterday = IfspDateTime.plusTime(request.getSettleDate(), IfspDateTime.YYYYMMDD, IfspTimeUnit.DAY, -1);
            params.put("regDate",yesterday);
//            if(ifsParam!=null) {
//                String queryAllMchtDate = ifsParam.getParamValue();
//                //批量日期为参数表中配置日期时，
//                if(IfspDataVerifyUtil.equals(queryAllMchtDate,request.getSettleDate())){
//                    params.remove("regDate");
//                }
//            }
            List<MchtBaseInfo>  mchtBaseInfoList = mchtBaseInfoDao.selectAllInfo(params);
            if(mchtBaseInfoList.isEmpty()){ //无需要处理商户数据，直接返回
                return cr;
            }
            String  uploadDate = DateUtils.getCurrentDate17();
            String localPath = FileUtil.makeUpUserFile(mchtBaseInfoList,USER_FILE_PATH, CHARSET,uploadDate);
            String uploadFileUrl = UPLOAD_FILE_URL;
            FileUploadResult result = FileLoadUtils.fileUpload(uploadFileUrl,localPath);
            if ("0000".equals(result.getRespCode())) {
                String uploadFileId = result.getFileId();
                log.info("=============="+"文件上传成功！"+"==============");
                log.info("=============="+"上传文件路径为："+"uploadFileUrl");
                if (null != uploadFileId && !"".equals(uploadFileId)) {
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("id", uploadFileId);
                    log.info("=======================================开始调用接口，请求报文"+params.toString());
                    Map respMap = DubboServiceUtil.invokeDubboService(param, new SoaKey("putMchtFile", null, null));
                    log.info("=======================================调接口完成，返回报文"+respMap.toString());
                    if("0000".equals((String)respMap.get("respCode"))){
                        log.info("===========调用接口传送文件ID成功！===============");
                        cr.setRespCode((String)respMap.get("respCode"));
                        cr.setRespMsg((String)respMap.get("respMsg"));
                    }else{
                        log.info("===========调用接口传送文件ID失败！"+respMap.get("respMsg")+"===============");
                        cr.setRespCode((String)respMap.get("respCode"));
                        cr.setRespMsg((String)respMap.get("respMsg"));
                    }
                }else{
                    log.error("uploadFileId 值为空！");
                }
            } else{
                cr.setRespCode(result.getRespCode());
                cr.setRespMsg("上传失败！");
                log.info("=============="+"文件上传失败！"+"==============");
                log.info("==============RespCode = "+result.getRespCode()+"==============RespMsg =" + result.getRespMsg());
                log.info("=============="+"上传文件路径为："+"uploadFileUrl");
            }
        }catch (Exception e) {
            log.error("查询数据异常:", e);
        }
        return cr;
    }
    /**
     * 下载黑名单查询数据文件接口
     * @param request
     * @return
     * @throws IOException
     * miyajun  20190918
     */
    @Override
    public CommonResponse downloadMchtFile(MchtBlackGreyListRequest request) throws IOException {
        CommonResponse cr = new CommonResponse();
        //取执行日期的前一天
        String yesterday = IfspDateTime.plusTime(request.getSettleDate(), IfspDateTime.YYYYMMDD, IfspTimeUnit.DAY, -1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("date",yesterday);
        log.info("=======================================开始调用接口，请求报文"+params.toString());
        Map respMap = DubboServiceUtil.invokeDubboService(params, new SoaKey("getMchtResultFile", null, null));
        String filedId = "";
        log.info("==================调用文件下载接口返回信息[{}]",IfspFastJsonUtil.tojson(respMap));

        if ("0000".equals(respMap.get("respCode"))) {
            filedId = (String) respMap.get("id");
            log.info("==================调接口获取文件ID成功！");
        }else {

//            cr.setRespCode((String)respMap.get("respCode"));
//            cr.setRespMsg((String)respMap.get("respMsg"));
            log.info("==================调用文件下载接口返回不成功，不处理文件!!!!");
            return cr;
        }
        if (!"".equals(filedId) && null != filedId) {
            String downloadFileUrl = DOWNLOAD_FILE_URL;
            String userFilePath = USER_FILE_PATH;//商户文件存放地址
            String  uploadDate = DateUtils.getCurrentDate17();
            String userFilePath2 = DateUtils.changeDateMark(userFilePath, uploadDate.substring(0,8));
            //创建文件目录
            File path = new File(userFilePath2);
            if (!path.exists()) {
                path.mkdirs();
            }
            String fileName = fileName = FileUtil.makeDownUserFileName(uploadDate);
            FileLoadUtils.fileDownload(downloadFileUrl,filedId,userFilePath2+fileName);
            cr.setRespCode("0000");
            cr.setRespMsg("下载文件成功！");
            log.info("==================下载文件成功！开始读取文件数据...");
            //读取文件数据
            List<ReturnFileInfo> list = readFile(userFilePath2+File.separator+fileName,CHARSET);
            log.info("==================读取文件数据成功！开始更新黑灰名单数据...");
            //开始更新黑灰名单数据
            if (list != null && list.size() > 0) {
                for (ReturnFileInfo rfi : list) {
                    if((rfi.getBlNo_result()!=null && rfi.getBlNo_result().trim().indexOf("1")>-1)
                        ||(rfi.getName_result()!=null && rfi.getName_result().trim().indexOf("1")>-1)
                    ){
                        MchtBlackGreyInfo mb = mchtBlackGreyInfoDao.queryStateByMchtId(rfi.getMchtId());
                        if (mb != null) {
                            String mchtListType = mb.getMchtListType();
                            if (MchtListTypeDict.GREY.getCode().equals(mchtListType)) {  //原为灰名单
                                mb.setMchtListType(MchtListTypeDict.BLACK.getCode());
                                mb.setReason(BlackGreyReasonDict.REASON_7.getCode());
                                mb.setLastUpdTm(new Date());
                                mchtBlackGreyInfoDao.updateByPrimaryKey(mb);
                            }else{//原为黑名单
                                Set<String> set = getReasonSet(mb.getReason());
                                if(!set.contains(BlackGreyReasonDict.REASON_7.getCode())){ //如果已经存在银联黑名单原因，不再更新
                                    mb.setMchtListType(MchtListTypeDict.BLACK.getCode());
                                    set.add(BlackGreyReasonDict.REASON_7.getCode()); //添加黑名单原因
                                    mb.setReason(toReasonStr(set));
                                    mb.setLastUpdTm(new Date());
                                    mchtBlackGreyInfoDao.updateByPrimaryKey(mb);
                                }
                            }
                        }else{
                            MchtBlackGreyInfo mbgi = new MchtBlackGreyInfo();
                            mbgi.setId(IfspId.getUUID32());
                            mbgi.setMchtId(rfi.getMchtId());
                            mbgi.setMchtListType(MchtListTypeDict.BLACK.getCode());
                            mbgi.setReason(BlackGreyReasonDict.REASON_7.getCode());
                            mbgi.setCrtTm(new Date());
                            mchtBlackGreyInfoDao.insert(mbgi);
                        }
                    }

                }
                cr.setRespCode("0000");
                cr.setRespMsg("=====================下载读取文件并更新黑灰名单数据成功！=================");
            }else{
                cr.setRespCode("9999");
                cr.setRespMsg("list为空或文件读取失败！");
            }

        }
        return cr;
    }

    /**
     * 读取下载后的文件
     */
    public static List<ReturnFileInfo> readFile(String localFile,String charset){
        BufferedReader reader = null;
        List<ReturnFileInfo> list = new ArrayList<ReturnFileInfo>();
        try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(localFile),charset));
                String tempString = null;
                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    //防止读取到空白的行
                    if("".equals(tempString)){
                        continue;
                    }
                    if(tempString.trim().startsWith("\"") || tempString.startsWith("“")){
                        tempString = tempString.trim().substring(1);
                    }
                    if(tempString.trim().endsWith("\"") || tempString.endsWith("”")){
                        tempString = tempString.trim().substring(0, tempString.length()-1);
                    }
                    if(tempString.trim().endsWith("|#|")){
                        tempString = tempString.trim().substring(0, tempString.length()-3);
                    }
                    String info[] = tempString.replaceAll(" ", "").replaceAll("，", ",").split("\\|#\\|",-1);

                    log.info("解析文件【"+ localFile +"】,内容:" + Arrays.toString(info));

                    ReturnFileInfo rfi = new ReturnFileInfo();
                    rfi.setMchtId(info[0]);
                    rfi.setDataType(info[1]);
                    rfi.setBlNo(info[2]);
                    rfi.setBlNo_result(info[3]);
                    rfi.setOwnerCertNo(info[4]);
                    rfi.setOwnerName(info[5]);
                    rfi.setName_result(info[6]);
                    list.add(rfi);
                }

            log.info("解析完毕！共"+list.size());
        } catch (IOException e) {
            log.info("文件读取失败！"+e.getMessage());
            log.error(e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    log.error(e1.toString());
                }

            }
            reader=null;
        }
        return list;
    }
    private void initPool() {
        destoryPool();
        /*
         * 构建
         */
        log.info("====初始化线程池(start)====");
        executor = Executors.newFixedThreadPool(mchtBlackGreyChkThreadCount, new ThreadFactory() {
            AtomicInteger atomic = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "mchtBlackGreyChkHander_" + this.atomic.getAndIncrement());
            }
        });
        log.info("====初始化线程池(end)====");
    }


    private void destoryPool() {
        log.info("====销毁线程池(start)====");
        /*
         * 初始化线程池
         */
        if (executor != null) {
            log.info("线程池为null, 无需清理");
            /*
             * 关闭线程池
             */
            try {
                executor.shutdown();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.out.println("awaitTermination interrupted: " + e);
                executor.shutdownNow();
            }
        }
        log.info("====销毁线程池(end)====");
    }
    /**
     * 工作线程
     *
     * @param <T>
     */
    class Handler<T> implements Callable<T> {

        //最小行数
        private DataInterval dataInterval;


        public Handler(DataInterval dataInterval) {
            this.dataInterval = dataInterval;
        }

        @Override
        public T call() throws Exception {
            try {
                log.info("====处理{}数据(start)====", dataInterval);
                /*
                 * 结果集合
                 */
                //对平
                List<BthChkRsltInfo> identicalList = new ArrayList<>();
                //不平: 包括本地单边 和 不平
                List<BillRecoErr> errorList = new ArrayList<>();
                /*
                 * 对账
                 */
                //分页查询出本地流水
                List<BthMchtListTempInfo> localRecords = bthMchtListTempInfoDao.queryByRange( dataInterval.getMin(), dataInterval.getMax());
                if (localRecords == null || localRecords.isEmpty()) {
                    log.warn("{}数据为null", dataInterval);
                } else {
                    log.debug("{},数据容量:{}", dataInterval, localRecords.size());
                    /**
                     *
                     */
                    for (BthMchtListTempInfo localRecord : localRecords) {
                        if(IfspDataVerifyUtil.equals("01",localRecord.getStatus())) {
                            continue; //已处理的不再处理
                        }
                        mchtBlackGreyHandler(localRecord);
                    }
                }
                log.debug("====处理{}数据(end)====", dataInterval);
            } catch (Exception e) {
                log.error("未知异常:", e);
                log.debug("====处理{}数据(error end)====", dataInterval);
                throw e;
            }
            return null;
        }
    }
    public void mchtBlackGreyHandler(BthMchtListTempInfo bthMchtListTempInfo){
        Date curDate = initDateByDay(new Date());
        boolean blackFlag = false;
        boolean greyFlag = false;

        Set<String> blackReasons = new HashSet<>();
        Set<String> greyReasons = new HashSet<>();



        //1、检查商户日期
        MchtBaseInfo mchtBaseInfo = mchtBaseInfoDao.queryById(bthMchtListTempInfo.getMchtId());
//        if(!IfspDataVerifyUtil.equals("106765100271111",mchtBaseInfo.getMchtId())
//          &&!IfspDataVerifyUtil.equals("106765100271111",mchtBaseInfo.getMchtId())){
//            return;
//        }
        //MchtContInfo mchtContInfo = mchtContInfoDao.queryByMchtId(bthMchtListTempInfo.getMchtId());
        //检查法人证件到期日期

        log.info("商户号[{}],创建日期[{}],证件到期日期[{}],营业执照到期类型[{}],营业执照到期日期[{}],最后交易日期[{}]",
                mchtBaseInfo.getMchtId(),
                mchtBaseInfo.getCrtTm()==null?"null":IfspDateTime.getYYYYMMDDHHMMSS(mchtBaseInfo.getCrtTm()),
                mchtBaseInfo.getOwnerCertExpDate()==null?"null":IfspDateTime.getYYYYMMDDHHMMSS(mchtBaseInfo.getOwnerCertExpDate()),
                mchtBaseInfo.getBlExpType(),
                mchtBaseInfo.getBlExpDate()==null?"null":IfspDateTime.getYYYYMMDDHHMMSS(mchtBaseInfo.getBlExpDate()),
                mchtBaseInfo.getOrderTmMax()
        );

        /**
         * 检查法人证件到期日期
         * 距到期日3个月至到期日的商户纳入灰名单
         */
        if(mchtBaseInfo.getOwnerCertExpDate()!=null){
            Date ownerCertExpDate  = initDateByDay(mchtBaseInfo.getOwnerCertExpDate());
            //判断日期是否进入黑名单
            if(curDate.compareTo(ownerCertExpDate)==1){
                blackReasons.add(BlackGreyReasonDict.REASON_3.getCode()); //证件到期
            }else if (curDate.compareTo(ownerCertExpDate)==0){
                greyReasons.add(BlackGreyReasonDict.REASON_1.getCode()); //证件将到期
            }else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(curDate);
                cal.add(Calendar.MONTH,3);//加上3个月
                if(cal.getTime().compareTo(ownerCertExpDate)>=0){
                    greyReasons.add(BlackGreyReasonDict.REASON_1.getCode()); //证件将到期
                }
            }
        }
        /**
         * 检查营业执照证件到期日期
         * 距到期日3个月至到期日的商户纳入灰名单
         */
        if(IfspDataVerifyUtil.equals(mchtBaseInfo.getBlExpType(),"1") && mchtBaseInfo.getBlExpDate()!=null){
            Date blExpDate  = initDateByDay(mchtBaseInfo.getBlExpDate());
            //判断日期是否进入黑名单
            if(curDate.compareTo(blExpDate)==1){
                blackReasons.add(BlackGreyReasonDict.REASON_3.getCode()); //证件到期
            }else if (curDate.compareTo(blExpDate)==0){
                greyReasons.add(BlackGreyReasonDict.REASON_1.getCode()); //证件将到期
            }else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(curDate);
                cal.add(Calendar.MONTH,3); //加上3个月
                if(cal.getTime().compareTo(blExpDate)>=0){
                    greyReasons.add(BlackGreyReasonDict.REASON_1.getCode()); //证件将到期
                }
            }
        }




        //2、检查商户订单
        //连续3个月无交易，即为注册日期满3个月且连续3个月无交易,纳入灰名单
        //连续1年无交易，即为注册日期满1年且连续1年无交易
        //注册当天也计算在内

        //检查订单时必须保证有创建日期
        if(IfspDataVerifyUtil.isNotBlank(mchtBaseInfo.getCrtTm())){

        Date crtTm  = initDateByDay(mchtBaseInfo.getCrtTm());
        Date startDate =  crtTm;
        Date endDate =  curDate;

        //计算是否满3个月
        Calendar crtCal = Calendar.getInstance();  //创建日期
        crtCal.setTime(crtTm);
        crtCal.add(Calendar.YEAR,1); //加上1年

        Calendar curCal = Calendar.getInstance();  //当前日期
        curCal.setTime(curDate);
        curCal.add(Calendar.DATE,-1);  //当前日期前一天

        if(crtCal.getTime().compareTo(curCal.getTime())<=0){//超过1年
            //需要统计交易量
            //当前日期前一天减1年
            //curCal.add(Calendar.YEAR,-1);
            String orderTmMax  = mchtBaseInfo.getOrderTmMax();
            if(IfspDataVerifyUtil.isBlank(orderTmMax)){
                blackReasons.add(BlackGreyReasonDict.REASON_4.getCode());
            }else{
                Date orderMaxDate = IfspDateTime.parseDate(orderTmMax,IfspDateTime.YYYYMMDD);  //最后订单日期
                if(orderMaxDate==null){  //商户基础信息表中保证订单日期不正确
                    blackReasons.add(BlackGreyReasonDict.REASON_4.getCode());
                }else{
                    Calendar   orderMaxDateCal = Calendar.getInstance();
                    orderMaxDateCal.setTime(orderMaxDate);
                    orderMaxDateCal.add(Calendar.YEAR,1);
                    if(orderMaxDateCal.getTime().compareTo(curCal.getTime())<0){
                        blackReasons.add(BlackGreyReasonDict.REASON_4.getCode());
                    }
                }

            }
//            int count =  payOrderInfoDao.countOrders(mchtBaseInfo.getMchtId(),curCal.getTime(),curDate);
//            if(count==0){
//                blackReasons.add(BlackGreyReasonDict.REASON_4.getCode());
//            }
        }
        if(!blackReasons.contains(BlackGreyReasonDict.REASON_4.getCode())){ //非黑名单时，检查灰名单
            //处理超过3个月小于1年的商户
            crtCal.setTime(crtTm);
            crtCal.add(Calendar.MONTH,3); //加上3个月
            curCal.setTime(curDate);
            curCal.add(Calendar.DATE,-1);  //当前日期前一天
            if(crtCal.getTime().compareTo(curCal.getTime())<=0){ //超过3个月
                //需要统计交易量
                //当前日期前一天减3个月
                //curCal.add(Calendar.MONTH,-3);


                String orderTmMax  = mchtBaseInfo.getOrderTmMax();
                if(IfspDataVerifyUtil.isBlank(orderTmMax)){
                    greyReasons.add(BlackGreyReasonDict.REASON_2.getCode());
                }else{
                    Date orderMaxDate = IfspDateTime.parseDate(orderTmMax,IfspDateTime.YYYYMMDD);  //最后订单日期
                    if(orderMaxDate==null){  //商户基础信息表中保证订单日期不正确
                        greyReasons.add(BlackGreyReasonDict.REASON_2.getCode());
                    }else{
                        Calendar   orderMaxDateCal = Calendar.getInstance();
                        orderMaxDateCal.setTime(orderMaxDate);
                        orderMaxDateCal.add(Calendar.MONTH,3);
                        if(orderMaxDateCal.getTime().compareTo(curCal.getTime())<0){
                            greyReasons.add(BlackGreyReasonDict.REASON_2.getCode());
                        }
                    }

                }
//
//
//                int count =  payOrderInfoDao.countOrders(mchtBaseInfo.getMchtId(),curCal.getTime(),curDate);
//                if(count==0){
//                    greyReasons.add(BlackGreyReasonDict.REASON_2.getCode());
//                }
            }
        }
        }
        log.info("商户号[{}]处理判断结果：黑名单{}，灰名单原因：{}",mchtBaseInfo.getMchtId(),IfspFastJsonUtil.tojson(blackReasons),IfspFastJsonUtil.tojson(greyReasons));

        //3、修改处理状态
        //1、检查是否已存在的商户
        Map param = new HashMap();
        MchtBlackGreyInfo mchtBlackGreyInfo = mchtBlackGreyInfoDao.selectByMchtId(mchtBaseInfo.getMchtId());
        if(mchtBlackGreyInfo==null){  //商户不存在黑灰名单中
            if(!blackReasons.isEmpty()){//商户存在黑存名单
                MchtBlackGreyInfo addMchtBlackGreyInfo = new MchtBlackGreyInfo();
                addMchtBlackGreyInfo.setId(IfspId.getUUID(24));
                addMchtBlackGreyInfo.setMchtId(mchtBaseInfo.getMchtId());
                addMchtBlackGreyInfo.setCrtTlr("batch");
                addMchtBlackGreyInfo.setCrtTm(IfspDateTime.getToDay());
                addMchtBlackGreyInfo.setMchtListType(MchtListTypeDict.BLACK.getCode());
                addMchtBlackGreyInfo.setReason(toReasonStr(blackReasons));
                mchtBlackGreyInfoDao.insert(addMchtBlackGreyInfo);
            }else if(!greyReasons.isEmpty()){//商户存在灰名单
                MchtBlackGreyInfo addMchtBlackGreyInfo = new MchtBlackGreyInfo();
                addMchtBlackGreyInfo.setId(IfspId.getUUID(24));
                addMchtBlackGreyInfo.setMchtId(mchtBaseInfo.getMchtId());
                addMchtBlackGreyInfo.setCrtTlr("batch");
                addMchtBlackGreyInfo.setCrtTm(IfspDateTime.getToDay());
                addMchtBlackGreyInfo.setMchtListType(MchtListTypeDict.GREY.getCode());
                addMchtBlackGreyInfo.setReason(toReasonStr(greyReasons));
                mchtBlackGreyInfoDao.insert(addMchtBlackGreyInfo);
            }
        }else{
            log.info("商户号[{}],原黑灰名单值[{}]",mchtBaseInfo.getMchtId(),mchtBlackGreyInfo.getReason());
            //满足黑名单条件
            /**
             * 1、原为灰名单，直接改为黑名单，并更新所有的原因
             * 2、原为黑名单，先去掉 营业执照日期，证件日期，交易量因素重新添加
             * 3、
             */
            if(!blackReasons.isEmpty()){
                    //原为灰名单处理
                    if(IfspDataVerifyUtil.equals(mchtBlackGreyInfo.getMchtListType(), MchtListTypeDict.GREY.getCode())){
                        mchtBlackGreyInfo.setMchtListType(MchtListTypeDict.BLACK.getCode());
                        mchtBlackGreyInfo.setReason(toReasonStr(blackReasons));
                        mchtBlackGreyInfo.setLastUpdTlr("batch");
                        mchtBlackGreyInfo.setLastUpdTm(IfspDateTime.getToDay());
                        mchtBlackGreyInfoDao.updateByPrimaryKey(mchtBlackGreyInfo);
                    }else{ //原为黑名单处理
                        Set<String> oldReason = getReasonSet(mchtBlackGreyInfo.getReason());
                        oldReason.remove(BlackGreyReasonDict.REASON_3.getCode());
                        //oldReason.remove(BlackGreyReasonDict.REASON_4.getCode()); 不能通过指移除黑，灰名单
                        oldReason.addAll(blackReasons);
                        mchtBlackGreyInfo.setReason(toReasonStr(oldReason));
                        mchtBlackGreyInfo.setLastUpdTlr("batch");
                        mchtBlackGreyInfo.setLastUpdTm(IfspDateTime.getToDay());
                        mchtBlackGreyInfoDao.updateByPrimaryKey(mchtBlackGreyInfo);
                    }
                }else if (!greyReasons.isEmpty()){  //只存在灰名单
                //原为灰名单处理
                if(IfspDataVerifyUtil.equals(mchtBlackGreyInfo.getMchtListType(), MchtListTypeDict.GREY.getCode())){

                    Set<String> oldReason = getReasonSet(mchtBlackGreyInfo.getReason());
                    oldReason.remove(BlackGreyReasonDict.REASON_1.getCode());
                    // oldReason.remove(BlackGreyReasonDict.REASON_2.getCode()); // 不能通过指移除黑，灰名单
                    oldReason.addAll(greyReasons);
                    mchtBlackGreyInfo.setReason(toReasonStr(oldReason));
                    mchtBlackGreyInfo.setLastUpdTlr("batch");
                    mchtBlackGreyInfo.setLastUpdTm(IfspDateTime.getToDay());
                    mchtBlackGreyInfoDao.updateByPrimaryKey(mchtBlackGreyInfo);
                }else{ //原为黑名单处理
                    Set<String> oldReason = getReasonSet(mchtBlackGreyInfo.getReason());
                    oldReason.remove(BlackGreyReasonDict.REASON_3.getCode());
                    //oldReason.remove(BlackGreyReasonDict.REASON_2.getCode()); 不能通过指移除黑，灰名单
                    if(!oldReason.isEmpty()){ //清除后仍存在黑名单原因，则保存黑名单状态
                        mchtBlackGreyInfo.setMchtListType(MchtListTypeDict.BLACK.getCode());
                        mchtBlackGreyInfo.setLastUpdTlr("batch");
                        mchtBlackGreyInfo.setReason(toReasonStr(oldReason));
                        mchtBlackGreyInfo.setLastUpdTm(IfspDateTime.getToDay());
                        mchtBlackGreyInfoDao.updateByPrimaryKey(mchtBlackGreyInfo);
                    }else{//清除后无黑名单原因，则应修改为灰名单
                        mchtBlackGreyInfo.setMchtListType(MchtListTypeDict.GREY.getCode());
                        mchtBlackGreyInfo.setReason(toReasonStr(greyReasons));
                        mchtBlackGreyInfo.setLastUpdTlr("batch");
                        mchtBlackGreyInfo.setLastUpdTm(IfspDateTime.getToDay());
                        mchtBlackGreyInfoDao.updateByPrimaryKey(mchtBlackGreyInfo);
                    }
                }
                    }else{ //到期日期，不存在灰名单条件，也不存在黑名单条件
                    Set<String> oldReason = getReasonSet(mchtBlackGreyInfo.getReason());
                    oldReason.remove(BlackGreyReasonDict.REASON_1.getCode());
                    oldReason.remove(BlackGreyReasonDict.REASON_3.getCode());
                    if(oldReason.isEmpty()){ //若原来只有到期日期的原因，删除记录
                        mchtBlackGreyInfoDao.deleteByPrimaryKey(mchtBlackGreyInfo.getId());
                    }else{
                        mchtBlackGreyInfo.setReason(toReasonStr(oldReason));
                        mchtBlackGreyInfo.setLastUpdTlr("batch");
                        mchtBlackGreyInfo.setLastUpdTm(IfspDateTime.getToDay());
                        mchtBlackGreyInfoDao.updateByPrimaryKey(mchtBlackGreyInfo);
                    }

            }
        }
        bthMchtListTempInfo.setStatus("01");
        bthMchtListTempInfo.setLastUpdTm(IfspDateTime.getToDay());
        bthMchtListTempInfoDao.update(bthMchtListTempInfo);

    }
    public String toReasonStr(Set<String> reasonSet){
        StringBuffer sb = new StringBuffer();
        for(String str : reasonSet){
            sb.append(str).append(",");
        }
        if(sb.length()>0){
            return sb.substring(0,sb.length()-1);
        }
        return sb.toString();

    }
    public Set<String> getReasonSet(String reasons){
        Set<String> result = new HashSet();
        if(IfspDataVerifyUtil.isBlank(reasons)) return result;

        String[] arr = reasons.split(",");
        for(int i=0;i<arr.length;i++){
            if(IfspDataVerifyUtil.isNotBlank(arr[i])){
                result.add(arr[i]);
            }
        }
        return result;
    }
    /**
     * 获得当天零时零分零秒
     * @return
     */
    public Date initDateByDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }
}

