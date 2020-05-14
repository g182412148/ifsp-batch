package com.scrcu.ebank.ebap.batch.bean.dto;

import java.io.InputStream;

public class FileInfo {
    String fileBizTopic;
    String fileBizId;
    String fileBizTag;
    String fileName;
    InputStream inputStream;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileBizTopic() {
        return fileBizTopic;
    }

    public void setFileBizTopic(String fileBizTopic) {
        this.fileBizTopic = fileBizTopic;
    }

    public String getFileBizId() {
        return fileBizId;
    }

    public void setFileBizId(String fileBizId) {
        this.fileBizId = fileBizId;
    }

    public String getFileBizTag() {
        return fileBizTag;
    }

    public void setFileBizTag(String fileBizTag) {
        this.fileBizTag = fileBizTag;
    }
}
