package com.scrcu.ebank.ebap.batch.utils;

import com.alibaba.fastjson.JSONObject;
import com.ruim.ifsp.utils.datetime.IfspDateTime;
import com.ruim.ifsp.utils.id.IfspId;
import com.scrcu.ebank.ebap.batch.bean.dto.FileInfo;
import com.scrcu.ebank.ebap.batch.bean.dto.FileUploadResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
public class FileLoadUtils {

    private final static String reqChnl = "01";
    
    public static FileUploadResult fileUpload(String uploadFileUrl, String uploadFileName) {
        log.info("====================文件上传(start)====================");
        log.info("upload loacl file :" + uploadFileName);
        FileUploadResult result = new FileUploadResult();
        result.setRespCode(FileUploadResult.failRespCode);
        result.setRespMsg("上传初始化");
        try {
        	FileInfo fileInfo = new FileInfo();
        	File f = new File(uploadFileName);
            String fileName = f.getName();
            String fileBizTopic = "0000";
            String fileBizId = "";
            String fileBizTag = "mchtChk";
            
            fileInfo.setInputStream(new FileInputStream(f));
            fileInfo.setFileName(fileName);
            fileInfo.setFileBizTopic(fileBizTopic);
            fileInfo.setFileBizId(fileBizId);
            fileInfo.setFileBizTag(fileBizTag);
            result = uploadToFileServer(uploadFileUrl, fileInfo);
            log.info("文件服务响应报文:" + JSONObject.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件失败:", e);
            result.setRespCode(FileUploadResult.failRespCode);
            result.setRespMsg("上传文件失败");
        }finally {
            //删除临时文件
        }
        return result;
    }

    private static FileUploadResult uploadToFileServer(String uploadFileUrl, FileInfo fileInfo) {
        FileUploadResult result = new FileUploadResult();
        result.setRespCode(FileUploadResult.successRespCode);
        result.setRespMsg("成功");
        if (fileInfo == null) {
            log.error("上传文件为空");
            result.setRespCode(FileUploadResult.failRespCode);
            result.setRespMsg("上传文件为空");
            return result;
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String msgResult = null;
        try (InputStream inputStream = fileInfo.getInputStream()){
            log.info("文件服务请求地址:" + uploadFileUrl);
            HttpPost httpPost = new HttpPost(uploadFileUrl);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("reqSsn", IfspId.getId24());
            builder.addTextBody("reqTm", new DateTime().toString("yyyyMMddHHmmss"));
            builder.addTextBody("reqChnl", reqChnl);
            builder.addTextBody("fileName", fileInfo.getFileName());
            builder.addTextBody("fileBizTopic", fileInfo.getFileBizTopic());
            builder.addTextBody("fileBizId", fileInfo.getFileBizId());
            builder.addTextBody("fileBizTag", fileInfo.getFileBizTag());
            builder.addTextBody("fileExpType", "00");  //文件有效期类型: 永久保存
            builder.addTextBody("addlOrigFlag", "00");  //附加文件处理
            log.info("文件服务请求报文:" + JSONObject.toJSONString(builder));
            builder.addBinaryBody("file", inputStream, ContentType.MULTIPART_FORM_DATA, fileInfo.getFileName());
            httpPost.setEntity(builder.build());
            response = httpclient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            log.info("文件服务HTTP响应状态:" + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                msgResult = EntityUtils.toString(resEntity);
                if(msgResult==null || msgResult.length()==0){
                    log.error("文件上传失败,服务端响应信息为空");
                    result.setRespCode(FileUploadResult.failRespCode);
                    result.setRespMsg("文件上传失败");
                    return result;
                }
                // 消耗掉response
                EntityUtils.consume(resEntity);
                result = JSONObject.parseObject(msgResult, FileUploadResult.class);
                return result;
            }else{
                log.error("文件上传失败,服务端响应状态码：" + statusCode );
                result.setRespCode(FileUploadResult.failRespCode);
                result.setRespMsg("文件上传失败");
                return result;
            }
        } catch (Exception e) {
            log.error("文件上传失败", e);
            result.setRespCode(FileUploadResult.failRespCode);
            result.setRespMsg("文件上传失败");
            return result;
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
            HttpClientUtils.closeQuietly(response);
        }
    }

    public static void fileDownload(String downLoadFileUrl, String fileId, String downloadFileName) {
        //载入图片到输入流
        BufferedInputStream bis=null;
        //设置写入路径以及图片名称
        OutputStream bos = null;
        try {
            log.info("down loacl file :" + downloadFileName);
//        	FileLoad fl = new FileLoad();
            log.info("DOWNLOAD_FILE_URL:" + downLoadFileUrl);
            StringBuffer downloadUrl = new StringBuffer();
            downloadUrl.append(downLoadFileUrl);
            downloadUrl.append("?");
            downloadUrl.append("reqSsn=").append(IfspId.getId24());
            downloadUrl.append("&reqTm=").append(IfspDateTime.getYYYYMMDDHHMMSS());
            downloadUrl.append("&reqChnl=").append("00");
            downloadUrl.append("&fileId=").append(fileId);
            log.info("远程调用取文件开始:{}",downloadUrl.toString());
            //实例化url
            URL url = new URL(downloadUrl.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.connect();
            Map<String,List<String>> headerFileds = connection.getHeaderFields();

            //设置response head值
//            if(headerFileds!=null){
//                String headerKey = "Content-Disposition";
//                if(headerFileds.containsKey(headerKey))   response.setHeader(headerKey,StringUtils.join(headerFileds.get(headerKey).toArray(),","));
//                headerKey = "Content-Type";
//                if(headerFileds.containsKey(headerKey))   response.setHeader(headerKey,StringUtils.join(headerFileds.get(headerKey).toArray(),","));
//            }
//            response.setStatus(connection.getResponseCode());
            //载入s图片到输入流
            bis = new BufferedInputStream(connection.getInputStream());
            //实例化存储字节数组 1M
            byte[] bytes = new byte[1024];
            //设置写入路径以及图片名称
            bos = new FileOutputStream(new File(downloadFileName));
            int len;
            while ((len = bis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            bis.close();
            bos.flush();
            bos.close();
            log.info("文件下载成功，fileUrl="+downloadUrl.toString());
            //关闭输出流
        } catch (Exception e) {
            e.printStackTrace();
            log.info("取文件出错，返回500错误");
            log.info(e.getMessage());
            log.info(ArrayUtils.toString(e.getStackTrace()));
        }finally {
            if(bis!=null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bos!=null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public static FileUploadResult errFileUpload(String uploadFileUrl, String uploadFileName,String fileBizTag) {
        log.info("====================文件上传(start)====================");
        log.info("upload loacl file :" + uploadFileName);
        FileUploadResult result = new FileUploadResult();
        result.setRespCode(FileUploadResult.failRespCode);
        result.setRespMsg("上传初始化");
        try {
            FileInfo fileInfo = new FileInfo();
            File f = new File(uploadFileName);
            String fileName = f.getName();
            String fileBizTopic = "0000";
            String fileBizId = "";

            fileInfo.setInputStream(new FileInputStream(f));
            fileInfo.setFileName(fileName);
            fileInfo.setFileBizTopic(fileBizTopic);
            fileInfo.setFileBizId(fileBizId);
            fileInfo.setFileBizTag(fileBizTag);
            result = uploadToFileServer(uploadFileUrl, fileInfo);
            log.info("文件服务响应报文:" + JSONObject.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件失败:", e);
            result.setRespCode(FileUploadResult.failRespCode);
            result.setRespMsg("上传文件失败");
        }finally {
            //删除临时文件
        }
        return result;
    }

}
