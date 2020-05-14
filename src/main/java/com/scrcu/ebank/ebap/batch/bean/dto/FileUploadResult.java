package com.scrcu.ebank.ebap.batch.bean.dto;

public class FileUploadResult {
    public final static String failRespCode = "9999";
    public final static String successRespCode = "0000";
    String respCode;
    String respMsg;
    String fileId;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }
}
