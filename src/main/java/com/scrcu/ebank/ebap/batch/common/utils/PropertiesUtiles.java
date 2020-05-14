package com.scrcu.ebank.ebap.batch.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>名称 : 读取配置参数 </p>
 * <p>版本 : 1.0 </p>
 * <p>作者 : M.chen </p>
 * <p>日期 : 2018/6/24 </p>
 */
@Slf4j
public class PropertiesUtiles {
    private static Properties props;
    static{
        loadProps();
    }

    synchronized static private void loadProps(){
        props = new Properties();
        InputStream in = null;
        try {
            //第一种，通过类加载器进行获取properties文件流
            in = PropertiesUtiles.class.getClassLoader().getResourceAsStream("ifspconfigs/constant/encrypt.properties");
            //第二种，通过类进行获取properties文件流 ifspconfigs/constant/encrypt.properties
            //in = PropertiesUtiles.class.getResourceAsStream("ifspconfigs/constant/encrypt.properties");
            props.load(in);
        } catch (FileNotFoundException e) {
            log.error("sysConfig.properties文件未找到");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("出现IOException");
            e.printStackTrace();
        } finally {
            try {
                if(null != in) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("sysConfig.properties文件流关闭出现异常");
            }
        }
        log.info("加载properties文件内容完成...........");
        log.info("properties文件内容：" + props);
    }

    public static String getProperty(String key){
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }

}
