package com.scrcu.ebank.ebap.batch.common.utils;

import com.scrcu.ebank.ebap.batch.bean.ErrorInfo;
import com.scrcu.ebank.ebap.batch.bean.ExcelData;
import com.scrcu.ebank.ebap.batch.bean.WorkbookWapper;
import com.scrcu.ebank.ebap.batch.bean.vo.TxnCountVo;
import com.scrcu.ebank.ebap.log.IfspLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 */
public abstract class ExcelUtils {

    static Logger log = IfspLoggerFactory.getLogger(ExcelUtils.class);


    public static List<WorkbookWapper> getExcels(String path) throws IOException {
        return getExcels(FileUtils.getFiles(path));
    }

    /**
     * 获取Excel表格
     * @param fileList
     * @return
     * @throws IOException
     */
    public static List<WorkbookWapper> getExcels(List<File> fileList) throws IOException {
        List<WorkbookWapper> result = new ArrayList<>();
        if(fileList != null && !fileList.isEmpty()){
            for(File file : fileList){
                try(FileInputStream fis = new FileInputStream(file)){
                    Workbook wb;
                    WorkbookWapper wbBean;
                    if(file.getName().endsWith(".xls")){
                        wb = new HSSFWorkbook(fis);
                        wbBean = new WorkbookWapper(file, wb);
                    }else if(file.getName().endsWith(".xlsx")){
                        wb = new XSSFWorkbook(fis);
                        wbBean = new WorkbookWapper(file, wb);
                    }else {
                        log.error("跳过处理: [" + file.getName() + "]不是excel表格.");
                        continue;
                    }
                    result.add(wbBean);
                }catch (IOException e){
                    throw e;
                }
            }
        }
        return result;
    }


    /**
     * 获取Excel表格
     */
    public static List<WorkbookWapper> getExcelsNotEmpty(String path){
        log.info("扫描[" + path + "目录, 获取待处理的Excel表格...");
        //获取商户数据excel表格
        List<WorkbookWapper> excels;
        try {
            excels = ExcelUtils.getExcels(path);
        }catch (Exception e){
            log.error("获取Excel表格失败: ", e);
            throw new RuntimeException("获取Excel表格失败");
        }
        //校验excel表格
        if(excels == null || excels.isEmpty()){
            log.error("没有需要处理的商户excel表格");
            throw new RuntimeException("没有需要处理的商户excel表格");
        }
        return excels;
    }

    /**
     * 获取单元格内容
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell){
        DecimalFormat df = new DecimalFormat("0");
        String value;
        if (cell!=null){
            if (cell.getCellType() == Cell.CELL_TYPE_STRING) { //字符串
                value = cell.getStringCellValue().trim();
            } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) { //数值
                value = df.format(cell.getNumericCellValue());
            } else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){//公式
                value=df.format(cell.getNumericCellValue());
            }
            else if (cell.getCellType()==Cell.CELL_TYPE_BLANK){//空白
                value="";
            }else {
                log.error("不支持的数据类型:" + cell.getCellType());
                throw new RuntimeException("单元格类型非法:" + cell.getCellType());
            }
        }else{
            value="";
        }




        return value;
    }

    /**
     * 获取单元格内容
     * @param row
     * @param index
     * @return
     */
    public static String getCellValue(Row row, int index){
        return getCellValue(row.getCell(index));
    }

    /**
     * 根据行, 获取数据
     */
    public static <T> T getInstance(Class<T> clazz, Row row) throws IllegalAccessException, InstantiationException {
        T t = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        Field errorInfoField = null;
        StringBuffer errorDesc = new StringBuffer();
        if(fields != null && fields.length>=0){
            for(Field field : fields){
                //设置可存取
                field.setAccessible(true);
                //获取字段注解
                ExcelData excelData = field.getAnnotation(ExcelData.class);
                //获取字段注解
                ErrorInfo errorInfo = field.getAnnotation(ErrorInfo.class);
                if(excelData == null && errorInfo == null){
                    throw new RuntimeException("类" +  clazz.getName() + "字段的注解为空");
                }else if(excelData != null && errorInfo!= null){
                    throw new RuntimeException("@ExcelData和@ErrorInfo不能同时存在");
                }
                if(errorInfo != null && errorInfoField != null){
                    throw new RuntimeException("@ErrorInfo只能存在一个");
                }else if(errorInfo != null){
                    errorInfoField = field;
                    continue;
                }
                //获取字段名称
                String name = excelData.name();
                if(StringUtils.isBlank(name)){
                    throw new IllegalArgumentException("属性" + field.getName() +"@ExcelData中的name不能为空");
                }
                //获取字段所在单元格索引
                int cellIndex = excelData.cellIndex();
                if(cellIndex < 0){
                    throw new IllegalArgumentException("属性" + field.getName() +"@ExcelData中的cellIndex不能小于0");
                }
                //获取字段正则表达式
                String regex = excelData.regex();
                //获取单元格值
                String cellValue;
                try {
                    cellValue = ExcelUtils.getCellValue(row, cellIndex);
                }catch (Exception e){
                    log.error("获取单元格值失败：", e.getMessage());
                    errorDesc.append("第" + (row.getRowNum() + 1) +"行|第" + (cellIndex + 1) + "列, [" + name + "]获取单元格值失败: " + e.getMessage());
                    continue;
                }

                //校验值是否有效
                if(StringUtils.isNotBlank(regex)){
                    if(!Pattern.matches(regex, cellValue)){
                        log.warn("第" + (row.getRowNum() + 1) +"行|第" + (cellIndex + 1) + "列, [" + name + "]值非法");
                        errorDesc
                                .append("第" + (row.getRowNum() + 1) +"行|第" + (cellIndex + 1) + "列, [" + name + "]值非法")
                                .append(";");
                    }
                }
                //设置值
                try {
                    field.set(t, cellValue);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("属性" + field.getName() + "不支持存取.");
                }
            }
            //记录错误信息
            if(errorInfoField != null){
                errorInfoField.set(t, errorDesc.toString());
            }else {
                log.warn("未找到错误信息字段, 无需记录.");
            }
        }
        return t;
    }


    /**
     * 解析Excel表格, 获取Bean集合
     * @param path excel文件目录
     * @param clazz bean类型
     * @param headCount 表头高度
     * @param <T>
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> List<T> parseExcel(String path, Class<T> clazz, int headCount) throws InstantiationException, IllegalAccessException {
        //每个Excel的表头
        int startHeadCount=headCount;
        int start=headCount;
        List<WorkbookWapper> excels = ExcelUtils.getExcelsNotEmpty(path);
        List<T> result = new ArrayList<>();
        log.info("需要处理的文件数量:" + excels.size());
        //解析
        for(WorkbookWapper wb : excels){
            long startTime = System.currentTimeMillis();
            log.info("=============处理" + wb.getFile().getName() + "(start)=============");
            startHeadCount=start;
            //有数据的Sheet页
            Sheet sheet = wb.getWorkbook().getSheetAt(0);
            //行数
            int totalCount = sheet.getLastRowNum() + 1;
            log.info("总行数:" + totalCount);
            log.info("表头行数:" + headCount);
            //数据行数
            int dataCount = totalCount - start;
            log.info("数据行数:" + dataCount);
            //循环处理: 跳过表头
            for(; startHeadCount < totalCount; startHeadCount++){
                //获取当前行
                Row row =  sheet.getRow(startHeadCount);
                //获取数据
                T t = ExcelUtils.getInstance(clazz, row);
                result.add(t);
            }

            long endTime = System.currentTimeMillis();
            log.info("=============处理" + wb.getFile().getName() + "(end), 耗时: " +(endTime - startTime)+ "=============");
        }

        return result;
    }

    /**
     * 生成表格
     * @param dataList 数据
     * @param dataList2 数据
     * @param path 文件存放路径
     * @param fileName 文件名
     */
    public static <T,T2>  void createExcel(List<T> dataList, List<T2> dataList2,int merNum, String inAccEndTm, String path, String fileName) throws IllegalAccessException {
        long startTime = System.currentTimeMillis();
        log.info("=============生成文件:[" + path + fileName + "](start)=============");

        /*
         *  创建Excel
         */
        XSSFWorkbook wb = new XSSFWorkbook();

        /**
         * 创建第一个sheet
         */
        XSSFSheet sheet = wb.createSheet("入账失败记录");
        //当前记录行数
        int currentRowNum = 0;
        /*
         *  生成数据
         */
        if(dataList != null && !dataList.isEmpty()){
            /*
                解析类字段
             */
            Field[] fields = dataList.get(0).getClass().getDeclaredFields();
            if(fields == null || fields.length<=0){
                log.warn("数据字段为空, 无法生成数据!");
                return;
            }
            /*
             *  生成表头
             */
            XSSFRow headRow = sheet.createRow(currentRowNum);
            currentRowNum ++ ;
            for(Field field : fields){
                //获取字段注解
                ExcelData excelData = field.getAnnotation(ExcelData.class);
                //获取字段注解
                ErrorInfo errorInfo = field.getAnnotation(ErrorInfo.class);
                if(excelData == null && errorInfo == null){
                    throw new RuntimeException("类" +  dataList.get(0).getClass().getName() + "字段的注解为空");
                }else if(excelData != null && errorInfo!= null){
                    throw new RuntimeException("@ExcelData和@ErrorInfo不能同时存在");
                }else if(excelData != null){
                    headRow.createCell(excelData.cellIndex(), Cell.CELL_TYPE_STRING).setCellValue(excelData.name());
                }else if(errorInfo != null){
                    headRow.createCell(errorInfo.cellIndex(), Cell.CELL_TYPE_STRING).setCellValue(errorInfo.name());
                }
            }
            /*
                解析数据
             */
            XSSFRow dataRow;
            for(T t : dataList){
                //创建一行
                dataRow = sheet.createRow(currentRowNum);
                currentRowNum ++ ;
                //生成单元格
                for(Field field : fields){
                    //设置可存取
                    field.setAccessible(true);
                    //获取字段值
                    Object value = field.get(t);
                    //获取字段注解
                    ExcelData excelData = field.getAnnotation(ExcelData.class);
                    //获取字段注解
                    ErrorInfo errorInfo = field.getAnnotation(ErrorInfo.class);
                    //填充对应单元格数据
                    if(excelData != null){
                        dataRow.createCell(excelData.cellIndex(), Cell.CELL_TYPE_STRING).setCellValue(value == null? "" : value.toString());
                    }else if(errorInfo != null){
                        dataRow.createCell(errorInfo.cellIndex(), Cell.CELL_TYPE_STRING).setCellValue(value == null? "" : value.toString());
                    }
                }
            }
        }



        /**
         * 创建第二个sheet
         */

        XSSFSheet sheet2 = wb.createSheet("跑批结果");
        //第一个参数代表列id(从0开始),第2个参数代表宽度值
        sheet2.setColumnWidth(0, 5000);

        /**
         * 样式
         */
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);


        CellStyle style2 = wb.createCellStyle();
        style2.setBorderBottom(CellStyle.BORDER_THIN);
        style2.setBorderLeft(CellStyle.BORDER_THIN);
        style2.setBorderRight(CellStyle.BORDER_THIN);
        style2.setBorderTop(CellStyle.BORDER_THIN);
        style2.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        style2.setFillPattern(CellStyle.SOLID_FOREGROUND);


        CellStyle style3 = wb.createCellStyle();
        style3.setBorderBottom(CellStyle.BORDER_THIN);
        style3.setBorderLeft(CellStyle.BORDER_THIN);
        style3.setBorderRight(CellStyle.BORDER_THIN);
        style3.setBorderTop(CellStyle.BORDER_THIN);
        style3.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        style3.setFillPattern(CellStyle.SOLID_FOREGROUND);



        //当前记录行数
        int currentRowNum2 = 0;
        /**
         * 生成数据
         */
        if(dataList2 != null && !dataList2.isEmpty()){
             /*
                解析类字段
             */
            Field[] fields = dataList2.get(0).getClass().getDeclaredFields();
            if(fields == null || fields.length<=0){
                log.warn("数据字段为空, 无法生成数据!");
                return;
            }
            /*
             *  生成表头
             */
            XSSFRow headRow = sheet2.createRow(currentRowNum2);
            currentRowNum2 ++ ;
            for(Field field : fields){
                //获取字段注解
                ExcelData excelData = field.getAnnotation(ExcelData.class);
                //获取字段注解
                ErrorInfo errorInfo = field.getAnnotation(ErrorInfo.class);
                if(excelData == null && errorInfo == null){
                    throw new RuntimeException("类" +  dataList2.get(0).getClass().getName() + "字段的注解为空");
                }else if(excelData != null && errorInfo!= null){
                    throw new RuntimeException("@ExcelData和@ErrorInfo不能同时存在");
                }else if(excelData != null){
                    XSSFCell cell = headRow.createCell(excelData.cellIndex(), Cell.CELL_TYPE_STRING);
                    cell.setCellValue(excelData.name());
                    cell.setCellStyle(style2);
                }else if(errorInfo != null){
                    XSSFCell cell = headRow.createCell(errorInfo.cellIndex(), Cell.CELL_TYPE_STRING);
                    cell.setCellValue(errorInfo.name());
                    cell.setCellStyle(style2);
                }
            }
            /*
                解析数据
             */
            XSSFRow dataRow;
            for(T2 t : dataList2){
                //创建一行
                dataRow = sheet2.createRow(currentRowNum2);
                currentRowNum2 ++ ;
                // 如果是合计行 , 设背景色
                boolean colFlag = false;
                TxnCountVo v = (TxnCountVo)t;
                if ("小计: ".equals(v.getFundChannel()) || "总计: ".equals(v.getFundChannel())  ){
                    colFlag = true;
                }else {
                    colFlag = false;
                }

                //生成单元格
                for(Field field : fields){
                    //设置可存取
                    field.setAccessible(true);
                    //获取字段值
                    Object value = field.get(t);
                    //获取字段注解
                    ExcelData excelData = field.getAnnotation(ExcelData.class);
                    //获取字段注解
                    ErrorInfo errorInfo = field.getAnnotation(ErrorInfo.class);
                    //填充对应单元格数据
                    if(excelData != null){
                        XSSFCell cell = dataRow.createCell(excelData.cellIndex(), Cell.CELL_TYPE_STRING);
                        cell.setCellValue(value == null? "" : value.toString());
                        if (colFlag){
                            cell.setCellStyle(style3);
                        }else {
                            cell.setCellStyle(style);
                        }
                    }else if(errorInfo != null){
                        XSSFCell cell = dataRow.createCell(errorInfo.cellIndex(), Cell.CELL_TYPE_STRING);
                        cell.setCellValue(value == null? "" : value.toString());
                        if (colFlag){
                            cell.setCellStyle(style3);
                        }else {
                            cell.setCellStyle(style);
                        }
                    }
                }
            }


            XSSFRow row = sheet2.createRow(currentRowNum2);
            currentRowNum2 ++ ;
            XSSFCell cell = row.createCell(0,  Cell.CELL_TYPE_STRING);
            cell.setCellValue("入账成功总共"+merNum+"个商家, 入账结束时间为 "+inAccEndTm+"");

        }


        /*
         * 写入Excel文件
         */
        try (
            FileOutputStream fos = new FileOutputStream(path + fileName)
        ){
            wb.write(fos);
            fos.flush();
        } catch (Exception e) {
            log.info("写入excel文件失败", e);
        }

        long endTime = System.currentTimeMillis();
        log.info("=============生成文件:[" + path + fileName + "](end), 耗时: " +(endTime - startTime)+ "=============");
    }

    /**
     * 解析Excel表格, 获取Bean集合
     * @param path 文件夹路径
     * @param clazz
     * @param headCount
     * @param <T>
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> Map<String, List<T>> newParseExcel(String path, Class<T> clazz, int headCount) throws InstantiationException, IllegalAccessException {
        Map<String, List<T>> result = new HashMap<>();
        //每个Excel的表头
        int startHeadCount=headCount;
        int start=headCount;
        List<WorkbookWapper> excels = ExcelUtils.getExcelsNotEmpty(path);
        List<T> beanResult = new ArrayList<>();
        log.info("需要处理的文件数量:" + excels.size());
        //解析
        for(WorkbookWapper wb : excels){
            long startTime = System.currentTimeMillis();
            log.info("=============处理" + wb.getFile().getName() + "(start)=============");
            startHeadCount=start;
            //有数据的Sheet页
            Sheet sheet = wb.getWorkbook().getSheetAt(0);
            //行数
            int totalCount = sheet.getLastRowNum() + 1;
            log.info(wb.getFile().getName() + "_总行数:" + totalCount);
            log.info(wb.getFile().getName() + "_表头行数:" + headCount);
            //数据行数
            int dataCount = totalCount - start;
            log.info(wb.getFile().getName() + "_数据行数:" + dataCount);
            //循环处理: 跳过表头
            for(; startHeadCount < totalCount; startHeadCount++){
                //获取当前行
                Row row =  sheet.getRow(startHeadCount);
                //获取数据
                T t = ExcelUtils.getInstance(clazz, row);
                beanResult.add(t);
            }
            result.put(wb.getFile().getName(), beanResult);
            long endTime = System.currentTimeMillis();
            log.info("=============处理" + wb.getFile().getName() + "(end), 耗时: " +(endTime - startTime)+ "=============");
        }

        return result;
    }

    public static void main(String[] args) {
        String regex = "^d[0-1]$";
        String name = "达州农村商业银行股份有限公司赵固分理处";
        System.out.println(Pattern.matches(regex, name));
    }
}
