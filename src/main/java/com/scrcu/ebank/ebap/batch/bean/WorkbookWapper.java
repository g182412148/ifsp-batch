package com.scrcu.ebank.ebap.batch.bean;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;

public class WorkbookWapper {

    private File file;
    private Workbook workbook;

    public WorkbookWapper(File file, Workbook workbook) {
        this.file = file;
        this.workbook = workbook;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }
}
