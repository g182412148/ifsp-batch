package com.scrcu.ebank.ebap.batch.service.impl;

import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.file.ftp.IfspFtpClientUtil;
import com.scrcu.ebank.ebap.batch.bean.request.BatchRequest;
import com.scrcu.ebank.ebap.batch.bean.vo.ecif.FileResultVo;
import com.scrcu.ebank.ebap.batch.common.constant.ECIFConstants;
import com.scrcu.ebank.ebap.batch.common.dict.RespConstans;
import com.scrcu.ebank.ebap.batch.service.EcifFileService;
import com.scrcu.ebank.ebap.batch.service.EcifService;
import com.scrcu.ebank.ebap.common.beans.CommonResponse;
import com.scrcu.ebank.ebap.exception.IfspBizException;
import com.scrcu.ebank.ebap.exception.template.IfspMessageCoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;

/**
 * 描述 </br>
 *
 * @author M.chen
 * 2019/6/13 15:09
 */
@Service
@Slf4j
public class EcifServiceImpl implements EcifService {
    @Resource
    private EcifFileService ecifFileService;

    @Override
    public CommonResponse ecifUpdateMchtInfo(BatchRequest request) {

        log.info("=================开始ecif商户文件生成=============");
        // 开始时间
        long startTm = System.currentTimeMillis();
        log.info("开始时间" + startTm);
        try {
            // 文件日期
            String yyyymmdd = IfspDateTime.getYYYYMMDD(IfspDateTime.getYYYYMMDD(request.getSettleDate()));
            // FTP工具
            IfspFtpClientUtil util = new IfspFtpClientUtil(
                    IfspMessageCoreUtil.getMessage("ecifIp"),//FTP服务器地址
                    21,//FTP服务器端口
                    IfspMessageCoreUtil.getMessage("ecifUserNm"),//FTP服务器用户名
                    IfspMessageCoreUtil.getMessage("ecifPwd"),//FTP服务器密码
                    null,//字符集
                    false//FTP链接模式：true:被动模式  /false: 主动方式
            );
            // ok文件名字
            String okFileNm = IfspMessageCoreUtil.getMessage("ecifServer") + yyyymmdd + ".OK";
            // ok文件路径
            String okUpPath = IfspMessageCoreUtil.getMessage("ecifOKUpPath");
            /* 判断ok文件是否上传成功
            boolean fileExist = util.isFileExist(okUpPath, okFileNm);
            if (fileExist) {
                log.info("文件已上传成功");
                throw new IfspBizException(RespConstans.RESP_ECIF_READY_UP_SUC.getCode(), RespConstans.RESP_ECIF_READY_UP_SUC.getDesc());
            }*/
            // 文件名字
            String fileNorName = IfspMessageCoreUtil.getMessage("ecifNorFileNm") + yyyymmdd + ".FDL";//普通
            String fileMiniName = IfspMessageCoreUtil.getMessage("ecifMiniFileNm") + yyyymmdd + ".FDL";//小微
            //查询并写入文件
            FileResultVo norResult = ecifFileService.createNormalUpdateFile(request.getSettleDate());//文件创建参数
            //查询并写入文件
            FileResultVo miniResult = ecifFileService.createMiniUpdateFile(request.getSettleDate());
            // ========================== 上传文件 ==========================
            // 上传普通商户
            boolean nor = util.uploadFile(IfspMessageCoreUtil.getMessage("ecifUpPath"), fileNorName, new ByteArrayInputStream(norResult.getMsg().getBytes()), true);
            // 上传小微商户
            boolean mini = util.uploadFile(IfspMessageCoreUtil.getMessage("ecifUpPath"), fileMiniName, new ByteArrayInputStream(miniResult.getMsg().getBytes()), true);
            // 判断结果并生成OK文件
            if (nor && mini) {
                /* 成功则上传OK文件 */
                // 追加文字
                String ok = fileNorName + " " + norResult.getCount() + ECIFConstants.NEW_LINE + fileMiniName + " " + miniResult.getCount() + ECIFConstants.NEW_LINE;
                // 获取输入流
                ByteArrayInputStream bis = new ByteArrayInputStream(ok.getBytes());
                // 上传OK文件
                util.uploadFile(okUpPath, okFileNm, bis, true);
            }
        } catch (Exception e) {
            log.info("商户文件生成失败", e);
        } finally {
            // 结束时间
            long endTm = System.currentTimeMillis();
            log.info("商户文件生成用时:" + (endTm - startTm) + "秒");
        }
        return new CommonResponse();
    }
}
