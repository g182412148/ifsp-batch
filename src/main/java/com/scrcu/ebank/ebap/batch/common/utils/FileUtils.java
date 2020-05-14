package com.scrcu.ebank.ebap.batch.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 */
public abstract class FileUtils {

    /**
     * 根据路径获取文件
     * @param path
     * @return
     */
    public static List<File> getFiles(String path){
        if(StringUtils.isBlank(path)){
            throw new IllegalArgumentException("目录参数为空");
        }
        File dir = new File(path);
        if(!dir.exists()){
            throw new IllegalArgumentException("路径" + path + "不存在");
        }
        if(!dir.isDirectory()){
            throw new IllegalArgumentException("路径" + path + "不是目录");
        }
        //获取目录下的文件
        List<File> files = new ArrayList<>();
        File[] childFiles = dir.listFiles();
        if(childFiles != null && childFiles.length > 0){
            for (File childFile : childFiles) {
                files.add(childFile);
            }
        }
        return files;
    }

}
