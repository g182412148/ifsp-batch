package com.scrcu.ebank.ebap.batch.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 文件工具类<br/>
 * 
 * @author WUDUFENG
 * 
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    /**
     * 校验文件是否存在，若不存在则抛出异常
     * 
     * @param fileName
     */
    public static boolean exists(String fileName) {
        File f = new File(fileName);
        logger.debug(f.getAbsolutePath());
        return f.exists();
    }


    /**
     * 获取文件内容的总记录数，除去文件头和文件尾
     * 
     * @param fileName
     *            文件名,包含文件路径
     * @param clazz
     *            文件内容对应的类
     * @return
     */
    public static <T> long getFileRowCount(String fileName, Class<T> clazz) {
        if (!exists(fileName))
            throw new RuntimeException(fileName.concat("文件不存在"));

        Data data = FileTemplateContext.getInstance().getConfig(TemplateType.READ, clazz.getName());

        int headLength = data.getHeadLength();
        int perRecordLength = data.getPerRecordLength();
        int endingLength = data.getEndingLength();

        // 定长的文件   //定长文件也使用一行一行的去统计
        /*if (!data.hasLineSeparator() || !data.hasSplit()) 
        {
            long len = new File(fileName).length();
            long detailLen = len - headLength - endingLength;// 明细长度

            // 带换行符的定长文件
            if (data.hasLineSeparator()) {
                perRecordLength += data.getLineSeparator().getBytes().length;
            }
            if (detailLen % perRecordLength != 0) {
                throw new RuntimeException("文件不完整");
            }
            //return detailLen / perRecordLength;             //定长文件也使用一行一行的去统计
        }*/

        // 非定长文件只能一行行地去统计了
        try (FileInputStream fis = new FileInputStream(fileName);
             InputStreamReader isr = new InputStreamReader(fis, data.getCharSet());
             BufferedReader br = new BufferedReader(isr)){

            int lineIndex = 0;
            while (br.readLine() != null) {
                lineIndex++;
            }
            return lineIndex - headLength - endingLength;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 读取带换行的txt文件
     * 
     * @param fileName
     *            文件名
     * @param clazz
     *            文件内容对应的class类
     * @param startLineIndex
     *            读文件到起始行从0开始 ，忽略了文件头的情况
     * @param maxLine
     *            需要读取的行数
     * @return
     */
    public static <T> List<T> readFileToList(String fileName, Class<T> clazz, int startLineIndex, int maxLine) {
        if (!exists(fileName))
            throw new RuntimeException(fileName.concat("文件不存在"));

        Data data = FileTemplateContext.getInstance().getConfig(TemplateType.READ, clazz.getName());
        if (data.hasLineSeparator()) {
            return readHasLineSeparatorFileToList(fileName, data, clazz, startLineIndex, maxLine);
        } else {
            return readSequenceFileToList(fileName, data, clazz, startLineIndex, maxLine);
        }
    }


    private static <T> List<T> readSequenceFileToList(String fileName, Data data, Class<T> clazz,
            int startLineIndex, int maxLine) {
        int headLength = data.getHeadLength();
        int perRecordLength = data.getPerRecordLength();
        try (FileInputStream is = new FileInputStream(fileName);
                BufferedInputStream fis = new BufferedInputStream(is)){

            long startLineIndexL = startLineIndex;// 防止相乘后超过Integer最大值
            long skipByte = startLineIndexL * perRecordLength + headLength;
            long actSkipByte = fis.skip(skipByte);
            if (skipByte != actSkipByte) {
                logger.error("startLineIndex：{},lineByteSize：{},headSize：{}", startLineIndexL,
                    perRecordLength, headLength);
                logger.error("预计跳过的字节数为：{},实际跳过的字节数为：{}", skipByte, actSkipByte);
                throw new RuntimeException("读取数据错误");
            }

            List<T> list = new ArrayList<T>();

            int lineNum = 0;
            byte[] buf = new byte[perRecordLength];

            while (lineNum < maxLine && (fis.read(buf) == perRecordLength)) {
                String lineContent = new String(buf);
                T instance = convert(data, clazz, lineContent, lineNum + startLineIndex);
                list.add(instance);
                lineNum += 1;
            }

            return list;
        } catch (Exception e) {
            throw new RuntimeException("读取文件失败", e);
        }

    }


    private static <T> List<T> readHasLineSeparatorFileToList(String fileName, Data data, Class<T> clazz,
            int startLineIndex, int maxLine) {
        List<T> list = new ArrayList<T>();

        try(FileInputStream fis =  new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis, data.getCharSet());
            BufferedReader br = new BufferedReader(isr);) {
            int lineIndex = 0;
            startLineIndex += data.getHeadLength();
            while (lineIndex < startLineIndex) {
                br.readLine();
                lineIndex++;
            }
            String lineContent = null;
            lineIndex = 0;
            while ((lineIndex < maxLine) && (lineContent = br.readLine()) != null) {
                if (lineContent.length() == 0)
                    continue;
//            	System.out.println("lineContent:"+lineContent+"?");
//
//            	System.out.println("lineContent.length() :"+lineContent.length() );
//            	System.out.println("data.getPerRecordLength():"+data.getPerRecordLength() );
//            	System.out.println("data.hasSplit() :"+data.hasSplit());
            	
                if (!data.hasSplit() && lineContent.length() != data.getPerRecordLength())
                    throw new RuntimeException(String.format("文件错误,行记录长度应为%d,实际为%d",
                        data.getPerRecordLength(), lineContent.length()));
                T instance = convert(data, clazz, lineContent, lineIndex + startLineIndex);
                list.add(instance);
                lineIndex++;
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("读取文件数据失败", e);
        }

        return list;
    }


    private static <T> T convert(Data data, Class<T> clazz, String lineContent, int index) throws Exception {
        List<Column> colList = data.getColumnList();
        String split = data.getSplit();
        int size = colList.size();// 文件里一条记录的字段数

        T instance = clazz.newInstance();

        // 带分隔符
        if (data.hasSplit()) {
            String[] values = new StringBuilder(lineContent).append(" ").toString().split(split);
            if (values.length != size) {
            	logger.info("split:{"+split+"}");
            	logger.info("content : "+lineContent);
            	logger.info("size : "+size+" len:"+values.length);
                throw new IllegalFileException("文件第" + index + "行字段数不符合");
            }
            // 最后一个把加的空格去掉
            values[values.length - 1] =
                    values[values.length - 1].substring(0, values[values.length - 1].length() - 1);

            for (int a = 0; a < values.length; a++) {
                Column col = colList.get(a);
                parseValue(values[a], col, instance);
            }
        } else {
            // 定长 不应该包含中文
            int c = 0;
            for (int a = 0; a < colList.size(); a++) {
                Column col = colList.get(a);
                String valueStr = lineContent.substring(c, c + col.getLength());
                c = c + col.getLength();
                parseValue(valueStr, col, instance);
            }
        }
        return instance;
    }


    /**
     * 将字符串设置成对象属性值 ， 读文件
     * 
     * @param valueStr
     * @param column
     * @param t
     * @throws Exception
     */
    private static <T> void parseValue(String valueStr, Column column, T t) throws Exception {
        Method method = column.getWriteValueMethod();
        if (method == null)
            return;
        Class<?> parameterType = method.getParameterTypes()[0];
        Pattern pattern = column.getPattern();
        if (column.getLength() > 0 && valueStr.length() > column.getLength()) {
            throw new IllegalFileException("属性[" + column.getName() + "]值[" + valueStr + "]超出定义的长度");
        }
        if (pattern != null && !pattern.matcher(valueStr).matches())
            throw new IllegalFileException(valueStr + " no matches " + pattern.pattern());
        if (valueStr != null && !valueStr.trim().equals("")) {
            Object args = null;
            if (Date.class.isAssignableFrom(parameterType)) {
                if (!"0".equals(valueStr))
                    args = DateUtil.parse(valueStr, column.getDateFormat());
            } else {
                valueStr = valueStr.trim();
                // 数字类型，判断是否需要转换
                // Number.class.isAssignableFrom(parameterType)
                if (column.getNumMultiple() != null) {
                    valueStr =
                            new BigDecimal(valueStr).multiply(new BigDecimal(column.getNumMultiple()))
                                .toString();
                }
                if (column.isNeedTrans()) {
                    valueStr = column.trans(valueStr);
                }
                if (!parameterType.isPrimitive()) {
                    args = parameterType.getConstructor(String.class).newInstance(valueStr);
                } else {
                    if (char.class == parameterType)
                        args = valueStr.toCharArray()[0];
                    else if (int.class == parameterType)
                        args = Integer.parseInt(valueStr);
                    else if (double.class == parameterType)
                        args = Double.parseDouble(valueStr);
                    else if (float.class == parameterType)
                        args = Float.parseFloat(valueStr);
                    else if (long.class == parameterType)
                        args = Long.parseLong(valueStr);
                    else if (short.class == parameterType)
                        args = Short.parseShort(valueStr);
                    else if (boolean.class == parameterType)
                        args = Boolean.parseBoolean(valueStr);
                    else if (byte.class == parameterType)
                        args = Byte.parseByte(valueStr);
                    else
                        throw new RuntimeException("类型转换错误" + parameterType);

                }
            }
            method.invoke(t, args);
        }
    }


    /**
     * 写文件
     * 
     * @param fileName
     * @param list
     * @param append
     *            是否追加文件
     */
    public static <T> void write(String fileName, List<T> list, boolean append) {
        StringBuilder content = new StringBuilder();
        BufferedWriter bw = null;
        String charSet = "UTF-8";
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;

        try {
            if (list != null && list.size() > 0) {
                Data data =
                        FileTemplateContext.getInstance().getConfig(TemplateType.WRITE,
                            list.get(0).getClass().getName());
                List<Column> cols = data.getColumnList();
                charSet = data.getCharSet();

                for (T t : list) {
                    for (int a = 0, size = cols.size(); a < size; a++) {
                        Column col = cols.get(a);
                        Method m = col.getReadValueMethod();
                        Class<?> returnType = m.getReturnType();
                        String valueStr = null;
                        Object value = m.invoke(t);
                        if (Date.class.isAssignableFrom(returnType)) {
                            valueStr =
                                    value != null ? DateUtil.format((Date) value, col.getDateFormat()) : "";
                        } else if (Number.class.isAssignableFrom(returnType) || returnType.isPrimitive()) {
                            if (!returnType.isPrimitive()) {
                                if (value == null) {
                                    value = 0;
                                    valueStr = "";
                                } else
                                    valueStr = value.toString();
                            }
                            if (returnType.isPrimitive()) {
                                valueStr = String.valueOf(value);
                            }
                            if (col.isNeedTrans()) {
                                valueStr = col.trans(valueStr);
                            }
                            if (col.getNumMultiple() != null) {
                                BigDecimal bd =
                                        new BigDecimal(value.toString()).multiply(new BigDecimal(col
                                            .getNumMultiple()));
                                valueStr = bd.toString();
                                if (col.getLength() > 0) {
                                    // 数字左补零
                                    valueStr = String.format("%0" + col.getLength() + "d", bd.intValue());
                                }
                            }
                        } else {
                            valueStr = value == null ? "" : value.toString();
                            if (col.isNeedTrans()) {
                                valueStr = col.trans(valueStr);
                            }
                            if (col.getLength() > 0) {
                                // 字符串右补空格
                                valueStr = String.format("%-" + col.getLength() + "s", valueStr);
                            }
                        }
                        content.append(valueStr);
                        if (data.hasSplit() && a < size - 1) {
                            content.append(data.getSplit());
                        }
                    }
                    if (data.hasLineSeparator()) {
                        content.append("\r\n");
                    }
                }
            }

            File f = new File(fileName);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }

            fos = new FileOutputStream(fileName, append);
            osw = new OutputStreamWriter(fos, charSet);
            bw = new BufferedWriter(osw);

            bw.write(content.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    logger.error("关闭[BufferedReader]异常了，异常信息:",e);
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    logger.error("关闭[OutputStreamWriter]异常了，异常信息:",e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error("关闭[FileOutputStream]异常了，异常信息:",e);
                }
            }
        }
    }
    
    /**
     * 写文件
     * @param startStr 
     * @param endStr  
     * @param fileName
     * @param list
     * @param append
     *            是否追加文件
     * @param splitEndFlag  是否已分隔符结尾         
     *  @return 是否创建成功
     */
    public static <T> boolean write(String fileName, List<T> list, boolean append , String startStr , String endStr,boolean splitEndFlag ,String ...args )
    {
        StringBuilder content = new StringBuilder();
        boolean flag = false ;
        BufferedWriter bw = null;
        String charSet = "UTF-8";
        
        String version = "";
        if(args != null && args.length != 0)
        {
        	version = args[0];
        }

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;

        try {
        	if(startStr !=null && !"".equals(startStr)){
        		content.append(startStr);
        	}
        	
            if (list != null && list.size() > 0) {
                Data data =
                        FileTemplateContext.getInstance().getConfig(TemplateType.WRITE,
                            list.get(0).getClass().getName()+version);
                List<Column> cols = data.getColumnList();
                charSet = data.getCharSet();
                for (T t : list) {
                    for (int a = 0, size = cols.size(); a < size; a++) {
                        Column col = cols.get(a);
                        Method m = col.getReadValueMethod();
                        Class<?> returnType = m.getReturnType();
                        String valueStr = null;
                        Object value = m.invoke(t);
                        String type = col.getType();                      
                        if (Date.class.isAssignableFrom(returnType) || "Date".equals(type)) {
                            valueStr =
                                    value != null ? DateUtil.format((Date) value, col.getDateFormat()) : "";
                        } else if (Number.class.isAssignableFrom(returnType) || returnType.isPrimitive() || "Number".equals(type)) {
                            if (!returnType.isPrimitive()) {
                                if (value == null) {
                                    value = 0;
                                    valueStr = "";
                                } else
                                    valueStr = value.toString();
                            }
                            if (returnType.isPrimitive()) {
                                valueStr = String.valueOf(value);
                            }
                            if (col.isNeedTrans()) {
                                valueStr = col.trans(valueStr);
                            }
                            if (col.getNumMultiple() != null) {
                                BigDecimal bd =
                                        new BigDecimal(value.toString()).multiply(new BigDecimal(col
                                            .getNumMultiple()));
                                valueStr = bd.toString();
                                if (col.getLength() > 0) {
                                    // 数字左补零
                                    valueStr = String.format("%0" + col.getLength() + "d", bd.intValue());
                                }
                            }
                        } else {
                            valueStr = value == null ? "" : value.toString();
                            if (col.isNeedTrans()) {
                                valueStr = col.trans(valueStr);
                            }
                            if (col.getLength() > 0) {
                                // 字符串右补空格
                                valueStr = String.format("%-" + col.getLength() + "s", valueStr);
                            }
                        }
                        content.append(valueStr);
                        if (data.hasSplit() && a < size - 1) {
                            content.append(data.getSplit());
                        }
                    }
                    if(splitEndFlag)
                    {
                    	 content.append(data.getSplit());
                    }
                    if (data.hasLineSeparator()) {
                        content.append("\r\n");
                    }
                }                
                if(endStr !=null && !"".equals(endStr)){
            		content.append(endStr);
            	}
            }

            File f = new File(fileName);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }

            fos = new FileOutputStream(fileName, append);
            osw = new OutputStreamWriter(fos, charSet);
            bw = new BufferedWriter(osw);
            bw.write(content.toString());
            flag = true ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    logger.error("关闭[BufferedWriter]异常了，异常信息:",e);
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    logger.error("关闭[OutputStreamWriter]异常了，异常信息:",e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error("关闭[FileOutputStream]异常了，异常信息:",e);
                }
            }
        }
        return flag ;
    }
    
    /**
     * 创建空文件
     * @author caog
     */
    public static void createFile(String fileName){
    	String charSet = "UTF-8";
    	try(FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter osw =new OutputStreamWriter(fos, charSet);
            BufferedWriter bw = new BufferedWriter(osw)) {
		} catch (Exception e) {
			throw new RuntimeException(e);			
		}
    }
    

    public static class IllegalFileException extends RuntimeException {
        private static final long serialVersionUID = -4040593750097815090L;


        public IllegalFileException(String message) {
            super(message);
        }
    }
    
    /**
     * 生成核心记账文件文件名
     * @param type ：文件类型，F-一级商户结算文件，S-二级商户结算文件,C:对账文件,G：补足保证金结算文件
     * 				 CC - 信用卡对账文件, U-统一支付记账文件
     * @return
     */
    public static synchronized String genFileName(String type){
    	String fileName = "";
    	String fileType = "";
    	
    	String timeStamp = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
    	if("C".equals(type))
    	{
    		fileName = "052"+timeStamp+"RPS001"+"_check.txt";
    	}
    	else if("CC".equals(type))
    	{
    		fileName = "CREDIT-CHK-FILE-052-"+timeStamp+".TXT";
    	}
    	else if("U".equals(type))
    	{
    		//S(1位) + 渠道号 + 交易日期(8位) + 渠道流水号 + .txt
    		//16 - 31 位为流水号
    		fileName = "S052"+timeStamp.substring(0, 8)+"RPS"+timeStamp.substring(2)+".txt";
    	}
    	else if("G".equals(type))
    	{
    		//文件传输平台目前只支持类型为F/S的结算文件的传输，为避免文件传输平台的改造，这里使用固定值F
    		//只是文件名的长度与一级商户结算文件不同
    		fileName = "RPS"+"052"+"F"+timeStamp.substring(0, 14)+fileType;
    	}
    	else if("ST".equals(type))
    	{
    		//生成一级商户转账文件（用于二级商户手动结算：文件方式）
    		fileName = "RPS"+"052"+"S"+timeStamp.substring(0, 14)+fileType;
    	}
    	else if("RT".equals(type))
    	{
    		//生成二级商户结算余额回退（用于二级商户手动结算：文件方式）
    		fileName = "RPS"+"052"+"S"+timeStamp.substring(0, 15)+fileType;
    	}
    	else
    	{
    		fileName = "RPS"+"052"+type+timeStamp+fileType;
    	}
    	
    	
    	return fileName;
    }
    
    /**
     * 
     * @param fileName 文件路径
     * @param charset 编码格式
     * @return 文件首行内容
     */
    public static String getFirstLineCont (String fileName , String charset){

    	if (!exists(fileName))
            throw new RuntimeException(fileName.concat("文件不存在"));
    	    	
    	try (FileInputStream fis = new FileInputStream(fileName);
             InputStreamReader isr = new InputStreamReader(fis, charset);
    	        BufferedReader br = new BufferedReader(isr)){
			String str = br.readLine().trim();
			br.close();
			return  str;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
    	
    }
    
    /**
     * 去除字符串中的回车、换行符、制表符,竖线分隔符|等
     * @param str
     * @return
     */
    public static String replaceSpecialStr(String str) {
        String repl = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s|\r|\n|\\|");
            Matcher m = p.matcher(str);
            repl = m.replaceAll(",");
        }
        return repl;
    }

}
