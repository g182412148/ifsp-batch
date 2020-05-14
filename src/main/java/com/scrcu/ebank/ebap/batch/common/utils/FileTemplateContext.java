package com.scrcu.ebank.ebap.batch.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据文件模板缓存
 * 含分隔符的文件必须有换行符
 * 没有换行符或者没有分隔符，必须设置字段的长度（必须是定长文件）
 * @author wudufeng
 * @since 2014年8月05日
 */
public class FileTemplateContext {
	private static final Logger logger = LoggerFactory.getLogger(FileTemplateContext.class);
	private static FileTemplateContext instance;

	private Map<String, Data> configurations = new ConcurrentHashMap<String, Data>();

	private final String fileDir = "templates/txt";

	private FileTemplateContext() {
	}

	public static synchronized FileTemplateContext getInstance() {
		if (null == instance) {
			synchronized (FileTemplateContext.class) {
				// if (null == instance) {
					instance = new FileTemplateContext();
					try {
						instance.loadTemplateFileDir();
					} catch (Exception e) {
						throw new RuntimeException("初始化加载文件失败", e);
					}
				// }
			}
		}
		return instance;
	}

	/**
	 * 
	 * @param templateType
	 * @param key 文件内容对应的类的className
	 * @return
	 */
	public Data getConfig(TemplateType templateType , String key) {
		Data data = this.configurations.get(templateType + key);
		if (data == null) {
			throw new RuntimeException("配置模板"+templateType+"[" + key+"]不存在");
		}
		return data;
	}

	private void loadTemplateFileDir() throws Exception {
		configurations.clear();
		List<String> subFiles = getResourceFile(fileDir, ".xml");
		for (String subFile : subFiles) {
			Data data = parseXML(Data.class, subFile);
			//含分隔符的文件必须换行
			if(data.hasSplit() && !data.hasLineSeparator()){
				throw new RuntimeException(
						"the template file "
								+ subFile
								+ " has split , you must define the line separator ");
			}
			
			String clazz = data.getClazz();
			String version = data.getVersion() == null? "":data.getVersion();
			this.configurations.put(data.getTemplateType() + clazz +version, data);

			BeanInfo beanInfo = Introspector.getBeanInfo(Class.forName(clazz));
			PropertyDescriptor[] propDesc = beanInfo.getPropertyDescriptors();
			List<Column> cols = data.getColumnList();

			
			for (Column col : cols) {
				// 定长文件,没有换行符或者没有分隔符，必须设置字段的长度
				if (data.isFixLengthFile() && col.getLength() <= 0) {
					throw new RuntimeException(
							"the template file "
									+ subFile
									+ " is fixed length file , but column length is empty or less than zero ,you must define the column length ");
				}

				// 设置属性的方法
				for (PropertyDescriptor prop : propDesc) {
					String name = prop.getName();
					if (col.getName().equals(name)) {
						col.setWriteValueMethod(prop.getWriteMethod());
						col.setReadValueMethod(prop.getReadMethod());
						break;
					}
				}
				
				// 设置正则表达式
				String regex = col.getRegex();
				if(regex != null && !regex.trim().equals("")){
					col.setPattern(Pattern.compile(regex));
				}

				// 设置转换值
				String tranSrc = col.getTranSrc();
				if (tranSrc != null && !tranSrc.trim().equals("")) {
					String tranDest = col.getTranDest();
					if (tranDest == null
							|| tranDest.trim().equals("")
							|| tranDest.split(",").length != tranSrc.split(",").length) {
						throw new RuntimeException("the template file "
								+ subFile + " tran source [" + tranSrc
								+ "] not match tran dest [" + tranDest + "]");
					}
					String[] src = tranSrc.split(",");
					String[] dest = tranDest.split(",");
					for (int i = 0; i < src.length; i++) {
						col.addTrans(src[i], dest[i]);
					}
				}

				// 如果是定长的文件，根据字段长度统计每条记录的总长
				data.setPerRecordLength(data.getPerRecordLength()
						+ col.getLength());
				
				// 分隔符16进制转换
				if(data.hasSplit() && data.getSplit().startsWith("0x")){
					//TODO:cancel
					//data.setSplit("\\" + new String(Hex.decodeHex(data.getSplit().substring(2).toCharArray())));
				}
				
			}

		}
	}

	private List<String> getResourceFile(String path, final String extensions) {
		ClassLoader classLoader = getClass().getClassLoader();
		List<String> fileNameLists = new ArrayList<String>();

		try {
			Enumeration<URL> urls = classLoader.getResources(path);
			while (urls.hasMoreElements()) {
				URL temp = urls.nextElement();
				String protocol = temp.getProtocol();
				if ("jar".equals(protocol)) {
					JarFile jarFile = null;
					JarURLConnection connection = (JarURLConnection) temp
							.openConnection();
					jarFile = connection.getJarFile();

					for (Enumeration<JarEntry> en = jarFile.entries(); en
							.hasMoreElements();) {
						JarEntry entry = en.nextElement();
						final String entryName = entry.getName();
						logger.debug("Current entry name is {}", entryName);
						if (entryName.startsWith(path)) {
							if ((extensions == null || extensions.trim()
									.equals(""))
									|| entryName.toUpperCase(Locale.ENGLISH).endsWith(
											extensions.toUpperCase(Locale.ENGLISH))) {
								fileNameLists.add(entryName);
							}
						}
					}
				} else if ("file".equals(protocol)) {
					File file = new File(temp.getFile());
					logger.debug(file.getAbsolutePath());
					String[] files = file.list(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							if ((extensions == null || extensions.trim()
									.equals(""))
									|| name.toUpperCase(Locale.ENGLISH).endsWith(
											extensions.toUpperCase(Locale.ENGLISH)))
								return true;
							return false;
						}
					});
					for (String f : files) {
						fileNameLists.add(new StringBuilder(path)
								.append(File.separatorChar).append(f)
								.toString());
					}
				} else {
					throw new RuntimeException("Unknow Protocol [" + protocol
							+ "]");
				}
			}
			return fileNameLists;
		} catch (Exception e) {
			throw new RuntimeException("获取资源文件出错", e);
		}
	}

	private <M> M parseXML(Class<M> className, String xmlPath) {
		InputStream is = null;
		XMLStreamReader xsr = null;

		try {
			JAXBContext context = JAXBContext.newInstance(className);

			XMLInputFactory xif = XMLInputFactory.newFactory();
			xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			is = getClass().getClassLoader().getResourceAsStream(xmlPath);
			xsr = xif.createXMLStreamReader(is);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			@SuppressWarnings("unchecked")
			M m = (M) unmarshaller.unmarshal(xsr);

			return m;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{

			if(xsr != null){
				try{
					xsr.close();
				}catch(Exception e){
					logger.error("关闭[XMLStreamReader]异常了，异常信息：",e);
				}
			}

			if(is != null){
				try{
					is.close();
				}catch(Exception e){
					logger.error("关闭[InputStream]异常了，异常信息：",e);
				}
			}

		}
	}
}

/** 模板文件类型定义 */
enum TemplateType {
	READ, WRITE
};

@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
class Data {

	@XmlAttribute(name = "templateType")
	private TemplateType templateType;

	/** 文件头字节大小 */
	@XmlAttribute(name = "headLength")
	private int headLength = 0;

	/** 文件尾字节大小 */
	@XmlAttribute(name = "endingLength")
	private int endingLength = 0;

	/** 每条记录的字节大小 */
	@XmlAttribute(name = "perRecordLength")
	private int perRecordLength = 0;

	/** 字符集 */
	@XmlAttribute(name = "charSet")
	private String charSet;

	/** 分隔符 */
	@XmlAttribute(name = "split")
	private String split;

	/** 对应到class类 */
	@XmlAttribute(name = "clazz")
	private String clazz;

	@XmlAttribute(name = "lineSeparator")
	private String lineSeparator;
	
	/**版本号：将版本号作为key的一部分，允许一个类对应多种文件格式**/
	@XmlAttribute(name = "version")
	private String version;

	@XmlElement(name = "column")
	private List<Column> columnList;

	public TemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}

	public int getHeadLength() {
		return headLength;
	}

	public void setHeadLength(int headLength) {
		this.headLength = headLength;
	}

	public int getEndingLength() {
		return endingLength;
	}

	public void setEndingLength(int endingLength) {
		this.endingLength = endingLength;
	}

	public int getPerRecordLength() {
		return perRecordLength;
	}

	public void setPerRecordLength(int perRecordLength) {
		this.perRecordLength = perRecordLength;
	}

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
	
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public List<Column> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}

	/** 判断是否换行的文件 */
	public boolean hasLineSeparator() {
		return getLineSeparator() != null
				&& !getLineSeparator().trim().equals("");
	}

	/** 判断是否含分隔符 */
	public boolean hasSplit() {
		return getSplit() != null && !getSplit().trim().equals("");
	}
	
	/** 判断是否定长文件*/
	public boolean isFixLengthFile(){
		return !hasLineSeparator() || !hasSplit();
	}
}

@XmlRootElement(name = "column")
@XmlAccessorType(XmlAccessType.FIELD)
class Column {

	/** 属性名称 */
	@XmlAttribute(name = "name")
	private String name;

	/** 长度 */
	@XmlAttribute(name = "length")
	private int length;
	
	/** 字段格式对应的正则表达式 */
	@XmlAttribute(name = "type")
	private String type;

	/** 字段格式对应的正则表达式 */
	@XmlAttribute(name = "regex")
	private String regex;
	
	private Pattern pattern;

	/** 字段是日期类型，对应的日期格式 */
	@XmlAttribute(name = "dateFormat")
	private String dateFormat;

	@XmlAttribute(name = "numMultiple")
	private String numMultiple;

	/** 字段描述 */
	@XmlAttribute(name = "desc")
	private String desc;

	/** 源值 */
	@XmlAttribute(name = "tranSrc")
	private String tranSrc;

	/** 目标值 */
	@XmlAttribute(name = "tranDest")
	private String tranDest;

	/** 对应类属性的值的read方法 */
	private Method readValueMethod;

	/** 对应类属性的值的write方法 */
	private Method writeValueMethod;

	/** 值转换 tranSrc 和 tranDes 转换成map */
	private Map<String, String> transMap = new HashMap<String, String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public Pattern getPattern() {
		return pattern;
	}
	
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getNumMultiple() {
		return numMultiple == null || numMultiple.trim().equals("") ? null : numMultiple;
	}

	public void setNumMultiple(String numMultiple) {
		this.numMultiple = numMultiple;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTranSrc() {
		return tranSrc;
	}

	public void setTranSrc(String tranSrc) {
		this.tranSrc = tranSrc;
	}

	public String getTranDest() {
		return tranDest;
	}

	public void setTranDest(String tranDest) {
		this.tranDest = tranDest;
	}

	public Method getReadValueMethod() {
		return readValueMethod;
	}

	public void setReadValueMethod(Method readValueMethod) {
		this.readValueMethod = readValueMethod;
	}

	public Method getWriteValueMethod() {
		return writeValueMethod;
	}

	public void setWriteValueMethod(Method writeValueMethod) {
		this.writeValueMethod = writeValueMethod;
	}

	/** 根据源值获取转换后的值 */
	public String trans(String source) {
		if(source != null){
			String dest = transMap.get(source);
			if(dest == null)
				throw new RuntimeException("trans error:source ["+source+"] has no dest ");
			return dest;
		}
		return null;
	}

	public void addTrans(String source, String dest) {
		this.transMap.put(source, dest);
	}

	/** 判断是否需要转换数据 */
	public boolean isNeedTrans() {
		return transMap.size() > 0;
	}
}
